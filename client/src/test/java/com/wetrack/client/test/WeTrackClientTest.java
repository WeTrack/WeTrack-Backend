package com.wetrack.client.test;

import com.google.gson.Gson;
import com.wetrack.client.WeTrackClient;
import com.wetrack.client.config.Config;
import com.wetrack.client.model.User;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;

public abstract class WeTrackClientTest {
    protected MockWebServer server;
    protected WeTrackClient client;
    protected Gson gson = Config.gson();

    protected String dummyToken = "1234567890abcdef1234567890abcdef";
    protected User robertPeng = new User("robert-peng", "robert-peng", "Robert Peng");
    protected User windyChan = new User("windy-chan", "windy-chan", "Windy Chan");
    protected User mrDai = new User("mr-dai", "mr-dai", "Mr.Dai");
    protected User littleHearth = new User("little-hearth", "little-hearth", "Little Hearth");

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
}
