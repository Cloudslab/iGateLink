package org.cloudbus.foggatewaylib.fogbus;

import android.text.TextUtils;

import org.cloudbus.foggatewaylib.core.Data;
import org.cloudbus.foggatewaylib.core.SequentialProvider;
import org.cloudbus.foggatewaylib.core.utils.SimpleHttpConnection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple provider skeleton for FogBus.
 * User needs to implement the following classes:
 * <ul>
 *     <li>{@link #getMasterIP()}</li>
 *     <li>{@link #getSessionUrl()}</li>
 *     <li>{@link #serializeData(ProgressPublisher, Data[])}</li>
 *     <li>{@link #parseOutput(ProgressPublisher, String, long)}</li>
 * </ul>
 *
 * @param <T> the type of input data
 * @param <S> the type of output data
 *
 * @author Riccardo Mancini
 */
public abstract class FogBusProvider<T extends Data, S extends Data> extends SequentialProvider<T, S> {
    public static final String TAG = "FogBusProvider";

    private String masterIP;
    private String sessionUrl;
    private boolean analyze;
    private int timeout = 10000;

    /**
     * Create a provider that sends data to FogBus and, optionally, analyzes it.
     *
     * @param analyze {@code true} if data will be analyzed, {@code false} otherwise.
     * @param inputType the type of input data
     * @param outputType the type of output data
     */
    public FogBusProvider(boolean analyze, Class<T> inputType, Class<S> outputType) {
        super(inputType, outputType);
        this.analyze = analyze;
    }

    /**
     *  Called when an {@link org.cloudbus.foggatewaylib.core.ExecutionManager} is attached,
     *  returns the IP of the master node, to which sending the requests.
     *
     * @return the IP of the master node.
     * @see #onAttach()
     */
    protected abstract String getMasterIP();

    /**
     *  Called when an {@link org.cloudbus.foggatewaylib.core.ExecutionManager} is attached,
     *  returns the url of the session page, to which sending the requests.
     *
     * @return the url of the session page.
     * @see #onAttach()
     */
    protected abstract String getSessionUrl();

    /**
     * Called outside the Main Thread, serializes data to send to FogBus.
     * FogBus input uses HTTP GET parameters of the type "dataX=v1,v2,v3,...".
     * This function must return a map where the keys are the indexes to append to data and
     * the list of strings the value to be serialized in the csv.
     *
     * @param progressPublisher used to publish progress.
     * @param input the data in input to be serialized.
     * @return the map as previously described.
     * @throws Exception in case of any error.
     * @see #getData(ProgressPublisher, long, Data[])
     */
    protected abstract Map<Integer, List<String>> serializeData(ProgressPublisher progressPublisher,
                                                                T... input) throws Exception;

    /**
     * Parses the output array from the server response.
     *
     * @param progressPublisher used to publish progress.
     * @param output the response of the FogBus server.
     * @param requestID the requestID of the input data.
     * @return the array with the outputs.
     * @throws Exception in case of any error
     * @see #getData(ProgressPublisher, long, Data[])
     */
    protected abstract S[] parseOutput(ProgressPublisher progressPublisher,
                                       String output, long requestID) throws Exception;

    @Override
    public void onAttach() {
        super.onAttach();
        masterIP = getMasterIP();
        sessionUrl = getSessionUrl();
    }


    @Override
    @SuppressWarnings("unchecked")
    public S[] getData(ProgressPublisher progressPublisher,
                               long requestID, T... input) throws Exception {
        SimpleHttpConnection connection;

        if (input.length == 0)
            throw new Exception("Input array is empty.");

        Map<Integer, List<String>> serializedData = serializeData(progressPublisher, input);

        Map<String, String> params = new HashMap<>();
        for (Integer key:serializedData.keySet()){
            params.put(String.format("data%d", key),
                    TextUtils.join(",", serializedData.get(key)));
        }
        if (analyze){
            params.put("analyze", "analyze");
        }

        connection = new SimpleHttpConnection(masterIP, sessionUrl, params);
        connection.setReadTimeout(timeout);
        return parseOutput(progressPublisher, connection.get(), input[0].getRequestID());
    }

    /**
     * Returns the previously set read timeout in milliseconds (default = 10000).
     *
     * @see #setTimeout(int)
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Sets the read timeout in milliseconds for the HTTP connection.
     *
     * @see SimpleHttpConnection#setReadTimeout(int)
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
