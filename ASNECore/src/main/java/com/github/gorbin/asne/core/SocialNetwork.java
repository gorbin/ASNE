package com.github.gorbin.asne.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.github.gorbin.asne.core.listener.OnCheckIsFriendCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestAccessTokenCompleteListener;
import com.github.gorbin.asne.core.listener.OnLoginCompleteListener;
import com.github.gorbin.asne.core.listener.OnPostingCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestAddFriendCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestDetailedSocialPersonCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestGetFriendsCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestRemoveFriendCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestSocialPersonCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestSocialPersonsCompleteListener;
import com.github.gorbin.asne.core.listener.base.SocialNetworkListener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.github.gorbin.asne.core.Consts.TAG;

/**
 * Ids:
 * <p/>
 * 1 - Twitter
 * 2 - LinkedIn
 * 3 - Google Plus
 * 4 - Facebook
 * 5 - Vkontakte
 * 6 - Odnoklassniki
 */

public abstract class SocialNetwork {

    public static final String REQUEST_LOGIN = "SocialNetwork.REQUEST_LOGIN";
    public static final String REQUEST_LOGIN2 = "SocialNetwork.REQUEST_LOGIN2"; // used with OAuth in Twitter and LinekdIn
    public static final String REQUEST_ACCESS_TOKEN = "SocialNetwork.REQUEST_ACCESS_TOKEN";
    public static final String REQUEST_GET_DETAIL_PERSON = "SocialNetwork.REQUEST_GET_DETAIL_PERSON";
    public static final String REQUEST_GET_PERSON = "SocialNetwork.REQUEST_GET_PERSON";
    public static final String REQUEST_GET_PERSONS = "SocialNetwork.REQUEST_GET_PERSONS";
    public static final String REQUEST_GET_CURRENT_PERSON = "SocialNetwork.REQUEST_GET_CURRENT_PERSON";
    public static final String REQUEST_POST_MESSAGE = "SocialNetwork.REQUEST_POST_MESSAGE";
    public static final String REQUEST_POST_PHOTO = "SocialNetwork.REQUEST_POST_PHOTO";
    public static final String REQUEST_POST_LINK = "SocialNetwork.REQUEST_POST_LINK";
    public static final String REQUEST_POST_DIALOG = "SocialNetwork.REQUEST_POST_DIALOG";
    public static final String REQUEST_CHECK_IS_FRIEND = "SocialNetwork.REQUEST_CHECK_IS_FRIEND";
    public static final String REQUEST_GET_FRIENDS = "SocialNetwork.REQUEST_GET_FRIENDS";
    public static final String REQUEST_ADD_FRIEND = "SocialNetwork.REQUEST_ADD_FRIEND";
    public static final String REQUEST_REMOVE_FRIEND = "SocialNetwork.REQUEST_REMOVE_FRIEND";

    public static final String BUNDLE_MESSAGE = "message";
    public static final String BUNDLE_LINK = "link";
    public static final String DIALOG_FRIENDS = "friends";
    public static final String BUNDLE_NAME = "name";
    public static final String BUNDLE_APP_NAME = "app_name";
    public static final String BUNDLE_CAPTION = "caption";
    public static final String BUNDLE_PICTURE = "picture";

    private static final String SHARED_PREFERENCES_NAME = "social_networks";
    protected Fragment mSocialNetworkManager;
    protected SharedPreferences mSharedPreferences;
    protected Map<String, SocialNetworkListener> mGlobalListeners = new HashMap<String, SocialNetworkListener>();
    protected Map<String, SocialNetworkListener> mLocalListeners = new HashMap<String, SocialNetworkListener>();

