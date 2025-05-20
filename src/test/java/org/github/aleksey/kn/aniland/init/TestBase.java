package org.github.aleksey.kn.aniland.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

@AutoConfigureWebTestClient
@Import(TestcontainersConfiguration.class)
@SpringBootTest
public abstract class TestBase {
    @Autowired
    protected WebTestClient webClient;
}
