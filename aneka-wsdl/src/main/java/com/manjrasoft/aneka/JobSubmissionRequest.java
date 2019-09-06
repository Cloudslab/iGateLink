package com.manjrasoft.aneka;

import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class JobSubmissionRequest extends Request {

	/** Optional property */
	private String applicationId;

	/** Optional property */
	private String reservationId;

	/** Optional property */
	private ArrayOfJob jobs;


	public JobSubmissionRequest() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "JobSubmissionRequest");
	}

	protected JobSubmissionRequest(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}


	protected void fromSoapResponse(JobSubmissionRequest object, AttributeContainer response) {
		super.fromSoapResponse(object, response);
		applicationId = KSoap2Utils.getString((SoapObject) response, "ApplicationId");
		reservationId = KSoap2Utils.getString((SoapObject) response, "ReservationId");
		Object jobsValue = KSoap2Utils.getProperty((SoapObject) response, "Jobs");
		object.setJobs(jobsValue != null ? (com.manjrasoft.aneka.ArrayOfJob) KSoap2Utils.getObject(new com.manjrasoft.aneka.ArrayOfJob(), (AttributeContainer) jobsValue) : null);
	}

	public int getPropertyCount() {
		return 4;
	}

	public Object getProperty(int index) {
		switch (index) {
			case 1:
				return applicationId;
			case 2:
				return reservationId;
			case 3:
				return jobs;
			default:
				return super.getProperty(index);
		}
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
			case 1:
				info.name = "ApplicationId";
				info.type = String.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			case 2:
				info.name = "ReservationId";
				info.type = String.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			case 3:
				info.name = "Jobs";
				info.type = ArrayOfJob.class;
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

	public String getReservationId() {
		return reservationId;
	}

	public void setReservationId(String reservationId) {
		this.reservationId = reservationId;
	}

	public ArrayOfJob getJobs() {
		return jobs;
	}

	public void setJobs(ArrayOfJob jobs) {
		this.jobs = jobs;
	}

	public void setProperty(int index, Object object) {}

	public String toString() {
		StringBuilder sb = new StringBuilder("ApplicationSubmissionRequest [");
		sb.append("userCredential=").append(getUserCredential());
		sb.append(", ");
		sb.append("applicationId=").append(applicationId);
		sb.append(", ");
		sb.append("reservationId=").append(reservationId);
		sb.append(", ");
		sb.append("jobs=").append(jobs);
		return sb.append(']').toString();
	}
}
