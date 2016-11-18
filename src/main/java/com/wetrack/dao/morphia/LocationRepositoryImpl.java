package com.wetrack.dao.morphia;

import com.wetrack.dao.LocationRepository;
import com.wetrack.model.Location;

import java.time.LocalDateTime;
import java.util.List;

public class LocationRepositoryImpl extends MorphiaRepository<String, Location> implements LocationRepository {
    @Override
    public List<Location> findLocationsSince(String username, LocalDateTime sinceTime) {
        return createQuery().field("username").equal(username)
                .field("time").greaterThan(sinceTime).asList();
    }

    @Override
    public Location getLatestLocation(String username) {
        return createQuery().field("username").equal(username).order("-time").get();
    }

    @Override
    protected Class<Location> getEntityClass() {
        return Location.class;
    }
}
