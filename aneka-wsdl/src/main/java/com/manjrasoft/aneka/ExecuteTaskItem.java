package com.manjrasoft.aneka;

import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;

public class ExecuteTaskItem extends TaskItem {

    /** Optional property */
    private java.lang.String command;

    /** Optional property */
    private java.lang.String arguments;

    public ExecuteTaskItem() {
        super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "ExecuteTaskItem");
    }

    protected ExecuteTaskItem(String nsUri, String name) {
        super(nsUri, name);
    }

    public void fromSoapResponse(AttributeContainer response) {
        fromSoapResponse(this, response);
    }

    protected void fromSoapResponse(ExecuteTaskItem object, AttributeContainer response) {
        object.setCommand(KSoap2Utils.getString(response, "Command"));
        object.setArguments(KSoap2Utils.getString(response, "Arguments"));

    }

    public int getPropertyCount() {
        return 2;
    }

    public Object getProperty(int index) {
        switch (index) {
            case 0:
                return command;
            case 1:
                return arguments;
            default:
                return super.getProperty(index);
        }
    }

    public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
        switch (index) {
            case 0:
                info.name = "Command";
                info.type = java.lang.String.class;
                info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
                break;
            case 1:
                info.name = "Arguments";
                info.type = java.lang.String.class;
                info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
                break;
            default:
                super.getPropertyInfo(index, table, info);
        }
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    public void setProperty(int index, Object object) {}

    public String toString() {
        StringBuilder sb = new StringBuilder("ExecuteTaskItem [");
        sb.append("command=").append(command);
        sb.append(", ");
        sb.append("arguments=").append(arguments);
        return sb.append(']').toString();
    }
}
