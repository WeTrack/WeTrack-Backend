package com.wetrack.dao.morphia;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.wetrack.config.SpringConfig;
import com.wetrack.config.SpringTestConfig;
import com.wetrack.dao.LocationRepository;
import com.wetrack.model.Location;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mongodb.morphia.geo.GeoJson;
import org.mongodb.morphia.geo.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringTestConfig.class)
public class LocationRepositoryTest {

    @Autowired private MongoClient client;
    @Autowired private LocationRepository locationRepository;

    private String username = "robert-peng";
    private String anotherUsername = "windy-chan";

    private Point poLamMtrStation = GeoJson.point(22.322663, 114.257771);
    private LocalDateTime poLamMtrStationArriveTime = LocalDateTime.of(2016, 10, 24, 10, 0, 0);
    private Point hkustSouthGate = GeoJson.point(22.333101, 114.263275);
    private LocalDateTime hkustSouthGateArriveTime = LocalDateTime.of(2016, 10, 24, 10, 30, 0);
    private Point hkustLibrary = GeoJson.point(22.338022, 114.264158);
    private LocalDateTime hkustLibraryArriveTime = LocalDateTime.of(2016, 10, 24, 10, 45, 0);
    private Point hkustNorthGate = GeoJson.point(22.338589, 114.262013);
    private LocalDateTime hkustNorthGateArriveTime = LocalDateTime.of(2016, 10, 24, 17, 30, 0);

    private MongoCollection<Document> locations;

    @Before
    public void setUp() {
        locations = client.getDatabase(SpringConfig.DEFAULT_DATABASE).getCollection("locations");
    }

    @After
    public void tearDown() {
        locations.deleteMany(new Document("username", username));
        locations.deleteMany(new Document("username", anotherUsername));
    }

    @Test
    public void testFindLocationSince() {
        insertTestLocations();

        List<Location> foundLocations =
                locationRepository.findLocationsSince(username, hkustLibraryArriveTime.plusHours(1));
        assertThat(foundLocations.size(), is(1));
        Location foundLocation = foundLocations.get(0);
        assertThat(foundLocation.getUsername(), is(username));
        assertThat(foundLocation.getPoint(), is(hkustNorthGate));
        assertThat(foundLocation.getTime(), is(hkustNorthGateArriveTime));

        foundLocations = locationRepository.findLocationsSince(anotherUsername, hkustSouthGateArriveTime.plusMinutes(1));
        assertThat(foundLocations.size(), is(2));
        foundLocation = foundLocations.get(0);
        assertThat(foundLocation.getUsername(), is(anotherUsername));
        assertThat(foundLocation.getPoint(), is(hkustLibrary));
        assertThat(foundLocation.getTime(), is(hkustLibraryArriveTime));
        foundLocation = foundLocations.get(1);
        assertThat(foundLocation.getUsername(), is(anotherUsername));
        assertThat(foundLocation.getPoint(), is(hkustNorthGate));
        assertThat(foundLocation.getTime(), is(hkustNorthGateArriveTime));
    }

    @Test
    public void testLocationInsert() {
        Location location = new Location(username, poLamMtrStation, poLamMtrStationArriveTime);
        locationRepository.insert(location);

        Document locInDB = locations.find(new Document("username", username)).first();
        assertThat(locInDB, notNullValue());
        assertThat(locInDB.getString("username"), is(username));
        assertThat(locInDB.get("time", Date.class),
                is(Date.from(poLamMtrStationArriveTime.atZone(ZoneId.systemDefault()).toInstant())));
        Point point = pointDocumentToPoint(locInDB.get("point", Document.class));
        assertThat(point.getLatitude(), is(poLamMtrStation.getLatitude()));
        assertThat(point.getLongitude(), is(poLamMtrStation.getLongitude()));
    }

    @Test
    public void testGetLatestLocation() {
        insertTestLocations();

        Location location = locationRepository.getLatestLocation(username);
        assertThat(location, notNullValue());
        assertThat(location.getUsername(), is(username));
        assertThat(location.getTime(), is(hkustNorthGateArriveTime));
        assertThat(location.getPoint(), is(hkustNorthGate));

        location = locationRepository.getLatestLocation(anotherUsername);
        assertThat(location, notNullValue());
        assertThat(location.getUsername(), is(anotherUsername));
        assertThat(location.getTime(), is(hkustNorthGateArriveTime));
        assertThat(location.getPoint(), is(hkustNorthGate));
    }

    private void insertTestLocations() {
        Location location = new Location(username, poLamMtrStation, poLamMtrStationArriveTime);
        locationRepository.insert(location);
        location = new Location(username, hkustSouthGate, hkustSouthGateArriveTime);
        locationRepository.insert(location);
        location = new Location(username, hkustLibrary, hkustLibraryArriveTime);
        locationRepository.insert(location);
        location = new Location(username, hkustNorthGate, hkustNorthGateArriveTime);
        locationRepository.insert(location);
        location = new Location(anotherUsername, poLamMtrStation, poLamMtrStationArriveTime);
        locationRepository.insert(location);
        location = new Location(anotherUsername, hkustSouthGate, hkustSouthGateArriveTime);
        locationRepository.insert(location);
        location = new Location(anotherUsername, hkustLibrary, hkustLibraryArriveTime);
        locationRepository.insert(location);
        location = new Location(anotherUsername, hkustNorthGate, hkustNorthGateArriveTime);
        locationRepository.insert(location);
    }

    private Point pointDocumentToPoint(Document point) {
        ArrayList<Double> coordinates = point.get("coordinates", ArrayList.class);
        assert coordinates.size() == 2;
        return GeoJson.point(coordinates.get(1), coordinates.get(0));
    }
}
