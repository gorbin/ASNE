package com.github.gorbin.asne.core.listener;

import com.github.gorbin.asne.core.listener.base.SocialNetworkListener;

/**
 * Interface definition for a callback to be invoked when check friend request complete.
 */
public interface OnCheckIsFriendCompleteListener extends SocialNetworkListener {
    /**
     * Called when check friend request complete.
     * @param socialNetworkID id of social network where request was complete
     * @param userID user id that was checked
     * @param isFriend true if friends, else false
     */
    public void onCheckIsFriendComplete(int socialNetworkID, String userID, boolean isFriend);
}
