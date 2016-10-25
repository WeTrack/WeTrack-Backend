package com.wetrack.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wetrack.dao.LocationRepository;
import com.wetrack.dao.UserTokenRepository;
import com.wetrack.model.Location;
import com.wetrack.model.UserToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

import static com.wetrack.util.RsResponseUtils.*;

@Path("/users/{username}/locations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LocationService {
    private static final Logger LOG = LoggerFactory.getLogger(LocationService.class);

    @Autowired private Gson gson;
    @Autowired private UserTokenRepository userTokenRepository;
    @Autowired private LocationRepository locationRepository;

    @GET
    @Path("/latest")
    public Response getLatestLocation(@PathParam("username") @DefaultValue("") String username) {
        LOG.debug("GET  /users/{}/locations/latest", username);

        Location location = locationRepository.getLatestLocation(username);
        if (location == null)
            return notFoundMessage();
        return ok(gson.toJson(location));
    }

    @GET
    public Response getLocationsSince(@PathParam("username") @DefaultValue("") String username,
                                      @QueryParam("since") @DefaultValue("") String since) {
        LOG.debug("GET  /users/{}/locations", username);
        if (since.trim().isEmpty())
            since = "1970-01-01T00:00:00Z";
        LocalDateTime sinceTime = LocalDateTime.parse(since);

        List<Location> foundLocations = locationRepository.findLocationsSince(username, sinceTime);
        return ok(gson.toJson(foundLocations));
    }

    @POST
    public Response postLocations(@PathParam("username") String username,
                                  @DefaultValue("") String requestBody) {
        LOG.debug("POST /users/{}/locations", username);
        if (requestBody.trim().isEmpty())
            ok();

        JsonObject receivedJson = gson.fromJson(requestBody, JsonObject.class);
        if (!receivedJson.has("token"))
            return badRequest("Token must be provided in the request body.");
        String token = receivedJson.get("token").getAsString();
        if (token.trim().isEmpty())
            return badRequest("Token must be provided in the request body.");

        UserToken tokenInDB = userTokenRepository.findByTokenStr(token);
        if (tokenInDB == null || tokenInDB.getExpireTime().isBefore(LocalDateTime.now()))
            return unauthorized("The given token is invalid or has expired. Please log in again.");
        if (!tokenInDB.getUsername().equals(username))
            return unauthorized("You cannot upload others' locations.");

        if (!receivedJson.has("locations"))
            return ok();

        List<Location> receivedLocations = null;
        if (receivedJson.get("locations").isJsonArray()) {
            receivedLocations = gson.fromJson(receivedJson.get("locations").getAsJsonArray(),
                    new TypeToken<List<Location>>() {}.getType());
        }
        else if (receivedJson.get("locations").isJsonPrimitive()) {
            receivedLocations = gson.fromJson(receivedJson.get("locations").getAsString(),
                    new TypeToken<List<Location>>() {}.getType());
        }
        int insertedCounter = 0;
        for (Location location : receivedLocations) {
            if (location.getUsername() != null && !location.getUsername().trim().isEmpty()
                    && !tokenInDB.getUsername().equals(location.getUsername()))
                continue;
            location.setUsername(tokenInDB.getUsername());
            location.generateId();
            locationRepository.insert(location);
            insertedCounter++;
        }

        return okMessage("Received " + insertedCounter + " locations.");
    }

}
