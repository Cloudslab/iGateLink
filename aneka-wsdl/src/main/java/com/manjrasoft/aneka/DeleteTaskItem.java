package com.manjrasoft.aneka;

import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;

public class DeleteTaskItem extends TaskItem {

    /** Optional property */
    private java.lang.String file;

    public DeleteTaskItem() {
        super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "DeleteTaskItem");
    }

    protected DeleteTaskItem(String nsUri, String name) {
        super(nsUri, name);
    }

    public void fromSoapResponse(AttributeContainer response) {
        fromSoapResponse(this, response);
    }

    protected void fromSoapResponse(DeleteTaskItem object, AttributeContainer response) {
        object.setFile(KSoap2Utils.getString(response, "File"));

    }

    public int getPropertyCount() {
        return 1;
    }

    public Object getProperty(int index) {
        switch (index) {
            case 0:
                return file;
            default:
                return super.getProperty(index);
        }
    }

    public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
        switch (index) {
            case 0:
                info.name = "File";
                info.type = java.lang.String.class;
                info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
                break;
            default:
                super.getPropertyInfo(index, table, info);
        }
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void setProperty(int index, Object object) {}

    public String toString() {
        StringBuilder sb = new StringBuilder("DeleteTaskItem [");
        sb.append("file=").append(file);
        return sb.append(']').toString();
    }
}
