package org.cloudbus.fogappcamerademo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.cloudbus.foggatewaylib.DataStore;
import org.cloudbus.foggatewaylib.DataStoreObserver;
import org.cloudbus.foggatewaylib.camera.CameraInput;
import org.cloudbus.foggatewaylib.camera.ImageData;
import org.cloudbus.foggatewaylib.camera.ImageStore;

import java.util.List;

public class MainActivity extends AppCompatActivity implements PreviewFragment.OnPreviewFragmentInteractionListener,
                                                                ResultFragment.OnResultFragmentInteractionListener {
    public static final int STORE_INPUT = 1;
    public static final int STORE_OUTPUT = 2;

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;

    private Integer imageStoreObserverId = null;

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
        } else {
            // Permission has already been granted
        }

        if (imageStoreObserverId == null)
            ImageStore.getInstance(STORE_INPUT).addObserver(new DataStoreObserver<ImageData>() {
                @Override
                public void onDataStored(DataStore<ImageData> dataStore, List<ImageData> data) {
                    Log.d("DEBUG", "Image stored");
                    FogBusExecutor.executeStart(MainActivity.this);
                }
            });

    }

    @Override
    public void onButtonClick() {
        CameraInput.takePicture(this);
        Navigation.findNavController(this, R.id.mainNavigationFragment).navigate(R.id.resultFragment);
    }

    @Override
    public void onCancelButtonClick() {
        Navigation.findNavController(this, R.id.mainNavigationFragment).navigate(R.id.previewFragment);
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
    protected void onDestroy() {
        super.onDestroy();
        if (imageStoreObserverId != null){
            ImageStore.getInstance(STORE_INPUT).removeObserver(imageStoreObserverId);
            imageStoreObserverId = null;
        }
    }
}
