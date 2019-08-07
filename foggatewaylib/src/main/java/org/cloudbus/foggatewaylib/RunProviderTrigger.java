package org.cloudbus.foggatewaylib;

public class RunProviderTrigger<T extends Data> extends BulkDataTrigger<T> {
    private String providerKey;

    public RunProviderTrigger(String providerKey, Class<T> dataType) {
        super(dataType);
        this.providerKey = providerKey;
    }

    @Override
    public void onNewData(DataStore<T> dataStore, T... data) {
        getService().runProvider(providerKey,
                data[0].getRequestID(),
                data);
    }
}
