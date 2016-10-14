package com.wetrack.client.test;

import com.wetrack.client.ResultMessageCallback;
import com.wetrack.client.model.Message;

public class MessageResponseTest extends EntityResponseTest<Message> {

    protected boolean successful;
    protected String receivedMessage;
    protected Throwable receivedException;

    protected ResultMessageCallback messageCallback(int successfulStatusCode) {
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
}
