package com.androidsocialnetworks.lib.listener;

import com.androidsocialnetworks.lib.listener.base.SocialNetworkListener;
import com.androidsocialnetworks.lib.persons.SocialPerson;

public interface OnRequestSocialPersonCompleteListener extends SocialNetworkListener {
    public void onRequestSocialPersonSuccess(int socialNetworkId, SocialPerson socialPerson);
}
