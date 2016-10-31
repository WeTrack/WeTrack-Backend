package com.wetrack.client;

public class CreatedMessageCallback {

    protected void onSuccess(String newEntityId, String message) {}

    protected void onFail(String message, int failedStatusCode) {}

    protected void onError(Throwable ex) {}

}
