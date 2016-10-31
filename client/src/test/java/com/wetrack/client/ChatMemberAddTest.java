package com.wetrack.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.wetrack.client.model.Message;
import com.wetrack.client.test.EntityResponseTestHelper;
import com.wetrack.client.test.MessageResponseTestHelper;
import com.wetrack.client.test.WeTrackClientTest;
import com.wetrack.util.ResourceUtils;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ChatMemberAddTest extends WeTrackClientTest {

    private String dummyChatId = "12346523";

    private EntityResponseTestHelper<Message> entityHelper = new EntityResponseTestHelper<>(gson);
    private MessageResponseTestHelper messageHelper = new MessageResponseTestHelper(200);

    @Test
    public void testChatMemberAddRequestFormat() throws InterruptedException {
        MockResponse response = new MockResponse().setResponseCode(200)
                .setBody(ResourceUtils.readResource("test_chat_member_add/200.json"));
        server.enqueue(response);
        server.enqueue(response);

        client.addChatMembers(dummyChatId, dummyToken, Arrays.asList(robertPeng, windyChan), entityHelper.callback(200));
        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);
        assertThat(request.getMethod(), is("POST"));
        assertThat(request.getPath(), is("/chats/" + dummyChatId + "/members?token=" + dummyToken));
        JsonArray requestEntity = gson.fromJson(request.getBody().readUtf8(), JsonArray.class);
        assertThat(requestEntity.size(), is(2));
        assertThat(requestEntity.contains(new JsonPrimitive(robertPeng.getUsername())), is(true));
        assertThat(requestEntity.contains(new JsonPrimitive(windyChan.getUsername())), is(true));

        client.addChatMembers(dummyChatId, dummyToken, Arrays.asList(robertPeng, windyChan), messageHelper.callback());
        request = server.takeRequest(3, TimeUnit.SECONDS);
        assertThat(request.getMethod(), is("POST"));
        assertThat(request.getPath(), is("/chats/" + dummyChatId + "/members?token=" + dummyToken));
        requestEntity = gson.fromJson(request.getBody().readUtf8(), JsonArray.class);
        assertThat(requestEntity.size(), is(2));
        assertThat(requestEntity.contains(new JsonPrimitive(robertPeng.getUsername())), is(true));
        assertThat(requestEntity.contains(new JsonPrimitive(windyChan.getUsername())), is(true));
    }

}
