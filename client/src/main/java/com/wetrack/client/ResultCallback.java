package com.wetrack.client;

public class ResultCallback {
    private final int successStatusCode;

    public ResultCallback(int successStatusCode) {
        this.successStatusCode = successStatusCode;
    }

    protected void onSuccess() {}

    protected void onFail() {}

    protected void onError(Throwable ex) {}

    public int getSuccessStatusCode() {
        return successStatusCode;
    }
}
