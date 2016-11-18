package com.wetrack;

import com.wetrack.client.model.User;
import com.wetrack.client.test.EntityResponseHelper;
import com.wetrack.test.WeTrackIntegrateTestWithUserCreated;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class UserGetTest extends WeTrackIntegrateTestWithUserCreated {

    private EntityResponseHelper<User> entityHelper = new EntityResponseHelper<>(gson);

    @Test
    public void testGetExistedUser() {
        client.getUserInfo(robertPeng.getUsername(), entityHelper.callback(200));
        entityHelper.assertReceivedEntity(200);

        User userFromServer = entityHelper.getReceivedEntity();
        assertThat(userFromServer.getUsername(), is(robertPeng.getUsername()));
        assertThat(userFromServer.getPassword(), nullValue());
        assertThat(userFromServer.getNickname(), is(robertPeng.getNickname()));
        assertThat(userFromServer.getEmail(), is(robertPeng.getEmail()));
        assertThat(userFromServer.getBirthDate(), is(robertPeng.getBirthDate()));
    }

    @Test
    public void testGetNotExistedUser() {
        client.getUserInfo(username + "Not important", entityHelper.callback(200));
        entityHelper.assertReceivedErrorMessage(404);
    }

}
