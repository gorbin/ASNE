package com.github.gorbin.asne.core;

public class AccessToken {
    //*** Access token string for social network*/
    public String token;
    //*** Access secret string for social network if present*/
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