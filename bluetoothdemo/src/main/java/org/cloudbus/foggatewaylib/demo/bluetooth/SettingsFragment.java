package org.cloudbus.foggatewaylib.demo.bluetooth;

import android.content.Intent;
import android.net.Uri;
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
    public static final String KEY_CONFIGURE_MASTER = "configure_master";
    public static final String KEY_MASTER_DOMAIN = "fogbus_master_ip";

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
                        FogGatewayService.start(getActivity());
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

        Preference configureMaster = findPreference(KEY_CONFIGURE_MASTER);
        if (configureMaster != null){
            configureMaster.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (getContext() == null)
                        return false;

                    String masterIP = PreferenceManager.getDefaultSharedPreferences(getContext())
                            .getString(KEY_MASTER_DOMAIN, "");
                    String url = String.format("http://%s/HealthKeeper/RPi/Master/", masterIP);
                    Uri webPage = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
                    if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                        startActivity(intent);
                        return true;
                    } else{
                        return false;
                    }
                }
            });
        }
    }
}
