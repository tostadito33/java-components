package programmingtheiot.gda.P12.src;
import org.eclipse.paho.client.mqttv3.*;

public class GDA {
    private static final String BROKER = "ssl://localhost:8883";
    private static final String CLIENT_ID = "GDA";
    private static final String SUB_TOPIC = "gda/data";
    private static final String PUB_TOPIC = "cloud/data";
    private static final String ACT_TOPIC = "cda/actuator";

    public static void start() {
        try {
            MqttClient client = new MqttClient(BROKER, CLIENT_ID);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setSocketFactory(SSLUtil.getSocketFactory());
            client.connect(options);

            client.subscribe(SUB_TOPIC, (topic, msg) -> {
                String payload = new String(msg.getPayload());
                int temp = Integer.parseInt(payload.split(":")[1]);

                String sysData = SystemMonitor.generate();

                Storage.saveToFile(payload + ", " + sysData);
                client.publish(PUB_TOPIC, new MqttMessage((payload + ", " + sysData).getBytes()));

                if (temp > 30) {
                    client.publish(ACT_TOPIC, new MqttMessage("fan:on".getBytes()));
                } else {
                    client.publish(ACT_TOPIC, new MqttMessage("fan:off".getBytes()));
                }
            });

            CloudSubscriber.listen(client);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
