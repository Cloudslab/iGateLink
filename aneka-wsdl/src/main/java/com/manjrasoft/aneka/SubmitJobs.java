package com.manjrasoft.aneka;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class SubmitJobs extends SoapObject implements Deserializable {

	/** Optional property */
	private com.manjrasoft.aneka.JobSubmissionRequest request;

	public SubmitJobs() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "SubmitJobs");
	}

	protected SubmitJobs(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(SubmitJobs object, AttributeContainer response) {
		Object requestValue = KSoap2Utils.getProperty((SoapObject) response, "request");
		object.setRequest(requestValue != null ? (com.manjrasoft.aneka.JobSubmissionRequest) KSoap2Utils.getObject(new com.manjrasoft.aneka.JobSubmissionRequest(), (AttributeContainer) requestValue) : null);
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
			info.type = com.manjrasoft.aneka.JobSubmissionRequest.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public com.manjrasoft.aneka.JobSubmissionRequest getRequest() {
		return request;
	}

	public void setRequest(com.manjrasoft.aneka.JobSubmissionRequest newValue) {
		request = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("SubmitJobs [");
		sb.append("request=").append(request);
		return sb.append(']').toString();
	}
}
