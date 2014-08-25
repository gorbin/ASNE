package com.github.gorbin.asne.core.listener;

import com.github.gorbin.asne.core.AccessToken;
import com.github.gorbin.asne.core.listener.base.SocialNetworkListener;

public interface OnRequestAccessTokenCompleteListener extends SocialNetworkListener {
    public void onRequestAccessTokenComplete(int socialNetworkID, AccessToken accessToken);
}
