package org.cloudbus.foggatewaylib;

public class RoundRobinChooser extends Chooser {
    private int lastChoice;

    public RoundRobinChooser(){
        super();
        lastChoice=0;
    }

    @Override
    public String chooseProvider(String... providers) {
        lastChoice = (lastChoice+1) % providers.length;
        return providers[lastChoice];
    }
}
