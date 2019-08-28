package org.cloudbus.foggatewaylib.aneka.wsdl;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class ArrayOfFile extends SoapObject implements Deserializable {

	/** Optional property */
	private org.cloudbus.foggatewaylib.aneka.wsdl.File[] file;

	public ArrayOfFile() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "ArrayOfFile");
	}

	protected ArrayOfFile(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(ArrayOfFile object, AttributeContainer response) {
		java.util.List fileList = KSoap2Utils.getObjectList((SoapObject) response, "File");
		org.cloudbus.foggatewaylib.aneka.wsdl.File[] fileArray = new org.cloudbus.foggatewaylib.aneka.wsdl.File[fileList.size()];
		for (int i = 0; i < fileArray.length; ++i) {
			fileArray[i] = (org.cloudbus.foggatewaylib.aneka.wsdl.File) KSoap2Utils.getObject(new org.cloudbus.foggatewaylib.aneka.wsdl.File(), (AttributeContainer) fileList.get(i));
		}
		object.setFile(fileArray);
	}

	public int getPropertyCount() {
		return 1;
	}

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return file;
		default:
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "File";
			info.type = org.cloudbus.foggatewaylib.aneka.wsdl.File[].class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public org.cloudbus.foggatewaylib.aneka.wsdl.File[] getFile() {
		return file;
	}

	public void setFile(org.cloudbus.foggatewaylib.aneka.wsdl.File[] newValue) {
		file = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("ArrayOfFile [");
		sb.append("file=").append(java.util.Arrays.toString(file));
		return sb.append(']').toString();
	}
}
