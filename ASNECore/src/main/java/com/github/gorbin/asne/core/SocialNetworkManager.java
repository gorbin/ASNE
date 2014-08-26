package com.github.gorbin.asne.core;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SocialNetworkManager extends Fragment {

    private static final String TAG = SocialNetworkManager.class.getSimpleName();

    private Map<Integer, SocialNetwork> mSocialNetworksMap = new HashMap<Integer, SocialNetwork>();
    private OnInitializationCompleteListener mOnInitializationCompleteListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onResume();
        }

        if (mOnInitializationCompleteListener != null) {
            mOnInitializationCompleteListener.onSocialNetworkManagerInitialized();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onStop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onDestroy();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onActivityResult(requestCode, resultCode, data);
        }
    }

    public SocialNetwork getSocialNetwork(int id) throws SocialNetworkException {
        if (!mSocialNetworksMap.containsKey(id)) {
            throw new SocialNetworkException("Social network with id = " + id + " not found");
        }

        return mSocialNetworksMap.get(id);
    }

    public void addSocialNetwork(SocialNetwork socialNetwork) {
        if (mSocialNetworksMap.get(socialNetwork.getID()) != null) {
            throw new SocialNetworkException("Social network with id = " + socialNetwork.getID() + " already exists");
        }

        mSocialNetworksMap.put(socialNetwork.getID(), socialNetwork);
    }

    public List<SocialNetwork> getInitializedSocialNetworks() {
        return Collections.unmodifiableList(new ArrayList<SocialNetwork>(mSocialNetworksMap.values()));
    }

    public void setOnInitializationCompleteListener(OnInitializationCompleteListener onInitializationCompleteListener) {
        mOnInitializationCompleteListener = onInitializationCompleteListener;
    }

    public static interface OnInitializationCompleteListener {
        public void onSocialNetworkManagerInitialized();
    }
}
