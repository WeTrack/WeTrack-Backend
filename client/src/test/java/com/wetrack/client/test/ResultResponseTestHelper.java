package com.wetrack.client.test;

import com.wetrack.client.ResultCallback;

public class ResultResponseTestHelper {

    private boolean successful;
    private Throwable receivedException;

    public void assertSucceeded() {
        if (!successful) {
            if (receivedException != null)
                throw new AssertionError("Expected to be successful, but failed with exception " +
                    receivedException.getClass().getName() + ": " + receivedException.getMessage());
            else
                throw new AssertionError("Expected to be successful, but failed.");
        }
    }

    public void assertFailed() {
        if (successful)
            throw new AssertionError("Expected to be failed, but succeeded.");
    }

    public ResultCallback callback(final int successStatusCode) {
        return new ResultCallback(successStatusCode) {
            @Override
            protected void onSuccess() {
                successful = true;
                receivedException = null;
            }

            @Override
            protected void onFail() {
                successful = false;
                receivedException = null;
            }

            @Override
            protected void onError(Throwable ex) {
                successful = false;
                receivedException = ex;
            }
        };
    }

    public boolean isSuccessful() {
        return successful;
    }

    public Throwable getReceivedException() {
        return receivedException;
    }
}
