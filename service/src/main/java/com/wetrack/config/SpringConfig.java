package com.wetrack.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;
import com.wetrack.dao.UserRepository;
import com.wetrack.dao.UserTokenRepository;
import com.wetrack.dao.morphia.UserRepositoryImpl;
import com.wetrack.dao.morphia.UserTokenRepositoryImpl;
import com.wetrack.morphia.converter.EnumOrdinalConverter;
import com.wetrack.morphia.converter.LocalDateConverter;
import com.wetrack.service.AuthenService;
import com.wetrack.service.UserService;
import com.wetrack.util.GsonJerseyProvider;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.converters.EnumConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    /** Name of the database */
    public static final String DATABASE_NAME = "wetrack";
    /** Name of package where the mapping classes are */
    public static final String PACKAGE_NAME = "com.wetrack.model";

    @Bean
    public Morphia morphia() {
        Morphia morphia = new Morphia();
        morphia.mapPackage(PACKAGE_NAME);

        morphia.getMapper().getConverters().removeConverter(new EnumConverter());
        morphia.getMapper().getConverters().addConverter(new EnumOrdinalConverter());
        morphia.getMapper().getConverters().addConverter(new LocalDateConverter());

        return morphia;
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder().create();
    }

    @Bean
    public MongoClient mongoClient() {
        return new MongoClient();
    }

    @Bean
    public Datastore datastore(Morphia morphia, MongoClient mongoClient) {
        Datastore datastore = morphia.createDatastore(mongoClient, DATABASE_NAME);
        datastore.ensureIndexes();
        return datastore;
    }

    @Bean
    public UserRepository userRepository(Datastore datastore) {
        UserRepositoryImpl userRepository = new UserRepositoryImpl();
        userRepository.setDatastore(datastore);

        return userRepository;
    }

    @Bean
    public UserTokenRepository userTokenRepository(Datastore datastore) {
        UserTokenRepositoryImpl userTokenRepository = new UserTokenRepositoryImpl();
        userTokenRepository.setDatastore(datastore);

        return userTokenRepository;
    }

    @Bean
    public UserService userService() {
        UserService userService = new UserService();
        return userService;
    }

    @Bean
    public AuthenService authenService() {
        AuthenService authenService = new AuthenService();
        return authenService;
    }

    @Bean
    public GsonJerseyProvider gsonJerseyProvider() {
        GsonJerseyProvider gsonJerseyProvider = new GsonJerseyProvider();
        return gsonJerseyProvider;
    }
}
