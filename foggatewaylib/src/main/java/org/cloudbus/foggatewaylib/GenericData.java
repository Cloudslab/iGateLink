package org.cloudbus.foggatewaylib;

public class GenericData<T> extends Data {
    private T value;

    public GenericData(T value){
        super();
        this.value = value;
    }

    public GenericData(T value, long id){
        super(id);
        this.value = value;
    }

    public GenericData(T value, long id, long request_id){
        super(id, request_id);
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
