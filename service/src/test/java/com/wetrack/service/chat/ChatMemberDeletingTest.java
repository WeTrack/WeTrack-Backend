package com.wetrack.service.chat;

import com.wetrack.model.User;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ChatMemberDeletingTest extends ChatServiceTestWithChatCreated {

    @Before
    public void setUp() throws Exception {
        super.setUp();

        addFriendWithAssertion(robertPeng, tokenOf(robertPeng), littleHearth);
        addMembersWithAssertion(chatIdOf(robertFamily), tokenOf(robertPeng), windyChan, littleHearth);
    }

    @Test
    public void testRemovingMember() {
        Response response = removeMembers(chatIdOf(robertFamily), tokenOf(robertPeng), windyChan);
        logResponse(response, "removing single member");
        assertReceivedEmptyResponse(response, 200);

        List<User> members = getChatMemberWithAssertion(chatIdOf(robertFamily), tokenOf(robertPeng));
        List<String> memberNames = members.stream().map(User::getUsername).collect(Collectors.toList());
        assertThat(memberNames.size(), is(3));
        assertThat(memberNames, hasItems(robertPeng.getUsername(), mrDai.getUsername(), littleHearth.getUsername()));
    }

    @Test
    public void testRemovingMemberWithEmptyToken() {
        Response response = removeMembers(chatIdOf(robertFamily), "", windyChan);
        logResponse(response, "removing member with empty token");
        assertReceivedNonemptyMessage(response, 400);
    }

    @Test
    public void testRemovingMemberWithInvalidToken() {
        Response response = removeMembers(chatIdOf(robertFamily), tokenOf(robertPeng), littleHearth);
        assertReceivedEmptyResponse(response, 200);
        response = removeMembers(chatIdOf(robertFamily), tokenOf(littleHearth), windyChan);
        assertReceivedNonemptyMessage(response, 401);
    }

}
