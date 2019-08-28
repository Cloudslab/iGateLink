package org.cloudbus.foggatewaylib.aneka.wsdl;

public class MemorySize extends StringType {
    public static final String SIZE_SMALL = "Small";
    public static final String SIZE_MEDIUM = "Medium";
    public static final String SIZE_BIG = "Big";

    public MemorySize() {
        super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "MemorySize");
    }

}
