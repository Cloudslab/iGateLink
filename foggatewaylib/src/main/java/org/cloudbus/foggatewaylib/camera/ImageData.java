package org.cloudbus.foggatewaylib.camera;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import androidx.exifinterface.media.ExifInterface;

import org.cloudbus.foggatewaylib.Data;

import java.util.Date;

import static org.cloudbus.foggatewaylib.camera.CameraUtils.byteArray2Bitmap;
import static org.cloudbus.foggatewaylib.camera.CameraUtils.getExifOrientation;
import static org.cloudbus.foggatewaylib.camera.CameraUtils.getcorrectRotationBitmap;

public class ImageData extends Data {
    private byte[] bytes;
    private String base64;
    private Bitmap bitmap;
    private int orientation;


    public ImageData(Date timestamp, byte[] bytes, int orientation){
        super(timestamp);
        this.bytes = bytes;
        this.orientation = orientation;
    }


    public ImageData(byte[] bytes, int orientation){
        super();
        this.bytes = bytes;
        this.orientation = orientation;
        Log.d("DEBUG", "Orientation is " + orientation);
    }

    public ImageData(byte[] bytes){
        this(bytes, -1);
    }

    public byte[] getBytes() {
        return bytes;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getImageBase64(){
        if (base64 == null)
            base64 = Base64.encodeToString(bytes, Base64.DEFAULT);
        return base64;
    }

    public void prepareBitmap(final BitmapReadyListener listener) {
        if (this.bitmap == null){
            AsyncTask<ImageData, Void, Bitmap> asyncTask = new AsyncTask<ImageData, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(ImageData... imageData) {
                    Bitmap bitmap = byteArray2Bitmap(imageData[0].getBytes());
                    if (imageData[0].getOrientation() == Configuration.ORIENTATION_UNDEFINED){
                        return getcorrectRotationBitmap(bitmap, getExifOrientation(imageData[0].getBytes()));
                    } else if (imageData[0].getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
                        return getcorrectRotationBitmap(bitmap, ExifInterface.ORIENTATION_ROTATE_90);
                    } else {
                        return bitmap;
                    }
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    super.onPostExecute(bitmap);
                    ImageData.this.bitmap = bitmap;
                    listener.onBitmapReady(bitmap);
                }
            };
            asyncTask.execute(this);
        } else {
            listener.onBitmapReady(this.bitmap);
        }
    }

    public interface BitmapReadyListener{
        void onBitmapReady(Bitmap bitmap);
    }
}
