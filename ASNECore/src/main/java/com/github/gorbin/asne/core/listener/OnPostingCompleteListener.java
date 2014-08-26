package com.github.gorbin.asne.core.listener;

import com.github.gorbin.asne.core.listener.base.SocialNetworkListener;

public interface OnPostingCompleteListener extends SocialNetworkListener {
    public void onPostSuccessfully(int socialNetworkID);
}
