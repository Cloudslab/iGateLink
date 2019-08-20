package org.cloudbus.foggatewaylib.demo.bluetooth;

import androidx.preference.PreferenceManager;

import org.cloudbus.foggatewaylib.fogbus.FogBusProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HealthKeeperProvider extends FogBusProvider<OximeterData, AnalysisResultData> {
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

    public HealthKeeperProvider() {
        super(true, OximeterData.class, AnalysisResultData.class);
    }

    @Override
    protected String getMasterIP() {
        return PreferenceManager
                .getDefaultSharedPreferences(getExecutionManager().getContext())
                .getString("fogbus_master_ip", "");
    }

    @Override
    protected String getSessionUrl() {
        return SESSION_URL;
    }

    @Override
    protected Map<Integer, List<String>> serializeData(ProgressPublisher progressPublisher,
                                                       OximeterData... input) {
        progressPublisher.publish(0, "Uploading data...");

        List<String> bpmList = new ArrayList<>();
        List<String> spo2List = new ArrayList<>();

        for (OximeterData d:input){
            if (d.getBPM() != -1 && d.getSpO2() != -1){
                bpmList.add(String.valueOf(d.getBPM()));
                spo2List.add(String.valueOf(d.getSpO2()));
            }
        }

        Map<Integer, List<String>> result = new HashMap<>();
        result.put(1, spo2List);
        result.put(2, bpmList);
        return result;
    }

    @Override
    protected AnalysisResultData[] parseOutput(ProgressPublisher progressPublisher,
                                               String result, long requestID)
                throws Exception{
        Matcher matcher = pattern.matcher(result);
        if (!matcher.find())
            throw new Exception("Output pattern does not match");

        AnalysisResultData output = new AnalysisResultData();
        output.setRequestID(requestID);

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
