package com.wetrack.service.chat;

import com.wetrack.model.User;
import com.wetrack.test.QueryParam;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ChatMemberGettingTest extends ChatServiceTestWithChatCreated {

    @Test
    public void testChatMemberGetting() {
        Response response = get("/chats/" + chatIdOf(robertFamily) + "/members",
                QueryParam.of("token", tokenOf(robertPeng)));
        logResponse(response, "getting chat member");
        List<User> members = assertReceivedEntity(response, 200, userListType);

        assertThat(members.size(), is(2));
        assertThat(members.stream().map(User::getUsername).collect(Collectors.toList()),
                hasItems(robertPeng.getUsername(), mrDai.getUsername()));
    }

    @Test
    public void testGettingNotExistChatMember() {
        Response response = get("/chats/" + notExistEntityId + "/members",
                QueryParam.of("token", tokenOf(robertPeng)));
        logResponse(response, "getting not-exist chat member");
        assertReceivedNonemptyMessage(response, 404);
    }

}
