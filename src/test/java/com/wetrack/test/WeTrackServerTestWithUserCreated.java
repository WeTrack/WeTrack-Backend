package com.wetrack.test;

import com.google.gson.JsonObject;
import com.wetrack.model.User;
import com.wetrack.util.CryptoUtils;
import org.junit.Before;

import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public abstract class WeTrackServerTestWithUserCreated extends WeTrackServerTest {

    protected User robertPeng = new User("robert-peng", "robert-peng", "Robert Peng");
    protected User windyChan = new User("windy-chan", "windy-chan", "Windy Chan");
    protected User mrDai = new User("mr-dai", "mr-dai", "Mr. Dai");
    protected User littleHearth = new User("little-hearth", "little-hearth", "Little Hearth");

    protected String notExistEntityId = "BlahBlahNotExist";

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

    protected Response userLogin(String username, String password) {
        JsonObject requestEntity = new JsonObject();
        requestEntity.addProperty("username", username);
        requestEntity.addProperty("password", CryptoUtils.md5Digest(password));
        return post("/login", requestEntity);
    }

}
