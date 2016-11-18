package com.wetrack;

import com.google.gson.reflect.TypeToken;
import com.wetrack.client.model.Location;
import com.wetrack.client.test.MessageResponseHelper;
import com.wetrack.test.WeTrackIntegrateTestWithUserLoggedIn;
import com.wetrack.util.ResourceUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LocationUploadTest extends WeTrackIntegrateTestWithUserLoggedIn {

    private MessageResponseHelper messageHelper = new MessageResponseHelper(200);

    private List<Location> testLocations;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        testLocations = gson.fromJson(ResourceUtils.readResource("example_locations.json"),
                new TypeToken<List<Location>>(){}.getType());
    }

    @Test
    public void testLocationUploadWithInvalidToken() {
        client.uploadLocations(username, "", testLocations, messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(400); // Bad Request for empty token
        client.uploadLocations(username, "Surprise, motherf*cker", testLocations, messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(401); // Unauthorized
    }

    @Test
    public void testLocationUpload() {
        client.uploadLocations(username, token, testLocations, messageHelper.callback());
        messageHelper.assertReceivedSuccessfulMessage();

        assertThat(messageHelper.getReceivedMessage(), is("Received 5 locations."));
    }

}
