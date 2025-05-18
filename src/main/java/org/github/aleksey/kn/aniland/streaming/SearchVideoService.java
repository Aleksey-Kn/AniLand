package org.github.aleksey.kn.aniland.streaming;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class SearchVideoService {
    public Resource searchVideo(final String videoName) throws MalformedURLException {
        final Path videoPath = Paths.get("video", videoName + ".mp4");
        final Resource videoResource = new UrlResource(videoPath.toUri());
        if (videoResource.exists()) {
            return videoResource;
        } else {
            throw new VideoNotFoundException(videoName);
        }
    }
}
