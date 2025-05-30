package programmingtheiot.gda.P12.src;
import org.eclipse.paho.client.mqttv3.*;

public class CloudSubscriber {
    private static final String CLOUD_EVENT_TOPIC = "cloud/event";
    private static final String ACT_TOPIC = "cda/actuator";

    public static void listen(MqttClient client) throws MqttException {
        client.subscribe(CLOUD_EVENT_TOPIC, (topic, msg) -> {
            String payload = new String(msg.getPayload());
            if (payload.equals("trigger_fan")) {
                client.publish(ACT_TOPIC, new MqttMessage("fan:on".getBytes()));
            }
        });
    }
}
