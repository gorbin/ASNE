/*******************************************************************************
 * Copyright (c) 2014 Evgeny Gorbin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package com.github.gorbin.asne.instagram;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.github.gorbin.asne.core.AccessToken;
import com.github.gorbin.asne.core.OAuthActivity;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

/**
 * Class for Instagram social network integration
 *
 * @author Evgeny Gorbin (gorbin.e.o@gmail.com)
 */
public class InstagramSocialNetwork extends OAuthSocialNetwork {
    /*** Social network ID in asne modules, should be unique*/
    public static final int ID = 7;
    private static final String SAVE_STATE_KEY_OAUTH_TOKEN = "InstagramSocialNetwork.SAVE_STATE_KEY_OAUTH_TOKEN";
    private static final String SAVE_STATE_KEY_OAUTH_REQUEST_TOKEN = "InstagramSocialNetwork.SAVE_STATE_KEY_OAUTH_SECRET";
    // max 16 bit to use in startActivityForResult
    private static final int REQUEST_AUTH = UUID.randomUUID().hashCode() & 0xFFFF;
    private static final String INSTAGRAM_TOKENURL ="https://api.instagram.com/oauth/access_token";
    private static final String INSTAGRAM_APIURL = "https://api.instagram.com/v1";
    private static final String ERROR_CODE = "InstagramSocialNetwork.ERROR_CODE";
//    private final String INSTAGRAM_CALLBACK_URL = "oauth://ASNE";
    private final String authURLString;
    private final String tokenURLString;
    private final String clientId;
    private final String clientSecret;
    private final String redirectURL;
    private boolean restart = false;
    private Bundle requestBundle;

    public InstagramSocialNetwork(Fragment fragment, String clientId, String clientSecret, String redirectURL, String scope) {
        super(fragment);

        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectURL = redirectURL;

        if (TextUtils.isEmpty(clientId) || TextUtils.isEmpty(clientSecret)) {
            throw new IllegalArgumentException("clientId and clientSecret are invalid");
        }
        if(scope == null) {
            scope = "basic";
        }
        String INSTAGRAM_AUTHURL = "https://api.instagram.com/oauth/authorize/";
        authURLString = INSTAGRAM_AUTHURL + "?client_id=" + clientId + "&redirect_uri="
                + redirectURL + "&response_type=code&display=touch&scope=" + scope;

        tokenURLString = INSTAGRAM_TOKENURL + "?client_id=" + clientId + "&client_secret="
                + clientSecret + "&redirect_uri=" + redirectURL + "&grant_type=authorization_code";
    }

