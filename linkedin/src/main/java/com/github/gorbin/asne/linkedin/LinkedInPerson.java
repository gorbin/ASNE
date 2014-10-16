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
package com.github.gorbin.asne.linkedin;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.gorbin.asne.core.persons.SocialPerson;

/**
 * Class for detailed LinkedIn social person
 *
 * @author Evgeny Gorbin (gorbin.e.o@gmail.com)
 */
public class LinkedInPerson extends SocialPerson implements Parcelable {

    public static final Creator<LinkedInPerson> CREATOR
            = new Creator<LinkedInPerson>() {
        public LinkedInPerson createFromParcel(Parcel in) {
            return new LinkedInPerson(in);
        }

        public LinkedInPerson[] newArray(int size) {
            return new LinkedInPerson[size];
        }
    };

    /*** Current organization with which this person is associated.*/
    public String company;
    /*** The person's job title or role within the organization.*/
    public String position;
    /*** First name of social person*/
    public String firstName;
    /*** Last name of social person*/
	public String lastName;
    /*** Headline of social person*/
	public String headLine;
    /*** Country code of social person*/
	public String countryCode;
    /*** Location description of social person*/
	public String locationDescription;
    /*** The person's job industry within the organization.*/
	public String industry;
    /*** Summary of social person*/
	public String summary;
    /*** Birthday of social person in the format DD/MM/YYYY or DD/MM*/
	public String birthday;
    /*** Main address of social person from contacts*/
	public String mainAddress;
    /*** Current status of social person*/
	public String currentStatus;
    /*** Interests of social person*/
	public String interests;
    /*** Specialties of social person*/
	public String specialties;
    /*** Phone of social person from contacts*/
    public String phone;
	
    public LinkedInPerson() {

    }
    protected LinkedInPerson(Parcel in) {
        company = in.readString();
        position = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        headLine = in.readString();
        countryCode = in.readString();
        locationDescription = in.readString();
        industry = in.readString();
        summary = in.readString();
        birthday = in.readString();
        mainAddress = in.readString();
        currentStatus = in.readString();
        interests = in.readString();
        specialties = in.readString();
        phone = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(company);
        dest.writeString(position);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(headLine);
        dest.writeString(countryCode);
        dest.writeString(locationDescription);
        dest.writeString(industry);
        dest.writeString(summary);
        dest.writeString(birthday);
        dest.writeString(mainAddress);
        dest.writeString(currentStatus);
        dest.writeString(interests);
        dest.writeString(specialties);
        dest.writeString(phone);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LinkedInPerson)) return false;
        if (!super.equals(o)) return false;

        LinkedInPerson that = (LinkedInPerson) o;

        if (birthday != null ? !birthday.equals(that.birthday) : that.birthday != null)
            return false;
        if (company != null ? !company.equals(that.company) : that.company != null) return false;
        if (currentStatus != null ? !currentStatus.equals(that.currentStatus) : that.currentStatus != null)
            return false;
        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null)
            return false;
        if (headLine != null ? !headLine.equals(that.headLine) : that.headLine != null)
            return false;
        if (industry != null ? !industry.equals(that.industry) : that.industry != null)
            return false;
        if (interests != null ? !interests.equals(that.interests) : that.interests != null)
            return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null)
            return false;
        if (locationDescription != null ? !locationDescription.equals(that.locationDescription) : that.locationDescription != null)
            return false;
        if (mainAddress != null ? !mainAddress.equals(that.mainAddress) : that.mainAddress != null)
            return false;
        if (position != null ? !position.equals(that.position) : that.position != null)
            return false;
        if (countryCode != null ? !countryCode.equals(that.countryCode) : that.countryCode != null)
            return false;
        if (specialties != null ? !specialties.equals(that.specialties) : that.specialties != null)
            return false;
        if (summary != null ? !summary.equals(that.summary) : that.summary != null) return false;
        if (phone != null ? !phone.equals(that.phone) : that.phone != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (company != null ? company.hashCode() : 0);
        result = 31 * result + (position != null ? position.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (headLine != null ? headLine.hashCode() : 0);
        result = 31 * result + (countryCode != null ? countryCode.hashCode() : 0);
        result = 31 * result + (locationDescription != null ? locationDescription.hashCode() : 0);
        result = 31 * result + (industry != null ? industry.hashCode() : 0);
        result = 31 * result + (summary != null ? summary.hashCode() : 0);
        result = 31 * result + (birthday != null ? birthday.hashCode() : 0);
        result = 31 * result + (mainAddress != null ? mainAddress.hashCode() : 0);
        result = 31 * result + (currentStatus != null ? currentStatus.hashCode() : 0);
        result = 31 * result + (interests != null ? interests.hashCode() : 0);
        result = 31 * result + (specialties != null ? specialties.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LinkedInPerson{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", avatarURL='" + avatarURL + '\'' +
                ", profileURL='" + profileURL + '\'' +
                ", email='" + email + '\'' +
                ", company='" + company + '\'' +
                ", position='" + position + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", headLine='" + headLine + '\'' +
                ", postalCode='" + countryCode + '\'' +
                ", locationDescription='" + locationDescription + '\'' +
                ", industry='" + industry + '\'' +
                ", summary='" + summary + '\'' +
                ", birthday='" + birthday + '\'' +
                ", mainAddress='" + mainAddress + '\'' +
                ", currentStatus='" + currentStatus + '\'' +
                ", interests='" + interests + '\'' +
                ", specialties='" + specialties + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
