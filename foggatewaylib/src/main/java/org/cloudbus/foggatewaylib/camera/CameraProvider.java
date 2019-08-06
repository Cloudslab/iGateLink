package org.cloudbus.foggatewaylib.camera;

import android.hardware.Camera;
import android.util.Log;

import org.cloudbus.foggatewaylib.Data;
import org.cloudbus.foggatewaylib.DataProvider;
import org.cloudbus.foggatewaylib.ForegroundService;
import org.cloudbus.foggatewaylib.VoidData;

import java.util.HashMap;

import static org.cloudbus.foggatewaylib.camera.CameraUtils.getCameraInstance;

public class CameraProvider extends DataProvider<VoidData, ImageData> {
    public static final String TAG = "CameraProvider";

    private Camera mCamera;

    private HashMap<Long, Integer> orientations;

    public CameraProvider(){
        super(VoidData.class, ImageData.class);
        orientations = new HashMap<>();
    }

    public void takePicture(final long requestID){
        if (mCamera != null){
            Log.d(TAG, "Taking picture");
            publishProgress(requestID, 0, "Taking picture");
            Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {

                @Override
                public void onPictureTaken(byte[] bytes, android.hardware.Camera camera) {
                    publishResult(requestID, new ImageData(bytes, orientations.get(requestID)));
                    publishProgress(requestID, 0, "Picture taken");
                }
            };
            Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {

                @Override
                public void onShutter() {
                    ForegroundService service = getService();
                    int orientation;
                    if (service != null) {
                        orientation = getService().getResources().getConfiguration().orientation;
                    }
                    else {
                        orientation = -1;
                    }
                    orientations.put(requestID, orientation);
                }
            };
            mCamera.takePicture(mShutterCallback, null, mPictureCallback);
        } else {
            Log.e(TAG, "Camera is null");
        }
    }

    public Camera getCamera(){
        if (mCamera == null)
            mCamera = getCameraInstance();
        return mCamera;
    }

    @Override
    public void onAttach() {
        if (mCamera == null)
                mCamera = getCameraInstance();
    }

    @Override
    public void onDetach() {
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void execute(long requestID, Data... input) {
        takePicture(requestID);
    }
}
