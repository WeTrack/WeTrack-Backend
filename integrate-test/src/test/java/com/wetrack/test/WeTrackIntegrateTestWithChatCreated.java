package com.wetrack.test;

import com.wetrack.client.model.Chat;
import com.wetrack.client.model.User;
import com.wetrack.client.test.CreatedResponseTestHelper;
import com.wetrack.client.test.EntityResponseTestHelper;
import com.wetrack.client.test.MessageResponseTestHelper;
import org.junit.Before;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class WeTrackIntegrateTestWithChatCreated extends WeTrackIntegrateTestWithFriendAdded {
    private Map<String, String> chatIds;

    protected String robertFamily = "Robert's Family";
    protected String windyFamily = "Windy's Family";
    protected String robertAndWindy = "Robert & Windy";
    protected String mrDaiAndLittleHearth = "Mr.Dai & Little Hearth";

    @Before
    public void setUp() throws Exception {
        super.setUp();

        chatIds = new HashMap<>();
        chatIds.put(robertFamily, createChatWithAssertion(tokenOf(robertPeng), robertFamily, mrDai));
        chatIds.put(windyFamily, createChatWithAssertion(tokenOf(windyChan), windyFamily, littleHearth));
        chatIds.put(robertAndWindy, createChatWithAssertion(tokenOf(robertPeng), robertAndWindy, windyChan));
        chatIds.put(mrDaiAndLittleHearth, createChatWithAssertion(tokenOf(mrDai), mrDaiAndLittleHearth, littleHearth));
    }

    protected String createChatWithAssertion(String token, String chatName, User... members) {
        CreatedResponseTestHelper messageHelper = new CreatedResponseTestHelper();
        Chat chat = new Chat(chatName);
        chat.setMembers(Arrays.asList(members));
        client.createChat(token, chat, messageHelper.callback());
        messageHelper.assertReceivedSuccessfulMessage();
        return messageHelper.getReceivedNewEntityId();
    }

    protected void addChatMemberWithAssertion(String chatId, String token, User... newMembers) {
        MessageResponseTestHelper messageHelper = new MessageResponseTestHelper(200);
        client.addChatMembers(chatId, token, Arrays.asList(newMembers), messageHelper.callback());
        messageHelper.assertReceivedSuccessfulMessage();
    }

    protected String chatIdOf(String chatName) {
        return chatIds.get(chatName);
    }

    protected void assertChatMember(String chatId, String token,
                                  int expectedMemberNum, User... expectedMembers) {
        EntityResponseTestHelper<List<User>> entityHelper = new EntityResponseTestHelper<>(gson);
        client.getChatMembers(chatId, token, entityHelper.callback(200));
        entityHelper.assertReceivedEntity(200);
        List<User> members = entityHelper.getReceivedEntity();
        assertThat(members.size(), is(expectedMemberNum));
        assertThat(members.stream().map(User::getUsername).collect(Collectors.toList()),
                hasItems(Arrays.stream(expectedMembers).map(User::getUsername).collect(Collectors.toList()).toArray()));
    }

}
