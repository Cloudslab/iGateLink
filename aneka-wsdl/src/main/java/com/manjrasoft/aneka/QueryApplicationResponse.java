package com.manjrasoft.aneka;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class QueryApplicationResponse extends SoapObject implements Deserializable {

	/** Optional property */
	private com.manjrasoft.aneka.ApplicationResult queryApplicationResult;

	public QueryApplicationResponse() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "QueryApplicationResponse");
	}

	protected QueryApplicationResponse(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(QueryApplicationResponse object, AttributeContainer response) {
		Object queryApplicationResultValue = KSoap2Utils.getProperty((SoapObject) response, "QueryApplicationResult");
		object.setQueryApplicationResult(queryApplicationResultValue != null ? (com.manjrasoft.aneka.ApplicationResult) KSoap2Utils.getObject(new com.manjrasoft.aneka.ApplicationResult(), (AttributeContainer) queryApplicationResultValue) : null);
	}

	public int getPropertyCount() {
		return 1;
	}

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return queryApplicationResult;
		default:
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "QueryApplicationResult";
			info.type = com.manjrasoft.aneka.ApplicationResult.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public com.manjrasoft.aneka.ApplicationResult getQueryApplicationResult() {
		return queryApplicationResult;
	}

	public void setQueryApplicationResult(com.manjrasoft.aneka.ApplicationResult newValue) {
		queryApplicationResult = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("QueryApplicationResponse [");
		sb.append("queryApplicationResult=").append(queryApplicationResult);
		return sb.append(']').toString();
	}
}
