package com.github.gorbin.asne.core.listener;

import com.github.gorbin.asne.core.listener.base.SocialNetworkListener;

public interface OnCheckIsFriendCompleteListener extends SocialNetworkListener {
    public void onCheckIsFriendComplete(int socialNetworkID, String userID, boolean isFriend);
}
