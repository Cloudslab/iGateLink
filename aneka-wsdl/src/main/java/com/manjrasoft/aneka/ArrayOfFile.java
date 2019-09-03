package com.manjrasoft.aneka;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class ArrayOfFile extends SoapObject implements Deserializable {

	/** Optional property */
	private File[] file;

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
		File[] fileArray = new File[fileList.size()];
		for (int i = 0; i < fileArray.length; ++i) {
			fileArray[i] = (File) KSoap2Utils.getObject(new File(), (AttributeContainer) fileList.get(i));
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
			info.type = File[].class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public File[] getFile() {
		return file;
	}

	public void setFile(File[] newValue) {
		file = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("ArrayOfFile [");
		sb.append("file=").append(java.util.Arrays.toString(file));
		return sb.append(']').toString();
	}
}
