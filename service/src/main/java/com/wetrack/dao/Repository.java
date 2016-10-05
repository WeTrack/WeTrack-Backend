package com.wetrack.dao;

import com.wetrack.model.Entity;

public interface Repository<T extends Entity> {
    T findById(Object id);
    void insert(T t);
    void update(T t);
    void delete(T t);
}
