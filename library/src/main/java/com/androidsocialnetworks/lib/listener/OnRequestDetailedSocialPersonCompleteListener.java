package com.androidsocialnetworks.lib.listener;

import com.androidsocialnetworks.lib.listener.base.SocialNetworkListener;
import com.androidsocialnetworks.lib.persons.SocialPerson;

public interface OnRequestDetailedSocialPersonCompleteListener<T extends SocialPerson> extends SocialNetworkListener {
    public void onRequestDetailedSocialPersonSuccess(int socialNetworkID, T socialPerson);
}