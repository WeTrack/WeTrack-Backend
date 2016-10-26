package com.wetrack.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;
import com.wetrack.dao.*;
import com.wetrack.dao.morphia.*;
import com.wetrack.json.LocalDateTimeTypeAdapter;
import com.wetrack.json.LocalDateTypeAdapter;
import com.wetrack.json.LocationTypeAdapter;
import com.wetrack.model.Location;
import com.wetrack.morphia.converter.EnumOrdinalConverter;
import com.wetrack.morphia.converter.Java8TimeConverter;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.converters.EnumConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
        morphia.getMapper().getConverters().addConverter(new Java8TimeConverter());

        return morphia;
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Location.class, new LocationTypeAdapter())
                .create();
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
    public LocationRepository locationRepository(Datastore datastore) {
        LocationRepositoryImpl locationRepository = new LocationRepositoryImpl();
        locationRepository.setDatastore(datastore);
        return locationRepository;
    }

    @Bean
    public FriendRepository friendRepository(Datastore datastore) {
        FriendRepositoryImpl friendRepository = new FriendRepositoryImpl();
        friendRepository.setDatastore(datastore);
        return friendRepository;
    }

    @Bean
    public ChatRepository chatRepository(Datastore datastore) {
        ChatRepositoryImpl chatRepository = new ChatRepositoryImpl();
        chatRepository.setDatastore(datastore);
        return chatRepository;
    }
}
