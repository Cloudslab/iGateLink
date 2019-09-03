package com.manjrasoft.aneka;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class AbortApplication extends SoapObject implements Deserializable {

	/** Optional property */
	private ApplicationAbortRequest request;

	public AbortApplication() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "AbortApplication");
	}

	protected AbortApplication(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(AbortApplication object, AttributeContainer response) {
		Object requestValue = KSoap2Utils.getProperty((SoapObject) response, "request");
		object.setRequest(requestValue != null ? (ApplicationAbortRequest) KSoap2Utils.getObject(new ApplicationAbortRequest(), (AttributeContainer) requestValue) : null);
	}

	public int getPropertyCount() {
		return 1;
	}

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return request;
		default:
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "request";
			info.type = ApplicationAbortRequest.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public ApplicationAbortRequest getRequest() {
		return request;
	}

	public void setRequest(ApplicationAbortRequest newValue) {
		request = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("AbortApplication [");
		sb.append("request=").append(request);
		return sb.append(']').toString();
	}
}
