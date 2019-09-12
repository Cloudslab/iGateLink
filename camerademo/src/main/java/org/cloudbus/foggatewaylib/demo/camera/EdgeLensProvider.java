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

/**
 * Provider for image recognition using EdgeLens.
 *
 * @author Riccardo Mancini
 */
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

    /**
     * Sets the master IP from user settings when attached to the service.
     */
    @Override
    public void onAttach() {
        super.onAttach();
        masterIP = PreferenceManager
                .getDefaultSharedPreferences(getContext())
                .getString("fogbus_master_ip", "");
    }

    /**
     * Runs image recognition on the provided EdgeLens instance.
     *
     * The high-level workflow is:
     * <ol>
     *     <li>the arbiter is queried to know to which worker to send the job.</li>
     *     <li>the image is uploaded to the worker (or the master in case of cloud execution).</li>
     *     <li>execution is started on the worker (or cloud).</li>
     *     <li>the output is retrieved from the worker (or master in case of cloud execution).</li>
     * </ol>
     */
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
        // spaces and newlines must be removed from the string
        workerIP = workerIP.replace("\n", "").trim();

        Log.d(TAG, "Arbiter result: " + workerIP);

        // in case result is `cloud`, communication must happen with master node
        boolean cloud = workerIP.equals("cloud");
        if (cloud)
            workerIP = masterIP;

        progressPublisher.publish(0, "Uploading image");

        uploadConnection  = new SimpleHttpConnection(workerIP, UPLOAD_URL);
        String uploadResult = uploadConnection.postBytes(input[0].getBytes());

        Log.d(TAG, "Image uploaded (result: " + uploadResult + ")");

        // check whether upload completed successfully
        if (!uploadResult.contains("File xfer completed."))
            throw new IOException("Error in file transfer.");

        if (cloud){
            Map<String, String> params = new HashMap<>();
            params.put("cloud", "true");
            execConnection = new SimpleHttpConnection(workerIP, EXEC_URL, params);
        } else{
            execConnection = new SimpleHttpConnection(workerIP, EXEC_URL);
        }

        progressPublisher.publish(0, "Elaborating image");

        // operation may take a lot of time so timeout is disabled
        execConnection.setReadTimeout(0);
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
