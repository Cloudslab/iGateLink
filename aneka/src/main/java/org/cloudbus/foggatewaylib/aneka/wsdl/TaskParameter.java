package org.cloudbus.foggatewaylib.aneka.wsdl;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class TaskParameter extends SoapObject implements Deserializable {

    /** Optional property */
    private java.lang.String name;

    /** Optional property */
    private java.lang.String value;

    public TaskParameter() {
        super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "TaskParameter");
    }

    protected TaskParameter(String nsUri, String name) {
        super(nsUri, name);
    }

    public void fromSoapResponse(AttributeContainer response) {
        fromSoapResponse(this, response);
    }

    protected void fromSoapResponse(TaskParameter object, AttributeContainer response) {
        object.setNameProperty(KSoap2Utils.getString(response, "Name"));
        object.setValue(KSoap2Utils.getString(response, "Value"));

    }

    public int getPropertyCount() {
        return 2;
    }

    public Object getProperty(int index) {
        switch (index) {
            case 0:
                return name;
            case 1:
                return value;
            default:
        }
        return null;
    }

    public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
        switch (index) {
            case 0:
                info.name = "Name";
                info.type = java.lang.String.class;
                info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
                break;
            case 1:
                info.name = "Value";
                info.type = java.lang.String.class;
                info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
                break;
            default:
        }
    }

    public String getNameProperty() {
        return name;
    }

    public void setNameProperty(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setProperty(int index, Object object) {}

    public String toString() {
        StringBuilder sb = new StringBuilder("TaskParameter [");
        sb.append("name=").append(name);
        sb.append(", ");
        sb.append("value=").append(value);
        return sb.append(']').toString();
    }
}