package com.wetrack.test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wetrack.config.SpringConfig;
import com.wetrack.config.WeTrackApplication;
import com.wetrack.model.CreatedMessage;
import com.wetrack.model.Message;
import com.wetrack.model.User;
import com.wetrack.util.GsonJerseyProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;
import java.util.Map;

public abstract class WeTrackServerTest extends JerseyTest {
    private static final Logger LOG = LoggerFactory.getLogger(WeTrackServerTest.class);

    protected String notExistEntityId = "1234567890abcdef1234567890abcdef";

    protected Gson gson = new SpringConfig().gson();

    protected Type userListType = new TypeToken<List<User>>() {}.getType();

    @Override
    protected Application configure() {
        return new WeTrackApplication();
    }

    @Override
    protected void configureClient(final ClientConfig config) {
        config.register(GsonJerseyProvider.class);
    }

    protected Response get(String url, QueryParam... queryParams) {
        WebTarget requestTarget = target(url);
        for (QueryParam queryParam : queryParams)
            requestTarget = requestTarget.queryParam(queryParam.getName(), queryParam.getValue());
        Invocation getRequest = requestTarget.request(MediaType.APPLICATION_JSON_TYPE).buildGet();
        Response response = getRequest.invoke();
        response.bufferEntity();
        return response;
    }

    protected Response head(String url, QueryParam... queryParams) {
        WebTarget requestTarget = target(url);
        for (QueryParam queryParam : queryParams)
            requestTarget = requestTarget.queryParam(queryParam.getName(), queryParam.getValue());
        Invocation headRequest = requestTarget.request(MediaType.APPLICATION_JSON_TYPE).build("HEAD");
        Response response = headRequest.invoke();
        response.bufferEntity();
        return response;
    }

    protected Response delete(String url, QueryParam... queryParams) {
        WebTarget requestTarget = target(url);
        for (QueryParam queryParam : queryParams)
            requestTarget = requestTarget.queryParam(queryParam.getName(), queryParam.getValue());
        Invocation headRequest = requestTarget.request(MediaType.APPLICATION_JSON_TYPE).build("DELETE");
        Response response = headRequest.invoke();
        response.bufferEntity();
        return response;
    }

    protected Response post(String url, String entity, MediaType mediaType, QueryParam... queryParams) {
        Entity requestEntity = Entity.entity(entity, mediaType);
        WebTarget requestTarget = target(url);
        for (QueryParam queryParam : queryParams)
            requestTarget = requestTarget.queryParam(queryParam.getName(), queryParam.getValue());
        Invocation postRequest = requestTarget.request(MediaType.APPLICATION_JSON_TYPE).buildPost(requestEntity);
        Response response = postRequest.invoke();
        response.bufferEntity();
        return response;
    }

    protected <T> Response post(String url, T entity, QueryParam... queryParams) {
        Entity requestEntity = Entity.entity(entity, MediaType.APPLICATION_JSON_TYPE);
        WebTarget requestTarget = target(url);
        for (QueryParam queryParam : queryParams)
            requestTarget = requestTarget.queryParam(queryParam.getName(), queryParam.getValue());
        Invocation postRequest = requestTarget.request(MediaType.APPLICATION_JSON_TYPE).buildPost(requestEntity);
        Response response = postRequest.invoke();
        response.bufferEntity();
        return response;
    }

    protected <T> Response put(String url, T entity, QueryParam... queryParams) {
        Entity requestEntity = Entity.entity(entity, MediaType.APPLICATION_JSON_TYPE);
        WebTarget requestTarget = target(url);
        for (QueryParam queryParam : queryParams)
            requestTarget = requestTarget.queryParam(queryParam.getName(), queryParam.getValue());
        Invocation putRequest = requestTarget.request(MediaType.APPLICATION_JSON_TYPE).buildPut(requestEntity);
        Response response = putRequest.invoke();
        response.bufferEntity();
        return response;
    }

