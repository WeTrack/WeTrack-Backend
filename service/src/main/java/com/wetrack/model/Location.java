package com.wetrack.model;

import com.wetrack.util.HashedIDGenerator;
import org.mongodb.morphia.annotations.*;
import org.mongodb.morphia.geo.GeoJson;
import org.mongodb.morphia.geo.Point;
import org.mongodb.morphia.utils.IndexType;

import java.time.LocalDateTime;

@Entity("locations")
@Indexes({
    @Index(fields = @Field("username")),
    @Index(fields = @Field(value = "point", type = IndexType.GEO2DSPHERE)),
    @Index(fields = @Field(value = "time", type = IndexType.DESC))
})
public class Location implements DbEntity<String> {
    @Id
    private String id;

    private String username;
    private Point point;
    private LocalDateTime time;

    public Location() {}

    public Location(String username, double latitude, double longitude, LocalDateTime time) {
        this.username = username;
        this.time = time;
        this.point = GeoJson.point(latitude, longitude);
        this.id = HashedIDGenerator.get(username, String.valueOf(latitude), String.valueOf(longitude), time.toString());
    }

    @Override
    public String getId() {
        return id;
    }
    @Override
    public void setId(String id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public Point getPoint() {
        return point;
    }
    public void setPoint(Point point) {
        this.point = point;
    }
    public LocalDateTime getTime() {
        return time;
    }
    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
