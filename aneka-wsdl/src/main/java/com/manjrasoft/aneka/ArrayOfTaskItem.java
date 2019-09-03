package com.manjrasoft.aneka;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class ArrayOfTaskItem extends SoapObject implements Deserializable {

	/** Optional property */
	private TaskItem[] taskItem;

	public ArrayOfTaskItem() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "ArrayOfTaskItem");
	}

	protected ArrayOfTaskItem(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(ArrayOfTaskItem object, AttributeContainer response) {
		java.util.List taskItemList = KSoap2Utils.getObjectList((SoapObject) response, "TaskItem");
		TaskItem[] taskItemArray = new TaskItem[taskItemList.size()];
		for (int i = 0; i < taskItemArray.length; ++i) {
			taskItemArray[i] = (TaskItem) KSoap2Utils.getObject(new TaskItem(), (AttributeContainer) taskItemList.get(i));
		}
		object.setTaskItem(taskItemArray);
	}

	public int getPropertyCount() {
		return 1;
	}

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return taskItem;
		default:
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "TaskItem";
			info.type = TaskItem[].class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public TaskItem[] getTaskItem() {
		return taskItem;
	}

	public void setTaskItem(TaskItem[] newValue) {
		taskItem = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("ArrayOfTaskItem [");
		sb.append("taskItem=").append(java.util.Arrays.toString(taskItem));
		return sb.append(']').toString();
	}
}
