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

import android.os.Parcel;
import android.text.TextUtils;

import org.json.JSONObject;


/**
 * Community object describes a community.
 */
@SuppressWarnings("unused")
public class VKApiCommunity extends VKApiOwner implements android.os.Parcelable, Identifiable {

    private final static String TYPE_GROUP = "group";
    private final static String TYPE_PAGE = "page";
    private final static String TYPE_EVENT = "event";
    final static String PHOTO_50 = "http://vk.com/images/community_50.gif";
    final static String PHOTO_100 = "http://vk.com/images/community_100.gif";

    /**
     * Community name
     */
    public String name;

    /**
     * Screen name of the community page (e.g. apiclub or club1).
     */
    public String screen_name;

    /**
     * Whether the community is closed
     * @see {@link com.vk.sdk.api.model.VKCommunity.Status}
     */
    public int is_closed;

    /**
     * Whether a user is the community manager
     */
    public boolean is_admin;

    /**
     * Rights of the user
     * @see {@link AdminLevel}
     */
    public int admin_level;

    /**
     * Whether a user is a community member
     */
    public boolean is_member;

    /**
     * Community type
     * @see {@link com.vk.sdk.api.model.VKCommunity.Type}
     */
    public int type;

    /**
     * URL of the 50px-wide community logo.
     */
    public String photo_50;

    /**
     * URL of the 100px-wide community logo.
     */
    public String photo_100;

    /**
     * URL of the 200px-wide community logo.
     */
    public String photo_200;

    /**
     * {@link #photo_50}, {@link #photo_100}, {@link #photo_200} included here in Photo Sizes format.
     */
    public VKPhotoSizes photo = new VKPhotoSizes();

    /**
     * Fills a community object from JSONObject
     * @param from JSONObject describes community object according with VK Docs.
     */
    public VKApiCommunity parse(JSONObject from) {
        super.parse(from);
        name = from.optString("name");
        screen_name = from.optString("screen_name", String.format("club%d", Math.abs(id)));
        is_closed = from.optInt("is_closed");
        is_admin = ParseUtils.parseBoolean(from, "is_admin");
        admin_level = from.optInt("admin_level");
        is_member = ParseUtils.parseBoolean(from, "is_member");

        photo_50 = from.optString("photo_50", PHOTO_50);
        if(!TextUtils.isEmpty(photo_50)) {
            photo.add(VKApiPhotoSize.create(photo_50, 50));
        }
        photo_100 = from.optString("photo_100", PHOTO_100);
        if(!TextUtils.isEmpty(photo_100)) {
            photo.add(VKApiPhotoSize.create(photo_100, 100));
        }
        photo_200 = from.optString("photo_200", null);
        if(!TextUtils.isEmpty(photo_200)) {
            photo.add(VKApiPhotoSize.create(photo_200, 200));
        }
        photo.sort();

        String type = from.optString("type", "group");
        if(TYPE_GROUP.equals(type)) {
            this.type = Type.GROUP;
        } else if(TYPE_PAGE.equals(type)) {
            this.type = Type.PAGE;
        } else if(TYPE_EVENT.equals(type)) {
            this.type = Type.EVENT;
        }
        return this;
    }

    /**
     * Creates a community object from Parcel
     */
    public VKApiCommunity(Parcel in) {
        super(in);
        this.name = in.readString();
        this.screen_name = in.readString();
        this.is_closed = in.readInt();
        this.is_admin = in.readByte() != 0;
        this.admin_level = in.readInt();
        this.is_member = in.readByte() != 0;
        this.type = in.readInt();
        this.photo_50 = in.readString();
        this.photo_100 = in.readString();
        this.photo_200 = in.readString();
        this.photo = in.readParcelable(VKPhotoSizes.class.getClassLoader());
    }

    /**
     * Creates empty Community instance.
     */
    public VKApiCommunity() {

    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.name);
        dest.writeString(this.screen_name);
        dest.writeInt(this.is_closed);
        dest.writeByte(is_admin ? (byte) 1 : (byte) 0);
        dest.writeInt(this.admin_level);
        dest.writeByte(is_member ? (byte) 1 : (byte) 0);
        dest.writeInt(this.type);
        dest.writeString(this.photo_50);
        dest.writeString(this.photo_100);
        dest.writeString(this.photo_200);
        dest.writeParcelable(this.photo, flags);
    }

    public static Creator<VKApiCommunity> CREATOR = new Creator<VKApiCommunity>() {
        public VKApiCommunity createFromParcel(Parcel source) {
            return new VKApiCommunity(source);
        }

        public VKApiCommunity[] newArray(int size) {
            return new VKApiCommunity[size];
        }
    };

    /**
     * Access level to manage community.
     */
    public static class AdminLevel {
        private AdminLevel() {}
        public final static int MODERATOR = 1;
        public final static int EDITOR = 2;
        public final static int ADMIN = 3;
    }

    /**
     * Privacy status of the group.
     */
    public static class Status {
        private Status() {}
        public final static int OPEN = 0;
        public final static int CLOSED = 1;
        public final static int PRIVATE = 2;
    }

    /**
     * Types of communities.
     */
    public static class Type {
        private Type() {}
        public final static int GROUP = 0;
        public final static int PAGE = 1;
        public final static int EVENT = 2;
    }
}
