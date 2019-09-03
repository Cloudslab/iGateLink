package org.cloudbus.foggatewaylib.core;

/**
 * Generic class for any type of {@link Data}.
 *
 * @param <T> the type of data
 *
 * @author Riccardo Mancini
 */
public class GenericData<T> extends Data {
    private T value;

    /**
     * Constructor that sets the value.
     *
     * @param value the value to be set in this instance.
     * @see Data#Data()
     */
    public GenericData(T value){
        super();
        this.value = value;
    }

    /**
     * Constructor that sets the value and id.
     *
     * @param value the value to be set in this instance.
     * @see Data#Data(long)
     */
    public GenericData(T value, long id){
        super(id);
        this.value = value;
    }

    /**
     * Constructor that sets the value, id and request id.
     *
     * @param value the value to be set in this instance.
     * @see Data#Data(long, long)
     */
    public GenericData(T value, long id, long request_id){
        super(id, request_id);
        this.value = value;
    }

    /**
     * @return the value of this instance.
     */
    public T getValue() {
        return value;
    }

    /**
     * @param value value to be set in this instance.
     */
    public void setValue(T value) {
        this.value = value;
    }
}
