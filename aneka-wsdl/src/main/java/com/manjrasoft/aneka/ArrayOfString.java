package com.manjrasoft.aneka;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class ArrayOfString extends SoapObject implements Deserializable {

	/** Optional property */
	private java.lang.String[] string;

	public ArrayOfString() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "ArrayOfString");
	}

	protected ArrayOfString(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(ArrayOfString object, AttributeContainer response) {
		java.util.List stringList = KSoap2Utils.getObjectList((SoapObject) response, "string");
		java.lang.String[] stringArray = new java.lang.String[stringList.size()];
		for (int i = 0; i < stringArray.length; ++i) {
			stringArray[i] = String.valueOf(stringList.get(i).toString());
		}
		object.setString(stringArray);
	}

	public int getPropertyCount() {
		return 1;
	}

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return string;
		default:
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "string";
			info.type = java.lang.String[].class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public java.lang.String[] getString() {
		return string;
	}

	public void setString(java.lang.String[] newValue) {
		string = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("ArrayOfString [");
		sb.append("string=").append(java.util.Arrays.toString(string));
		return sb.append(']').toString();
	}
}
