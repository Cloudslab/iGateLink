package org.cloudbus.foggatewaylib.aneka;

import com.manjrasoft.aneka.Property;

/**
 * Representation of an S3 storage bucket.
 *
 * @author Riccardo Mancini
 */
public class S3StorageBucket extends StorageBucket {
    private String accessKey;
    private String bucket;

    /**
     * Constructs a new storage bucket with the given properties.
     *
     * @param name the name of this bucket (also referred as id).
     * @param accessKey the access key for AWS.
     * @param bucket the name of the S3 bucket.
     */
    public S3StorageBucket(String name, String accessKey, String bucket){
        super("S3", name);
        this.accessKey = accessKey;
        this.bucket = bucket;
    }

    /**
     * Returns the number of properties. Override this method to update the number of properties.
     *
     * @see StorageBucket#getPropertyCount()
     */
    @Override
    public int getPropertyCount() {
        return 3;
    }

    /**
     * Fills the given {@code property} knowing its {@code index}.
     * When overriding this class, do not handle case 0 but just call this method in the default
     * case.
     *
     * @see StorageBucket#fillProperty(int, Property)
     */
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
