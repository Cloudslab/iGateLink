package org.cloudbus.foggatewaylib.aneka.wsdl;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class ArrayOfTaskDependency extends SoapObject implements Deserializable {

    /** Optional property */
    private org.cloudbus.foggatewaylib.aneka.wsdl.TaskDependency[] taskDependency;

    public ArrayOfTaskDependency() {
        super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "ArrayOfTaskDependency");
    }

    protected ArrayOfTaskDependency(String nsUri, String name) {
        super(nsUri, name);
    }

    public void fromSoapResponse(AttributeContainer response) {
        fromSoapResponse(this, response);
    }

    protected void fromSoapResponse(ArrayOfTaskDependency object, AttributeContainer response) {
        java.util.List taskDependencyList = KSoap2Utils.getObjectList((SoapObject) response, "TaskDependency");
        org.cloudbus.foggatewaylib.aneka.wsdl.TaskDependency[] taskDependencyArray = new org.cloudbus.foggatewaylib.aneka.wsdl.TaskDependency[taskDependencyList.size()];
        for (int i = 0; i < taskDependencyArray.length; ++i) {
            taskDependencyArray[i] = (org.cloudbus.foggatewaylib.aneka.wsdl.TaskDependency) KSoap2Utils.getObject(new org.cloudbus.foggatewaylib.aneka.wsdl.TaskDependency(), (AttributeContainer) taskDependencyList.get(i));
        }
        object.setTaskDependency(taskDependencyArray);
    }

    public int getPropertyCount() {
        return 1;
    }

    public Object getProperty(int index) {
        switch (index) {
            case 0:
                return taskDependency;
            default:
        }
        return null;
    }

    public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
        switch (index) {
            case 0:
                info.name = "TaskDependency";
                info.type = org.cloudbus.foggatewaylib.aneka.wsdl.TaskDependency[].class;
                info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
                break;
            default:
        }
    }

    public void setProperty(int index, Object object) {}

    public TaskDependency[] getTaskDependency() {
        return taskDependency;
    }

    public void setTaskDependency(TaskDependency[] taskDependency) {
        this.taskDependency = taskDependency;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ArrayOfTaskItem [");
        sb.append("taskDependency=").append(java.util.Arrays.toString(taskDependency));
        return sb.append(']').toString();
    }
}
