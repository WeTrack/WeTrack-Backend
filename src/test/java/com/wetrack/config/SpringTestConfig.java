package com.wetrack.config;

import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(SpringConfig.class)
public class SpringTestConfig {
    @Bean
    public MongoClient mongoClient() {
        return new Fongo("In Memory Mongo").getMongo();
    }
}
