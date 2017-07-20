package com.simon.demostompwebsocketclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;

@SpringBootApplication
public class DemoStompWebsocketClientApplication implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DemoStompWebsocketClientApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DemoStompWebsocketClientApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new StringMessageConverter());
        stompClient.connect("ws://localhost:8080/notification", new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                super.afterConnected(session, connectedHeaders);
                session.subscribe("/topic/demotenant1", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders stompHeaders) {
                        return String.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders stompHeaders, Object o) {
                        logger.info("tenant 1 received message: {}", o);
                    }
                });
            }
        });

        webSocketClient = new StandardWebSocketClient();
        stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new StringMessageConverter());
        stompClient.connect("ws://localhost:8080/notification", new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                super.afterConnected(session, connectedHeaders);
                session.subscribe("/topic/demotenant2", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders stompHeaders) {
                        return String.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders stompHeaders, Object o) {
                        logger.info("tenant 2 received message: {}", o);
                    }
                });
            }
        });
        synchronized (this) {
            while (!Thread.currentThread().isInterrupted()) {
                wait();
            }
        }
    }
}
