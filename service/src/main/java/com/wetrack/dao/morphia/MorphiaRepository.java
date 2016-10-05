package com.wetrack.dao.morphia;

import com.wetrack.dao.Repository;
import com.wetrack.model.Entity;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

public abstract class MorphiaRepository<T extends Entity> implements Repository<T> {

    private Datastore datastore;

    public void setDatastore(Datastore datastore) {
        this.datastore = datastore;
    }

    public Datastore getDatastore() {
        return datastore;
    }

    protected abstract Class<T> getEntityClass();

    protected Query<T> createQuery() {
        return datastore.createQuery(getEntityClass());
    }

    @Override
    public T findById(Object id) {
        return createQuery().field("_id").equal(id).get();
    }

    @Override
    public void insert(T t) {
        datastore.save(t);
    }

    @Override
    public void update(T t) {
        datastore.save(t);
    }

    @Override
    public void delete(T t) {
        datastore.delete(t);
    }

}