    /**
     * @param fragment ant not activity or context, as we will need to call startActivityForResult,
     *                 we will want to receice on onActivityResult in out SocialNetworkManager
     *                 fragment
     */
    protected SocialNetwork(Fragment fragment) {
        mSocialNetworkManager = fragment;
        mSharedPreferences = mSocialNetworkManager.getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    //////////////////// LIFECYCLE ////////////////////

    public void onCreate(Bundle savedInstanceState) {

    }

    public void onStart() {

    }

    public void onResume() {

    }

    public void onPause() {

    }

    public void onStop() {

    }

    public void onDestroy() {
        cancelAll();
    }

    public void onSaveInstanceState(Bundle outState) {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    //////////////////// API ////////////////////

    public abstract boolean isConnected();

    public void requestLogin() {
        requestLogin(null);
    }

    public void requestLogin(OnLoginCompleteListener onLoginCompleteListener) {
        if (isConnected()) {
            throw new SocialNetworkException("Already connected, please check isConnected() method");
        }

        registerListener(REQUEST_LOGIN, onLoginCompleteListener);
    }

    public abstract void logout();

    public abstract int getID();

    public abstract AccessToken getAccessToken();

    public void requestAccessToken(){
        requestAccessToken(null);
    }

    public void requestAccessToken(OnRequestAccessTokenCompleteListener onRequestAccessTokenCompleteListener) {
        registerListener(REQUEST_ACCESS_TOKEN, onRequestAccessTokenCompleteListener);
    }
	
	public void requestCurrentPerson() {
        requestCurrentPerson(null);
    }

    public void requestCurrentPerson(OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener) {
        registerListener(REQUEST_GET_CURRENT_PERSON, onRequestSocialPersonCompleteListener);
    }

    public void requestSocialPerson(String userID) {
        requestSocialPerson(userID, null);
    }

    public void requestSocialPerson(String userID, OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener) {
        registerListener(REQUEST_GET_PERSON, onRequestSocialPersonCompleteListener);
    }

    public void requestSocialPersons(String[] userID) {
        requestSocialPersons(userID, null);
    }

    public void requestSocialPersons(String[] userID, OnRequestSocialPersonsCompleteListener onRequestSocialPersonsCompleteListener) {
        registerListener(REQUEST_GET_PERSONS, onRequestSocialPersonsCompleteListener);
    }

    public void requestDetailedSocialPerson(String userID) {
        requestDetailedSocialPerson(userID, null);
    }

    public void requestDetailedSocialPerson(String userID, OnRequestDetailedSocialPersonCompleteListener onRequestDetailedSocialPersonCompleteListener) {
        registerListener(REQUEST_GET_DETAIL_PERSON, onRequestDetailedSocialPersonCompleteListener);
    }

    public void requestDetailedCurrentPerson(){
        requestDetailedSocialPerson(null);
    }

    public void requestDetailedCurrentPerson(OnRequestDetailedSocialPersonCompleteListener onRequestDetailedSocialPersonCompleteListener) {
        requestDetailedSocialPerson(null, onRequestDetailedSocialPersonCompleteListener);
    }

    public void requestPostMessage(String message) {
        requestPostMessage(message, null);
    }

    public void requestPostMessage(String message, OnPostingCompleteListener onPostingCompleteListener) {
        registerListener(REQUEST_POST_MESSAGE, onPostingCompleteListener);
    }

    public void requestPostPhoto(File photo, String message) {
        requestPostPhoto(photo, message, null);
    }

    public void requestPostPhoto(File photo, String message, OnPostingCompleteListener onPostingCompleteListener) {
        registerListener(REQUEST_POST_PHOTO, onPostingCompleteListener);
    }

    public void requestPostDialog(Bundle bundle) {
        requestPostDialog(bundle, null);
    }

    public void requestPostDialog(Bundle bundle, OnPostingCompleteListener onPostingCompleteListener) {
        registerListener(REQUEST_POST_DIALOG, onPostingCompleteListener);
    }

    public void requestPostLink(Bundle bundle, String message) {
        requestPostLink(bundle, message, null);
    }

    public void requestPostLink(Bundle bundle, String message, OnPostingCompleteListener onPostingCompleteListener) {
        registerListener(REQUEST_POST_LINK, onPostingCompleteListener);
    }

    public void requestCheckIsFriend(String userID) {
        requestCheckIsFriend(userID, null);
    }

    public void requestCheckIsFriend(String userID, OnCheckIsFriendCompleteListener onCheckIsFriendCompleteListener) {
        registerListener(REQUEST_CHECK_IS_FRIEND, onCheckIsFriendCompleteListener);
    }

    public void requestGetFriends() {
        requestGetFriends(null);
    }

    public void requestGetFriends(OnRequestGetFriendsCompleteListener onRequestGetFriendsCompleteListener) {
        registerListener(REQUEST_GET_FRIENDS, onRequestGetFriendsCompleteListener);
    }

    public void requestAddFriend(String userID) {
        requestAddFriend(userID, null);
    }

    public void requestAddFriend(String userID, OnRequestAddFriendCompleteListener onRequestAddFriendCompleteListener) {
        registerListener(REQUEST_ADD_FRIEND, onRequestAddFriendCompleteListener);
    }

    public void requestRemoveFriend(String userID) {
        requestRemoveFriend(userID, null);
    }

    public void requestRemoveFriend(String userID, OnRequestRemoveFriendCompleteListener onRequestRemoveFriendCompleteListener) {
        registerListener(REQUEST_REMOVE_FRIEND, onRequestRemoveFriendCompleteListener);
    }

    public void cancelLoginRequest() {
        mLocalListeners.remove(REQUEST_LOGIN);
    }

    public void cancelAccessTokenRequest() {
        mLocalListeners.remove(REQUEST_ACCESS_TOKEN);
    }

    public void cancelGetCurrentPersonRequest() {
        mLocalListeners.remove(REQUEST_GET_CURRENT_PERSON);
    }

    public void cancelGetSocialPersonRequest() {
        mLocalListeners.remove(REQUEST_GET_PERSON);
    }

    public void cancelGetSocialPersonsRequest() {
        mLocalListeners.remove(REQUEST_GET_PERSONS);
    }

    public void cancelGetDetailedSocialRequest() {
        mLocalListeners.remove(REQUEST_GET_DETAIL_PERSON);
    }
    public void cancelPostMessageRequest() {
        mLocalListeners.remove(REQUEST_POST_MESSAGE);
    }

    public void cancelPostPhotoRequest() {
        mLocalListeners.remove(REQUEST_POST_PHOTO);
    }

    public void cancelPostLinkRequest() {
        mLocalListeners.remove(REQUEST_POST_LINK);
    }

    public void cancelPostDialogRequest() {
        mLocalListeners.remove(REQUEST_POST_DIALOG);
    }

    public void cancelCheckIsFriendRequest() {
        mLocalListeners.remove(REQUEST_CHECK_IS_FRIEND);
    }

    public void cancelGetFriendsRequest() {
        mLocalListeners.remove(REQUEST_GET_FRIENDS);
    }

    public void cancelAddFriendRequest() {
        mLocalListeners.remove(REQUEST_ADD_FRIEND);
    }

    public void cancelRemoveFriendRequest() {
        mLocalListeners.remove(REQUEST_REMOVE_FRIEND);
    }

    public void cancelAll() {
        Log.d(TAG, this + ":SocialNetwork.cancelAll()");

        // we need to call all, because in implementations we can possible do aditional work in specific methods
        cancelLoginRequest();
        cancelAccessTokenRequest();
        cancelGetCurrentPersonRequest();
        cancelGetSocialPersonRequest();
        cancelGetSocialPersonsRequest();
        cancelGetDetailedSocialRequest();
        cancelPostMessageRequest();
        cancelPostPhotoRequest();
        cancelPostLinkRequest();
        cancelPostDialogRequest();
        cancelGetFriendsRequest();
        cancelCheckIsFriendRequest();
        cancelAddFriendRequest();
        cancelRemoveFriendRequest();

        // remove all local listeners
        mLocalListeners = new HashMap<String, SocialNetworkListener>();
    }

    //////////////////// UTIL METHODS ////////////////////

    protected void checkRequestState(AsyncTask request) throws SocialNetworkException {
        if (request != null) {
            throw new SocialNetworkException(request.toString() + "Request is already running");
        }
    }

    private void registerListener(String listenerID, SocialNetworkListener socialNetworkListener) {
        if (socialNetworkListener != null) {
            mLocalListeners.put(listenerID, socialNetworkListener);
        } else {
            mLocalListeners.put(listenerID, mGlobalListeners.get(listenerID));
        }
    }

    //////////////////// SETTERS FOR GLOBAL LISTENERS ////////////////////

    public void setOnLoginCompleteListener(OnLoginCompleteListener onLoginCompleteListener) {
        mGlobalListeners.put(REQUEST_LOGIN, onLoginCompleteListener);
    }

    public void setOnRequestAccessTokenCompleteListener(OnRequestAccessTokenCompleteListener onRequestAccessTokenCompleteListener) {
        mGlobalListeners.put(REQUEST_ACCESS_TOKEN, onRequestAccessTokenCompleteListener);
    }

    public void setOnRequestCurrentPersonCompleteListener(OnRequestSocialPersonCompleteListener onRequestCurrentPersonCompleteListener) {
        mGlobalListeners.put(REQUEST_GET_CURRENT_PERSON, onRequestCurrentPersonCompleteListener);
    }

    public void setOnRequestSocialPersonCompleteListener(OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener) {
        mGlobalListeners.put(REQUEST_GET_PERSON, onRequestSocialPersonCompleteListener);
    }
	
	public void setOnRequestDetailedSocialPersonCompleteListener(OnRequestDetailedSocialPersonCompleteListener onRequestDetailedSocialPersonCompleteListener) {
        mGlobalListeners.put(REQUEST_GET_DETAIL_PERSON, onRequestDetailedSocialPersonCompleteListener);
    }

    public void setOnRequestSocialPersonsCompleteListener(OnRequestSocialPersonsCompleteListener onRequestSocialPersonsCompleteListener) {
        mGlobalListeners.put(REQUEST_GET_PERSONS, onRequestSocialPersonsCompleteListener);
    }

    public void setOnCheckIsFriendListener(OnCheckIsFriendCompleteListener onCheckIsFriendListener) {
        mGlobalListeners.put(REQUEST_CHECK_IS_FRIEND, onCheckIsFriendListener);
    }

    public void setOnPostingMessageCompleteListener(OnPostingCompleteListener onPostingCompleteListener) {
        mGlobalListeners.put(REQUEST_POST_MESSAGE, onPostingCompleteListener);
    }

    public void setOnPostingPhotoCompleteListener(OnPostingCompleteListener onPostingCompleteListener) {
        mGlobalListeners.put(REQUEST_POST_PHOTO, onPostingCompleteListener);
    }

    public void setOnPostingLinkCompleteListener(OnPostingCompleteListener onPostingCompleteListener) {
        mGlobalListeners.put(REQUEST_POST_LINK, onPostingCompleteListener);
    }

    public void setOnPostingDialogCompleteListener(OnPostingCompleteListener onPostingCompleteListener) {
        mGlobalListeners.put(REQUEST_POST_DIALOG, onPostingCompleteListener);
    }

    public void setOnRequestGetFriendsCompleteListener(OnRequestGetFriendsCompleteListener onRequestGetFriendsCompleteListener) {
        mGlobalListeners.put(REQUEST_GET_FRIENDS, onRequestGetFriendsCompleteListener);
    }

    public void setOnRequestAddFriendCompleteListener(OnRequestAddFriendCompleteListener onRequestAddFriendCompleteListener) {
        mGlobalListeners.put(REQUEST_ADD_FRIEND, onRequestAddFriendCompleteListener);
    }

    public void setOnRequestRemoveFriendCompleteListener(OnRequestRemoveFriendCompleteListener onRequestRemoveFriendCompleteListener) {
        mGlobalListeners.put(REQUEST_REMOVE_FRIEND, onRequestRemoveFriendCompleteListener);
    }
}
