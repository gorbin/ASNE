package com.github.gorbin.asne.core.listener;

import com.github.gorbin.asne.core.listener.base.SocialNetworkListener;
import com.github.gorbin.asne.core.persons.SocialPerson;

import java.util.ArrayList;
/**
 * Interface definition for a callback to be invoked when friends list request complete.
 */
public interface OnRequestGetFriendsCompleteListener extends SocialNetworkListener {
    /**
     * Called when friends list request complete.
     * @param socialNetworkID id of social network where request was complete
     * @param friendsID array of friends list's user ids
     */
    public void OnGetFriendsIdComplete(int socialNetworkID, String[] friendsID);

    /**
     * Called when friends list request complete.
     * @param socialNetworkID id of social network where request was complete
     * @param socialFriends ArrayList of of friends list's social persons
     */
    public void OnGetFriendsComplete(int socialNetworkID, ArrayList<SocialPerson> socialFriends);
}
