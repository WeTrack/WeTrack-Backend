package com.wetrack.dao;

import com.wetrack.model.DbEntity;

public interface Repository<T extends DbEntity> {
    T findById(Object id);
    void insert(T t);
    void update(T t);
    void delete(T t);
}
