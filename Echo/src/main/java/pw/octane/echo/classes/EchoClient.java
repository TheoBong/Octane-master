package pw.octane.echo.classes;

import com.neovisionaries.ws.client.*;

import java.io.IOException;

public class EchoClient {
    private static final String SERVER = "wss://scanner.echo.ac";

    public static WebSocket connect(String pin) throws IOException, WebSocketException {
        String text = pin + "|no|progress";

        return new WebSocketFactory()
                //this should have a timemout (removed for debug purposes)
                .createSocket(SERVER)
                .addProtocol("progress")
                .sendText(text)
                .addListener(new WebSocketAdapter() {
                    @Override
                    public void onTextMessage(WebSocket websocket, String message) {
                        System.out.println(websocket.getAgreedProtocol());
                        System.out.println(pin);
                        System.out.println(text);
                        System.out.println("Being listening for updates on " + pin + "!");
                        System.out.println(message);
                        if (message.startsWith("NEW_PROGRESS$")) {
                            String progress = message.split("$")[1];
                            if (progress == "FINISHED") {
                                System.out.println("Finished");
                                websocket.sendClose();
                            } else if (progress == "STARTED") {
                                System.out.println("Started");
                            } else {
                                System.out.println(progress + "%");
                            }
                        }
                        while(true) {
                        }
                    }
                })
                .connect();
    }
}