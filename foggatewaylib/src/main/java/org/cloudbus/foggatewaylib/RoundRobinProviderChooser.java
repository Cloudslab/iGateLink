package org.cloudbus.foggatewaylib;

public class RoundRobinProviderChooser extends ProviderChooser {
    private int lastChoice;

    public RoundRobinProviderChooser(){
        super();
        lastChoice=0;
    }

    @Override
    public String chooseProvider(String... providers) {
        lastChoice = (lastChoice+1) % providers.length;
        return providers[lastChoice];
    }
}
