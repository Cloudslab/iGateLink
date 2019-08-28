package org.cloudbus.foggatewaylib.aneka.wsdl;

import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class NativeTaskItem extends TaskItem {

    /** Optional property */
    private java.lang.String runtime;

    /** Optional property */
    private ArrayOfTaskDependency dependencies;

    /** Optional property */
    private java.lang.String metadata;

    /** Optional property */
    private java.lang.String taskData;

    public NativeTaskItem() {
        super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "NativeTaskItem");
    }

    protected NativeTaskItem(String nsUri, String name) {
        super(nsUri, name);
    }

    public void fromSoapResponse(AttributeContainer response) {
        fromSoapResponse(this, response);
    }

    protected void fromSoapResponse(NativeTaskItem object, AttributeContainer response) {
        object.setRuntime(KSoap2Utils.getString(response, "Runtime"));
        Object dependenciesValue = KSoap2Utils.getProperty((SoapObject) response, "Dependencies");
        object.setDependencies(dependenciesValue != null ? (org.cloudbus.foggatewaylib.aneka.wsdl.ArrayOfTaskDependency) KSoap2Utils.getObject(new org.cloudbus.foggatewaylib.aneka.wsdl.ArrayOfTaskDependency(), (AttributeContainer) dependenciesValue) : null);
        object.setMetadata(KSoap2Utils.getString(response, "Metadata"));
        object.setTaskData(KSoap2Utils.getString(response, "TaskData"));

    }

    public int getPropertyCount() {
        return 4;
    }

    public Object getProperty(int index) {
        switch (index) {
            case 0:
                return runtime;
            case 1:
                return dependencies;
            case 2:
                return metadata;
            case 3:
                return taskData;
            default:
                return super.getProperty(index);
        }
    }

    public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
        switch (index) {
            case 0:
                info.name = "Runtime";
                info.type = java.lang.String.class;
                info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
                break;
            case 1:
                info.name = "Dependencies";
                info.type = ArrayOfTaskDependency.class;
                info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
                break;
            case 2:
                info.name = "Metadata";
                info.type = java.lang.String.class;
                info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
                break;
            case 3:
                info.name = "TaskData";
                info.type = java.lang.String.class;
                info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
                break;
            default:
                super.getPropertyInfo(index, table, info);
        }
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public ArrayOfTaskDependency getDependencies() {
        return dependencies;
    }

    public void setDependencies(ArrayOfTaskDependency dependencies) {
        this.dependencies = dependencies;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getTaskData() {
        return taskData;
    }

    public void setTaskData(String taskData) {
        this.taskData = taskData;
    }

    public void setProperty(int index, Object object) {}

    public String toString() {
        StringBuilder sb = new StringBuilder("NativeTaskItem [");
        sb.append("runtime=").append(runtime);
        sb.append(", ");
        sb.append("dependencies=").append(dependencies);
        sb.append(", ");
        sb.append("metadata=").append(metadata);
        sb.append(", ");
        sb.append("taskData=").append(taskData);
        return sb.append(']').toString();
    }
}
