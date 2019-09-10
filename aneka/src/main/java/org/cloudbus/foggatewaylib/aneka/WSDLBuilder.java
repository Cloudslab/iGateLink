package org.cloudbus.foggatewaylib.aneka;

import com.manjrasoft.aneka.ArrayOfFile;
import com.manjrasoft.aneka.ArrayOfTaskItem;
import com.manjrasoft.aneka.ExecuteTaskItem;
import com.manjrasoft.aneka.File;
import com.manjrasoft.aneka.TaskItem;

import org.cloudbus.foggatewaylib.utils.StringUtils;

/**
 * Utility class for generating the most common WSDL classes.
 *
 * @author Riccardo Mancini
 */
public class WSDLBuilder {

    /**
     * Builds an array of {@link File}s that have the same {@code storageBucketId} from an array of
     * file names (or relative paths) that are mapped from {@code path} on the server to
     * {@code virtualPath} in the execution environment.
     * This is especially useful for multiple files in the same directory. Furthermore, it can
     * save much boiler even if used with {@code path} and/or {@code virtualPath} set to
     * {@code ""} and a list of paths as {@code filenames}.
     *
     * @param storageBucketId the ID of the storage bucket.
     * @param path the base path of the files in the server.
     * @param virtualPath the base path of the files in the execution environment.
     * @param filenames the names (or relative paths) of the files.
     * @return the generated {@link ArrayOfFile}.
     */
    public static ArrayOfFile buildArrayOfFile(String storageBucketId, String path,
                                               String virtualPath, String[] filenames){
        ArrayOfFile arrayOfFile = new ArrayOfFile();
        File[] files = new File[filenames.length];
        for (int i = 0; i < filenames.length; i++){
            files[i] = new File();
            files[i].setStorageBucketId(storageBucketId);

            if (virtualPath.length() == 0 || virtualPath.charAt(virtualPath.length()-1) == '/')
                files[i].setVirtualPath(virtualPath + filenames[i]);
            else
                files[i].setVirtualPath(virtualPath + '/' + filenames[i]);

            if (path.length() > 0 && path.charAt(path.length()-1) == '/')
                files[i].setPath(path + filenames[i]);
            else
                files[i].setPath(path + '/' + filenames[i]);
        }
        arrayOfFile.setFile(files);
        return arrayOfFile;
    }

    /**
     * Merges multiple {@link ArrayOfFile} in a single {@link ArrayOfFile}.
     */
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

    /**
     * Builds a {@link File} instance with given attributes.
     */
    public static File buildFile(String storageBucketId, String path, String virtualPath){
        File file = new File();
        file.setStorageBucketId(storageBucketId);
        file.setVirtualPath(virtualPath);
        file.setPath(path);
        return file;
    }

    /**
     * Builds an {@link ArrayOfFile} with a single {@link File} with the given
     * {@code storageBucketId}, {@code path} and {@code virtualPath}.
     */
    public static ArrayOfFile buildArrayOfFile(String storageBucketId, String path,
                                               String virtualPath){
        ArrayOfFile arrayOfFile = new ArrayOfFile();
        arrayOfFile.setFile(new File[]{buildFile(storageBucketId, path, virtualPath)});
        return arrayOfFile;
    }

    /**
     * Builds an {@link ExecuteTaskItem} from the given command and list of arguments.
     *
     * @param cmd the command to be executed.
     * @param args the arguments to pass to the command.
     * @return the generated {@link ExecuteTaskItem}.
     */
    public static ExecuteTaskItem buildExecuteTaskItem(String cmd, String... args){
        String arguments = StringUtils.join(" ", args);
        ExecuteTaskItem executeTaskItem = new ExecuteTaskItem();
        executeTaskItem.setCommand(cmd);
        executeTaskItem.setArguments(arguments);
        return executeTaskItem;
    }

    /**
     * Builds an {@link ArrayOfTaskItem} from the given {@link TaskItem}s.
     */
    public static ArrayOfTaskItem buildArrayOfTaskItem(TaskItem... taskItems){
        ArrayOfTaskItem arrayOfTaskItem = new ArrayOfTaskItem();
        arrayOfTaskItem.setTaskItem(taskItems);
        return arrayOfTaskItem;
    }
}
