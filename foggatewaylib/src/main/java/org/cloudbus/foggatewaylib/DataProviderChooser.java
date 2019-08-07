package org.cloudbus.foggatewaylib;

import android.util.Log;

public abstract class DataProviderChooser {
    public static final String TAG = "DataProviderChooser";

    private FogGatewayService service;

    public DataProviderChooser(){ }

    public abstract String chooseProvider(String... providers);

    public void onAttach(){}
    public void onDetach(){}

    public void attach(FogGatewayService service){
        this.service = service;
        if (service == null){
            Log.e(TAG, "service is null");
            return;
        }
        onAttach();
    }

    public void detach(){
        onDetach();
        this.service = null;
    }

    protected FogGatewayService getService(){
        if (this.service == null)
            Log.e(TAG, "Service is not bound");
        return this.service;
    }
}
