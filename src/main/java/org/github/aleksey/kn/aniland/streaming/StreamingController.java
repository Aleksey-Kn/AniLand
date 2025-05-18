package org.github.aleksey.kn.aniland.streaming;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
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
public class StreamingController {
    private final SearchVideoService searchVideoService;

    @GetMapping("/stream/{filename}")
    public ResponseEntity<Resource> streamVideo(@PathVariable final String filename, @RequestHeader final HttpHeaders headers) throws IOException {
        final Resource videoResource = searchVideoService.searchVideo(filename);
        final long contentLength = videoResource.contentLength();
        final List<HttpRange> ranges = headers.getRange();
        if (!ranges.isEmpty()) {
            final HttpRange range = ranges.get(0);
            final long rangeStart = range.getRangeStart(contentLength);
            final long rangeEnd = range.getRangeEnd(contentLength);
            if (rangeStart < 0) {
                throw new IllegalRangeStartException("Range start must be non-negative");
            } else if (rangeStart >= rangeEnd) {
                throw new IllegalRangeStartException("Range start must be less than end");
            }
            final long rangeLength = rangeEnd - rangeStart + 1;
            final byte[] partialContent = new byte[(int) rangeLength];
            try (var inputStream = videoResource.getInputStream()) {
                inputStream.skip(rangeStart);
                inputStream.read(partialContent, 0, (int) rangeLength);
            }
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(rangeLength))
                    .header(HttpHeaders.CONTENT_RANGE, "bytes=" + rangeStart + "-" + rangeEnd + "/" + contentLength)
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .body(new ByteArrayResource(partialContent));
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength))
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .body(videoResource);
    }

    @ExceptionHandler(VideoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleVideoNotFound(final VideoNotFoundException exception) {
        return "{\"reason\":\"" + exception.getMessage() + "\"}";
    }

    @ExceptionHandler(IllegalRangeStartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalRangeStart(final IllegalRangeStartException exception) {
        return "{\"reason\":\"" + exception.getMessage() + "\"}";
    }
}
