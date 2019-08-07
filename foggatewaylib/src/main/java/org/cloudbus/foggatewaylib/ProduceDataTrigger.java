package org.cloudbus.foggatewaylib;

public class ProduceDataTrigger<T extends Data> extends Trigger<T> {
    private String outputDataKey;

    public ProduceDataTrigger(String outputDataKey, Class<T> dataType) {
        super(dataType);
        this.outputDataKey = outputDataKey;
    }

    @Override
    public void onNewData(Store<T> store, T... data) {
        getExecutionManager().produceData(outputDataKey,
                data[0].getRequestID(),
                data);
    }
}
