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
 * Chat.java
 * vk-android-sdk
 *
 * Created by Babichev Vitaly on 19.01.14.
 * Copyright (c) 2014 VK. All rights reserved.
 */
package com.vk.sdk.api.model;

import android.os.Parcel;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Chat object describes a user's chat.
 */
@SuppressWarnings("unused")
public class VKApiChat extends VKApiModel implements Identifiable, android.os.Parcelable {

    /**
     * Chat ID, positive number.
     */
    public int id;

    /**
     * Type of chat.
     */
    public String type;

    /**
     * Chat title.
     */
    public String title;

    /**
     * ID of the chat starter, positive number
     */
    public int admin_id;

    /**
     * List of chat participants' IDs.
     */
    public int[] users;

    /**
     * Fills a Chat instance from JSONObject.
     */
    public VKApiChat parse(JSONObject source) {
        id = source.optInt("id");
        type = source.optString("type");
        title = source.optString("title");
        admin_id = source.optInt("admin_id");
        JSONArray users = source.optJSONArray("users");
        if(users != null) {
            this.users = new int[users.length()];
            for(int i = 0; i < this.users.length; i++) {
                this.users[i] = users.optInt(i);
            }
        }
        return this;
    }

    /**
     * Creates a Chat instance from Parcel.
     */
    public VKApiChat(Parcel in) {
        this.id = in.readInt();
        this.type = in.readString();
        this.title = in.readString();
        this.admin_id = in.readInt();
        this.users = in.createIntArray();
    }

    /**
     * Creates empty Chat instance.
     */
    public VKApiChat() {

    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.type);
        dest.writeString(this.title);
        dest.writeInt(this.admin_id);
        dest.writeIntArray(this.users);
    }

    public static Creator<VKApiChat> CREATOR = new Creator<VKApiChat>() {
        public VKApiChat createFromParcel(Parcel source) {
            return new VKApiChat(source);
        }

        public VKApiChat[] newArray(int size) {
            return new VKApiChat[size];
        }
    };
}
