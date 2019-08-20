package org.cloudbus.foggatewaylib.demo.bluetooth;

import org.cloudbus.foggatewaylib.core.Data;

import java.util.Date;

public class OximeterData extends Data {
    private int bpm;
    private int spo2;
    private float pi;

    public static OximeterData fromJumper(String address, byte[] value){
        if (unsigned(value[0]) == 0x81 && value.length == 4){
            int bpm = unsigned(value[1]);
            if (bpm == 0xFF)
                bpm = -1;

            int spo2 = unsigned(value[2]);
            if (spo2 == 0x7F)
                spo2 = -1;

            int piInt = unsigned(value[3]);
            float pi;
            if (piInt == 0x00)
                pi = -1;
            else
                pi = piInt/10f;

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

    private static int unsigned(byte b){
        return b & 0xFF;
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
