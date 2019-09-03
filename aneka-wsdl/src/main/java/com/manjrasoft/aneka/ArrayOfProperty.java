package com.manjrasoft.aneka;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class ArrayOfProperty extends SoapObject implements Deserializable {

	/** Optional property */
	private Property[] property;

	public ArrayOfProperty() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "ArrayOfProperty");
	}

	protected ArrayOfProperty(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(ArrayOfProperty object, AttributeContainer response) {
		java.util.List propertyList = KSoap2Utils.getObjectList((SoapObject) response, "Property");
		Property[] propertyArray = new Property[propertyList.size()];
		for (int i = 0; i < propertyArray.length; ++i) {
			propertyArray[i] = (Property) KSoap2Utils.getObject(new Property(), (AttributeContainer) propertyList.get(i));
		}
		object.setProperty(propertyArray);
	}

	public int getPropertyCount() {
		return 1;
	}

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return property;
		default:
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "Property";
			info.type = Property[].class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public Property[] getProperty() {
		return property;
	}

	public void setProperty(Property[] newValue) {
		property = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("ArrayOfProperty [");
		sb.append("property=").append(java.util.Arrays.toString(property));
		return sb.append(']').toString();
	}
}
