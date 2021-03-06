package org.cloudbus.foggatewaylib.camera;

import android.util.Base64;

import org.cloudbus.foggatewaylib.core.GenericData;
import org.cloudbus.foggatewaylib.core.SequentialProvider;

/**
 * Basic provider that converts an image (as a byte array) to a Base64 String.
 *
 * @see Base64#encodeToString(byte[], int)
 *
 * @author Riccardo Mancini
 */
public class Base64Provider extends SequentialProvider<ImageData, GenericData> {

    public Base64Provider(Class<ImageData> inputType,
                          Class<GenericData> outputType) {
        super(inputType, outputType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public GenericData[] getData(ProgressPublisher progressPublisher, long requestID,
                                 ImageData... input) {
        GenericData[] output = new GenericData[input.length];

        for (int i = 0; i < input.length; i++){
            byte[] bytes = input[i].getBytes();
            output[i] = new GenericData<>(Base64.encodeToString(bytes, Base64.DEFAULT));
        }

        return output;
    }
}
