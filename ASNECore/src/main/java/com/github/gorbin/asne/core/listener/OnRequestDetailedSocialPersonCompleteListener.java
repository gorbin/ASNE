package com.github.gorbin.asne.core.listener;

import com.github.gorbin.asne.core.listener.base.SocialNetworkListener;
import com.github.gorbin.asne.core.persons.SocialPerson;

/**
 * Interface definition for a callback to be invoked when detailed social person request complete.
 */
public interface OnRequestDetailedSocialPersonCompleteListener<T extends SocialPerson> extends SocialNetworkListener {
    /**
     * Called when detailed social person request complete.
     * @param socialNetworkID id of social network where request was complete
     * @param socialPerson Detailed social person object. Look at Person class in chosen module.
     */
    public void onRequestDetailedSocialPersonSuccess(int socialNetworkID, T socialPerson);
}