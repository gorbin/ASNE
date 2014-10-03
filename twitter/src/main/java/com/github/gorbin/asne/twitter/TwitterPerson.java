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
package com.github.gorbin.asne.twitter;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.gorbin.asne.core.persons.SocialPerson;

/**
 * Class for detailed Twitter social person
 *
 * @author Evgeny Gorbin (gorbin.e.o@gmail.com)
 */
public class TwitterPerson extends SocialPerson implements Parcelable {

    public static final Creator<TwitterPerson> CREATOR
            = new Creator<TwitterPerson>() {
        public TwitterPerson createFromParcel(Parcel in) {
            return new TwitterPerson(in);
        }

        public TwitterPerson[] newArray(int size) {
            return new TwitterPerson[size];
        }
    };
    /*** Date when profile was created*/
	public Long createdDate;
    /*** Description of twitter user*/
	public String description;
    /*** Count of favorites for twitter user*/
	public int favoritesCount;
    /*** Count of followers for twitter user*/
	public int followersCount;
    /*** Count of friends for twitter user*/
	public int friendsCount;
    /*** Preferred language for twitter user*/
	public String lang;
    /*** Location of twitter user*/
	public String location;
    /*** Screen name of twitter user*/
	public String screenName;
    /*** Last status of twitter user*/
	public String status;
    /*** Preferred timezone for twitter user*/
	public String timezone;
    /*** Check if twitter user is translator*/
	public Boolean isTranslator;
    /*** Check if twitter user is verified*/
	public Boolean isVerified;

    public TwitterPerson() {

    }

    private TwitterPerson(Parcel in) {
		createdDate = in.readLong();
		description = in.readString();
		favoritesCount = in.readInt();
		followersCount = in.readInt();
		friendsCount = in.readInt();
		lang = in.readString();
		location = in.readString();
		screenName = in.readString();
		status = in.readString();
		timezone = in.readString();
		isTranslator = in.readByte() != 0;
		isVerified = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(createdDate);
        dest.writeString(description);
		dest.writeInt(favoritesCount);
		dest.writeInt(followersCount);
        dest.writeInt(friendsCount);
		dest.writeString(lang);
		dest.writeString(location);
		dest.writeString(screenName);
		dest.writeString(status);
		dest.writeString(timezone);
		dest.writeByte((byte) (isTranslator ? 1 : 0));
		dest.writeByte((byte) (isVerified ? 1 : 0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof TwitterPerson)) return false;

        TwitterPerson that = (TwitterPerson) o;

        if (createdDate != 0 ? !createdDate.equals(that.createdDate) : that.createdDate != null)	return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (favoritesCount != 0 ? !(favoritesCount == that.favoritesCount) : that.favoritesCount != 0)	return false;
        if (followersCount != 0 ? !(followersCount == that.followersCount) : that.followersCount != 0)	return false;
        if (friendsCount != 0 ? !(friendsCount == that.friendsCount) : that.friendsCount != 0)	return false;
        if (lang != null ? !lang.equals(that.lang) : that.lang != null) return false;
        if (location != null ? !location.equals(that.location) : that.location != null) return false;
        if (screenName != null ? !screenName.equals(that.screenName) : that.screenName != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (timezone != null ? !timezone.equals(that.timezone) : that.timezone != null) return false;
        if (isTranslator != null ? !isTranslator.equals(that.isTranslator) : that.isTranslator != null) return false;
        if (isVerified != null ? !isVerified.equals(that.isVerified) : that.isVerified != null) return false;

        return true;
    }
	
    @Override
    public int hashCode() {
        int result = createdDate != null ? createdDate.hashCode() : 0;
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (((Integer)favoritesCount) != null ? ((Integer)favoritesCount).hashCode() : 0);
        result = 31 * result + (((Integer)followersCount) != null ? ((Integer)followersCount).hashCode() : 0);
        result = 31 * result + (((Integer)friendsCount) != null ? ((Integer)friendsCount).hashCode() : 0);
        result = 31 * result + (lang != null ? lang.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (screenName != null ? screenName.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (timezone != null ? timezone.hashCode() : 0);
        result = 31 * result + (isTranslator != null ? isTranslator.hashCode() : 0);
        result = 31 * result + (isVerified != null ? isVerified.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TwitterPerson{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", avatarURL='" + avatarURL + '\'' +
                ", profileURL='" + profileURL + '\'' +
                ", email='" + email + '\'' +
				", createdDate='" + createdDate + '\'' +
				", description='" + description + '\'' +
				", favoritesCount='" + favoritesCount + '\'' +
				", followersCount='" + followersCount + '\'' +
				", friendsCount='" + friendsCount + '\'' +
				", lang='" + lang + '\'' +
				", location='" + location + '\'' +
				", screenName='" + screenName + '\'' +
				", status='" + status + '\'' +
				", timezone='" + timezone + '\'' +
				", isTranslator='" + isTranslator + '\'' +
				", isVerified='" + isVerified + '\'' +
                '}';
    }
}
