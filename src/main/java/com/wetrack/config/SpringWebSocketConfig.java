package com.wetrack.config;

import com.wetrack.ws.WebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Import(SpringConfig.class)
@Configuration
@EnableWebSocket
public class SpringWebSocketConfig implements WebSocketConfigurer {
    private static final Logger LOG = LoggerFactory.getLogger(SpringWebSocketConfig.class);

    @Bean
    public WebSocketService notificationService() {
        return new WebSocketService();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        LOG.info("Registering WebSocketService...");
        registry.addHandler(notificationService(), "/notifications").setAllowedOrigins("*");
    }
}
