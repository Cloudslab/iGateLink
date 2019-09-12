package org.cloudbus.foggatewaylib.demo.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.cloudbus.foggatewaylib.core.ExecutionManager;
import org.cloudbus.foggatewaylib.core.GenericData;
import org.cloudbus.foggatewaylib.core.IndividualTrigger;
import org.cloudbus.foggatewaylib.core.ProgressData;
import org.cloudbus.foggatewaylib.core.Store;
import org.cloudbus.foggatewaylib.service.FogGatewayService;
import org.cloudbus.foggatewaylib.service.FogGatewayServiceActivity;

import static org.cloudbus.foggatewaylib.demo.camera.MainActivity.KEY_DATA_INPUT_BITMAP;
import static org.cloudbus.foggatewaylib.demo.camera.MainActivity.KEY_DATA_OUTPUT_BITMAP;


public class ResultFragment extends Fragment {
    public static final String TAG = "Result Fragment";
    private OnResultFragmentInteractionListener mListener;

    private ImageView imageView;
    private TextView progressText;
    private ContentLoadingProgressBar progressBar;

    private long request_id;

    public ResultFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_result, container, false);
        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onCancelButtonClick();
            }
        });
        imageView = rootView.findViewById(R.id.image_view);
        progressBar = rootView.findViewById(R.id.progress_bar);
        progressText = rootView.findViewById(R.id.progress_text);

        progressBar.show();
        progressBar.setIndeterminate(true);

        return rootView;
    }


    @Override
    public void onAttach(@NonNull final Context context) {
        super.onAttach(context);
        if (context instanceof OnResultFragmentInteractionListener) {
            mListener = (OnResultFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getArguments() != null)
            request_id = getArguments().getLong("request_id", -1);
        else
            request_id = -1;
        if (getActivity() == null)
            return;

        ExecutionManager executionManager
                = ((ExecutionManager.Holder)getActivity()).getExecutionManager();
        if (executionManager != null){
            initExecutionManager(executionManager);
        } else{
            ((FogGatewayServiceActivity)getActivity())
                    .addServiceConnectionListener("resultFragment",
                    new FogGatewayServiceActivity.ServiceConnectionListener() {
                        @Override
                        public void onServiceConnected(FogGatewayService service) {
                            initExecutionManager(service.getExecutionManager());
                        }

                        @Override
                        public void onServiceDisconnected() { }
                    });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() == null)
            return;

        ExecutionManager executionManager
                = ((ExecutionManager.Holder)getActivity()).getExecutionManager();
        if (executionManager != null){
            executionManager.removeUITrigger("inputUpdateUI");
            executionManager.removeUITrigger("outputUpdateUI");
            executionManager.removeUITrigger("messageUpdateUI");
        }
        ((FogGatewayServiceActivity)getActivity())
                .removeServiceConnectionListener("resultFragment");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @SuppressWarnings("unchecked")
    private void initExecutionManager(ExecutionManager executionManager){
        ProgressData lastMsg = (ProgressData) executionManager
                .getStore(ExecutionManager.KEY_DATA_PROGRESS)
                .retrieveLast(request_id);

        if (lastMsg != null)
            updateUIOnProgress(lastMsg);

        if (imageView != null){
            GenericData<Bitmap> outputBitmap = (GenericData<Bitmap>) executionManager
                    .getStore(KEY_DATA_OUTPUT_BITMAP)
                    .retrieveLast(request_id);
            if (outputBitmap != null)
                imageView.setImageBitmap(outputBitmap.getValue());
            else{
                GenericData<Bitmap> inputBitmap = (GenericData<Bitmap>) executionManager
                        .getStore(KEY_DATA_INPUT_BITMAP)
                        .retrieveLast(request_id);
                if (inputBitmap != null)
                    imageView.setImageBitmap(inputBitmap.getValue());
            }
        }

        executionManager.addUITrigger(KEY_DATA_INPUT_BITMAP,
                "inputUpdateUI",
                request_id,
                new IndividualTrigger<GenericData>(GenericData.class) {
                    @Override
                    public void onNewData(Store store, GenericData data) {
                        if (imageView != null)
                            imageView.setImageBitmap(((GenericData<Bitmap>)data)
                                    .getValue());
                    }
                })
            .addUITrigger(KEY_DATA_OUTPUT_BITMAP,
                "outputUpdateUI",
                request_id,
                new IndividualTrigger<GenericData>(GenericData.class) {
                    @Override
                    public void onNewData(Store store, GenericData data) {
                        if (imageView != null)
                            imageView.setImageBitmap(((GenericData<Bitmap>)data)
                                    .getValue());
                    }
                })
            .addUITrigger(ExecutionManager.KEY_DATA_PROGRESS,
                "messageUpdateUI",
                request_id,
                new IndividualTrigger<ProgressData>(ProgressData.class) {

                    @Override
                    public void onNewData(Store<ProgressData> store,
                                          ProgressData data) {
                        updateUIOnProgress(data);
                    }
                });
    }

    private void updateUIOnProgress(ProgressData data){
        if (progressText == null || progressBar == null)
            return;

        switch(data.getProgress()){
            case -1:
                progressBar.hide();
                progressText.setText(data.getMessage());
                break;
            case 0:
                progressBar.show();
                progressText.setText(data.getMessage());
                break;
            case 100:
                progressBar.hide();
                progressText.setText(data.getMessage());
                break;
            default:
                progressBar.show();
                progressText.setText("Unknown code: "
                        + data.getProgress()
                        + ", message: "
                        + data.getMessage());
        }
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
    public interface OnResultFragmentInteractionListener {
        void onCancelButtonClick();
    }
}
