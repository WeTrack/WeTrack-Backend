package com.wetrack.client.test;

import com.wetrack.client.Callback;
import com.wetrack.client.model.Message;
import retrofit2.Response;

import java.io.IOException;

public abstract class EntityResponseTest<T> extends WeTrackClientTest {

    protected T receivedEntity;
    protected int receivedStatusCode;
    protected Throwable receivedException;
    protected Message receivedErrorMessage;

    protected Callback<T> callback(final int successStatusCode) {
        return new Callback<T>() {
            @Override
            protected void onReceive(T entity) {
                receivedEntity = entity;
                receivedStatusCode = successStatusCode;
                receivedException = null;
            }

            @Override
            protected void onErrorResponse(Response response) {
                try {
                    String recievedErrorBody = response.errorBody().string();
                    receivedErrorMessage = gson.fromJson(recievedErrorBody, Message.class);
                } catch (IOException e) {
                    receivedErrorMessage = null;
                }
                receivedEntity = null;
                receivedStatusCode = response.code();
                receivedException = null;
            }

            @Override
            protected void onException(Throwable ex) {
                receivedEntity = null;
                receivedStatusCode = -1;
                receivedException = ex;
            }
        };
    }
}
