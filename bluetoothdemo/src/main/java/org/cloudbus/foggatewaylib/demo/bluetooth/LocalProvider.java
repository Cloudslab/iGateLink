package org.cloudbus.foggatewaylib.demo.bluetooth;

import org.cloudbus.foggatewaylib.core.SequentialProvider;

public class LocalProvider extends SequentialProvider<OximeterData, AnalysisResultData> {

    public LocalProvider() {
        super(OximeterData.class, AnalysisResultData.class);
    }

    @Override
    public AnalysisResultData[] getData(ProgressPublisher progressPublisher,
                                        long requestID,
                                        OximeterData[] input) throws Exception{

        progressPublisher.publish(0, "Starting local execution...");

        AnalysisResultData result = new AnalysisResultData();
        result.AHI = 0;
        result.minSpO2 = Integer.MAX_VALUE;
        result.avgBPM = 0;
        result.maxBPM = 0;
        result.minBPM = Integer.MAX_VALUE;

        OximeterData previous = null;
        int count = 0;
        for (OximeterData d:input){
            if (d.getBPM() != -1 && d.getSpO2() != 127){
                if (d.getSpO2() <= 88 && (previous == null || previous.getSpO2() > 88)){
                    result.AHI++;
                }

                if (d.getSpO2() < result.minSpO2)
                    result.minSpO2 = d.getSpO2();

                result.avgBPM += d.getBPM();

                if (d.getBPM() > result.maxBPM)
                    result.maxBPM = d.getBPM();

                if (d.getBPM() < result.minBPM)
                    result.minBPM = d.getBPM();

                previous = d;
                count++;
            }
        }

        if (count == 0)
            throw new Exception("There is no valid data!");

        result.avgBPM /= count;

        if (result.avgBPM  > 100) {
            result.EKGDiagnosis  = "High Probability of Tachycardia";
        } else if (result.avgBPM  < 60) {
            result.EKGDiagnosis  = "High Probability of Bradycardia";
        } else {
            result.EKGDiagnosis  = "Normal ECG";
        }

        if (result.AHI < 5) {
            result.AHSeverity = "None";
        } else if (result.AHI < 15){
            result.AHSeverity = "Mild";
        } else if (result.AHI < 30){
            result.AHSeverity = "Moderate";
        } else {
            result.AHSeverity = "Highly Severe";
        }

        Thread.sleep(5000); //debug

        progressPublisher.publish(100, "Done");

        return new AnalysisResultData[]{result};
    }
}
