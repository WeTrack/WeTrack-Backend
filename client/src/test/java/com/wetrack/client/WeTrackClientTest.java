package com.wetrack.client;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import retrofit2.Response;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.fail;

abstract class WeTrackClientTest<T> {

    protected MockWebServer server;
    protected WeTrackClient client;

    protected T receivedEntity;
    protected int receivedStatusCode;
    protected Throwable receivedException;

    @Before
    public void setUp() throws IOException {
        server = new MockWebServer();
        server.start();

        client = new WeTrackClient(server.url("/").toString(), 3);
    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }

    protected AsyncCallback<T> callback(final int successStatusCode) {
        return new AsyncCallback<T>() {
            @Override
            protected void onReceive(T entity) {
                receivedEntity = entity;
                receivedStatusCode = successStatusCode;
                receivedException = null;
            }

            @Override
            protected void onErrorResponse(Response response) {
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

    protected String readResource(String filePath) throws Exception {
        URL resourceUrl = getClass().getClassLoader().getResource(filePath);
        if (resourceUrl == null)
            fail("Failed to find `" + filePath + "` in resources folder. Check if it is deleted.");
        return new String(Files.readAllBytes(Paths.get(resourceUrl.toURI())));
    }
}
