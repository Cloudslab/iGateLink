package org.cloudbus.foggatewaylib.core.utils;

import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Simplified connection management for HTTP GET/POST requests.
 * The same {@link SimpleHttpConnection} could be used for multiple requests to the same
 * {@link URL}.
 *
 * @see HttpURLConnection
 *
 * @author Riccardo Mancini
 */
public class SimpleHttpConnection {
    private int maxReadSize = 5000000;
    private int connectionTimeout = 5000;
    private int readTimeout = 5000;

    private URL url;

    /**
     * Constructor that initializes a new request to the specified url.
     *
     * @param url the url of the request.
     */
    public SimpleHttpConnection(@NonNull URL url){
        this.url = url;
    }

    /**
     * Constructor that initializes a new request to the url built using
     * {@link #makeUrl(String, String, boolean, Pair[])}.
     *
     * @param domain the domain of the web server.
     * @param page the page to request from the web server.
     * @param parameters additional GET parameters.
     * @throws MalformedURLException refer to {@link #makeUrl(String, String, boolean, Pair[])}.
     * @see #makeUrl(String, String, boolean, Pair[])
     */
    public SimpleHttpConnection(String domain, String page, Map<String, String> parameters)
            throws MalformedURLException{
        this(makeUrl(domain, page, false, parameters));
    }

    /**
     * Sets the maximum size of the response from the server.
     * Default is {@code 5000000}.
     *
     * @param maxReadSize the new maximum size in bytes.
     * @see #getMaxReadSize()
     * @see #readStream(InputStream)
     * @see #readByteStream(InputStream)
     */
    public void setMaxReadSize(int maxReadSize) {
        this.maxReadSize = maxReadSize;
    }

    /**
     * Gets the maximum read size (default is {@code 5000000} bytes).
     *
     * @return the previously set maximum read size in bytes or {@code 5000000}.
     * @see #setMaxReadSize(int)
     * @see #readStream(InputStream)
     * @see #readByteStream(InputStream)
     */
    public int getMaxReadSize() {
        return maxReadSize;
    }

    /**
     * Sets the connection timeout.
     * {@code 0} means infinite timeout.
     * Default is 5 seconds.
     * Refer to {@link HttpURLConnection#setConnectTimeout(int)} for more details.
     *
     * @param connectionTimeout the new connection timeout in milliseconds.
     * @see HttpURLConnection#setConnectTimeout(int)
     * @see #getConnectionTimeout()
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * Gets the connection timeout.
     * {@code 0} means infinite timeout.
     * Default is 5 seconds.
     *
     * @return the previously set connection timeout in milliseconds or {@code 5000}.
     * @see HttpURLConnection#getConnectTimeout()
     * @see #setConnectionTimeout(int)
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Sets the read timeout.
     * {@code 0} means infinite timeout.
     * Default is 5 seconds.
     * Refer to {@link HttpURLConnection#setReadTimeout(int)} for more details.
     *
     * @param readTimeout the new read timeout in milliseconds.
     * @see HttpURLConnection#setReadTimeout(int)
     * @see #getReadTimeout()
     */
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    /**
     * Gets the read timeout.
     * {@code 0} means infinite timeout.
     * Default is 5 seconds.
     *
     * @return the previously set read timeout in milliseconds or {@code 5000}.
     * @see HttpURLConnection#getReadTimeout()
     * @see #setReadTimeout(int)
     */
    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * Gets the {@link URL} of this connection.
     *
     * @return the {@link URL} of this connection.
     * @see #SimpleHttpConnection(URL)
     * @see #getStringUrl()
     */
    @NonNull
    public URL getUrl() {
        return url;
    }

    /**
     * Gets the {@link URL} of this connection as a {@link String}.
     *
     * @return the {@code url} of this connection as a {@link String}.
     * @see #SimpleHttpConnection(URL)
     * @see URL#toString()
     * @see #getUrl()
     */
    public String getStringUrl() {
        return url.toString();
    }

    /**
     * Sets up the connection (timeouts and method).
     *
     * @param connection the connection
     * @param method the method to be used: {@code "GET"} or {@code "POST"}.
     * @return the response of the server as an {@link InputStream}.
     * @throws IOException in case of an HTTP error.
     * @see #get()
     * @see #getBytes()
     */
    private void setupConnection(HttpURLConnection connection, String method) throws IOException{
        connection.setReadTimeout(readTimeout);
        connection.setConnectTimeout(connectionTimeout);
        connection.setRequestMethod(method);
    }

