package org.cloudbus.foggatewaylib.aneka.wsdl;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class ArrayOfJob extends SoapObject implements Deserializable {

	/** Optional property */
	private org.cloudbus.foggatewaylib.aneka.wsdl.Job[] job;

	public ArrayOfJob() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "ArrayOfJob");
	}

	protected ArrayOfJob(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(ArrayOfJob object, AttributeContainer response) {
		java.util.List jobList = KSoap2Utils.getObjectList((SoapObject) response, "Job");
		org.cloudbus.foggatewaylib.aneka.wsdl.Job[] jobArray = new org.cloudbus.foggatewaylib.aneka.wsdl.Job[jobList.size()];
		for (int i = 0; i < jobArray.length; ++i) {
			jobArray[i] = (org.cloudbus.foggatewaylib.aneka.wsdl.Job) KSoap2Utils.getObject(new org.cloudbus.foggatewaylib.aneka.wsdl.Job(), (AttributeContainer) jobList.get(i));
		}
		object.setJob(jobArray);
	}

	public int getPropertyCount() {
		return 1;
	}

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return job;
		default:
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "Job";
			info.type = org.cloudbus.foggatewaylib.aneka.wsdl.Job[].class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public org.cloudbus.foggatewaylib.aneka.wsdl.Job[] getJob() {
		return job;
	}

	public void setJob(org.cloudbus.foggatewaylib.aneka.wsdl.Job[] newValue) {
		job = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("ArrayOfJob [");
		sb.append("job=").append(java.util.Arrays.toString(job));
		return sb.append(']').toString();
	}
}
