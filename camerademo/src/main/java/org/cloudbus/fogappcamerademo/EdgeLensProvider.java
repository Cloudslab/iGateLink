package org.cloudbus.fogappcamerademo;

import android.content.res.Configuration;
import android.util.Log;
import android.util.Pair;

import androidx.preference.PreferenceManager;

import org.cloudbus.foggatewaylib.Data;
import org.cloudbus.foggatewaylib.SimpleHttpConnection;
import org.cloudbus.foggatewaylib.ThreadPoolDataProvider;
import org.cloudbus.foggatewaylib.camera.ImageData;

public class EdgeLensProvider extends ThreadPoolDataProvider<ImageData, ImageData> {
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
                .getDefaultSharedPreferences(getService())
                .getString("fogbus_master_ip", "");
    }

    @Override
    public ImageData[] getData(ProgressPublisher progressPublisher,
                               long requestID, Data... input) throws Exception {

        SimpleHttpConnection arbiterConnection;
        SimpleHttpConnection uploadConnection;
        SimpleHttpConnection execConnection;
        SimpleHttpConnection outputConnection;

        ImageData output;

        if (input.length == 0)
            throw new Exception("Input image is empty.");

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
        String uploadResult = uploadConnection.postBytes(((ImageData)input[0]).getBytes());

        Log.d(TAG, "Image uploaded (result: " + uploadResult + ")");

        if (!uploadResult.contains("File xfer completed."))
            throw new Exception("Error in file transfer.");

        if (cloud){
            execConnection = new SimpleHttpConnection(workerIP, EXEC_URL,
                    new Pair<>("cloud", "true"));
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

        progressPublisher.publish(1, "Done");

        return new ImageData[]{output};
    }
}
