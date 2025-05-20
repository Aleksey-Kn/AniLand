package org.github.aleksey.kn.aniland.streaming;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log
public class StreamingController {
    private final SearchVideoService searchVideoService;

    @GetMapping("/stream/{filename}")
    public ResponseEntity<Resource> streamVideo(@PathVariable final String filename, @RequestHeader final HttpHeaders headers) throws IOException {
        final Resource videoResource = searchVideoService.searchVideo(filename);
        final List<HttpRange> ranges = headers.getRange();
        if (!ranges.isEmpty()) {
            return getPartialResourceResponseEntity(ranges.get(0), videoResource);
        }
        return ResponseEntity.ok()
                .contentType(new MediaType("video", "mp4"))
                .contentLength(videoResource.contentLength())
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .body(videoResource);
    }

    private ResponseEntity<Resource> getPartialResourceResponseEntity(final HttpRange requestRange, final Resource videoResource) throws IOException {
        final long contentLength = videoResource.contentLength();
        final long rangeStart = requestRange.getRangeStart(contentLength);
        if (rangeStart >= contentLength) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        final long rangeEnd = requestRange.getRangeEnd(contentLength);
        final long rangeLength = rangeEnd - rangeStart + 1;
        final byte[] partialContent = new byte[(int) rangeLength];
        try (var inputStream = videoResource.getInputStream()) {
            inputStream.skip(rangeStart);
            inputStream.read(partialContent, 0, (int) rangeLength);
        }
        final HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(new MediaType("video", "mp4"));
        responseHeaders.setContentLength(rangeLength);
        responseHeaders.setRange(List.of(HttpRange.createByteRange(rangeStart, rangeEnd)));
        responseHeaders.set(HttpHeaders.ACCEPT_RANGES, "bytes");
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .headers(responseHeaders)
                .body(new ByteArrayResource(partialContent));
    }

    @ExceptionHandler(VideoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleVideoNotFound(final VideoNotFoundException exception) {
        log.warning(exception.toString());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleIllegalRangeStart(final IllegalArgumentException exception) {
        log.warning(exception.toString());
    }
}
