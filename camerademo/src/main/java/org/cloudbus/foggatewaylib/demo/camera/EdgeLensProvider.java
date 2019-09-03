package org.cloudbus.foggatewaylib.demo.camera;

import android.content.res.Configuration;
import android.util.Log;

import androidx.preference.PreferenceManager;

import org.cloudbus.foggatewaylib.camera.ImageData;
import org.cloudbus.foggatewaylib.core.ThreadPoolProvider;
import org.cloudbus.foggatewaylib.utils.SimpleHttpConnection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EdgeLensProvider extends ThreadPoolProvider<ImageData, ImageData> {
    public static final String TAG = "EdgeLensProvider";

    public static final String ARBITER_URL = "/EdgeLens/arbiter.php";
    public static final String UPLOAD_URL = "/EdgeLens/upload.php";
    public static final String EXEC_URL = "/EdgeLens/exec.php";
    public static final String OUTPUT_URL = "/EdgeLens/output.jpg";

    private String masterIP;

    public EdgeLensProvider(int nThreads) {
        super(nThreads, ImageData.class, ImageData.class);
    }

    @Override
    public void onAttach() {
        super.onAttach();
        masterIP = PreferenceManager
                .getDefaultSharedPreferences(getExecutionManager().getContext())
                .getString("fogbus_master_ip", "");
    }

    @Override
    @SuppressWarnings("unchecked")
    public ImageData[] getData(ProgressPublisher progressPublisher,
                               long requestID, ImageData... input) throws Exception {

        SimpleHttpConnection arbiterConnection;
        SimpleHttpConnection uploadConnection;
        SimpleHttpConnection execConnection;
        SimpleHttpConnection outputConnection;

        ImageData output;

        if (input.length == 0)
            throw new Exception("Input array is empty.");

        Log.d(TAG, "Starting image upload...");
        progressPublisher.publish(0, "Querying arbiter");

        arbiterConnection = new SimpleHttpConnection(masterIP, ARBITER_URL);
        String workerIP = arbiterConnection.get();
        workerIP = workerIP.replace("\n", "").trim();

        Log.d(TAG, "Arbiter result: " + workerIP);

        boolean cloud = workerIP.equals("cloud");
        if (cloud)
            workerIP = masterIP;

        progressPublisher.publish(0, "Uploading image");

        uploadConnection  = new SimpleHttpConnection(workerIP, UPLOAD_URL);
        String uploadResult = uploadConnection.postBytes(input[0].getBytes());

        Log.d(TAG, "Image uploaded (result: " + uploadResult + ")");

        if (!uploadResult.contains("File xfer completed."))
            throw new IOException("Error in file transfer.");

        if (cloud){
            Map<String, String> params = new HashMap<>();
            params.put("cloud", "true");
            execConnection = new SimpleHttpConnection(workerIP, EXEC_URL, params);
        } else{
            execConnection = new SimpleHttpConnection(workerIP, EXEC_URL);
        }

        execConnection.setReadTimeout(0);

        progressPublisher.publish(0, "Elaborating image");

        String execResult = execConnection.get();

        Log.d(TAG, "Execution completed (result: " + execResult + ")");

        progressPublisher.publish(0, "Retrieving output");

        outputConnection = new SimpleHttpConnection(workerIP, OUTPUT_URL);

        output = new ImageData(outputConnection.getBytes(), Configuration.ORIENTATION_PORTRAIT);

        Log.d(TAG, "Output retrieved (length = " + output.getBytes().length + ")");

        progressPublisher.publish(100, "Done");

        return new ImageData[]{output};
    }
}
