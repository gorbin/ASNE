package com.github.gorbin.asne.core.listener;

import com.github.gorbin.asne.core.listener.base.SocialNetworkListener;

/**
 * Interface definition for a callback to be invoked when posting complete.
 */
public interface OnPostingCompleteListener extends SocialNetworkListener {
    /**
     * Called when posting complete.
     * @param socialNetworkID id of social network where request was complete
     */
    public void onPostSuccessfully(int socialNetworkID);
}
