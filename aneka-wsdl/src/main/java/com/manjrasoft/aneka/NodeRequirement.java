package com.manjrasoft.aneka;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class NodeRequirement extends SoapObject implements Deserializable {

	/** Mandatory property */
	private int numberOfNodes;

	/** Mandatory property */
	private String cpuSpeed;

	/** Mandatory property */
	private String diskSize;

	/** Mandatory property */
	private String memorySize;

	/** Mandatory property */
	private boolean multiCoreAllowed;

	public NodeRequirement() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "NodeRequirement");
	}

	protected NodeRequirement(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(NodeRequirement object, AttributeContainer response) {
		object.setNumberOfNodes(KSoap2Utils.getInteger(response, "NumberOfNodes"));
		object.setCpuSpeed(KSoap2Utils.getString(response,  "CpuSpeed"));
		object.setDiskSize(KSoap2Utils.getString(response,  "DiskSize"));
		object.setMemorySize(KSoap2Utils.getString(response,  "MemorySize"));
		object.setMultiCoreAllowed(KSoap2Utils.getBoolean(response, "MultiCoreAllowed"));
	}

	public int getPropertyCount() {
		return 5;
	}

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return numberOfNodes;
		case 1:
			return cpuSpeed;
		case 2:
			return diskSize;
		case 3:
			return memorySize;
		case 4:
			return multiCoreAllowed;
		default:
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "NumberOfNodes";
			info.type = int.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		case 1:
			info.name = "CpuSpeed";
			info.type = String.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		case 2:
			info.name = "DiskSize";
			info.type = String.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		case 3:
			info.name = "MemorySize";
			info.type = String.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		case 4:
			info.name = "MultiCoreAllowed";
			info.type = boolean.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public int getNumberOfNodes() {
		return numberOfNodes;
	}

	public void setNumberOfNodes(int newValue) {
		numberOfNodes = newValue;
	}

	public String getCpuSpeed() {
		return cpuSpeed;
	}

	public void setCpuSpeed(String newValue) {
		cpuSpeed = newValue;
	}

	public String getDiskSize() {
		return diskSize;
	}

	public void setDiskSize(String newValue) {
		diskSize = newValue;
	}

	public String getMemorySize() {
		return memorySize;
	}

	public void setMemorySize(String newValue) {
		memorySize = newValue;
	}

	public boolean isMultiCoreAllowed() {
		return multiCoreAllowed;
	}

	public void setMultiCoreAllowed(boolean newValue) {
		multiCoreAllowed = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("NodeRequirement [");
		sb.append("numberOfNodes=").append(numberOfNodes);
		sb.append(", ");
		sb.append("cpuSpeed=").append(cpuSpeed);
		sb.append(", ");
		sb.append("diskSize=").append(diskSize);
		sb.append(", ");
		sb.append("memorySize=").append(memorySize);
		sb.append(", ");
		sb.append("multiCoreAllowed=").append(multiCoreAllowed);
		return sb.append(']').toString();
	}
}
