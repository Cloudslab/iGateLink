package org.cloudbus.foggatewaylib.core;

import androidx.annotation.Nullable;

/**
 * Abstract implementation of a {@link Store.Observer} that is bound to an {@link ExecutionManager}.
 *
 * @param <T> the type of {@link Data} in the {@link Store} that this {@link Trigger} is
 *           observing.
 *
 * @author Riccardo Mancini
 */
public abstract class Trigger<T extends Data> implements Store.Observer<T> {

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
     * Attaches to the {@link ExecutionManager} and calls {@link #onAttach()}.
     *
     * @param executionManager the {@link ExecutionManager} that is being attached.
     */
    public void attach(ExecutionManager executionManager){
        this.executionManager = executionManager;
        onAttach();
    }

    /**
     * Detaches from the {@link ExecutionManager} attached to this {@link Trigger} after calling
     * {@link #onDetach()}.
     */
    public void detach(){
        onDetach();
        this.executionManager = null;
    }

    /**
     * @return the {@link ExecutionManager} bound to this {@link Trigger} or null.
     */
    @Nullable
    protected ExecutionManager getExecutionManager() {
        return executionManager;
    }

    /**
     * Performs actions after the {@link ExecutionManager} is bound.
     * It does nothing unless overridden.
     *
     * @see #attach(ExecutionManager)
     */
    public void onAttach(){}

    /**
     * Performs actions before the {@link ExecutionManager} is unbound.
     * It does nothing unless overridden.
     *
     * @see #detach()
     */
    public void onDetach(){}
}
