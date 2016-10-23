package com.wetrack;

import com.wetrack.client.model.User;
import com.wetrack.client.test.EntityResponseTestHelper;
import com.wetrack.client.test.MessageResponseTestHelper;
import com.wetrack.test.Utils;
import com.wetrack.test.WeTrackIntegrateTest;
import com.wetrack.util.CryptoUtils;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class UserGetTest extends WeTrackIntegrateTest {

    private EntityResponseTestHelper<User> entityHelper = new EntityResponseTestHelper<>(gson);
    private User testUser;
    private String username;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        testUser = Utils.loadExampleUser(gson);
        MessageResponseTestHelper messageHelper = new MessageResponseTestHelper();
        client.createUser(testUser, messageHelper.callback(201));
        messageHelper.assertReceivedMessage(true);

        username = testUser.getUsername();
    }

    @Test
    public void testGetExistedUser() {
        client.getUserInfo(username, entityHelper.callback(200));
        entityHelper.assertReceivedEntity(200);

        User userFromServer = entityHelper.getReceivedEntity();
        assertThat(userFromServer.getUsername(), is(username));
        assertThat(userFromServer.getPassword(), nullValue());
        assertThat(userFromServer.getNickname(), is(testUser.getNickname()));
        assertThat(userFromServer.getEmail(), is(testUser.getEmail()));
        assertThat(userFromServer.getGender(), is(testUser.getGender()));
        assertThat(userFromServer.getBirthDate(), is(testUser.getBirthDate()));
    }

    @Test
    public void testGetNotExistedUser() {
        client.getUserInfo(username + "Not important", entityHelper.callback(200));
        entityHelper.assertReceivedErrorMessage(404);
    }

}
