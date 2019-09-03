package com.manjrasoft.aneka;

import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class CompositeTaskItem extends TaskItem {

    /** Optional property */
    private ArrayOfTaskItem tasks;

    public CompositeTaskItem() {
        super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "CompositeTaskItem");
    }

    protected CompositeTaskItem(String nsUri, String name) {
        super(nsUri, name);
    }

    public void fromSoapResponse(AttributeContainer response) {
        fromSoapResponse(this, response);
    }

    protected void fromSoapResponse(CompositeTaskItem object, AttributeContainer response) {
        Object tasksValue = KSoap2Utils.getProperty((SoapObject) response, "Tasks");
        object.setTasks(tasksValue != null ? (com.manjrasoft.aneka.ArrayOfTaskItem) KSoap2Utils.getObject(new com.manjrasoft.aneka.ArrayOfTaskItem(), (AttributeContainer) tasksValue) : null);
    }

    public int getPropertyCount() {
        return 1;
    }

    public Object getProperty(int index) {
        switch (index) {
            case 0:
                return tasks;
            default:
                return super.getProperty(index);
        }
    }

    public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
        switch (index) {
            case 0:
                info.name = "Tasks";
                info.type = ArrayOfTaskItem.class;
                info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
                break;
            default:
                super.getPropertyInfo(index, table, info);
        }
    }
    public
    ArrayOfTaskItem getTasks() {
        return tasks;
    }

    public void setTasks(ArrayOfTaskItem tasks) {
        this.tasks = tasks;
    }


    public void setProperty(int index, Object object) {}

    public String toString() {
        StringBuilder sb = new StringBuilder("CompositeTaskItem [");
        sb.append("tasks=").append(tasks);
        return sb.append(']').toString();
    }
}
