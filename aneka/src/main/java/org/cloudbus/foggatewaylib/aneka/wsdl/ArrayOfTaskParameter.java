package org.cloudbus.foggatewaylib.aneka.wsdl;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class ArrayOfTaskParameter extends SoapObject implements Deserializable {

    /** Optional property */
    private org.cloudbus.foggatewaylib.aneka.wsdl.TaskParameter[] taskParameter;

    public ArrayOfTaskParameter() {
        super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "ArrayOfTaskParameter");
    }

    protected ArrayOfTaskParameter(String nsUri, String name) {
        super(nsUri, name);
    }

    public void fromSoapResponse(AttributeContainer response) {
        fromSoapResponse(this, response);
    }

    protected void fromSoapResponse(ArrayOfTaskParameter object, AttributeContainer response) {
        java.util.List taskParameterList = KSoap2Utils.getObjectList((SoapObject) response, "TaskParameter");
        org.cloudbus.foggatewaylib.aneka.wsdl.TaskParameter[] taskParameterArray = new org.cloudbus.foggatewaylib.aneka.wsdl.TaskParameter[taskParameterList.size()];
        for (int i = 0; i < taskParameterArray.length; ++i) {
            taskParameterArray[i] = (org.cloudbus.foggatewaylib.aneka.wsdl.TaskParameter) KSoap2Utils.getObject(new org.cloudbus.foggatewaylib.aneka.wsdl.TaskParameter(), (AttributeContainer) taskParameterList.get(i));
        }
        object.setTaskParameter(taskParameterArray);
    }

    public int getPropertyCount() {
        return 1;
    }

    public Object getProperty(int index) {
        switch (index) {
            case 0:
                return taskParameter;
            default:
        }
        return null;
    }

    public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
        switch (index) {
            case 0:
                info.name = "TaskParameter";
                info.type = org.cloudbus.foggatewaylib.aneka.wsdl.TaskParameter[].class;
                info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
                break;
            default:
        }
    }

    public void setProperty(int index, Object object) {}

    public TaskParameter[] getTaskParameter() {
        return taskParameter;
    }

    public void setTaskParameter(TaskParameter[] taskParameter) {
        this.taskParameter = taskParameter;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ArrayOfTaskParameter [");
        sb.append("taskParameter=").append(java.util.Arrays.toString(taskParameter));
        return sb.append(']').toString();
    }
}
