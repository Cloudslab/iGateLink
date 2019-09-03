package org.cloudbus.foggatewaylib.aneka;

import com.manjrasoft.aneka.ArrayOfProperty;
import com.manjrasoft.aneka.Property;
import com.manjrasoft.aneka.PropertyGroup;

/**
 * Class representing a storage bucket as defined in Aneka API.
 * This enables to easily create the required {@link PropertyGroup} to use in the
 * {@link com.manjrasoft.aneka.ApplicationSubmissionRequest#metadata}.
 *
 * Extend this class to add new storage bucket types. In particular, the methods to be
 * overridden are {@link #getPropertyCount()} and {@link #fillProperty(int, Property)}.
 *
 * When {@link #getArrayOfProperty()} is called, the array is filled using a for loop that calls
 * {@link #getProperty(int)} with increasing index from 0 up to {@link #getPropertyCount()}.
 *
 * @author Riccardo Mancini
 */
public abstract class StorageBucket {
    private String type;
    private String name;

    /**
     * Constructs a new storage bucket with given type and name (also referred as id).
     */
    public StorageBucket(String type, String name){
        this.type = type;
        this.name = name;
    }

    /**
     * Returns the number of properties. Override this method to update the number of properties.
     *
     * @see #getArrayOfProperty()
     */
    public int getPropertyCount(){
        return 1;
    }

    /**
     * Returns the property at the given index. The content of the property is filled by calling
     * {@link #fillProperty(int, Property)}.
     */
    public final Property getProperty(int index){
        Property property = new Property();
        fillProperty(index, property);
        return property;
    }

    /**
     * Fills the given {@code property} knowing its {@code index}.
     * When overriding this class, do not handle case 0 but just call this method in the default
     * case.
     *
     * E.g.
     * <code>
     * protected void fillProperty(int index, Property property){
     *     switch (index){
     *         case 1:
     *             property.setNameProperty("myName");
     *             property.setValue(myValue);
     *             break;
     *         case 2:
     *             ...
     *         default:
     *             super.fillProperty(index, property);
     *     }
     * }
     * </code>
     *
     * @see #getArrayOfProperty()
     */
    protected void fillProperty(int index, Property property){
        switch (index){
            case 0:
                property.setNameProperty("StorageType");
                property.setValue(type);
                break;
            default:

        }
    }

    /**
     * Creates an {@link ArrayOfProperty} calling in a loop {@link #getProperty(int)} with index
     * from 0 to {@link #getPropertyCount()}.
     */
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

    /**
     * Builds the {@link PropertyGroup} as required by Aneka API.
     * The {@code name} of the {@link PropertyGroup} is set to {@link #name}, while the
     * {@link ArrayOfProperty} is set with the result of {@link #getArrayOfProperty()}.
     */
    public PropertyGroup asPropertyGroup(){
        PropertyGroup propertyGroup = new PropertyGroup();
        propertyGroup.setProperties(getArrayOfProperty());
        propertyGroup.setNameProperty(name);
        return propertyGroup;
    }

    /**
     * Returns the type of this {@link StorageBucket}.
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the name (also referred as id) of this {@link StorageBucket}.
     */
    public String getName() {
        return name;
    }
}
