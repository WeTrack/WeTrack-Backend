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

public class FriendAddTest extends WeTrackClientTest {

    private EntityResponseTestHelper<Message> entityHelper = new EntityResponseTestHelper<>(gson);
    private MessageResponseTestHelper messageHelper = new MessageResponseTestHelper(200);

    @Test
    public void testFriendAddRequestFormat() throws InterruptedException {
        MockResponse response = new MockResponse().setResponseCode(200)
                .setBody(ResourceUtils.readResource("test_friend_add/200.json"));
        server.enqueue(response);
        server.enqueue(response);

        String expectedPath = "/users/" + robertPeng.getUsername() + "/friends/" + windyChan.getUsername()
                + "?token=" + dummyToken;
        client.addFriend(robertPeng.getUsername(), dummyToken, windyChan.getUsername(), entityHelper.callback(200));
        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);
        assertThat(request.getMethod(), is("POST"));
        assertThat(request.getPath(), is(expectedPath));
        assertThat(request.getBody().readUtf8().isEmpty(), is(true));

        client.addFriend(robertPeng.getUsername(), dummyToken, windyChan.getUsername(), messageHelper.callback());
        request = server.takeRequest(3, TimeUnit.SECONDS);
        assertThat(request.getMethod(), is("POST"));
        assertThat(request.getPath(), is(expectedPath));
        assertThat(request.getBody().readUtf8().isEmpty(), is(true));
    }

}
