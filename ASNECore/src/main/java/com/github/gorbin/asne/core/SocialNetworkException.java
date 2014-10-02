package com.github.gorbin.asne.core;

public class SocialNetworkException extends RuntimeException {
    /**
     * Constructs a new exception with the specified detail message.
     * @param detailMessage the detail message constructed from social network error
     */
    public SocialNetworkException(String detailMessage) {
        super(detailMessage);
    }
}