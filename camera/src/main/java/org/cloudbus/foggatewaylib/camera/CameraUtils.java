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

import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Collection of useful functions for the camera.
 *
 * @see <a href="https://developer.android.com/guide/topics/media/camera">https://developer.android.com/guide/topics/media/camera</a>
 *
 * @author Riccardo Mancini
 */
public class CameraUtils {

    /**
     * Check if this device has a camera.
     *
     * @return {@code true} if the device has a camera, {@code false} otherwise.
     * @see <a href="https://developer.android.com/guide/topics/media/camera">https://developer.android.com/guide/topics/media/camera</a>
     */
    public static boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * Gets an instance of the Camera object.
     *
     * @return the camera if {@link Camera#open()} was successful, {@link false} otherwise.
     * @see <a href="https://developer.android.com/guide/topics/media/camera">https://developer.android.com/guide/topics/media/camera</a>
     */
    @Nullable
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

    /**
     * Sets the orientation of the picture taken by the camera to the one of the display.
     * Without a call to this method, the picture could be shown rotated in portrait mode.
     *
     * @param activity the activity showing the preview.
     * @param cameraId the id of the camera .
     * @param camera the camera instance.
     */
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

    /**
     * Calculates the correct transformation matrix for the given exif orientation flag.
     *
     * @param orientation exif orientation flag.
     * @param width  image width.
     * @param height image height.
     * @return the matrix transformation to apply to the image.
     * @see <a href="https://stackoverflow.com/a/6010475">https://stackoverflow.com/a/6010475</a>
     */
    private static Matrix getExifTransformation(int orientation, int width, int height) {
        Matrix matrix = new Matrix();

        switch (orientation) {
            case ExifInterface.ORIENTATION_UNDEFINED:
            case ExifInterface.ORIENTATION_NORMAL:
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL: // Flip X
                matrix.postScale(-1, 1);
                matrix.postTranslate(-width, 0);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180: // PI rotation
                matrix.postTranslate(width, height);
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL: // Flip Y
                matrix.postScale(1, -1);
                matrix.postTranslate(0, -height);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE: // - PI/2 and Flip X
                matrix.postRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90: // -PI/2 and -width
                matrix.postTranslate(height, 0);
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE: // PI/2 and Flip
                matrix.postScale(-1, 1);
                matrix.postTranslate(-height, 0);
                matrix.postTranslate(0, width);
                matrix.postRotate(270);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270: // PI / 2
                matrix.postTranslate(0, width);
                matrix.postRotate(270);
                break;
            default:
                throw new IllegalArgumentException("Unrecognized orientation: " + orientation);
        }

        return matrix;
    }

    /**
     * Converts the image as a byte array to a {@link Bitmap}.
     *
     * @see BitmapFactory#decodeByteArray(byte[], int, int)
     */
    public static Bitmap byteArray2Bitmap(byte[] bytes){
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * Returns the Exif orientation flag in the image.
     *
     * @param bytes the image as a byte array.
     * @return the Exif orientation flag if found, {@link ExifInterface#ORIENTATION_NORMAL}
     * otherwise.
     */
    public static int getExifOrientation(byte[] bytes){
        InputStream inputStream = new ByteArrayInputStream(bytes);
        int orientation = ExifInterface.ORIENTATION_UNDEFINED;

        try {

            ExifInterface exif = new ExifInterface(inputStream);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                               ExifInterface.ORIENTATION_NORMAL);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return orientation;
    }

    /**
     * Rotates the image depending on the source orientation.
     *
     * @param sourceBitmap the source image.
     * @param orientation the Exif orientation flag.
     * @return the rotated image.
     */
    public static Bitmap getCorrectRotationBitmap(Bitmap sourceBitmap, int orientation){
        int w = sourceBitmap.getWidth();
        int h = sourceBitmap.getHeight();

        Log.d("DEBUG", "width = " + w + ", height = " + h + ", orientation = "
                + orientation);

        Matrix matrix = getExifTransformation(orientation, w, h);

        if (matrix.isIdentity())
            return sourceBitmap;
        else
            return Bitmap.createBitmap(sourceBitmap, 0, 0, w, h, matrix, true);
    }
}
