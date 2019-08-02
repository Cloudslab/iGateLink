package org.cloudbus.foggatewaylib;

import androidx.room.ColumnInfo;

import java.util.Date;

public class Data implements Comparable<Data>{

    @ColumnInfo(name = "timestamp")
    private Date timestamp;

    @Override
    public int compareTo(Data other) {
        return timestamp.compareTo(other.timestamp);
    }

    public Data(){
        this.timestamp = new Date();
    }

    public Data(long timestamp){
        this.timestamp = new Date(timestamp);
    }

    public Data(Date timestamp){
        this.timestamp = timestamp;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }


}
