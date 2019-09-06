package com.manjrasoft.aneka;

import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class JobSubmissionResult extends Result {

	/** Optional property */
	private ArrayOfString ids;

	public JobSubmissionResult() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "JobSubmissionResult");
	}

	protected JobSubmissionResult(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}


	protected void fromSoapResponse(JobSubmissionResult object, AttributeContainer response) {
		super.fromSoapResponse(object, response);
		Object idsValue = KSoap2Utils.getProperty((SoapObject) response, "Ids");
		object.setIds(idsValue != null ? (com.manjrasoft.aneka.ArrayOfString) KSoap2Utils.getObject(new com.manjrasoft.aneka.ArrayOfString(), (AttributeContainer) idsValue) : null);
	}

	public int getPropertyCount() {
		return 3;
	}

	public Object getProperty(int index) {
		switch (index) {
			case 2:
				return ids;
			default:
				return super.getProperty(index);
		}
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
			case 2:
				info.name = "Ids";
				info.type = ArrayOfString.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			default:
				super.getPropertyInfo(index, table, info);
		}
	}

	public ArrayOfString getIds() {
		return ids;
	}

	public void setIds(ArrayOfString ids) {
		this.ids = ids;
	}

	public void setProperty(int index, Object object) {
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("ApplicationResult [");
		sb.append("success=").append(isSuccess());
		sb.append(", ");
		sb.append("error=").append(getError());
		sb.append(", ");
		sb.append("ids=").append(ids);
		return sb.append(']').toString();
	}
}
