package com.github.gorbin.asne.core.listener;

import com.github.gorbin.asne.core.listener.base.SocialNetworkListener;
import com.github.gorbin.asne.core.persons.SocialPerson;
/**
 * Interface definition for a callback to be invoked when social person request complete.
 */
public interface OnRequestSocialPersonCompleteListener extends SocialNetworkListener {
    /**
     * Called when social person request complete.
     * @param socialNetworkId id of social network where request was complete
     * @param socialPerson requested {@link com.github.gorbin.asne.core.persons.SocialPerson}
     */
    public void onRequestSocialPersonSuccess(int socialNetworkId, SocialPerson socialPerson);
}
