package org.github.aleksey.kn.aniland.streaming;

import org.github.aleksey.kn.aniland.init.TestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StreamingControllerTest extends TestBase {

    @Test
    void streamVideo() {
    }

    @Test
    void handleVideoNotFound() {
    }

    @Test
    void handleIllegalRangeStart() {
        webClient.get().uri("/stream/pj")
                .header("Range", "bytes=1000-100")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().jsonPath("$.reason").isEqualTo("Range start must be less than end");
    }
}