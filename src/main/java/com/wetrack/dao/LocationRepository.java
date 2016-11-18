package com.wetrack.dao;

import com.wetrack.model.Location;

import java.time.LocalDateTime;
import java.util.List;

public interface LocationRepository extends Repository<String, Location> {

    List<Location> findLocationsSince(String username, LocalDateTime sinceTime);

    Location getLatestLocation(String username);

}
