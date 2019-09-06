package com.manjrasoft.aneka;

import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class JobStatusResult extends Result {

	/** Mandatory property */
	private String status;

	public JobStatusResult() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "JobStatusResult");
	}

	protected JobStatusResult(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(JobStatusResult object, AttributeContainer response) {
		super.fromSoapResponse(object, response);
		object.setStatus(KSoap2Utils.getString(response,  "Status"));
	}

	public int getPropertyCount() {
		return 3;
	}

	public Object getProperty(int index) {
		switch (index) {
			case 2:
				return status;
			default:
				return super.getProperty(index);
		}
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
			case 2:
				info.name = "Status";
				info.type = String.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			default:
				super.getPropertyInfo(index, table, info);
		}
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setProperty(int index, Object object) {
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("JobStatusResult [");
		sb.append("success=").append(isSuccess());
		sb.append(", ");
		sb.append("error=").append(getError());
		sb.append(", ");
		sb.append("state=").append(status);
		return sb.append(']').toString();
	}
}
