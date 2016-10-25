package com.wetrack;

import com.wetrack.client.model.Message;
import com.wetrack.client.model.User;
import com.wetrack.client.test.EntityResponseTestHelper;
import com.wetrack.client.test.MessageResponseTestHelper;
import com.wetrack.test.Utils;
import com.wetrack.test.WeTrackIntegrateTestWithUserLoggedIn;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UserUpdateTest extends WeTrackIntegrateTestWithUserLoggedIn {

    private MessageResponseTestHelper messageHelper = new MessageResponseTestHelper(200);
    private EntityResponseTestHelper<Message> entityHelper = new EntityResponseTestHelper<>(gson);
    private EntityResponseTestHelper<User> userHelper = new EntityResponseTestHelper<>(gson);

    private User newUser;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        newUser = Utils.loadExampleUser(gson, "updated_user.json");
        assertThat(newUser.getUsername(), is(username));
    }

    @Test
    public void testInvalidUserUpdate() {
        client.updateUser(username, "", newUser, entityHelper.callback(201));
        entityHelper.assertReceivedErrorMessage(400);

        client.updateUser("Something Strange", token, new User(), entityHelper.callback(201));
        entityHelper.assertReceivedErrorMessage(404);
    }

    @Test
    public void testValidUserUpdateWithMessageCallback() {
        client.updateUser(username, token, newUser, messageHelper.callback());
        messageHelper.assertReceivedSuccessfulMessage();

        client.getUserInfo(username, userHelper.callback(200));
        userHelper.assertReceivedEntity(200);
        User userFromServer = userHelper.getReceivedEntity();

        assertThat(userFromServer.getUsername(), is(username));
        assertThat(userFromServer.getNickname(), is(newUser.getNickname()));
        assertThat(userFromServer.getEmail(), is(newUser.getEmail()));
        assertThat(userFromServer.getGender(), is(newUser.getGender()));
        assertThat(userFromServer.getBirthDate(), is(newUser.getBirthDate()));
    }

    @Test
    public void testValidUserUpdateWithEntityCallback() {
        client.updateUser(username, token, newUser, entityHelper.callback(201));
        entityHelper.assertReceivedEntity(201);

        client.getUserInfo(username, userHelper.callback(200));
        userHelper.assertReceivedEntity(200);
        User userFromServer = userHelper.getReceivedEntity();

        assertThat(userFromServer.getUsername(), is(username));
        assertThat(userFromServer.getNickname(), is(newUser.getNickname()));
        assertThat(userFromServer.getEmail(), is(newUser.getEmail()));
        assertThat(userFromServer.getGender(), is(newUser.getGender()));
        assertThat(userFromServer.getBirthDate(), is(newUser.getBirthDate()));
    }

}
