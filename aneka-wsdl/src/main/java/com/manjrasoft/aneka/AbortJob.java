package com.manjrasoft.aneka;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class AbortJob extends SoapObject implements Deserializable {

	/** Optional property */
	private JobAbortRequest jobAbortInfo;

	public AbortJob() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "AbortJob");
	}

	protected AbortJob(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(AbortJob object, AttributeContainer response) {
		Object jobAbortInfoValue = KSoap2Utils.getProperty((SoapObject) response, "jobAbortInfo");
		object.setJobAbortInfo(jobAbortInfoValue != null ? (JobAbortRequest) KSoap2Utils.getObject(new JobAbortRequest(), (AttributeContainer) jobAbortInfoValue) : null);
	}

	public int getPropertyCount() {
		return 1;
	}

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return jobAbortInfo;
		default:
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "jobAbortInfo";
			info.type = JobAbortRequest.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public JobAbortRequest getJobAbortInfo() {
		return jobAbortInfo;
	}

	public void setJobAbortInfo(JobAbortRequest newValue) {
		jobAbortInfo = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("AbortJob [");
		sb.append("jobAbortInfo=").append(jobAbortInfo);
		return sb.append(']').toString();
	}
}