    /**
     * Makes a GET request to the server, returning the response as a {@link String}.
     *
     * @return the response of the server as a {@link String}.
     * @throws IOException in case of an HTTP error.
     * @see #setupConnection(HttpURLConnection, String)
     * @see #getBytes()
     */
    public String get() throws IOException {
        HttpURLConnection connection = null;
        InputStream stream = null;
        String result = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            setupConnection(connection, "GET");

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            stream = connection.getInputStream();

            if (stream != null) {
                result = readStream(stream);
            }
        } finally {
            if (connection != null){
                connection.disconnect();
            }
            if (stream != null) {
                stream.close();
            }
        }
        return result;
    }

    /**
     * Makes a GET request to the server, returning the response as a byte array.
     *
     * @return the response of the server as a byte array ({@code byte[]}).
     * @throws IOException in case of an HTTP error.
     * @see #setupConnection(HttpURLConnection, String)
     * @see #get()
     */
    public byte[] getBytes() throws IOException {
        HttpURLConnection connection = null;
        InputStream stream = null;
        byte[] result = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            setupConnection(connection, "GET");

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            stream = connection.getInputStream();
            if (stream != null) {
                result = readByteStream(stream);
            }
        } finally {
            if (connection != null){
                connection.disconnect();
            }
            if (stream != null) {
                stream.close();
            }
        }
        return result;
    }

    //TODO post(...) for simple key-value pair post request

    /**
     * Posts the given {@code bytes} to the server as a raw POST request.
     *
     * @param bytes the {@code bytes} to be sent.
     * @return the response of the server as a {@link String}.
     * @throws IOException in case of an HTTP error.
     */
    public String postBytes(byte[] bytes) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        HttpURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            setupConnection(connection, "POST");

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

    /**
     * Converts the contents of an InputStream to a {@link String} up to a maximum of
     * {@link #maxReadSize}.
     *
     * @param stream the input stream.
     * @return the converted {@code stream} as a {@link String}.
     * @see #setMaxReadSize(int)
     * @see #readByteStream(InputStream)
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
     * Converts the contents of an InputStream to a byte array ({@code byte[]}) up to a maximum
     * of {@link #maxReadSize}.
     *
     * @param stream the input stream.
     * @return the converted {@code stream} as a {@code byte[]}.
     * @see #setMaxReadSize(int)
     * @see #readStream(InputStream)
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

    /**
     * Builds an URL from domain, page and key-value parameters.
     * The URL is built as follows:
     * http[s]://$domain/$page[?$key1=$val1&$key2=$val2&...&$keyN=$valN]
     *
     * @param domain the domain of the server.
     * @param page the page to request from the server.
     * @param ssl {@code true} if ssl should be enabled, false otherwise.
     * @param parameters key-value map for the parameters or null if there are no parameters.
     * @return the built URL.
     * @throws MalformedURLException refer to {@link URL#URL(String)}.
     * @see URL#URL(String)
     * @see #makeUrl(String, String, boolean)
     */
    private static URL makeUrl(String domain, String page, boolean ssl,
                        @Nullable Map<String, String> parameters) throws MalformedURLException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http");
        if (ssl)
            stringBuilder.append('s');
        stringBuilder.append("://");
        stringBuilder.append(domain);
        stringBuilder.append(page);
        if (parameters != null && !parameters.isEmpty()){
            stringBuilder.append('?');
            Iterator<String> i = parameters.keySet().iterator();
            while (i.hasNext()){
                String key = i.next();
                String value = parameters.get(key);
                stringBuilder.append(key);
                stringBuilder.append('=');
                stringBuilder.append(value);
                if (i.hasNext())
                    stringBuilder.append('&');
            }
        }
        return new URL(stringBuilder.toString());
    }

    /**
     * Builds an URL from domain and page.
     * The URL is built as follows:
     * http[s]://$domain/$page
     *
     * @param domain the domain of the server.
     * @param page the page to request from the server.
     * @param ssl {@code true} if ssl should be enabled, false otherwise.
     * @return the built URL.
     * @throws MalformedURLException refer to {@link URL#URL(String)}.
     * @see URL#URL(String)
     * @see #makeUrl(String, String, boolean, Map)
     */
    private static URL makeUrl(String domain, String page, boolean ssl)
            throws MalformedURLException {
        return makeUrl(domain, page, ssl, null);
    }
}
