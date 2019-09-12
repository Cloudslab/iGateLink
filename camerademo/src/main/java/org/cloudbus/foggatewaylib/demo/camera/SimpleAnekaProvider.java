package org.cloudbus.foggatewaylib.demo.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.provider.Settings;

import com.manjrasoft.aneka.ArrayOfFile;
import com.manjrasoft.aneka.ArrayOfTaskItem;

import org.apache.commons.net.ftp.FTP;
import org.cloudbus.foggatewaylib.aneka.AnekaProvider;
import org.cloudbus.foggatewaylib.aneka.FTPStorageBucket;
import org.cloudbus.foggatewaylib.aneka.StorageBucket;
import org.cloudbus.foggatewaylib.aneka.WSDLBuilder;
import org.cloudbus.foggatewaylib.camera.ImageData;

/**
 * Simple provider for Aneka direct execution.
 *
 * @author Riccardo Mancini
 */
class SimpleAnekaProvider extends AnekaProvider<ImageData, ImageData> {
    private String host;

    public SimpleAnekaProvider() {
        super(FTP.BINARY_FILE_TYPE, 2, ImageData.class, ImageData.class);
    }

    @Override
    protected ArrayOfTaskItem buildTasks() {
        return WSDLBuilder.buildArrayOfTaskItem(
                // unzip the library files
                WSDLBuilder.buildExecuteTaskItem(
                        "7z",
                        "x", "-tzip", "Yolo.zip", "-y"),
                // start the recognition
                WSDLBuilder.buildExecuteTaskItem(
                        "python",
                        "test_images.py", "params.py")
        );
    }

    @Override
    protected void initCredentials() {
        if (getExecutionManager() == null)
            return;

        Context context = getContext();
        if (context == null)
            return;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        username = prefs.getString("aneka_username", "");
        password = prefs.getString("aneka_password", "");
        host = prefs.getString("aneka_master_ip", "");
        url = "http://" + host + "/Aneka.2.0/TaskService.asmx";
        appName = context.getApplicationContext().getPackageName()
                + "#"
                + Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
    }

    @Override
    protected ArrayOfFile initSharedFiles() {
        return WSDLBuilder.buildArrayOfFile("FTP", "/", "",
                new String[]{
                        "Yolo.zip"
                }
        );
    }

    @Override
    protected StorageBucket[] initStorageBuckets() {
        return new StorageBucket[]{
                new FTPStorageBucket(
                        "FTP",
                        username,
                        password,
                        host
                )
        };
    }

    @Override
    protected byte[] inputToBytes(ImageData... input) {
        return input[0].getBytes();
    }

    /**
     * Not needed since I'm using only binary mode.
     */
    @Override
    protected String inputToString(ImageData... input) {
        return null;
    }

    @Override
    protected ImageData[] bytesToOutput(byte[] bytes) {
        return new ImageData[]{new ImageData(bytes, Configuration.ORIENTATION_PORTRAIT)};
    }

    /**
     * Not needed since I'm using only binary mode.
     */
    @Override
    protected ImageData[] stringToOutput(String string) {
        return new ImageData[0];
    }

    @Override
    protected String getInputVirtualPath() {
        return "input/input.jpg";
    }

    @Override
    protected String getOutputVirtualPath() {
        return "output/0_0.jpg";
    }
}