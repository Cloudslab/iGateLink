package com.manjrasoft.aneka;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class SubmitJobsResponse extends SoapObject implements Deserializable {

	/** Optional property */
	private com.manjrasoft.aneka.JobSubmissionResult submitJobsResult;

	public SubmitJobsResponse() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "SubmitJobsResponse");
	}

	protected SubmitJobsResponse(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(SubmitJobsResponse object, AttributeContainer response) {
		Object submitJobsResultValue = KSoap2Utils.getProperty((SoapObject) response, "SubmitJobsResult");
		object.setSubmitJobsResult(submitJobsResultValue != null ? (com.manjrasoft.aneka.JobSubmissionResult) KSoap2Utils.getObject(new com.manjrasoft.aneka.JobSubmissionResult(), (AttributeContainer) submitJobsResultValue) : null);
	}

	public int getPropertyCount() {
		return 1;
	}

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return submitJobsResult;
		default:
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "SubmitJobsResult";
			info.type = com.manjrasoft.aneka.JobSubmissionResult.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public com.manjrasoft.aneka.JobSubmissionResult getSubmitJobsResult() {
		return submitJobsResult;
	}

	public void setSubmitJobsResult(com.manjrasoft.aneka.JobSubmissionResult newValue) {
		submitJobsResult = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("SubmitJobsResponse [");
		sb.append("submitJobsResult=").append(submitJobsResult);
		return sb.append(']').toString();
	}
}
