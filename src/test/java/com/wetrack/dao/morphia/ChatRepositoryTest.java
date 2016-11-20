package com.wetrack.dao.morphia;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.wetrack.config.SpringConfig;
import com.wetrack.config.SpringTestConfig;
import com.wetrack.dao.ChatRepository;
import com.wetrack.dao.UserRepository;
import com.wetrack.model.Chat;
import com.wetrack.model.User;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringTestConfig.class)
public class ChatRepositoryTest {

    @Autowired private MongoClient client;
    @Autowired private UserRepository userRepository;
    @Autowired private ChatRepository chatRepository;

    private MongoCollection<Document> chats;

    private User userA = new User("robert-peng", null, null);
    private User userB = new User("windy-chan", null, null);
    private User userC = new User("mr-dai", null, null);
    private User userD = new User("little-hearth", null, null);

    private String chatName = "Robert's Family";
    private String anotherChatName = "Windy's Family";

    @Before
    public void setUp() {
        chats = client.getDatabase(SpringConfig.DEFAULT_DATABASE).getCollection("chats");
        userRepository.insert(userA);
        userRepository.insert(userB);
        userRepository.insert(userC);
        userRepository.insert(userD);
    }

    @After
    public void tearDown() {
        userRepository.delete(userA);
        userRepository.delete(userB);
        userRepository.delete(userC);
        userRepository.delete(userD);

        chats.deleteMany(new Document("name", chatName));
        chats.deleteMany(new Document("name", anotherChatName));
    }

    @Test
    public void testGetChatList() {
        Chat chat = new Chat(chatName);
        chat.addMember(userA);
        chat.addMember(userB);
        chat.addMember(userC);
        chat.addMember(userD);
        chatRepository.insert(chat);

        chat = new Chat(anotherChatName);
        chat.addMember(userA);
        chat.addMember(userC);
        chatRepository.insert(chat);

        assertThat(chatRepository.getChatListByUsername(userA.getUsername()).size(), is(2));
        assertThat(chatRepository.getChatListByUsername(userB.getUsername()).size(), is(1));
        assertThat(chatRepository.getChatListByUsername(userC.getUsername()).size(), is(2));
        assertThat(chatRepository.getChatListByUsername(userD.getUsername()).size(), is(1));
    }

    @Test
    public void testIsMember() {
        Chat chat = new Chat(chatName);
        chat.addMember(userA);
        chat.addMember(userC);
        chatRepository.insert(chat);

        assertThat(chatRepository.isMember(chat.getId(), userA.getUsername()), is(true));
        assertThat(chatRepository.isMember(chat.getId(), userB.getUsername()), is(false));
        assertThat(chatRepository.isMember(chat.getId(), userC.getUsername()), is(true));
        assertThat(chatRepository.isMember(chat.getId(), userD.getUsername()), is(false));
    }

    @Test
    public void testChatInsert() {
        Chat chat = new Chat(chatName);
        chat.getMemberNames().add(userA.getUsername());
        chatRepository.insert(chat);

        Document chatInDB = chats.find(new Document("name", chatName)).first();
        assertThat(chatInDB, notNullValue());
        assertThat(chatInDB.getString("name"), is(chatName));
        ArrayList<String> membersInDoc = chatInDB.get("memberNames", ArrayList.class);
        assertThat(membersInDoc.size(), is(1));
        assertThat(membersInDoc.get(0), is(userA.getUsername()));
    }

    @Test
    public void testChatDuplicateInsert() {
        Chat chat = new Chat(chatName);
        chat.addMember(userA);
        chat.addMember(userB);
        chat.addMember(userC);
        chat.addMember(userD);

        chat.addMember(userA);

        chatRepository.insert(chat);
        Document chatInDB = chats.find(new Document("name", chatName)).first();
        ArrayList<String> membersInDoc = chatInDB.get("memberNames", ArrayList.class);
        assertThat(membersInDoc.size(), is(4));
    }

    @Test
    public void testChatUpdate() {
        Chat chat = new Chat(chatName);
        chat.addMember(userA);
        chatRepository.insert(chat);

        // Incremental Update
        chat.addMember(userB);
        chatRepository.update(chat);
        Document chatInDB = chats.find(new Document("name", chatName)).first();
        ArrayList<String> membersInDoc = chatInDB.get("memberNames", ArrayList.class);
        assertThat(membersInDoc.size(), is(2));
        assertThat(membersInDoc.get(0), is(userA.getUsername()));
        assertThat(membersInDoc.get(1), is(userB.getUsername()));

        // Decremental Update
        chat.removeMember(userA);
        chatRepository.update(chat);
        chatInDB = chats.find(new Document("name", chatName)).first();
        membersInDoc = chatInDB.get("memberNames", ArrayList.class);
        assertThat(membersInDoc.size(), is(1));
        assertThat(membersInDoc.get(0), is(userB.getUsername()));
    }

}
