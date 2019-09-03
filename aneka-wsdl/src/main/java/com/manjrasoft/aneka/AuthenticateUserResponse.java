package com.manjrasoft.aneka;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class AuthenticateUserResponse extends SoapObject implements Deserializable {

	/** Optional property */
	private com.manjrasoft.aneka.AuthenticateResult authenticateUserResult;

	public AuthenticateUserResponse() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "AuthenticateUserResponse");
	}

	protected AuthenticateUserResponse(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(AuthenticateUserResponse object, AttributeContainer response) {
		Object authenticateUserResultValue = KSoap2Utils.getProperty((SoapObject) response, "AuthenticateUserResult");
		object.setAuthenticateUserResult(authenticateUserResultValue != null ? (com.manjrasoft.aneka.AuthenticateResult) KSoap2Utils.getObject(new com.manjrasoft.aneka.AuthenticateResult(), (AttributeContainer) authenticateUserResultValue) : null);
	}

	public int getPropertyCount() {
		return 1;
	}

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return authenticateUserResult;
		default:
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "AuthenticateUserResult";
			info.type = com.manjrasoft.aneka.AuthenticateResult.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public com.manjrasoft.aneka.AuthenticateResult getAuthenticateUserResult() {
		return authenticateUserResult;
	}

	public void setAuthenticateUserResult(com.manjrasoft.aneka.AuthenticateResult newValue) {
		authenticateUserResult = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("AuthenticateUserResponse [");
		sb.append("authenticateUserResult=").append(authenticateUserResult);
		return sb.append(']').toString();
	}
}
