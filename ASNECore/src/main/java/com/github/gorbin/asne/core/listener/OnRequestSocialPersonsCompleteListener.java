package com.github.gorbin.asne.core.listener;

import com.github.gorbin.asne.core.listener.base.SocialNetworkListener;
import com.github.gorbin.asne.core.persons.SocialPerson;

import java.util.ArrayList;

public interface OnRequestSocialPersonsCompleteListener extends SocialNetworkListener {
    public void onRequestSocialPersonsSuccess(int socialNetworkID, ArrayList<SocialPerson> socialPersons);
}
