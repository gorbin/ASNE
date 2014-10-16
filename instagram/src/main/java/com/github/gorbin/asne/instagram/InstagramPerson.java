/*******************************************************************************
 * Copyright (c) 2014 Evgeny Gorbin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package com.github.gorbin.asne.instagram;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.gorbin.asne.core.persons.SocialPerson;

/**
 * Class for detailed Instagram social person
 *
 * @author Evgeny Gorbin (gorbin.e.o@gmail.com)
 */
public class InstagramPerson extends SocialPerson implements Parcelable {

    public static final Parcelable.Creator<InstagramPerson> CREATOR
            = new Parcelable.Creator<InstagramPerson>() {
        public InstagramPerson createFromParcel(Parcel in) {
            return new InstagramPerson(in);
        }

        public InstagramPerson[] newArray(int size) {
            return new InstagramPerson[size];
        }
    };
    /*** Bio of social person*/
    public String bio;
    /*** Website of social person from user contacts*/
    public String website;
    /*** Full name of social person*/
    public String fullName;
    /*** Count of social person's media*/
    public int media;
    /*** Count of social person's followers*/
    public int followedBy;
    /*** Count of social person's follows*/
    public int follows;

    public InstagramPerson() {

    }

    protected InstagramPerson(Parcel in) {
        bio = in.readString();
        website = in.readString();
        fullName = in.readString();
        media = in.readInt();
        followedBy = in.readInt();
        follows = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bio);
        dest.writeString(website);
        dest.writeString(fullName);
        dest.writeInt(media);
        dest.writeInt(followedBy);
        dest.writeInt(follows);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InstagramPerson)) return false;
        if (!super.equals(o)) return false;

        InstagramPerson that = (InstagramPerson) o;

        if (followedBy != that.followedBy) return false;
        if (follows != that.follows) return false;
        if (media != that.media) return false;
        if (bio != null ? !bio.equals(that.bio) : that.bio != null) return false;
        if (fullName != null ? !fullName.equals(that.fullName) : that.fullName != null)
            return false;
        if (website != null ? !website.equals(that.website) : that.website != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (bio != null ? bio.hashCode() : 0);
        result = 31 * result + (website != null ? website.hashCode() : 0);
        result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
        result = 31 * result + media;
        result = 31 * result + followedBy;
        result = 31 * result + follows;
        return result;
    }

    @Override
    public String toString() {
        return "InstagramPerson{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", avatarURL='" + avatarURL + '\'' +
                ", profileURL='" + profileURL + '\'' +
                ", email='" + email + '\'' +
                ", bio='" + bio + '\'' +
                ", website='" + website + '\'' +
                ", fullName='" + fullName + '\'' +
                ", media=" + media +
                ", followedBy=" + followedBy +
                ", follows=" + follows +
                '}';
    }
}
