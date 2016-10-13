package com.wetrack.client;

import retrofit2.Response;

public class AsyncCallback<T> {

    public void onReceive(T value) {}

    public void onResponse(Response<T> response) {}

    public void onException(Throwable ex) {}

    public void onErrorResponse(Response response) {}

}
