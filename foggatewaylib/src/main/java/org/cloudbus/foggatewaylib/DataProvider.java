package org.cloudbus.foggatewaylib;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.Arrays;

public abstract class DataProvider<T extends Data, S extends Data>{
    private static final String TAG = "DataProvider";

    private Class<T> inputType;
    private Class<S> outputType;
    private FogGatewayService service;

    protected DataStore<S> outStore;
    protected DataStore<ProgressData> progressStore;

    public DataProvider(Class<T> inputType, Class<S> outputType){
        this.inputType = inputType;
        this.outputType = outputType;
    }

    public Class<T> getInputType(){return inputType;}
    public Class<S> getOutputType(){return outputType;}

    public void attach(FogGatewayService service, DataStore<S> outStore,
                       DataStore<ProgressData> progressStore){
        this.service = service;
        if (service == null){
            Log.e(TAG, "service is null");
            return;
        }
        this.outStore = outStore;
        this.progressStore = progressStore;
        onAttach();
    }

    protected FogGatewayService getService(){
        if (this.service == null)
            Log.e(TAG, "Service is not bound");
        return this.service;
    }

    public void detach(){
        onDetach();
        this.service = null;
    }

    protected void publishProgress(long requestID, int progress, String message){
        progressStore.store(new ProgressData(requestID, progress, message));
    }

    protected void publishResult(long requestID, S... data){
        for (S d:data)
            d.setRequestID(requestID);
        outStore.store(data);
    }

    protected void publishResult(S... data){
        outStore.store(data);
    }


    public void onAttach(){}
    public void onDetach(){}

    public abstract void execute(long requestID, T... input);

    @SuppressWarnings("unchecked")
    public void executeCast(long requestID, Data... input){
        execute(requestID, Arrays.asList(input).toArray(
                (T[]) Array.newInstance(getInputType(), input.length)));
    }
}
