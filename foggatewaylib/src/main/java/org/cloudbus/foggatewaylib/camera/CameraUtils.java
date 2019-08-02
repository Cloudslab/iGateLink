package org.cloudbus.foggatewaylib.camera;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;

import androidx.exifinterface.media.ExifInterface;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CameraUtils {
    /** Check if this device has a camera */
    public static boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private static Matrix getExifTransformation(int orientation, int width, int height) {
        Matrix matrix = new Matrix();

        switch (orientation) {
            case 1:
                break;
            case 2: // Flip X
                matrix.postScale(-1, 1);
                matrix.postTranslate(-width, 0);
                break;
            case 3: // PI rotation
                matrix.postTranslate(width, height);
                matrix.postRotate(180);
                break;
            case 4: // Flip Y
                matrix.postScale(1, -1);
                matrix.postTranslate(0, -height);
                break;
            case 5: // - PI/2 and Flip X
                matrix.postRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case 6: // -PI/2 and -width
                matrix.postTranslate(height, 0);
                matrix.postRotate(90);
                break;
            case 7: // PI/2 and Flip
                matrix.postScale(-1, 1);
                matrix.postTranslate(-height, 0);
                matrix.postTranslate(0, width);
                matrix.postRotate(270);
                break;
            case 8: // PI / 2
                matrix.postTranslate(0, width);
                matrix.postRotate(270);
                break;
        }

        return matrix;
    }

    public static Bitmap byteArray2Bitmap(byte[] bytes){
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static int getExifOrientation(byte[] bytes){
        InputStream inputStream = new ByteArrayInputStream(bytes);
        int orientation = ExifInterface.ORIENTATION_NORMAL;

        try {

            ExifInterface exif = new ExifInterface(inputStream);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return orientation;
    }

    public static Bitmap getcorrectRotationBitmap(Bitmap sourceBitmap, int orientation){
        Log.d("DEBUG", "width = " + sourceBitmap.getWidth() + ", height = " + sourceBitmap.getHeight()  + ", orientation = " + orientation);

        if (orientation != ExifInterface.ORIENTATION_NORMAL)
            return Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(),
                    getExifTransformation(orientation, sourceBitmap.getWidth(), sourceBitmap.getHeight()),
                    true);
        else
            return sourceBitmap;
    }
}
