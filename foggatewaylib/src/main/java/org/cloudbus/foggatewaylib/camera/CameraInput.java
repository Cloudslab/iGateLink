package org.cloudbus.foggatewaylib.camera;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;

import org.cloudbus.foggatewaylib.InputHandler;
import org.cloudbus.foggatewaylib.Timer;

import static org.cloudbus.foggatewaylib.camera.CameraUtils.getCameraInstance;

public class CameraInput extends InputHandler<ImageData, ImageStore> {
    public static final String KEY_STORE_ID = "STORE_ID";
    public static final String KEY_PERIOD = "PERIOD";
    public static final String KEY_DELAY = "DELAY";
    public static final String ACTION_TAKE_PICTURE = "TAKE_PICTURE";

    private int storeId;
    private int period;
    private int delay;
    private static Camera mCamera;
    private Timer timer;

    private int orientation;

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] bytes, android.hardware.Camera camera) {
            inStore.store(new ImageData(bytes, orientation));
        }
    };

    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {

        @Override
        public void onShutter() {
            orientation = getResources().getConfiguration().orientation;
        }
    };

    @Override
    protected void init(Bundle extras) {
        storeId = extras.getInt(KEY_STORE_ID , 0);
        period = extras.getInt(KEY_PERIOD , 0);
        delay = extras.getInt(KEY_DELAY , 0);

        if (period > 0){
            timer = new Timer(new Runnable() {
                @Override
                public void run() {
                    __takePicture();
                }
            }, delay > 0 ? delay : 0, period);
        }

        super.init(extras);
    }

    @Override
    protected ImageStore initInStore() {
        return ImageStore.getInstance(storeId, 10);
    }

    @Override
    protected int execute() {
        if (mCamera == null)
            mCamera = getCameraInstance();

        if (timer != null){
            timer.start();
        }

        return KEEP_ALIVE;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (timer != null)
            timer.stop();
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }

    protected void __takePicture(){
        Log.d("DEBUG", "Taking picture");
        mCamera.takePicture(mShutterCallback, null, mPictureCallback);
    }

    @Override
    protected boolean customAction(String action) {
        if (!super.customAction(action)){
            switch (action){
                case ACTION_TAKE_PICTURE:
                    __takePicture();
                    return true;
                default:
                    return false;
            }
        } else{
            return true;
        }

    }

    public static void takePicture(Context context){
        sendIntentToForegroundService(context, ACTION_TAKE_PICTURE, null, CameraInput.class);
    }

    public static void start(Context context, int storeId, Class<? extends Activity> cls){
        Bundle extras = new Bundle();
        extras.putInt(KEY_STORE_ID, storeId);
        startForegroundService(context, extras, CameraInput.class, cls);
    }

    public static void startPeriodic(Context context, int storeId, int period, int delay, Class<? extends Activity> cls){
        Bundle extras = new Bundle();
        extras.putInt(KEY_STORE_ID, storeId);
        extras.putInt(KEY_PERIOD, period);
        extras.putInt(KEY_DELAY, delay);
        startForegroundService(context, extras, CameraInput.class, cls);
    }

    public static void startPeriodic(Context context, int storeId, int period, Class<? extends Activity> cls){
        startPeriodic(context, storeId, period, 0, cls);
    }

    public static void stop(Context context){
        stopForegroundService(context, CameraInput.class);
    }

    public static Camera getCamera(){
        if (mCamera == null)
            mCamera = getCameraInstance();
        return mCamera;
    }
}
