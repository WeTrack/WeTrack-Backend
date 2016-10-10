package com.wetrack.dao.morphia;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.wetrack.config.SpringConfig;
import com.wetrack.config.SpringTestConfig;
import com.wetrack.dao.Repository;
import com.wetrack.dao.UserRepository;
import com.wetrack.model.DbEntity;
import com.wetrack.model.User;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringTestConfig.class)
public class UserRepositoryTest {

    @Autowired private UserRepository userRepository;
    @Autowired private MongoClient client;

    private String username = "robert-peng";
    private String nickname = "Robert Peng";
    private String password = "I\'m a password";
    private String email = "robert.peng@example.com";

    private MongoCollection<Document> users;

    @Before
    public void setUp() {
        users = client.getDatabase(SpringConfig.DATABASE_NAME).getCollection("users");
    }

    @After
    public void cleanUp() {
        // Delete the inserted user token
        users.deleteOne(new Document("_id", username));
    }

    /**
     * Unit test for {@link Repository#insert(DbEntity)} on {@link User} type
     */
    @Test
    public void testUserDaoInsert() {
        User user = new User(username, password, nickname);
        user.setEmail(email);
        user.setGender(User.Gender.Male);
        user.setBirthDate(LocalDate.now());

        // Insert the user via the testing method
        userRepository.insert(user);

        // Verify the result
        Document userInDB = users.find(new Document("_id", username)).first();

        assertNotNull(userInDB);
        assertThat(userInDB.getString("nickname"), is(nickname));
        assertThat(userInDB.getString("password"), is(password));
        assertThat(userInDB.getString("email"), is(email));
        assertThat(userInDB.getInteger("gender"), is(0));
    }

    /**
     * Unit test for {@link Repository#delete(DbEntity)} on {@link User} type
     * and {@link UserRepository#findByUsername(String)}
     */
    @Test
    public void testUserDelete() {
        Document userDoc = new Document("_id", username).append("nickname", nickname).append("password", password);
        users.insertOne(userDoc);

        // Delete the user via the testing method
        User user = new User(username, "anything", "anything");
        userRepository.delete(user);

        assertThat(users.count(new Document("_id", username)), is(0L));
    }

    /**
     * Unit test for {@link Repository#findById(Object)} on {@link User} type
     */
    @Test
    public void testUserQuery() {
        User user = new User(username, password, nickname);
        user.setEmail(email);
        user.setBirthDate(LocalDate.now());
        user.setGender(User.Gender.Male);

        userRepository.insert(user);

        // Query via the testing method
        user = userRepository.findById(username);
        assertThat(user.getId(), is(username));
        assertThat(user.getUsername(), is(username));
        assertThat(user.getNickname(), is(nickname));
        assertThat(user.getPassword(), is(password));
        assertThat(user.getEmail(), is(email));
        assertThat(user.getGender(), is(User.Gender.Male));
        assertThat(user.getBirthDate(), is(LocalDate.now()));

        user = userRepository.findByUsername(username);
        assertThat(user.getId(), is(username));
        assertThat(user.getUsername(), is(username));
        assertThat(user.getNickname(), is(nickname));
        assertThat(user.getPassword(), is(password));
        assertThat(user.getEmail(), is(email));
        assertThat(user.getGender(), is(User.Gender.Male));
        assertThat(user.getBirthDate(), is(LocalDate.now()));
    }

    /**
     * Unit test for {@link Repository#update(DbEntity)} on {@link User} type
     */
    @Test
    public void testUserUpdate() {
        User user = new User(username, password, nickname);
        user.setEmail(email);
        user.setGender(User.Gender.Male);
        user.setBirthDate(LocalDate.now());
        userRepository.insert(user);

        // Update via the testing method
        String newNickname = nickname.concat("Windy Chen");
        String newPassword = password.concat("This is a password");
        String newEmail = email.concat("Meow");
        user = new User(username, newPassword, newNickname);
        user.setEmail(newEmail);
        user.setBirthDate(LocalDate.now().plusDays(1));
        user.setGender(User.Gender.Female);

        userRepository.update(user);

        // Verify the result
        user = userRepository.findByUsername(username);
        assertThat(user, notNullValue());
        assertThat(user.getUsername(), is(username));
        assertThat(user.getPassword(), is(newPassword));
        assertThat(user.getNickname(), is(newNickname));
        assertThat(user.getEmail(), is(newEmail));
        assertThat(user.getGender(), is(User.Gender.Female));
        assertThat(user.getBirthDate(), is(LocalDate.now().plusDays(1)));
    }
}
