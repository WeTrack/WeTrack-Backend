package com.wetrack.client;

import com.wetrack.client.model.Message;
import com.wetrack.client.test.EntityResponseTestHelper;
import com.wetrack.client.test.MessageResponseTestHelper;
import com.wetrack.client.test.WeTrackClientTest;
import com.wetrack.util.ResourceUtils;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UserChatExitTest extends WeTrackClientTest {

    private String dummyChatId = "12346523";

    private EntityResponseTestHelper<Message> entityHelper = new EntityResponseTestHelper<>(gson);
    private MessageResponseTestHelper messageHelper = new MessageResponseTestHelper(200);

    @Test
    public void testUserChatExitRequestFormat() throws InterruptedException {
        MockResponse response = new MockResponse().setResponseCode(200)
                .setBody(ResourceUtils.readResource("test_user_chat_exit/200.json"));
        server.enqueue(response);
        server.enqueue(response);

        String expectedPath = "/users/" + robertPeng.getUsername() + "/chats/" + dummyChatId
                + "?token=" + dummyToken;
        client.exitChat(robertPeng.getUsername(), dummyToken, dummyChatId, entityHelper.callback(200));
        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);
        assertThat(request.getMethod(), is("DELETE"));
        assertThat(request.getPath(), is(expectedPath));
        assertThat(request.getBody().readUtf8().isEmpty(), is(true));

        client.exitChat(robertPeng.getUsername(), dummyToken, dummyChatId, messageHelper.callback());
        request = server.takeRequest(3, TimeUnit.SECONDS);
        assertThat(request.getMethod(), is("DELETE"));
        assertThat(request.getPath(), is(expectedPath));
        assertThat(request.getBody().readUtf8().isEmpty(), is(true));
    }

}
