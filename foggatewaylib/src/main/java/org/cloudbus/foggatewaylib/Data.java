package org.cloudbus.foggatewaylib;

import java.util.Date;

public class Data implements Comparable<Data>{
    private static long NEXT_ID = 1;

    private long id;
    private long request_id;

    public static synchronized long incrementID(){
        return NEXT_ID++;
    }

    public Data(){
        this.id = incrementID();
    }

    public Data(long id){
        this.id = id;
    }

    public Data(Date date){
        this.id = date.getTime();
    }

    public Data(long id, long request_id){
        this.id = id;
        this.request_id = request_id;
    }

    public Data(Date date, long request_id){
        this.id = date.getTime();
        this.request_id = request_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRequestID() {
        return request_id;
    }

    public void setRequestID(long request_id) {
        this.request_id = request_id;
    }

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
}
