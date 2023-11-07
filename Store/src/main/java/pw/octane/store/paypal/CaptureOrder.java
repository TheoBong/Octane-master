package pw.octane.store.paypal;

import java.io.IOException;

import com.paypal.http.HttpResponse;
import com.paypal.http.exceptions.HttpException;
import com.paypal.orders.*;
import org.bukkit.Bukkit;
import pw.octane.manager.OctaneManager;
import pw.octane.manager.networking.mongo.MongoRequest;
import pw.octane.manager.networking.redis.RedisMessage;

public class CaptureOrder {

    public static void main(String orderId, String playerUUID, double amount, long expiry) {

        Order order = null;
        OrdersCaptureRequest request = new OrdersCaptureRequest(orderId);

        try {
            HttpResponse<Order> response = PaypalClient.client.execute(request);
            order = response.result();
            System.out.println("Captured Purchase! Info: " + order.purchaseUnits().get(0).payments().captures().get(0).id());
            order.purchaseUnits().get(0).payments().captures().get(0).links()
                    .forEach(link -> System.out.println(link.rel() + " => " + link.method() + ":" + link.href()));

            System.out.println("Success, gib the playa his mooney");

            OctaneManager.INSTANCE.getMongo().deleteDocument(true, "paypal", orderId);

            OctaneManager.INSTANCE.getMongo().getOrCreateDocument(true, "successful", orderId, document -> {
                MongoRequest.newRequest("successful", orderId)
                        .put("amount", amount)
                        .put("player", playerUUID)
                        .put("created", expiry - 3600000)
                        .put("captured", System.currentTimeMillis())
                        .run(true);
            });

            new RedisMessage(Bukkit.getPlayer(playerUUID) + " (" + playerUUID + ") has just deposited $" + amount + "!");

        } catch (IOException ioe) {
            if (ioe instanceof HttpException) {
                // Something went wrong server-side
                HttpException he = (HttpException) ioe;
                System.out.println(he.getMessage());
            } else {
                // Something went wrong client-side
                System.out.println("U-oh! I made a fucky wucky");
            }
        }
    }
}