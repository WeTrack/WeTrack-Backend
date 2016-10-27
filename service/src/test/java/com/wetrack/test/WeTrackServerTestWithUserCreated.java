package com.wetrack.test;

import com.wetrack.model.User;
import org.junit.Before;

import javax.ws.rs.core.Response;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public abstract class WeTrackServerTestWithUserCreated extends WeTrackServerTest {

    protected User robertPeng = new User("robert-peng", "Robert Peng", "robert-peng");
    protected User windyChan = new User("windy-chan", "Windy Chan", "windy-chan");
    protected User mrDai = new User("mr-dai", "Mr. Dai", "mr-dai");
    protected User littleHearth = new User("little-hearth", "Little Hearth", "little-hearth");

    protected String notExistUsername = "BlahBlahNotExist";

    @Before
    public void setUp() throws Exception {
        super.setUp();

        robertPeng.setEmail("robert.peng@example.com");
        robertPeng.setGender(User.Gender.Male);

        windyChan.setEmail("windy.chan@example.com");
        windyChan.setGender(User.Gender.Female);

        mrDai.setEmail("mr.dai@example.com");
        mrDai.setGender(User.Gender.Male);

        littleHearth.setEmail("little.hearth@example.com");
        littleHearth.setGender(User.Gender.Female);

        createUserWithAssertion(robertPeng);
        createUserWithAssertion(windyChan);
        createUserWithAssertion(mrDai);
        createUserWithAssertion(littleHearth);
    }

    protected void createUserWithAssertion(User user) {
        Response response = post("/users", user);

        assertThat(response.getStatus(), is(201));
        assertThat(response.getHeaderString("Location"), endsWith("/users/" + user.getUsername()));
    }

}
