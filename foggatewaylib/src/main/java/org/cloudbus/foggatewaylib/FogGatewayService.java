package org.cloudbus.foggatewaylib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class FogGatewayService extends ForegroundService implements ExecutionManagerHolder{
    public static final String TAG = "FogGatewayService";

    private ExecutionManager executionManager;

    public FogGatewayService(){
        super();
        executionManager = new ExecutionManager(this);
    }

    @Override
    protected boolean init(Bundle extras) {
        return true;
    }

    @Override
    protected int execute() {
        return 0;
    }

    @Override
    public ExecutionManager getExecutionManager() {
        return executionManager;
    }

    public static void start(Context context, Class<? extends Activity> cls){
        startForegroundService(context, FogGatewayService.class, cls);
    }

    public static void stop(Context context){
        stopForegroundService(context, FogGatewayService.class);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        executionManager.removeAllUITriggers();
        return false;
    }
}
