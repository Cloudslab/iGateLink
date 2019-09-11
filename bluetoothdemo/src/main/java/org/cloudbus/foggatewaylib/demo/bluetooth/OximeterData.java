package org.cloudbus.foggatewaylib.demo.bluetooth;

import org.cloudbus.foggatewaylib.core.Data;

import java.util.Date;

/**
 * Class representing data from the oximeter. A value of {@code -1} in any attribute means that
 * the oximeter returned an invalid value (e.g. oximeter couldn't read the beat).
 *
 * @author Riccardo Mancini
 */
public class OximeterData extends Data {
    private int bpm;
    private int spo2;
    private float pi;

    /**
     * Builds a new instance from raw data from a Jumper oximeter.
     */
    public static OximeterData fromJumper(String address, byte[] value){
        if (unsigned(value[0]) == 0x81 && value.length == 4){
            int bpm = unsigned(value[1]);
            if (bpm == 0xFF) //invalid
                bpm = -1;

            int spo2 = unsigned(value[2]);
            if (spo2 == 0x7F) //invalid
                spo2 = -1;

            int piInt = unsigned(value[3]);
            float pi;
            if (piInt == 0x00) //invalid
                pi = -1;
            else
                pi = piInt/10f;

            // set the request_id to the MAC address of the device.
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

    /**
     * Returns the unsigned value of the input byte as an integer.
     */
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