    protected void logResponse(Response response, String event) {
        if (LOG.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder(String.valueOf(response.getStatus()));
            for (Map.Entry<String, List<String>> entry : response.getStringHeaders().entrySet()) {
                String key = entry.getKey();
                for (String value : entry.getValue())
                    builder.append('\n').append(key).append(": ").append(value);
            }
            builder.append("\n\n").append(response.readEntity(String.class));
            LOG.debug("Received response on {}:\n==========================\n{}\n==========================",
                    event, builder.toString());
        }
    }

    protected void assertReceivedEmptyResponse(Response response, int expectedStatusCode) {
        if (response.getStatus() != expectedStatusCode)
            throw new AssertionError("Expected status code: " + expectedStatusCode +
                    "\nActual status code: " + response.getStatus());
        String responseBody = response.readEntity(String.class);
        if (responseBody != null && !responseBody.trim().isEmpty())
            throw new AssertionError("Expected to receive empty body, but received:\n" + responseBody);
    }

    protected void assertReceivedCreatedMessage(Response response) {
        if (response.getStatus() != 201)
            throw new AssertionError("Expected to received status code `201 Created`, but received: "
                    + response.getStatus());
        URI locationUri = response.getLocation();
        String locationHeader = locationUri.getPath();
        if (locationHeader == null || locationHeader.trim().isEmpty())
            throw new AssertionError("`Location` header of the received response is empty.");

        String responseEntity = response.readEntity(String.class);
        CreatedMessage createdResponse = gson.fromJson(responseEntity, CreatedMessage.class);
        if (createdResponse.getEntityUrl() == null || createdResponse.getEntityUrl().trim().isEmpty())
            throw new AssertionError("`entity_url` field of the received created message is empty.");
        if (!locationHeader.equals(createdResponse.getEntityUrl()))
            throw new AssertionError("`Location` header field and `entity_url` message field mismatch.\n"
                    + "`Location` header: " + locationHeader + "\n"
                    + "`entity_url` field: " + createdResponse.getEntityUrl());
    }

    protected void assertReceivedCreatedMessage(Response response, String newEntityUrl) {
        assertReceivedCreatedMessage(response);

        URI locationUri = response.getLocation();
        String locationHeader = locationUri.getPath();
        if (!locationHeader.equals(newEntityUrl))
            throw new AssertionError("Expected new entity URL: " + newEntityUrl
                + "\nActual received `Location` header: " + locationHeader);
    }

    protected <T> T assertReceivedEntity(Response response, int expectedStatusCode, Type expectedEntityType) {
        if (response.getStatus() != expectedStatusCode)
            throw new AssertionError("Expected status code: " + expectedStatusCode +
                    "\nActual status code: " + response.getStatus());
        return assertReceivedEntity(response, expectedEntityType);
    }

    protected void assertReceivedNonemptyMessage(Response response, int expectedStatusCode) {
        if (response.getStatus() != expectedStatusCode)
            throw new AssertionError("Expected status code: " + expectedStatusCode +
                "\nActual status code: " + response.getStatus());
        assertReceivedNonemptyMessage(response);
    }

    protected <T> T assertReceivedEntity(Response response, Type expectedEntityType) {
        String responseEntity = response.readEntity(String.class);
        T receivedEntity = gson.fromJson(responseEntity, expectedEntityType);
        if (receivedEntity == null)
            throw new AssertionError("Expected to receive entity but received nothing.");
        return receivedEntity;
    }

    protected void assertReceivedNonemptyMessage(Response response) {
        String responseEntity = response.readEntity(String.class);
        Message message = gson.fromJson(responseEntity, Message.class);
        if (message == null)
            throw new AssertionError("Expected to receive message but received nothing.");

        if (message.getStatusCode() != response.getStatus())
            throw new AssertionError("Status code mismatch.\nStatus code in received message: " +
                    message.getStatusCode() + "\nActual status code: " + response.getStatus());
        if (message.getMessage() == null || message.getMessage().trim().isEmpty())
            throw new AssertionError("`message` field of received `Message` object is empty.");
    }

}
