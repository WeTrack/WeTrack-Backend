package com.wetrack.model;

public abstract class DbEntity<T> {
    public abstract T getId();
    public abstract void setId(T id);

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !this.getClass().isAssignableFrom(obj.getClass()))
            return false;
        return getId().equals(((DbEntity<T>) obj).getId());
    }
}
