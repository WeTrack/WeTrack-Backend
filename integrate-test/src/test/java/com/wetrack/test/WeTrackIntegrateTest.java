package com.wetrack.test;

import com.google.gson.Gson;
import com.wetrack.client.WeTrackClient;
import com.wetrack.client.config.Config;
import org.junit.Before;

public class WeTrackIntegrateTest extends WeTrackServerTest {
    protected WeTrackClient client;
    protected Gson gson = Config.gson();

    @Before
    public void setUp() throws Exception {
        super.setUp();
        client = new WeTrackClient(getBaseUri().toString(), 3);
    }
}
