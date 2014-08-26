package com.github.gorbin.asne.core.listener;

import com.github.gorbin.asne.core.listener.base.SocialNetworkListener;

public interface OnRequestRemoveFriendCompleteListener extends SocialNetworkListener {
    public void onRequestRemoveFriendComplete(int socialNetworkID, String userID);
}
