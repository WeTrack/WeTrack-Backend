package com.wetrack.test;

import com.google.gson.Gson;
import com.wetrack.client.WeTrackClient;
import org.junit.Before;
import rx.schedulers.Schedulers;

import java.lang.reflect.Field;

public class WeTrackIntegrateTest extends WeTrackServerTest {
    protected WeTrackClient client;
    protected Gson gson;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        client = new WeTrackClient(getBaseUri().toString(), 3, Schedulers.immediate(), Schedulers.immediate());

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
}
