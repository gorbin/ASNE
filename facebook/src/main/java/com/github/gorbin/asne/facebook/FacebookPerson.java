package com.github.gorbin.asne.facebook;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.gorbin.asne.core.persons.SocialPerson;

public class FacebookPerson extends SocialPerson implements Parcelable {

    public static final Creator<FacebookPerson> CREATOR
            = new Creator<FacebookPerson>() {
        public FacebookPerson createFromParcel(Parcel in) {
            return new FacebookPerson(in);
        }

        public FacebookPerson[] newArray(int size) {
            return new FacebookPerson[size];
        }
    };

	public String firstName;
	public String middleName;
	public String lastName;
	public String gender;
	public String birthday;
	public String city;
    public String verified;

    public FacebookPerson() {

    }

    private FacebookPerson(Parcel in) {
		firstName = in.readString();
		middleName = in.readString();
		lastName = in.readString();
		gender = in.readString();
		birthday = in.readString();
		city = in.readString();
        verified = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
		dest.writeString(middleName);
		dest.writeString(lastName);
		dest.writeString(gender);
		dest.writeString(birthday);
		dest.writeString(city);
        dest.writeString(verified);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof FacebookPerson)) return false;

        FacebookPerson that = (FacebookPerson) o;

		if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) return false;
		if (middleName != null ? !middleName.equals(that.middleName) : that.middleName != null) return false;
		if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) return false;
		if (gender != null ? !gender.equals(that.gender) : that.gender != null) return false;
		if (birthday != null ? !birthday.equals(that.birthday) : that.birthday != null) return false;
		if (city != null ? !city.equals(that.city) : that.city != null) return false;
        if (verified != null ? !verified.equals(that.verified) : that.verified != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = firstName != null ? firstName.hashCode() : 0;
		result = 31 * result + (middleName != null ? middleName.hashCode() : 0);
		result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
		result = 31 * result + (gender != null ? gender.hashCode() : 0);
		result = 31 * result + (birthday != null ? birthday.hashCode() : 0);
		result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (verified != null ? verified.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FacebookPerson{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", avatarURL='" + avatarURL + '\'' +
                ", profileURL='" + profileURL + '\'' +
                ", email='" + email + '\'' +
				", firstName='" + firstName + '\'' +
				", middleName='" + middleName + '\'' +
				", lastName='" + lastName + '\'' +
				", gender='" + gender + '\'' +
				", birthday='" + birthday + '\'' +
				", city='" + city + '\'' +
                ", city='" + verified + '\'' +
                '}';
    }
}
