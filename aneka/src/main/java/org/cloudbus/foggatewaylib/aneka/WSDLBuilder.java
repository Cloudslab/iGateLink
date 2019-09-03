package org.cloudbus.foggatewaylib.aneka;

import com.manjrasoft.aneka.ArrayOfFile;
import com.manjrasoft.aneka.ArrayOfTaskItem;
import com.manjrasoft.aneka.ExecuteTaskItem;
import com.manjrasoft.aneka.File;
import com.manjrasoft.aneka.TaskItem;

import org.cloudbus.foggatewaylib.utils.StringUtils;

public class WSDLBuilder {
    public static ArrayOfFile buildArrayOfFile(String storageBucketId, String path,
                                               String virtualPath, String[] filenames){
        ArrayOfFile arrayOfFile = new ArrayOfFile();
        File[] files = new File[filenames.length];
        for (int i = 0; i < filenames.length; i++){
            files[i] = new File();
            files[i].setStorageBucketId(storageBucketId);
            files[i].setVirtualPath(virtualPath + filenames[i]);
            files[i].setPath(path + filenames[i]);
        }
        arrayOfFile.setFile(files);
        return arrayOfFile;
    }

    public static ArrayOfFile mergeArrayOfFiles(ArrayOfFile... arrays){
        int totalLength = 0;

        for (ArrayOfFile array : arrays) {
            totalLength += array.getFile().length;
        }

        ArrayOfFile mergedArray = new ArrayOfFile();
        File[] files = new File[totalLength];

        int index = 0;
        for (ArrayOfFile array : arrays) {
            for (File file : array.getFile()){
                files[index++] = file;
            }
        }

        mergedArray.setFile(files);
        return mergedArray;
    }

    public static ArrayOfFile buildArrayOfFile(String storageBucketId, String path,
                                               String virtualPath){
        ArrayOfFile arrayOfFile = new ArrayOfFile();
        File[] files = new File[1];
        files[0] = new File();
        files[0].setStorageBucketId(storageBucketId);
        files[0].setVirtualPath(virtualPath);
        files[0].setPath(path);
        arrayOfFile.setFile(files);
        return arrayOfFile;
    }

    public static File buildFile(String storageBucketId, String path, String virtualPath){
        File file = new File();
        file.setStorageBucketId(storageBucketId);
        file.setVirtualPath(virtualPath);
        file.setPath(path);
        return file;
    }

    public static ExecuteTaskItem buildExecuteTaskItem(String cmd, String... args){
        String arguments = StringUtils.join(" ", args);
        ExecuteTaskItem executeTaskItem = new ExecuteTaskItem();
        executeTaskItem.setCommand(cmd);
        executeTaskItem.setArguments(arguments);
        return executeTaskItem;
    }

    public static ArrayOfTaskItem buildArrayOfTaskItem(TaskItem... taskItems){
        ArrayOfTaskItem arrayOfTaskItem = new ArrayOfTaskItem();
        arrayOfTaskItem.setTaskItem(taskItems);
        return arrayOfTaskItem;
    }
}
