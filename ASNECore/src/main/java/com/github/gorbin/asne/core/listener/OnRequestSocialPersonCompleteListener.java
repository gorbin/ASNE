package com.github.gorbin.asne.core.listener;

import com.github.gorbin.asne.core.listener.base.SocialNetworkListener;
import com.github.gorbin.asne.core.persons.SocialPerson;

public interface OnRequestSocialPersonCompleteListener extends SocialNetworkListener {
    public void onRequestSocialPersonSuccess(int socialNetworkId, SocialPerson socialPerson);
}
