package com.manjrasoft.aneka;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class Job extends SoapObject implements Deserializable {

	/** Optional property */
	private com.manjrasoft.aneka.ArrayOfTaskItem tasks;

	/** Optional property */
	private com.manjrasoft.aneka.ArrayOfFile inputFiles;

	/** Optional property */
	private com.manjrasoft.aneka.ArrayOfFile outputFiles;

	/** Optional property */
	private java.lang.String reservationId;

	public Job() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "Job");
	}

	protected Job(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(Job object, AttributeContainer response) {
		Object tasksValue = KSoap2Utils.getProperty((SoapObject) response, "Tasks");
		object.setTasks(tasksValue != null ? (com.manjrasoft.aneka.ArrayOfTaskItem) KSoap2Utils.getObject(new com.manjrasoft.aneka.ArrayOfTaskItem(), (AttributeContainer) tasksValue) : null);
		Object inputFilesValue = KSoap2Utils.getProperty((SoapObject) response, "InputFiles");
		object.setInputFiles(inputFilesValue != null ? (com.manjrasoft.aneka.ArrayOfFile) KSoap2Utils.getObject(new com.manjrasoft.aneka.ArrayOfFile(), (AttributeContainer) inputFilesValue) : null);
		Object outputFilesValue = KSoap2Utils.getProperty((SoapObject) response, "OutputFiles");
		object.setOutputFiles(outputFilesValue != null ? (com.manjrasoft.aneka.ArrayOfFile) KSoap2Utils.getObject(new com.manjrasoft.aneka.ArrayOfFile(), (AttributeContainer) outputFilesValue) : null);
		object.setReservationId(KSoap2Utils.getString(response, "ReservationId"));
	}

	public int getPropertyCount() {
		return 4;
	}

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return tasks;
		case 1:
			return inputFiles;
		case 2:
			return outputFiles;
		case 3:
			return reservationId;
		default:
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "Tasks";
			info.type = com.manjrasoft.aneka.ArrayOfTaskItem.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		case 1:
			info.name = "InputFiles";
			info.type = com.manjrasoft.aneka.ArrayOfFile.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		case 2:
			info.name = "OutputFiles";
			info.type = com.manjrasoft.aneka.ArrayOfFile.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		case 3:
			info.name = "ReservationId";
			info.type = java.lang.String.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public com.manjrasoft.aneka.ArrayOfTaskItem getTasks() {
		return tasks;
	}

	public void setTasks(com.manjrasoft.aneka.ArrayOfTaskItem newValue) {
		tasks = newValue;
	}

	public com.manjrasoft.aneka.ArrayOfFile getInputFiles() {
		return inputFiles;
	}

	public void setInputFiles(com.manjrasoft.aneka.ArrayOfFile newValue) {
		inputFiles = newValue;
	}

	public com.manjrasoft.aneka.ArrayOfFile getOutputFiles() {
		return outputFiles;
	}

	public void setOutputFiles(com.manjrasoft.aneka.ArrayOfFile newValue) {
		outputFiles = newValue;
	}

	public java.lang.String getReservationId() {
		return reservationId;
	}

	public void setReservationId(java.lang.String newValue) {
		reservationId = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("Job [");
		sb.append("tasks=").append(tasks);
		sb.append(", ");
		sb.append("inputFiles=").append(inputFiles);
		sb.append(", ");
		sb.append("outputFiles=").append(outputFiles);
		sb.append(", ");
		sb.append("reservationId=").append(reservationId);
		return sb.append(']').toString();
	}
}
