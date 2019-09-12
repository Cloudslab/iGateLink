package org.cloudbus.foggatewaylib.demo.camera;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.cloudbus.foggatewaylib.camera.BitmapProvider;
import org.cloudbus.foggatewaylib.camera.CameraProvider;
import org.cloudbus.foggatewaylib.camera.ImageData;
import org.cloudbus.foggatewaylib.core.ExecutionManager;
import org.cloudbus.foggatewaylib.core.GenericData;
import org.cloudbus.foggatewaylib.core.IndividualTrigger;
import org.cloudbus.foggatewaylib.core.ProduceDataTrigger;
import org.cloudbus.foggatewaylib.core.ProgressData;
import org.cloudbus.foggatewaylib.core.RoundRobinChooser;
import org.cloudbus.foggatewaylib.core.Store;
import org.cloudbus.foggatewaylib.service.FogGatewayServiceActivity;

/**
 * Main Activity.
 *
 * @author Riccardo Mancini
 */
public class MainActivity extends FogGatewayServiceActivity
        implements PreviewFragment.OnPreviewFragmentInteractionListener,
                   ResultFragment.OnResultFragmentInteractionListener {

    public static final String KEY_DATA_INPUT = "input";
    public static final String KEY_DATA_INPUT_BITMAP = "inputBitmap";
    public static final String KEY_DATA_OUTPUT = "output";
    public static final String KEY_DATA_OUTPUT_BITMAP = "outputBitmap";
    public static final String KEY_PROVIDER_INPUT = "inputProvider";
    public static final String KEY_PROVIDER_INPUT_BITMAP = "inputProviderBitmap";
    public static final String KEY_PROVIDER_FOGBUS = "fogbusProvider";
    public static final String KEY_PROVIDER_ANEKA = "anekaProvider";
    public static final String KEY_PROVIDER_OUTPUT_BITMAP = "outputProviderBitmap";
    public static final String KEY_TRIGGER_EXEC = "execTrigger";
    public static final String KEY_TRIGGER_BITMAP_OUTPUT = "bitmapOutputTrigger";
    public static final String KEY_TRIGGER_BITMAP_INPUT = "bitmapInputTrigger";
    public static final String KEY_TRIGGER_FALLBACK = "fallbackTrigger";

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;

    /**
     * Makes back button touch work as expected with the Navigation library.
     */
    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.mainNavigationFragment).navigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize the bottom navigation

        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(navView,
                Navigation.findNavController(this, R.id.mainNavigationFragment));

        // ask user for missing permissions (if any)

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);

        }
    }

    /**
     * Handles click on camera button from the {@link PreviewFragment}.
     *
     * @see PreviewFragment.OnPreviewFragmentInteractionListener#onCameraButtonClick()
     */
    @Override
    public void onCameraButtonClick() {
        if (getExecutionManager() == null)
            return;

        // get a new request id
        long request_ID = ExecutionManager.nextRequestID();

        // run the camera provider
        getExecutionManager().runProvider(KEY_PROVIDER_INPUT, request_ID);

        // start the result fragment, passing the request_id
        Bundle args = new Bundle();
        args.putLong("request_id", request_ID);
        Navigation.findNavController(this, R.id.mainNavigationFragment)
                .navigate(R.id.resultFragment, args);
    }

    /**
     * Handles click on cancel button from the {@link ResultFragment}.
     *
     * @see PreviewFragment.OnPreviewFragmentInteractionListener#onCameraButtonClick()
     */
    @Override
    public void onCancelButtonClick() {
        Navigation.findNavController(this, R.id.mainNavigationFragment)
                .navigate(R.id.previewFragment);
    }

    /**
     * Checks whether user granted the requested permissions.
     *
     * It basically does nothing but complaining to the user if they didn't grant permission.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length == 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permission for camera was not granted.",
                            Toast.LENGTH_LONG).show();
                    return;

                }
            }
        }
    }

    /**
     * Initializes the {@link ExecutionManager} when it is bound to the activity.
     */
    protected void initExecutionManager(ExecutionManager executionManager) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean fogbusEnabled = prefs.getBoolean("enable_fogbus", false);
        boolean anekaEnabled = prefs.getBoolean("enable_aneka", false);


        if (fogbusEnabled){
            executionManager.addProvider(KEY_PROVIDER_FOGBUS, KEY_DATA_OUTPUT,
                    new EdgeLensProvider(4));
        } else{
            // if disabled but still present, it should be removed
            executionManager.removeProvider(KEY_PROVIDER_FOGBUS);
        }

        if (anekaEnabled){
            executionManager.addProvider(KEY_PROVIDER_ANEKA, KEY_DATA_OUTPUT,
                    new SimpleAnekaProvider());
        } else{
            executionManager.removeProvider(KEY_PROVIDER_ANEKA);
        }
        // NB: they can be both active, in which case the chooser will kick in

        // If none of them is active, nothing should be done.
        if (fogbusEnabled || anekaEnabled){
            executionManager
                    // provider for input camera image
                    .addProvider(KEY_PROVIDER_INPUT, KEY_DATA_INPUT,
                            new CameraProvider())
                    // provider that converts input to bitmap
                    .addProvider(KEY_PROVIDER_INPUT_BITMAP, KEY_DATA_INPUT_BITMAP,
                            new BitmapProvider())
                    // provider that converts output to bitmap
                    .addProvider(KEY_PROVIDER_OUTPUT_BITMAP, KEY_DATA_OUTPUT_BITMAP,
                            new BitmapProvider())
                    // when input is ready, send it to output
                    .addTrigger(KEY_DATA_INPUT, KEY_TRIGGER_EXEC,
                            new ProduceDataTrigger<>(KEY_DATA_OUTPUT, ImageData.class))
                    // when input is ready, convert it to a bitmap to be shown
                    .addTrigger(KEY_DATA_INPUT, KEY_TRIGGER_BITMAP_INPUT,
                            new ProduceDataTrigger<>(KEY_DATA_INPUT_BITMAP, ImageData.class))
                    // when output is ready, convert it to a bitmap to be shown
                    .addTrigger(KEY_DATA_OUTPUT, KEY_TRIGGER_BITMAP_OUTPUT,
                            new ProduceDataTrigger<>(KEY_DATA_OUTPUT_BITMAP, ImageData.class))
                    // add a fallback trigger that detects an error and retries with another
                    // provider, if available
                    .addTrigger(ExecutionManager.KEY_DATA_PROGRESS, KEY_TRIGGER_FALLBACK,
                            new IndividualTrigger<ProgressData>(ProgressData.class) {
                                @Override
                                public void onNewData(Store store, ProgressData data) {
                                    if (data.getProgress() < 0 && getExecutionManager() != null){
                                        if (data.getPublisher().equals(KEY_PROVIDER_FOGBUS)
                                                || data.getPublisher().equals(KEY_PROVIDER_ANEKA)){
                                            getExecutionManager().produceDataExcludeProviders(
                                                    KEY_DATA_OUTPUT,
                                                    data.getRequestID(),
                                                    new String[]{data.getPublisher()},
                                                    getExecutionManager().getStore(KEY_DATA_INPUT)
                                                            .retrieveLast(data.getRequestID())
                                            );

                                        }
                                    }
                                }
                            })
                    // add a simple round robin chooser
                    .addChooser(KEY_DATA_OUTPUT, new RoundRobinChooser());
        }
    }
}
