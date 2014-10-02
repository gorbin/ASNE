package com.github.gorbin.asne.core.listener;

import com.github.gorbin.asne.core.listener.base.SocialNetworkListener;

/**
 * Interface definition for a callback to be invoked when invite friend request complete.
 */
public interface OnRequestAddFriendCompleteListener extends SocialNetworkListener {

    /**
     * Called when invite friend request complete.
     * @param socialNetworkID id of social network where request was complete
     * @param userID user id that was invited
     */
    public void onRequestAddFriendComplete(int socialNetworkID, String userID);
}
