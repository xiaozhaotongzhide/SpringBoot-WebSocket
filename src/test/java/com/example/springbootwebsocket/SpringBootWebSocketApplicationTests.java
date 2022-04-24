package com.example.springbootwebsocket;

import com.example.springbootwebsocket.service.WebSocketServer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = SpringBootWebSocketApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringBootWebSocketApplicationTests {

    @Test
    void contextLoads() {
    }

}
