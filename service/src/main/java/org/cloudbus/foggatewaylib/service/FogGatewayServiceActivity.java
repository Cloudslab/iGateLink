package org.cloudbus.foggatewaylib.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.cloudbus.foggatewaylib.core.ExecutionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Companion activity for the {@link FogGatewayService}.
 * The user just needs to define how to initialize the {@link ExecutionManager} in the
 * method {@link #initExecutionManager(ExecutionManager)} and to start and bind to it.
 * This class will take care of everything else related to the binding and unbinding.
 *
 * @author Riccardo Mancini
 */
public abstract class FogGatewayServiceActivity extends AppCompatActivity
                                         implements ServiceConnection, ExecutionManager.Holder {

    public static final String TAG = "FGServiceActivity";

    /**
     * The {@link FogGatewayService} this activity is bound to.
     */
    private FogGatewayService fogGatewayService;

    /**
     * A map of custom ServiceConnectionListener that will be called after the service is bound.
     *
     * @see #addServiceConnectionListener(String, ServiceConnectionListener)
     * @see #removeServiceConnectionListener(String)
     */
    private Map<String, ServiceConnectionListener> listeners = new HashMap<>();

    /**
     * @return the {@link FogGatewayService} bound to this activity.
     */
    @Nullable
    public FogGatewayService getService() {
        if (fogGatewayService == null)
            Log.e(TAG, "Service is null!");
        return fogGatewayService;
    }

    /**
     * @return the {@link ExecutionManager} in the {@link FogGatewayService} bound to this
     *         activity or {@code null} if no service is bound.
     * @see FogGatewayService#getExecutionManager()
     */
    @Override
    public ExecutionManager getExecutionManager() {
        if (fogGatewayService != null)
            return fogGatewayService.getExecutionManager();
        else
            return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // bind as soon as the service comes alive.
        FogGatewayService.bind(this, this, FogGatewayService.class);
    }

    /**
     * Performs initialization of the {@link ExecutionManager}.
     *
     * @param executionManager the {@link ExecutionManager} of the {@link FogGatewayService}
     *                         this activity is just been bound to.
     */
    protected abstract void initExecutionManager(ExecutionManager executionManager);

    /**
     * When the {@link FogGatewayService} is bound to the activity, initializes its
     * {@link ExecutionManager} and calls the callbacks
     * {@link ServiceConnectionListener#onServiceConnected(FogGatewayService)}
     * in {@link #listeners}.
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "Service connected");
        ForegroundService.ForegroundServiceBinder binder
                = (ForegroundService.ForegroundServiceBinder) service;
        this.fogGatewayService = (FogGatewayService) binder.getService();
        initExecutionManager(fogGatewayService.getExecutionManager());
        for(ServiceConnectionListener listener:listeners.values())
            listener.onServiceConnected(fogGatewayService);
    }

    /**
     * When the {@link FogGatewayService} is unbound from the activity, calls the callbacks
     * {@link ServiceConnectionListener#onServiceConnected(FogGatewayService)} in
     * {@link #listeners}.
     */
    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "Service disconnected");
        this.fogGatewayService = null;
        for(ServiceConnectionListener listener:listeners.values())
            listener.onServiceDisconnected();
    }

    /**
     * When activity is destroyed, it unbinds from the {@link FogGatewayService}.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);
    }

    /**
     * Adds a new {@link ServiceConnectionListener} identified by the given key.
     *
     * @param key the key of the {@link ServiceConnectionListener} for later retrieval.
     * @param listener the {@link ServiceConnectionListener} to be added.
     * @see #removeServiceConnectionListener(String)
     */
    public void addServiceConnectionListener(String key, ServiceConnectionListener listener){
        listeners.put(key, listener);
    }

    /**
     * Removes the {@link ServiceConnectionListener} identified by the given key.
     *
     * @param key the key of the {@link ServiceConnectionListener}
     * @see #addServiceConnectionListener(String, ServiceConnectionListener)
     */
    public void removeServiceConnectionListener(String key){
        listeners.remove(key);
    }

    /**
     * Simple interface for callbacks at service connection and diconnectio events.
     */
    public interface ServiceConnectionListener{

        /**
         * Called right after the service has connected to the activity.
         *
         * @param service the service that has just connected
         */
        void onServiceConnected(FogGatewayService service);

        /**
         * Called when the service has disconnected from the activity.
         */
        void onServiceDisconnected();
    }
}
