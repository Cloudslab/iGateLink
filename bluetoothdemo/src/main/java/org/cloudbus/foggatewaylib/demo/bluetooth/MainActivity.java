package org.cloudbus.foggatewaylib.demo.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothGatt;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.cloudbus.foggatewaylib.bluetooth.BluetoothLeHandler;
import org.cloudbus.foggatewaylib.bluetooth.ui.PairBluetoothLeFragment;
import org.cloudbus.foggatewaylib.core.ExecutionManager;
import org.cloudbus.foggatewaylib.service.FogGatewayServiceActivity;

import java.util.HashSet;
import java.util.Set;

/**
 * Main Activity.
 *
 * @author Riccardo Mancini
 */
public class MainActivity extends FogGatewayServiceActivity
        implements PairBluetoothLeFragment.OnPairDevice {

    public static final String KEY_DATA_INPUT = "input";
    public static final String KEY_DATA_OUTPUT = "output";
    public static final String KEY_PROVIDER_INPUT = "inputProvider";
    public static final String KEY_PROVIDER_OUTPUT = "outputProvider";
    public static final String KEY_PROVIDER_LOCAL = "localProvider";

    public static final String[] PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
    };

    private static final int MY_REQUEST_CODE = 1;

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

        Set<String> missingPermissions = new HashSet<>();

        for (String permission:PERMISSIONS) {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }

        if (!missingPermissions.isEmpty())
            ActivityCompat.requestPermissions(this,
                    missingPermissions.toArray(new String[]{}),
                    MY_REQUEST_CODE);


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
            case MY_REQUEST_CODE: {
                for (int i = 0; i < permissions.length; i++){
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permission for " + permissions[i]
                                + " was not granted.", Toast.LENGTH_LONG).show();
                    }
                }
                return;
            }
        }
    }

    /**
     * Initializes the {@link ExecutionManager} when it is bound to the activity.
     */
    protected void initExecutionManager(ExecutionManager executionManager) {
        executionManager
                // add the input provider
                .addProvider(KEY_PROVIDER_INPUT, KEY_DATA_INPUT,
                        new BluetoothOximeterProvider())
                // add the output provider
                .addProvider(KEY_PROVIDER_OUTPUT, KEY_DATA_OUTPUT,
                        new HealthKeeperProvider())
                // add the local output provider
                .addProvider(KEY_PROVIDER_LOCAL, KEY_DATA_OUTPUT,
                        new LocalProvider())
                // add the chooser
                .addChooser(KEY_DATA_OUTPUT, new MyChooser());
        // note that no store was added since it is implicit.
        // furthermore, no trigger is necessary since analysis is started by user touch on a
        // button.
    }

    @Override
    public void onDevicePaired(BluetoothGatt gatt) {
        // a device has been paired
        // in this simple demo there's nothing that's needed to be done.
    }

    /**
     * Returns the requirements that the bluetooth devices must satisfy in order to be
     * managed by this application.
     *
     * @see PairBluetoothLeFragment.OnPairDevice
     */
    @Override
    @SuppressWarnings("unchecked")
    public Set<BluetoothLeHandler.ServiceCharacteristicPair>[] getRequirements() {
        BluetoothLeHandler.ServiceCharacteristicPair requirement
                = new BluetoothLeHandler.ServiceCharacteristicPair(
                        BluetoothOximeterProvider.SERVICE_UUID,
                        BluetoothOximeterProvider.CHARACTERISTIC_UUID);
        Set<BluetoothLeHandler.ServiceCharacteristicPair> set = new HashSet<>();
        set.add(requirement);

        return new Set[]{set};
    }
}
