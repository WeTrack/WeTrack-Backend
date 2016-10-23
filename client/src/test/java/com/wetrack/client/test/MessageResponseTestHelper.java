package com.wetrack.client.test;

import com.wetrack.client.ResultMessageCallback;

public class MessageResponseTestHelper {

    private boolean successful;
    private String receivedMessage;
    private Throwable receivedException;

    public void assertReceivedMessage(boolean successful) {
        if (this.successful != successful) {
            if (successful) {
                if (receivedMessage != null)
                    throw new AssertionError("Expected to be successful, but failed with message `" +
                        receivedMessage + "`.");
                else if (receivedException != null)
                    throw new AssertionError("Expected to be successful, but failed with exception `" +
                        receivedException.getClass().getName() + ": " + receivedException.getMessage(), receivedException);
                else
                    throw new AssertionError("Expected to be successful, but failed.");
            } else {
                if (receivedMessage != null)
                    throw new AssertionError("Expected to be failed, but succeeded with message `" +
                        receivedMessage + "`");
                else
                    throw new AssertionError("Expected to be failed, but succeeded with no message received.");
            }
        }
        assertReceivedMessage();
    }

    public void assertReceivedMessage() {
        if (receivedMessage == null) {
            if (receivedException != null)
                throw new AssertionError("Expected to receive message, but received exception " +
                    receivedException.getClass().getName() + ": " + receivedException.getMessage(), receivedException);
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
