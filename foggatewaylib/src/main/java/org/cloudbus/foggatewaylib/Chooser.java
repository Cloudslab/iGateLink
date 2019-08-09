package org.cloudbus.foggatewaylib;

import android.util.Log;

/**
 * This class performs a choice between different {@link Provider}s that provide the same output.
 *
 * @see Provider
 * @see ExecutionManager
 *
 * @author Riccardo Mancini
 */
public abstract class Chooser {
    public static final String TAG = "Chooser";

    /**
     * The {@link ExecutionManager} this {@link Chooser} is bound to.
     *
     * @see #attach(ExecutionManager)
     * @see #detach()
     */
    private ExecutionManager executionManager;

    /**
     * Default constructor. It does noting.
     */
    public Chooser(){ }

    /**
     * Performs a choice among the given {@link Provider}s.
     *
     * @param providers the provider pool to choose from.
     */
    //TODO consider passing Providers directly
    public abstract String chooseProvider(String... providers);

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

    /**
     * Detaches this {@link Chooser} from the {@link ExecutionManager}.
     */
    public void attach(ExecutionManager executionManager){
        this.executionManager = executionManager;
        if (executionManager == null){
            Log.e(TAG, "executionManager is null");
            return;
        }
        onAttach();
    }

    /**
     * Detaches this {@link Chooser} from the {@link ExecutionManager}.
     */
    public void detach(){
        onDetach();
        this.executionManager = null;
    }

    /**
     * @return the {@link ExecutionManager} this provider is bound to, or null.
     */
    protected ExecutionManager getExecutionManager(){
        if (this.executionManager == null)
            Log.e(TAG, "ExecutionManager is not bound");
        return this.executionManager;
    }
}
