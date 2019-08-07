package org.cloudbus.foggatewaylib;

public abstract class BulkTrigger<T extends Data> implements StoreObserver<T> {
    private Class<T> dataType;
    private ExecutionManager executionManager;

    public BulkTrigger(Class<T> dataType){
        this.dataType = dataType;
    }

    public Class<T> getDataType() {
        return dataType;
    }

    public void bindExecutionManager(ExecutionManager executionManager){
        this.executionManager = executionManager;
    }

    public void unbindExecutionManager(){
        this.executionManager = null;
    }

    public void onDataStored(Store<T> store, T... data){
        onNewData(store, data);
    }

    public abstract void onNewData(Store<T> store, T... data);

    protected ExecutionManager getExecutionManager() {
        return executionManager;
    }
}
