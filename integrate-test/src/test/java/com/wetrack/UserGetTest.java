package com.wetrack;

import com.wetrack.client.model.User;
import com.wetrack.client.test.EntityResponseTestHelper;
import com.wetrack.test.WeTrackIntegrateTestWithUserCreated;
import org.joda.time.LocalDate;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class UserGetTest extends WeTrackIntegrateTestWithUserCreated {

    private EntityResponseTestHelper<User> entityHelper = new EntityResponseTestHelper<>(gson);

    @Test
    public void testGetExistedUser() {
        client.getUserInfo(username, entityHelper.callback(200));
        entityHelper.assertReceivedEntity(200);

        User userFromServer = entityHelper.getReceivedEntity();
        assertThat(userFromServer.getUsername(), is(username));
        assertThat(userFromServer.getPassword(), nullValue());
        assertThat(userFromServer.getNickname(), is(nickname));
        assertThat(userFromServer.getEmail(), is(email));
        assertThat(userFromServer.getBirthDate(), is(LocalDate.parse(birthDate.toString())));
    }

    @Test
    public void testGetNotExistedUser() {
        client.getUserInfo(username + "Not important", entityHelper.callback(200));
        entityHelper.assertReceivedErrorMessage(404);
    }

}
