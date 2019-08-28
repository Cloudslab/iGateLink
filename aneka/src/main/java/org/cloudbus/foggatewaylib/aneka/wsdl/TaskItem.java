package org.cloudbus.foggatewaylib.aneka.wsdl;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class TaskItem extends SoapObject implements Deserializable {

	public TaskItem() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "TaskItem");
	}

	protected TaskItem(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(TaskItem object, AttributeContainer response) {
	}

	public int getPropertyCount() {
		return 0;
	}

	public Object getProperty(int index) {
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
	}

	public void setProperty(int index, Object object) {}
}
