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
 * Note.java
 * vk-android-sdk
 *
 * Created by Babichev Vitaly on 19.01.14.
 * Copyright (c) 2014 VK. All rights reserved.
 */
package com.vk.sdk.api.model;

import android.os.Parcel;

import org.json.JSONObject;

import static com.vk.sdk.api.model.VKAttachments.TYPE_NOTE;

/**
 * A note object describes a note.
 */
@SuppressWarnings("unused")
public class VKApiNote extends VKAttachments.VKApiAttachment implements Identifiable, android.os.Parcelable {

    /**
     * Note ID, positive number
     */
    public int id;

    /**
     * Note owner ID.
     */
    public int user_id;

    /**
     * Note title.
     */
    public String title;

    /**
     * Note text.
     */
    public String text;

    /**
     * Date (in Unix time) when the note was created.
     */
    public long date;

    /**
     * Number of comments.
     */
    public int comments;

    /**
     * Number of read comments (only if owner_id is the current user).
     */
    public int read_comments;

    /**
     * Fills a Note instance from JSONObject.
     */
    public VKApiNote parse(JSONObject source) {
        id = source.optInt("id");
        user_id = source.optInt("user_id");
        title = source.optString("title");
        text = source.optString("text");
        date = source.optLong("date");
        comments = source.optInt("comments");
        read_comments = source.optInt("read_comments");
        return this;
    }

    /**
     * Creates a Note instance from Parcel.
     */
    public VKApiNote(Parcel in) {
        this.id = in.readInt();
        this.user_id = in.readInt();
        this.title = in.readString();
        this.text = in.readString();
        this.date = in.readLong();
        this.comments = in.readInt();
        this.read_comments = in.readInt();
    }

    /**
     * Creates empty Note instance.
     */
    public VKApiNote() {

    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public CharSequence toAttachmentString() {
        return new StringBuilder(TYPE_NOTE).append(user_id).append('_').append(id);
    }

    @Override
    public String getType() {
        return TYPE_NOTE;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.user_id);
        dest.writeString(this.title);
        dest.writeString(this.text);
        dest.writeLong(this.date);
        dest.writeInt(this.comments);
        dest.writeInt(this.read_comments);
    }

    public static Creator<VKApiNote> CREATOR = new Creator<VKApiNote>() {
        public VKApiNote createFromParcel(Parcel source) {
            return new VKApiNote(source);
        }

        public VKApiNote[] newArray(int size) {
            return new VKApiNote[size];
        }
    };

}
