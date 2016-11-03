package com.wetrack.json;

import com.google.gson.*;
import com.wetrack.model.ChatMessage;
import com.wetrack.model.Notification;

import java.lang.reflect.Type;

public class NotificationAdapter implements JsonSerializer<Notification>, JsonDeserializer<Notification> {
    private static final String TYPE_CHAT_MESSAGE = "chat_message";

    @Override
    public Notification deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (!json.isJsonObject())
            throw new JsonParseException("The given JSON entity must be an object.");
        if (!json.getAsJsonObject().has("type"))
            throw new JsonParseException("The given JSON object must contain `type` field.");
        String type = json.getAsJsonObject().get("type").getAsString();
        if (!type.equals(TYPE_CHAT_MESSAGE))
            return context.deserialize(json, ChatMessage.class);
        throw new JsonParseException("Does not support Notification object with type `" + type + "`.");
    }

    @Override
    public JsonElement serialize(Notification src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = context.serialize(typeOfSrc).getAsJsonObject();
        if (src instanceof ChatMessage)
            result.addProperty("type", TYPE_CHAT_MESSAGE);
        return result;
    }
}
