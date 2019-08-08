package org.cloudbus.fogappcamerademo;

import android.content.Context;
import android.content.DialogInterface;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.cloudbus.foggatewaylib.ExecutionManager;
import org.cloudbus.foggatewaylib.ExecutionManagerHolder;
import org.cloudbus.foggatewaylib.camera.CameraPreview;
import org.cloudbus.foggatewaylib.camera.CameraProvider;
import org.cloudbus.foggatewaylib.camera.CameraUtils;

import static org.cloudbus.foggatewaylib.camera.CameraUtils.checkCameraHardware;

public class PreviewFragment extends Fragment {
    private OnPreviewFragmentInteractionListener mListener;

    private Camera mCamera;
    private CameraPreview mPreview;

    private AlertDialog alertDialog;

    public PreviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() != null && checkCameraHardware(getActivity())){

            ExecutionManager executionManager
                    = ((ExecutionManagerHolder)getActivity()).getExecutionManager();
            if (executionManager == null)
                return;
            // Create an instance of Camera
            mCamera = ((CameraProvider)executionManager.getProvider("inputProvider"))
                    .getCamera();

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
        FrameLayout preview = rootView.findViewById(R.id.camera_preview);
        if (mPreview != null && preview.getChildCount() == 0){
            if (mPreview.getParent() != null)
                ((FrameLayout) mPreview.getParent()).removeView(mPreview);
            preview.addView(mPreview);
        }
        if (getActivity() == null)
            return rootView;

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        if (mCamera != null){
            Camera.Size cameraSize = mCamera.getParameters().getPreviewSize();
    //        CameraPreview.getOptimalPreviewSize(mCamera.getParameters().getSupportedPreviewSizes(), width, height);

            double screenRatio = (double) width / height;
            double previewRatio = (double) cameraSize.width / cameraSize.height;

            boolean portrait = getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_PORTRAIT;

            if (portrait) {
                previewRatio = 1/previewRatio;
            }

            Log.d("DEBUG", "Screen size is " + width + "x" + height + " ratio: "
                    + screenRatio);
            Log.d("DEBUG", "Camera size is " + cameraSize.width + "x"
                    + cameraSize.height+ " ratio: " + previewRatio);

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
            Log.d("DEBUG", "Setting preview size to " + targetWidth + "x"
                    + targetHeight + ", ratio=" + ((double) targetWidth / targetHeight));

            preview.setLayoutParams(new FrameLayout.LayoutParams(targetWidth, targetHeight));
        }

        return rootView;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnPreviewFragmentInteractionListener) {
            mListener = (OnPreviewFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null
                && ((ExecutionManagerHolder) getActivity()).getExecutionManager() == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            alertDialog = builder.setMessage("Service is not running")
                    .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Navigation.findNavController(getActivity(),
                                            R.id.mainNavigationFragment)
                                    .navigate(R.id.settingsFragment);
                        }
                    })
                    .setCancelable(false)
                    .create();
            alertDialog.show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (alertDialog != null)
            alertDialog.dismiss();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (mCamera != null)
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
