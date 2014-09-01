package com.github.gorbin.asne.odnoklassniki;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.github.gorbin.asne.core.AccessToken;
import com.github.gorbin.asne.core.OAuthSocialNetwork;
import com.github.gorbin.asne.core.SocialNetworkAsyncTask;
import com.github.gorbin.asne.core.SocialNetworkException;
import com.github.gorbin.asne.core.listener.OnCheckIsFriendCompleteListener;
import com.github.gorbin.asne.core.listener.OnLoginCompleteListener;
import com.github.gorbin.asne.core.listener.OnPostingCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestAccessTokenCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestAddFriendCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestDetailedSocialPersonCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestGetFriendsCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestRemoveFriendCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestSocialPersonCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestSocialPersonsCompleteListener;
import com.github.gorbin.asne.core.persons.SocialPerson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.ok.android.sdk.Odnoklassniki;
import ru.ok.android.sdk.OkTokenRequestListener;

public class OkSocialNetwork extends OAuthSocialNetwork implements OkTokenRequestListener {
    public static final int ID = 6;
    private static final String FRIENDS = "OkSocialNetwork.FRIENDS";
    private static final String USERID = "OkSocialNetwork.USERID";
    private static final String TOKEN = "OkSocialNetwork.TOKEN";
    private static final String ERROR_CODE = "OkSocialNetwork.ERROR_CODE";
    private Activity activity;
    private String userId;
    private Bundle requestBundle;
	private Odnoklassniki mOdnoklassniki;
    private  String[] permissions;
    private String appId;
    private String appPublicKey;
    private String appSecretKey;

