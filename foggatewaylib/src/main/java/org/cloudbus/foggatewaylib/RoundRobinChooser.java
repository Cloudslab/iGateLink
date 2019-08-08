package org.cloudbus.foggatewaylib;

/**
 * Simple {@link Chooser} implementation using the Round-Robin algorithm.
 * In other words, every time a choice has to be taken, the value following the previous choice
 * will be chosen.
 *
 * @author Riccardo Mancini
 */
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
