package com.wetrack.service.chat;

import com.google.gson.reflect.TypeToken;
import com.wetrack.json.GsonTypes;
import com.wetrack.model.Chat;
import com.wetrack.model.User;
import com.wetrack.test.QueryParam;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.lang.reflect.Type;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ChatExitingTest extends ChatServiceTestWithChatCreated {

    @Test
    public void testChatExiting() {
        assertChatNum(robertPeng, 2);
        Response response = exitChat(robertPeng, tokenOf(robertPeng), chatIdOf(robertFamily));
        logResponse(response, "exiting chat");
        assertReceivedNonemptyMessage(response, 200);
        assertChatNum(robertPeng, 1);
    }

    @Test
    public void testChatExitingWithEmptyToken() {
        Response response = exitChat(robertPeng, "", chatIdOf(robertFamily));
        logResponse(response, "exiting chat with empty token");
        assertReceivedNonemptyMessage(response, 401);
    }

    @Test
    public void testChatExitingWithInvalidToken() {
        Response response = exitChat(robertPeng, notExistEntityId, chatIdOf(robertFamily));
        logResponse(response, "exiting chat with invalid token");
        assertReceivedNonemptyMessage(response, 401);
    }

    @Test
    public void testChatExitingWithOtherToken() {
        Response response = exitChat(robertPeng, tokenOf(windyChan), chatIdOf(robertFamily));
        logResponse(response, "exiting chat with other token");
        assertReceivedNonemptyMessage(response, 401);
    }

    @Test
    public void testChatExitingWithNotExistChatID() {
        Response response = exitChat(robertPeng, tokenOf(robertPeng), notExistEntityId);
        logResponse(response, "exiting chat with not-exist chat id");
        assertReceivedNonemptyMessage(response, 404);
    }

    private Response exitChat(User user, String token, String chatId) {
        return delete("/users/" + user.getUsername() + "/chats/" + chatId, QueryParam.of("token", token));
    }

    private void assertChatNum(User user, int expectedChatNum) {
        Response response = get("/users/" + user.getUsername() + "/chats", QueryParam.of("token", tokenOf(user)));
        List<Chat> chats = assertReceivedEntity(response, GsonTypes.chatListType);
        assertThat(chats.size(), is(expectedChatNum));
    }
}
