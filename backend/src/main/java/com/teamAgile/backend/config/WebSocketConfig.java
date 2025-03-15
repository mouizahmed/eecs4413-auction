package com.teamAgile.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import com.teamAgile.backend.websocket.AuctionWebSocketHandler;
import com.teamAgile.backend.websocket.JoinNotificationWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final JoinNotificationWebSocketHandler joinNotificationWebSocketHandler;
    private final AuctionWebSocketHandler auctionWebSocketHandler;

    public WebSocketConfig(JoinNotificationWebSocketHandler joinNotificationWebSocketHandler,
            AuctionWebSocketHandler auctionWebSocketHandler) {
        this.joinNotificationWebSocketHandler = joinNotificationWebSocketHandler;
        this.auctionWebSocketHandler = auctionWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(joinNotificationWebSocketHandler, "/ws/join-notifications")
                .setAllowedOrigins("*"); // In production, restrict to specific origins

        registry.addHandler(auctionWebSocketHandler, "/ws/auction-updates")
                .setAllowedOrigins("*"); // In production, restrict to specific origins
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
        return container;
    }
}