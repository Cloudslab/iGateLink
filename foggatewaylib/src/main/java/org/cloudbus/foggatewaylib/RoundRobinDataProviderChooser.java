package org.cloudbus.foggatewaylib;

public class RoundRobinDataProviderChooser extends DataProviderChooser {
    private int lastChoice;

    public RoundRobinDataProviderChooser(){
        super();
        lastChoice=0;
    }

    @Override
    public String chooseProvider(String... providers) {
        lastChoice = (lastChoice+1) % providers.length;
        return providers[lastChoice];
    }
}
