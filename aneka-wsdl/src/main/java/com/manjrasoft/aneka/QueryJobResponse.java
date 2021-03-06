package com.manjrasoft.aneka;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class QueryJobResponse extends SoapObject implements Deserializable {

	/** Optional property */
	private com.manjrasoft.aneka.JobResult queryJobResult;

	public QueryJobResponse() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "QueryJobResponse");
	}

	protected QueryJobResponse(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(QueryJobResponse object, AttributeContainer response) {
		Object queryJobResultValue = KSoap2Utils.getProperty((SoapObject) response, "QueryJobResult");
		object.setQueryJobResult(queryJobResultValue != null ? (com.manjrasoft.aneka.JobResult) KSoap2Utils.getObject(new com.manjrasoft.aneka.JobResult(), (AttributeContainer) queryJobResultValue) : null);
	}

	public int getPropertyCount() {
		return 1;
	}

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return queryJobResult;
		default:
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "QueryJobResult";
			info.type = com.manjrasoft.aneka.JobResult.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public com.manjrasoft.aneka.JobResult getQueryJobResult() {
		return queryJobResult;
	}

	public void setQueryJobResult(com.manjrasoft.aneka.JobResult newValue) {
		queryJobResult = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("QueryJobResponse [");
		sb.append("queryJobResult=").append(queryJobResult);
		return sb.append(']').toString();
	}
}
