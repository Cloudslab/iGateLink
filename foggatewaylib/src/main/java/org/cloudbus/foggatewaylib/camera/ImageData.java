package org.cloudbus.foggatewaylib.camera;

import android.util.Log;

import org.cloudbus.foggatewaylib.Data;

import java.util.Date;

public class ImageData extends Data {
    private byte[] bytes;
    private int orientation;


    public ImageData(Date timestamp, byte[] bytes, int orientation){
        super(timestamp);
        this.bytes = bytes;
        this.orientation = orientation;
    }


    public ImageData(byte[] bytes, int orientation){
        super();
        this.bytes = bytes;
        this.orientation = orientation;
        Log.d("DEBUG", "Orientation is " + orientation);
    }

    public ImageData(byte[] bytes){
        this(bytes, -1);
    }

    public byte[] getBytes() {
        return bytes;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
