package com.wetrack;

import com.wetrack.client.model.User;
import com.wetrack.client.test.EntityResponseTestHelper;
import com.wetrack.test.WeTrackIntegrateTestWithChatCreated;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ChatMemberGettingTest extends WeTrackIntegrateTestWithChatCreated {

    private EntityResponseTestHelper<List<User>> entityHelper = new EntityResponseTestHelper<>(gson);

    @Test
    public void testChatMemberGetting() {
        client.getChatMembers(chatIdOf(robertFamily), tokenOf(robertPeng), entityHelper.callback(200));
        entityHelper.assertReceivedEntity(200);
        List<User> members = entityHelper.getReceivedEntity();
        assertThat(members.size(), is(2));
        assertThat(members.stream().map(User::getUsername).collect(Collectors.toList()),
                hasItems(robertPeng.getUsername(), mrDai.getUsername()));
    }

    @Test
    public void testGettingNotExistChatMember() {
        client.getChatMembers(notExistEntityId, tokenOf(robertPeng), entityHelper.callback(200));
        entityHelper.assertReceivedErrorMessage(404);
    }

}
