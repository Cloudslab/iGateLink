package com.manjrasoft.aneka;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class AuthenticateUser extends SoapObject implements Deserializable {

	/** Optional property */
	private java.lang.String username;

	/** Optional property */
	private java.lang.String password;

	public AuthenticateUser() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "AuthenticateUser");
	}

	protected AuthenticateUser(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(AuthenticateUser object, AttributeContainer response) {
		object.setUsername(KSoap2Utils.getString(response, "username"));
		object.setPassword(KSoap2Utils.getString(response, "password"));
	}

	public int getPropertyCount() {
		return 2;
	}

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return username;
		case 1:
			return password;
		default:
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "username";
			info.type = java.lang.String.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		case 1:
			info.name = "password";
			info.type = java.lang.String.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public java.lang.String getUsername() {
		return username;
	}

	public void setUsername(java.lang.String newValue) {
		username = newValue;
	}

	public java.lang.String getPassword() {
		return password;
	}

	public void setPassword(java.lang.String newValue) {
		password = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("AuthenticateUser [");
		sb.append("username=").append(username);
		sb.append(", ");
		sb.append("password=").append(password);
		return sb.append(']').toString();
	}
}
