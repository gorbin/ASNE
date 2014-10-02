package com.github.gorbin.asne.core.listener;

import com.github.gorbin.asne.core.listener.base.SocialNetworkListener;

/**
 * Interface definition for a callback to be invoked when login complete.
 */
public interface OnLoginCompleteListener extends SocialNetworkListener {
    /**
     * Called when login complete.
     * @param socialNetworkID id of social network where request was complete
     */
    public void onLoginSuccess(int socialNetworkID);
}
