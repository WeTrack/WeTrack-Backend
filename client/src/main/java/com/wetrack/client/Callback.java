package com.wetrack.client;

import retrofit2.Response;

/**
 * Asynchronous callback object for methods in {@link WeTrackClient}, with four callback methods and its
 * default implementations (which do nothing at all).
 *
 * @see WeTrackClient
 * @see #onReceive(Object)
 * @see #onResponse(Response)
 * @see #onException(Throwable)
 * @see #onErrorResponse(Response)
 *
 * @param <T> the expected type of the response entity
 */
public class Callback<T> {

    /**
     * Invoked on entity successfully received from response body. This method will be invoked
     * after {@link #onResponse(Response)} if the status code of the response if {@code 200}.
     *
     * @param value the received entity.
     */
    protected void onReceive(T value) {}

    /**
     * Invoked on response received from server. This method will always be invoked unless
     * there is an exception occurred when connecting to the server.
     *
     * @param response the received raw response.
     */
    protected void onResponse(Response<T> response) {}

    /**
     * Invoked when there is an exception occurred when connecting to the server.
     *
     * @param ex the exception occurred when connecting to the server.
     */
    protected void onException(Throwable ex) {}

    /**
     * Invoked on response received from server and the status code of the response is not {@code 200}
     * (receiving a error response).
     *
     * @param response the received raw response.
     */
    protected void onErrorResponse(Response<T> response) {}

}
