package org.cloudbus.foggatewaylib.aneka.wsdl;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class StringType extends SoapObject implements Deserializable {
    private String value;
    private String type;

    protected StringType(String type, String nsUri, String name) {
        super(nsUri, name);
        this.type = type;
    }
    protected StringType(String nsUri, String name) {
        this("anyType", nsUri, name);
    }

    public void fromSoapResponse(AttributeContainer response) {
        fromSoapResponse(this, response);
    }

    protected void fromSoapResponse(StringType object, AttributeContainer response) {
        object.setValue(KSoap2Utils.getString(response, type));
    }

    public int getPropertyCount() {
        return 3;
    }

    public Object getProperty(int index) {
        switch (index) {
            case 0:
                return value;
            default:
        }
        return null;
    }

    public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
        switch (index) {
            case 0:
                info.name = getName();
                info.type = String.class;
                info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
                break;
            default:
        }
    }

    public void setProperty(int index, Object object) {}

    public String getValue() {
        return value;
    }

    public void setValue(String newValue) {
        value = newValue;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(getName() + " [");
        sb.append("value=").append(value);
        return sb.append(']').toString();
    }
}
