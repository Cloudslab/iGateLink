package org.cloudbus.foggatewaylib;

public abstract class BulkDataTrigger<T extends Data> implements DataStoreObserver<T>{
    private Class<T> dataType;
    private FogGatewayService service;

    public BulkDataTrigger(Class<T> dataType){
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

    public void onDataStored(DataStore<T> dataStore, T... data){
        onNewData(dataStore, data);
    }

    public abstract void onNewData(DataStore<T> dataStore, T... data);

    protected FogGatewayService getService() {
        return service;
    }
}
