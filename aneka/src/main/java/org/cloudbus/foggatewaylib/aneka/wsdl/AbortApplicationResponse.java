package org.cloudbus.foggatewaylib.aneka.wsdl;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class AbortApplicationResponse extends SoapObject implements Deserializable {

	/** Optional property */
	private org.cloudbus.foggatewaylib.aneka.wsdl.Result abortApplicationResult;

	public AbortApplicationResponse() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "AbortApplicationResponse");
	}

	protected AbortApplicationResponse(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(AbortApplicationResponse object, AttributeContainer response) {
		Object abortApplicationResultValue = KSoap2Utils.getProperty((SoapObject) response, "AbortApplicationResult");
		object.setAbortApplicationResult(abortApplicationResultValue != null ? (org.cloudbus.foggatewaylib.aneka.wsdl.Result) KSoap2Utils.getObject(new org.cloudbus.foggatewaylib.aneka.wsdl.Result(), (AttributeContainer) abortApplicationResultValue) : null);
	}

	public int getPropertyCount() {
		return 1;
	}

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return abortApplicationResult;
		default:
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "AbortApplicationResult";
			info.type = org.cloudbus.foggatewaylib.aneka.wsdl.Result.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public org.cloudbus.foggatewaylib.aneka.wsdl.Result getAbortApplicationResult() {
		return abortApplicationResult;
	}

	public void setAbortApplicationResult(org.cloudbus.foggatewaylib.aneka.wsdl.Result newValue) {
		abortApplicationResult = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("AbortApplicationResponse [");
		sb.append("abortApplicationResult=").append(abortApplicationResult);
		return sb.append(']').toString();
	}
}
