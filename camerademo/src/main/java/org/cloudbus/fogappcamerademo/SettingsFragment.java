package org.cloudbus.fogappcamerademo;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import org.cloudbus.foggatewaylib.camera.CameraInput;

import static org.cloudbus.fogappcamerademo.MainActivity.STORE_INPUT;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        SwitchPreference preference = findPreference("enable_services");
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ((boolean) newValue) {
                    CameraInput.start(getContext(), STORE_INPUT, MainActivity.class);
                    FogBusExecutor.start(getContext(), MainActivity.class);
                } else {
                    CameraInput.stop(getContext());
                    FogBusExecutor.stop(getContext());
                }
                return true;
            }
        });
    }
}
