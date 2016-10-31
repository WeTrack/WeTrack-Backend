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
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ChatListGettingTest extends ChatServiceTestWithChatCreated {

    @Test
    public void testChatListGetting() {
        Response response = getChatList(robertPeng, tokenOf(robertPeng));
        logResponse(response, "getting chat list");
        List<Chat> chats = assertReceivedEntity(response, 200, GsonTypes.chatListType);
        List<String> chatNames = chats.stream().map(Chat::getName).collect(Collectors.toList());
        assertThat(chatNames.size(), is(2));
        assertThat(chatNames, hasItems(robertFamily, robertAndWindy));

        response = getChatList(mrDai, tokenOf(mrDai));
        logResponse(response, "getting chat list");
        chats = assertReceivedEntity(response, 200, GsonTypes.chatListType);
        chatNames = chats.stream().map(Chat::getName).collect(Collectors.toList());
        assertThat(chatNames.size(), is(2));
        assertThat(chatNames, hasItems(robertFamily, mrDaiAndLittleHearth));
    }

    @Test
    public void testGettingChatListWithEmptyToken() {
        Response response = getChatList(robertPeng, "");
        logResponse(response, "getting chat list with empty token");
        assertReceivedNonemptyMessage(response, 401);
    }

    @Test
    public void testGettingChatListWithInvalidToken() {
        Response response = getChatList(robertPeng, "I\'m a token");
        logResponse(response, "getting chat list with invalid token");
        assertReceivedNonemptyMessage(response, 401);
    }

    @Test
    public void testGettingChatListWithOthersToken() {
        Response response = getChatList(robertPeng, tokenOf(windyChan));
        logResponse(response, "getting chat list with other's token");
        assertReceivedNonemptyMessage(response, 401);
    }

    private Response getChatList(User user, String token) {
        return get("/users/" + user.getUsername() + "/chats", QueryParam.of("token", token));
    }

}
