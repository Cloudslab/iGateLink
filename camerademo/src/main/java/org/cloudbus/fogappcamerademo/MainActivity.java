package org.cloudbus.fogappcamerademo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.cloudbus.foggatewaylib.FogGatewayActivity;
import org.cloudbus.foggatewaylib.FogGatewayService;
import org.cloudbus.foggatewaylib.GenericData;
import org.cloudbus.foggatewaylib.InMemoryDataStore;
import org.cloudbus.foggatewaylib.ProduceDataTrigger;
import org.cloudbus.foggatewaylib.RoundRobinDataProviderChooser;
import org.cloudbus.foggatewaylib.camera.BitmapProvider;
import org.cloudbus.foggatewaylib.camera.CameraProvider;
import org.cloudbus.foggatewaylib.camera.ImageData;

public class MainActivity extends FogGatewayActivity
        implements PreviewFragment.OnPreviewFragmentInteractionListener,
                   ResultFragment.OnResultFragmentInteractionListener {

    public static final String KEY_DATA_INPUT = "input";
    public static final String KEY_DATA_INPUT_BITMAP = "inputBitmap";
    public static final String KEY_DATA_OUTPUT = "output";
    public static final String KEY_DATA_OUTPUT_BITMAP = "outputBitmap";
    public static final String KEY_PROVIDER_INPUT = "inputProvider";
    public static final String KEY_PROVIDER_INPUT_BITMAP = "inputProviderBitmap";
    public static final String KEY_PROVIDER_OUTPUT = "outputProvider";
    public static final String KEY_PROVIDER_DUMMY = "dummyProvider";
    public static final String KEY_PROVIDER_OUTPUT_BITMAP = "outputProviderBitmap";
    public static final String KEY_TRIGGER_EXEC = "execTrigger";
    public static final String KEY_TRIGGER_BITMAP_OUTPUT = "bitmapOutputTrigger";
    public static final String KEY_TRIGGER_BITMAP_INPUT = "bitmapInputTrigger";

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
    public void onButtonClick() {
        long request_ID = FogGatewayService.nextRequestID();
        if (getService() != null)
            getService().runProvider(KEY_PROVIDER_INPUT, request_ID);
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
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // camera-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    protected void initService(FogGatewayService service) {
        service.addDataStore(KEY_DATA_INPUT,
                        new InMemoryDataStore<>(10, ImageData.class))
                .addDataStore(KEY_DATA_INPUT_BITMAP,
                        new InMemoryDataStore<>(10, GenericData.class))
                .addDataStore(KEY_DATA_OUTPUT,
                        new InMemoryDataStore<>(10, ImageData.class))
                .addDataStore(KEY_DATA_OUTPUT_BITMAP,
                        new InMemoryDataStore<>(10, GenericData.class))
                .addProvider(KEY_PROVIDER_INPUT, KEY_DATA_INPUT,
                        new CameraProvider())
                .addChooser(KEY_DATA_OUTPUT, new RoundRobinDataProviderChooser())
                .addProvider(KEY_PROVIDER_OUTPUT, KEY_DATA_OUTPUT,
                        new EdgeLensProvider(4))
                .addProvider(KEY_PROVIDER_DUMMY, KEY_DATA_OUTPUT,
                        new DummyProvider<>(ImageData.class))
                .addProvider(KEY_PROVIDER_INPUT_BITMAP, KEY_DATA_INPUT_BITMAP,
                        new BitmapProvider(ImageData.class, GenericData.class))
                .addProvider(KEY_PROVIDER_OUTPUT_BITMAP, KEY_DATA_OUTPUT_BITMAP,
                        new BitmapProvider(ImageData.class, GenericData.class))
                .addTrigger(KEY_DATA_INPUT, KEY_TRIGGER_EXEC,
                        new ProduceDataTrigger<>(KEY_DATA_OUTPUT, ImageData.class))
                .addTrigger(KEY_DATA_INPUT, KEY_TRIGGER_BITMAP_INPUT,
                        new ProduceDataTrigger<>(KEY_DATA_INPUT_BITMAP, ImageData.class))
                .addTrigger(KEY_DATA_OUTPUT, KEY_TRIGGER_BITMAP_OUTPUT,
                        new ProduceDataTrigger<>(KEY_DATA_OUTPUT_BITMAP, ImageData.class));
    }
}
