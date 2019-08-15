package org.cloudbus.foggatewaylib.demo.bluetooth;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import org.cloudbus.foggatewaylib.core.ExecutionManager;
import org.cloudbus.foggatewaylib.service.FogGatewayService;
import org.cloudbus.foggatewaylib.service.FogGatewayServiceActivity;
import org.cloudbus.foggatewaylib.service.ForegroundService;

public class SettingsFragment extends PreferenceFragmentCompat {
    public static final String KEY_ENABLE_SERVICES = "enable_services";
    public static final String KEY_BT_SCAN_TIMEOUT = "bluetooth_scan_timeout";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        if (getActivity() == null)
            return;

        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .edit()
                .putBoolean(KEY_ENABLE_SERVICES,
                        ((ExecutionManager.Holder) getActivity()).getExecutionManager() != null)
                .apply();

        setPreferencesFromResource(R.xml.preferences, rootKey);

        SwitchPreference preference = findPreference(KEY_ENABLE_SERVICES);
        if (preference != null){
            preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (getActivity() == null)
                        return false;

                    if ((boolean) newValue) {
                        FogGatewayService.start(getActivity(), MainActivity.class);
                        ForegroundService.bind(getActivity(),
                                (FogGatewayServiceActivity) getActivity(),
                                FogGatewayService.class);
                    } else {
                        FogGatewayService.stop(getActivity());
                    }
                    return true;
                }
            });
        }

        EditTextPreference bluetoothScanTimeout = findPreference(KEY_BT_SCAN_TIMEOUT);
        if (bluetoothScanTimeout != null){
            bluetoothScanTimeout.setOnBindEditTextListener(
                    new EditTextPreference.OnBindEditTextListener() {
                            @Override
                            public void onBindEditText(@NonNull EditText editText) {
                                editText.setInputType(InputType.TYPE_CLASS_NUMBER
                                        | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            }
            });
        }
    }
}
