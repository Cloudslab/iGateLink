package org.cloudbus.foggatewaylib;

public class RunProviderTrigger<T extends Data> extends Trigger<T> {
    private String providerKey;

    public RunProviderTrigger(String providerKey, Class<T> dataType) {
        super(dataType);
        this.providerKey = providerKey;
    }

    @Override
    public void onNewData(Store<T> store, T... data) {
        getExecutionManager().runProvider(providerKey,
                data[0].getRequestID(),
                data);
    }
}
