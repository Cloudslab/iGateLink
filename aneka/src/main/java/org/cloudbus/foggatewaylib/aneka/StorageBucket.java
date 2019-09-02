package org.cloudbus.foggatewaylib.aneka;

import org.cloudbus.foggatewaylib.aneka.wsdl.ArrayOfProperty;
import org.cloudbus.foggatewaylib.aneka.wsdl.Property;
import org.cloudbus.foggatewaylib.aneka.wsdl.PropertyGroup;

public class StorageBucket {
    private String type;
    private String name;

    public StorageBucket(String type, String name){
        this.type = type;
        this.name = name;
    }

    public int getPropertyCount(){
        return 1;
    }

    public final Property getProperty(int index){
        Property property = new Property();
        fillProperty(index, property);
        return property;
    }

    protected void fillProperty(int index, Property property){
        switch (index){
            case 0:
                property.setNameProperty("StorageType");
                property.setValue(type);
                break;
            default:

        }
    }

    public ArrayOfProperty getArrayOfProperty(){
        int count = getPropertyCount();
        Property[] properties = new Property[count];
        for (int i = 0; i < count; i++){
            properties[i] = getProperty(i);
        }
        ArrayOfProperty arrayOfProperty = new ArrayOfProperty();
        arrayOfProperty.setProperty(properties);
        return arrayOfProperty;
    }

    public PropertyGroup asPropertyGroup(){
        PropertyGroup propertyGroup = new PropertyGroup();
        propertyGroup.setProperties(getArrayOfProperty());
        propertyGroup.setNameProperty(name);
        return propertyGroup;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
