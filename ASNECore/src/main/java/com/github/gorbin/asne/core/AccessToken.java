package com.github.gorbin.asne.core;

public class AccessToken {
    public String token;
    public String secret;

    public AccessToken(String token, String secret) {
        this.token = token;
        this.secret = secret;
    }

    @Override
    public String toString() {
        return "AccessToken{" +
                "token='" + token + '\'' +
                ", secret='" + secret + '\'' +
                '}';
    }
}