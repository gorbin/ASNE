package com.github.gorbin.asne.core.listener;

import com.github.gorbin.asne.core.listener.base.SocialNetworkListener;

public interface OnRequestAddFriendCompleteListener extends SocialNetworkListener {
    public void onRequestAddFriendComplete(int socialNetworkID, String userID);
}
