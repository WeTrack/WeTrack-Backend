package com.wetrack.client.test;

import com.google.gson.Gson;
import com.wetrack.client.WeTrackClient;
import com.wetrack.client.model.User;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class WeTrackClientTest {
    protected MockWebServer server;
    protected WeTrackClient client;
    protected Gson gson;

    protected String dummyToken = "1234567890abcdef1234567890abcdef";
    protected User robertPeng = new User("robert-peng", "robert-peng", "Robert Peng");
    protected User windyChan = new User("windy-chan", "windy-chan", "Windy Chan");
    protected User mrDai = new User("mr-dai", "mr-dai", "Mr.Dai");
    protected User littleHearth = new User("little-hearth", "little-hearth", "Little Hearth");

    @Before
    public void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        client = new WeTrackClient(server.url("/").toString(), 3, Schedulers.immediate(), Schedulers.immediate());

        try {
            Field gsonField = WeTrackClient.class.getDeclaredField("gson");
            if (!gsonField.isAccessible())
                gsonField.setAccessible(true);
            gson = (Gson) gsonField.get(client);
        } catch (NoSuchFieldException ex) {
            throw new AssertionError("Cannot find field `gson` in class `WeTrackClient`.");
        } catch (IllegalAccessException ex) {
            throw new AssertionError("Failed to access `gson` field of `WeTrackClient`.");
        }
    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }

    protected String readResource(String fileName) {
        URL resourceUrl = getClass().getClassLoader().getResource(fileName);
        if (resourceUrl == null)
            throw new AssertionError("Failed to find `" + fileName + "` in resources folder. Check if it is deleted.");
        try {
            return new String(Files.readAllBytes(Paths.get(resourceUrl.toURI())));
        } catch (URISyntaxException ex) {
            throw new AssertionError("Failed to convert URL `" + resourceUrl.toString() + "` to URI.", ex);
        } catch (IOException ex) {
            throw new AssertionError("Failed to read from `" + resourceUrl.toString() + "`.", ex);
        }
    }
}
