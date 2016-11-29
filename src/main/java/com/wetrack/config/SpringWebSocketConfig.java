package com.wetrack.config;

import com.wetrack.ws.ExceptionHandlerDecorator;
import com.wetrack.ws.WebSocketService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Import(SpringConfig.class)
@Configuration
@EnableWebSocket
public class SpringWebSocketConfig implements WebSocketConfigurer {

    @Bean
    public WebSocketService webSocketService() {
        return new WebSocketService();
    }

    @Bean
    public WebSocketHandler webSocketHandler() {
        return new ExceptionHandlerDecorator(webSocketService());
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler(), "/notifications").setAllowedOrigins("*");
    }
}
