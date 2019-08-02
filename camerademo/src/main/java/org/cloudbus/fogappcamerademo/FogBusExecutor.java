package org.cloudbus.fogappcamerademo;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import org.cloudbus.foggatewaylib.EventExecutor;
import org.cloudbus.foggatewaylib.SimpleHttpConnection;
import org.cloudbus.foggatewaylib.camera.ImageData;
import org.cloudbus.foggatewaylib.camera.ImageStore;

import static org.cloudbus.fogappcamerademo.MainActivity.STORE_INPUT;
import static org.cloudbus.fogappcamerademo.MainActivity.STORE_OUTPUT;

public class FogBusExecutor extends EventExecutor<ImageData, ImageData> {
    public static final String ARBITER_URL = "/EdgeLens/arbiter.php";
    public static final String UPLOAD_URL = "/EdgeLens/upload.php";
    public static final String EXEC_URL = "/EdgeLens/exec.php";
    public static final String OUTPUT_URL = "/EdgeLens/output.jpg";

    private String masterIP;

    @Override
    public ImageData[] onEventGatherInput() {
        masterIP = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString("fogbus_master_ip", "");
        return new ImageData[]{inStore.retrieveLast()};
    }

    @Override
    public ImageData onEventExecute(ImageData... input) throws Exception {
        SimpleHttpConnection arbiterConnection;
        SimpleHttpConnection uploadConnection;
        SimpleHttpConnection execConnection;
        SimpleHttpConnection outputConnection;

        ImageData output;

        if (input.length == 0)
            throw new Exception("Input image is empty.");

        Log.d("DEBUG", "Starting image upload...");

        arbiterConnection = new SimpleHttpConnection(masterIP, ARBITER_URL);
        String workerIP = arbiterConnection.get();
        workerIP = workerIP.replace("\n", "").trim();

        Log.d("DEBUG", "Arbiter result: " + workerIP);

        boolean cloud = workerIP.equals("cloud");
        if (cloud)
            workerIP = masterIP;

        uploadConnection  = new SimpleHttpConnection(workerIP, UPLOAD_URL);
        String uploadResult = uploadConnection.postBytes(input[0].getBytes());

        Log.d("DEBUG", "Image uploaded (result: " + uploadResult + ")");

        if (!uploadResult.contains("File xfer completed."))
            throw new Exception("Error in file transfer.");

        if (cloud){
            execConnection = new SimpleHttpConnection(workerIP, EXEC_URL, new Pair<>("cloud", "true"));
        } else{
            execConnection = new SimpleHttpConnection(workerIP, EXEC_URL);
        }

        String execResult = execConnection.get();

        Log.d("DEBUG", "Execution completed (result: " + execResult + ")");

        outputConnection = new SimpleHttpConnection(workerIP, OUTPUT_URL);

        output = new ImageData(outputConnection.getBytes(), Configuration.ORIENTATION_PORTRAIT);

        Log.d("DEBUG", "Output retrieved (length = " + output.getBytes().length + ")");

        return output;
    }

    @Override
    public void onEventResult(@NonNull ImageData output) {
        outStore.store(output);
    }

    @Override
    public void onEventError(Exception e) {
        e.printStackTrace();
        Toast.makeText(this, "An error occurred while elaborating the image: " + e.getMessage(),
                        Toast.LENGTH_LONG)
             .show();
    }

    @Override
    protected ImageStore initInStore() {
        return ImageStore.getInstance(STORE_INPUT);
    }

    @Override
    protected ImageStore initOutStore() {
        return ImageStore.getInstance(STORE_OUTPUT);
    }

    public static void start(Context context, Class<? extends Activity> cls){
        startForegroundService(context, FogBusExecutor.class, cls);
    }

    public static void stop(Context context){
        stopForegroundService(context, FogBusExecutor.class);
    }

    public static void executeStart(Context context){
        sendIntentToForegroundService(context, ACTION_EXECUTE, null, FogBusExecutor.class);
    }
}
