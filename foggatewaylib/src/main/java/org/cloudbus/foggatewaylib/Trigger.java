package org.cloudbus.foggatewaylib;

import androidx.annotation.Nullable;

/**
 * Abstract implementation of a {@link StoreObserver} that is bound to an {@link ExecutionManager}.
 *
 * @param <T> the type of {@link Data} in the {@link Store} that this {@link Trigger} is
 *           observing.
 *
 * @author Riccardo Mancini
 */
public abstract class Trigger<T extends Data> implements StoreObserver<T> {

    /**
     * Type of {@link Data} in the {@link Store} that this {@link Trigger} is observing.
     */
    private Class<T> dataType;

    /**
     * The {@link ExecutionManager} this {@link Trigger} is bound to.
     */
    private ExecutionManager executionManager;

    /**
     * @param dataType type of {@link Data} in the {@link Store} that this {@link Trigger} is
     *                 observing.
     */
    public Trigger(Class<T> dataType){
        this.dataType = dataType;
    }

    /**
     * @return type of {@link Data} in the {@link Store} that this {@link Trigger} is observing.
     */
    public Class<T> getDataType() {
        return dataType;
    }

    /**
     * Binds the {@link ExecutionManager} to this {@link Trigger}.
     *
     * @param executionManager the {@link ExecutionManager} that is being bound.
     */
    public void bindExecutionManager(ExecutionManager executionManager){
        this.executionManager = executionManager;
    }

    /**
     * Unbinds the {@link ExecutionManager} previously bound to this {@link Trigger}.
     */
    public void unbindExecutionManager(){
        this.executionManager = null;
    }

    /**
     * @return the {@link ExecutionManager} bound to this {@link Trigger} or null.
     */
    @Nullable
    protected ExecutionManager getExecutionManager() {
        return executionManager;
    }
}
