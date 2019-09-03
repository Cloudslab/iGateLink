package com.manjrasoft.aneka;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class Result extends SoapObject implements Deserializable {

	/** Mandatory property */
	private boolean success;

	/** Optional property */
	private com.manjrasoft.aneka.Error error;

	public Result() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "Result");
	}

	protected Result(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(Result object, AttributeContainer response) {
		object.setSuccess(KSoap2Utils.getBoolean(response, "Success"));
		Object errorValue = KSoap2Utils.getProperty((SoapObject) response, "Error");
		object.setError(errorValue != null ? (com.manjrasoft.aneka.Error) KSoap2Utils.getObject(new com.manjrasoft.aneka.Error(), (AttributeContainer) errorValue) : null);
	}

	public int getPropertyCount() {
		return 2;
	}

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return success;
		case 1:
			return error;
		default:
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "Success";
			info.type = boolean.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		case 1:
			info.name = "Error";
			info.type = com.manjrasoft.aneka.Error.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean newValue) {
		success = newValue;
	}

	public com.manjrasoft.aneka.Error getError() {
		return error;
	}

	public void setError(com.manjrasoft.aneka.Error newValue) {
		error = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("Result [");
		sb.append("success=").append(success);
		sb.append(", ");
		sb.append("error=").append(error);
		return sb.append(']').toString();
	}
}
