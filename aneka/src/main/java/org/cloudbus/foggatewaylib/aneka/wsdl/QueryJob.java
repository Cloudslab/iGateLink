package org.cloudbus.foggatewaylib.aneka.wsdl;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class QueryJob extends SoapObject implements Deserializable {

	/** Optional property */
	private org.cloudbus.foggatewaylib.aneka.wsdl.JobQueryRequest request;

	public QueryJob() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "QueryJob");
	}

	protected QueryJob(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(QueryJob object, AttributeContainer response) {
		Object requestValue = KSoap2Utils.getProperty((SoapObject) response, "request");
		object.setRequest(requestValue != null ? (org.cloudbus.foggatewaylib.aneka.wsdl.JobQueryRequest) KSoap2Utils.getObject(new org.cloudbus.foggatewaylib.aneka.wsdl.JobQueryRequest(), (AttributeContainer) requestValue) : null);
	}

	public int getPropertyCount() {
		return 1;
	}

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return request;
		default:
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "request";
			info.type = org.cloudbus.foggatewaylib.aneka.wsdl.JobQueryRequest.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public org.cloudbus.foggatewaylib.aneka.wsdl.JobQueryRequest getRequest() {
		return request;
	}

	public void setRequest(org.cloudbus.foggatewaylib.aneka.wsdl.JobQueryRequest newValue) {
		request = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("QueryJob [");
		sb.append("request=").append(request);
		return sb.append(']').toString();
	}
}
