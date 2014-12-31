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
package com.github.gorbin.asne.linkedin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.DateUtils;

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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

/**
 * Class for LinkedIn social network integration using OAuth2
 *
 * @author Evgeny Gorbin (gorbin.e.o@gmail.com)
 */
public class LinkedInSocialNetwork extends OAuthSocialNetwork {
    /*** Social network ID in asne modules, should be unique*/
    public static final int ID = 2;
    private static final int REQUEST_AUTH = UUID.randomUUID().hashCode() & 0xFFFF;
    private static final String SAVE_STATE_KEY_OAUTH_TOKEN = "LinkedInSocialNetwork.SAVE_STATE_KEY_OAUTH_TOKEN";
    private static final String SAVE_STATE_KEY_OAUTH_REQUEST_TOKEN = "LinkedInSocialNetwork.SAVE_STATE_KEY_OAUTH_SECRET";
    private static final String SAVE_STATE_KEY_EXPIRES_DATE = "LinkedInSocialNetwork.SAVE_STATE_KEY_EXPIRES_DATE";
//    private final String LINKEDIN_OAUTH2_CALLBACK_URL = "https://asne";
    private final String mAuthURLString;
    private String LINKEDIN_API = "https://www.linkedin.com/uas/oauth2/authorization?response_type=code";
    private String LINKEDIN_TOKEN = "https://www.linkedin.com/uas/oauth2/accessToken?grant_type=authorization_code";
    private String LINKEDIN_V1_API = "https://api.linkedin.com/v1";
    private String FORMAT_JSON = "&format=json";
    private String SHARE = "<share>{0}<visibility><code>anyone</code></visibility></share>";
    private String COMMENT = "<comment>{0}</comment>";
    private String CONTENT = "<content><title>{0}</title><description>{1}</description>" +
            "<submitted-url>{2}</submitted-url><submitted-image-url>{3}</submitted-image-url></content>";
    private String mConsumerKey;
    private String mConsumerSecret;
    private String mRedirectURL;

