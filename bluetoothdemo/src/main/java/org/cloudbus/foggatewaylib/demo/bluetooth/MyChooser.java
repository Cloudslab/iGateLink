package org.cloudbus.foggatewaylib.demo.bluetooth;

import android.preference.PreferenceManager;

import org.cloudbus.foggatewaylib.core.AndroidExecutionManager;
import org.cloudbus.foggatewaylib.core.Chooser;
import org.cloudbus.foggatewaylib.core.RoundRobinChooser;

import java.util.ArrayList;
import java.util.List;

/**
 * Round-robin Chooser that skips the {@link LocalProvider} if it is busy or disabled.
 *
 * @author Riccardo Mancini
 */
public class MyChooser extends Chooser {
    
    private RoundRobinChooser roundRobinChooser = new RoundRobinChooser();
    private boolean localExecutionEnabled = false;
    
    @Override
    public String chooseProvider(String... providers) {
        if (contains(providers, MainActivity.KEY_PROVIDER_LOCAL)){
            LocalProvider localProvider = (LocalProvider) getExecutionManager()
                    .getProvider(MainActivity.KEY_PROVIDER_LOCAL);
            if (localExecutionEnabled && (providers.length == 1 || localProvider.isFree()))
                return MainActivity.KEY_PROVIDER_LOCAL;
            else
                return roundRobinChooser.chooseProvider(remove(providers,
                        MainActivity.KEY_PROVIDER_LOCAL));
        } else
            return roundRobinChooser.chooseProvider(providers);
    }

    /**
     * Checks if an element is inside an array.
     */
    private boolean contains(String[] array, String element){
        for (String e:array){
            if (e.equals(element))
                return true;
        }
        return false;
    }

    /**
     * Returns an array that is the same as the input array without the given element.
     */
    private String[] remove(String[] array, String element){
        List<String> resList = new ArrayList<>();
        for (String e:array){
            if (!e.equals(element))
                resList.add(e);
        }
        return resList.toArray(new String[]{});
    }

    @Override
    public void onAttach() {
        super.onAttach();
        localExecutionEnabled = PreferenceManager
                .getDefaultSharedPreferences(
                        ((AndroidExecutionManager)getExecutionManager()).getContext())
                .getBoolean("enable_local_execution", false);
    }
}
