package com.manjrasoft.aneka;

import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.util.Date;

public class JobResult extends Result {

	/** Optional property */
	private String jobId;

	/** Optional property */
	private String name;

	/** Optional property */
	private String nodeId;

	/** Optional property */
	private String applicationId;

	/** Optional property */
	private String reservationId;

	/** Mandatory property */
	private Date submitTime;

	/** Mandatory property */
	private Date queuedTime;

	/** Mandatory property */
	private Date scheduleTime;

	/** Mandatory property */
	private Date completionTime;

	/** Mandatory property */
	private TimeSpan maximumExecutionRuntime;

	/** Mandatory property */
	private boolean preemptableTag;

	/** Mandatory property */
	private JobStatus status;

	/** Optional property */
	private String objectInstance;

	public JobResult() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "JobResult");
	}

	protected JobResult(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(JobResult object, AttributeContainer response) {
		super.fromSoapResponse(object, response);
		jobId = KSoap2Utils.getString((SoapObject) response, "JobId");
		name = KSoap2Utils.getString((SoapObject) response, "Name");
		nodeId = KSoap2Utils.getString((SoapObject) response, "NodeId");
		applicationId = KSoap2Utils.getString((SoapObject) response, "ApplicationId");
		reservationId = KSoap2Utils.getString((SoapObject) response, "ReservationId");
		submitTime = KSoap2Utils.getDate((SoapObject) response, "SubmitTime");
		queuedTime = KSoap2Utils.getDate((SoapObject) response, "QueuedTime");
		scheduleTime = KSoap2Utils.getDate((SoapObject) response, "ScheduleTime");
		completionTime = KSoap2Utils.getDate((SoapObject) response, "CompletionTime");
		Object maximumExecutionRuntimeValue = KSoap2Utils.getProperty((SoapObject) response, "MaximumExecutionRuntime");
		object.setMaximumExecutionRuntime(maximumExecutionRuntimeValue != null ? (TimeSpan) KSoap2Utils.getObject(new TimeSpan(), (AttributeContainer) maximumExecutionRuntimeValue) : null);
		preemptableTag = KSoap2Utils.getBoolean((SoapObject) response, "PreemptableTag");
		Object statusValue = KSoap2Utils.getProperty((SoapObject) response, "Status");
		object.setStatus(statusValue != null ? (JobStatus) KSoap2Utils.getObject(new JobStatus(), (AttributeContainer) statusValue) : null);
		objectInstance = KSoap2Utils.getString((SoapObject) response, "ObjectInstance");
	}

	public int getPropertyCount() {
		return 15;
	}

	public Object getProperty(int index) {
		switch (index) {
			case 2:
				return jobId;
			case 3:
				return name;
			case 4:
				return nodeId;
			case 5:
				return applicationId;
			case 6:
				return reservationId;
			case 7:
				return submitTime;
			case 8:
				return queuedTime;
			case 9:
				return scheduleTime;
			case 10:
				return completionTime;
			case 11:
				return maximumExecutionRuntime;
			case 12:
				return preemptableTag;
			case 13:
				return status;
			case 14:
				return objectInstance;
			default:
				return super.getProperty(index);
		}
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
			case 2:
				info.name = "JobId";
				info.type = String.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			case 3:
				info.name = "Name";
				info.type = String.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			case 4:
				info.name = "NodeId";
				info.type = String.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			case 5:
				info.name = "ApplicationId";
				info.type = String.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			case 6:
				info.name = "ReservationId";
				info.type = String.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			case 7:
				info.name = "SubmitTime";
				info.type = Date.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			case 8:
				info.name = "QueuedTime";
				info.type = Date.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			case 9:
				info.name = "ScheduleTime";
				info.type = Date.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			case 10:
				info.name = "CompletionTime";
				info.type = Date.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			case 11:
				info.name = "MaximumExecutionRuntime";
				info.type = Long.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			case 12:
				info.name = "PreemptableTag";
				info.type = Boolean.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			case 13:
				info.name = "Status";
				info.type = JobStatus.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			case 14:
				info.name = "ObjectInstance";
				info.type = String.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			default:
				super.getPropertyInfo(index, table, info);
		}
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getNameProperty() {
		return name;
	}

	public void setNameProperty(String name) {
		this.name = name;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
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

	public Date getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}

	public Date getQueuedTime() {
		return queuedTime;
	}

	public void setQueuedTime(Date queuedTime) {
		this.queuedTime = queuedTime;
	}

	public Date getScheduleTime() {
		return scheduleTime;
	}

	public void setScheduleTime(Date scheduleTime) {
		this.scheduleTime = scheduleTime;
	}

	public Date getCompletionTime() {
		return completionTime;
	}

	public void setCompletionTime(Date completionTime) {
		this.completionTime = completionTime;
	}

	public TimeSpan getMaximumExecutionRuntime() {
		return maximumExecutionRuntime;
	}

	public void setMaximumExecutionRuntime(TimeSpan maximumExecutionRuntime) {
		this.maximumExecutionRuntime = maximumExecutionRuntime;
	}

	public boolean isPreemptableTag() {
		return preemptableTag;
	}

	public void setPreemptableTag(boolean preemptableTag) {
		this.preemptableTag = preemptableTag;
	}

	public JobStatus getStatus() {
		return status;
	}

	public void setStatus(JobStatus status) {
		this.status = status;
	}

	public String getObjectInstance() {
		return objectInstance;
	}

	public void setObjectInstance(String objectInstance) {
		this.objectInstance = objectInstance;
	}

	public void setProperty(int index, Object object) {}

	public String toString() {
		StringBuilder sb = new StringBuilder("AuthenticateResult [");
		sb.append("success=").append(isSuccess());
		sb.append(", ");
		sb.append("jobId=").append(jobId);
		sb.append(", ");
		sb.append("name=").append(name);
		sb.append(", ");
		sb.append("nodeId=").append(nodeId);
		sb.append(", ");
		sb.append("applicationId=").append(applicationId);
		sb.append(", ");
		sb.append("reservationId=").append(reservationId);
		sb.append(", ");
		sb.append("submitTime=").append(submitTime);
		sb.append(", ");
		sb.append("queuedTime=").append(queuedTime);
		sb.append(", ");
		sb.append("scheduleTime=").append(scheduleTime);
		sb.append(", ");
		sb.append("completionTime=").append(completionTime);
		sb.append(", ");
		sb.append("maximumExecutionRuntime=").append(maximumExecutionRuntime);
		sb.append(", ");
		sb.append("preemptableTag=").append(preemptableTag);
		sb.append(", ");
		sb.append("status=").append(status);
		sb.append(", ");
		sb.append("objectInstance=").append(objectInstance);
		return sb.append(']').toString();
	}
}
