package com.wetrack.service.chat;

import com.google.gson.JsonObject;
import com.wetrack.model.User;
import com.wetrack.test.QueryParam;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.ArrayList;

public class ChatCreatingTest extends ChatServiceTest {

    @Test
    public void testChatCreating() {
        Response response = createChat(robertAndWindy, tokenOf(robertPeng), windyChan);
        logResponse(response, "chat creating");
        assertReceivedNonemptyMessage(response, 201);
    }

    @Test
    public void testChatCreatingWithoutToken() {
        Response response = createChat(robertAndWindy, "", windyChan);
        logResponse(response, "chat creating without token");
        assertReceivedNonemptyMessage(response, 400);
    }

    @Test
    public void testChatCreatingWithInvalidToken() {
        Response response = createChat(robertAndWindy, notExistEntityId, windyChan);
        logResponse(response, "chat creating with invalid token");
        assertReceivedNonemptyMessage(response, 401);
    }

    @Test
    public void testChatCreatingWithNotExistUser() {
        Response response = createChat(robertAndWindy, tokenOf(robertPeng), new User(notExistEntityId, null, null));
        logResponse(response, "chat creating with not-exist user");
        assertReceivedNonemptyMessage(response, 404);
    }

    @Test
    public void testChatCreatingWithNotFriend() {
        Response response = createChat(robertAndWindy, tokenOf(robertPeng), littleHearth);
        logResponse(response, "chat creating with no-friend user");
        assertReceivedNonemptyMessage(response, 403);
    }

    @Test
    public void testChatCreatingWithInvalidRequestBody() {
        ArrayList<String> memberNames = new ArrayList<>();
        memberNames.add(windyChan.getUsername());
        JsonObject requestEntity = new JsonObject();
        requestEntity.add("members", gson.toJsonTree(memberNames));
        Response response = post("/chats", gson.toJson(requestEntity), QueryParam.of("token", tokenOf(robertPeng)));
        logResponse(response, "chat creating with invalid request body");
        assertReceivedNonemptyMessage(response, 400);
    }

}
