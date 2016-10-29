package com.wetrack.service.chat;

import com.wetrack.model.User;
import com.wetrack.test.QueryParam;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ChatMemberAddingTest extends ChatServiceTestWithChatCreated {

    @Test
    public void testChatMemberAdding() {
        Response response = addMembers(chatIdOf(robertFamily), tokenOf(robertPeng), windyChan);
        logResponse(response, "adding chat member");
        assertReceivedEmptyResponse(response, 200);
        response = get("/chats/" + chatIdOf(robertFamily) + "/members", QueryParam.of("token", tokenOf(robertPeng)));
        List<User> members = assertReceivedEntity(response, userListType);
        assertThat(members.size(), is(3));
        assertThat(members.stream().map(User::getUsername).collect(Collectors.toList()), hasItem(windyChan.getUsername()));
    }

    @Test
    public void testMultipleMemberAdding() {
        addFriendWithAssertion(robertPeng, tokenOf(robertPeng), littleHearth);
        Response response = addMembers(chatIdOf(robertFamily), tokenOf(robertPeng), windyChan, littleHearth);
        logResponse(response, "adding multiple chat member");
        assertReceivedEmptyResponse(response, 200);

        List<User> members = getChatMemberWithAssertion(chatIdOf(robertFamily), tokenOf(robertPeng));
        List<String> memberNames = members.stream().map(User::getUsername).collect(Collectors.toList());
        assertThat(memberNames.size(), is(4));
        assertThat(memberNames, hasItems(windyChan.getUsername(), littleHearth.getUsername()));
    }

    @Test
    public void testChatMemberAddingWithoutToken() {
        Response response = addMembers(chatIdOf(robertFamily), "", windyChan);
        logResponse(response, "adding chat member without token");
        assertReceivedNonemptyMessage(response, 400);
    }

    @Test
    public void testChatMemberAddingWithInvalidToken() {
        Response response = addMembers(chatIdOf(robertFamily), notExistEntityId, windyChan);
        logResponse(response, "adding chat member with invalid token");
        assertReceivedNonemptyMessage(response, 401);
    }

    @Test
    public void testChatMemberAddingWithNonMemberToken() {
        Response response = addMembers(chatIdOf(robertFamily), tokenOf(windyChan), windyChan);
        logResponse(response, "adding chat member with non-member token");
        assertReceivedNonemptyMessage(response, 401);
    }

    @Test
    public void testAddingMemberToNotExistChat() {
        Response response = addMembers(notExistEntityId, tokenOf(windyChan), windyChan);
        logResponse(response, "adding member to not-exist chat");
        assertReceivedNonemptyMessage(response, 404);
    }

    @Test
    public void testAddingNonFriendToChat() {
        Response response = addMembers(chatIdOf(robertFamily), tokenOf(robertPeng), littleHearth);
        logResponse(response, "adding chat member with non-member token");
        assertReceivedNonemptyMessage(response, 403);
    }

}
