package org.github.aleksey.kn.aniland.streaming;

import lombok.SneakyThrows;
import org.github.aleksey.kn.aniland.init.TestBase;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class StreamingControllerTest extends TestBase {

    @Test
    @SneakyThrows
    void streamVideo() {
        final byte[] pjData = new byte[100];
        try (InputStream pjInputStream = Files.newInputStream(Path.of("src/test/resources/pj.mp4"))) {
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
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void handleVideoNotFound() {
        webClient.get().uri("/stream/notfound")
                .header("Range", "bytes=1000-100")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void handleTooLargeRangeStart() {
        webClient.get().uri("/stream/pj")
                .header("Range", "bytes=1000-100")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void handleNegativeRangeStart() {
        webClient.get().uri("/stream/pj")
                .header("Range", "bytes=-10-100")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void handleRangeStartMoreFileSize() {
        webClient.get().uri("/stream/pj")
                .header("Range", String.format("bytes=%d-%d", Integer.MAX_VALUE, (long) Integer.MAX_VALUE + 100))
                .exchange()
                .expectStatus().isNoContent();
    }
}
