package org.cloudbus.foggatewaylib.aneka.wsdl;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class Error extends SoapObject implements Deserializable {

	/** Optional property */
	private java.lang.String message;

	/** Optional property */
	private java.lang.String fullTypeName;

	/** Optional property */
	private org.cloudbus.foggatewaylib.aneka.wsdl.Error innerError;

	public Error() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "Error");
	}

	protected Error(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(Error object, AttributeContainer response) {
		object.setMessage(KSoap2Utils.getString(response, "Message"));
		object.setFullTypeName(KSoap2Utils.getString(response, "FullTypeName"));
		Object innerErrorValue = KSoap2Utils.getProperty((SoapObject) response, "InnerError");
		object.setInnerError(innerErrorValue != null ? (org.cloudbus.foggatewaylib.aneka.wsdl.Error) KSoap2Utils.getObject(new org.cloudbus.foggatewaylib.aneka.wsdl.Error(), (AttributeContainer) innerErrorValue) : null);
	}

	public int getPropertyCount() {
		return 3;
	}

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return message;
		case 1:
			return fullTypeName;
		case 2:
			return innerError;
		default:
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "Message";
			info.type = java.lang.String.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		case 1:
			info.name = "FullTypeName";
			info.type = java.lang.String.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		case 2:
			info.name = "InnerError";
			info.type = org.cloudbus.foggatewaylib.aneka.wsdl.Error.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public java.lang.String getMessage() {
		return message;
	}

	public void setMessage(java.lang.String newValue) {
		message = newValue;
	}

	public java.lang.String getFullTypeName() {
		return fullTypeName;
	}

	public void setFullTypeName(java.lang.String newValue) {
		fullTypeName = newValue;
	}

	public org.cloudbus.foggatewaylib.aneka.wsdl.Error getInnerError() {
		return innerError;
	}

	public void setInnerError(org.cloudbus.foggatewaylib.aneka.wsdl.Error newValue) {
		innerError = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("Error [");
		sb.append("message=").append(message);
		sb.append(", ");
		sb.append("fullTypeName=").append(fullTypeName);
		sb.append(", ");
		sb.append("innerError=").append(innerError);
		return sb.append(']').toString();
	}
}
