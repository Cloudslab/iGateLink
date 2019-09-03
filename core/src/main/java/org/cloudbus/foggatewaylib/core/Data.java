package org.cloudbus.foggatewaylib.core;

import androidx.annotation.Nullable;

import java.util.Date;

/**
 * Base class for all data types managed by this library.
 *
 * @author Riccardo Mancini
 */
public class Data implements Comparable<Data>{
    /**
     * Unique for Data within a {@link Store}.
     */
    private long id;

    /**
     * ID of the request this Data belongs to.
     * Defaults to 0.
     */
    private long request_id;

    /**
     * Used for generating unique auto-incrementing {@code id}s using {@link #incrementID}.
     */
    private static long NEXT_ID = 1;

    /**
     * Generates unique auto-incrementing {@code id}s.
     *
     * @return unique auto-incremented ID
     * @see #NEXT_ID
     */
    public static synchronized long incrementID(){
        return NEXT_ID++;
    }

    /**
     * Constructor for auto-incrementing {@code id} and default {@code request_id} (0).
     *
     * @see #incrementID()
     */
    public Data(){
        this.id = incrementID();
    }

    /**
     * Constructor for user-defined {@code id} and default {@code request_id} (0).
     *
     * @param id the {@code id} to be set in the object.
     */
    public Data(long id){
        this.id = id;
    }

    /**
     * Constructor for setting {@code id} to {@code date} in milliseconds and default
     * {@code request_id} (0).
     *
     * @param date the {@code date} whose milliseconds will be the object {@code id}.
     * @see Date#getTime()
     */
    public Data(Date date){
        this.id = date.getTime();
    }

    /**
     * Constructor for setting {@code id} and {@code request_id} to user-defined values.
     *
     * @param id the {@code id} to be set in the object.
     * @param request_id the {@code id} to be set in the object.
     */
    public Data(long id, long request_id){
        this.id = id;
        this.request_id = request_id;
    }

    /**
     * Constructor for setting {@code id} to {@code date} in milliseconds and
     * {@code request_id} to user-defined value.
     *
     * @param date the {@code date} whose milliseconds will be the object {@code id}.
     * @param request_id the {@code id} to be set in the object.
     * @see Date#getTime()
     */
    public Data(Date date, long request_id){
        this.id = date.getTime();
        this.request_id = request_id;
    }

    /**
     * @return this object {@code id}.
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the new value for the object {@code id}.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return this object {@code request_id}.
     */
    public long getRequestID() {
        return request_id;
    }

    /**
     * @param request_id the new value for the object {@code request_id}.
     */
    public void setRequestID(long request_id) {
        this.request_id = request_id;
    }

    /**
     * Comparable interface for sorting {@code Data} objects based on their
     * {@code id}.
     *
     * @param other the other {@code Data} to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     *         is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(Data other) {
        long difference = this.getId() - other.getId();
        if (difference > 0)
            return 1;
        else if (difference < 0)
            return -1;
        else
            return 0;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Data)
            return this.id == ((Data) obj).getId();
        else
            return super.equals(obj);
    }
}
