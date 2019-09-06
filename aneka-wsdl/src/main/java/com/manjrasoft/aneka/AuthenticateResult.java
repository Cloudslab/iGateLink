package com.manjrasoft.aneka;

import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class AuthenticateResult extends Result {

	/** Optional property */
	private String userCredential;

	public AuthenticateResult() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "AuthenticateResult");
	}

	protected AuthenticateResult(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(AuthenticateResult object, AttributeContainer response) {
		super.fromSoapResponse(object, response);
		userCredential = KSoap2Utils.getString((SoapObject) response, "UserCredential");
	}

	public int getPropertyCount() {
		return 3;
	}

	public Object getProperty(int index) {
		switch (index) {
			case 2:
				return userCredential;
			default:
				return super.getProperty(index);
		}
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
			case 2:
				info.name = "UserCredential";
				info.type = String.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			default:
				super.getPropertyInfo(index, table, info);
		}
	}

	public void setProperty(int index, Object object) {}

	public String getUserCredential() {
		return userCredential;
	}

	public void setUserCredential(String newValue) {
		userCredential = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("AuthenticateResult [");
		sb.append("success=").append(isSuccess());
		sb.append(", ");
		sb.append("error=").append(getError());
		sb.append(", ");
		sb.append("userCredential=").append(userCredential);
		return sb.append(']').toString();
	}
}
