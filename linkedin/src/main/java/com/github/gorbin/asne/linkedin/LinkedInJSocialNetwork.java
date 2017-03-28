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

import android.content.Context;
import android.app.Activity;
import android.content.Intent;
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
import com.google.code.linkedinapi.client.CommunicationsApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.constant.ApplicationConstants;
import com.google.code.linkedinapi.client.enumeration.ProfileField;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.google.code.linkedinapi.schema.Connections;
import com.google.code.linkedinapi.schema.Headers;
import com.google.code.linkedinapi.schema.HttpHeader;
import com.google.code.linkedinapi.schema.Person;
import com.google.code.linkedinapi.schema.Position;
import com.google.code.linkedinapi.schema.VisibilityType;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

//import javax.naming.Context;

/**
 * Class for LinkedIn social network integration using LinkedIn-j library
 *
 * @author Anton Krasov
 * @author Evgeny Gorbin (gorbin.e.o@gmail.com)
 */
public class LinkedInJSocialNetwork extends OAuthSocialNetwork {
    /*** Social network ID in asne modules, should be unique*/
    public static final int ID = 2;

    private static final String OAUTH_CALLBACK_SCHEME = "x-oauthflow-linkedin";
    private static final String OAUTH_CALLBACK_HOST = "linkedinApiTestCallback";
    private static final String OAUTH_CALLBACK_URL = String.format("%s://%s", OAUTH_CALLBACK_SCHEME, OAUTH_CALLBACK_HOST);
    private static final String OAUTH_QUERY_TOKEN = "oauth_token";
    private static final String OAUTH_QUERY_VERIFIER = "oauth_verifier";
    private static final String OAUTH_QUERY_PROBLEM = "oauth_problem";
    private static final String SAVE_STATE_KEY_OAUTH_TOKEN = "LinkedInSocialNetwork.SAVE_STATE_KEY_OAUTH_TOKEN";
    private static final String SAVE_STATE_KEY_OAUTH_SECRET = "LinkedInSocialNetwork.SAVE_STATE_KEY_OAUTH_SECRET";
    private static final EnumSet<ProfileField> PROFILE_PARAMETERS = EnumSet.of(
            ProfileField.ID,
            ProfileField.FIRST_NAME,
            ProfileField.LAST_NAME,
            ProfileField.POSITIONS,
            ProfileField.CONNECTIONS,
            ProfileField.LOCATION,
            ProfileField.SUMMARY,
            ProfileField.DATE_OF_BIRTH,
            ProfileField.SPECIALTIES,
            ProfileField.CURRENT_STATUS,
            ProfileField.INTERESTS,
            ProfileField.INDUSTRY,
            ProfileField.MAIN_ADDRESS,
            ProfileField.PHONE_NUMBERS,
            ProfileField.CURRENT_SHARE,
            ProfileField.API_STANDARD_PROFILE_REQUEST_HEADERS,
            ProfileField.PICTURE_URL,
            ProfileField.PUBLIC_PROFILE_URL
    );
    // max 16 bit to use in startActivityForResult
    private static final int REQUEST_AUTH = UUID.randomUUID().hashCode() & 0xFFFF;
    private final LinkedInOAuthService mOAuthService;
    private final LinkedInApiClientFactory mLinkedInApiClientFactory;

    private String mOAuthTokenSecret;

    //TODO: refactor to use an init that is shared by constructors
    public LinkedInJSocialNetwork(Fragment fragment, String consumerKey, String consumerSecret, String permissions) {
        super(fragment);

        if (TextUtils.isEmpty(consumerKey) || TextUtils.isEmpty(consumerSecret) || TextUtils.isEmpty(permissions)) {
            throw new IllegalArgumentException("TextUtils.isEmpty(consumerKey) || TextUtils.isEmpty(consumerSecret) || TextUtils.isEmpty(permissions)");
        }

        mOAuthService = LinkedInOAuthServiceFactory.getInstance()
                .createLinkedInOAuthService(consumerKey, consumerSecret, permissions);
        mLinkedInApiClientFactory = LinkedInApiClientFactory.newInstance(consumerKey, consumerSecret);
    }

//    public LinkedInJSocialNetwork(Fragment fragment, Context context, String consumerKey, String consumerSecret, String permissions) {
//        super(fragment, context);
//
//        if (TextUtils.isEmpty(consumerKey) || TextUtils.isEmpty(consumerSecret) || TextUtils.isEmpty(permissions)) {
//            throw new IllegalArgumentException("TextUtils.isEmpty(fConsumerKey) || TextUtils.isEmpty(fConsumerSecret) || TextUtils.isEmpty(fPermissions)");
//        }
//
//        mOAuthService = LinkedInOAuthServiceFactory.getInstance()
//                .createLinkedInOAuthService(consumerKey, consumerSecret, permissions);
//        mLinkedInApiClientFactory = LinkedInApiClientFactory.newInstance(consumerKey, consumerSecret);
//    }

