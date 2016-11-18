package com.wetrack.dao;

import com.wetrack.model.Friend;

public interface FriendRepository extends Repository<String, Friend> {

    boolean isFriend(String usernameA, String usernameB);

}