    public OkSocialNetwork(Fragment fragment, String appId, String appPublicKey, String appSecretKey, String[] permissions) {
        super(fragment);
        if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(appPublicKey) || TextUtils.isEmpty(appSecretKey)) {
            throw new IllegalArgumentException("TextUtils.isEmpty(appId) || TextUtils.isEmpty(appPublicKey) || TextUtils.isEmpty(appSecretKey)");
        }
        this.appId = appId;
        this.appPublicKey = appPublicKey;
        this.appSecretKey = appSecretKey;
        this.permissions = permissions;
        activity = mSocialNetworkManager.getActivity();
        mOdnoklassniki = Odnoklassniki.createInstance(activity, appId, appSecretKey, appPublicKey);
        mOdnoklassniki.setTokenRequestListener(this);
    }

    @Override
    public void onSuccess(String token) {
        mSharedPreferences.edit()
                .putString(TOKEN, token)
                .apply();
        if (mLocalListeners.get(REQUEST_LOGIN) != null) {
            ((OnLoginCompleteListener) mLocalListeners.get(REQUEST_LOGIN)).onLoginSuccess(getID());
            mLocalListeners.remove(REQUEST_LOGIN);
            return;
        }

        if (mLocalListeners.containsKey(REQUEST_GET_DETAIL_PERSON)) {
            mRequests.remove(REQUEST_GET_DETAIL_PERSON);
            executeRequest(new RequestGetDetailedPersonAsyncTask(), requestBundle, REQUEST_GET_DETAIL_PERSON);
        } else if (mLocalListeners.containsKey(REQUEST_GET_PERSON)) {
            mRequests.remove(REQUEST_GET_PERSON);
            executeRequest(new RequestGetSocialPersonAsyncTask(), requestBundle, REQUEST_GET_PERSON);
        } else if(mLocalListeners.containsKey(REQUEST_GET_CURRENT_PERSON)){
            mRequests.remove(REQUEST_GET_CURRENT_PERSON);
            executeRequest(new RequestGetSocialPersonAsyncTask(), requestBundle, REQUEST_GET_CURRENT_PERSON);
        } else if(mLocalListeners.containsKey(REQUEST_GET_PERSONS)){
            mRequests.remove(REQUEST_GET_PERSONS);
            executeRequest(new RequestSocialPersonsAsyncTask(), requestBundle, REQUEST_GET_PERSONS);
        } else if (mLocalListeners.containsKey(REQUEST_POST_LINK)) {
            mRequests.remove(REQUEST_POST_LINK);
            executeRequest(new RequestPostLinkAsyncTask(), requestBundle, REQUEST_POST_LINK);
        } else if(mLocalListeners.containsKey(REQUEST_CHECK_IS_FRIEND)){
            mRequests.remove(REQUEST_CHECK_IS_FRIEND);
            executeRequest(new RequestCheckIsFriendAsyncTask(), requestBundle, REQUEST_CHECK_IS_FRIEND);
        } else if(mLocalListeners.containsKey(REQUEST_GET_FRIENDS)){
            mRequests.remove(REQUEST_GET_FRIENDS);
            executeRequest(new RequestGetFriendsAsyncTask(), requestBundle, REQUEST_GET_FRIENDS);
        } else if (mLocalListeners.containsKey(REQUEST_ACCESS_TOKEN)) {
            mRequests.remove(REQUEST_ACCESS_TOKEN);
            String accessToken = mSharedPreferences.getString(TOKEN, null);
            ((OnRequestAccessTokenCompleteListener) mLocalListeners.get(REQUEST_ACCESS_TOKEN))
                    .onRequestAccessTokenComplete(getID(), new AccessToken(accessToken, null));
        }
    }

    @Override
    public void onError() {
        mLocalListeners.get(REQUEST_LOGIN).onError(getID(), REQUEST_LOGIN, "OK Login Error!", null);
    }

    @Override
    public void onCancel() {
        mLocalListeners.get(REQUEST_LOGIN).onError(getID(), REQUEST_LOGIN, "ОK Login cancaled!", null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean isConnected() {
        String accessToken = mSharedPreferences.getString(TOKEN, null);
        return accessToken != null;
    }

    @Override
    public void requestLogin(OnLoginCompleteListener onLoginCompleteListener) {
        super.requestLogin(onLoginCompleteListener);
		mOdnoklassniki.requestAuthorization(activity, false, permissions);
    }

    @Override
    public void logout() {
        mSharedPreferences.edit()
                .remove(TOKEN)
                .apply();
        mOdnoklassniki.clearTokens(activity);
        mOdnoklassniki.removeTokenRequestListener();

    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public AccessToken getAccessToken() {
        String accessToken = mSharedPreferences.getString(TOKEN, null);
        return new AccessToken(accessToken, null);
    }

    @Override
    public void requestAccessToken(OnRequestAccessTokenCompleteListener onRequestAccessTokenCompleteListener) {
        super.requestAccessToken(onRequestAccessTokenCompleteListener);
        String accessToken = mSharedPreferences.getString(TOKEN, null);
        ((OnRequestAccessTokenCompleteListener) mLocalListeners.get(REQUEST_ACCESS_TOKEN))
                .onRequestAccessTokenComplete(getID(), new AccessToken(accessToken, null));
    }

    private boolean checkTokenError(Bundle result){
        if(result != null && result.containsKey(ERROR_CODE) && result.getString(ERROR_CODE).equals("102")) {
            requestBundle = result;
            requestBundle.remove(ERROR_CODE);
            requestBundle.remove(SocialNetworkAsyncTask.RESULT_ERROR);
            mOdnoklassniki.refreshToken(activity);
            return true;
        }
        return false;
    }

    private String requestIdPerson(Bundle result) throws IOException, JSONException {
        Map<String, String> idRequestParams = new HashMap<String, String>();
        idRequestParams.put("fields", "uid");
        String idResponse = mOdnoklassniki.request("users.getCurrentUser", idRequestParams, "get");
        JSONObject jsonObject = new JSONObject(idResponse);
        String id = jsonObject.getString("uid");
        result.putString(USERID, userId);
        return id;
    }

    @Override
    public void requestCurrentPerson(OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener) {
        super.requestCurrentPerson(onRequestSocialPersonCompleteListener);
        executeRequest(new RequestGetSocialPersonAsyncTask(), null, REQUEST_GET_CURRENT_PERSON);
    }

    @Override
    public void requestSocialPerson(String userID, OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener) {
        super.requestSocialPerson(userID, onRequestSocialPersonCompleteListener);
        if (TextUtils.isEmpty(userID)) {
            throw new SocialNetworkException("userID can't be null or empty");
        }
        Bundle args = new Bundle();
        args.putString(RequestGetSocialPersonAsyncTask.PARAM_USER_ID, userID);
        executeRequest(new RequestGetSocialPersonAsyncTask(), args, REQUEST_GET_PERSON);
    }

    @Override
    public void requestSocialPersons(String[] userID, OnRequestSocialPersonsCompleteListener onRequestSocialPersonsCompleteListener) {
        super.requestSocialPersons(userID, onRequestSocialPersonsCompleteListener);
        Bundle args = new Bundle();
        args.putStringArray(RequestSocialPersonsAsyncTask.PARAM_USER_ID, userID);
        executeRequest(new RequestSocialPersonsAsyncTask(), args, REQUEST_GET_PERSONS);
    }

    @Override
    public void requestDetailedSocialPerson(String userId, OnRequestDetailedSocialPersonCompleteListener onRequestDetailedSocialPersonCompleteListener) {
        super.requestDetailedSocialPerson(userId, onRequestDetailedSocialPersonCompleteListener);
        Bundle args = new Bundle();
        if(userId != null) {
            args.putString(RequestGetDetailedPersonAsyncTask.PARAM_USER_ID, userId);
        }
        executeRequest(new RequestGetDetailedPersonAsyncTask(), args, REQUEST_GET_DETAIL_PERSON);
    }

    private SocialPerson getSocialPerson(SocialPerson socialPerson, JSONObject jsonResponse) throws JSONException {
        if(jsonResponse.has("uid")) {
            socialPerson.id = jsonResponse.getString("uid");
            socialPerson.profileURL = "http://www.odnoklassniki.ru/profile/" + jsonResponse.getString("uid");
        }
        if(jsonResponse.has("name")) {
            socialPerson.name = jsonResponse.getString("name");
        }
        if(jsonResponse.has("pic190x190")) {
            socialPerson.avatarURL = jsonResponse.getString("pic190x190");
        }
        return socialPerson;
    }

    private OkPerson getDetailedSocialPerson(OkPerson okPerson, JSONObject jsonResponse) throws JSONException {
        getSocialPerson(okPerson, jsonResponse);
        if(jsonResponse.has("first_name")) {
            okPerson.firstName = jsonResponse.getString("first_name");
        }
        if(jsonResponse.has("last_name")) {
            okPerson.lastName = jsonResponse.getString("last_name");
        }
        if(jsonResponse.has("gender")) {
            okPerson.gender = jsonResponse.getString("gender");
        }
        if(jsonResponse.has("birthday")) {
            okPerson.birthday = jsonResponse.getString("birthday");
        }
        if(jsonResponse.has("age")) {
            okPerson.age = jsonResponse.getString("age");
        }
        if(jsonResponse.has("locale")) {
            okPerson.locale = jsonResponse.getString("locale");
        }
        if(jsonResponse.has("has_email")) {
            okPerson.has_email = jsonResponse.getBoolean("has_email");
        }
        if(jsonResponse.has("current_status")) {
            okPerson.current_status = jsonResponse.getString("current_status");
        }
        if(jsonResponse.has("online")) {
            okPerson.online = jsonResponse.getString("online");
        }
        if(jsonResponse.has("location")){
            if(jsonResponse.getJSONObject("location").has("city")){
                okPerson.city = jsonResponse.getJSONObject("location").getString("city");
            }
            if(jsonResponse.getJSONObject("location").has("countryName")){
                okPerson.country = jsonResponse.getJSONObject("location").getString("countryName");
            }
        }
        return okPerson;
    }

    @Override
    public void requestPostMessage(String message, OnPostingCompleteListener onPostingCompleteListener) {
        throw new SocialNetworkException("requestPostMessage isn't allowed for OkSocialNetwork");
    }
    
	@Override
    public void requestPostPhoto(File photo, final String message, OnPostingCompleteListener onPostingCompleteListener) {
        throw new SocialNetworkException("requestPostPhoto isn't allowed for OkSocialNetwork");
    }

    @Override
    public void requestPostLink(Bundle bundle, String message, OnPostingCompleteListener onPostingCompleteListener) {
        super.requestPostLink(bundle, message, onPostingCompleteListener);
        bundle.putString("message", message);
        executeRequest(new RequestPostLinkAsyncTask(), bundle, REQUEST_POST_LINK);
    }

    @Override
    public void requestPostDialog(Bundle bundle, OnPostingCompleteListener onPostingCompleteListener) {
        throw new SocialNetworkException("requestPostDialog isn't allowed for OkSocialNetwork");
    }

    @Override
    public void requestCheckIsFriend(final String userID, OnCheckIsFriendCompleteListener onCheckIsFriendCompleteListener) {
        super.requestCheckIsFriend(userID, onCheckIsFriendCompleteListener);
        if (TextUtils.isEmpty(userID)) {
            throw new SocialNetworkException("userID can't be null or empty");
        }
        Bundle args = new Bundle();
        args.putString(RequestCheckIsFriendAsyncTask.PARAM_USER_ID, userID);         args.putString(USERID, userId);
        executeRequest(new RequestCheckIsFriendAsyncTask(), args, REQUEST_CHECK_IS_FRIEND);
    }
    
	@Override
    public void requestGetFriends(OnRequestGetFriendsCompleteListener onRequestGetFriendsCompleteListener) {
        super.requestGetFriends(onRequestGetFriendsCompleteListener);
        executeRequest(new RequestGetFriendsAsyncTask(), null, REQUEST_GET_FRIENDS);
    }

    @Override
    public void requestAddFriend(final String userID, OnRequestAddFriendCompleteListener onRequestAddFriendCompleteListener) {
        throw new SocialNetworkException("requestAddFriend isn't allowed for OkSocialNetwork");
    }

    @Override
    public void requestRemoveFriend(String userID, OnRequestRemoveFriendCompleteListener onRequestRemoveFriendCompleteListener) {
        throw new SocialNetworkException("requestRemoveFriend isn't allowed for OkSocialNetwork");
    }

    private class RequestGetSocialPersonAsyncTask extends SocialNetworkAsyncTask {
        public static final String PARAM_USER_ID = "RequestGetPersonAsyncTask.PARAM_USER_ID";
        public static final String CURRENT = "RequestGetPersonAsyncTask.CURRENT";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle args = params[0];
            Bundle result = new Bundle(args);
            String userID;
            Map<String, String> requestParams = new HashMap<String, String>();
            requestParams.put("fields", "uid, name, pic190x190");
            String response;
            if (args.containsKey(PARAM_USER_ID)) {
                userID = args.getString(PARAM_USER_ID);
                requestParams.put("uids", userID);
            }

            try {
                JSONObject jsonObject = null;
                if (args.containsKey(PARAM_USER_ID)) {
                    response = mOdnoklassniki.request("users.getInfo", requestParams, "get");
                    result.putBoolean(CURRENT, false);
                } else {
                    response = mOdnoklassniki.request("users.getCurrentUser", requestParams, "get");
                    result.putBoolean(CURRENT, true);
                }
                Object json = new JSONTokener(response).nextValue();
                if(json instanceof JSONObject){
                    jsonObject = new JSONObject(response);
                    if(jsonObject.has("error_code")) {
                        result.putString(RESULT_ERROR, jsonObject.toString());
                        result.putString(ERROR_CODE, jsonObject.getString("error_code"));
                        return result;
                    }
                } else if (json instanceof JSONArray){
                    JSONArray jsonArray = new JSONArray(response);
                    jsonObject = jsonArray.getJSONObject(0);
                }
                if (jsonObject != null) {
                    result.putString(USERID, jsonObject.getString("uid"));
                }
                SocialPerson socialPerson = new SocialPerson();
                getSocialPerson(socialPerson, jsonObject);
                result.putParcelable(REQUEST_GET_PERSON, socialPerson);
            } catch (Exception e) {
                e.printStackTrace();
                result.putString(RESULT_ERROR, e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            userId = result.getString(USERID);
            if(checkTokenError(result)) return;
            SocialPerson socialPerson = result.getParcelable(REQUEST_GET_PERSON);
            if(result.containsKey(CURRENT) && result.getBoolean(CURRENT)){
                if (!handleRequestResult(result, REQUEST_GET_CURRENT_PERSON)) return;
                ((OnRequestSocialPersonCompleteListener) mLocalListeners.get(REQUEST_GET_CURRENT_PERSON))
                        .onRequestSocialPersonSuccess(getID(), socialPerson);
            } else {
                if (!handleRequestResult(result, REQUEST_GET_PERSON)) return;
                ((OnRequestSocialPersonCompleteListener) mLocalListeners.get(REQUEST_GET_PERSON))
                        .onRequestSocialPersonSuccess(getID(), socialPerson);
            }
        }
    }

    private class RequestGetDetailedPersonAsyncTask extends SocialNetworkAsyncTask {
        public static final String PARAM_USER_ID = "RequestGetPersonAsyncTask.PARAM_USER_ID";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle args = params[0];
            Bundle result = new Bundle(args);
            String userID;
            Map<String, String> requestParams = new HashMap<String, String>();
            requestParams.put("fields", "uid, name, pic190x190, last_name, first_name, gender, birthday, age, locale, " +
                    "has_email, current_status, online, location");
            String response;
            if (args.containsKey(PARAM_USER_ID)) {
                userID = args.getString(PARAM_USER_ID);
                requestParams.put("uids", userID);
            }

            try {
                JSONObject jsonObject = null;
                if (args.containsKey(PARAM_USER_ID)) {
                    response = mOdnoklassniki.request("users.getInfo", requestParams, "get");
                } else {
                    response = mOdnoklassniki.request("users.getCurrentUser", requestParams, "get");
                }
                Object json = new JSONTokener(response).nextValue();
                if(json instanceof JSONObject){
                    jsonObject = new JSONObject(response);
                    if(jsonObject.has("error_code")) {
                        result.putString(RESULT_ERROR, jsonObject.toString());
                        result.putString(ERROR_CODE, jsonObject.getString("error_code"));
                        return result;
                    }
                } else if (json instanceof JSONArray){
                    JSONArray jsonArray = new JSONArray(response);
                    jsonObject = jsonArray.getJSONObject(0);
                }
                OkPerson okPerson = new OkPerson();
                getDetailedSocialPerson(okPerson, jsonObject);
                result.putParcelable(REQUEST_GET_DETAIL_PERSON, okPerson);
            } catch (Exception e) {
                e.printStackTrace();
                result.putString(RESULT_ERROR, e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if(checkTokenError(result)) return;
            if (!handleRequestResult(result, REQUEST_GET_DETAIL_PERSON)) return;
            OkPerson okPerson = result.getParcelable(REQUEST_GET_DETAIL_PERSON);
            ((OnRequestDetailedSocialPersonCompleteListener) mLocalListeners.get(REQUEST_GET_DETAIL_PERSON))
                    .onRequestDetailedSocialPersonSuccess(getID(), okPerson);
        }
    }

    private class RequestSocialPersonsAsyncTask extends SocialNetworkAsyncTask {
        public static final String PARAM_USER_ID = "RequestGetPersonAsyncTask.PARAM_USER_ID";
        private static final String RESULT_USERS_ARRAY = "RequestPersonAsyncTask.RESULT_USERS_ARRAY";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle args = params[0];
            Bundle result = new Bundle(args);
            String[] userIDs = args.getStringArray(PARAM_USER_ID);
            String ids = null;
            if (userIDs != null) {
                ids = TextUtils.join(",", userIDs);
            }
            Map<String, String> requestParams = new HashMap<String, String>();
            requestParams.put("uids", ids);
            requestParams.put("fields", "uid, name, pic190x190");
            String response;
            if (args.containsKey(FRIENDS)) {
                result.putBoolean(FRIENDS, true);
            }
            SocialPerson socialPerson = new SocialPerson();
            ArrayList<SocialPerson> socialPersons = new ArrayList<SocialPerson>();
            try {
                response = mOdnoklassniki.request("users.getInfo", requestParams, "get");
                Object json = new JSONTokener(response).nextValue();
                if(json instanceof JSONObject){
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("error_code")) {
                        result.putString(RESULT_ERROR, jsonObject.toString());
                        result.putString(ERROR_CODE, jsonObject.getString("error_code"));
                        return result;
                    }
                } else if (json instanceof JSONArray){
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        getSocialPerson(socialPerson, jsonArray.getJSONObject(i));
                        socialPersons.add(socialPerson);
                        socialPerson = new SocialPerson();
                    }
                    result.putParcelableArrayList(RESULT_USERS_ARRAY, socialPersons);
                }
            } catch (Exception e) {
                e.printStackTrace();
                result.putString(RESULT_ERROR, e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if(checkTokenError(result)) return;
            ArrayList<SocialPerson> socialPersons = result.getParcelableArrayList(RESULT_USERS_ARRAY);
            if (result.containsKey(FRIENDS)) {
                if (!handleRequestResult(result, REQUEST_GET_FRIENDS)) return;
                ((OnRequestGetFriendsCompleteListener) mLocalListeners.get(REQUEST_GET_FRIENDS))
                        .OnGetFriendsComplete(getID(), socialPersons);
            } else {
                if (!handleRequestResult(result, REQUEST_GET_PERSONS)) return;
                ((OnRequestSocialPersonsCompleteListener) mLocalListeners.get(REQUEST_GET_PERSONS))
                    .onRequestSocialPersonsSuccess(getID(), socialPersons);
                mLocalListeners.remove(REQUEST_GET_PERSONS);
            }
        }
    }

    private class RequestPostLinkAsyncTask extends SocialNetworkAsyncTask {

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle args = params[0];
            Bundle result = new Bundle(args);
            String link = args.getString("link");
            String comment = args.getString("message");
            try {
                Map<String, String> requestParams = new HashMap<String, String>();
                requestParams.put("linkUrl", link);
                if(comment != null) {
                    requestParams.put("comment", comment);
                }
                String response = mOdnoklassniki.request("share.addLink", requestParams, "get");
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.has("error_code")) {
                    result.putString(RESULT_ERROR, jsonObject.toString());
                    result.putString(ERROR_CODE, jsonObject.getString("error_code"));
                    return result;
                }
            }  catch (Exception e) {
                result.putString(RESULT_ERROR, e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if(checkTokenError(result)) return;
            if (!handleRequestResult(result, REQUEST_POST_LINK)) return;
            ((OnPostingCompleteListener) mLocalListeners.get(REQUEST_POST_LINK))
                    .onPostSuccessfully(getID());
        }
    }

    private class RequestGetFriendsAsyncTask extends SocialNetworkAsyncTask {
        public static final String RESULT_GET_FRIENDS_ID = "RESULT_GET_FRIENDS_ID";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle result = new Bundle(params[0]);
            ArrayList<String> friendIds = new ArrayList<String>();
            String response;
            try {
                response = mOdnoklassniki.request("friends.get", null, "get");
                Object json = new JSONTokener(response).nextValue();
                if(json instanceof JSONObject){
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("error_code")) {
                        result.putString(RESULT_ERROR, jsonObject.toString());
                        result.putString(ERROR_CODE, jsonObject.getString("error_code"));
                        return result;
                    }
                } else if (json instanceof JSONArray){
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        friendIds.add(jsonArray.getString(i));
                    }
                    result.putStringArray(RESULT_GET_FRIENDS_ID, friendIds.toArray(new String[friendIds.size()]));
                }
            } catch (Exception e) {
                e.printStackTrace();
                result.putString(RESULT_ERROR, e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if(checkTokenError(result)) return;
            if (!handleRequestResult(result, REQUEST_GET_FRIENDS,
                    result.getStringArray(RESULT_GET_FRIENDS_ID))) return;
            String[] friendsIds = result.getStringArray(RESULT_GET_FRIENDS_ID);

            ((OnRequestGetFriendsCompleteListener) mLocalListeners.get(REQUEST_GET_FRIENDS))
                        .OnGetFriendsIdComplete(getID(), friendsIds);
            Bundle args = new Bundle();
            args.putStringArray(RequestSocialPersonsAsyncTask.PARAM_USER_ID, friendsIds);
            args.putBoolean(FRIENDS, true);
            executeRequest(new RequestSocialPersonsAsyncTask(), args, REQUEST_GET_FRIENDS);
        }
    }

    private class RequestCheckIsFriendAsyncTask extends SocialNetworkAsyncTask {
        public static final String PARAM_USER_ID = "PARAM_USER_ID";
        public static final String RESULT_IS_FRIEND = "RESULT_IS_FRIEND";
        public static final String RESULT_REQUESTED_ID = "RESULT_REQUESTED_ID";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle args = params[0];
            Bundle result = new Bundle(args);
            String requestedId = args.getString(PARAM_USER_ID);
            result.putString(RESULT_REQUESTED_ID, requestedId);
            String userId = args.getString(USERID);
            String response;
            try {
                if(userId == null) {
                    userId = requestIdPerson(result);
                }
                Map<String, String> requestParams = new HashMap<String, String>();
                requestParams.put("uids1", userId);
                requestParams.put("uids2", requestedId);
                response = mOdnoklassniki.request("friends.areFriends", requestParams, "get");
                Object json = new JSONTokener(response).nextValue();
                if(json instanceof JSONObject){
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("error_code")) {
                        result.putString(RESULT_ERROR, jsonObject.toString());
                        result.putString(ERROR_CODE, jsonObject.getString("error_code"));
                        return result;
                    }
                } else if (json instanceof JSONArray){
                    JSONArray jsonResponseArray = new JSONArray(response);
                    result.putBoolean(RESULT_IS_FRIEND, jsonResponseArray.getJSONObject(0).getBoolean("are_friends"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                result.putString(RESULT_ERROR, e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            userId = result.getString(USERID);
            if(checkTokenError(result)) return;
            if (!handleRequestResult(result, REQUEST_CHECK_IS_FRIEND,
                    result.getString(RESULT_REQUESTED_ID))) return;
            ((OnCheckIsFriendCompleteListener) mLocalListeners.get(REQUEST_CHECK_IS_FRIEND))
                    .onCheckIsFriendComplete(getID(), result.getString(RESULT_REQUESTED_ID),
                            result.getBoolean(RESULT_IS_FRIEND));
        }
    }
}


