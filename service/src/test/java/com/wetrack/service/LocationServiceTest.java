package com.wetrack.service;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wetrack.model.Location;
import com.wetrack.test.QueryParam;
import com.wetrack.test.WeTrackServerTestWithUserLoggedIn;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.wetrack.util.ResourceUtils.readResource;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LocationServiceTest extends WeTrackServerTestWithUserLoggedIn {
    private Type locationListType = new TypeToken<List<Location>>(){}.getType();

    @Test
    public void testLocationUploadInvalidToken() {
        JsonObject requestEntity = new JsonObject();
        requestEntity.addProperty("token", "Surprise, motherf*cker!");
        requestEntity.addProperty("locations", "[]");
        Response response = post("/users/" + robertPeng.getUsername() + "/locations", requestEntity.toString());

        logResponse(response, "location upload with invalid token");
        assertReceivedNonemptyMessage(response, 401);
    }

    @Test
    public void testLocationUploadAndGetWithStandardInput() {
        String testInput = readResource("location_service/standard.json");

        JsonObject requestEntity = new JsonObject();
        requestEntity.addProperty("token", tokenOf(robertPeng));
        requestEntity.addProperty("locations", testInput);
        Response response = post("/users/" + robertPeng.getUsername() + "/locations", requestEntity.toString());

        logResponse(response, "standard locations upload");
        assertReceivedNonemptyMessage(response, 200);

        testLocationGet(gson.fromJson(testInput, locationListType));
    }

    @Test
    public void testLocationUploadAndGetWithNonstandardInput() {
        String testInput = readResource("location_service/nonstandard.json");

        JsonObject requestEntity = new JsonObject();
        requestEntity.addProperty("token", tokenOf(robertPeng));
        requestEntity.addProperty("locations", testInput);
        Response response = post("/users/" + robertPeng.getUsername() + "/locations", requestEntity.toString());

        logResponse(response, "nonstandard locations upload");
        assertReceivedNonemptyMessage(response, 200);

        testLocationGet(gson.fromJson(testInput, locationListType));
    }

    private void testLocationGet(List<Location> testLocations) {
        LocalDateTime sinceTime = LocalDateTime.of(2016, 10, 24, 17, 45, 0);
        Response response = get("/users/" + robertPeng.getUsername() + "/locations", QueryParam.of("since", sinceTime.toString()));

        logResponse(response, "locations get since `2016-10-24T17:45:00`");
        List<Location> actualLocations = testLocations.stream()
                .filter((l) -> l.getTime().isAfter(sinceTime))
                .collect(Collectors.toList());
        List<Location> receivedLocations = assertReceivedEntity(response, 200, locationListType);
        assertThat(receivedLocations.size(), is(actualLocations.size()));
        for (int i = 0; i < actualLocations.size(); i++) {
            Location actualLocation = actualLocations.get(i);
            Location receivedLocation = receivedLocations.get(i);
            assertThat(receivedLocation.getUsername(), is(robertPeng.getUsername()));
            assertThat(receivedLocation.getTime(), is(actualLocation.getTime()));
            assertThat(receivedLocation.getPoint(), is(actualLocation.getPoint()));
        }

        Location latestLocation = testLocations.stream()
                .sorted((l1, l2) -> l2.getTime().compareTo(l1.getTime()))
                .findFirst().get();
        response = get("/users/" + robertPeng.getUsername() + "/locations/latest");

        logResponse(response, "latest location get");
        Location receivedLocation = assertReceivedEntity(response, 200, Location.class);
        assertThat(receivedLocation.getUsername(), is(robertPeng.getUsername()));
        assertThat(receivedLocation.getTime(), is(latestLocation.getTime()));
        assertThat(receivedLocation.getPoint(), is(latestLocation.getPoint()));
    }

}
