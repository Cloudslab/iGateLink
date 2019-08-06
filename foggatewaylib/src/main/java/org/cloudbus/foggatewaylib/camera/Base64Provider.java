package org.cloudbus.foggatewaylib.camera;

import android.util.Base64;

import org.cloudbus.foggatewaylib.Data;
import org.cloudbus.foggatewaylib.GenericData;
import org.cloudbus.foggatewaylib.SequentialDataProvider;

public class Base64Provider extends SequentialDataProvider<ImageData, GenericData> {

    public Base64Provider(Class<ImageData> inputType,
                          Class<GenericData> outputType) {
        super(inputType, outputType);
    }

    @Override
    public GenericData[] getData(ProgressPublisher progressListener, long requestID,
                                         Data... input) throws Exception {
        GenericData<String>[] output = new GenericData[input.length];

        for (int i = 0; i < input.length; i++){
            byte[] bytes = ((ImageData)input[i]).getBytes();
            output[i] = new GenericData<>(Base64.encodeToString(bytes, Base64.DEFAULT));
        }

        return output;
    }
}
