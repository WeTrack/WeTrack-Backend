package com.wetrack.service.chat;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wetrack.json.GsonTypes;
import com.wetrack.model.User;
import com.wetrack.test.QueryParam;
import com.wetrack.test.WeTrackServerTestWithUserLoggedIn;
import org.junit.Before;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

abstract class ChatServiceTest extends WeTrackServerTestWithUserLoggedIn {
    String robertFamily = "Robert's Family";
    String windyFamily = "Windy's Family";
    String robertAndWindy = "Robert & Windy";
    String mrDaiAndLittleHearth = "Mr.Dai & Little Hearth";

    @Before
    public void setUp() throws Exception {
        super.setUp();

        addFriendWithAssertion(robertPeng, windyChan, tokenOf(robertPeng));
        addFriendWithAssertion(mrDai, littleHearth, tokenOf(mrDai));
        addFriendWithAssertion(robertPeng, mrDai, tokenOf(robertPeng));
        addFriendWithAssertion(windyChan, littleHearth, tokenOf(windyChan));

        assertFriendsNum(robertPeng, 2);
        assertFriendsNum(windyChan, 2);
        assertFriendsNum(mrDai, 2);
        assertFriendsNum(littleHearth, 2);
    }

    void addFriendWithAssertion(User userA, User userB, String tokenOfUserA) {
        Response response = post("/users/" + userA.getUsername() + "/friends/" + userB.getUsername(),
                "", QueryParam.of("token", tokenOfUserA));
        assertReceivedNonemptyMessage(response, 200);
    }

    void assertFriendsNum(User user, int expectedFriendsNum) {
        Response response = get("/users/" + user.getUsername() + "/friends",
                QueryParam.of("token", tokenOf(user)));
        List<User> receivedFriends = assertReceivedEntity(response, 200, new TypeToken<List<User>>(){}.getType());
        assertThat(receivedFriends.size(), is(expectedFriendsNum));
    }

    Response createChat(String chatName, String token, User... members) {
        ArrayList<String> memberNames = new ArrayList<>();
        for (User member : members)
            memberNames.add(member.getUsername());
        JsonObject requestEntity = new JsonObject();
        requestEntity.addProperty("name", chatName);
        requestEntity.add("members", gson.toJsonTree(memberNames));
        return post("/chats", gson.toJson(requestEntity), QueryParam.of("token", token));
    }

    String createChatWithAssertion(String chatName, String token, User... members) {
        Response response = createChat(chatName, token, members);
        assertReceivedCreatedMessage(response);
        return response.getHeaderString("Location").split("/")[4];
    }

    Response addMembers(String chatId, String token, User... newMembers) {
        List<String> newMemberNames = Arrays.stream(newMembers).map(User::getUsername).collect(Collectors.toList());
        return post("/chats/" + chatId + "/members", gson.toJson(newMemberNames), QueryParam.of("token", token));
    }

    void addMembersWithAssertion(String chatId, String token, User... newMembers) {
        Response response = addMembers(chatId, token, newMembers);
        assertReceivedNonemptyMessage(response, 200);
    }

    void addFriendWithAssertion(User sourceUser, String token, User newFriendUser) {
        Response response = post("/users/" + sourceUser.getUsername() + "/friends/" + newFriendUser.getUsername(),
                "", QueryParam.of("token", token));
        assertReceivedNonemptyMessage(response, 200);
    }

    List<User> getChatMemberWithAssertion(String chatId, String token) {
        Response response = get("/chats/" + chatId + "/members", QueryParam.of("token", token));
        return assertReceivedEntity(response, GsonTypes.userListType);
    }

    Response removeMembers(String chatId, String token, User toRemoveMember) {
        return delete("/chats/" + chatId + "/members/" + toRemoveMember.getUsername(), QueryParam.of("token", token));
    }

}
