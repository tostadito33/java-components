package programmingtheiot.gda.connection.handlers;

import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SystemPerformanceData;


public class UpdateTelemetryResourceHandler extends CoapResource{

    private IDataMessageListener dataMsgListener = null;
    private static final Logger _Logger = Logger.getLogger(UpdateTelemetryResourceHandler.class.getName());
    
    public UpdateTelemetryResourceHandler(String resourceName) {
        super(resourceName);
    }
    public UpdateTelemetryResourceHandler(ResourceNameEnum resource) {
        this(resource.getResourceName());
    }
    @Override
    public void handlePUT(CoapExchange context) {
      ResponseCode code = ResponseCode.NOT_ACCEPTABLE;

	context.accept();

	if (this.dataMsgListener != null) {
		try {
			String jsonData = new String(context.getRequestPayload());

			SystemPerformanceData sysPerfData =
				DataUtil.getInstance().jsonToSystemPerformanceData(jsonData);

			// TODO: Choose the following (but keep it idempotent!)
			//   1) Check MID to see if it’s repeated for some reason
			//      - optional, as the underlying lib should handle this
			//   2) Cache the previous update – is the PAYLOAD repeated?
			//   2) Delegate the data check to this.dataMsgListener

			this.dataMsgListener.handleSystemPerformanceMessage(
				ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE, sysPerfData);

			code = ResponseCode.CHANGED;
		} catch (Exception e) {
			_Logger.warning(
				"Failed to handle PUT request. Message: " +
					e.getMessage());

			code = ResponseCode.BAD_REQUEST;
		}
	} else {
		_Logger.info(
			"No callback listener for request. Ignoring PUT.");

		code = ResponseCode.CONTINUE;
	}

	String msg =
		"Update system perf data request handled: " + super.getName();

	context.respond(code, msg);
    }
    @Override
    public void handleDELETE(CoapExchange context) {
        // Handle DELETE request if needed
        context.respond(ResponseCode.METHOD_NOT_ALLOWED);
    }
    @Override
    public void handleGET(CoapExchange context) {
        // Handle GET request if needed
        context.respond(ResponseCode.METHOD_NOT_ALLOWED);
    }
    @Override
    public void handlePOST(CoapExchange context) {
        // Handle POST request if needed
        context.respond(ResponseCode.METHOD_NOT_ALLOWED);
    }
    @Override
    public void handlePATCH(CoapExchange context) {
        // Handle PATCH request if needed
        context.respond(ResponseCode.METHOD_NOT_ALLOWED);
    }
    public void setDataMessageListener(IDataMessageListener listener) {
        if (listener != null) {
            this.dataMsgListener = listener;
        }
    }
    
    
}