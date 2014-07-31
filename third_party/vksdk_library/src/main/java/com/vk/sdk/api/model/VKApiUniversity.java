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

import static android.text.TextUtils.isEmpty;

/**
 * An university object describes an university.
 */
@SuppressWarnings("unused")
public class VKApiUniversity extends VKApiModel implements Parcelable, Identifiable {

    /**
     * University ID, positive number
     */
    public int id;

    /**
     * ID of the country the university is located in, positive number
     */
    public int country_id;

    /**
     * ID of the city the university is located in, positive number
     */
    public int city_id;

    /**
     * University name
     */
    public String name;

    /**
     * Faculty ID
     */
    public String faculty;

    /**
     * Faculty name
     */
    public String faculty_name;

    /**
     * University chair ID;
     */
    public int chair;

    /**
     * Chair name
     */
    public String chair_name;

    /**
     * Graduation year
     */
    public int graduation;

    /**
     * Form of education
     */
    public String education_form;

    /**
     * Status of education
     */
    public String education_status;

    /**
     * Fills a University instance from JSONObject.
     */
    public VKApiUniversity parse(JSONObject from) {
        id = from.optInt("id");
        country_id = from.optInt("country_id");
        city_id = from.optInt("city_id");
        name = from.optString("name");
        faculty = from.optString("faculty");
        faculty_name = from.optString("faculty_name");
        chair = from.optInt("chair");
        chair_name = from.optString("chair_name");
        graduation = from.optInt("graduation");
        education_form = from.optString("education_form");
        education_status = from.optString("education_status");
        return this;
    }

    /**
     * Creates a University instance from Parcel.
     */
    public VKApiUniversity(Parcel in) {
        this.id = in.readInt();
        this.country_id = in.readInt();
        this.city_id = in.readInt();
        this.name = in.readString();
        this.faculty = in.readString();
        this.faculty_name = in.readString();
        this.chair = in.readInt();
        this.chair_name = in.readString();
        this.graduation = in.readInt();
        this.education_form = in.readString();
        this.education_status = in.readString();
    }

    /**
     * Creates empty University instance.
     */
    public VKApiUniversity() {

    }

    private String fullName;

    @Override
    public String toString() {
        if(fullName == null) {
            StringBuilder result = new StringBuilder(name);
            result.append(" \'");
            result.append(String.format("%02d", graduation % 100));
            if(!isEmpty(faculty_name)) {
                result.append(", ");
                result.append(faculty_name);
            }
            if(!isEmpty(chair_name)) {
                result.append(", ");
                result.append(chair_name);
            }
            fullName = result.toString();
        }
        return fullName;
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
        dest.writeInt(this.country_id);
        dest.writeInt(this.city_id);
        dest.writeString(this.name);
        dest.writeString(this.faculty);
        dest.writeString(this.faculty_name);
        dest.writeInt(this.chair);
        dest.writeString(this.chair_name);
        dest.writeInt(this.graduation);
        dest.writeString(this.education_form);
        dest.writeString(this.education_status);
    }

    public static Creator<VKApiUniversity> CREATOR = new Creator<VKApiUniversity>() {
        public VKApiUniversity createFromParcel(Parcel source) {
            return new VKApiUniversity(source);
        }

        public VKApiUniversity[] newArray(int size) {
            return new VKApiUniversity[size];
        }
    };

}