    /**
     * Check is social network connected
     * @return true if connected to LinkedIn social network and false if not
     */
    @Override
    public boolean isConnected() {
        String accessToken = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null);
        String accessTokenSecret = mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_SECRET, null);
        return accessToken != null && accessTokenSecret != null;
    }

    /**
     * Make login request - authorize in LinkedIn social network
     * @param onLoginCompleteListener listener to trigger when Login complete
     */
    @Override
    public void requestLogin(OnLoginCompleteListener onLoginCompleteListener) {
        super.requestLogin(onLoginCompleteListener);
        executeRequest(new RequestLoginAsyncTask(), null, REQUEST_LOGIN);
    }

    /**
     * Logout from LinkedIn social network
     */
    @Override
    public void logout() {
        fatalError();
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
                mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_SECRET, null)
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
                        mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_SECRET, null)
                ));
    }

    /**
     * Request current user {@link com.github.gorbin.asne.core.persons.SocialPerson}
     * @param onRequestSocialPersonCompleteListener listener for {@link com.github.gorbin.asne.core.persons.SocialPerson} request
     */
    @Override
    public void requestCurrentPerson(OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener) {
        super.requestCurrentPerson(onRequestSocialPersonCompleteListener);
        executeRequest(new RequestSocialPersonAsyncTask(), null, REQUEST_GET_CURRENT_PERSON);
    }

    /**
     * Request {@link com.github.gorbin.asne.core.persons.SocialPerson} by user id
     * @param userID user id in social network
     * @param onRequestSocialPersonCompleteListener listener for request {@link com.github.gorbin.asne.core.persons.SocialPerson}
     */
    @Override
    public void requestSocialPerson(String userID, OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener) {
        super.requestSocialPerson(userID, onRequestSocialPersonCompleteListener);
        if (TextUtils.isEmpty(userID)) {
            throw new SocialNetworkException("userID can't be null or empty");
        }
        Bundle args = new Bundle();
        args.putString(RequestSocialPersonAsyncTask.PARAM_USER_ID, userID);
        executeRequest(new RequestSocialPersonAsyncTask(), args, REQUEST_GET_PERSON);
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
     * @param onRequestDetailedSocialPersonCompleteListener listener for request detailed social person
     */
    @Override
    public void requestDetailedSocialPerson(String userId, OnRequestDetailedSocialPersonCompleteListener onRequestDetailedSocialPersonCompleteListener) {
        super.requestDetailedSocialPerson(userId, onRequestDetailedSocialPersonCompleteListener);
        Bundle args = new Bundle();
        if(userId != null) {
           args.putString(RequestGetDetailedPersonAsyncTask.PARAM_USER_ID, userId);
        }
        executeRequest(new RequestGetDetailedPersonAsyncTask(), args, REQUEST_GET_DETAIL_PERSON);
    }

    private SocialPerson getSocialPerson(SocialPerson socialPerson, Person person) {
        socialPerson.id = person.getId();
        socialPerson.name = person.getFirstName() + " " + person.getLastName();
        socialPerson.avatarURL = person.getPictureUrl();
        socialPerson.profileURL = person.getPublicProfileUrl();

        return socialPerson;
    }

    private LinkedInPerson getDetailedSocialPerson(LinkedInPerson linkedinPerson, Person person) {
        getSocialPerson(linkedinPerson, person);
        List<Position> positions = person.getPositions().getPositionList();
        if (positions.size() > 0) {
            Position position = positions.get(positions.size() - 1);
            linkedinPerson.company = position.getCompany().getName();
            linkedinPerson.position =  position.getTitle();
        }
        linkedinPerson.firstName = person.getFirstName();
        linkedinPerson.lastName = person.getLastName();
        linkedinPerson.headLine = person.getHeadline();
        if(person.getLocation() != null) {
            if(person.getLocation().getPostalCode() != null) {
                linkedinPerson.countryCode = person.getLocation().getPostalCode();
            }
            if(person.getLocation().getDescription() != null) {
                linkedinPerson.locationDescription = person.getLocation().getDescription();
            }
        }
        linkedinPerson.industry =  person.getIndustry();
        linkedinPerson.summary =  person.getSummary();
        if(person.getDateOfBirth() != null) {
            linkedinPerson.birthday = person.getDateOfBirth().getDay() + "/"
                    + person.getDateOfBirth().getMonth() + "/" + person.getDateOfBirth().getYear();
        }
        linkedinPerson.mainAddress = person.getMainAddress();
        linkedinPerson.currentStatus = person.getCurrentStatus();
        linkedinPerson.interests = person.getInterests();
        linkedinPerson.specialties = person.getSpecialties();
//        if(person.getPhoneNumbers() != null) {
//            linkedinPerson.phone = person.getPhoneNumbers().getPhoneNumberList().get(0).getPhoneNumber();
//        }
        return  linkedinPerson;
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

    /**
     * Invite friend by id to current user
     * @param userID id of user that should be invited
     * @param onRequestAddFriendCompleteListener listener for invite result
     */
    @Override
    public void requestAddFriend(String userID, OnRequestAddFriendCompleteListener onRequestAddFriendCompleteListener) {
        super.requestAddFriend(userID, onRequestAddFriendCompleteListener);
        Bundle args = new Bundle();
        args.putString(RequestSendInviteAsyncTask.PARAM_USER_ID, userID);
        executeRequest(new RequestSendInviteAsyncTask(), args, REQUEST_ADD_FRIEND);
    }

    /**
     * Not supported via LinkedIn api
     * @throws com.github.gorbin.asne.core.SocialNetworkException
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

        if (resultCode != Activity.RESULT_OK || data == null || data.getData() == null) {
            if (mLocalListeners.get(REQUEST_LOGIN) != null) {
                mLocalListeners.get(REQUEST_LOGIN).onError(getID(), REQUEST_LOGIN, "Login canceled", null);
            }
            return;
        }

        Uri uri = data.getData();

        final String problem = uri.getQueryParameter(OAUTH_QUERY_PROBLEM);
        if (problem != null) {
            if (mLocalListeners.get(REQUEST_LOGIN) != null) {
                mLocalListeners.get(REQUEST_LOGIN).onError(getID(), REQUEST_LOGIN, problem, null);
            }

            return;
        }

        Bundle args = new Bundle();
        args.putString(RequestLogin2AsyncTask.PARAM_VERIFIER, uri.toString());
        args.putString(RequestLogin2AsyncTask.PARAM_AUTH_REQUEST_TOKEN, mOAuthTokenSecret);
        executeRequest(new RequestLogin2AsyncTask(), args, REQUEST_LOGIN2);
    }

    private void fatalError() {
        mSharedPreferences.edit()
                .remove(SAVE_STATE_KEY_OAUTH_TOKEN)
                .remove(SAVE_STATE_KEY_OAUTH_SECRET)
                .apply();
    }

    private class RequestLoginAsyncTask extends SocialNetworkAsyncTask {
        private static final String RESULT_URL = "RequestLoginAsyncTask.RESULT_URL";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle result = new Bundle();

            try {
                final LinkedInRequestToken liToken = mOAuthService.getOAuthRequestToken(OAUTH_CALLBACK_URL);
                mOAuthTokenSecret = liToken.getTokenSecret();
                result.putString(RESULT_URL, liToken.getAuthorizationUrl());
            } catch (Exception e) {
                result.putString(RESULT_ERROR, e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if (!handleRequestResult(result, REQUEST_LOGIN)) return;

            Intent intent = new Intent(mSocialNetworkManager.getActivity(), OAuthActivity.class)
                    .putExtra(OAuthActivity.PARAM_CALLBACK, OAUTH_CALLBACK_URL)
                    .putExtra(OAuthActivity.PARAM_URL_TO_LOAD, result.getString(RESULT_URL));

            mSocialNetworkManager.getActivity().startActivityForResult(intent, REQUEST_AUTH);
        }
    }

    private class RequestLogin2AsyncTask extends SocialNetworkAsyncTask {
        public static final String PARAM_AUTH_REQUEST_TOKEN = "PARAM_AUTH_REQUEST_TOKEN";
        public static final String PARAM_VERIFIER = "PARAM_VERIFIER";
        private static final String RESULT_TOKEN = "RESULT_TOKEN";
        private static final String RESULT_SECRET = "RESULT_SECRET";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle args = params[0];
            final String paramAuthRequestToken = args.getString(PARAM_AUTH_REQUEST_TOKEN);
            final String paramVerifier = args.getString(PARAM_VERIFIER);

            Uri uri = Uri.parse(paramVerifier);

            Bundle result = new Bundle();

            try {
                final LinkedInAccessToken accessToken = mOAuthService.getOAuthAccessToken(
                        new LinkedInRequestToken(
                                uri.getQueryParameter(OAUTH_QUERY_TOKEN),
                                paramAuthRequestToken
                        ),
                        uri.getQueryParameter(OAUTH_QUERY_VERIFIER)
                );

                result.putString(RESULT_TOKEN, accessToken.getToken());
                result.putString(RESULT_SECRET, accessToken.getTokenSecret());
            } catch (Exception e) {
                result.putString(RESULT_ERROR, e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            mRequests.remove(REQUEST_LOGIN2);
            if (!handleRequestResult(result, REQUEST_LOGIN)) return;

            // Shared Preferences
            mSharedPreferences.edit()
                    .putString(SAVE_STATE_KEY_OAUTH_TOKEN, result.getString(RESULT_TOKEN))
                    .putString(SAVE_STATE_KEY_OAUTH_SECRET, result.getString(RESULT_SECRET))
                    .apply();

            ((OnLoginCompleteListener) mLocalListeners.get(REQUEST_LOGIN)).onLoginSuccess(getID());
            mLocalListeners.remove(REQUEST_LOGIN);
        }
    }

    private class RequestSocialPersonAsyncTask extends SocialNetworkAsyncTask {
        public static final String PARAM_USER_ID = "RequestGetPersonAsyncTask.PARAM_USER_ID";
        public static final String CURRENT = "RequestGetPersonAsyncTask.CURRENT";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle result = new Bundle();
            Bundle args = params[0];
            String userID;

            try {
                LinkedInApiClient client = mLinkedInApiClientFactory.createLinkedInApiClient(
                        new LinkedInAccessToken(
                                mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null),
                                mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_SECRET, null)
                        )
                );
                Person person;
                if (args.containsKey(PARAM_USER_ID)) {
                    userID = args.getString(PARAM_USER_ID);
                    result.putBoolean(CURRENT, false);
                    Set<ProfileField> scope = EnumSet.of(ProfileField.PICTURE_URL, ProfileField.ID,
                        ProfileField.FIRST_NAME, ProfileField.LAST_NAME, ProfileField.PUBLIC_PROFILE_URL);
                    person = client.getProfileById(userID, scope);
                } else {
                    result.putBoolean(CURRENT, true);
                    person = client.getProfileForCurrentUser(PROFILE_PARAMETERS);
                }
                SocialPerson socialPerson = new SocialPerson();
                getSocialPerson(socialPerson, person);
                result.putParcelable(REQUEST_GET_PERSON, socialPerson);
				
            } catch (Exception e) {
                result.putString(RESULT_ERROR, e.getMessage());
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
		public static final String PARAM_USER_ID = "PARAM_USER_ID";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle result = new Bundle();
            Bundle args = params[0];
            String userID;
			
            try {
                LinkedInApiClient client = mLinkedInApiClientFactory.createLinkedInApiClient(
                        new LinkedInAccessToken(
                                mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null),
                                mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_SECRET, null)
                        )
                );
                Person person;
                if (args.containsKey(PARAM_USER_ID)) {
                    userID = args.getString(PARAM_USER_ID);

                    person = client.getProfileById(userID, EnumSet.allOf(ProfileField.class));
                } else {
                    person = client.getProfileForCurrentUser(PROFILE_PARAMETERS);
                }
                LinkedInPerson linkedInPerson = new LinkedInPerson();
                getDetailedSocialPerson(linkedInPerson, person);
                result.putParcelable(REQUEST_GET_DETAIL_PERSON, linkedInPerson);
            } catch (Exception e) {
                result.putString(RESULT_ERROR, e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
			if (!handleRequestResult(result, REQUEST_GET_DETAIL_PERSON)) return;
			LinkedInPerson linkedInPerson = result.getParcelable(REQUEST_GET_DETAIL_PERSON);
			((OnRequestDetailedSocialPersonCompleteListener) mLocalListeners.get(REQUEST_GET_DETAIL_PERSON))
    			.onRequestDetailedSocialPersonSuccess(getID(), linkedInPerson);
            mLocalListeners.remove(REQUEST_GET_DETAIL_PERSON);
        }
    }

    private class RequestPostMessageAsyncTask extends SocialNetworkAsyncTask {
        private static final String PARAM_MESSAGE = "PARAM_MESSAGE";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            String message = params[0].getString(PARAM_MESSAGE);

            Bundle result = new Bundle();

            try {
                LinkedInApiClient apiClient = mLinkedInApiClientFactory.createLinkedInApiClient(
                        mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, ""),
                        mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_SECRET, "")
                );

                apiClient.updateCurrentStatus(message);
            } catch (Exception e) {
                result.putString(RESULT_ERROR, e.getMessage());
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
            String commentText = params[0].getString(PARAM_MESSAGE);
            String url = params[0].getString(BUNDLE_LINK);
            String title = params[0].getString(BUNDLE_NAME);
            String description = params[0].getString(BUNDLE_MESSAGE);
            String imageUrl = params[0].getString(BUNDLE_PICTURE);
            VisibilityType visibilityType = VisibilityType.ANYONE;
            Bundle result = new Bundle();

            try {
                LinkedInApiClient apiClient = mLinkedInApiClientFactory.createLinkedInApiClient(
                        mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, ""),
                        mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_SECRET, "")
                );

                apiClient.postShare(commentText, title, description, url, imageUrl, visibilityType);
            } catch (Exception e) {
                result.putString(RESULT_ERROR, e.getMessage());
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

            try {
                LinkedInApiClient apiClient = mLinkedInApiClientFactory.createLinkedInApiClient(
                        mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, ""),
                        mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_SECRET, "")
                );

                Person person = apiClient.getProfileForCurrentUser(PROFILE_PARAMETERS);

                List<Person> list = person.getConnections().getPersonList();
                if (list != null) {
                    for (Person p : list) {
                        if (p.getId().equals(userID)) {
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

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle result = new Bundle();
            try {
                LinkedInApiClient apiClient = mLinkedInApiClientFactory.createLinkedInApiClient(
                        mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, ""),
                        mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_SECRET, "")
                );
                ArrayList<String> friendIds = new ArrayList<String>();
                ArrayList<SocialPerson> socialPersons = new ArrayList<SocialPerson>();
                SocialPerson socialPerson = new SocialPerson();
                Connections connections = apiClient.getConnectionsForCurrentUser();
                if(connections.getPersonList() != null)
                    for (Person person : connections.getPersonList()) {
                        if(person.getId() != null) {
                            friendIds.add(person.getId());
                        }
                        getSocialPerson(socialPerson, person);
                        socialPersons.add(socialPerson);
                        socialPerson = new SocialPerson();
                    }
                result.putStringArray(RESULT_GET_FRIENDS_ID, friendIds.toArray(new String[friendIds.size()]));
                result.putParcelableArrayList(RESULT_GET_FRIENDS, socialPersons);
            } catch (Exception e) {
                result.putString(RESULT_ERROR, e.getMessage());
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
            mLocalListeners.remove(RESULT_GET_FRIENDS);
        }
    }

    private class RequestSendInviteAsyncTask extends SocialNetworkAsyncTask {
        public static final String PARAM_USER_ID = "PARAM_USER_ID";
        private static final String RESULT_REQUESTED_ID = "RESULT_REQUESTED_ID";

        @Override
        protected Bundle doInBackground(Bundle... params) {
            String userID = params[0].getString(PARAM_USER_ID);

            Bundle result = new Bundle();

            try {
                result.putString(RESULT_REQUESTED_ID, userID);

                LinkedInApiClient apiClient = mLinkedInApiClientFactory.createLinkedInApiClient(
                        mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, ""),
                        mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_SECRET, "")
                );

                Set<ProfileField> fields = new HashSet<ProfileField>();
                fields.add(ProfileField.API_STANDARD_PROFILE_REQUEST);

                Person person = apiClient.getProfileById(userID, fields);

                String authHeader = "";
                Headers headers = person.getApiStandardProfileRequest().getHeaders();
                List<HttpHeader> httpHeaders = headers.getHttpHeaderList();
                for (HttpHeader httpHeader : httpHeaders) {
                    if (httpHeader.getName().equals(ApplicationConstants.AUTH_HEADER_NAME)) {
                        authHeader = httpHeader.getValue();
                        break;
                    }
                }

                CommunicationsApiClient communicationsApiClient = mLinkedInApiClientFactory.createCommunicationsApiClient(
                        mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, ""),
                        mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_SECRET, "")
                );
                communicationsApiClient.sendInviteById(userID, "Join my network on LinkedIn",
                        "Since you are a person I trust, I wanted to invite you to join my network on LinkedIn.", authHeader);
            } catch (Exception e) {
                result.putString(RESULT_ERROR, e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if (!handleRequestResult(result, REQUEST_ADD_FRIEND,
                    result.getString(RESULT_REQUESTED_ID))) return;

            ((OnRequestAddFriendCompleteListener) mLocalListeners.get(REQUEST_ADD_FRIEND))
                    .onRequestAddFriendComplete(
                            getID(),
                            result.getString(RESULT_REQUESTED_ID)
                    );
            mLocalListeners.remove(REQUEST_CHECK_IS_FRIEND);
        }
    }
}
