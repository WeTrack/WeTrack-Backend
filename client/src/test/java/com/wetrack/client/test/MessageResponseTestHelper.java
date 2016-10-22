package com.wetrack.client.test;

import com.wetrack.client.ResultMessageCallback;

public class MessageResponseTestHelper {

    private boolean successful;
    private String receivedMessage;
    private Throwable receivedException;

    public void assertReceivedMessage(boolean successful) {
        if (this.successful != successful) {
            if (successful)
                throw new AssertionError("Expected to be successful, but failed.");
            else
                throw new AssertionError("Expected to be failed, but succeeded.");
        }
        assertReceivedMessage();
    }

    public void assertReceivedMessage() {
        if (receivedMessage == null) {
            if (receivedException != null)
                throw new AssertionError("Expected to receive message, but received exception " +
                    receivedException.getClass().getName() + ": " + receivedException.getMessage());
            else
                throw new AssertionError("Expected to receive message, but received nothing.");
        }
    }

    public ResultMessageCallback callback(int successfulStatusCode) {
        return new ResultMessageCallback(successfulStatusCode) {
            @Override
            protected void onSuccess(String message) {
                successful = true;
                receivedMessage = message;
                receivedException = null;
            }

            @Override
            protected void onFail(String message) {
                successful = false;
                receivedMessage = message;
                receivedException = null;
            }

            @Override
            protected void onError(Throwable ex) {
                successful = false;
                receivedMessage = null;
                receivedException = ex;
            }
        };
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getReceivedMessage() {
        return receivedMessage;
    }

    public Throwable getReceivedException() {
        return receivedException;
    }
}
