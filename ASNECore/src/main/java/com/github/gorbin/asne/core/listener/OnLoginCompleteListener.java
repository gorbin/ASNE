package com.github.gorbin.asne.core.listener;

import com.github.gorbin.asne.core.listener.base.SocialNetworkListener;

public interface OnLoginCompleteListener extends SocialNetworkListener {
    public void onLoginSuccess(int socialNetworkID);
}
