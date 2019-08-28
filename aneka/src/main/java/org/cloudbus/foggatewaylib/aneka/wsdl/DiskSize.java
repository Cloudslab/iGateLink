package org.cloudbus.foggatewaylib.aneka.wsdl;

public class DiskSize extends StringType {
    public static final String SIZE_TINY = "Tiny";
    public static final String SIZE_SMALL = "Small";
    public static final String SIZE_NORMAL = "Normal";
    public static final String SIZE_BIG = "Big";
    public static final String SIZE_HUGE = "Huge";

    public DiskSize() {
        super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "DiskSize");
    }

}
