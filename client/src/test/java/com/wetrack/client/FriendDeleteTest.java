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

public class FriendDeleteTest extends WeTrackClientTest {

    private EntityResponseTestHelper<Message> entityHelper = new EntityResponseTestHelper<>(gson);
    private MessageResponseTestHelper messageHelper = new MessageResponseTestHelper(200);

    @Test
    public void testFriendDeleteRequestFormat() throws InterruptedException {
        MockResponse response = new MockResponse().setResponseCode(200)
                .setBody(ResourceUtils.readResource("test_friend_delete/200.json"));
        server.enqueue(response);
        server.enqueue(response);

        String expectedPath = "/users/" + robertPeng.getUsername() + "/friends/" + mrDai.getUsername()
                + "?token=" + dummyToken;
        client.deleteFriend(robertPeng.getUsername(), dummyToken, mrDai.getUsername(), entityHelper.callback(200));
        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);
        assertThat(request.getMethod(), is("DELETE"));
        assertThat(request.getPath(), is(expectedPath));
        assertThat(request.getBody().readUtf8().isEmpty(), is(true));

        client.deleteFriend(robertPeng.getUsername(), dummyToken, mrDai.getUsername(), messageHelper.callback());
        request = server.takeRequest(3, TimeUnit.SECONDS);
        assertThat(request.getMethod(), is("DELETE"));
        assertThat(request.getPath(), is(expectedPath));
        assertThat(request.getBody().readUtf8().isEmpty(), is(true));
    }
}