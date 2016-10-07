package com.wetrack.dao;

import com.wetrack.model.User;

import java.util.Iterator;

public interface UserRepository extends Repository<User> {

    User findByUsername(String username);
    long countByUsername(String username);

    /**
     * Find all {@link User} that have the similar nickname
     *
     * @param nickname the given nickname
     * @return {@link User} that have the similar nickname
     */
    Iterator<User> findAllByNickname(String nickname);

}
