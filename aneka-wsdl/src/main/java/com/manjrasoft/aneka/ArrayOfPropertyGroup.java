package com.manjrasoft.aneka;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class ArrayOfPropertyGroup extends SoapObject implements Deserializable {

	/** Optional property */
	private PropertyGroup[] propertyGroup;

	public ArrayOfPropertyGroup() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "ArrayOfPropertyGroup");
	}

	protected ArrayOfPropertyGroup(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(ArrayOfPropertyGroup object, AttributeContainer response) {
		java.util.List propertyGroupList = KSoap2Utils.getObjectList((SoapObject) response, "PropertyGroup");
		PropertyGroup[] propertyGroupArray = new PropertyGroup[propertyGroupList.size()];
		for (int i = 0; i < propertyGroupArray.length; ++i) {
			propertyGroupArray[i] = (PropertyGroup) KSoap2Utils.getObject(new PropertyGroup(), (AttributeContainer) propertyGroupList.get(i));
		}
		object.setPropertyGroup(propertyGroupArray);
	}

	public int getPropertyCount() {
		return 1;
	}

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return propertyGroup;
		default:
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "PropertyGroup";
			info.type = PropertyGroup[].class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public PropertyGroup[] getPropertyGroup() {
		return propertyGroup;
	}

	public void setPropertyGroup(PropertyGroup[] newValue) {
		propertyGroup = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("ArrayOfPropertyGroup [");
		sb.append("propertyGroup=").append(java.util.Arrays.toString(propertyGroup));
		return sb.append(']').toString();
	}
}
