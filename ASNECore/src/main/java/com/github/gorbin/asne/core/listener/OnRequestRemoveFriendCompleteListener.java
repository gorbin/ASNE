package com.github.gorbin.asne.core.listener;

import com.github.gorbin.asne.core.listener.base.SocialNetworkListener;

/**
 * Interface definition for a callback to be invoked when remove friend request complete.
 */
public interface OnRequestRemoveFriendCompleteListener extends SocialNetworkListener {
    /**
     * Called when remove friend request complete.
     * @param socialNetworkID id of social network where request was complete
     * @param userID user id that was removed
     */
    public void onRequestRemoveFriendComplete(int socialNetworkID, String userID);
}
