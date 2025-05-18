package org.github.aleksey.kn.aniland.streaming;

import java.io.Serial;

public class VideoNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public VideoNotFoundException(final String videoId) {
        super("Video with name = " + videoId + " not found");
    }
}
