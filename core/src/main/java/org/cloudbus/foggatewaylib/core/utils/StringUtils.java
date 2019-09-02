package org.cloudbus.foggatewaylib.core.utils;

import androidx.annotation.NonNull;

/**
 * Utility functions for Strings.
 *
 * @author Riccardo Mancini
 */
public class StringUtils {
    /**
     * Returns a string containing the tokens joined by delimiters.
     * Copied from {@link android.text.TextUtils#join(CharSequence, Object[])}.
     *
     * @param delimiter a CharSequence that will be inserted between the tokens. If null, the string
     *     "null" will be used as the delimiter.
     * @param tokens an array objects to be joined. Strings will be formed from the objects by
     *     calling object.toString(). If tokens is null, a NullPointerException will be thrown. If
     *     tokens is an empty array, an empty string will be returned.
     *
     * @see android.text.TextUtils#join(CharSequence, Object[])
     */
    public static String join(@NonNull CharSequence delimiter, @NonNull Object[] tokens) {
        final int length = tokens.length;
        if (length == 0) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(tokens[0]);
        for (int i = 1; i < length; i++) {
            sb.append(delimiter);
            sb.append(tokens[i]);
        }
        return sb.toString();
    }
}
