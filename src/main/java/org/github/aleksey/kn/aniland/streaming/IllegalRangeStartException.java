package org.github.aleksey.kn.aniland.streaming;

import java.io.Serial;

public class IllegalRangeStartException extends IllegalArgumentException {
    @Serial
    private static final long serialVersionUID = 1L;

    public IllegalRangeStartException(final String reason) {
        super(reason);
    }
}
