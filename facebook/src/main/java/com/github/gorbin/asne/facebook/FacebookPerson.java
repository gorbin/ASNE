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
package com.github.gorbin.asne.facebook;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.gorbin.asne.core.persons.SocialPerson;

/**
 * Class for detailed Facebook social person
 *
 * @author Evgeny Gorbin (gorbin.e.o@gmail.com)
 */
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

    /*** First name of social person*/
	public String firstName;
    /*** Middle name of social person*/
	public String middleName;
    /*** Last name of social person*/
	public String lastName;
    /*** Sex of social person*/
	public String gender;
    /*** Birthday of social person in the format MM/DD/YYYY*/
	public String birthday;
    /*** City of social person from user*/
	public String city;
    /*** Check if user is verified*/
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
