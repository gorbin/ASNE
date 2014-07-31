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

package com.vk.sdk.api.model;

import java.util.ArrayList;

/**
 * Application Access Permissions
 * @see <a href="http://vk.com/dev/permissions">http://vk.com/dev/permissions</a>
 */
@SuppressWarnings("unused")
public class VKScopes {

    private VKScopes() {}

    /**
     * User allowed to send notifications to him/her.
     */
    public final static String NOTIFY = "notify";
    
    /**
     * Access to friends.
     */
    public final static String FRIENDS = "friends";
    
    /**
     * Access to photos.
     */
    public final static String PHOTOS = "photos";  
    
    /**
     * Access to audios.
     */
    public final static String AUDIO = "audio";
    
    /**
     * Access to videos.
     */
    public final static String VIDEO = "video"; 
    
    /**
     * Access to documents.
     */
    public final static String DOCS = "docs";
    
    /**
     * Access to user notes.
     */
    public final static String NOTES = "notes";
    
    /**
     * Access to wiki pages.
     */
    public final static String PAGES = "pages";
    
    /**
     * Access to user status.
     */
    public final static String STATUS = "status";
    
    /**
     * Access to offers (obsolete methods).
     */
    @Deprecated
    public final static String OFFERS = "offers";
    
    /**
     * Access to questions (obsolete methods).
     */
    @Deprecated
    public final static String QUESTIONS = "questions";
    
    /**
     * Access to standard and advanced methods for the wall.
     */
    public final static String WALL = "wall";
    
    /**
     * Access to user groups.
     */
    public final static String GROUPS = "groups";
    
    /**
     * Access to advanced methods for messaging.
     */
    public final static String MESSAGES = "messages";
    
    /**
     * Access to notifications about answers to the user.
     */
    public final static String NOTIFICATIONS = "notifications";
    
    /**
     * Access to statistics of user groups and applications where he/she is an administrator.
     */
    public final static String STATS = "stats";
    
    /**
     * Access to advanced methods for <a href="http://vk.com/dev/ads">Ads API</a>.
     */
    public final static String ADS = "ads";
    
    /**
     * Access to API at any time from a third party server.
     */
    public final static String OFFLINE = "offline"; 
    
    /**
     * Possibility to make API requests without HTTPS. <br />
     * <b>Note that this functionality is under testing and can be changed.</b>
     */
    public final static String NOHTTPS = "nohttps";

    /**
     * Converts integer value of permissions into arraylist of constants
     * @param permissions integer permissions value
     * @return ArrayList contains string constants of permissions (scope)
     */
    public static ArrayList<String> parse(int permissions) {
        ArrayList<String> result = new ArrayList<String>();
        if ((permissions & 1) > 0) result.add(NOTIFY);
        if ((permissions & 2) > 0) result.add(FRIENDS);
        if ((permissions & 4) > 0) result.add(PHOTOS);
        if ((permissions & 8) > 0) result.add(AUDIO);
        if ((permissions & 16) > 0) result.add(VIDEO);
        if ((permissions & 128) > 0) result.add(PAGES);
        if ((permissions & 1024) > 0) result.add(STATUS);
        if ((permissions & 2048) > 0) result.add(NOTES);
        if ((permissions & 4096) > 0) result.add(MESSAGES);
        if ((permissions & 8192) > 0) result.add(WALL);
        if ((permissions & 32768) > 0) result.add(ADS);
        if ((permissions & 65536) > 0) result.add(OFFLINE);
        if ((permissions & 131072) > 0) result.add(DOCS);
        if ((permissions & 262144) > 0) result.add(GROUPS);
        if ((permissions & 524288) > 0) result.add(NOTIFICATIONS);
        if ((permissions & 1048576) > 0) result.add(STATS);
        return result;
    }
    
}
