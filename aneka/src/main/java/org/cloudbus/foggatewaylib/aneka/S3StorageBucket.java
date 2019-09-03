package org.cloudbus.foggatewaylib.aneka;

import com.manjrasoft.aneka.Property;

public class S3StorageBucket extends StorageBucket {
    public final static String DEFAULT_FTP_PORT = "21";

    private String accessKey;
    private String bucket;

    public S3StorageBucket(String name, String accessKey, String bucket){
        super("S3", name);
        this.accessKey = accessKey;
        this.bucket = bucket;
    }

    @Override
    public int getPropertyCount() {
        return 3;
    }

    @Override
    protected void fillProperty(int index, Property property) {
        switch (index){
            case 1:
                property.setNameProperty("AWSAccessKeyId");
                property.setValue(accessKey);
                break;
            case 2:
                property.setNameProperty("Bucket");
                property.setValue(bucket);
                break;
            default:
                super.fillProperty(index, property);
        }
    }
}
