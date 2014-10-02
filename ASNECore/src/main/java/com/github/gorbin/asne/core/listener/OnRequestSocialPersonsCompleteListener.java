package com.github.gorbin.asne.core.listener;

import com.github.gorbin.asne.core.listener.base.SocialNetworkListener;
import com.github.gorbin.asne.core.persons.SocialPerson;

import java.util.ArrayList;

/**
 * Interface definition for a callback to be invoked when array of social person request complete.
 */
public interface OnRequestSocialPersonsCompleteListener extends SocialNetworkListener {
    /**
     * Called when array of social person request complete.
     * @param socialNetworkID id of social network where request was complete
     * @param socialPersons ArrayList of requested {@link com.github.gorbin.asne.core.persons.SocialPerson}
     */
    public void onRequestSocialPersonsSuccess(int socialNetworkID, ArrayList<SocialPerson> socialPersons);
}
