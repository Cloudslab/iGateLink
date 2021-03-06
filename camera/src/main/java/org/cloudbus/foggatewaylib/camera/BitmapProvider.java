package org.cloudbus.foggatewaylib.camera;

import android.content.res.Configuration;
import android.graphics.Bitmap;

import androidx.exifinterface.media.ExifInterface;

import org.cloudbus.foggatewaylib.core.GenericData;
import org.cloudbus.foggatewaylib.core.SequentialProvider;

import static org.cloudbus.foggatewaylib.camera.CameraUtils.byteArray2Bitmap;
import static org.cloudbus.foggatewaylib.camera.CameraUtils.getCorrectRotationBitmap;
import static org.cloudbus.foggatewaylib.camera.CameraUtils.getExifOrientation;

/**
 * Basic provider that converts an image (as a byte array) to a {@link Bitmap}.
 * In case the image has information about orientation (either in {@link ImageData#orientation} or
 * in its EXIF metadata, it will also be transformed accordingly.
 *
 * @see CameraUtils#byteArray2Bitmap(byte[])
 * @see CameraUtils#getCorrectRotationBitmap(Bitmap, int)
 *
 * @author Riccardo Mancini
 */
public class BitmapProvider extends SequentialProvider<ImageData, GenericData> {

    public BitmapProvider() {
        super(ImageData.class, GenericData.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public GenericData[] getData(ProgressPublisher progressPublisher, long requestID,
                                 ImageData... input) {
        GenericData<Bitmap>[] output = new GenericData[input.length];

        for (int i = 0; i < input.length; i++){
            ImageData imageData = input[i];
            Bitmap bitmap = byteArray2Bitmap(imageData.getBytes());
            if (imageData.getOrientation() == Configuration.ORIENTATION_UNDEFINED){
                bitmap = getCorrectRotationBitmap(bitmap, getExifOrientation(imageData.getBytes()));
            } else if (imageData.getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
                bitmap = getCorrectRotationBitmap(bitmap, ExifInterface.ORIENTATION_ROTATE_90);
            }
            output[i] = new GenericData<>(bitmap);
        }

        return output;
    }
}
