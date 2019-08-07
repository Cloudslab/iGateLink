package org.cloudbus.foggatewaylib;

import android.util.Log;

public abstract class Chooser {
    public static final String TAG = "Chooser";

    private ExecutionManager executionManager;

    public Chooser(){ }

    public abstract String chooseProvider(String... providers);

    public void onAttach(){}
    public void onDetach(){}

    public void attach(ExecutionManager executionManager){
        this.executionManager = executionManager;
        if (executionManager == null){
            Log.e(TAG, "executionManager is null");
            return;
        }
        onAttach();
    }

    public void detach(){
        onDetach();
        this.executionManager = null;
    }

    protected ExecutionManager getExecutionManager(){
        if (this.executionManager == null)
            Log.e(TAG, "ExecutionManager is not bound");
        return this.executionManager;
    }
}
