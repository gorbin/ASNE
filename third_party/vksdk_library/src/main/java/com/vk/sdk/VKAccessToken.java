//
//  Copyright (c) 2014 VK.com
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy of
//  this software and associated documentation files (the "Software"), to deal in
//  the Software without restriction, including without limitation the rights to
//  use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
//  the Software, and to permit persons to whom the Software is furnished to do so,
//  subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in all
//  copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
//  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
//  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
//  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
//  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//

package com.vk.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.vk.sdk.api.VKParameters;
import com.vk.sdk.util.VKStringJoiner;
import com.vk.sdk.util.VKUtil;

import java.util.Map;

/**
 * Presents VK API access token that used for loading API methods and other stuff.
 */
public class VKAccessToken {
    public static final String ACCESS_TOKEN = "access_token";
    public static final String EXPIRES_IN = "expires_in";
    public static final String USER_ID = "user_id";
    public static final String SECRET = "secret";
    public static final String HTTPS_REQUIRED = "https_required";
    public static final String CREATED = "created";
    /**
     * String token for use in request parameters
     */
    public String accessToken = null;
    /**
     * Time when token expires
     */
    public int expiresIn = 0;
    /**
     * Current user id for this token
     */
    public String userId = null;
    /**
     * User secret to sign requests (if nohttps used)
     */
    public String secret = null;
    /**
     * If user sets "Always use HTTPS" setting in his profile, it will be true
     */
    public boolean httpsRequired = false;

    /**
     * Indicates time of token creation
     */
    public long created = 0;

    /**
     * Save token into specified file
     *
     * @param filePath path to file with saved token
     */
    public void saveTokenToFile(String filePath) {
        VKUtil.stringToFile(filePath, serialize());
    }

    /**
     * Save token into shared preferences with key
     *
     * @param ctx      Context for preferences
     * @param tokenKey Key for saving settings
     */
    public void saveTokenToSharedPreferences(Context ctx, String tokenKey) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(tokenKey, serialize());
        edit.commit();
    }

    /**
     * Removes token from preferences with specified key
     * @param ctx      Context for preferences
     * @param tokenKey Key for saving settings
     */
    public static void removeTokenAtKey(Context ctx, String tokenKey) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = prefs.edit();
        edit.remove(tokenKey);
        edit.commit();
    }

    private VKAccessToken() {
    }

    /**
     * Serialize token into string
     *
     * @return Serialized token string as query-string
     */
    protected String serialize() {
        VKParameters params = new VKParameters();
        params.put(ACCESS_TOKEN, accessToken);
        params.put(EXPIRES_IN, expiresIn);
        params.put(USER_ID, userId);
        params.put(CREATED, created);

        if (secret != null) {
            params.put(SECRET, secret);
        }
        if (httpsRequired) {
            params.put(HTTPS_REQUIRED, "1");
        }

        return VKStringJoiner.joinParams(params);
    }

    /**
     * Retrieve token from key-value query string
     *
     * @param urlString string that contains URL-query part with token. E.g. access_token=eee&expires_in=0...
     * @return parsed token
     */
    public static VKAccessToken tokenFromUrlString(String urlString) {
        if (urlString == null)
            return null;
        Map<String, String> parameters = VKUtil.explodeQueryString(urlString);

        return tokenFromParameters(parameters);
    }

    /**
     * Retrieve token from key-value map
     *
     * @param parameters map that contains token info
     * @return Parsed token
     */
    public static VKAccessToken tokenFromParameters(Map<String, String> parameters) {
        if (parameters == null || parameters.size() == 0)
            return null;
        VKAccessToken token = new VKAccessToken();
        try {
            token.accessToken = parameters.get(ACCESS_TOKEN);
            token.expiresIn = Integer.parseInt(parameters.get(EXPIRES_IN));
            token.userId = parameters.get(USER_ID);
            token.secret = parameters.get(SECRET);
            token.httpsRequired = false;

            if (parameters.containsKey(HTTPS_REQUIRED)) {
                token.httpsRequired = parameters.get(HTTPS_REQUIRED).equals("1");
            }
            if (parameters.containsKey(CREATED)) {
                token.created = Long.parseLong(parameters.get(CREATED));
            } else {
                token.created = System.currentTimeMillis();
            }
            return token;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Retrieves token from shared preferences
     *
     * @param ctx Context for preferences
     * @param key Key for retrieve token
     * @return Previously saved token or null
     */
    public static VKAccessToken tokenFromSharedPreferences(Context ctx, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return tokenFromUrlString(prefs.getString(key, null));
    }

    /**
     * Retrieve token from file. Token must be saved into file with saveTokenToFile method
     *
     * @param filePath path to file with saved token
     * @return Previously saved token or null
     */
    public static VKAccessToken tokenFromFile(String filePath) {
        try {
            String data = VKUtil.fileToString(filePath);
            return tokenFromUrlString(data);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Checks expiration time of token and returns result.
     *
     * @return true if token has expired, false otherwise.
     */
    public boolean isExpired() {
        return expiresIn > 0 && expiresIn * 1000 + created < System.currentTimeMillis();
    }
}