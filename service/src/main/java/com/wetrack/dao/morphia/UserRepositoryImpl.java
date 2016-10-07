package com.wetrack.dao.morphia;

import com.wetrack.dao.UserRepository;
import com.wetrack.model.User;

import java.util.Iterator;

public class UserRepositoryImpl extends MorphiaRepository<User> implements UserRepository {
    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    public User findByUsername(String username) {
        return findById(username);
    }

    @Override
    public Iterator<User> findAllByNickname(String nickname) {
        return createQuery().search(nickname).iterator();
    }
}