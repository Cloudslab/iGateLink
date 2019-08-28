package org.cloudbus.foggatewaylib.aneka.wsdl;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class File extends SoapObject implements Deserializable {

	/** Optional property */
	private java.lang.String path;

	/** Optional property */
	private java.lang.String virtualPath;

	/** Optional property */
	private java.lang.String storageBucketId;

	public File() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "File");
	}

	protected File(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(File object, AttributeContainer response) {
		object.setPath(KSoap2Utils.getString(response, "Path"));
		object.setVirtualPath(KSoap2Utils.getString(response, "VirtualPath"));
		object.setStorageBucketId(KSoap2Utils.getString(response, "StorageBucketId"));
	}

	public int getPropertyCount() {
		return 3;
	}

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return path;
		case 1:
			return virtualPath;
		case 2:
			return storageBucketId;
		default:
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "Path";
			info.type = java.lang.String.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		case 1:
			info.name = "VirtualPath";
			info.type = java.lang.String.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		case 2:
			info.name = "StorageBucketId";
			info.type = java.lang.String.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public java.lang.String getPath() {
		return path;
	}

	public void setPath(java.lang.String newValue) {
		path = newValue;
	}

	public java.lang.String getVirtualPath() {
		return virtualPath;
	}

	public void setVirtualPath(java.lang.String newValue) {
		virtualPath = newValue;
	}

	public java.lang.String getStorageBucketId() {
		return storageBucketId;
	}

	public void setStorageBucketId(java.lang.String newValue) {
		storageBucketId = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("File [");
		sb.append("path=").append(path);
		sb.append(", ");
		sb.append("virtualPath=").append(virtualPath);
		sb.append(", ");
		sb.append("storageBucketId=").append(storageBucketId);
		return sb.append(']').toString();
	}
}
