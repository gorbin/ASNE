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

/**
 * Privacy.java
 * VK Dev
 *
 * Created by Babichev Vitaly on 29.09.13.
 * Copyright (c) 2013 VK. All rights reserved.
 */
package com.vk.sdk.api.model;

import org.json.JSONObject;

/**
 * Describes privacy of some VK object
 */
public class VKPrivacy {

    VKPrivacy() {}

    /**
     * Access for all users
     */
    public final static int PRIVACY_ALL = 0;

    /**
     * Access only for friends.
     */
    public final static int PRIVACY_FRIENDS = 1;

    /**
     * Access only for friends and friend of friends.
     */
    public final static int PRIVACY_FRIENDS_OF_FRIENDS = 2;

    /**
     * Access only for logged user.
     */
    public final static int PRIVACY_NOBODY = 3;

    /**
     * Unknown privacy format
     */
    public final static int PRIVACY_UNKNOWN = 4;

    /**
     * Parses privacy in int format from privacy_view format.
     * @see <a href="http://vk.com/dev/privacy_setting">http://vk.com/dev/privacy_setting</a>
     */
    public static int parsePrivacy(JSONObject privacyView) {
        int privacy = 0;
        if(privacyView != null) {
            String type = privacyView.optString("type");
            if("all".equals(type)) {
                privacy = PRIVACY_ALL;
            } else if("friends".equals(type)) {
                privacy = PRIVACY_FRIENDS;
            } else if("friends_of_friends".equals(type)) {
                privacy = PRIVACY_FRIENDS_OF_FRIENDS;
            } else if("nobody".equals(type)) {
                privacy = PRIVACY_NOBODY;
            } else {
                privacy = PRIVACY_UNKNOWN;
            }
        }
        return privacy;
    }
}