package com.wetrack;

import com.wetrack.client.model.Message;
import com.wetrack.client.model.User;
import com.wetrack.client.model.UserToken;
import com.wetrack.client.test.EntityResponseTestHelper;
import com.wetrack.client.test.MessageResponseTestHelper;
import com.wetrack.test.Utils;
import com.wetrack.test.WeTrackIntegrateTest;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UserUpdateTest extends WeTrackIntegrateTest {

    private MessageResponseTestHelper messageHelper = new MessageResponseTestHelper();
    private EntityResponseTestHelper<Message> entityHelper = new EntityResponseTestHelper<>(gson);
    private EntityResponseTestHelper<User> userHelper = new EntityResponseTestHelper<>(gson);

    private User oldUser;
    private User newUser;

    private String username;
    private String token;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        oldUser = Utils.loadExampleUser(gson);
        MessageResponseTestHelper messageHelper = new MessageResponseTestHelper();
        client.createUser(oldUser, messageHelper.callback(201));
        messageHelper.assertReceivedMessage(true);

        username = oldUser.getUsername();

        newUser = Utils.loadExampleUser(gson, "updated_user.json");
        assertThat(newUser.getUsername(), is(username));

        // Login
        EntityResponseTestHelper<UserToken> entityHelper = new EntityResponseTestHelper<>(gson);
        client.userLogin(username, oldUser.getPassword(), entityHelper.callback(200));
        entityHelper.assertReceivedEntity(200);
        token = entityHelper.getReceivedEntity().getToken();
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
        client.updateUser(username, token, newUser, messageHelper.callback(201));
        messageHelper.assertReceivedMessage(true);

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
