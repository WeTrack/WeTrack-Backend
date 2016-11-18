package com.wetrack;

import com.wetrack.client.model.Chat;
import com.wetrack.client.test.EntityResponseHelper;
import com.wetrack.test.WeTrackIntegrateTestWithChatCreated;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ChatListGettingTest extends WeTrackIntegrateTestWithChatCreated {

    private EntityResponseHelper<List<Chat>> entityHelper = new EntityResponseHelper<>(gson);

    @Test
    public void testChatListGetting() {
        client.getUserChatList(robertPeng.getUsername(), tokenOf(robertPeng), entityHelper.callback(200));
        entityHelper.assertReceivedEntity(200);
        List<String> chatNames = entityHelper.getReceivedEntity().stream()
                .map(Chat::getName).collect(Collectors.toList());
        assertThat(chatNames.size(), is(2));
        assertThat(chatNames, hasItems(robertFamily, robertAndWindy));

        client.getUserChatList(mrDai.getUsername(), tokenOf(mrDai), entityHelper.callback(200));
        entityHelper.assertReceivedEntity(200);
        chatNames = entityHelper.getReceivedEntity().stream()
                .map(Chat::getName).collect(Collectors.toList());
        assertThat(chatNames.size(), is(2));
        assertThat(chatNames, hasItems(robertFamily, mrDaiAndLittleHearth));
    }

    @Test
    public void testGettingChatListWithEmptyToken() {
        client.getUserChatList(robertPeng.getUsername(), "", entityHelper.callback(200));
        entityHelper.assertReceivedErrorMessage(401);
    }

    @Test
    public void testGettingChatListWithInvalidToken() {
        client.getUserChatList(robertPeng.getUsername(), notExistEntityId, entityHelper.callback(200));
        entityHelper.assertReceivedErrorMessage(401);
    }

    @Test
    public void testGettingChatListWithOthersToken() {
        client.getUserChatList(robertPeng.getUsername(), tokenOf(windyChan), entityHelper.callback(200));
        entityHelper.assertReceivedErrorMessage(401);
    }

}
