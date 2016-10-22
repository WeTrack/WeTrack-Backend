package com.wetrack.test;

import com.google.gson.Gson;
import com.wetrack.JerseyTest;
import com.wetrack.client.WeTrackClient;
import com.wetrack.client.config.Config;
import org.junit.Before;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.fail;

public class WeTrackIntegrateTest extends JerseyTest {
    protected WeTrackClient client;
    protected Gson gson = Config.gson();

    @Before
    public void setUp() throws Exception {
        super.setUp();
        client = new WeTrackClient(getBaseUri().toString(), 3);
    }

    protected String readResource(String filePath) throws Exception {
        URL resourceUrl = getClass().getClassLoader().getResource(filePath);
        if (resourceUrl == null)
            fail("Failed to find `" + filePath + "` in resources folder. Check if it is deleted.");
        return new String(Files.readAllBytes(Paths.get(resourceUrl.toURI())));
    }
}
