package org.cloudbus.foggatewaylib.aneka.wsdl;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class TaskDependency extends SoapObject implements Deserializable {

    /** Optional property */
    private java.lang.String moduleName;

    /** Optional property */
    private java.lang.String moduleData;

    /** Optional property */
    private java.lang.String fileName;

    public TaskDependency() {
        super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "TaskDependency");
    }

    protected TaskDependency(String nsUri, String name) {
        super(nsUri, name);
    }

    public void fromSoapResponse(AttributeContainer response) {
        fromSoapResponse(this, response);
    }

    protected void fromSoapResponse(TaskDependency object, AttributeContainer response) {
        object.setModuleName(KSoap2Utils.getString(response, "ModuleName"));
        object.setModuleData(KSoap2Utils.getString(response, "ModuleData"));
        object.setFileName(KSoap2Utils.getString(response, "FileName"));
    }

    public int getPropertyCount() {
        return 3;
    }

    public Object getProperty(int index) {
        switch (index) {
            case 0:
                return moduleName;
            case 1:
                return moduleData;
            case 2:
                return fileName;
            default:
        }
        return null;
    }

    public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
        switch (index) {
            case 0:
                info.name = "ModuleName";
                info.type = java.lang.String.class;
                info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
                break;
            case 1:
                info.name = "ModuleData";
                info.type = java.lang.String.class;
                info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
                break;
            case 2:
                info.name = "FileName";
                info.type = java.lang.String.class;
                info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
                break;
            default:
        }
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleData() {
        return moduleData;
    }

    public void setModuleData(String moduleData) {
        this.moduleData = moduleData;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setProperty(int index, Object object) {}

    public String toString() {
        StringBuilder sb = new StringBuilder("TaskDependency [");
        sb.append("moduleName=").append(moduleName);
        sb.append(", ");
        sb.append("moduleData=").append(moduleData);
        sb.append(", ");
        sb.append("fileName=").append(fileName);
        return sb.append(']').toString();
    }
}