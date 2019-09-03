package com.manjrasoft.aneka;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class CreateApplicationResponse extends SoapObject implements Deserializable {

	/** Optional property */
	private com.manjrasoft.aneka.ApplicationSubmissionResult createApplicationResult;

	public CreateApplicationResponse() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "CreateApplicationResponse");
	}

	protected CreateApplicationResponse(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(CreateApplicationResponse object, AttributeContainer response) {
		Object createApplicationResultValue = KSoap2Utils.getProperty((SoapObject) response, "CreateApplicationResult");
		object.setCreateApplicationResult(createApplicationResultValue != null ? (com.manjrasoft.aneka.ApplicationSubmissionResult) KSoap2Utils.getObject(new com.manjrasoft.aneka.ApplicationSubmissionResult(), (AttributeContainer) createApplicationResultValue) : null);
	}

	public int getPropertyCount() {
		return 1;
	}

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return createApplicationResult;
		default:
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "CreateApplicationResult";
			info.type = com.manjrasoft.aneka.ApplicationSubmissionResult.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public com.manjrasoft.aneka.ApplicationSubmissionResult getCreateApplicationResult() {
		return createApplicationResult;
	}

	public void setCreateApplicationResult(com.manjrasoft.aneka.ApplicationSubmissionResult newValue) {
		createApplicationResult = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("CreateApplicationResponse [");
		sb.append("createApplicationResult=").append(createApplicationResult);
		return sb.append(']').toString();
	}
}
