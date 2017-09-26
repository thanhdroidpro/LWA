package com.kinglloy.album.exception;

import android.content.Context;


import com.kinglloy.album.R;
import com.kinglloy.album.data.exception.NetworkConnectionException;
import com.kinglloy.album.data.exception.RemoteServerException;

import java.net.SocketTimeoutException;

/**
 * @author jinyalin
 * @since 2017/4/29.
 */

public class ErrorMessageFactory {
    private ErrorMessageFactory() {
        //empty
    }

    /**
     * Creates a String representing an error message.
     *
     * @param context   Context needed to retrieve string resources.
     * @param exception An exception used as a condition to retrieve the correct error message.
     * @return {@link String} an error message.
     */
    public static String create(Context context, Exception exception) {
        String message = context.getString(R.string.exception_message_generic);

        if (exception instanceof NetworkConnectionException) {
            message = context.getString(R.string.exception_message_no_connection);
        } else if (exception instanceof SocketTimeoutException) {
            message = context.getString(R.string.exception_message_remote_service);
        } else if (exception instanceof RemoteServerException) {
            message = context.getString(R.string.exception_message_remote_service);
        }
        return message;
    }
}
