package org.cloudbus.fogappbluetoothdemo;

import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import androidx.preference.PreferenceManager;

import org.cloudbus.foggatewaylib.SequentialProvider;
import org.cloudbus.foggatewaylib.utils.SimpleHttpConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HealthKeeperProvider extends SequentialProvider<OximeterData, AnalysisResultData> {
    public static final String TAG = "HealthKeeperProvider";

    public static final String SESSION_URL = "/HealthKeeper/RPi/Master/session.php";

    private static Pattern pattern = Pattern.compile(
            "<br/>AHI \\(Apnea-hypopnea index\\) = ([0-9]+)[ \n]+" +
            "<br/>Minimum Oxygen Level = ([0-9]+)[ \n]+" +
            "<br/>Disease Severity : (.+?)[ \n]+" +
            "<br/>Minimum Heart Rate : ([0-9]+)[ \n]+" +
            "<br/>Maximum Heart Rate : ([0-9]+)[ \n]+" +
            "<br/>Average Heart Rate : ([0-9.]+)[ \n]+" +
            "<br/>Diagnosis : (.+?)<br/>");

    private String masterIP;

    public HealthKeeperProvider() {
        super(OximeterData.class, AnalysisResultData.class);
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
    public AnalysisResultData[] getData(ProgressPublisher progressPublisher,
                               long requestID, OximeterData... input) throws Exception {

        SimpleHttpConnection connection;

        if (input.length == 0)
            throw new Exception("Input array is empty.");

        List<Integer> bpmList = new ArrayList<>();
        List<Integer> spo2List = new ArrayList<>();

        for (OximeterData d:input){
            if (d.getBPM() != -1 && d.getSpO2() != 127){
                bpmList.add(d.getBPM());
                spo2List.add(d.getSpO2());
            }
        }

        String bpmCSV = TextUtils.join(",", bpmList);
        String spo2CSV = TextUtils.join(",", spo2List);

        Pair<String, String>[] params = new Pair[]{
                new Pair<>("data1", spo2CSV),
                new Pair<>("data2", bpmCSV),
                new Pair<>("analyze", "analyze")
        };


        Log.d(TAG, "Starting execution...");

        progressPublisher.publish(0, "Uploading data...");

        connection = new SimpleHttpConnection(masterIP, SESSION_URL, params);
        connection.setReadTimeout(10000);
        String result = connection.get();
        Matcher matcher = pattern.matcher(result);
        if (!matcher.find())
            throw new Exception("Output pattern does not match");

        AnalysisResultData output = new AnalysisResultData();
        output.setRequestID(input[0].getRequestID());

        output.AHI = Integer.valueOf(matcher.group(1));
        output.minSpO2 = Integer.valueOf(matcher.group(2));
        output.AHSeverity = matcher.group(3);
        output.minBPM = Integer.valueOf(matcher.group(4));
        output.maxBPM = Integer.valueOf(matcher.group(5));
        output.avgBPM = Float.valueOf(matcher.group(6));
        output.EKGDiagnosis = matcher.group(7);


        progressPublisher.publish(100, "Done");

        return new AnalysisResultData[]{output};
    }
}
