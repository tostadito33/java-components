/**
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 * 
 * Copyright (c) 2020 by Andrew D. King
 */ 

 package programmingtheiot.part03.integration.app;

 import java.util.logging.Logger;
 
 import org.junit.After;
 import org.junit.AfterClass;
 import org.junit.Before;
 import org.junit.BeforeClass;
 import org.junit.Test;
 
 import programmingtheiot.common.ConfigConst;
 import programmingtheiot.common.ConfigUtil;
 import programmingtheiot.common.ResourceNameEnum;
 import programmingtheiot.data.DataUtil;
 import programmingtheiot.data.SensorData;
 import programmingtheiot.gda.app.DeviceDataManager;
 import programmingtheiot.gda.connection.IPubSubClient;
 import programmingtheiot.gda.connection.MqttClientConnector;
 
 public class DeviceDataManagerSimpleCdaActuationTest {
 
	 // static variables
	 private static final Logger _Logger = Logger.getLogger(DeviceDataManagerSimpleCdaActuationTest.class.getName());
 
	 // test lifecycle methods
	 @BeforeClass
	 public static void setUpBeforeClass() throws Exception {
	 }
	  
	 @AfterClass
	 public static void tearDownAfterClass() throws Exception {
	 }
	  
	 @Before
	 public void setUp() throws Exception {
	 }
	  
	 @After
	 public void tearDown() throws Exception {
	 }
	  
	 // test methods
 
	 /**
	  * Test method for running the DeviceDataManager.
	  */
	 @Test
	 public void testSendActuationEventsToCda() {
		 DeviceDataManager devDataMgr = new DeviceDataManager();
 
		 // NOTE: Be sure your PiotConfig.props is setup properly
		 // to connect with the CDA
		 devDataMgr.startManager();
 
		 ConfigUtil cfgUtil = ConfigUtil.getInstance();
 
		 // TODO: add these to ConfigConst
		 float nominalVal = cfgUtil.getFloat(ConfigConst.GATEWAY_DEVICE, "nominalHumiditySetting");
		 float lowVal     = cfgUtil.getFloat(ConfigConst.GATEWAY_DEVICE, "triggerHumidifierFloor");
		 float highVal    = cfgUtil.getFloat(ConfigConst.GATEWAY_DEVICE, "triggerHumidifierCeiling");
		 int delay        = cfgUtil.getInteger(ConfigConst.GATEWAY_DEVICE, "humidityMaxTimePastThreshold");
 
		 // Test Sequence No. 1
		 generateAndProcessHumiditySensorDataSequence(devDataMgr, nominalVal, lowVal, highVal, delay);
 
		 // TODO: Add more test sequences if desired
 
		 devDataMgr.stopManager();
	 }
 
	 private void generateAndProcessHumiditySensorDataSequence(DeviceDataManager ddm,
																 float nominalVal,
																 float lowVal,
																 float highVal,
																 int delay) {
		 SensorData sd = new SensorData();
		 sd.setName("My Test Humidity Sensor");
		 sd.setLocationID("constraineddevice001");
		 sd.setTypeID(ConfigConst.HUMIDITY_SENSOR_TYPE);
 
		 sd.setValue(nominalVal);
		 ddm.handleSensorMessage(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, sd);
		 waitForSeconds(2);
 
		 sd.setValue(nominalVal);
		 ddm.handleSensorMessage(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, sd);
		 waitForSeconds(2);
 
		 sd.setValue(lowVal - 2);
		 ddm.handleSensorMessage(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, sd);
		 waitForSeconds(delay + 1);
 
		 sd.setValue(lowVal - 1);
		 ddm.handleSensorMessage(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, sd);
		 waitForSeconds(delay + 1);
 
		 sd.setValue(lowVal + 1);
		 ddm.handleSensorMessage(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, sd);
		 waitForSeconds(delay + 1);
 
		 sd.setValue(nominalVal);
		 ddm.handleSensorMessage(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, sd);
		 waitForSeconds(delay + 1);
	 }
 
	 private void waitForSeconds(int seconds) {
		 try {
			 Thread.sleep(seconds * 1000);
		 } catch (InterruptedException e) {
			 // ignore
		 }
	 }
 }