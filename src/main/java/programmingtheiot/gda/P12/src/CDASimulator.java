package programmingtheiot.gda.P12.src;
import org.eclipse.paho.client.mqttv3.*;

import java.util.Random;

public class CDASimulator {
    private static final String BROKER = "ssl://localhost:8883";
    private static final String CLIENT_ID = "CDA";
    private static final String TOPIC = "gda/data";

    public static void start() {
        try {
            MqttClient client = new MqttClient(BROKER, CLIENT_ID);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setSocketFactory(SSLUtil.getSocketFactory());

            client.connect(options);

            Random rand = new Random();

            while (true) {
                int temp = 20 + rand.nextInt(21); // 20–40
                String message = "temperature:" + temp;

                client.publish(TOPIC, new MqttMessage(message.getBytes()));
                Thread.sleep(5000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
