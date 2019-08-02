package org.cloudbus.foggatewaylib.camera;

import org.cloudbus.foggatewaylib.InMemoryDataStore;

import java.util.HashMap;
import java.util.Map;

public class ImageStore extends InMemoryDataStore<ImageData> {
    private static Map<Integer, ImageStore> instances = new HashMap<>();

    public static ImageStore getInstance(int id, int maxElements){
        if (!instances.containsKey(id)){
            instances.put(id, new ImageStore(maxElements));
        }
        return instances.get(id);
    }

    public static ImageStore getInstance(int id){
        return getInstance(id, 0);
    }

    private ImageStore(int maxElements){
        super(maxElements);
    }
}
