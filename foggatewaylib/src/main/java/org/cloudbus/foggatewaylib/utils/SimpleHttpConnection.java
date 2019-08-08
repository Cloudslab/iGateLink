package org.cloudbus.foggatewaylib.utils;

import android.util.Pair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class SimpleHttpConnection {
    private int maxReadSize = 5000000;
    private int connectionTimeout = 5000;
    private int readTimeout = 5000;

    private URL url;

    public SimpleHttpConnection(String domain, String page, Pair<String, String>... parameters)
            throws MalformedURLException{
        url = makeUrl(domain, page, false, parameters);
    }

    public SimpleHttpConnection(URL url){
        this.url = url;
    }

    public void setMaxReadSize(int maxReadSize) {
        this.maxReadSize = maxReadSize;
    }

    public int getMaxReadSize() {
        return maxReadSize;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public URL getUrl() {
        return url;
    }

    public String getStringUrl() {
        return url.toString();
    }

    public String get() throws IOException {
        InputStream stream = null;
        HttpURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(readTimeout);
            connection.setConnectTimeout(connectionTimeout);
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();

            if (stream != null) {
                result = readStream(stream);
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    public String postBytes(byte[] bytes) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        HttpURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(readTimeout);
            connection.setConnectTimeout(connectionTimeout);
            connection.setRequestMethod("POST");

            outputStream = connection.getOutputStream();
            outputStream.write(bytes);

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            // Retrieve the response body as an InputStream.
            inputStream = connection.getInputStream();

            if (inputStream != null) {
                // Converts Stream to String with max length of 500.
                result = readStream(inputStream);
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (inputStream != null) {
                inputStream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    public byte[] getBytes() throws IOException {
        InputStream stream = null;
        HttpURLConnection connection = null;
        byte[] result = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(readTimeout);
            connection.setConnectTimeout(connectionTimeout);
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();

            if (stream != null) {
                // Converts Stream to String with max length of 500.
                result = readByteStream(stream);
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    /**
     * Converts the contents of an InputStream to a String.
     */
    private String readStream(InputStream stream)
            throws IOException {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        char[] rawBuffer = new char[maxReadSize];
        int readSize;
        StringBuilder builder = new StringBuilder();
        while (((readSize = reader.read(rawBuffer)) != -1) && maxReadSize > 0) {
            if (readSize > maxReadSize) {
                readSize = maxReadSize;
            }
            builder.append(rawBuffer, 0, readSize);
            maxReadSize -= readSize;
        }
        return builder.toString();
    }

    /**
     * Converts the contents of an InputStream to a Byte array.
     */
    private byte[] readByteStream(InputStream stream)
            throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        byte[] buffer = new byte[maxReadSize];
        int len;

        // read bytes from the input stream and store them in buffer
        while ((len = stream.read(buffer)) != -1) {
            // write bytes from the buffer into output stream
            os.write(buffer, 0, len);
        }

        return os.toByteArray();
    }

    private URL makeUrl(String domain, String page, boolean ssl,
                        Pair<String, String>... parameters) throws MalformedURLException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http");
        if (ssl)
            stringBuilder.append('s');
        stringBuilder.append("://");
        stringBuilder.append(domain);
        stringBuilder.append(page);
        if (parameters.length > 0){
            stringBuilder.append('?');
            for (Pair<String, String> param:parameters){
                stringBuilder.append(param.first);
                stringBuilder.append(':');
                stringBuilder.append(param.second);
            }
        }
        return new URL(stringBuilder.toString());
    }
}
