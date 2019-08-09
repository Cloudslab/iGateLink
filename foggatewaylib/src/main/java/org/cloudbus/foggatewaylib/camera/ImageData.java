package org.cloudbus.foggatewaylib.camera;

import android.content.res.Configuration;
import android.util.Log;

import org.cloudbus.foggatewaylib.Data;

import java.util.Date;

/**
 * Type of {@link Data} used for images taken by the camera, stored as a byte array with
 * orientation information.
 *
 * @author Riccardo Mancini
 */
public class ImageData extends Data {
    public static final int ORIENTATION_IGNORE = -1;

    private byte[] bytes;

    /**
     * Orientation of the image, should be one of:
     * <ul>
     *     <li>{@link #ORIENTATION_IGNORE}</li>
     *     <li>{@link Configuration#ORIENTATION_UNDEFINED}</li>
     *     <li>{@link Configuration#ORIENTATION_PORTRAIT}</li>
     *     <li>{@link Configuration#ORIENTATION_LANDSCAPE}</li>
     * </ul>
     */
    private int orientation;

    /**
     * Creates an instance with given {@code bytes} and {@code orientation}, id set to
     * {@code timestamp} in milliseconds and request id {@code 0}.
     */
    public ImageData(Date timestamp, byte[] bytes, int orientation){
        super(timestamp);
        this.bytes = bytes;
        this.orientation = orientation;
    }

    /**
     * Creates an instance with given {@code bytes} and {@code orientation}, id set to the default
     * auto-incrementing one and request id {@code 0}.
     */
    public ImageData(byte[] bytes, int orientation){
        super();
        this.bytes = bytes;
        this.orientation = orientation;
        Log.d("DEBUG", "Orientation is " + orientation);
    }

    /**
     * Creates an instance with given {@code bytes}, {@code orientation} set to
     * {@link #ORIENTATION_IGNORE}, id set to the default auto-incrementing one and request id
     * {@code 0}.
     */
    public ImageData(byte[] bytes){
        this(bytes, ORIENTATION_IGNORE);
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
