/**
 * AppInfo.java
 * vk-android-sdk
 *
 * Created by Babichev Vitaly on 19.01.14.
 * Copyright (c) 2014 VK. All rights reserved.
 */
package com.vk.sdk.api.model;

import android.os.Parcel;
import android.text.TextUtils;

import org.json.JSONObject;

import static com.vk.sdk.api.model.VKAttachments.TYPE_APP;
import static com.vk.sdk.api.model.VKAttachments.VKApiAttachment;

/**
 * Describes information about application in the post.
 */
@SuppressWarnings("unused")
public class VKApiApplicationContent extends VKApiAttachment implements android.os.Parcelable {

    /**
     * ID of the application that posted on the wall;
     */
    public int id;

    /**
     * Application name
     */
    public String name;

    /**
     * Image URL for preview with maximum width in 130px
     */
    public String photo_130;

    /**
     * Image URL for preview with maximum width in 130px
     */
    public String photo_604;

    /**
     * Image URL for preview;
     */
    public VKPhotoSizes photo = new VKPhotoSizes();

    /**
     * Fills an ApplicationContent instance from JSONObject.
     */
    public VKApiApplicationContent parse(JSONObject source) {
        id = source.optInt("id");
        name = source.optString("name");
        photo_130 = source.optString("photo_130");
        if(!TextUtils.isEmpty(photo_130)) {
            photo.add(VKApiPhotoSize.create(photo_130, 130));
        }
        photo_604 = source.optString("photo_604");
        if(!TextUtils.isEmpty(photo_604)) {
            photo.add(VKApiPhotoSize.create(photo_604, 604));
        }
        return this;
    }

    /**
     * Creates an ApplicationContent instance from Parcel.
     */
    public VKApiApplicationContent(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.photo_130 = in.readString();
        this.photo_604 = in.readString();
        this.photo = in.readParcelable(VKPhotoSizes.class.getClassLoader());
    }

    /**
     * Creates empty ApplicationContent instance.
     */
    public VKApiApplicationContent() {

    }

    @Override
    public CharSequence toAttachmentString() {
        throw new UnsupportedOperationException("Attaching app info is not supported by VK.com API");
    }

    @Override
    public String getType() {
        return TYPE_APP;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.photo_130);
        dest.writeString(this.photo_604);
        dest.writeParcelable(this.photo, flags);
    }

    public static Creator<VKApiApplicationContent> CREATOR = new Creator<VKApiApplicationContent>() {
        public VKApiApplicationContent createFromParcel(Parcel source) {
            return new VKApiApplicationContent(source);
        }

        public VKApiApplicationContent[] newArray(int size) {
            return new VKApiApplicationContent[size];
        }
    };

    @Override
    public int getId() {
        return id;
    }
}
