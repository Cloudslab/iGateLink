package org.cloudbus.foggatewaylib.camera;

import android.hardware.Camera;
import android.util.Log;

import org.cloudbus.foggatewaylib.core.ExecutionManager;
import org.cloudbus.foggatewaylib.core.Provider;
import org.cloudbus.foggatewaylib.core.VoidData;

import java.util.HashMap;

import static org.cloudbus.foggatewaylib.camera.CameraUtils.getCameraInstance;

/**
 * Basic provider for taking photos using CameraAPI v1.
 * Only one photo should be taken at a time for the same request (id).
 *
 * @see <a href="https://developer.android.com/guide/topics/media/camera">https://developer.android.com/guide/topics/media/camera</a>
 *
 * @author Riccardo Mancini
 */
public class CameraProvider extends Provider<VoidData, ImageData> {
    public static final String TAG = "CameraProvider";

    private Camera mCamera;

    /**
     * Maps the request id to the orientation of the phone at the moment the photo was taken.
     */
    private HashMap<Long, Integer> orientations;

    /**
     * Default constructor.
     */
    public CameraProvider(){
        super(VoidData.class, ImageData.class);
        orientations = new HashMap<>();
    }

    /**
     * Takes a picture (non-blocking).
     * It also saves the orientation of the phone when the photo was taken.
     * It publishes a progress of 0 as the start of the request.
     *
     * @param requestID the id of the request the taken photo belongs to.
     * @see Camera#takePicture(Camera.ShutterCallback, Camera.PictureCallback, Camera.PictureCallback)
     */
    private void takePicture(final long requestID){
        if (mCamera != null){
            Log.d(TAG, "Taking picture");
            publishProgress(requestID, 0, "Taking picture");
            Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {

                @Override
                public void onPictureTaken(byte[] bytes, android.hardware.Camera camera) {
                    publishResults(requestID, new ImageData(bytes, orientations.get(requestID)));
                    publishProgress(requestID, 0, "Picture taken");
                }
            };
            Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {

                @Override
                public void onShutter() {
                    ExecutionManager executionManager = getExecutionManager();
                    int orientation;
                    if (executionManager != null) {
                        orientation = executionManager.getContext().getResources()
                                .getConfiguration().orientation;
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

    /**
     * Get the camera instance (either a new one or an old one).
     *
     * @return the instance of the default camera of the device.
     * @see CameraUtils#getCameraInstance()
     */
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

    /**
     * When this provider is run, a picture is asynchronously taken.
     *
     * @see #takePicture(long)
     */
    @Override
    public void execute(long requestID, VoidData... input) {
        takePicture(requestID);
    }
}
