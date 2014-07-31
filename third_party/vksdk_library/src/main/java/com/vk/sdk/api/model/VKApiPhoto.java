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
import android.os.Parcelable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.vk.sdk.api.model.ParseUtils.parseBoolean;
import static com.vk.sdk.api.model.ParseUtils.parseInt;
import static com.vk.sdk.api.model.VKAttachments.TYPE_PHOTO;

/**
 * Describes a photo object from VK.
 */
public class VKApiPhoto extends VKAttachments.VKApiAttachment implements Parcelable, Identifiable {

    /**
     * Photo ID, positive number
     */
    public int id;

    /**
     * Photo album ID.
     */
    public int album_id;

    /**
     * ID of the user or community that owns the photo.
     */
    public int owner_id;

    /**
     * Width (in pixels) of the original photo.
     */
    public int width;

    /**
     * Height (in pixels) of the original photo.
     */
    public int height;

    /**
     * Text describing the photo.
     */
    public String text;

    /**
     * Date (in Unix time) the photo was added.
     */
    public long date;

    /**
     * URL of image with maximum size 75x75px.
     */
    public String photo_75;

    /**
     * URL of image with maximum size 130x130px.
     */
    public String photo_130;

    /**
     * URL of image with maximum size 604x604px.
     */
    public String photo_604;

    /**
     * URL of image with maximum size 807x807px.
     */
    public String photo_807;

    /**
     * URL of image with maximum size 1280x1024px.
     */
    public String photo_1280;

    /**
     * URL of image with maximum size 2560x2048px.
     */
    public String photo_2560;

    /**
     * All photo thumbs in photo sizes.
     * It has data even if server returned them without {@code PhotoSizes} format.
     */
    public VKPhotoSizes src = new VKPhotoSizes();

    /**
     * Information whether the current user liked the photo.
     */
    public boolean user_likes;

    /**
     * Whether the current user can comment on the photo
     */
    public boolean can_comment;

    /**
     * Number of likes on the photo.
     */
    public int likes;

    /**
     * Number of comments on the photo.
     */
    public int comments;

    /**
     * Number of tags on the photo.
     */
    public int tags;

    /**
     * An access key using for get information about hidden objects.
     */
    public String access_key;

    /**
     * Fills a Photo instance from JSONObject.
     */
    public VKApiPhoto parse(JSONObject from) {
        album_id = from.optInt("album_id");
        date = from.optLong("date");
        height = from.optInt("height");
        width = from.optInt("width");
        owner_id = from.optInt("owner_id");
        id = from.optInt("id");
        text = from.optString("text");
        access_key = from.optString("access_key");

        photo_75 = from.optString("photo_75");
        photo_130 = from.optString("photo_130");
        photo_604 = from.optString("photo_604");
        photo_807 = from.optString("photo_807");
        photo_1280 = from.optString("photo_1280");
        photo_2560 = from.optString("photo_2560");

        JSONObject likes = from.optJSONObject("likes");
        this.likes = ParseUtils.parseInt(likes, "count");
        this.user_likes = ParseUtils.parseBoolean(likes, "user_likes");
        comments = parseInt(from.optJSONObject("comments"), "count");
        tags = parseInt(from.optJSONObject("tags"), "count");
        can_comment = parseBoolean(from, "can_comment");

        src.setOriginalDimension(width, height);
        JSONArray photo_sizes = from.optJSONArray("sizes");
        if(photo_sizes != null) {
            src.fill(photo_sizes);
        } else {
            if(!TextUtils.isEmpty(photo_75)) {
                src.add(VKApiPhotoSize.create(photo_75, VKApiPhotoSize.S, width, height));
            }
            if(!TextUtils.isEmpty(photo_130)) {
                src.add(VKApiPhotoSize.create(photo_130, VKApiPhotoSize.M, width, height));
            }
            if(!TextUtils.isEmpty(photo_604)) {
                src.add(VKApiPhotoSize.create(photo_604, VKApiPhotoSize.X, width, height));
            }
            if(!TextUtils.isEmpty(photo_807)) {
                src.add(VKApiPhotoSize.create(photo_807, VKApiPhotoSize.Y, width, height));
            }
            if(!TextUtils.isEmpty(photo_1280)) {
                src.add(VKApiPhotoSize.create(photo_1280, VKApiPhotoSize.Z, width, height));
            }
            if(!TextUtils.isEmpty(photo_2560)) {
                src.add(VKApiPhotoSize.create(photo_2560, VKApiPhotoSize.W, width, height));
            }
            src.sort();
        }
        return this;
    }

    /**
     * Creates a Photo instance from Parcel.
     */
    public VKApiPhoto(Parcel in) {
        this.id = in.readInt();
        this.album_id = in.readInt();
        this.owner_id = in.readInt();
        this.width = in.readInt();
        this.height = in.readInt();
        this.text = in.readString();
        this.date = in.readLong();
        this.src = in.readParcelable(VKPhotoSizes.class.getClassLoader());
        this.photo_75 = in.readString();
        this.photo_130 = in.readString();
        this.photo_604 = in.readString();
        this.photo_807 = in.readString();
        this.photo_1280 = in.readString();
        this.photo_2560 = in.readString();
        this.user_likes = in.readByte() != 0;
        this.can_comment = in.readByte() != 0;
        this.likes = in.readInt();
        this.comments = in.readInt();
        this.tags = in.readInt();
        this.access_key = in.readString();
    }

    /**
     * Creates empty Photo instance.
     */
    public VKApiPhoto() {

    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public CharSequence toAttachmentString() {
        StringBuilder result = new StringBuilder(TYPE_PHOTO).append(owner_id).append('_').append(id);
        if(!TextUtils.isEmpty(access_key)) {
            result.append('_');
            result.append(access_key);
        }
        return result;
    }

    @Override
    public String getType() {
        return TYPE_PHOTO;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.album_id);
        dest.writeInt(this.owner_id);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeString(this.text);
        dest.writeLong(this.date);
        dest.writeParcelable(this.src, flags);
        dest.writeString(this.photo_75);
        dest.writeString(this.photo_130);
        dest.writeString(this.photo_604);
        dest.writeString(this.photo_807);
        dest.writeString(this.photo_1280);
        dest.writeString(this.photo_2560);
        dest.writeByte(user_likes ? (byte) 1 : (byte) 0);
        dest.writeByte(can_comment ? (byte) 1 : (byte) 0);
        dest.writeInt(this.likes);
        dest.writeInt(this.comments);
        dest.writeInt(this.tags);
        dest.writeString(this.access_key);
    }

    public static Creator<VKApiPhoto> CREATOR = new Creator<VKApiPhoto>() {
        public VKApiPhoto createFromParcel(Parcel source) {
            return new VKApiPhoto(source);
        }

        public VKApiPhoto[] newArray(int size) {
            return new VKApiPhoto[size];
        }
    };

}
