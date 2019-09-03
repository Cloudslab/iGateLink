package com.manjrasoft.aneka;

import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;

public class CopyTaskItem extends TaskItem {

    /** Optional property */
    private java.lang.String source;

    /** Optional property */
    private java.lang.String target;

    public CopyTaskItem() {
        super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "CopyTaskItem");
    }

    protected CopyTaskItem(String nsUri, String name) {
        super(nsUri, name);
    }

    public void fromSoapResponse(AttributeContainer response) {
        fromSoapResponse(this, response);
    }

    protected void fromSoapResponse(CopyTaskItem object, AttributeContainer response) {
        object.setSource(KSoap2Utils.getString(response, "Source"));
        object.setTarget(KSoap2Utils.getString(response, "Target"));

    }

    public int getPropertyCount() {
        return 2;
    }

    public Object getProperty(int index) {
        switch (index) {
            case 0:
                return source;
            case 1:
                return target;
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
                info.name = "Target";
                info.type = java.lang.String.class;
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

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setProperty(int index, Object object) {}

    public String toString() {
        StringBuilder sb = new StringBuilder("CopyTaskItem [");
        sb.append("source=").append(source);
        sb.append(", ");
        sb.append("target=").append(target);
        return sb.append(']').toString();
    }
}
