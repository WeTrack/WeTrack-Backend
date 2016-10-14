package com.wetrack.client.test;

import com.google.gson.Gson;
import com.wetrack.client.WeTrackClient;
import com.wetrack.client.config.Config;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.fail;

public abstract class WeTrackClientTest {
    protected MockWebServer server;
    protected WeTrackClient client;
    protected Gson gson = Config.gson();

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

    protected String readResource(String filePath) throws Exception {
        URL resourceUrl = getClass().getClassLoader().getResource(filePath);
        if (resourceUrl == null)
            fail("Failed to find `" + filePath + "` in resources folder. Check if it is deleted.");
        return new String(Files.readAllBytes(Paths.get(resourceUrl.toURI())));
    }
}