    public LinkedInSocialNetwork(Fragment fragment, String consumerKey, String consumerSecret, String redirectURL, String permissions) {
        super(fragment);
        if (TextUtils.isEmpty(consumerKey) || TextUtils.isEmpty(consumerSecret) || TextUtils.isEmpty(permissions)) {
            throw new IllegalArgumentException("TextUtils.isEmpty(ConsumerKey) || TextUtils.isEmpty(ConsumerSecret) || TextUtils.isEmpty(Permissions)");
        }
        this.mConsumerKey = consumerKey;
        this.mConsumerSecret = consumerSecret;
        this.mRedirectURL = redirectURL;
        this.mAuthURLString = LINKEDIN_API + "&client_id=" + consumerKey + "&scope=" + permissions +
                         "&state=" + REQUEST_AUTH + "&redirect_uri=" + redirectURL;
    }
    /**
     * Check is social network connected
     * @return true if connected to LinkedIn social network and false if not
     */
    @Override
    public boolean isConnected() {
        String accessToken = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null);
        String requestToken = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_REQUEST_TOKEN, null);
        long expiresDate = mSharedPreferences.getLong(SAVE_STATE_KEY_EXPIRES_DATE, 0);
        boolean notExpired = expiresDate > 0 && Calendar.getInstance().getTimeInMillis() - DateUtils.DAY_IN_MILLIS < expiresDate;
        return accessToken != null && requestToken != null && notExpired;
    }

    /**
     * Make login request - authorize in LinkedIn social network
     * @param onLoginCompleteListener listener to trigger when Login complete
     */
    @Override
    public void requestLogin(OnLoginCompleteListener onLoginCompleteListener) {
        super.requestLogin(onLoginCompleteListener);
        Intent intent = new Intent(mSocialNetworkManager.getActivity(), OAuthActivity.class)
                .putExtra(OAuthActivity.PARAM_CALLBACK, mRedirectURL)
                .putExtra(OAuthActivity.PARAM_URL_TO_LOAD, mAuthURLString);
        mSocialNetworkManager.getActivity().startActivityForResult(intent, REQUEST_AUTH);
    }

    /**
     * Logout from LinkedIn social network
     */
    @Override
    public void logout() {
        mSharedPreferences.edit()
                .remove(SAVE_STATE_KEY_OAUTH_TOKEN)
                .remove(SAVE_STATE_KEY_OAUTH_REQUEST_TOKEN)
                .remove(SAVE_STATE_KEY_EXPIRES_DATE)
                .apply();
    }

    /**
     * Get id of LinkedIn social network
     * @return Social network id for LinkedIn = 2
     */
    @Override
    public int getID() {
        return ID;
    }

    /**
     * Method to get AccessToken of LinkedIn social network
     * @return {@link com.github.gorbin.asne.core.AccessToken}
     */
    @Override
    public AccessToken getAccessToken() {
        return new AccessToken(
                mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null),
                null
        );
    }

    /**
     * Request {@link com.github.gorbin.asne.core.AccessToken} of LinkedIn social network that you can get from onRequestAccessTokenCompleteListener
     * @param onRequestAccessTokenCompleteListener listener for {@link com.github.gorbin.asne.core.AccessToken} request
     */
    @Override
    public void requestAccessToken(OnRequestAccessTokenCompleteListener onRequestAccessTokenCompleteListener) {
        super.requestAccessToken(onRequestAccessTokenCompleteListener);
        ((OnRequestAccessTokenCompleteListener) mLocalListeners.get(REQUEST_ACCESS_TOKEN))
                .onRequestAccessTokenComplete(getID(), new AccessToken(
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
     * @param userID id of LinkedIn user
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
     * Not supported via LinkedIn api
     * @throws com.github.gorbin.asne.core.SocialNetworkException
     * @param userID array of user ids in social network
     * @param onRequestSocialPersonsCompleteListener listener for request ArrayList of {@link com.github.gorbin.asne.core.persons.SocialPerson}
     */
    @Override
    public void requestSocialPersons(String[] userID, OnRequestSocialPersonsCompleteListener onRequestSocialPersonsCompleteListener) {
        throw new SocialNetworkException("requestSocialPersons isn't allowed for LinkedInSocialNetwork");
    }

    /**
     * Request user {@link com.github.gorbin.asne.linkedin.LinkedInPerson} by userId - detailed user data
     * @param userId id of LinkedIn user
     * @param onRequestDetailedSocialPersonCompleteListener listener for {@link com.github.gorbin.asne.linkedin.LinkedInPerson} request
     */
    @Override
    public void requestDetailedSocialPerson(String userId, OnRequestDetailedSocialPersonCompleteListener onRequestDetailedSocialPersonCompleteListener) {
        super.requestDetailedSocialPerson(userId, onRequestDetailedSocialPersonCompleteListener);
        Bundle args = new Bundle();
        if(userId != null) {
            args.putString(RequestGetDetailedPersonAsyncTask.PARAM_USER_ID, userId);
        } else {
            args.putString(RequestGetDetailedPersonAsyncTask.PARAM_USER_ID, "~");
        }
        executeRequest(new RequestGetDetailedPersonAsyncTask(), args, REQUEST_GET_DETAIL_PERSON);
    }

    private SocialPerson getSocialPerson(SocialPerson socialPerson, JSONObject jsonResponse) throws JSONException {
        String firstName = null;
        String lastName = null;
        if(jsonResponse.has("id")) {
            socialPerson.id = jsonResponse.getString("id");
        }
        if(jsonResponse.has("firstName")) {
            firstName = jsonResponse.getString("firstName");
        }
        if(jsonResponse.has("lastName")) {
            lastName = jsonResponse.getString("lastName");
        }
        socialPerson.name =  firstName + " " + lastName;
        if(jsonResponse.has("pictureUrl")) {
            socialPerson.avatarURL = jsonResponse.getString("pictureUrl");
        }
        if(jsonResponse.has("publicProfileUrl")) {
            socialPerson.profileURL = jsonResponse.getString("publicProfileUrl");
        }
        if(jsonResponse.has("emailAddress")) {
            socialPerson.email = jsonResponse.getString("emailAddress");
        }
        return socialPerson;
    }

    private LinkedInPerson getDetailedSocialPerson(LinkedInPerson linkedinPerson, JSONObject jsonResponse) throws JSONException {
        getSocialPerson(linkedinPerson, jsonResponse);
        if(jsonResponse.has("firstName")) {
            linkedinPerson.firstName = jsonResponse.getString("firstName");
        }
        if(jsonResponse.has("lastName")) {
            linkedinPerson.lastName = jsonResponse.getString("lastName");
        }
        if(jsonResponse.has("positions")) {
            if(jsonResponse.getJSONObject("positions").has("values")){
                if(jsonResponse.getJSONObject("positions").getJSONArray("values").getJSONObject(0).has("title")) {
                    linkedinPerson.position = jsonResponse.getJSONObject("positions").getJSONArray("values").getJSONObject(0).getString("title");
                }
                if(jsonResponse.getJSONObject("positions").getJSONArray("values").getJSONObject(0).has("company")) {
                    linkedinPerson.company = jsonResponse.getJSONObject("positions").getJSONArray("values").getJSONObject(0).getJSONObject("company").getString("name");
                }
            }
        }
        if(jsonResponse.has("headline")) {
            linkedinPerson.headLine = jsonResponse.getString("headline");
        }
        if(jsonResponse.has("location")) {
            if(jsonResponse.getJSONObject("location").has("name")){
                linkedinPerson.locationDescription = jsonResponse.getJSONObject("location").getString("name");
            }
            if(jsonResponse.getJSONObject("location").has("country")){
                linkedinPerson.countryCode = jsonResponse.getJSONObject("location").getJSONObject("country").getString("code");
            }
        }
        if(jsonResponse.has("industry")) {
            linkedinPerson.industry = jsonResponse.getString("industry");
        }
        if(jsonResponse.has("summary")) {
            linkedinPerson.summary = jsonResponse.getString("summary");
        }
        String birthday = null;
        if(jsonResponse.has("dateOfBirth")) {
            if(jsonResponse.getJSONObject("dateOfBirth").has("day")){
                birthday = jsonResponse.getJSONObject("dateOfBirth").getString("day");
            }
            if(jsonResponse.getJSONObject("dateOfBirth").has("month")){
                birthday = birthday + "/" + jsonResponse.getJSONObject("dateOfBirth").getString("month");
            }
            if(jsonResponse.getJSONObject("dateOfBirth").has("year")){
                birthday = birthday + "/" + jsonResponse.getJSONObject("dateOfBirth").getString("year");
            }
            linkedinPerson.birthday = birthday;
        }
        if(jsonResponse.has("mainAddress")) {
            linkedinPerson.mainAddress = jsonResponse.getString("mainAddress");
        }
        if(jsonResponse.has("currentShare")) {
            if(jsonResponse.getJSONObject("currentShare").has("content")){
                if(jsonResponse.getJSONObject("currentShare").getJSONObject("content").has("description")){
                    linkedinPerson.currentStatus = jsonResponse.getJSONObject("currentShare").getJSONObject("content").getString("description");
                }
            }
        }
        if(jsonResponse.has("interests")) {
            linkedinPerson.interests = jsonResponse.getString("interests");
        }
        if(jsonResponse.has("specialties")) {
            linkedinPerson.specialties = jsonResponse.getString("specialties");
        }
        if(jsonResponse.has("phoneNumbers")) {
            if(jsonResponse.getJSONObject("phoneNumbers").has("values")){
                if(jsonResponse.getJSONObject("phoneNumbers").getJSONArray("values").getJSONObject(0).has("phoneNumber")){
                    linkedinPerson.phone = jsonResponse.getJSONObject("phoneNumbers").getJSONArray("values").getJSONObject(0).getString("phoneNumber");
                }
            }
        }
        return linkedinPerson;
    }

    /**
     * Post message to social network
     * @param message  message that should be shared
     * @param onPostingCompleteListener listener for posting request
     */
    @Override
    public void requestPostMessage(String message, OnPostingCompleteListener onPostingCompleteListener) {
        super.requestPostMessage(message, onPostingCompleteListener);
        Bundle args = new Bundle();
        args.putString(RequestPostMessageAsyncTask.PARAM_MESSAGE, message);
        executeRequest(new RequestPostMessageAsyncTask(), args, REQUEST_POST_MESSAGE);
    }

    /**
     * Not supported via LinkedIn api
     * @throws com.github.gorbin.asne.core.SocialNetworkException
     * @param photo photo that should be shared
     * @param message message that should be shared with photo
     * @param onPostingCompleteListener listener for posting request
     */
    @Override
    public void requestPostPhoto(File photo, String message, OnPostingCompleteListener onPostingCompleteListener) {
        throw new SocialNetworkException("requestPostPhoto isn't allowed for LinkedInSocialNetwork");
    }

    /**
     * Post link with comment to social network
     * @param bundle bundle containing information that should be shared(Bundle constants in {@link com.github.gorbin.asne.core.SocialNetwork})
     * @param message message that should be shared with bundle
     * @param onPostingCompleteListener listener for posting request
     */
    @Override
    public void requestPostLink(Bundle bundle, String message, OnPostingCompleteListener onPostingCompleteListener) {
        super.requestPostLink(bundle, message, onPostingCompleteListener);
        bundle.putString(RequestPostLinkAsyncTask.PARAM_MESSAGE, message);
        executeRequest(new RequestPostLinkAsyncTask(), bundle, REQUEST_POST_LINK);
    }

    /**
     * Not supported via LinkedIn api
     * @throws com.github.gorbin.asne.core.SocialNetworkException
     * @param bundle bundle containing information that should be shared(Bundle constants in {@link com.github.gorbin.asne.core.SocialNetwork})
     * @param onPostingCompleteListener listener for posting request
     */
    @Override
    public void requestPostDialog(Bundle bundle, OnPostingCompleteListener onPostingCompleteListener) {
        throw new SocialNetworkException("requestPostDialog isn't allowed for LinkedInSocialNetwork");
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

    private void getAllFriends(String urlString, final ArrayList<SocialPerson> socialPersons, final ArrayList<String> ids, String token) throws Exception {
        URL url = new URL(urlString);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        checkConnectionErrors(connection);
        InputStream inputStream = connection.getInputStream();
        String response = streamToString(inputStream);
        JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();

        int jsonStart = 0, jsonCount = 0, jsonTotal = 0;
        String nextToken = null;
        if(jsonObject.has("_start")) {
            jsonStart = jsonObject.getInt("_start");
        }
        if(jsonObject.has("_count")) {
            jsonCount = jsonObject.getInt("_count");
        }
        if(jsonObject.has("_total")) {
            jsonTotal = jsonObject.getInt("_total");
        }
        int start = jsonStart + jsonCount;
        if(jsonTotal > 0 && start > 0 && jsonCount > 0 && jsonTotal > start) {
            nextToken = LINKEDIN_V1_API + "/people/~/connections"+ RequestGetFriendsAsyncTask.fields
                    + "?oauth2_access_token=" + token + FORMAT_JSON + "&start=" + start
                    + "&count=" + RequestGetFriendsAsyncTask.count;
        }
        JSONArray jsonResponse = jsonObject.getJSONArray("values");
        int length = jsonResponse.length();
        for (int i = 0; i < length; i++) {
            SocialPerson socialPerson = new SocialPerson();
            getSocialPerson(socialPerson, jsonResponse.getJSONObject(i));
            socialPersons.add(socialPerson);
            ids.add(jsonResponse.getJSONObject(i).getString("id"));
        }

        if((nextToken != null) && (!TextUtils.isEmpty(nextToken))){
            getAllFriends(nextToken, socialPersons, ids, token);
        }
    }

    /**
     * Not supported via LinkedIn api
     * @param userID id of user that should be invited
     * @param onRequestAddFriendCompleteListener listener for invite result
     */
    @Override
    public void requestAddFriend(String userID, OnRequestAddFriendCompleteListener onRequestAddFriendCompleteListener) {
        throw new SocialNetworkException("requestAddFriend isn't allowed for LinkedInSocialNetwork");
    }

    /**
     * Not supported via LinkedIn api
     * @param userID user id that should be removed from friends
     * @param onRequestRemoveFriendCompleteListener listener to remove friend request response
     */
    @Override
    public void requestRemoveFriend(String userID, OnRequestRemoveFriendCompleteListener onRequestRemoveFriendCompleteListener) {
        throw new SocialNetworkException("requestRemoveFriend isn't allowed for LinkedInSocialNetwork");
    }

    /**
     * Overrided for LinkedIn support
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int sanitizedRequestCode = requestCode & 0xFFFF;
        if (sanitizedRequestCode != REQUEST_AUTH) return;
        super.onActivityResult(requestCode, resultCode, data);

        Uri uri = data != null ? data.getData() : null;

        if (uri != null && uri.toString().startsWith(mRedirectURL.toLowerCase())) {
            String parts[] = uri.toString().split("=");
            String verifier = parts[1];
            verifier = verifier.substring(0, verifier.indexOf("&"));
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

    private String streamToString(InputStream is) {
        try {
            StringBuilder outString = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String read = reader.readLine();
            while (read != null) {
                outString.append(read);
                read = reader.readLine();
            }
            return outString.toString();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private String checkInputStream(HttpURLConnection connection){
        String code = null, errorMessage = null;
        InputStream inputStream = connection.getErrorStream();
        String response = streamToString(inputStream);
        try {
            JSONObject jsonResponse = (JSONObject) new JSONTokener(response).nextValue();
            if(jsonResponse.has("status")) {
                code = jsonResponse.getString("status");
            }
            if(jsonResponse.has("message")) {
                errorMessage = jsonResponse.getString("message");
            }
            return "ERROR CODE: " + code + " ERROR MESSAGE: " + errorMessage;
        } catch (JSONException e) {
            return e.getMessage();
        }
    }

    private void checkConnectionErrors(HttpsURLConnection connection) throws Exception {
        int responseCode;
        try {
            responseCode = connection.getResponseCode();
        } catch (IOException e) {
            responseCode = connection.getResponseCode();
        }
        if(responseCode >= 400){
            if(responseCode == 401) {
                mSharedPreferences.edit()
                        .remove(SAVE_STATE_KEY_OAUTH_TOKEN)
                        .remove(SAVE_STATE_KEY_OAUTH_REQUEST_TOKEN)
                        .remove(SAVE_STATE_KEY_EXPIRES_DATE)
                        .apply();
            }
            throw new Exception(checkInputStream(connection));
        }
    }

    private void checkException(Exception e, Bundle result){
        result.putString(SocialNetworkAsyncTask.RESULT_ERROR, e.getMessage());
    }

    private class RequestLogin2AsyncTask extends SocialNetworkAsyncTask {
        public static final String PARAM_VERIFIER = "Login2AsyncTask.PARAM_VERIFIER";

        private static final String RESULT_ACCESS_TOKEN = "Login2AsyncTask.RESULT_TOKEN";
        private static final String RESULT_REQUEST_TOKEN = "Login2AsyncTask.RESULT_SECRET";
        private static final String RESULT_EXPIRES_DATE = "Login2AsyncTask.RESULT_EXPIRES_DATE";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            String verifier = params[0].getString(PARAM_VERIFIER);

            Bundle result = new Bundle();
            HttpsURLConnection httpsURLConnection = null;
            try
            {
                String tokenURLString = LINKEDIN_TOKEN + "&code=" + verifier + "&redirect_uri=" + mRedirectURL +
                        "&client_id=" + mConsumerKey + "&client_secret=" + mConsumerSecret;

                URL url = new URL(tokenURLString);
                httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setRequestMethod("POST");

                String response = streamToString(httpsURLConnection.getInputStream());
                JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();

                String accessToken = jsonObject.getString("access_token");
                int expiresIn = jsonObject.getInt("expires_in");
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.SECOND, expiresIn);
                long expireDate = calendar.getTimeInMillis();
                result.putString(RESULT_ACCESS_TOKEN, accessToken);
                result.putString(RESULT_REQUEST_TOKEN, verifier);
                result.putLong(RESULT_EXPIRES_DATE, expireDate);

            } catch (Exception e) {
                String error;
                if(e == null){
                    InputStream inputStream = httpsURLConnection.getErrorStream();
                    error = streamToString(inputStream);
                } else {
                    error = e.getMessage();
                }
                result.putString(RESULT_ERROR, error);
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if (!handleRequestResult(result, REQUEST_LOGIN)){//&&!checkRequests()) {
                return;
            }

            mSharedPreferences.edit()
                    .putString(SAVE_STATE_KEY_OAUTH_TOKEN, result.getString(RESULT_ACCESS_TOKEN))
                    .putString(SAVE_STATE_KEY_OAUTH_REQUEST_TOKEN, result.getString(RESULT_REQUEST_TOKEN))
                    .putLong(SAVE_STATE_KEY_EXPIRES_DATE, result.getLong(RESULT_EXPIRES_DATE))
                    .apply();
            mRequests.remove(REQUEST_LOGIN2);
            if (mLocalListeners.get(REQUEST_LOGIN) != null){// && !restart) {
                ((OnLoginCompleteListener) mLocalListeners.get(REQUEST_LOGIN)).onLoginSuccess(getID());
            }
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
            String fields = ":(id,first-name,last-name,picture-url,email-address,public-profile-url)";
            String token = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null);
            if (args.containsKey(PARAM_USER_ID)) {
                userID = args.getString(PARAM_USER_ID);
                result.putBoolean(CURRENT, false);
            } else {
                userID = "~";
                result.putBoolean(CURRENT, true);
            }
            String urlString = LINKEDIN_V1_API + "/people/"+ userID + fields
                    +"?oauth2_access_token=" + token + FORMAT_JSON;
            try {
                URL url = new URL(urlString);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setInstanceFollowRedirects(true);
                checkConnectionErrors(connection);

                InputStream inputStream = connection.getInputStream();

                String response = streamToString(inputStream);
                JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();

                SocialPerson socialPerson = new SocialPerson();
                getSocialPerson(socialPerson, jsonObject);
                result.putParcelable(REQUEST_GET_PERSON, socialPerson);
            } catch (Exception e) {
                checkException(e, result);
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
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
            String fields = ":(id,first-name,last-name,headline,industry,date-of-birth,summary," +
                    "picture-url,email-address,public-profile-url,positions,location,main-address," +
                    "current-share,interests,specialties,phone-numbers,skills)";

            String urlString = LINKEDIN_V1_API + "/people/"+ userID + fields
                    +"?oauth2_access_token=" + token + FORMAT_JSON;
            try {
                URL url = new URL(urlString);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                checkConnectionErrors(connection);

                InputStream inputStream = connection.getInputStream();
                String response = streamToString(inputStream);
                JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();

                LinkedInPerson linkedinPerson = new LinkedInPerson();
                getDetailedSocialPerson(linkedinPerson, jsonObject);
                result.putParcelable(REQUEST_GET_DETAIL_PERSON, linkedinPerson);
            } catch (Exception e) {
                checkException(e, result);
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if (!handleRequestResult(result, REQUEST_GET_DETAIL_PERSON)) return;
            LinkedInPerson linkedinPerson = result.getParcelable(REQUEST_GET_DETAIL_PERSON);
            ((OnRequestDetailedSocialPersonCompleteListener) mLocalListeners.get(REQUEST_GET_DETAIL_PERSON))
                    .onRequestDetailedSocialPersonSuccess(getID(), linkedinPerson);
        }
    }


    private class RequestPostMessageAsyncTask extends SocialNetworkAsyncTask {
        private static final String PARAM_MESSAGE = "PARAM_MESSAGE";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            String message = params[0].getString(PARAM_MESSAGE);
            Bundle result = new Bundle();
            String token = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null);
            String urlString = LINKEDIN_V1_API + "/people/~/shares?oauth2_access_token=" + token;
            String xml;
            if(message != null && message.length()>0) {
                xml = MessageFormat.format(SHARE, MessageFormat.format(COMMENT, message));
            } else {
                result.putString(RESULT_ERROR, "Message can't be null");
                return result;
            }

            try {
                URL url = new URL(urlString);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setInstanceFollowRedirects(true);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/xml");
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
                outputStreamWriter.write(xml);
                outputStreamWriter.flush();
                checkConnectionErrors(connection);
            } catch (Exception e) {
                checkException(e, result);
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if (!handleRequestResult(result, REQUEST_POST_MESSAGE)) return;

            ((OnPostingCompleteListener) mLocalListeners.get(REQUEST_POST_MESSAGE))
                    .onPostSuccessfully(getID());
        }
    }

    private class RequestPostLinkAsyncTask extends SocialNetworkAsyncTask {
        private static final String PARAM_MESSAGE = "PARAM_MESSAGE";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            String message = params[0].getString(PARAM_MESSAGE);
            if(message == null) {message = "";}
            String link = params[0].getString(BUNDLE_LINK);
            String title = params[0].getString(BUNDLE_NAME);
            String description = params[0].getString(BUNDLE_MESSAGE);
            if(description == null) {description = "";}
            String imageUrl = params[0].getString(BUNDLE_PICTURE);
            if(imageUrl == null) {imageUrl = "";}
            Bundle result = new Bundle();

            String token = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null);
            String urlString = LINKEDIN_V1_API + "/people/~/shares?oauth2_access_token=" + token;
            String comment = MessageFormat.format(COMMENT, message);
            String content;
            if(link != null && link.length()>0 && title != null && title.length()>0) {
                content = MessageFormat.format(CONTENT, title, description, link, imageUrl);
            } else {
                result.putString(RESULT_ERROR, "Message can't be null");
                return result;
            }
            String xml =  MessageFormat.format(SHARE, comment + content);

            try {
                URL url = new URL(urlString);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setInstanceFollowRedirects(true);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/xml");
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
                outputStreamWriter.write(xml);
                outputStreamWriter.flush();
                checkConnectionErrors(connection);
            } catch (Exception e) {
                checkException(e, result);
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if (!handleRequestResult(result, REQUEST_POST_LINK)) return;

            ((OnPostingCompleteListener) mLocalListeners.get(REQUEST_POST_LINK))
                    .onPostSuccessfully(getID());
        }
    }

    private class RequestCheckIsFriendAsyncTask extends SocialNetworkAsyncTask {
        public static final String PARAM_USER_ID = "PARAM_USER_ID";
        private static final String RESULT_IS_FRIEND = "RESULT_IS_FRIEND";
        private static final String RESULT_REQUESTED_ID = "RESULT_REQUESTED_ID";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            String userID = params[0].getString(PARAM_USER_ID);

            Bundle result = new Bundle();
            result.putString(RESULT_REQUESTED_ID, userID);

            String token = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null);
            String urlString = LINKEDIN_V1_API + "/people/~/connections"+ RequestGetFriendsAsyncTask.fields
                    +"?oauth2_access_token=" + token + FORMAT_JSON+ "&start=0"
                    + "&count=" + RequestGetFriendsAsyncTask.count;
            ArrayList<SocialPerson> socialPersons = new ArrayList<SocialPerson>();
            ArrayList<String> ids = new ArrayList<String>();
            try {
                getAllFriends(urlString, socialPersons, ids, token);

                if (socialPersons != null && socialPersons.size() > 0) {
                    for (SocialPerson socialPerson : socialPersons) {
                        if (socialPerson.id.equals(userID)) {
                            result.putBoolean(RESULT_IS_FRIEND, true);
                            return result;
                        }
                    }
                }

                result.putBoolean(RESULT_IS_FRIEND, false);
            } catch (Exception e) {
                result.putString(RESULT_ERROR, e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if (!handleRequestResult(result, REQUEST_CHECK_IS_FRIEND,
                    result.getString(RESULT_REQUESTED_ID))) return;

            ((OnCheckIsFriendCompleteListener) mLocalListeners.get(REQUEST_CHECK_IS_FRIEND))
                    .onCheckIsFriendComplete(
                            getID(),
                            result.getString(RESULT_REQUESTED_ID),
                            result.getBoolean(RESULT_IS_FRIEND)
                    );
            mLocalListeners.remove(REQUEST_CHECK_IS_FRIEND);
        }
    }

    private class RequestGetFriendsAsyncTask extends SocialNetworkAsyncTask {
        public static final String RESULT_GET_FRIENDS = "RESULT_GET_FRIENDS";
        public static final String RESULT_GET_FRIENDS_ID = "RESULT_GET_FRIENDS_ID";
        private static final String fields = ":(id,first-name,last-name,picture-url,email-address,public-profile-url)";
        private static final int count = 500;

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle result = new Bundle();
            String token = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null);
            String urlString = LINKEDIN_V1_API + "/people/~/connections"+ fields
                    +"?oauth2_access_token=" + token + FORMAT_JSON+ "&start=0"
                    + "&count=" + count;
            ArrayList<SocialPerson> socialPersons = new ArrayList<SocialPerson>();
            ArrayList<String> ids = new ArrayList<String>();
            try {
                getAllFriends(urlString, socialPersons, ids, token);
                result.putStringArray(RESULT_GET_FRIENDS_ID, ids.toArray(new String[ids.size()]));
                result.putParcelableArrayList(RESULT_GET_FRIENDS, socialPersons);
            } catch (Exception e) {
                checkException(e, result);
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if (!handleRequestResult(result, REQUEST_GET_FRIENDS,
                    result.getStringArray(RESULT_GET_FRIENDS_ID))) return;

            ((OnRequestGetFriendsCompleteListener) mLocalListeners.get(REQUEST_GET_FRIENDS))
                    .onGetFriendsIdComplete(getID(), result.getStringArray(RESULT_GET_FRIENDS_ID));
            ArrayList<SocialPerson> socialPersons = result.getParcelableArrayList(RESULT_GET_FRIENDS);
            ((OnRequestGetFriendsCompleteListener) mLocalListeners.get(REQUEST_GET_FRIENDS))
                    .onGetFriendsComplete(getID(), socialPersons);
        }
    }

}
