package com.github.gorbin.asne.linkedin;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.gorbin.asne.core.persons.SocialPerson;

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
    /*** Postal code of social person from contacts*/
	public String postalCode;
    /*** Location description of social person from contacts*/
	public String locationDescription;
    /*** Location address of social person from contacts*/
	public String locationAddress;
    /*** The person's job industry within the organization.*/
	public String industry;
    /*** Summary of social person*/
	public String summary;
    /*** Birthday of social person in the format MM/DD/YYYY*/
	public String birthday;
    /*** Main address of social person from contacts*/
	public String mainAdress;
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
        postalCode = in.readString();
        locationDescription = in.readString();
        locationAddress = in.readString();
        industry = in.readString();
        summary = in.readString();
        birthday = in.readString();
        mainAdress = in.readString();
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
        dest.writeString(postalCode);
        dest.writeString(locationDescription);
        dest.writeString(locationAddress);
        dest.writeString(industry);
        dest.writeString(summary);
        dest.writeString(birthday);
        dest.writeString(mainAdress);
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
        if (locationAddress != null ? !locationAddress.equals(that.locationAddress) : that.locationAddress != null)
            return false;
        if (locationDescription != null ? !locationDescription.equals(that.locationDescription) : that.locationDescription != null)
            return false;
        if (mainAdress != null ? !mainAdress.equals(that.mainAdress) : that.mainAdress != null)
            return false;
        if (position != null ? !position.equals(that.position) : that.position != null)
            return false;
        if (postalCode != null ? !postalCode.equals(that.postalCode) : that.postalCode != null)
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
        result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
        result = 31 * result + (locationDescription != null ? locationDescription.hashCode() : 0);
        result = 31 * result + (locationAddress != null ? locationAddress.hashCode() : 0);
        result = 31 * result + (industry != null ? industry.hashCode() : 0);
        result = 31 * result + (summary != null ? summary.hashCode() : 0);
        result = 31 * result + (birthday != null ? birthday.hashCode() : 0);
        result = 31 * result + (mainAdress != null ? mainAdress.hashCode() : 0);
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
                ", postalCode='" + postalCode + '\'' +
                ", locationDescription='" + locationDescription + '\'' +
                ", locationAddress='" + locationAddress + '\'' +
                ", industry='" + industry + '\'' +
                ", summary='" + summary + '\'' +
                ", birthday='" + birthday + '\'' +
                ", mainAdress='" + mainAdress + '\'' +
                ", currentStatus='" + currentStatus + '\'' +
                ", interests='" + interests + '\'' +
                ", specialties='" + specialties + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
