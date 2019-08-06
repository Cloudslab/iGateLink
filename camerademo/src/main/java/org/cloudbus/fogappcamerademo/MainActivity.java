package org.cloudbus.fogappcamerademo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.cloudbus.foggatewaylib.FogGatewayActivity;
import org.cloudbus.foggatewaylib.FogGatewayService;
import org.cloudbus.foggatewaylib.GenericData;
import org.cloudbus.foggatewaylib.InMemoryDataStore;
import org.cloudbus.foggatewaylib.RunProviderTrigger;
import org.cloudbus.foggatewaylib.camera.BitmapProvider;
import org.cloudbus.foggatewaylib.camera.CameraProvider;
import org.cloudbus.foggatewaylib.camera.ImageData;

public class MainActivity extends FogGatewayActivity
        implements PreviewFragment.OnPreviewFragmentInteractionListener,
                   ResultFragment.OnResultFragmentInteractionListener {

    public static final String KEY_STORE_INPUT = "input";
    public static final String KEY_STORE_INPUT_BITMAP = "inputBitmap";
    public static final String KEY_STORE_OUTPUT = "output";
    public static final String KEY_STORE_OUTPUT_BITMAP = "outputBitmap";
    public static final String KEY_PROVIDER_INPUT = "inputProvider";
    public static final String KEY_PROVIDER_INPUT_BITMAP = "inputProviderBitmap";
    public static final String KEY_PROVIDER_OUTPUT = "outputProvider";
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
        long request_ID = -1;
        try {
            request_ID = FogGatewayService.nextRequestID();
            if (getService() != null)
                getService().runProvider("inputProvider", request_ID);
        } catch (FogGatewayService.ProviderNotDefinedException e) {
            e.printStackTrace();
        }
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
                                           String[] permissions, int[] grantResults) {
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
        service.addDataStore(KEY_STORE_INPUT,
                        new InMemoryDataStore<>(10, ImageData.class))
                .addDataStore(KEY_STORE_INPUT_BITMAP,
                        new InMemoryDataStore<>(10, GenericData.class))
                .addDataStore(KEY_STORE_OUTPUT,
                        new InMemoryDataStore<>(10, ImageData.class))
                .addDataStore(KEY_STORE_OUTPUT_BITMAP,
                        new InMemoryDataStore<>(10, GenericData.class))
                .addProvider(KEY_PROVIDER_INPUT, KEY_STORE_INPUT,
                        new CameraProvider())
                .addProvider(KEY_PROVIDER_OUTPUT, KEY_STORE_OUTPUT,
                        new EdgeLensProvider(4))
                .addProvider(KEY_PROVIDER_INPUT_BITMAP, KEY_STORE_INPUT_BITMAP,
                        new BitmapProvider(ImageData.class, GenericData.class))
                .addProvider(KEY_PROVIDER_OUTPUT_BITMAP, KEY_STORE_OUTPUT_BITMAP,
                        new BitmapProvider(ImageData.class, GenericData.class))
                .addTrigger(KEY_STORE_INPUT, KEY_TRIGGER_EXEC,
                        new RunProviderTrigger<>(KEY_PROVIDER_OUTPUT, ImageData.class))
                .addTrigger(KEY_STORE_INPUT, KEY_TRIGGER_BITMAP_INPUT,
                        new RunProviderTrigger<>(KEY_PROVIDER_INPUT_BITMAP, ImageData.class))
                .addTrigger(KEY_STORE_OUTPUT, KEY_TRIGGER_BITMAP_OUTPUT,
                        new RunProviderTrigger<>(KEY_PROVIDER_OUTPUT_BITMAP, ImageData.class));
    }
}
