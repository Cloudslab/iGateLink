package org.cloudbus.foggatewaylib.camera;

import android.content.res.Configuration;
import android.graphics.Bitmap;

import androidx.exifinterface.media.ExifInterface;

import org.cloudbus.foggatewaylib.GenericData;
import org.cloudbus.foggatewaylib.SequentialProvider;

import static org.cloudbus.foggatewaylib.camera.CameraUtils.byteArray2Bitmap;
import static org.cloudbus.foggatewaylib.camera.CameraUtils.getExifOrientation;
import static org.cloudbus.foggatewaylib.camera.CameraUtils.getcorrectRotationBitmap;

public class BitmapProvider extends SequentialProvider<ImageData, GenericData> {

    public BitmapProvider(Class<ImageData> inputType,
                          Class<GenericData> outputType) {
        super(inputType, outputType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public GenericData[] getData(ProgressPublisher progressListener, long requestID,
                                 ImageData... input) {
        GenericData<Bitmap>[] output = new GenericData[input.length];

        for (int i = 0; i < input.length; i++){
            ImageData imageData = input[i];
            Bitmap bitmap = byteArray2Bitmap(imageData.getBytes());
            if (imageData.getOrientation() == Configuration.ORIENTATION_UNDEFINED){
                bitmap = getcorrectRotationBitmap(bitmap, getExifOrientation(imageData.getBytes()));
            } else if (imageData.getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
                bitmap = getcorrectRotationBitmap(bitmap, ExifInterface.ORIENTATION_ROTATE_90);
            }
            output[i] = new GenericData<>(bitmap);
        }

        return output;
    }
}
