package com.wetrack;

import com.google.gson.reflect.TypeToken;
import com.wetrack.client.model.Location;
import com.wetrack.client.test.EntityResponseHelper;
import com.wetrack.client.test.MessageResponseHelper;
import com.wetrack.test.WeTrackIntegrateTestWithUserLoggedIn;
import com.wetrack.util.ResourceUtils;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LocationLatestGetTest extends WeTrackIntegrateTestWithUserLoggedIn {

    private EntityResponseHelper<Location> entityHelper = new EntityResponseHelper<>(gson);

    @Test
    public void testLocationLatestGetWithErrorResponse() {
        client.getUserLatestLocation(username, entityHelper.callback(200));
        entityHelper.assertReceivedErrorMessage(404);
    }

    @Test
    public void testLocationLatestGet() {
        List<Location> testLocations = gson.fromJson(ResourceUtils.readResource("example_locations.json"),
                new TypeToken<List<Location>>() {}.getType());
        MessageResponseHelper messageHelper = new MessageResponseHelper(200);
        client.uploadLocations(username, token, testLocations, messageHelper.callback());
        messageHelper.assertReceivedSuccessfulMessage();

        client.getUserLatestLocation(username, entityHelper.callback(200));
        entityHelper.assertReceivedEntity(200);
        Location receivedLocation = entityHelper.getReceivedEntity();
        Location actualLocation = testLocations.stream()
                .sorted((l1, l2) -> l2.getTime().compareTo(l1.getTime()))
                .findFirst().get();
        assertThat(receivedLocation.getUsername(), is(username));
        assertThat(receivedLocation.getLongitude(), is(actualLocation.getLongitude()));
        assertThat(receivedLocation.getLatitude(), is(actualLocation.getLatitude()));
        assertThat(receivedLocation.getTime(), is(actualLocation.getTime()));
    }

}
