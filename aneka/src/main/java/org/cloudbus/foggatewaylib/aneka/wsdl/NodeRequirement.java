package org.cloudbus.foggatewaylib.aneka.wsdl;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class NodeRequirement extends SoapObject implements Deserializable {

	/** Mandatory property */
	private int numberOfNodes;

	/** Mandatory property */
	private org.cloudbus.foggatewaylib.aneka.wsdl.CpuSpeed cpuSpeed;

	/** Mandatory property */
	private org.cloudbus.foggatewaylib.aneka.wsdl.DiskSize diskSize;

	/** Mandatory property */
	private org.cloudbus.foggatewaylib.aneka.wsdl.MemorySize memorySize;

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
		Object cpuSpeedValue = KSoap2Utils.getProperty((SoapObject) response, "CpuSpeed");
		object.setCpuSpeed(cpuSpeedValue != null ? (org.cloudbus.foggatewaylib.aneka.wsdl.CpuSpeed) KSoap2Utils.getObject(new org.cloudbus.foggatewaylib.aneka.wsdl.CpuSpeed(), (AttributeContainer) cpuSpeedValue) : null);
		Object diskSizeValue = KSoap2Utils.getProperty((SoapObject) response, "DiskSize");
		object.setDiskSize(diskSizeValue != null ? (org.cloudbus.foggatewaylib.aneka.wsdl.DiskSize) KSoap2Utils.getObject(new org.cloudbus.foggatewaylib.aneka.wsdl.DiskSize(), (AttributeContainer) diskSizeValue) : null);
		Object memorySizeValue = KSoap2Utils.getProperty((SoapObject) response, "MemorySize");
		object.setMemorySize(memorySizeValue != null ? (org.cloudbus.foggatewaylib.aneka.wsdl.MemorySize) KSoap2Utils.getObject(new org.cloudbus.foggatewaylib.aneka.wsdl.MemorySize(), (AttributeContainer) memorySizeValue) : null);
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
			info.type = org.cloudbus.foggatewaylib.aneka.wsdl.CpuSpeed.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		case 2:
			info.name = "DiskSize";
			info.type = org.cloudbus.foggatewaylib.aneka.wsdl.DiskSize.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		case 3:
			info.name = "MemorySize";
			info.type = org.cloudbus.foggatewaylib.aneka.wsdl.MemorySize.class;
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

	public org.cloudbus.foggatewaylib.aneka.wsdl.CpuSpeed getCpuSpeed() {
		return cpuSpeed;
	}

	public void setCpuSpeed(org.cloudbus.foggatewaylib.aneka.wsdl.CpuSpeed newValue) {
		cpuSpeed = newValue;
	}

	public org.cloudbus.foggatewaylib.aneka.wsdl.DiskSize getDiskSize() {
		return diskSize;
	}

	public void setDiskSize(org.cloudbus.foggatewaylib.aneka.wsdl.DiskSize newValue) {
		diskSize = newValue;
	}

	public org.cloudbus.foggatewaylib.aneka.wsdl.MemorySize getMemorySize() {
		return memorySize;
	}

	public void setMemorySize(org.cloudbus.foggatewaylib.aneka.wsdl.MemorySize newValue) {
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
