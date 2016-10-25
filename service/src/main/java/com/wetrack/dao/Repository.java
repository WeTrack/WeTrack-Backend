package com.wetrack.dao;

import com.wetrack.model.DbEntity;

public interface Repository<S, T extends DbEntity<S>> {
    T findById(S id);
    void insert(T t);
    void update(T t);
    void delete(T t);
}
