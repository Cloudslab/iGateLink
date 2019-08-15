package org.cloudbus.foggatewaylib.demo.bluetooth;


import org.cloudbus.foggatewaylib.core.Data;

public class AnalysisResultData extends Data {
    int AHI;
    int minSpO2;
    String AHSeverity;
    int maxBPM;
    int minBPM;
    float avgBPM;
    String EKGDiagnosis;
}
