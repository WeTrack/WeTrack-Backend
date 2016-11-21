package com.wetrack.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.wetrack.dao.*;
import com.wetrack.dao.morphia.*;
import com.wetrack.json.*;
import com.wetrack.model.Location;
import com.wetrack.model.Notification;
import com.wetrack.model.User;
import com.wetrack.morphia.converter.EnumOrdinalConverter;
import com.wetrack.morphia.converter.Java8TimeConverter;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.converters.EnumConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

@Configuration
public class SpringConfig {
    private static final Logger LOG = LoggerFactory.getLogger(SpringConfig.class);

    static final String CONFIG_PATH = ".wetrack";
    static final String DB_CONFIG = "db.conf";

    static final String DEFAULT_HOST = "127.0.0.1";
    static final int DEFAULT_PORT = 12701;
    public static final String DEFAULT_DATABASE = "wetrack";
    static final String DEFAULT_USERNAME = "";
    static final String DEFAULT_PASSWORD = "";

    /** Name of package where the mapping classes are */
    private static final String PACKAGE_NAME = "com.wetrack.model";

    @Bean
    public MongoClient mongoClient() {
        String userHome = System.getProperty("user.home");
        LOG.debug("Fetched user home path `{}`", userHome);
        Path dbConfig = Paths.get(userHome, CONFIG_PATH, DB_CONFIG);
        if (Files.exists(dbConfig)) {
            try {
                Properties configs = new Properties();
                configs.load(Files.newInputStream(dbConfig));
                String host = configs.containsKey("Host") ? configs.getProperty("Host") : DEFAULT_HOST;
                int port = configs.containsKey("Port") ? Integer.parseInt(configs.getProperty("Port")) : DEFAULT_PORT;
                String username = configs.containsKey("Username") ? configs.getProperty("Username") : DEFAULT_USERNAME;
                char[] password = configs.containsKey("Password") ?
                        configs.getProperty("Password").toCharArray() : DEFAULT_PASSWORD.toCharArray();
                String database = configs.containsKey("Database") ? configs.getProperty("Database") : DEFAULT_DATABASE;

                LOG.debug("Connecting to {}:{} with username `{}`", host, port, username);
                MongoClient client = new MongoClient(
                        new ServerAddress(host, port),
                        Collections.singletonList(MongoCredential.createCredential(username, database, password))
                );
                Arrays.fill(password, '\0');
                return client;
            } catch (IOException e) {
                LOG.warn("Exception occurred when trying to load database config file: ", e);
            }
        }

        LOG.info("Database config file could not be loaded. Using default setting...");
        return new MongoClient();
    }

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
        return new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Location.class, new LocationTypeAdapter())
                .registerTypeAdapter(Notification.class, new NotificationAdapter())
                .registerTypeAdapter(User.class, new UserSerializer())
                .setPrettyPrinting()
                .create();
    }

    @Bean
    public Datastore datastore(Morphia morphia, MongoClient mongoClient) {
        String userHome = System.getProperty("user.home");
        Path dbConfig = Paths.get(userHome, CONFIG_PATH, DB_CONFIG);
        String database = DEFAULT_DATABASE;
        if (Files.exists(dbConfig)) {
            try {
                Properties configs = new Properties();
                configs.load(Files.newInputStream(dbConfig));
                if (configs.containsKey("Database"))
                    database = configs.getProperty("Database");
            } catch (IOException e) {
                LOG.warn("Exception occurred when trying to load database config file: ", e);
            }
        }
        Datastore datastore = morphia.createDatastore(mongoClient, database);
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

    @Bean
    public ChatMessageRepository chatMessageRepository(Datastore datastore) {
        ChatMessageRepositoryImpl chatMessageRepository = new ChatMessageRepositoryImpl();
        chatMessageRepository.setDatastore(datastore);
        return chatMessageRepository;
    }
}
