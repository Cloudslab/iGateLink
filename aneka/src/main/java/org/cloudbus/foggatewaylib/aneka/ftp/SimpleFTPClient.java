package org.cloudbus.foggatewaylib.aneka.ftp;

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

    public boolean getToStream(String path, OutputStream outputStream) {
        try{
            if (!ftpClient.isConnected()){
                connect();
            }
            return ftpClient.retrieveFile(path, outputStream);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean getToFile(String path, File file) {
        try {
            return getToStream(path, new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public InputStream getStream(String path) {
        try{
            if (!ftpClient.isConnected()){
                connect();
            }

            return ftpClient.retrieveFileStream(path);
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public String getString(String path) {
        InputStream inputStream = getStream(path);
        
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
        InputStream inputStream = getStream(path);
        
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


    public boolean putStream(String path, InputStream inputStream) {
        try{
            if (!ftpClient.isConnected()){
                connect();
            }
            return ftpClient.storeFile(path, inputStream);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean putFile(String path, File file) {
        try {
            return putStream(path, new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean putBytes(String path, byte[] bytes) {
        return putStream(path, new ByteArrayInputStream(bytes));
    }

    public boolean putString(String path, String string) {
        return putBytes(path, string.getBytes());
    }

    public boolean mkdir(String path){
        try{
            if (!ftpClient.isConnected()){
                connect();
            }
            return ftpClient.makeDirectory(path);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    public int getMaxReadSize() {
        return maxReadSize;
    }

    public void setMaxReadSize(int maxReadSize) {
        this.maxReadSize = maxReadSize;
    }
}

