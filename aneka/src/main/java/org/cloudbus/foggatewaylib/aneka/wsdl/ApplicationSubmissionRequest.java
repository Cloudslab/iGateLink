package org.cloudbus.foggatewaylib.aneka.wsdl;

import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class ApplicationSubmissionRequest extends org.cloudbus.foggatewaylib.aneka.wsdl.Request {

	/** Optional property */
	private String displayName;

	/** Optional property */
	private PropertyGroup metadata;

	/** Optional property */
	private ArrayOfFile sharedFiles;

	/** Optional property */
	private QoS qos;

	public ApplicationSubmissionRequest() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "ApplicationSubmissionRequest");
	}

	protected ApplicationSubmissionRequest(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(ApplicationSubmissionRequest object, AttributeContainer response) {
		super.fromSoapResponse(object, response);
		displayName = KSoap2Utils.getString((SoapObject) response, "DisplayName");
		Object metadataValue = KSoap2Utils.getProperty((SoapObject) response, "Metadata");
		object.setMetadata(metadataValue != null ? (org.cloudbus.foggatewaylib.aneka.wsdl.PropertyGroup) KSoap2Utils.getObject(new org.cloudbus.foggatewaylib.aneka.wsdl.PropertyGroup(), (AttributeContainer) metadataValue) : null);
		Object sharedFilesValue = KSoap2Utils.getProperty((SoapObject) response, "SharedFiles");
		object.setSharedFiles(sharedFilesValue != null ? (org.cloudbus.foggatewaylib.aneka.wsdl.ArrayOfFile) KSoap2Utils.getObject(new org.cloudbus.foggatewaylib.aneka.wsdl.ArrayOfFile(), (AttributeContainer) sharedFilesValue) : null);
		Object qosValue = KSoap2Utils.getProperty((SoapObject) response, "QoS");
		object.setQos(qosValue != null ? (org.cloudbus.foggatewaylib.aneka.wsdl.QoS) KSoap2Utils.getObject(new org.cloudbus.foggatewaylib.aneka.wsdl.QoS(), (AttributeContainer) qosValue) : null);
	}

	public int getPropertyCount() {
		return 5;
	}

	public Object getProperty(int index) {
		switch (index) {
			case 1:
				return displayName;
			case 2:
				return metadata;
			case 3:
				return sharedFiles;
			case 4:
				return qos;
			default:
				super.getProperty(index);
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
			case 1:
				info.name = "DisplayName";
				info.type = String.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			case 2:
				info.name = "Metadata";
				info.type = PropertyGroup.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			case 3:
				info.name = "SharedFiles";
				info.type = ArrayOfFile.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			case 4:
				info.name = "QoS";
				info.type = QoS.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			default:
				super.getPropertyInfo(index, table, info);
		}
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public PropertyGroup getMetadata() {
		return metadata;
	}

	public void setMetadata(PropertyGroup metadata) {
		this.metadata = metadata;
	}

	public ArrayOfFile getSharedFiles() {
		return sharedFiles;
	}

	public void setSharedFiles(ArrayOfFile sharedFiles) {
		this.sharedFiles = sharedFiles;
	}

	public QoS getQos() {
		return qos;
	}

	public void setQos(QoS qos) {
		this.qos = qos;
	}

	public void setProperty(int index, Object object) {}

	public String toString() {
		StringBuilder sb = new StringBuilder("ApplicationSubmissionRequest [");
		sb.append("userCredential=").append(getUserCredential());
		sb.append(", ");
		sb.append("displayName=").append(displayName);
		sb.append(", ");
		sb.append("metadata=").append(metadata);
		sb.append(", ");
		sb.append("sharedFiles=").append(sharedFiles);
		sb.append(", ");
		sb.append("qos=").append(qos);
		return sb.append(']').toString();
	}
}