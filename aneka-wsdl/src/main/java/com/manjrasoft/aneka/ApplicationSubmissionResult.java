package com.manjrasoft.aneka;

import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class ApplicationSubmissionResult extends Result {

	/** Optional property */
	private String applicationId;

	public ApplicationSubmissionResult() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "ApplicationSubmissionResult");
	}

	protected ApplicationSubmissionResult(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(ApplicationSubmissionResult object, AttributeContainer response) {
		super.fromSoapResponse(object, response);
		applicationId = KSoap2Utils.getString((SoapObject) response, "ApplicationId");
	}

	public int getPropertyCount() {
		return 3;
	}

	public Object getProperty(int index) {
		switch (index) {
			case 2:
				return applicationId;
			default:
				super.getProperty(index);
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
			case 2:
				info.name = "ApplicationId";
				info.type = String.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			default:
				super.getPropertyInfo(index, table, info);
		}
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public void setProperty(int index, Object object) {}

	public String toString() {
		StringBuilder sb = new StringBuilder("ApplicationSubmissionResult [");
		sb.append("success=").append(isSuccess());
		sb.append(", ");
		sb.append("error=").append(getError());
		sb.append(", ");
		sb.append("applicationId=").append(getApplicationId());
		return sb.append(']').toString();
	}
}
