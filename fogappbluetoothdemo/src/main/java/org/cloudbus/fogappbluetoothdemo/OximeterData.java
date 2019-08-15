package org.cloudbus.fogappbluetoothdemo;

import org.cloudbus.foggatewaylib.Data;

import java.util.Date;

public class OximeterData extends Data {
    private int bpm;
    private int spo2;
    private float pi;

    public static OximeterData fromJumper(String address, byte[] value){
        if (value[0] == -127 && value.length == 4){
            int bpm = value[1];
            int spo2 = value[2];
            float pi = value[3]/10f;

            long request_id = Long.valueOf(address.replaceAll(":", ""), 16);

            return new OximeterData(request_id, bpm, spo2, pi);
        } else
            return null;
    }

    public OximeterData(long request_id, int bpm, int spo2, float pi){
        super(new Date(), request_id);
        this.bpm = bpm;
        this.spo2 = spo2;
        this.pi = pi;
    }

    public float getPI() {
        return pi;
    }

    public int getBPM() {
        return bpm;
    }

    public int getSpO2() {
        return spo2;
    }
}
