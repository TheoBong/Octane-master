package pw.octane.store.paypal;

import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import pw.octane.manager.networking.mongo.MongoRequest;
import pw.octane.store.StoreModule;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DepositHandler extends PaypalClient {

    public DepositHandler(final StoreModule storeModule) {
        super(storeModule);
    }

    public void createDepositOrder(final double amount, final String uuid, final Player player) {

        getModule().getManager().getServer().getScheduler().runTaskAsynchronously(getModule().getManager(), () -> {
            final double total = ((1000.0 / 956.0) * (amount + 0.30));

            final DecimalFormat df = new DecimalFormat("#.##");

            final OrdersCreateRequest request = new OrdersCreateRequest();
            request.header("prefer", "return=representation");

            final OrderRequest orderRequest = new OrderRequest();
            orderRequest.checkoutPaymentIntent("CAPTURE");
            final PaymentMethod method = new PaymentMethod().payeePreferred("IMMEDIATE_PAYMENT_REQUIRED");
            final ApplicationContext context = new ApplicationContext()
                    .brandName("Octane")
                    .landingPage("LOGIN")
                    .userAction("PAY_NOW")
                    .shippingPreference("NO_SHIPPING")
                    .paymentMethod(method);
            orderRequest.applicationContext(context);
            final List<PurchaseUnitRequest> unitRequests = new ArrayList<>();
            final PurchaseUnitRequest unitRequest = new PurchaseUnitRequest()
                    .description("$" + df.format(amount) + " deposit for " + uuid.toString())
                    .amountWithBreakdown(new AmountWithBreakdown().currencyCode("USD").value(df.format(total)));
            unitRequests.add(unitRequest);
            orderRequest.purchaseUnits(unitRequests);
            request.requestBody(orderRequest);

            HttpResponse<Order> repsonse = null;

            try {
                repsonse = this.client().execute(request);
            } catch (final IOException e) {
                e.printStackTrace();
            }

            HttpResponse<Order> finalRepsonse = repsonse;

            getModule().getManager().getMongo().getOrCreateDocument(true, "paypal", finalRepsonse.result().id(), document -> {
                    MongoRequest.newRequest("paypal", finalRepsonse.result().id())
                            .put("amount", amount)
                            .put("player", uuid)
                            .put("expiry_time", System.currentTimeMillis() + 3600000)
                            .run(true);

                    Logger logger = getModule().getManager().getLogger();
                    logger.info("");
                    logger.info("New deposit!");
                    logger.info("UUID: " + uuid);
                    logger.info("Amount: $" + df.format(amount));
                    logger.info("Total: $" + df.format(total));
                    logger.info("ID:" + finalRepsonse.result().id());
                    logger.info("");

                    player.sendMessage(ChatColor.GREEN + "Here is your deposit link for $" + df.format(amount) + " plus PayPal fees:");
                    player.sendMessage(ChatColor.WHITE + "" + ChatColor.UNDERLINE + finalRepsonse.result().links().get(1).href());
                });
            });
    }
}
