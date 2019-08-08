package org.cloudbus.foggatewaylib;

/**
 * Simple interface for a class that holds an {@link ExecutionManager} reference.
 *
 * @see ExecutionManager
 *
 * @author Riccardo Mancini
 */
//TODO consider moving it to ExecutionManager file.
public interface ExecutionManagerHolder {
    ExecutionManager getExecutionManager();
}
