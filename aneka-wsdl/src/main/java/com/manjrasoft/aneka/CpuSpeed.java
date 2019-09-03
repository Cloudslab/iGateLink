package com.manjrasoft.aneka;

public class CpuSpeed extends StringType {
    public static final String SPEED_LOW = "Low";
    public static final String SPEED_MEDIUM = "Medium";
    public static final String SPEED_HIGH = "High";

    public CpuSpeed() {
        super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "CpuSpeed");
    }

}
