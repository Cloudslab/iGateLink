package org.cloudbus.foggatewaylib.aneka.ftp;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.cloudbus.foggatewaylib.aneka.FTPStorageBucket;
import org.cloudbus.foggatewaylib.core.utils.StreamReader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SimpleFTPClient {
    private String username;
    private String password;
    private String host;
    private int port;
    private int maxReadSize = 20000000; //20MB
    private FTPClient ftpClient;
    private boolean autoMKD = true;


    public SimpleFTPClient(String username, String password, String host, int port){
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;

        ftpClient = new FTPClient();
    }

    public SimpleFTPClient(FTPStorageBucket bucket){
        this(bucket.getUser(), bucket.getPassword(), bucket.getHost(), bucket.getPort());
    }

    public void configure(FTPClientConfig config){
        ftpClient.configure(config);
    }

    public boolean connect() {
        try{
            ftpClient.connect(host, port);
            return ftpClient.login(username, password);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    public void disconnect(){
        try {
            ftpClient.disconnect();
        } catch (IOException e) {
            // do nothing
        }
    }

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
    
    public boolean getToFile(String path, File file, int fileType) {
        try {
            return getToStream(path, new FileOutputStream(file), fileType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    
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

    public boolean putFile(String path, File file, int fileType) {
        try {
            return putStream(path, new FileInputStream(file), fileType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean putFile(String path, File file) {
        return putFile(path, file, FTP.BINARY_FILE_TYPE);
    }
    
    public boolean putBytes(String path, byte[] bytes, int fileType) {
        return putStream(path, new ByteArrayInputStream(bytes), fileType);
    }

    public boolean putBytes(String path, byte[] bytes) {
        return putBytes(path, bytes, FTP.BINARY_FILE_TYPE);
    }

    public boolean putString(String path, String string) {
        return putBytes(path, string.getBytes(), FTP.ASCII_FILE_TYPE);
    }

    public boolean mkdir(String path, boolean recursive){
        try{
            if (!ftpClient.isConnected()){
                connect();
            }
            ftpClient.enterLocalPassiveMode();
            int index = recursive ? 0 : path.length();
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
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean mkdir(String path){
        return mkdir(path, false);
    }

    public int getMaxReadSize() {
        return maxReadSize;
    }

    public void setMaxReadSize(int maxReadSize) {
        this.maxReadSize = maxReadSize;
    }

    public boolean isAutoMkdirEnabled() {
        return autoMKD;
    }

    public void setAutoMkdirEnabled(boolean autoMKD) {
        this.autoMKD = autoMKD;
    }
}

