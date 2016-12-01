package com.wetrack.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.wetrack.model.User;

import java.lang.reflect.Type;

public class UserSerializer implements JsonSerializer<User> {
    @Override
    public JsonElement serialize(User src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("username", context.serialize(src.getUsername()));
        result.add("nickname", context.serialize(src.getNickname()));
        result.add("email", context.serialize(src.getEmail()));
        result.add("gender", context.serialize(src.getGender()));
        result.add("birthDate", context.serialize(src.getBirthDate()));
        return result;
    }
}
