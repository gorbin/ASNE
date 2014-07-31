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

package com.vk.sdk.util;

import android.net.Uri;
import android.text.TextUtils;

import com.vk.sdk.api.model.VKAttachments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Helper class for join collections to strings
 */
public class VKStringJoiner {
    /**
     * Joins array of strings to one string with glue
     *
     * @param what Array of string to join
     * @param glue Glue for joined strings
     * @return Joined string
     */
    public static String join(String[] what, String glue) {
        return join(Arrays.asList(what), glue);
    }

    /**
     * Joins some collection to string with glue
     *
     * @param what Collection to join
     * @param glue Glue for joined strings
     * @return Joined string
     */
    public static String join(Collection<?> what, String glue) {
        return TextUtils.join(glue, what);
    }

    /**
     * Join parameters map into string, usually query string
     *
     * @param queryParams Map to join
     * @param isUri       Indicates that value parameters must be uri-encoded
     * @return Result query string, like k=v&k1=v1
     */
    public static String joinParams(Map<String, Object> queryParams, boolean isUri) {
        ArrayList<String> params = new ArrayList<String>(queryParams.size());
        for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof VKAttachments) {
                value = ((VKAttachments)value).toAttachmentsString();
            }
            params.add(String.format("%s=%s", entry.getKey(),
                    isUri ? Uri.encode(String.valueOf(value)) : String.valueOf(value)));
        }
        return join(params, "&");
    }

    /**
     * Join parameters from map without URI-encoding
     *
     * @param queryParams Map to join
     * @return Joined string
     */
    public static String joinParams(Map<String, Object> queryParams) {
        return joinParams(queryParams, false);
    }

    /**
     * Join parameters from map with URI-encoding
     *
     * @param queryParams Map to join
     * @return Joined string
     */
    public static String joinUriParams(Map<String, Object> queryParams) {
        return joinParams(queryParams, true);
    }
}
