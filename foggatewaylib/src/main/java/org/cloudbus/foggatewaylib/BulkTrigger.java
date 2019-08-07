package org.cloudbus.foggatewaylib;

public abstract class BulkTrigger<T extends Data> implements StoreObserver<T> {
    private Class<T> dataType;
    private FogGatewayService service;

    public BulkTrigger(Class<T> dataType){
        this.dataType = dataType;
    }

    public Class<T> getDataType() {
        return dataType;
    }

    public void bindService(FogGatewayService service){
        this.service = service;
    }

    public void unbindService(){
        this.service = null;
    }

    public void onDataStored(Store<T> store, T... data){
        onNewData(store, data);
    }

    public abstract void onNewData(Store<T> store, T... data);

    protected FogGatewayService getService() {
        return service;
    }
}
