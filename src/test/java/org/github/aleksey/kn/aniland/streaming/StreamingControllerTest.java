package org.github.aleksey.kn.aniland.streaming;

import lombok.SneakyThrows;
import org.github.aleksey.kn.aniland.init.TestBase;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;

import static org.assertj.core.api.Assertions.assertThat;

class StreamingControllerTest extends TestBase {

    @Test
    @SneakyThrows
    void streamVideo() {
        final byte[] pjData = new byte[100];
        try (final FileInputStream pjInputStream = new FileInputStream("src/test/resources/pj.mp4")) {
            pjInputStream.read(pjData, 0, 100);
        }

        final byte[] response = webClient.get().uri("/stream/pj")
                .header("Range", "bytes=0-99")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody().returnResult().getResponseBody();

        assertThat(response).isEqualTo(pjData);
    }

    @Test
    void handleVideoNotFound() {
        webClient.get().uri("/stream/notfound")
                .header("Range", "bytes=1000-100")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void handleTooLargeRangeStart() {
        webClient.get().uri("/stream/pj")
                .header("Range", "bytes=1000-100")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void handleNegativeRangeStart() {
        webClient.get().uri("/stream/pj")
                .header("Range", "bytes=-10-100")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void handleRangeStartMoreFileSize() {
        webClient.get().uri("/stream/pj")
                .header("Range", String.format("bytes=%d-%d", Integer.MAX_VALUE, (long) Integer.MAX_VALUE + 100))
                .exchange()
                .expectStatus().isNoContent();
    }
}