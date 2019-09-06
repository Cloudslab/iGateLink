package com.manjrasoft.aneka;

import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.util.Date;

public class ApplicationResult extends Result {

	/** Optional property */
	private String applicationId;

	/** Optional property */
	private String displayName;

	/** Mandatory property */
	private Date createdDateTime;

	/** Mandatory property */
	private Date finishedDateTime;

	/** Optional property */
	private ArrayOfJobResult jobs;

	/** Mandatory property */
	private boolean useFileTransfer;

	/** Mandatory property */
	private ApplicationStatus state;


	public ApplicationResult() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "ApplicationResult");
	}

	protected ApplicationResult(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(ApplicationResult object, AttributeContainer response) {
		super.fromSoapResponse(object, response);
		applicationId = KSoap2Utils.getString((SoapObject) response, "ApplicationId");
		displayName = KSoap2Utils.getString((SoapObject) response, "DisplayName");
		createdDateTime = KSoap2Utils.getDate((SoapObject) response, "CreatedDateTime");
		try {
			finishedDateTime = KSoap2Utils.getDate((SoapObject) response, "FinishedDateTime");
		} catch (StringIndexOutOfBoundsException e){
			// not finished
			finishedDateTime = null;
		}
		Object jobsValue = KSoap2Utils.getProperty((SoapObject) response, "Jobs");
		object.setJobs(jobsValue != null ? (ArrayOfJobResult) KSoap2Utils.getObject(new ArrayOfJobResult(), (AttributeContainer) jobsValue) : null);
		useFileTransfer = KSoap2Utils.getBoolean((SoapObject) response, "UseFileTransfer");
		Object stateValue = KSoap2Utils.getProperty((SoapObject) response, "State");
		object.setState(stateValue != null ? (ApplicationStatus) KSoap2Utils.getObject(new ApplicationStatus(), (AttributeContainer) stateValue) : null);
	}

	public int getPropertyCount() {
		return 9;
	}

	public Object getProperty(int index) {
		switch (index) {
			case 2:
				return applicationId;
			case 3:
				return displayName;
			case 4:
				return createdDateTime;
			case 5:
				return finishedDateTime;
			case 6:
				return jobs;
			case 7:
				return useFileTransfer;
			case 8:
				return state;
			default:
				return super.getProperty(index);
		}
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
			case 2:
				info.name = "ApplicationId";
				info.type = String.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			case 3:
				info.name = "DisplayName";
				info.type = String.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			case 4:
				info.name = "CreatedDateTime";
				info.type = Date.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			case 5:
				info.name = "FinishedDateTime";
				info.type = Date.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			case 6:
				info.name = "Jobs";
				info.type = ArrayOfJobResult.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			case 7:
				info.name = "UseFileTransfer";
				info.type = Boolean.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			case 8:
				info.name = "State";
				info.type = ApplicationStatus.class;
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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Date getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public Date getFinishedDateTime() {
		return finishedDateTime;
	}

	public void setFinishedDateTime(Date finishedDateTime) {
		this.finishedDateTime = finishedDateTime;
	}

	public ArrayOfJobResult getJobs() {
		return jobs;
	}

	public void setJobs(ArrayOfJobResult jobs) {
		this.jobs = jobs;
	}

	public boolean isUseFileTransfer() {
		return useFileTransfer;
	}

	public void setUseFileTransfer(boolean useFileTransfer) {
		this.useFileTransfer = useFileTransfer;
	}

	public ApplicationStatus getState() {
		return state;
	}

	public void setState(ApplicationStatus state) {
		this.state = state;
	}

	public void setProperty(int index, Object object) {}

	public String toString() {
		StringBuilder sb = new StringBuilder("ApplicationResult [");
		sb.append("success=").append(isSuccess());
		sb.append(", ");
		sb.append("error=").append(getError());
		sb.append(", ");
		sb.append("applicationId=").append(getApplicationId());
		sb.append(", ");
		sb.append("displayName=").append(displayName);
		sb.append(", ");
		sb.append("createdDateTime=").append(createdDateTime);
		sb.append(", ");
		sb.append("finishedDateTime=").append(finishedDateTime);
		sb.append(", ");
		sb.append("jobs=").append(jobs);
		sb.append(", ");
		sb.append("useFileTransfer=").append(useFileTransfer);
		sb.append(", ");
		sb.append("state=").append(state);
		return sb.append(']').toString();
	}
}
