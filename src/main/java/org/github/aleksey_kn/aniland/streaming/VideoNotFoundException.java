package org.github.aleksey_kn.aniland.streaming;

public class VideoNotFoundException extends RuntimeException {
    public VideoNotFoundException(final String videoId) {
        super("Video with name = " + videoId + " not found");
    }
}
