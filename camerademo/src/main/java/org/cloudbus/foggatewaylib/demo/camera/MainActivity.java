package org.cloudbus.foggatewaylib.demo.camera;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;

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
import org.cloudbus.foggatewaylib.core.InMemoryStore;
import org.cloudbus.foggatewaylib.core.IndividualTrigger;
import org.cloudbus.foggatewaylib.core.ProduceDataTrigger;
import org.cloudbus.foggatewaylib.core.ProgressData;
import org.cloudbus.foggatewaylib.core.RoundRobinChooser;
import org.cloudbus.foggatewaylib.core.Store;
import org.cloudbus.foggatewaylib.service.FogGatewayServiceActivity;

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

    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.mainNavigationFragment).navigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(navView,
                Navigation.findNavController(this, R.id.mainNavigationFragment));


        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    @Override
    public void onCameraButtonClick() {
        long request_ID = ExecutionManager.nextRequestID();
        if (getExecutionManager() != null)
            getExecutionManager().runProvider(KEY_PROVIDER_INPUT, request_ID);
        Bundle args = new Bundle();
        args.putLong("request_id", request_ID);
        Navigation.findNavController(this, R.id.mainNavigationFragment)
                .navigate(R.id.resultFragment, args);
    }

    @Override
    public void onCancelButtonClick() {
        Navigation.findNavController(this, R.id.mainNavigationFragment)
                .navigate(R.id.previewFragment);
    }

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
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    protected void initExecutionManager(ExecutionManager executionManager) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean fogbusEnabled = prefs.getBoolean("enable_fogbus", false);
        boolean anekaEnabled = prefs.getBoolean("enable_aneka", false);

        if (fogbusEnabled){
            executionManager.addProvider(KEY_PROVIDER_FOGBUS, KEY_DATA_OUTPUT,
                    new EdgeLensProvider(4));
        } else{
            executionManager.removeProvider(KEY_PROVIDER_FOGBUS);
        }

        if (anekaEnabled){
            executionManager.addProvider(KEY_PROVIDER_ANEKA, KEY_DATA_OUTPUT,
                    new SimpleAnekaProvider());
        } else{
            executionManager.removeProvider(KEY_PROVIDER_ANEKA);
        }

        if (fogbusEnabled || anekaEnabled){
            executionManager
                    .addProvider(KEY_PROVIDER_INPUT, KEY_DATA_INPUT,
                            new CameraProvider())
                    .addProvider(KEY_PROVIDER_INPUT_BITMAP, KEY_DATA_INPUT_BITMAP,
                            new BitmapProvider())
                    .addProvider(KEY_PROVIDER_OUTPUT_BITMAP, KEY_DATA_OUTPUT_BITMAP,
                            new BitmapProvider())
                    .addTrigger(KEY_DATA_INPUT, KEY_TRIGGER_EXEC,
                            new ProduceDataTrigger<>(KEY_DATA_OUTPUT, ImageData.class))
                    .addTrigger(KEY_DATA_INPUT, KEY_TRIGGER_BITMAP_INPUT,
                            new ProduceDataTrigger<>(KEY_DATA_INPUT_BITMAP, ImageData.class))
                    .addTrigger(KEY_DATA_OUTPUT, KEY_TRIGGER_BITMAP_OUTPUT,
                            new ProduceDataTrigger<>(KEY_DATA_OUTPUT_BITMAP, ImageData.class))
                    .addTrigger(ExecutionManager.KEY_DATA_PROGRESS, KEY_TRIGGER_FALLBACK,
                            new IndividualTrigger<ProgressData>(ProgressData.class) {
                                @Override
                                public void onNewData(Store store, ProgressData data) {
                                    if (data.getProgress() < 0){
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
                    .addChooser(KEY_DATA_OUTPUT, new RoundRobinChooser());
        }
    }
}
