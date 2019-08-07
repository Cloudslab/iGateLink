package org.cloudbus.foggatewaylib;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public abstract class FogGatewayServiceActivity extends AppCompatActivity
                                         implements ServiceConnection, ExecutionManagerHolder {

    public static final String TAG = "FGServiceActivity";

    private FogGatewayService fogGatewayService;
    private ExecutionManager executionManager;

    private Map<String, ServiceConnectionListener> listeners = new HashMap<>();

    public FogGatewayService getService() {
        if (fogGatewayService == null)
            Log.e(TAG, "Service is null!");
        return fogGatewayService;
    }

    @Override
    public ExecutionManager getExecutionManager() {
        return executionManager;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FogGatewayService.bind(this, this, FogGatewayService.class);
    }

    protected abstract void initExecutionManager(ExecutionManager executionManager);

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "Service connected");
        ForegroundService.ForegroundServiceBinder binder
                = (ForegroundService.ForegroundServiceBinder) service;
        this.fogGatewayService = (FogGatewayService) binder.getService();
        this.executionManager = this.fogGatewayService.getExecutionManager();
        initExecutionManager(executionManager);
        for(ServiceConnectionListener listener:listeners.values())
            listener.onServiceConnected(fogGatewayService);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "Service disconnected");
        this.fogGatewayService = null;
        for(ServiceConnectionListener listener:listeners.values())
            listener.onServiceDisconnected();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);
    }

    public void addServiceConnectionListener(String key, ServiceConnectionListener listener){
        listeners.put(key, listener);
    }

    public void removeServiceConnectionListener(String key){
        listeners.remove(key);
    }

    public interface ServiceConnectionListener{
        void onServiceConnected(FogGatewayService service);
        void onServiceDisconnected();
    }
}
