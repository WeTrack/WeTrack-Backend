package com.wetrack.client.test;

import com.google.gson.Gson;
import com.wetrack.client.Callback;
import com.wetrack.client.model.Message;
import retrofit2.Response;

import java.io.IOException;

public class EntityResponseTestHelper<T> {
    private Gson gson;

    private T receivedEntity;
    private int receivedStatusCode;
    private Throwable receivedException;
    private Message receivedErrorMessage;

    public EntityResponseTestHelper(Gson gson) {
        this.gson = gson;
    }

    public void assertReceivedEntity(int receivedStatusCode) {
        if (this.receivedStatusCode != receivedStatusCode) {
            if (receivedErrorMessage != null)
                throw new AssertionError("Expected received status code: " + receivedStatusCode +
                        "\nActual received status code: " + this.receivedStatusCode +
                        "\nWith error message: " + receivedErrorMessage.getMessage());
            else if (receivedException != null)
                throw new AssertionError("Expected received status code: " + receivedStatusCode +
                        "\nActual received status code: " + this.receivedStatusCode +
                        "\nWith exception: " + receivedException.getClass().getName() + ": " +
                        receivedException.getMessage(), receivedException);
            throw new AssertionError("Expected received status code: " + receivedStatusCode +
                    "\nActual received status code: " + this.receivedStatusCode);
        }
        assertReceivedEntity();
    }

    public void assertReceivedErrorMessage(int receivedStatusCode) {
        if (this.receivedStatusCode != receivedStatusCode)
            throw new AssertionError("Expected received status code: " + receivedStatusCode +
                    "\nActual received status code: " + this.receivedStatusCode);
        assertReceivedErrorMessage();
    }

    public void assertReceivedEntity() {
        if (receivedEntity == null) {
            if (receivedErrorMessage != null)
                throw new AssertionError("Expected to receive entity, but received error message: "
                        + receivedStatusCode + " " + receivedErrorMessage.getMessage());
            else if (receivedException != null)
                throw new AssertionError("Expected to receive entity, but received exception "
                        + receivedException.getClass().getName() + ": " + receivedException.getMessage());
            else
                throw new AssertionError("Expected to receive entity but received nothing.");
        }
    }

    public void assertReceivedErrorMessage() {
        if (receivedErrorMessage == null) {
            if (receivedEntity != null)
                throw new AssertionError("Expected to receive error message, but received entity: "
                        + receivedEntity.toString());
            else if (receivedException != null)
                throw new AssertionError("Expected to receive error message, but received exception "
                        + receivedException.getClass().getName() + ": " + receivedException.getMessage());
            else
                throw new AssertionError("Expected to receive error message but received nothing.");
        }
    }

    public Callback<T> callback(final int successStatusCode) {
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
                    String receivedErrorBody = response.errorBody().string();
                    receivedErrorMessage = gson.fromJson(receivedErrorBody, Message.class);
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

    public T getReceivedEntity() {
        return receivedEntity;
    }

    public int getReceivedStatusCode() {
        return receivedStatusCode;
    }

    public Throwable getReceivedException() {
        return receivedException;
    }

    public Message getReceivedErrorMessage() {
        return receivedErrorMessage;
    }
}
