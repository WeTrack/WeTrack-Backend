package com.wetrack;

import com.google.gson.reflect.TypeToken;
import com.wetrack.client.model.Location;
import com.wetrack.client.test.EntityResponseHelper;
import com.wetrack.client.test.MessageResponseHelper;
import com.wetrack.test.WeTrackIntegrateTestWithUserLoggedIn;
import com.wetrack.util.ResourceUtils;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LocationsGetSinceTest extends WeTrackIntegrateTestWithUserLoggedIn {

    private EntityResponseHelper<List<Location>> entityHelper = new EntityResponseHelper<>(gson);
    private List<Location> testLocations;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        testLocations = gson.fromJson(ResourceUtils.readResource("example_locations.json"),
                new TypeToken<List<Location>>() {}.getType());
        MessageResponseHelper messageHelper = new MessageResponseHelper(200);
        client.uploadLocations(username, token, testLocations, messageHelper.callback());
        messageHelper.assertReceivedSuccessfulMessage();
    }

    @Test
    public void testGetSince() {
        LocalDateTime[] testSinceTimes = new LocalDateTime[] {
                LocalDateTime.parse("2016-10-24T09:30:00"),
                LocalDateTime.parse("2016-10-24T10:15:00"),
                LocalDateTime.parse("2016-10-24T10:35:00"),
                LocalDateTime.parse("2016-10-24T14:30:00"),
                LocalDateTime.parse("2016-10-24T17:45:00"),
                LocalDateTime.parse("2016-10-24T19:00:00")
        };

        for (LocalDateTime sinceTime : testSinceTimes) {
            client.getUserLocationsSince(username, sinceTime, entityHelper.callback(200));
            entityHelper.assertReceivedEntity(200);

            List<Location> receivedLocations = entityHelper.getReceivedEntity();
            List<Location> actualLocations = testLocations.stream()
                    .filter((l) -> l.getTime().compareTo(sinceTime) > 0)
                    .collect(Collectors.toList());

            assertThat(receivedLocations.size(), is(actualLocations.size()));
            for (int i = 0; i < receivedLocations.size(); i++) {
                Location receivedLocation = receivedLocations.get(i);
                Location actualLocation = actualLocations.get(i);
                assertThat(receivedLocation.getUsername(), is(username));
                assertThat(receivedLocation.getLongitude(), is(actualLocation.getLongitude()));
                assertThat(receivedLocation.getLatitude(), is(actualLocation.getLatitude()));
                assertThat(receivedLocation.getTime(), is(actualLocation.getTime()));
            }
        }
    }

}
