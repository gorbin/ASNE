package com.github.gorbin.asne.core.listener;

import com.github.gorbin.asne.core.AccessToken;
import com.github.gorbin.asne.core.listener.base.SocialNetworkListener;

/**
 * Interface definition for a callback to be invoked when access token request complete.
 */
public interface OnRequestAccessTokenCompleteListener extends SocialNetworkListener {
    /**
     * Called when access token request complete.
     * @param socialNetworkID id of social network where request was complete
     * @param accessToken {@link com.github.gorbin.asne.core.AccessToken} that social network returned
     */
    public void onRequestAccessTokenComplete(int socialNetworkID, AccessToken accessToken);
}
