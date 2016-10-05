package com.wetrack.model;

public interface DbEntity<T> {
    T getId();
    void setId(T id);
}
