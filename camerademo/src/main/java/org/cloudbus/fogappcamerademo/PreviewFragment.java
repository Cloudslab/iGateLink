package org.cloudbus.fogappcamerademo;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.cloudbus.foggatewaylib.camera.CameraInput;
import org.cloudbus.foggatewaylib.camera.CameraPreview;
import org.cloudbus.foggatewaylib.camera.CameraUtils;

import static org.cloudbus.foggatewaylib.camera.CameraUtils.checkCameraHardware;

public class PreviewFragment extends Fragment {
    private OnPreviewFragmentInteractionListener mListener;
    private FrameLayout preview;

    private Camera mCamera;
    private CameraPreview mPreview;

    public PreviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkCameraHardware(getContext())){

            // Create an instance of Camera
            mCamera = CameraInput.getCamera();

            if (mCamera == null)
                return;

            // Create our Preview view and set it as the content of our activity.
            mPreview = new CameraPreview(getContext(), mCamera);

            CameraUtils.setCameraDisplayOrientation(getActivity(), 0, mCamera);
        } else{
            Log.e("Camera", "Camera check failed");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_preview, container, false);
        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onButtonClick();
            }
        });
        preview = rootView.findViewById(R.id.camera_preview);
        if (mPreview != null && preview.getChildCount() == 0)
            preview.addView(mPreview);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Camera.Size cameraSize = mCamera.getParameters().getPreviewSize();
//        CameraPreview.getOptimalPreviewSize(mCamera.getParameters().getSupportedPreviewSizes(), width, height);

        double screenRatio = (double) width / height;
        double previewRatio = (double) cameraSize.width / cameraSize.height;

        boolean portrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        if (portrait) {
            previewRatio = 1/previewRatio;
        }

        Log.d("DEBUG", "Screen size is " + width + "x" + height + " ratio: " + screenRatio);
        Log.d("DEBUG", "Camera size is " + cameraSize.width + "x" + cameraSize.height+ " ratio: " + previewRatio);

        int targetWidth;
        int targetHeight;
        //the screen ratio should match the preview ratio
        if (screenRatio < previewRatio) {
            //then, the height must be increased,
            targetWidth = width;
            targetHeight = (int) (((double) width) / previewRatio);
        } else {
            //the width must be increased
            targetHeight = height;
            targetWidth = (int) (((double) height) * previewRatio);
        }
        Log.d("DEBUG", "Setting preview size to " + targetWidth + "x" + targetHeight +
                ", ratio=" + ((double) targetWidth / targetHeight));

        preview.setLayoutParams(new FrameLayout.LayoutParams(targetWidth, targetHeight));

        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPreviewFragmentInteractionListener) {
            mListener = (OnPreviewFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mCamera.stopPreview();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnPreviewFragmentInteractionListener {
        void onButtonClick();
    }
}
