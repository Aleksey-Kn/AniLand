package org.github.aleksey.kn.aniland.streaming;

import java.io.Serial;

public class IllegalRangeStartException extends IllegalArgumentException {
    @Serial
    private static final long serialVersionUID = 1L;

    public IllegalRangeStartException(final long start, final long length) {
        super(String.format("Start position %d is out of range %d", start, length));
    }
}
