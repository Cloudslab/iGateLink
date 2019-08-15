package org.cloudbus.foggatewaylib.demo.bluetooth;

import org.cloudbus.foggatewaylib.core.Chooser;
import org.cloudbus.foggatewaylib.core.RoundRobinChooser;

import java.util.ArrayList;
import java.util.List;

public class MyChooser extends Chooser {
    
    private RoundRobinChooser roundRobinChooser = new RoundRobinChooser();
    
    @Override
    public String chooseProvider(String... providers) {
        if (contains(providers, MainActivity.KEY_PROVIDER_LOCAL)){
            LocalProvider localProvider = (LocalProvider) getExecutionManager()
                    .getProvider(MainActivity.KEY_PROVIDER_LOCAL);
            if (providers.length == 1 || localProvider.isFree())
                return MainActivity.KEY_PROVIDER_LOCAL;
            else
                return roundRobinChooser.chooseProvider(remove(providers,
                        MainActivity.KEY_PROVIDER_LOCAL));
        } else
            return roundRobinChooser.chooseProvider(providers);
    }
    
    private boolean contains(String[] array, String element){
        for (String e:array){
            if (e.equals(element))
                return true;
        }
        return false;
    }
    
    private String[] remove(String[] array, String element){
        List<String> resList = new ArrayList<>();
        for (String e:array){
            if (!e.equals(element))
                resList.add(e);
        }
        return resList.toArray(new String[]{});
    }
}
