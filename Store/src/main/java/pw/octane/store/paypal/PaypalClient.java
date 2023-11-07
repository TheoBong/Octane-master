package pw.octane.store.paypal;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import lombok.Getter;
import pw.octane.store.StoreModule;

public class PaypalClient {

    //cow's paypal api keys :)
    private static String ClientID;
    private static String Secret;
    static PayPalEnvironment environment;
    static PayPalHttpClient client;

    private @Getter StoreModule module;

    public PaypalClient(StoreModule module) {

        this.module = module;

        ClientID = module.getConfig().getString("client-id");
        Secret = module.getConfig().getString("secret");

        this.environment = new PayPalEnvironment.Live(ClientID, Secret);
        this.client = new PayPalHttpClient(this.environment);
    }

    public PayPalHttpClient client() {
        return this.client;
    }
}
