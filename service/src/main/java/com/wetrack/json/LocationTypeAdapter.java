package com.wetrack.json;

import com.google.gson.*;
import com.wetrack.model.Location;
import org.mongodb.morphia.geo.GeoJson;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

public class LocationTypeAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {
    @Override
    public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject receivedObject = json.getAsJsonObject();
        Location location = new Location();
        location.setId(context.deserialize(receivedObject.getAsJsonPrimitive("id"), String.class));
        location.setUsername(context.deserialize(receivedObject.getAsJsonPrimitive("username"), String.class));
        location.setTime(context.deserialize(receivedObject.getAsJsonPrimitive("time"), LocalDateTime.class));
        double latitude = context.deserialize(receivedObject.getAsJsonPrimitive("latitude"), double.class);
        double longitude = context.deserialize(receivedObject.getAsJsonPrimitive("longitude"), double.class);
        location.setPoint(GeoJson.point(latitude, longitude));
        return location;
    }

    @Override
    public JsonElement serialize(Location src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("id", context.serialize(src.getId()));
        result.add("username", context.serialize(src.getUsername()));
        result.add("time", context.serialize(src.getTime()));
        result.add("latitude", context.serialize(src.getPoint().getLatitude()));
        result.add("longitude", context.serialize(src.getPoint().getLongitude()));
        return result;
    }
}
