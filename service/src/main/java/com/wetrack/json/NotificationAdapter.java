package com.wetrack.json;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.*;
import com.wetrack.model.ChatInvitation;
import com.wetrack.model.ChatMessage;
import com.wetrack.model.FriendInvitation;
import com.wetrack.model.Notification;
import com.wetrack.ws.WsMessage;

import java.lang.reflect.Type;

public class NotificationAdapter implements JsonSerializer<Notification>, JsonDeserializer<Notification> {
    private static final String TYPE_CHAT_MESSAGE = "chat_message";
    private static final String TYPE_FRIEND_INVITATION = "friend_invitation";
    private static final String TYPE_CHAT_INVITATION = "chat_invitation";
    private static final String TYPE_WS_MESSAGE = "message";

    private static final BiMap<Class, String> types = HashBiMap.create();
    static {
        types.forcePut(ChatMessage.class, TYPE_CHAT_MESSAGE);
        types.forcePut(FriendInvitation.class, TYPE_FRIEND_INVITATION);
        types.forcePut(ChatInvitation.class, TYPE_CHAT_INVITATION);
        types.forcePut(WsMessage.class, TYPE_WS_MESSAGE);
    }

    @Override
    public Notification deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (!json.isJsonObject())
            throw new JsonParseException("The given JSON entity must be an object.");
        if (!json.getAsJsonObject().has("type"))
            throw new JsonParseException("The given JSON object must contain `type` field.");
        String type = json.getAsJsonObject().get("type").getAsString();
        Class targetClass = types.inverse().get(type);
        if (targetClass == null)
            throw new JsonParseException("NotificationAdapter does not support deserialization "
                    + "for object of type `" + type + "`");
        return context.deserialize(json, targetClass);
    }

    @Override
    public JsonElement serialize(Notification src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = context.serialize(typeOfSrc).getAsJsonObject();
        String type = types.get(src.getClass());
        if (type == null)
            throw new IllegalArgumentException("NotificationAdapter does not support serialization "
                    + "for object of type " + src.getClass().getName());
        result.addProperty("type", type);
        return result;
    }
}
