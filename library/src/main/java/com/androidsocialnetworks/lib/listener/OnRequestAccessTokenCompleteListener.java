package com.androidsocialnetworks.lib.listener;

import com.androidsocialnetworks.lib.AccessToken;
import com.androidsocialnetworks.lib.listener.base.SocialNetworkListener;

public interface OnRequestAccessTokenCompleteListener extends SocialNetworkListener {
    public void onRequestAccessTokenComplete(int socialNetworkID, AccessToken accessToken);
}
