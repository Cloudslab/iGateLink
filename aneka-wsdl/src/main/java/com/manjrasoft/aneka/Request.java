package com.manjrasoft.aneka;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class Request extends SoapObject implements Deserializable {

	/** Optional property */
	private UserCredential userCredential;

	public Request() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "Request");
	}

	protected Request(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(Request object, AttributeContainer response) {
		Object userCredentialValue = KSoap2Utils.getProperty((SoapObject) response, "UserCredential");
		object.setUserCredential(userCredentialValue != null ? (UserCredential) KSoap2Utils.getObject(new UserCredential(), (AttributeContainer) userCredentialValue) : null);
	}

	public int getPropertyCount() {
		return 1;
	}

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return userCredential;
		default:
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "UserCredential";
			info.type = UserCredential.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public UserCredential getUserCredential() {
		return userCredential;
	}

	public void setUserCredential(UserCredential newValue) {
		userCredential = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("Request [");
		sb.append("userCredential=").append(userCredential);
		return sb.append(']').toString();
	}
}
