/*******************************************************************************
 * Copyright (c) 2016 Evgeny Gorbin
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
package com.github.gorbin.asne.vk;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.gorbin.asne.core.persons.SocialPerson;

/**
 * Class for detailed VK social person
 *
 * @author Evgeny Gorbin (gorbin.e.o@gmail.com)
 */
public class VKPerson extends SocialPerson implements Parcelable {

    public static final Creator<VKPerson> CREATOR
            = new Creator<VKPerson>() {
        public VKPerson createFromParcel(Parcel in) {
            return new VKPerson(in);
        }

        public VKPerson[] newArray(int size) {
            return new VKPerson[size];
        }
    };

    /*** First name of social person*/
    public String firstName;
    /*** Last name of social person*/
    public String lastName;
    /*** Sex of social person.  1 - female, 2 - male, 0 - not presented. */
    public int sex;
    /*** Birthday of social person like dd.MM.yyyy or dd.mm if year not permitted*/
    public String birthday;
    /*** City of social person from user contacts*/
    public String city;
    /*** Country of social person from user contacts*/
    public String country;
    /*** Max sized image url of social person avatar*/
    public String photoMaxOrig;
    /*** Is social person online now. 1 - true, 0 - false*/
    public boolean online;
    /*** Username of social person*/
    public String username;
    /*** If social person has mobile. 1 - true, 0 - false*/
    public boolean hasMobile;
    /*** Mobile phone of social person from contacts*/
    public String mobilePhone;
    /*** Home phone of social person from contacts*/
    public String homePhone;
    /*** University name of social person*/
    public String universityName;
    /*** Faculty name of social person*/
    public String facultyName;
    /*** Year when social person was\will graduate*/
    public String graduationYear;
    /*** Current social person status*/
    public String status;
    /*** Is it permitted to post on social person wall*/
    public boolean canPost;
    /*** Is it permitted to see all posts from social person wall*/
    public boolean canSeeAllPosts;
    /*** Is it permitted to write private messages to social person*/
    public boolean canWritePrivateMessage;


    public VKPerson() {
    }

    private VKPerson(Parcel in) {
        lastName = in.readString();
        firstName = in.readString();
        sex = in.readInt();
        birthday = in.readString();
        city = in.readString();
        country = in.readString();
        photoMaxOrig = in.readString();
        online = in.readByte() != 0x00;
        username = in.readString();
        hasMobile = in.readByte() != 0x00;
        mobilePhone = in.readString();
        homePhone = in.readString();
        universityName = in.readString();
        facultyName = in.readString();
        graduationYear = in.readString();
        status = in.readString();
        canPost = in.readByte() != 0x00;
        canSeeAllPosts = in.readByte() != 0x00;
        canWritePrivateMessage = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeInt(sex);
        dest.writeString(birthday);
        dest.writeString(city);
        dest.writeString(country);
        dest.writeString(photoMaxOrig);
        dest.writeByte((byte) (online ? 0x01 : 0x00));
        dest.writeString(username);
        dest.writeByte((byte) (hasMobile ? 0x01 : 0x00));
        dest.writeString(mobilePhone);
        dest.writeString(homePhone);
        dest.writeString(universityName);
        dest.writeString(facultyName);
        dest.writeString(graduationYear);
        dest.writeString(status);
        dest.writeByte((byte) (canPost ? 0x01 : 0x00));
        dest.writeByte((byte) (canSeeAllPosts ? 0x01 : 0x00));
        dest.writeByte((byte) (canWritePrivateMessage ? 0x01 : 0x00));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VKPerson)) return false;
        if (!super.equals(o)) return false;

        VKPerson vkPerson = (VKPerson) o;

        if (canPost != vkPerson.canPost) return false;
        if (canSeeAllPosts != vkPerson.canSeeAllPosts) return false;
        if (canWritePrivateMessage != vkPerson.canWritePrivateMessage) return false;
        if (hasMobile != vkPerson.hasMobile) return false;
        if (online != vkPerson.online) return false;
        if (sex != vkPerson.sex) return false;
        if (firstName != null ? !firstName.equals(vkPerson.firstName) : vkPerson.firstName != null)
            return false;
        if (lastName != null ? !lastName.equals(vkPerson.lastName) : vkPerson.lastName != null)
            return false;
        if (birthday != null ? !birthday.equals(vkPerson.birthday) : vkPerson.birthday != null)
            return false;
        if (city != null ? !city.equals(vkPerson.city) : vkPerson.city != null) return false;
        if (country != null ? !country.equals(vkPerson.country) : vkPerson.country != null)
            return false;
        if (facultyName != null ? !facultyName.equals(vkPerson.facultyName) : vkPerson.facultyName != null)
            return false;
        if (graduationYear != null ? !graduationYear.equals(vkPerson.graduationYear) : vkPerson.graduationYear != null)
            return false;
        if (homePhone != null ? !homePhone.equals(vkPerson.homePhone) : vkPerson.homePhone != null)
            return false;
        if (mobilePhone != null ? !mobilePhone.equals(vkPerson.mobilePhone) : vkPerson.mobilePhone != null)
            return false;
        if (photoMaxOrig != null ? !photoMaxOrig.equals(vkPerson.photoMaxOrig) : vkPerson.photoMaxOrig != null)
            return false;
        if (status != null ? !status.equals(vkPerson.status) : vkPerson.status != null)
            return false;
        if (universityName != null ? !universityName.equals(vkPerson.universityName) : vkPerson.universityName != null)
            return false;
        if (username != null ? !username.equals(vkPerson.username) : vkPerson.username != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + sex;
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (birthday != null ? birthday.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (photoMaxOrig != null ? photoMaxOrig.hashCode() : 0);
        result = 31 * result + (online ? 1 : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (hasMobile ? 1 : 0);
        result = 31 * result + (mobilePhone != null ? mobilePhone.hashCode() : 0);
        result = 31 * result + (homePhone != null ? homePhone.hashCode() : 0);
        result = 31 * result + (universityName != null ? universityName.hashCode() : 0);
        result = 31 * result + (facultyName != null ? facultyName.hashCode() : 0);
        result = 31 * result + (graduationYear != null ? graduationYear.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (canPost ? 1 : 0);
        result = 31 * result + (canSeeAllPosts ? 1 : 0);
        result = 31 * result + (canWritePrivateMessage ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VKPerson{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", avatarURL='" + avatarURL + '\'' +
                ", profileURL='" + profileURL + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", sex='" + sex + '\'' +
                ", birthday='" + birthday + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", photoMaxOrig='" + photoMaxOrig + '\'' +
                ", online=" + online +
                ", gender='" + username + '\'' +
                ", hasMobile=" + hasMobile +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", homePhone='" + homePhone + '\'' +
                ", universityName='" + universityName + '\'' +
                ", facultyName='" + facultyName + '\'' +
                ", graduationYear='" + graduationYear + '\'' +
                ", status='" + status + '\'' +
                ", canPost=" + canPost +
                ", canSeeAllPosts=" + canSeeAllPosts +
                ", canWritePrivateMessage=" + canWritePrivateMessage +
                '}';
    }
}
