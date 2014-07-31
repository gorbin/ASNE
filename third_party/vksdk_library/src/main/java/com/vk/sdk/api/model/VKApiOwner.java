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
 * Owner.java
 * vk-android-sdk
 *
 * Created by Babichev Vitaly on 18.01.14.
 * Copyright (c) 2014 VK. All rights reserved.
 */
package com.vk.sdk.api.model;

import android.os.Parcel;

import org.json.JSONObject;

/**
 * This class represents owner of some VK object.
 */
@SuppressWarnings("unused")
public class VKApiOwner extends VKApiModel implements Identifiable, android.os.Parcelable {

    /**
     * User or group ID.
     * If ID is positive, owner is user.
     * If ID is negative, owner is community.
     */
    public int id;

    /**
     * Creates an owner with empty ID.
     */
    public VKApiOwner() {

    }

    /**
     * Fills an owner from JSONObject
     */
    public VKApiOwner parse(JSONObject from) {
        fields = from;
        id = from.optInt("id");
        return this;
    }

    /**
     * Creates according with given ID.
     */
    public VKApiOwner(int id) {
        this.id = id;
    }

    /**
     * Creates an owner from Parcel.
     */
    public VKApiOwner(Parcel in) {
        this.id = in.readInt();
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
    }

    public static Creator<VKApiOwner> CREATOR = new Creator<VKApiOwner>() {
        public VKApiOwner createFromParcel(Parcel source) {
            return new VKApiOwner(source);
        }

        public VKApiOwner[] newArray(int size) {
            return new VKApiOwner[size];
        }
    };
}
