package org.cloudbus.foggatewaylib.aneka.wsdl;

import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class SubstituteTaskItem extends TaskItem {

    /** Optional property */
    private java.lang.String source;

    /** Optional property */
    private java.lang.String destination;

    /** Optional property */
    private ArrayOfTaskParameter parameters;

    public SubstituteTaskItem() {
        super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "SubstituteTaskItem");
    }

    protected SubstituteTaskItem(String nsUri, String name) {
        super(nsUri, name);
    }

    public void fromSoapResponse(AttributeContainer response) {
        fromSoapResponse(this, response);
    }

    protected void fromSoapResponse(SubstituteTaskItem object, AttributeContainer response) {
        object.setSource(KSoap2Utils.getString(response, "Source"));
        object.setDestination(KSoap2Utils.getString(response, "Destination"));
        Object parametersValue = KSoap2Utils.getProperty((SoapObject) response, "Parameters");
        object.setParameters(parametersValue != null ? (org.cloudbus.foggatewaylib.aneka.wsdl.ArrayOfTaskParameter) KSoap2Utils.getObject(new org.cloudbus.foggatewaylib.aneka.wsdl.ArrayOfTaskParameter(), (AttributeContainer) parametersValue) : null);

    }

    public int getPropertyCount() {
        return 3;
    }

    public Object getProperty(int index) {
        switch (index) {
            case 0:
                return source;
            case 1:
                return destination;
            case 2:
                return parameters;
            default:
                return super.getProperty(index);
        }
    }

    public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
        switch (index) {
            case 0:
                info.name = "Source";
                info.type = java.lang.String.class;
                info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
                break;
            case 1:
                info.name = "Destination";
                info.type = java.lang.String.class;
                info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
                break;
            case 2:
                info.name = "Parameters";
                info.type = ArrayOfTaskParameter.class;
                info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
                break;
            default:
                super.getPropertyInfo(index, table, info);
        }
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public ArrayOfTaskParameter getParameters() {
        return parameters;
    }

    public void setParameters(ArrayOfTaskParameter parameters) {
        this.parameters = parameters;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }


    public void setProperty(int index, Object object) {}

    public String toString() {
        StringBuilder sb = new StringBuilder("SubstituteTaskItem [");
        sb.append("source=").append(source);
        sb.append(", ");
        sb.append("destination=").append(destination);
        sb.append(", ");
        sb.append("parameters=").append(parameters);
        return sb.append(']').toString();
    }
}
