package com.androidsocialnetworks.lib.listener;

import com.androidsocialnetworks.lib.listener.base.SocialNetworkListener;
import com.androidsocialnetworks.lib.persons.SocialPerson;

import java.util.ArrayList;

public interface OnRequestSocialPersonsCompleteListener extends SocialNetworkListener {
    public void onRequestSocialPersonsSuccess(int socialNetworkID, ArrayList<SocialPerson> socialPersons);
}
