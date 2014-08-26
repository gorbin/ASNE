package com.github.gorbin.asne.googleplus;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.gorbin.asne.core.persons.SocialPerson;

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

	public String aboutMe;
	public String birthday;
	public String braggingRights;
	public String coverURL;
	public int friendsCount;
	public String currentLocation;
	public int gender;
	public String lang;
	public String nickname;
	public int objectType;
	public String company;
	public String position;
	public String placeLivedValue;
	public int relationshipStatus;
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
