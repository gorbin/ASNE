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

import java.util.ArrayList;

/**
 * Scope constants used for authorization
 */
public class VKScope {
    public static final String NOTIFY = "notify";
    public static final String FRIENDS = "friends";
    public static final String PHOTOS = "photos";
    public static final String AUDIO = "audio";
    public static final String VIDEO = "video";
    public static final String DOCS = "docs";
    public static final String NOTES = "notes";
    public static final String PAGES = "pages";
    public static final String STATUS = "status";
    public static final String WALL = "wall";
    public static final String GROUPS = "groups";
    public static final String MESSAGES = "messages";
    public static final String NOTIFICATIONS = "notifications";
    public static final String STATS = "stats";
    public static final String ADS = "ads";
    public static final String OFFLINE = "offline";
    public static final String NOHTTPS = "nohttps";

    /**
     * Converts integer value of permissions into arraylist of constants
     * @param permissionsValue integer permissions value
     * @return ArrayList contains string constants of permissions (scope)
     */
    public static ArrayList<String> parseVkPermissionsFromInteger(int permissionsValue) {
        ArrayList<String> res = new ArrayList<String>();
        if ((permissionsValue & 1) > 0) res.add(NOTIFY);
        if ((permissionsValue & 2) > 0) res.add(FRIENDS);
        if ((permissionsValue & 4) > 0) res.add(PHOTOS);
        if ((permissionsValue & 8) > 0) res.add(AUDIO);
        if ((permissionsValue & 16) > 0) res.add(VIDEO);
        if ((permissionsValue & 128) > 0) res.add(PAGES);
        if ((permissionsValue & 1024) > 0) res.add(STATUS);
        if ((permissionsValue & 2048) > 0) res.add(NOTES);
        if ((permissionsValue & 4096) > 0) res.add(MESSAGES);
        if ((permissionsValue & 8192) > 0) res.add(WALL);
        if ((permissionsValue & 32768) > 0) res.add(ADS);
        if ((permissionsValue & 65536) > 0) res.add(OFFLINE);
        if ((permissionsValue & 131072) > 0) res.add(DOCS);
        if ((permissionsValue & 262144) > 0) res.add(GROUPS);
        if ((permissionsValue & 524288) > 0) res.add(NOTIFICATIONS);
        if ((permissionsValue & 1048576) > 0) res.add(STATS);
        return res;
    }
}
