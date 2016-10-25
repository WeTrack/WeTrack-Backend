package com.wetrack.client;

public class ResultMessageCallback {
    private final int successStatusCode;

    public ResultMessageCallback(int successStatusCode) {
        this.successStatusCode = successStatusCode;
    }

    protected void onSuccess(String message) {}

    protected void onFail(String message, int failedStatusCode) {}

    protected void onError(Throwable ex) {}

    public int getSuccessStatusCode() {
        return successStatusCode;
    }

}
