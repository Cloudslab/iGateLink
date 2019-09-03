package org.cloudbus.foggatewaylib.utils;

import androidx.annotation.Nullable;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Simple client for FTP wrapping {@link FTPClient}.
 * It always uses passive mode since it is targeted for use in Android.
 *
 * @see FTPClient
 *
 * @author Riccardo Mancini
 */
public class SimpleFTPClient {
    private String username;
    private String password;
    private String host;
    private int port;
    private int maxReadSize = 20000000; //20MB
    private FTPClient ftpClient;
    private boolean autoMKD = true;


    /**
     * Create a new {@link SimpleFTPClient} instance with given credentials and host information.
     *
     * @param username the username to use when authenticating to the FTP server.
     * @param password the password to use when authenticating to the FTP server.
     * @param host the host DNS name or IP (do not include "ftp://").
     * @param port the port of the host listening for FTP connections (usually {@code 21}).
     */
    public SimpleFTPClient(String username, String password, String host, int port){
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;

        ftpClient = new FTPClient();
    }

    /**
     * Applies custom configurations to the {@link FTPClient}.
     *
     * @see FTPClient#configure(FTPClientConfig)
     */
    public void configure(FTPClientConfig config){
        ftpClient.configure(config);
    }

    /**
     * Connects to and logs in the FTP server.
     *
     * @return {@code true} in case of success, {@code false} otherwise.
     * @see FTPClient#connect(String, int)
     * @see FTPClient#login(String, String)
     */
    public boolean connect() {
        try{
            ftpClient.connect(host, port);
            return ftpClient.login(username, password);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Disconnects from the FTP server.
     */
    public void disconnect(){
        try {
            ftpClient.disconnect();
        } catch (IOException e) {
            // do nothing
        }
    }

    /**
     * Retrieves the file at the given {@code path} from the server and writes it in the
     * {@code outputStream}.
     *
     * @param path the path of the file on the server.
     * @param outputStream the {@link OutputStream} to which data is written.
     * @param fileType the type of the file (most common values are {@link FTP#ASCII_FILE_TYPE} and
     *                 {@link FTP#BINARY_FILE_TYPE}).
     * @return {@code true} in case of success, {@code false} otherwise.
     * @see FTPClient#setFileType(int)
     * @see FTPClient#retrieveFile(String, OutputStream)
     */
    public boolean getToStream(String path, OutputStream outputStream, int fileType) {
        try{
            if (!ftpClient.isConnected()){
                connect();
            }
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(fileType);
            return ftpClient.retrieveFile(path, outputStream);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Saves the file at the given {@code path} in the given {@link File}.
     *
     * @param path the path of the file on the server.
     * @param file the file to which data is written.
     * @param fileType the type of the file (most common values are {@link FTP#ASCII_FILE_TYPE} and
     *                 {@link FTP#BINARY_FILE_TYPE}).
     * @return {@code true} in case of success, {@code false} otherwise.
     * @see FileOutputStream
     * @see #getToStream(String, OutputStream, int)
     */
    public boolean getToFile(String path, File file, int fileType) {
        try {
            return getToStream(path, new FileOutputStream(file), fileType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves the {@link InputStream} to the file at the given {@code path} from the server.
     *
     * @param path the path of the file on the server.
     * @param fileType the type of the file (most common values are {@link FTP#ASCII_FILE_TYPE} and
     *                 {@link FTP#BINARY_FILE_TYPE}).
     * @return the opened {@link InputStream} or {@code null} in case of any error.
     * @see FTPClient#setFileType(int)
     * @see FTPClient#retrieveFileStream(String)
     */
    @Nullable
    public InputStream getStream(String path, int fileType) {
        try{
            if (!ftpClient.isConnected()){
                connect();
            }

            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(fileType);
            return ftpClient.retrieveFileStream(path);
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves the content of the file at the given {@code path} from the server as a
     * {@link String}.
     * The {@code fileType} is assumed {@link FTP#ASCII_FILE_TYPE}.
     *
     * NB: The maximum of the string is determined by {@link #maxReadSize}, with default value
     * of ~20MB. This value can be changed through {@link #setMaxReadSize(int)}.
     *
     * @param path the path of the file on the server.
     * @return the content of the file as a String.
     * @see #getStream(String, int)
     * @see StreamReader#readStream(InputStream, int)
     */
    public String getString(String path) {
        InputStream inputStream = getStream(path, FTP.ASCII_FILE_TYPE);
        
        if (inputStream == null)
            return null;

        try {
            return StreamReader.readStream(inputStream, maxReadSize);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                // do nothing
            }
        }
    }

    /**
     * Retrieves the content of the file at the given {@code path} from the server as a
     * byte array.
     * The {@code fileType} is assumed {@link FTP#BINARY_FILE_TYPE}.
     *
     * NB: The maximum of the string is determined by {@link #maxReadSize}, with default value
     * of ~20MB. This value can be changed through {@link #setMaxReadSize(int)}.
     *
     * @param path the path of the file on the server.
     * @return the content of the file as a byte array.
     * @see #getStream(String, int)
     * @see StreamReader#readStream(InputStream, int)
     */
    public byte[] getBytes(String path) {
        InputStream inputStream = getStream(path, FTP.BINARY_FILE_TYPE);
        
        if (inputStream == null)
            return null;

        try {
            return StreamReader.readByteStream(inputStream, maxReadSize);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                // do nothing
            }
        }
    }

    /**
     * Uploads content of the given {@link InputStream} as a new file at the given {@code path}
     * in the FTP server.
     * If {@link #autoMKD} is set to {@code true} (default), all necessary folders are created
     * in the server if missing. This behaviour can be changed by calling
     * {@link #enableAutoMkdirEnabled(boolean)}.
     *
     * @param path the path of the file on the server.
     * @param inputStream the stream from which data is uploaded.
     * @param fileType the type of the file (most common values are {@link FTP#ASCII_FILE_TYPE} and
     *                 {@link FTP#BINARY_FILE_TYPE}).
     * @return {@code true} in case of success, {@code false} otherwise.
     * @see FTPClient#storeFile(String, InputStream)
     */
    public boolean putStream(String path, InputStream inputStream, int fileType) {
        try{
            if (!ftpClient.isConnected()){
                connect();
            }
            ftpClient.enterLocalPassiveMode();
            if (autoMKD && path.contains("/")){
                if (!mkdir(path.substring(0, path.lastIndexOf('/')), true))
                    return false;
            }
            ftpClient.setFileType(fileType);
            return ftpClient.storeFile(path, inputStream);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                // do nothing
            }
        }
    }

    /**
     * Uploads the given {@link File} at the given {@code path} in the FTP server.
     *
     * @param path the path of the file on the server.
     * @param file the file to which data is written.
     * @param fileType the type of the file (most common values are {@link FTP#ASCII_FILE_TYPE} and
     *                 {@link FTP#BINARY_FILE_TYPE}).
     * @return {@code true} in case of success, {@code false} otherwise.
     * @see #putStream(String, InputStream, int)
     */
    public boolean putFile(String path, File file, int fileType) {
        try {
            return putStream(path, new FileInputStream(file), fileType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Uploads the given {@link File} at the given {@code path} in the FTP server as a binary file.
     *
     * @param path the path of the file on the server.
     * @param file the file to which data is written.
     * @return {@code true} in case of success, {@code false} otherwise.
     * @see #putFile(String, File, int)
     */
    public boolean putFile(String path, File file) {
        return putFile(path, file, FTP.BINARY_FILE_TYPE);
    }

    /**
     * Uploads the given bytes as a new file at the given {@code path} in the FTP server.
     *
     * @param path the path of the file on the server.
     * @param bytes the bytes to be uploaded.
     * @param fileType the type of the file (most common values are {@link FTP#ASCII_FILE_TYPE} and
     *                 {@link FTP#BINARY_FILE_TYPE}).
     * @return {@code true} in case of success, {@code false} otherwise.
     * @see #putStream(String, InputStream, int)
     */
    public boolean putBytes(String path, byte[] bytes, int fileType) {
        return putStream(path, new ByteArrayInputStream(bytes), fileType);
    }

    /**
     * Uploads the given bytes as a new binary file at the given {@code path} in the FTP server.
     *
     * @param path the path of the file on the server.
     * @param bytes the bytes to be uploaded.
     * @return {@code true} in case of success, {@code false} otherwise.
     * @see #putBytes(String, byte[])
     */
    public boolean putBytes(String path, byte[] bytes) {
        return putBytes(path, bytes, FTP.BINARY_FILE_TYPE);
    }

    /**
     * Uploads the given {@link String} as a new file at the given {@code path} in the FTP server.
     *
     * {@link FTP#ASCII_FILE_TYPE} is assumed.
     *
     * @param path the path of the file on the server.
     * @param string the string to be uploaded.
     * @return {@code true} in case of success, {@code false} otherwise.
     * @see #putBytes(String, byte[])
     */
    public boolean putString(String path, String string) {
        return putBytes(path, string.getBytes(), FTP.ASCII_FILE_TYPE);
    }

    /**
     * Creates a new directory in the FTP server at the given {@code path}. If {@code recursive} is
     * set to {@code true}, missing intermediate directory will be generated too. Otherwise, a
     * missing directory in the path will cause the function to return {@code false}.
     *
     * @param path the path of the new directory on the server.
     * @param recursive enable recursive mode.
     * @return {@code true} if the directory has been created, {@code false} in case of errors.
     * @see FTPClient#mkd(String)
     * @see FTPClient#makeDirectory(String)
     */
    public boolean mkdir(String path, boolean recursive){
        try{
            if (!ftpClient.isConnected()){
                connect();
            }
            ftpClient.enterLocalPassiveMode();
            if (recursive){
                int index = 0;
                do{
                    index = path.indexOf("/", index+1);
                    int reply = ftpClient.mkd(path.substring(
                            0,
                            index < 0 ? path.length() : index));

                    // 550: folder already exists; 257: ok
                    if (reply != 550 && reply != 257)
                        return false;
                } while (index >= 0);

                return true;
            } else{
                return ftpClient.makeDirectory(path);
            }
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates a new directory in the FTP server at the given {@code path}. All directories
     * from the root must exist.
     *
     * @param path the path of the new directory on the server.
     * @return {@code true} if the directory has been created, {@code false} in case of errors.
     * @see #mkdir(String, boolean)
     */
    public boolean mkdir(String path){
        return mkdir(path, false);
    }

    /**
     * Returns the currently set maximum read size.
     *
     * @see #getBytes(String)
     * @see #getString(String)
     */
    public int getMaxReadSize() {
        return maxReadSize;
    }

    /**
     * Sets the maximum read size.
     *
     * @see #getBytes(String)
     * @see #getString(String)
     */
    public void setMaxReadSize(int maxReadSize) {
        this.maxReadSize = maxReadSize;
    }

    /**
     * Returns {@code true} if automatic directory creation is enabled, {@code false} otherwise.
     *
     * @see #putStream(String, InputStream, int)
     */
    public boolean isAutoMkdirEnabled() {
        return autoMKD;
    }

    /**
     * Enables or disables automatic directory creation.
     *
     * @see #putStream(String, InputStream, int)
     */
    public void enableAutoMkdirEnabled(boolean autoMKD) {
        this.autoMKD = autoMKD;
    }
}

