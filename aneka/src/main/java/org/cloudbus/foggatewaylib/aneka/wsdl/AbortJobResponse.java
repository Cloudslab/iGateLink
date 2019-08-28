package org.cloudbus.foggatewaylib.aneka.wsdl;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class AbortJobResponse extends SoapObject implements Deserializable {

	/** Optional property */
	private org.cloudbus.foggatewaylib.aneka.wsdl.Result abortJobResult;

	public AbortJobResponse() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "AbortJobResponse");
	}

	protected AbortJobResponse(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(AbortJobResponse object, AttributeContainer response) {
		Object abortJobResultValue = KSoap2Utils.getProperty((SoapObject) response, "AbortJobResult");
		object.setAbortJobResult(abortJobResultValue != null ? (org.cloudbus.foggatewaylib.aneka.wsdl.Result) KSoap2Utils.getObject(new org.cloudbus.foggatewaylib.aneka.wsdl.Result(), (AttributeContainer) abortJobResultValue) : null);
	}

	public int getPropertyCount() {
		return 1;
	}

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return abortJobResult;
		default:
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "AbortJobResult";
			info.type = org.cloudbus.foggatewaylib.aneka.wsdl.Result.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public org.cloudbus.foggatewaylib.aneka.wsdl.Result getAbortJobResult() {
		return abortJobResult;
	}

	public void setAbortJobResult(org.cloudbus.foggatewaylib.aneka.wsdl.Result newValue) {
		abortJobResult = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("AbortJobResponse [");
		sb.append("abortJobResult=").append(abortJobResult);
		return sb.append(']').toString();
	}
}
