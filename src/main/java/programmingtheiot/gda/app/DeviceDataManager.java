/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

 package programmingtheiot.gda.app;

 import java.util.logging.Level;
 import java.util.logging.Logger;
 
 import programmingtheiot.common.ConfigConst;
 import programmingtheiot.common.ConfigUtil;
 import programmingtheiot.common.IActuatorDataListener;
 import programmingtheiot.common.IDataMessageListener;
 import programmingtheiot.common.ResourceNameEnum;
 
 import programmingtheiot.data.ActuatorData;
 import programmingtheiot.data.DataUtil;
 import programmingtheiot.data.SensorData;
 import programmingtheiot.data.SystemPerformanceData;
 import programmingtheiot.data.SystemStateData;
 
 import programmingtheiot.gda.connection.CloudClientConnector;
 import programmingtheiot.gda.connection.CoapServerGateway;
 import programmingtheiot.gda.connection.IPersistenceClient;
 import programmingtheiot.gda.connection.IPubSubClient;
 import programmingtheiot.gda.connection.IRequestResponseClient;
 import programmingtheiot.gda.connection.MqttClientConnector;
 import programmingtheiot.gda.connection.RedisPersistenceAdapter;
 import programmingtheiot.gda.connection.SmtpClientConnector;
 import programmingtheiot.gda.system.SystemPerformanceManager;
 
 /**
  * Shell representation of class for student implementation.
  *
  */
 public class DeviceDataManager implements IDataMessageListener
 {
	 // static
	 
	 private static final Logger _Logger =
		 Logger.getLogger(DeviceDataManager.class.getName());
	 
	 // private var's
	 
	 private boolean enableMqttClient = true;
	 private boolean enableCoapServer = false;
	 private boolean enableCloudClient = false;
	 private boolean enableSmtpClient = false;
	 private boolean enablePersistenceClient = false;
	 private boolean enableSystemPerf = false;
	 
	 private IActuatorDataListener actuatorDataListener = null;
	 private IPubSubClient mqttClient = null;
	 private IPubSubClient cloudClient = null;
	 private IPersistenceClient persistenceClient = null;
	 private IRequestResponseClient smtpClient = null;
	 private CoapServerGateway coapServer = null;
	 private SystemPerformanceManager sysPerfMgr = null;
	 
	 // constructors
	 
	 public DeviceDataManager()
	 {
		 super();

		 
		 initConnections();
		 
		 ConfigUtil configUtil = ConfigUtil.getInstance();
 
	 this.enableMqttClient =
		 configUtil.getBoolean(
			 ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_MQTT_CLIENT_KEY);
 
	 this.enableCoapServer =
		 configUtil.getBoolean(
			 ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_COAP_SERVER_KEY);
 
	 this.enableCloudClient =
		 configUtil.getBoolean(
			 ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_CLOUD_CLIENT_KEY);
 
	 this.enablePersistenceClient =
		 configUtil.getBoolean(
			 ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_PERSISTENCE_CLIENT_KEY);
 
	 initManager();
	 }
	 
	 public DeviceDataManager(
		 boolean enableMqttClient,
		 boolean enableCoapClient,
		 boolean enableCloudClient,
		 boolean enableSmtpClient,
		 boolean enablePersistenceClient)
	 {
		 super();
		 
		 initConnections();
	 }
	 
	 
	 // public methods
	 
	 @Override
	 public boolean handleActuatorCommandResponse(ResourceNameEnum resourceName, ActuatorData data)
	 {
		 if (data != null) {
			 _Logger.info("Handling actuator response: " + data.getName());
	 
			 // this next call is optional for now
			 //this.handleIncomingDataAnalysis(resourceName, data);
	 
			 if (data.hasError()) {
				 _Logger.warning("Error flag set for ActuatorData instance.");
			 }
	 
			 return true;
		 } else {
			 return false;
		 }
	 }
 
	 @Override
	 public boolean handleActuatorCommandRequest(ResourceNameEnum resourceName, ActuatorData data)
	 {
		 return false;
	 }
 
	 @Override
	 public boolean handleIncomingMessage(ResourceNameEnum resourceName, String msg)
	 {

		 return false;

	 }
 
	 @Override
	 public boolean handleSensorMessage(ResourceNameEnum resourceName, SensorData data)
	 {
		 if (data != null) {
			 _Logger.info("Handling sensor message: " + data.getName());
	 
			 if (data.hasError()) {
				 _Logger.warning("Error flag set for SensorData instance.");
			 }
	 
			 return true;
		 } else {
			 return false;
		 }
	 }
 
	 @Override
	 public boolean handleSystemPerformanceMessage(ResourceNameEnum resourceName, SystemPerformanceData data)
	 {
		 if (data != null) {
			 _Logger.info("Handling system performance message: " + data.getName());
	 
			 if (data.hasError()) {
				 _Logger.warning("Error flag set for SystemPerformanceData instance.");
			 }
	 
			 return true;
		 } else {
			 return false;
		 }
	 }
	 
	 public void setActuatorDataListener(String name, IActuatorDataListener listener)
	 {

		 if (listener !=null) {
			 // for now, just ignore 'name' - if you need more than one listener,
			 // you can use 'name' to create a map of listener instances
			 this.actuatorDataListener =listener;
				 }

	 }
	 
	 public void startManager()
	 {
		 if (this.mqttClient != null) {
			 if (this.mqttClient.connectClient()) {
				 _Logger.info("Successfully connected MQTT client to broker.");

	 
				 // add necessary subscriptions
	 
				 // TODO: read this from the configuration file
				 int qos = ConfigConst.DEFAULT_QOS;
	 
				 // TODO: check the return value for each and take appropriate action
	 

				 // IMPORTANT NOTE: The 'subscribeToTopic()' method calls shown
				 // below will be moved to MqttClientConnector.connectComplete()
				 // in Lab Module 10. For now, they can remain here.
				 this.mqttClient.subscribeToTopic(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, qos);
				 this.mqttClient.subscribeToTopic(ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE, qos);
				 this.mqttClient.subscribeToTopic(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, qos);
				 this.mqttClient.subscribeToTopic(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE, qos);
			 } else {
				 _Logger.severe("Failed to connect MQTT client to broker.");

	 
				 // TODO: take appropriate action
			 }
		 }
	 
		 if (this.sysPerfMgr != null) {
			 this.sysPerfMgr.startManager();
		 }	
 
		 if (this.enableCoapServer && this.coapServer != null) {
			 if (this.coapServer.startServer()) {
				 _Logger.info("CoAP server started.");
			 } else {
				 _Logger.severe("Failed to start CoAP server. Check log file for details.");
			 }

		 }
	 }
	 
	 public void stopManager()
	 {
		 if (this.sysPerfMgr != null) {
			 this.sysPerfMgr.stopManager();
		 }

	 
		 if (this.mqttClient != null) {
			 // add necessary un-subscribes
	 
			 // TODO: check the return value for each and take appropriate action
	 

			 // NOTE: The unsubscribeFromTopic() method calls below should match with
			 // the subscribeToTopic() method calls from startManager(). Also, the
			 // unsubscribe logic below can be moved to MqttClientConnector's
			 // disconnectClient() call PRIOR to actually disconnecting from
			 // the MQTT broker.
			 this.mqttClient.unsubscribeFromTopic(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE);
			 this.mqttClient.unsubscribeFromTopic(ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE);
			 this.mqttClient.unsubscribeFromTopic(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE);
			 this.mqttClient.unsubscribeFromTopic(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE);

			 if (this.mqttClient.disconnectClient()) {
				 _Logger.info("Successfully disconnected MQTT client from broker.");
			 } else {
				 _Logger.severe("Failed to disconnect MQTT client from broker.");

	 
				 // TODO: take appropriate action
			 }
		 }
 
		 if (this.enableCoapServer && this.coapServer != null) {
			 if (this.coapServer.stopServer()) {
				 _Logger.info("CoAP server stopped.");
			 } else {
				 _Logger.severe("Failed to stop CoAP server. Check log file for details.");
			 }
		 }
	 }
 

	 
	 // private methods
	 
	 /**
	  * Initializes the enabled connections. This will NOT start them, but only create the
	  * instances that will be used in the {@link #startManager() and #stopManager()) methods.
	  * 
	  */
	 private void initConnections()
	 {
	 }

	 private void initManager()
 {
	 ConfigUtil configUtil = ConfigUtil.getInstance();
 
	 this.enableSystemPerf =
		 configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE,  ConfigConst.ENABLE_SYSTEM_PERF_KEY);
 
	 if (this.enableSystemPerf) {
		 this.sysPerfMgr = new SystemPerformanceManager();
		 this.sysPerfMgr.setDataMessageListener(this);
	 }
 
	 if (this.enableMqttClient) {
		 this.mqttClient = new MqttClientConnector();
 
		 // NOTE: The next line isn't technically needed until Lab Module 10
		 this.mqttClient.setDataMessageListener(this);
	 }
 
	 if (this.enableCoapServer) {

		 if (this.enableCoapServer) {
			 this.coapServer = new CoapServerGateway(this);
		 }

	 }
 
	 if (this.enableCloudClient) {
		 // TODO: implement this in Lab Module 10
	 }
 
	 if (this.enablePersistenceClient) {
		 // TODO: implement this as an optional exercise in Lab Module 5
	 }
 }
 
	 private void handleIncomingDataAnalysis(ResourceNameEnum resourceName, ActuatorData data)
	 {

		 _Logger.info("Analyzing incoming actuator data: " +data.getName());
 
		 if (data.isResponseFlagEnabled()) {
		 // TODO: implement this
			 }else {
		 if (this.actuatorDataListener !=null) {
		 this.actuatorDataListener.onActuatorDataUpdate(data);
				 }
		 }
	 }
	 
	 private void handleIncomingDataAnalysis(ResourceNameEnum resourceName, SystemStateData data)
	 {
 

	 }
 
	 private boolean handleUpstreamTransmission(ResourceNameEnum resourceName, String jsonData, int qos)
	 {

		 return false;
	 }

 }