package com.manjrasoft.aneka;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class PropertyGroup extends SoapObject implements Deserializable {

	/** Optional property */
	private com.manjrasoft.aneka.ArrayOfPropertyGroup groups;

	/** Optional property */
	private java.lang.String name;

	/** Optional property */
	private com.manjrasoft.aneka.ArrayOfProperty properties;

	public PropertyGroup() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "PropertyGroup");
	}

	protected PropertyGroup(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(PropertyGroup object, AttributeContainer response) {
		Object groupsValue = KSoap2Utils.getProperty((SoapObject) response, "Groups");
		object.setGroups(groupsValue != null ? (com.manjrasoft.aneka.ArrayOfPropertyGroup) KSoap2Utils.getObject(new com.manjrasoft.aneka.ArrayOfPropertyGroup(), (AttributeContainer) groupsValue) : null);
		object.setNameProperty(KSoap2Utils.getString(response, "Name"));
		Object propertiesValue = KSoap2Utils.getProperty((SoapObject) response, "Properties");
		object.setProperties(propertiesValue != null ? (com.manjrasoft.aneka.ArrayOfProperty) KSoap2Utils.getObject(new com.manjrasoft.aneka.ArrayOfProperty(), (AttributeContainer) propertiesValue) : null);
	}

	public int getPropertyCount() {
		return 3;
	}

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return groups;
		case 1:
			return name;
		case 2:
			return properties;
		default:
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "Groups";
			info.type = com.manjrasoft.aneka.ArrayOfPropertyGroup.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		case 1:
			info.name = "Name";
			info.type = java.lang.String.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		case 2:
			info.name = "Properties";
			info.type = com.manjrasoft.aneka.ArrayOfProperty.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public com.manjrasoft.aneka.ArrayOfPropertyGroup getGroups() {
		return groups;
	}

	public void setGroups(com.manjrasoft.aneka.ArrayOfPropertyGroup newValue) {
		groups = newValue;
	}

	public java.lang.String getNameProperty() {
		return name;
	}

	public void setNameProperty(java.lang.String newValue) {
		name = newValue;
	}

	public com.manjrasoft.aneka.ArrayOfProperty getProperties() {
		return properties;
	}

	public void setProperties(com.manjrasoft.aneka.ArrayOfProperty newValue) {
		properties = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("PropertyGroup [");
		sb.append("groups=").append(groups);
		sb.append(", ");
		sb.append("name=").append(name);
		sb.append(", ");
		sb.append("properties=").append(properties);
		return sb.append(']').toString();
	}
}
