package org.cloudbus.foggatewaylib.aneka.wsdl;

import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class ApplicationAbortRequest extends org.cloudbus.foggatewaylib.aneka.wsdl.Request {

	/** Optional property */
	private String applicationId;

	public ApplicationAbortRequest() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "ApplicationQueryRequest");
	}

	protected ApplicationAbortRequest(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(ApplicationAbortRequest object, AttributeContainer response) {
		super.fromSoapResponse(object, response);
		applicationId = KSoap2Utils.getString((SoapObject) response, "ApplicationId");
	}

	public int getPropertyCount() {
		return 2;
	}

	public Object getProperty(int index) {
		switch (index) {
			case 1:
				return applicationId;
			default:
				super.getProperty(index);
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
			case 1:
				info.name = "ApplicationId";
				info.type = String.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			default:
				super.getPropertyInfo(index, table, info);
		}
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public void setProperty(int index, Object object) {
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("ApplicationAbortRequest [");
		sb.append("userCredential=").append(getUserCredential());
		sb.append(", ");
		sb.append("applicationId=").append(applicationId);
		return sb.append(']').toString();
	}
}