package org.cloudbus.foggatewaylib.camera;

import android.util.Base64;

import org.cloudbus.foggatewaylib.GenericData;
import org.cloudbus.foggatewaylib.SequentialProvider;

public class Base64Provider extends SequentialProvider<ImageData, GenericData> {

    public Base64Provider(Class<ImageData> inputType,
                          Class<GenericData> outputType) {
        super(inputType, outputType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public GenericData[] getData(ProgressPublisher progressListener, long requestID,
                                 ImageData... input) {
        GenericData[] output = new GenericData[input.length];

        for (int i = 0; i < input.length; i++){
            byte[] bytes = input[i].getBytes();
            output[i] = new GenericData<>(Base64.encodeToString(bytes, Base64.DEFAULT));
        }

        return output;
    }
}
