package org.cloudbus.foggatewaylib.service;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.cloudbus.foggatewaylib.core.ExecutionManager;

import java.util.UUID;

/**
 * Companion fragment for the {@link FogGatewayServiceActivity}.
 * It provides a simplified API for accessing and using the {@link ExecutionManager}.
 *
 * @author Riccardo Mancini
 */
public abstract class FogGatewayServiceFragment extends Fragment
        implements FogGatewayServiceActivity.ServiceConnectionListener {

    /**
     * Unique string used to register the {@link FogGatewayServiceActivity.ServiceConnectionListener}.
     */
    private String uuid = UUID.randomUUID().toString();

    /**
     * Keys of the triggers that this fragment will create.
     */
    private String[] triggerKeys;

    /**
     * Constructs a new Fragment that will use the given UI triggers.
     *
     * @param triggerKeys the keys of the UI triggers used by the fragment. These triggers will
     *                    be removed at the end of the execution.
     */
    public FogGatewayServiceFragment(String... triggerKeys) {
        this.triggerKeys = triggerKeys;
    }

    /**
     * Returns the {@link ExecutionManager} linked to the {@link FogGatewayServiceActivity} or
     * {@code null} if either the activity is {@code null} or the {@link ExecutionManager} is
     * {@code null} in the activity.
     *
     * @see FogGatewayServiceActivity#getExecutionManager()
     */
    @Nullable
    protected ExecutionManager getExecutionManager(){
        if (getActivity() == null)
            return null;

        return ((ExecutionManager.Holder)getActivity()).getExecutionManager();
    }

    /**
     * Initializes the {@link ExecutionManager}.
     * This method will be called at {@link #onResume()} in case the {@link ExecutionManager} is
     * ready or when it becomes available
     * ({@link FogGatewayServiceActivity.ServiceConnectionListener#onServiceConnected(FogGatewayService)}).
     *
     * @see #onResume()
     * @see FogGatewayServiceActivity.ServiceConnectionListener#onServiceConnected(FogGatewayService)
     */
    protected abstract void initExecutionManager(ExecutionManager executionManager);

    /**
     * Initializes the {@link ExecutionManager}, if available. Otherwise, sets the
     * {@link FogGatewayServiceActivity} to initialize it as soon as it becomes available.
     *
     * @see #initExecutionManager(ExecutionManager)
     * @see FogGatewayServiceActivity#addServiceConnectionListener(String, FogGatewayServiceActivity.ServiceConnectionListener)
     */
    @Override
    @CallSuper
    public void onResume() {
        super.onResume();
        if (getActivity() == null)
            return;

        if (getExecutionManager() != null){
            initExecutionManager(getExecutionManager());
        } else{
            ((FogGatewayServiceActivity)getActivity())
                    .addServiceConnectionListener(uuid, this);
        }
    }

    /**
     * Removes the {@link #triggerKeys} associated with this fragment and removes the
     * {@link FogGatewayServiceActivity.ServiceConnectionListener} from the activity.
     */
    @Override
    @CallSuper
    public void onPause() {
        super.onPause();
        if (getActivity() == null)
            return;

        if (getExecutionManager() != null){
            for (String triggerKey:triggerKeys){
                getExecutionManager().removeUITrigger(triggerKey);
            }
        }

        ((FogGatewayServiceActivity)getActivity())
                .removeServiceConnectionListener(uuid);
    }

    /**
     * When the {@link FogGatewayService} is bound to the activity, initializes the
     * {@link ExecutionManager}.
     *
     * @see #initExecutionManager(ExecutionManager)
     */
    @Override
    @CallSuper
    public void onServiceConnected(FogGatewayService service) {
        initExecutionManager(service.getExecutionManager());
    }

    /**
     * Called when the {@link FogGatewayService} unbinds from the activity, does nothing unless
     * overridden.
     */
    @Override
    public void onServiceDisconnected() { }
}