    /**
     * Check is social network connected
     * @return true if connected to Instagram and false if not
     */
    @Override
    public boolean isConnected() {
        String accessToken = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null);
        String requestToken = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_REQUEST_TOKEN, null);
        return accessToken != null && requestToken != null;
    }

    /**
     * Make login request - authorize in Instagram social network
     * @param onLoginCompleteListener listener to trigger when Login complete
     */
    @Override
    public void requestLogin(OnLoginCompleteListener onLoginCompleteListener) {
        super.requestLogin(onLoginCompleteListener);
        initInstagramLogin();
    }

    private void initInstagramLogin(){
        Intent intent = new Intent(mSocialNetworkManager.getActivity(), OAuthActivity.class)
                .putExtra(OAuthActivity.PARAM_CALLBACK, redirectURL)
                .putExtra(OAuthActivity.PARAM_URL_TO_LOAD, authURLString);
        mSocialNetworkManager.getActivity().startActivityForResult(intent, REQUEST_AUTH);
    }

    /**
     * Logout from Instagram social network
     */
    @Override
    public void logout() {
        mSharedPreferences.edit()
                .remove(SAVE_STATE_KEY_OAUTH_TOKEN)
                .remove(SAVE_STATE_KEY_OAUTH_REQUEST_TOKEN)
                .apply();
    }

    /**
     * Get id of Instagram social network
     * @return Social network id for Instagram = 7
     */
    @Override
    public int getID() {
        return ID;
    }

    /**
     * Method to get AccessToken of Instagram social network
     * @return {@link com.github.gorbin.asne.core.AccessToken}
     */
    @Override
    public AccessToken getAccessToken() {
        return new com.github.gorbin.asne.core.AccessToken(
                mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null),
                null
        );
    }

    /**
     * Request {@link com.github.gorbin.asne.core.AccessToken} of Instagram social network that you can get from onRequestAccessTokenCompleteListener
     * @param onRequestAccessTokenCompleteListener listener for {@link com.github.gorbin.asne.core.AccessToken} request
     */
    @Override
    public void requestAccessToken(OnRequestAccessTokenCompleteListener onRequestAccessTokenCompleteListener) {
        super.requestAccessToken(onRequestAccessTokenCompleteListener);
        ((OnRequestAccessTokenCompleteListener) mLocalListeners.get(REQUEST_ACCESS_TOKEN))
                .onRequestAccessTokenComplete(getID(), new com.github.gorbin.asne.core.AccessToken(
                        mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null),
                        null
                ));
    }

    /**
     * Request current user {@link com.github.gorbin.asne.core.persons.SocialPerson}
     * @param onRequestSocialPersonCompleteListener listener for {@link com.github.gorbin.asne.core.persons.SocialPerson} request
     */
    @Override
    public void requestCurrentPerson(OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener) {
        super.requestCurrentPerson(onRequestSocialPersonCompleteListener);
        executeRequest(new RequestGetSocialPersonAsyncTask(), null, REQUEST_GET_CURRENT_PERSON);
    }

    /**
     * Request {@link com.github.gorbin.asne.core.persons.SocialPerson} by user id
     * @param userID id of Instagram user
     * @param onRequestSocialPersonCompleteListener listener for {@link com.github.gorbin.asne.core.persons.SocialPerson} request
     */
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

    /**
     * Request ArrayList of {@link com.github.gorbin.asne.core.persons.SocialPerson} by array of userIds
     * @param userID array of Instagram users id
     * @param onRequestSocialPersonsCompleteListener listener for array of {@link com.github.gorbin.asne.core.persons.SocialPerson} request
     */
    @Override
    public void requestSocialPersons(String[] userID, OnRequestSocialPersonsCompleteListener onRequestSocialPersonsCompleteListener) {
        super.requestSocialPersons(userID, onRequestSocialPersonsCompleteListener);
        Bundle args = new Bundle();
        args.putStringArray(RequestSocialPersonsAsyncTask.PARAM_USER_ID, userID);
        executeRequest(new RequestSocialPersonsAsyncTask(), args, REQUEST_GET_PERSONS);
    }

    /**
     * Request user {@link com.github.gorbin.asne.instagram.InstagramPerson} by userId - detailed user data
     * @param userId id of Instagram user
     * @param onRequestDetailedSocialPersonCompleteListener listener for {@link com.github.gorbin.asne.instagram.InstagramPerson} request
     */
    @Override
    public void requestDetailedSocialPerson(String userId, OnRequestDetailedSocialPersonCompleteListener onRequestDetailedSocialPersonCompleteListener) {
        super.requestDetailedSocialPerson(userId, onRequestDetailedSocialPersonCompleteListener);
        Bundle args = new Bundle();
        if(userId != null) {
            args.putString(RequestGetDetailedPersonAsyncTask.PARAM_USER_ID, userId);
        } else {
            args.putString(RequestGetDetailedPersonAsyncTask.PARAM_USER_ID, "self");
        }
        executeRequest(new RequestGetDetailedPersonAsyncTask(), args, REQUEST_GET_DETAIL_PERSON);
    }

    private SocialPerson getSocialPerson(SocialPerson socialPerson, JSONObject jsonResponse) throws JSONException {
        if(jsonResponse.has("id")) {
            socialPerson.id = jsonResponse.getString("id");
        }
        if(jsonResponse.has("username")) {
            socialPerson.name = jsonResponse.getString("username");
            socialPerson.profileURL = "http://www.instagram.com/" + jsonResponse.getString("username");
        }
        if(jsonResponse.has("profile_picture")) {
            socialPerson.avatarURL = jsonResponse.getString("profile_picture");
        }
        return socialPerson;
    }

    private InstagramPerson getDetailedSocialPerson(InstagramPerson instagramPerson, JSONObject jsonResponse) throws JSONException {
        getSocialPerson(instagramPerson, jsonResponse);
        if(jsonResponse.has("bio")) {
            instagramPerson.bio = jsonResponse.getString("bio");
        }
        if(jsonResponse.has("website")) {
            instagramPerson.website = jsonResponse.getString("website");
        }
        if(jsonResponse.has("full_name")) {
            instagramPerson.fullName = jsonResponse.getString("full_name");
        }
        if(jsonResponse.has("counts")){
            if(jsonResponse.getJSONObject("counts").has("media")){
                instagramPerson.media = jsonResponse.getJSONObject("counts").getInt("media");
            }
            if(jsonResponse.getJSONObject("counts").has("followed_by")) {
                instagramPerson.followedBy = jsonResponse.getJSONObject("counts").getInt("followed_by");
            }
            if(jsonResponse.getJSONObject("counts").has("follows")) {
                instagramPerson.follows = jsonResponse.getJSONObject("counts").getInt("follows");
            }
        }
        return instagramPerson;
    }

    /**
     * Post message to social network
     * @param message  message that should be shared
     * @param onPostingCompleteListener listener for posting request
     */
    @Override
    public void requestPostMessage(String message, OnPostingCompleteListener onPostingCompleteListener) {
        super.requestPostMessage(message, onPostingCompleteListener);
        throw new SocialNetworkException("requestPostMessage isn't allowed for InstagramSocialNetwork");
    }

    /**
     * Post photo to social network
     * @param photo photo that should be shared
     * @param message message that should be shared with photo
     * @param onPostingCompleteListener listener for posting request
     */
    @Override
    public void requestPostPhoto(File photo, String message, OnPostingCompleteListener onPostingCompleteListener) {
        super.requestPostPhoto(photo, message, onPostingCompleteListener);
        String instagramPackage = "com.instagram.android";
        String errorMessage = "You should install Instagram app first";
        if(isPackageInstalled(instagramPackage, mSocialNetworkManager.getActivity())){
            Intent normalIntent = new Intent(Intent.ACTION_SEND);
            normalIntent.setType("image/*");
            normalIntent.setPackage(instagramPackage);
            File media = new File(photo.getAbsolutePath());
            Uri uri = Uri.fromFile(media);
            normalIntent.putExtra(Intent.EXTRA_STREAM, uri);
            normalIntent.putExtra(Intent.EXTRA_TEXT, message);
            mSocialNetworkManager.getActivity().startActivity(normalIntent);
        } else {
            mLocalListeners.get(REQUEST_POST_PHOTO).onError(getID(), REQUEST_POST_PHOTO, errorMessage, null);
        }
        mLocalListeners.remove(REQUEST_POST_PHOTO);
    }

    /**
     * Not supported via Instagram api.
     * @throws com.github.gorbin.asne.core.SocialNetworkException
     * @param bundle bundle containing information that should be shared(Bundle constants in {@link com.github.gorbin.asne.core.SocialNetwork})
     * @param message message that should be shared with bundle
     * @param onPostingCompleteListener listener for posting request
     */
    @Override
    public void requestPostLink(Bundle bundle, String message, OnPostingCompleteListener onPostingCompleteListener) {
        throw new SocialNetworkException("requestPostLink isn't allowed for InstagramSocialNetwork");
    }

    /**
     * Not supported via Instagram api.
     * @throws com.github.gorbin.asne.core.SocialNetworkException
     * @param bundle bundle containing information that should be shared(Bundle constants in {@link com.github.gorbin.asne.core.SocialNetwork})
     * @param onPostingCompleteListener listener for posting request
     */
    @Override
    public void requestPostDialog(Bundle bundle, OnPostingCompleteListener onPostingCompleteListener) {
        throw new SocialNetworkException("requestPostDialog isn't allowed for InstagramSocialNetwork");
    }

    /**
     * Check if user by id is friend of current user
     * @param userID user id that should be checked as friend of current user
     * @param onCheckIsFriendCompleteListener listener for checking friend request
     */
    @Override
    public void requestCheckIsFriend(String userID, OnCheckIsFriendCompleteListener onCheckIsFriendCompleteListener) {
        super.requestCheckIsFriend(userID, onCheckIsFriendCompleteListener);
        Bundle args = new Bundle();
        args.putString(RequestCheckIsFriendAsyncTask.PARAM_USER_ID, userID);
        executeRequest(new RequestCheckIsFriendAsyncTask(), args, REQUEST_CHECK_IS_FRIEND);
    }

    /**
     * Get current user friends list
     * @param onRequestGetFriendsCompleteListener listener for getting list of current user friends
     */
    @Override
    public void requestGetFriends(OnRequestGetFriendsCompleteListener onRequestGetFriendsCompleteListener) {
        super.requestGetFriends(onRequestGetFriendsCompleteListener);
        executeRequest(new RequestGetFriendsAsyncTask(), null, REQUEST_GET_FRIENDS);
    }

    /**
     * Invite friend by id to current user
     * @param userID id of user that should be invited
     * @param onRequestAddFriendCompleteListener listener for invite result
     */
    @Override
    public void requestAddFriend(String userID, OnRequestAddFriendCompleteListener onRequestAddFriendCompleteListener) {
        super.requestAddFriend(userID, onRequestAddFriendCompleteListener);
        Bundle args = new Bundle();
        args.putString(RequestAddFriendAsyncTask.PARAM_USER_ID, userID);
        executeRequest(new RequestAddFriendAsyncTask(), args, REQUEST_ADD_FRIEND);
    }

    /**
     * Remove friend by id from current user friends
     * @param userID user id that should be removed from friends
     * @param onRequestRemoveFriendCompleteListener listener to remove friend request response
     */
    @Override
    public void requestRemoveFriend(String userID, OnRequestRemoveFriendCompleteListener onRequestRemoveFriendCompleteListener) {
        super.requestRemoveFriend(userID, onRequestRemoveFriendCompleteListener);
        Bundle args = new Bundle();
        args.putString(RequestAddFriendAsyncTask.PARAM_USER_ID, userID);
        executeRequest(new RequestRemoveFriendAsyncTask(), args, REQUEST_REMOVE_FRIEND);
    }

    /**
     * Overrided for Instagram support
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int sanitizedRequestCode = requestCode % 0x10000;
        if (sanitizedRequestCode != REQUEST_AUTH) return;
        super.onActivityResult(requestCode, resultCode, data);

        Uri uri = data != null ? data.getData() : null;

        if (uri != null && uri.toString().startsWith(redirectURL)) {
            String parts[] = uri.toString().split("=");
            String verifier = parts[1];
            RequestLogin2AsyncTask requestLogin2AsyncTask = new RequestLogin2AsyncTask();
            mRequests.put(REQUEST_LOGIN2, requestLogin2AsyncTask);
            Bundle args = new Bundle();
            args.putString(RequestLogin2AsyncTask.PARAM_VERIFIER, verifier);
            requestLogin2AsyncTask.execute(args);
        } else {
            if (mLocalListeners.get(REQUEST_LOGIN) != null) {
                mLocalListeners.get(REQUEST_LOGIN).onError(getID(), REQUEST_LOGIN, "incorrect URI returned: " + uri, null);
                mLocalListeners.remove(REQUEST_LOGIN);
            }
        }
    }

    /**
     * Cancel login request
     */
    @Override
    public void cancelLoginRequest() {
        super.cancelLoginRequest();
    }

    private String streamToString(InputStream p_is) {
        try {
            BufferedReader m_br;
            StringBuilder m_outString = new StringBuilder();
            m_br = new BufferedReader(new InputStreamReader(p_is));
            String m_read = m_br.readLine();
            while(m_read != null) {
                m_outString.append(m_read);
                m_read =m_br.readLine();
            }
            return m_outString.toString();
        }
        catch (Exception p_ex) {
            p_ex.printStackTrace();
            return null;
        }
    }

    private String checkInputStream(HttpURLConnection connection){
        String errorType = null, code = null, errorMessage = null;
        InputStream inputStream = connection.getErrorStream();
        String response = streamToString(inputStream);
        try {
            JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
            JSONObject jsonResponse = jsonObject.getJSONObject("meta");
            if(jsonResponse.has("error_type")) {
                errorType = jsonResponse.getString("error_type");
            }
            if(jsonResponse.has("code")) {
                code = jsonResponse.getString("code");
            }
            if(jsonResponse.has("error_message")) {
                errorMessage = jsonResponse.getString("error_message");
            }
            return "ERROR TYPE: " + errorType + " ERROR CODE: " + code + " ERROR MESSAGE: " + errorMessage;
        } catch (JSONException e) {
            return e.getMessage();
        }
    }
    
    private void checkConnectionErrors(HttpURLConnection connection) throws Exception {
            if(connection.getResponseCode() >= 400){
                throw new Exception(checkInputStream(connection));
            }
    }

    private boolean checkTokenError(Bundle result) {
        if(result != null && result.containsKey(ERROR_CODE) && result.getString(ERROR_CODE).contains("400") && result.getString(ERROR_CODE).contains("OAuth")) {
            restart = true;
            requestBundle = result;
            requestBundle.remove(ERROR_CODE);
            requestBundle.remove(SocialNetworkAsyncTask.RESULT_ERROR);
            initInstagramLogin();
            return true;
        }
        return false;
    }

    private boolean checkRequests(){
        boolean queryRequests = false;
        for(String request: mRequests.keySet()){
            if(request.equals(REQUEST_LOGIN)){
                break;
            }
            queryRequests = true;
        }
        return queryRequests;
    }

    private void checkExeption(Exception e, Bundle result){
        if(e.getMessage().contains("ERROR CODE") && e.getMessage().contains("OAuth")){
            result.putString(ERROR_CODE, e.getMessage());
        } else {
            result.putString(SocialNetworkAsyncTask.RESULT_ERROR, e.getMessage());
        }
    }

    private void restartRequests(){
        restart = false;
        if(mLocalListeners.containsKey(REQUEST_GET_CURRENT_PERSON)){
            mRequests.remove(REQUEST_GET_CURRENT_PERSON);
            executeRequest(new RequestGetSocialPersonAsyncTask(), requestBundle, REQUEST_GET_CURRENT_PERSON);
        } else if (mLocalListeners.containsKey(REQUEST_GET_PERSON)) {
            mRequests.remove(REQUEST_GET_PERSON);
            executeRequest(new RequestGetSocialPersonAsyncTask(), requestBundle, REQUEST_GET_PERSON);
        } else if (mLocalListeners.containsKey(REQUEST_GET_DETAIL_PERSON)) {
            mRequests.remove(REQUEST_GET_DETAIL_PERSON);
            executeRequest(new RequestGetDetailedPersonAsyncTask(), requestBundle, REQUEST_GET_DETAIL_PERSON);
        } else if(mLocalListeners.containsKey(REQUEST_GET_PERSONS)){
            mRequests.remove(REQUEST_GET_PERSONS);
            executeRequest(new RequestSocialPersonsAsyncTask(), requestBundle, REQUEST_GET_PERSONS);
        } else if(mRequests.containsKey(REQUEST_CHECK_IS_FRIEND)) {
            mRequests.remove(REQUEST_CHECK_IS_FRIEND);
            executeRequest(new RequestCheckIsFriendAsyncTask(), requestBundle, REQUEST_CHECK_IS_FRIEND);
        } else if(mRequests.containsKey(REQUEST_GET_FRIENDS)) {
            mRequests.remove(REQUEST_GET_FRIENDS);
            executeRequest(new RequestGetFriendsAsyncTask(), requestBundle, REQUEST_GET_FRIENDS);
        } else if(mRequests.containsKey(REQUEST_ADD_FRIEND)) {
            mRequests.remove(REQUEST_ADD_FRIEND);
            executeRequest(new RequestAddFriendAsyncTask(), requestBundle, REQUEST_ADD_FRIEND);
        } else if(mRequests.containsKey(REQUEST_REMOVE_FRIEND)) {
            mRequests.remove(REQUEST_REMOVE_FRIEND);
            executeRequest(new RequestGetFriendsAsyncTask(), requestBundle, REQUEST_REMOVE_FRIEND);
        }
    }

    private boolean isPackageInstalled(String packagename, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    
    private class RequestLogin2AsyncTask extends SocialNetworkAsyncTask {
        public static final String PARAM_VERIFIER = "Login2AsyncTask.PARAM_VERIFIER";

        private static final String RESULT_ACCESS_TOKEN = "Login2AsyncTask.RESULT_TOKEN";
        private static final String RESULT_REQUEST_TOKEN = "Login2AsyncTask.RESULT_SECRET";
        private static final String RESULT_USER_ID = "Login2AsyncTask.RESULT_USER_ID";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            String verifier = params[0].getString(PARAM_VERIFIER);

            Bundle result = new Bundle();
            try
            {
                URL url = new URL(tokenURLString);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.setDoInput(true);
                httpsURLConnection.setDoOutput(true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
                outputStreamWriter.write("client_id="+clientId+
                        "&client_secret=" + clientSecret +
                        "&grant_type=authorization_code" +
                        "&redirect_uri=" + redirectURL +
                        "&code=" + verifier);
                outputStreamWriter.flush();
                String response = streamToString(httpsURLConnection.getInputStream());
                JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();

                String accessToken = jsonObject.getString("access_token");
                String id = jsonObject.getJSONObject("user").getString("id");
                result.putString(RESULT_ACCESS_TOKEN, accessToken);
                result.putString(RESULT_REQUEST_TOKEN, verifier);
                result.putString(RESULT_USER_ID, id);
            } catch (Exception e) {
                result.putString(RESULT_ERROR, e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if (!handleRequestResult(result, REQUEST_LOGIN)&&!checkRequests()) {
                return;
            }

            mSharedPreferences.edit()
                    .putString(SAVE_STATE_KEY_OAUTH_TOKEN, result.getString(RESULT_ACCESS_TOKEN))
                    .putString(SAVE_STATE_KEY_OAUTH_REQUEST_TOKEN, result.getString(RESULT_REQUEST_TOKEN))
                    .apply();
            mRequests.remove(REQUEST_LOGIN2);
            if (mLocalListeners.get(REQUEST_LOGIN) != null && !restart) {
                ((OnLoginCompleteListener) mLocalListeners.get(REQUEST_LOGIN)).onLoginSuccess(getID());
            }
            restartRequests();
        }


    }

    private class RequestGetSocialPersonAsyncTask extends SocialNetworkAsyncTask {
        public static final String PARAM_USER_ID = "RequestGetPersonAsyncTask.PARAM_USER_ID";
        public static final String CURRENT = "RequestGetPersonAsyncTask.CURRENT";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle args = params[0];
            Bundle result = new Bundle(args);
            String userID;
            String token = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null);
            if (args.containsKey(PARAM_USER_ID)) {
                userID = args.getString(PARAM_USER_ID);
                result.putBoolean(CURRENT, false);
            } else {
                userID = "self";
                result.putBoolean(CURRENT, true);
            }
            String urlString = INSTAGRAM_APIURL + "/users/"+ userID +"/?access_token=" + token;
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                checkConnectionErrors(connection);

                InputStream inputStream = connection.getInputStream();
                String response = streamToString(inputStream);
                JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
                JSONObject jsonResponse = jsonObject.getJSONObject("data");

                SocialPerson socialPerson = new SocialPerson();
                getSocialPerson(socialPerson, jsonResponse);
                result.putParcelable(REQUEST_GET_PERSON, socialPerson);
            } catch (Exception e) {
                checkExeption(e, result);
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if (checkTokenError(result)){return;}
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
            String userID = args.getString(PARAM_USER_ID);
            String token = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null);

            String urlString = INSTAGRAM_APIURL + "/users/"+ userID +"/?access_token=" + token;
            
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                checkConnectionErrors(connection);
                InputStream inputStream = connection.getInputStream();
                String response = streamToString(inputStream);
                JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
                JSONObject jsonResponse = jsonObject.getJSONObject("data");

                InstagramPerson instagramPerson = new InstagramPerson();
                getDetailedSocialPerson(instagramPerson, jsonResponse);
                result.putParcelable(REQUEST_GET_DETAIL_PERSON, instagramPerson);
            } catch (Exception e) {
                checkExeption(e, result);
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if (checkTokenError(result)){return;}
            if (!handleRequestResult(result, REQUEST_GET_DETAIL_PERSON)) return;
            InstagramPerson instagramPerson = result.getParcelable(REQUEST_GET_DETAIL_PERSON);
            ((OnRequestDetailedSocialPersonCompleteListener) mLocalListeners.get(REQUEST_GET_DETAIL_PERSON))
                    .onRequestDetailedSocialPersonSuccess(getID(), instagramPerson);
        }
    }

    private class RequestSocialPersonsAsyncTask extends SocialNetworkAsyncTask {
        public static final String PARAM_USER_ID = "RequestGetPersonAsyncTask.PARAM_USER_ID";
        private static final String RESULT_USERS_ARRAY = "RequestPersonAsyncTask.RESULT_USERS_ARRAY";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle args = params[0];
            Bundle result = new Bundle(args);
            String token = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null);
            String[] userIDs = args.getStringArray(PARAM_USER_ID);
            ArrayList<SocialPerson> socialPersons = new ArrayList<SocialPerson>();

            for (String userID : userIDs) {
                String urlString = INSTAGRAM_APIURL + "/users/" + userID + "/?access_token=" + token;
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    checkConnectionErrors(connection);
                    InputStream inputStream = connection.getInputStream();
                    String response = streamToString(inputStream);
                    JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
                    JSONObject jsonResponse = jsonObject.getJSONObject("data");
                    SocialPerson socialPerson = new SocialPerson();
                    getSocialPerson(socialPerson, jsonResponse);
                    socialPersons.add(socialPerson);
                } catch (Exception e) {
                    checkExeption(e, result);
                }
            }
            result.putParcelableArrayList(RESULT_USERS_ARRAY, socialPersons);
            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if (checkTokenError(result)){return;}
            ArrayList<SocialPerson> arraylist = result.getParcelableArrayList(RESULT_USERS_ARRAY);
            if (!handleRequestResult(result, REQUEST_GET_PERSONS)) return;
            ((OnRequestSocialPersonsCompleteListener) mLocalListeners.get(REQUEST_GET_PERSONS))
                    .onRequestSocialPersonsSuccess(getID(), arraylist);
        }
    }

    private class RequestGetFriendsAsyncTask extends SocialNetworkAsyncTask {
        public static final String RESULT_GET_FRIENDS = "RESULT_GET_FRIENDS";
        public static final String RESULT_GET_FRIENDS_ID = "RESULT_GET_FRIENDS_ID";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle result = new Bundle();
            String token = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null);
            String urlString = INSTAGRAM_APIURL + "/users/self/follows/?access_token=" + token;
            ArrayList<SocialPerson> socialPersons = new ArrayList<SocialPerson>();
            ArrayList<String> ids = new ArrayList<String>();
            try {
                getAllFriends(urlString, socialPersons, ids);
                result.putStringArray(RESULT_GET_FRIENDS_ID, ids.toArray(new String[ids.size()]));
                result.putParcelableArrayList(RESULT_GET_FRIENDS, socialPersons);
            } catch (Exception e) {
                checkExeption(e, result);
            }

            return result;
        }

        private void getAllFriends(String urlString, final ArrayList<SocialPerson> socialPersons, final ArrayList<String> ids) throws Exception {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            checkConnectionErrors(connection);
            InputStream inputStream = connection.getInputStream();
            String response = streamToString(inputStream);
            JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();

            JSONObject jsonPagination;
            String nextToken = null;
            if(jsonObject.has("pagination")) {
                jsonPagination = jsonObject.getJSONObject("pagination");
                if(jsonPagination.has("next_url")) {
                    nextToken = jsonPagination.getString("next_url");
                }
            }
            JSONArray jsonResponse = jsonObject.getJSONArray("data");
            for(int i = 0; i < jsonResponse.length(); i++){
                SocialPerson socialPerson = new SocialPerson();
                getSocialPerson(socialPerson, jsonResponse.getJSONObject(i));
                socialPersons.add(socialPerson);
                ids.add(jsonResponse.getJSONObject(i).getString("id"));
            }

            if((nextToken != null) && (!TextUtils.isEmpty(nextToken))){
                getAllFriends(nextToken, socialPersons, ids);
            }
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if (checkTokenError(result)){return;}
            if (!handleRequestResult(result, REQUEST_GET_FRIENDS,
                    result.getStringArray(RESULT_GET_FRIENDS_ID))) return;

            ((OnRequestGetFriendsCompleteListener) mLocalListeners.get(REQUEST_GET_FRIENDS))
                    .OnGetFriendsIdComplete(getID(), result.getStringArray(RESULT_GET_FRIENDS_ID));
            ArrayList<SocialPerson> socialPersons = result.getParcelableArrayList(RESULT_GET_FRIENDS);
            ((OnRequestGetFriendsCompleteListener) mLocalListeners.get(REQUEST_GET_FRIENDS))
                    .OnGetFriendsComplete(getID(), socialPersons);
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
            String userID = args.getString(PARAM_USER_ID);
            String token = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null);
            String urlString = INSTAGRAM_APIURL + "/users/" + userID + "/relationship/?access_token=" + token;
            result.putString(RESULT_REQUESTED_ID, userID);
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                checkConnectionErrors(connection);
                InputStream inputStream = connection.getInputStream();
                String response = streamToString(inputStream);
                JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();

                JSONObject jsonResponse = jsonObject.getJSONObject("data");
                String outgoing_status = jsonResponse.getString("outgoing_status");
                if(outgoing_status.equals("follows")){
                    result.putBoolean(RESULT_IS_FRIEND, true);
                } else {
                    result.putBoolean(RESULT_IS_FRIEND, false);
                }
            } catch (Exception e) {
                checkExeption(e, result);
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if (checkTokenError(result)){return;}
            if (!handleRequestResult(result, REQUEST_CHECK_IS_FRIEND,
                    result.getString(RESULT_REQUESTED_ID))) return;

            ((OnCheckIsFriendCompleteListener) mLocalListeners.get(REQUEST_CHECK_IS_FRIEND))
                    .onCheckIsFriendComplete(getID(),
                            "" + result.getString(RESULT_REQUESTED_ID),
                            result.getBoolean(RESULT_IS_FRIEND)
                    );
        }
    }

    private class RequestAddFriendAsyncTask extends SocialNetworkAsyncTask {
        public static final String PARAM_USER_ID = "PARAM_USER_ID";

        public static final String RESULT_REQUESTED_ID = "RESULT_REQUESTED_ID";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle args = params[0];
            Bundle result = new Bundle(args);
            String userID = args.getString(PARAM_USER_ID);
            String token = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null);
            String urlString = INSTAGRAM_APIURL + "/users/" + userID + "/relationship/?access_token=" + token;
            String parameters = "action=follow";
            
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestMethod("POST");
                OutputStreamWriter request = new OutputStreamWriter(connection.getOutputStream());
                request.write(parameters);
                request.flush();
                request.close();
                checkConnectionErrors(connection);
                InputStream inputStream = connection.getInputStream();
                String response = streamToString(inputStream);
                JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();

                JSONObject jsonResponse = jsonObject.getJSONObject("data");
                String outgoing_status = jsonResponse.getString("outgoing_status");
                if(outgoing_status.equals("follows")||outgoing_status.equals("requested")){
                    result.putString(RESULT_REQUESTED_ID, userID);
                } else {
                    result.putString(RESULT_ERROR, "REQUEST_ADD_FRIEND Error");
                }
            } catch (Exception e) {
                checkExeption(e, result);
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if (checkTokenError(result)){return;}
            if (!handleRequestResult(result, REQUEST_ADD_FRIEND,
                    result.getString(RESULT_REQUESTED_ID))) return;

            ((OnRequestAddFriendCompleteListener) mLocalListeners.get(REQUEST_ADD_FRIEND))
                    .onRequestAddFriendComplete(getID(),
                            "" + result.getString(RESULT_REQUESTED_ID)
                    );
        }
    }

    private class RequestRemoveFriendAsyncTask extends SocialNetworkAsyncTask {
        public static final String PARAM_USER_ID = "PARAM_USER_ID";

        public static final String RESULT_REQUESTED_ID = "RESULT_REQUESTED_ID";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle args = params[0];
            Bundle result = new Bundle(args);
            String userID = args.getString(PARAM_USER_ID);
            String token = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null);
            String urlString = INSTAGRAM_APIURL + "/users/" + userID + "/relationship/?access_token=" + token;
            String parameters = "action=unfollow";
            
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestMethod("POST");
                OutputStreamWriter request = new OutputStreamWriter(connection.getOutputStream());
                request.write(parameters);
                request.flush();
                request.close();
                checkConnectionErrors(connection);
                InputStream inputStream = connection.getInputStream();
                String response = streamToString(inputStream);
                JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();

                JSONObject jsonResponse = jsonObject.getJSONObject("data");
                String outgoing_status = jsonResponse.getString("outgoing_status");
                if(outgoing_status.equals("none")){
                    result.putString(RESULT_REQUESTED_ID, userID);
                } else {
                    result.putString(RESULT_ERROR, "REQUEST_ADD_FRIEND Error");
                }
            } catch (Exception e) {
                checkExeption(e, result);
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if (checkTokenError(result)){return;}
            if (!handleRequestResult(result, REQUEST_REMOVE_FRIEND,
                    result.getString(RESULT_REQUESTED_ID))) return;

            ((OnRequestRemoveFriendCompleteListener) mLocalListeners.get(REQUEST_REMOVE_FRIEND))
                    .onRequestRemoveFriendComplete(getID(),
                            "" + result.getString(RESULT_REQUESTED_ID));
        }
    }
}
