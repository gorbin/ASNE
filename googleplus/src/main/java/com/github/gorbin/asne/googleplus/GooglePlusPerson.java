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
package com.github.gorbin.asne.googleplus;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.gorbin.asne.core.persons.SocialPerson;

/**
 * Class for detailed Google plus social person
 *
 * @author Evgeny Gorbin (gorbin.e.o@gmail.com)
 */
public class GooglePlusPerson extends SocialPerson implements Parcelable {

    public static final Creator<GooglePlusPerson> CREATOR
            = new Creator<GooglePlusPerson>() {
        public GooglePlusPerson createFromParcel(Parcel in) {
            return new GooglePlusPerson(in);
        }

        public GooglePlusPerson[] newArray(int size) {
            return new GooglePlusPerson[size];
        }
    };

    /*** About me string in google plus user profile*/
	public String aboutMe;
    /*** The person's date of birth, represented as YYYY-MM-DD.*/
	public String birthday;
    /*** The "bragging rights" line of this person.*/
	public String braggingRights;
    /*** The cover photo content.*/
	public String coverURL;
    /*** If a Google+ Page and for followers who are visible, the number of people who have added this page to a circle.*/
	public int friendsCount;
    /*** The current location for this person.*/
	public String currentLocation;
    /*** The person's gender. Possible values include, but are not limited to, the following values: - "male" - Male gender. - "female" - Female gender. - "other" - Other.*/
	public int gender;
    /*** The user's preferred language for rendering.*/
	public String lang;
    /*** The nickname of this person.*/
	public String nickname;
    /*** Type of person within Google+. Possible values include, but are not limited to, the following values: - "person" - represents an actual person. - "page" - represents a page.*/
	public int objectType;
    /*** Current organization with which this person is associated.*/
	public String company;
    /*** The person's job title or role within the organization.*/
	public String position;
    /*** Last place where this person has lived.*/
	public String placeLivedValue;
    /*** The person's relationship status. Possible values include, but are not limited to, the following values: - "single" - Person is single. - "in_a_relationship" - Person is in a relationship. - "engaged" - Person is engaged. - "married" - Person is married. - "its_complicated" - The relationship is complicated. - "open_relationship" - Person is in an open relationship. - "widowed" - Person is widowed. - "in_domestic_partnership" - Person is in a domestic partnership. - "in_civil_union" - Person is in a civil union.*/
	public int relationshipStatus;
    /*** The brief description (tagline) of this person.*/
	public String tagline;

    public GooglePlusPerson() {

    }

    private GooglePlusPerson(Parcel in) {
        aboutMe = in.readString();
        birthday = in.readString();
        braggingRights = in.readString();
        coverURL = in.readString();
        friendsCount = in.readInt();
        currentLocation = in.readString();
        gender = in.readInt();
        lang = in.readString();
        nickname = in.readString();
        objectType = in.readInt();
        company = in.readString();
        position = in.readString();
        placeLivedValue = in.readString();
        relationshipStatus = in.readInt();
        tagline = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(aboutMe);
        dest.writeString(birthday);
        dest.writeString(braggingRights);
        dest.writeString(coverURL);
        dest.writeInt(friendsCount);
        dest.writeString(currentLocation);
        dest.writeInt(gender);
        dest.writeString(lang);
        dest.writeString(nickname);
        dest.writeInt(objectType);
        dest.writeString(company);
        dest.writeString(position);
        dest.writeString(placeLivedValue);
        dest.writeInt(relationshipStatus);
        dest.writeString(tagline);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GooglePlusPerson)) return false;
        if (!super.equals(o)) return false;

        GooglePlusPerson that = (GooglePlusPerson) o;

        if (friendsCount != that.friendsCount) return false;
        if (gender != that.gender) return false;
        if (objectType != that.objectType) return false;
        if (relationshipStatus != that.relationshipStatus) return false;
        if (aboutMe != null ? !aboutMe.equals(that.aboutMe) : that.aboutMe != null) return false;
        if (birthday != null ? !birthday.equals(that.birthday) : that.birthday != null)
            return false;
        if (braggingRights != null ? !braggingRights.equals(that.braggingRights) : that.braggingRights != null)
            return false;
        if (company != null ? !company.equals(that.company) : that.company != null) return false;
        if (coverURL != null ? !coverURL.equals(that.coverURL) : that.coverURL != null)
            return false;
        if (currentLocation != null ? !currentLocation.equals(that.currentLocation) : that.currentLocation != null)
            return false;
        if (lang != null ? !lang.equals(that.lang) : that.lang != null) return false;
        if (nickname != null ? !nickname.equals(that.nickname) : that.nickname != null)
            return false;
        if (placeLivedValue != null ? !placeLivedValue.equals(that.placeLivedValue) : that.placeLivedValue != null)
            return false;
        if (position != null ? !position.equals(that.position) : that.position != null)
            return false;
        if (tagline != null ? !tagline.equals(that.tagline) : that.tagline != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (aboutMe != null ? aboutMe.hashCode() : 0);
        result = 31 * result + (birthday != null ? birthday.hashCode() : 0);
        result = 31 * result + (braggingRights != null ? braggingRights.hashCode() : 0);
        result = 31 * result + (coverURL != null ? coverURL.hashCode() : 0);
        result = 31 * result + friendsCount;
        result = 31 * result + (currentLocation != null ? currentLocation.hashCode() : 0);
        result = 31 * result + gender;
        result = 31 * result + (lang != null ? lang.hashCode() : 0);
        result = 31 * result + (nickname != null ? nickname.hashCode() : 0);
        result = 31 * result + objectType;
        result = 31 * result + (company != null ? company.hashCode() : 0);
        result = 31 * result + (position != null ? position.hashCode() : 0);
        result = 31 * result + (placeLivedValue != null ? placeLivedValue.hashCode() : 0);
        result = 31 * result + relationshipStatus;
        result = 31 * result + (tagline != null ? tagline.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "GooglePlusPerson{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", avatarURL='" + avatarURL + '\'' +
                ", aboutMe='" + aboutMe + '\'' +
                ", profileURL='" + profileURL + '\'' +
                ", email='" + email + '\'' +
                ", birthday='" + birthday + '\'' +
                ", braggingRights='" + braggingRights + '\'' +
                ", coverURL='" + coverURL + '\'' +
                ", friendsCount=" + friendsCount +
                ", currentLocation='" + currentLocation + '\'' +
                ", gender=" + gender +
                ", lang='" + lang + '\'' +
                ", nickname='" + nickname + '\'' +
                ", objectType=" + objectType +
                ", company='" + company + '\'' +
                ", position='" + position + '\'' +
                ", placeLivedValue='" + placeLivedValue + '\'' +
                ", relationshipStatus=" + relationshipStatus +
                ", tagline='" + tagline + '\'' +
                '}';
    }
}
