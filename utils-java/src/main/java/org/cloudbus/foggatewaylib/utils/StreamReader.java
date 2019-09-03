package org.cloudbus.foggatewaylib.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class StreamReader {
    /**
     * Converts the contents of an InputStream to a {@link String} up to a maximum of
     * {@code maxReadSize}.
     *
     * @param stream the input stream.
     * @param maxReadSize the maximum stream size to read.
     * @return the converted {@code stream} as a {@link String}.
     * @see #readByteStream(InputStream)
     */
    public static String readStream(InputStream stream, int maxReadSize)
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
     * of {@code maxReadSize}.
     *
     * @param stream the input stream.
     * @param maxReadSize the maximum stream size to read.
     * @return the converted {@code stream} as a {@code byte[]}.
     * @see #readStream(InputStream)
     */
    public static byte[] readByteStream(InputStream stream, int maxReadSize)
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
}
