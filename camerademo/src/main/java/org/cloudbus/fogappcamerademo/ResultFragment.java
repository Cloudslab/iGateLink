package org.cloudbus.fogappcamerademo;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.cloudbus.foggatewaylib.DataStore;
import org.cloudbus.foggatewaylib.DataStoreObserver;
import org.cloudbus.foggatewaylib.camera.ImageData;
import org.cloudbus.foggatewaylib.camera.ImageStore;

import java.util.List;


public class ResultFragment extends Fragment {
    private OnResultFragmentInteractionListener mListener;

    private ImageView imageView;
    private Integer inputObserverId;
    private Integer outputObserverId;

    private ImageData.BitmapReadyListener bitmapReadyListener = new ImageData.BitmapReadyListener() {
        @Override
        public void onBitmapReady(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    };

    public ResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
        ImageData latestImage = ImageStore.getInstance(MainActivity.STORE_INPUT).retrieveLast();
        if (latestImage != null){
            latestImage.prepareBitmap(bitmapReadyListener);
        }
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnResultFragmentInteractionListener) {
            mListener = (OnResultFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (inputObserverId == null)
            inputObserverId = ImageStore.getInstance(MainActivity.STORE_INPUT).addObserver(new DataStoreObserver<ImageData>() {
                @Override
                public void onDataStored(DataStore<ImageData> dataStore, List<ImageData> data) {
                    data.get(data.size()-1).prepareBitmap(bitmapReadyListener);
                }
            });

        if (outputObserverId == null)
            outputObserverId = ImageStore.getInstance(MainActivity.STORE_OUTPUT).addObserver(new DataStoreObserver<ImageData>() {
                @Override
                public void onDataStored(DataStore<ImageData> dataStore, List<ImageData> data) {
                    data.get(data.size()-1).prepareBitmap(bitmapReadyListener);
                }
            });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (inputObserverId != null){
            ImageStore.getInstance(MainActivity.STORE_INPUT).removeObserver(inputObserverId);
            inputObserverId = null;
        }
        if (outputObserverId != null){
            ImageStore.getInstance(MainActivity.STORE_OUTPUT).removeObserver(outputObserverId);
            outputObserverId = null;
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
