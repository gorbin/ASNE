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

import org.json.JSONObject;

/**
 * A country object describes a country.
 */
@SuppressWarnings("unused")
public class VKApiCountry extends VKApiModel implements Parcelable, Identifiable {

    /**
     * Country ID.
     */
    public int id;

    /**
     * Country name
     */
    public String title;

    /**
     * Fills a Country instance from JSONObject.
     */
    public VKApiCountry parse(JSONObject from) {
        id = from.optInt("id");
        title = from.optString("title");
        return this;
    }

    /**
     * Creates a Country instance from Parcel.
     */
    public VKApiCountry(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
    }

    /**
     * Creates empty Country instance.
     */
    public VKApiCountry() {

    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
    }

    public static Creator<VKApiCountry> CREATOR = new Creator<VKApiCountry>() {
        public VKApiCountry createFromParcel(Parcel source) {
            return new VKApiCountry(source);
        }

        public VKApiCountry[] newArray(int size) {
            return new VKApiCountry[size];
        }
    };

}
