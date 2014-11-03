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
package com.github.gorbin.asne.googleplus;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;

import com.github.gorbin.asne.core.AccessToken;
import com.github.gorbin.asne.core.SocialNetwork;
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
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusShare;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class for Google plus social network integration
 *
 * @author Anton Krasov
 * @author Evgeny Gorbin (gorbin.e.o@gmail.com)
 */
public class GooglePlusSocialNetwork extends SocialNetwork implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    /*** Social network ID in asne modules, should be unique*/
    public static final int ID = 3;

    private static final String TAG = GooglePlusSocialNetwork.class.getSimpleName();
    // max 16 bit to use in startActivityForResult
    private static final int REQUEST_AUTH = UUID.randomUUID().hashCode() & 0xFFFF;
    /**
     * googleApiClient.isConntected() works really strange, it returs false right after init and then true,
     * so let's handle state by ourselves
     */
    private static final String SAVE_STATE_KEY_IS_CONNECTED = "GooglePlusSocialNetwork.SAVE_STATE_KEY_OAUTH_TOKEN";
    private static Activity mActivity;
    private GoogleApiClient googleApiClient;
    private ConnectionResult mConnectionResult;
    private boolean mConnectRequested;
    private Handler mHandler = new Handler();

    public GooglePlusSocialNetwork(Fragment fragment) {
        super(fragment);
    }

    /**
     * Check is social network connected
     * @return true if connected to Google Plus social network and false if not
     */
    @Override
    public boolean isConnected() {
//        return googleApiClient.isConnecting() || googleApiClient.isConnected();
        return mSharedPreferences.getBoolean(SAVE_STATE_KEY_IS_CONNECTED, false);
    }

    /**
     * Make login request - authorize in Google plus social network
     * @param onLoginCompleteListener listener for login complete
     */
    @Override
    public void requestLogin(OnLoginCompleteListener onLoginCompleteListener) {
        super.requestLogin(onLoginCompleteListener);
        mConnectRequested = true;
        try {
            mConnectionResult.startResolutionForResult(mActivity, REQUEST_AUTH);
        } catch (Exception e) {
            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        }
    }

    /**
     * Logout from Google plus social network
     */
    @Override
    public void logout() {
        mConnectRequested = false;

        if (googleApiClient.isConnected()) {
            mSharedPreferences.edit().remove(SAVE_STATE_KEY_IS_CONNECTED).commit();
            Plus.AccountApi.clearDefaultAccount(googleApiClient);
            googleApiClient.disconnect();
            googleApiClient.connect();
        }
    }

    /**
     * Get id of Google plus social network
     * @return Social network id for Google Plus = 3
     */
    @Override
    public int getID() {
        return ID;
    }

    /**
     * Not supported in Google plus sdk
     */
    @Override
    public AccessToken getAccessToken() {
        throw new SocialNetworkException("Not supported for GooglePlusSocialNetwork");
    }

    /**
     * Request {@link com.github.gorbin.asne.core.AccessToken} of Google plus social network that you can get from onRequestAccessTokenCompleteListener
     * @param onRequestAccessTokenCompleteListener listener for {@link com.github.gorbin.asne.core.AccessToken} request
     */
    @Override
    public void requestAccessToken(OnRequestAccessTokenCompleteListener onRequestAccessTokenCompleteListener) {
        super.requestAccessToken(onRequestAccessTokenCompleteListener);

        AsyncTask<Activity, Void, String> task = new AsyncTask<Activity, Void, String>() {
            @Override
            protected String doInBackground(Activity... params) {
                String scope = "oauth2:" + Scopes.PLUS_LOGIN;
                String token;
                try {
                    token = GoogleAuthUtil.getToken(params[0],
                            Plus.AccountApi.getAccountName(googleApiClient), scope);
                } catch (Exception e) {
                    e.printStackTrace();
                    return e.getMessage();
                }
                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                if(token != null) {
                    ((OnRequestAccessTokenCompleteListener) mLocalListeners.get(REQUEST_ACCESS_TOKEN))
                            .onRequestAccessTokenComplete(getID(), new AccessToken(token, null));
                } else {
                    mLocalListeners.get(REQUEST_ACCESS_TOKEN).onError(getID(), REQUEST_ACCESS_TOKEN, token, null);
                }
            }
        };
        task.execute(mActivity);
    }

    /**
     * Request current user {@link com.github.gorbin.asne.core.persons.SocialPerson}
     * @param onRequestSocialPersonCompleteListener listener for {@link com.github.gorbin.asne.core.persons.SocialPerson} request
     */
    @Override
    public void requestCurrentPerson(OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener) {
        super.requestCurrentPerson(onRequestSocialPersonCompleteListener);
        requestPerson("me", onRequestSocialPersonCompleteListener);
    }

    /**
     * Request {@link com.github.gorbin.asne.core.persons.SocialPerson} by user id
     * @param userID id of Google plus user
     * @param onRequestSocialPersonCompleteListener listener for {@link com.github.gorbin.asne.core.persons.SocialPerson} request
     */
    @Override
    public void requestSocialPerson(String userID, OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener) {
        super.requestSocialPerson(userID, onRequestSocialPersonCompleteListener);
        requestPerson(userID, onRequestSocialPersonCompleteListener);
    }

    /**
     * Request ArrayList of {@link com.github.gorbin.asne.core.persons.SocialPerson} by array of userIds
     * @param userID array of user ids in social network
     * @param onRequestSocialPersonsCompleteListener listener for request ArrayList of {@link com.github.gorbin.asne.core.persons.SocialPerson}
     */
    @Override
    public void requestSocialPersons(final String[] userID, OnRequestSocialPersonsCompleteListener onRequestSocialPersonsCompleteListener) {
        super.requestSocialPersons(userID, onRequestSocialPersonsCompleteListener);
        Plus.PeopleApi.load(googleApiClient, userID).setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
            @Override
            public void onResult(final People.LoadPeopleResult loadPeopleResult) {
                if (loadPeopleResult.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
                    PersonBuffer personBuffer = loadPeopleResult.getPersonBuffer();
                    try {
                        int count = personBuffer.getCount();
                        SocialPerson socialPerson = new SocialPerson();
                        final ArrayList<SocialPerson> socialPersons = new ArrayList<SocialPerson>();
                        for (int i = 0; i < count; i++) {
                            getSocialPerson(socialPerson, personBuffer.get(i), userID[i]);
                            socialPersons.add(socialPerson);
                            socialPerson = new SocialPerson();
                        }
                        if (mLocalListeners.get(REQUEST_GET_PERSONS) != null) {
                            ((OnRequestSocialPersonsCompleteListener) mLocalListeners.get(REQUEST_GET_PERSONS))
                                    .onRequestSocialPersonsSuccess(getID(), socialPersons);
                            mLocalListeners.remove(REQUEST_GET_PERSONS);
                        }
                    } finally {
                        personBuffer.close();
                    }
                } else {
                    if (mLocalListeners.get(REQUEST_GET_PERSONS) != null) {
                        mLocalListeners.get(REQUEST_GET_PERSONS)
                                .onError(getID(), REQUEST_GET_PERSONS, "Can't get persons"
                                        + loadPeopleResult.getStatus(), null);
                        mLocalListeners.remove(REQUEST_GET_PERSONS);
                    }
                }
            }
        });
    }

    /**
     * Request user {@link com.github.gorbin.asne.googleplus.GooglePlusPerson} by userId - detailed user data
     * @param userId id of Google plus user
     * @param onRequestDetailedSocialPersonCompleteListener listener for request detailed social person
     */
    @Override
    public void requestDetailedSocialPerson(final String userId, OnRequestDetailedSocialPersonCompleteListener onRequestDetailedSocialPersonCompleteListener) {
        super.requestDetailedSocialPerson(userId, onRequestDetailedSocialPersonCompleteListener);
        final String user = userId == null ? "me" : userId;
        Plus.PeopleApi.load(googleApiClient, user).setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
            @Override
            public void onResult(final People.LoadPeopleResult loadPeopleResult) {
                if (loadPeopleResult.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
                    PersonBuffer personBuffer = loadPeopleResult.getPersonBuffer();
                    try {
                        int count = personBuffer.getCount();
                        final GooglePlusPerson googlePlusPerson = new GooglePlusPerson();
                        for (int i = 0; i < count; i++) {
                            getDetailedSocialPerson(googlePlusPerson, personBuffer.get(i), user);
                        }
                        if (mLocalListeners.get(REQUEST_GET_DETAIL_PERSON) != null) {
                            ((OnRequestDetailedSocialPersonCompleteListener) mLocalListeners.get(REQUEST_GET_DETAIL_PERSON))
                                    .onRequestDetailedSocialPersonSuccess(getID(), googlePlusPerson);
                            mLocalListeners.remove(REQUEST_GET_DETAIL_PERSON);
                        }
                    } finally {
                        personBuffer.close();
                    }
                } else {
                    if (mLocalListeners.get(REQUEST_GET_DETAIL_PERSON) != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mLocalListeners.get(REQUEST_GET_DETAIL_PERSON)
                                        .onError(getID(), REQUEST_GET_DETAIL_PERSON, "Can't get person"
                                                + loadPeopleResult.getStatus(), null);
                                mLocalListeners.remove(REQUEST_GET_DETAIL_PERSON);
                            }
                        });
                    }
                }
            }
        });
    }

    private void requestPerson(final String userID, OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener){
        Plus.PeopleApi.load(googleApiClient, userID).setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
            @Override
            public void onResult(final People.LoadPeopleResult loadPeopleResult) {
                if (loadPeopleResult.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
                    PersonBuffer personBuffer = loadPeopleResult.getPersonBuffer();
                    try {
                        int count = personBuffer.getCount();
                        final SocialPerson socialPerson = new SocialPerson();
                        for (int i = 0; i < count; i++) {
                            getSocialPerson(socialPerson, personBuffer.get(i), userID);
                        }
                        if (mLocalListeners.get(REQUEST_GET_PERSON) != null) {
                            ((OnRequestSocialPersonCompleteListener) mLocalListeners
                                    .get(REQUEST_GET_PERSON))
                                    .onRequestSocialPersonSuccess(getID(), socialPerson);
                            mLocalListeners.remove(REQUEST_GET_PERSON);
                        } else if (mLocalListeners.get(REQUEST_GET_CURRENT_PERSON) != null) {
                            ((OnRequestSocialPersonCompleteListener) mLocalListeners
                                    .get(REQUEST_GET_CURRENT_PERSON))
                                    .onRequestSocialPersonSuccess(getID(), socialPerson);
                            mLocalListeners.remove(REQUEST_GET_CURRENT_PERSON);
                        }
                    } finally {
                        personBuffer.close();
                    }
                } else {
                    if (mLocalListeners.get(REQUEST_GET_PERSON) != null) {
                        mLocalListeners.get(REQUEST_GET_PERSON).onError(getID(), REQUEST_GET_PERSON, "Can't get person"
                                + loadPeopleResult.getStatus(), null);
                        mLocalListeners.remove(REQUEST_GET_PERSON);
                    } else if (mLocalListeners.get(REQUEST_GET_CURRENT_PERSON) != null) {
                        mLocalListeners.get(REQUEST_GET_CURRENT_PERSON).onError(getID(), REQUEST_GET_CURRENT_PERSON, "Can't get person"
                                + loadPeopleResult.getStatus(), null);
                        mLocalListeners.remove(REQUEST_GET_CURRENT_PERSON);
                    }
                }
            }
        });
    }

    private SocialPerson getSocialPerson(SocialPerson socialPerson, Person person, String userId){
        socialPerson.id = person.getId();
        socialPerson.name = person.getDisplayName();
        if ((person.hasImage()) && (person.getImage().hasUrl())) {
            socialPerson.avatarURL = person.getImage().getUrl().replace("?sz=50", "?sz=200");
        }
        socialPerson.profileURL = person.getUrl();
        if(userId.equals("me")) {
            socialPerson.email = Plus.AccountApi.getAccountName(googleApiClient);
        }
        return socialPerson;
    }

    private GooglePlusPerson getDetailedSocialPerson(GooglePlusPerson googlePlusPerson, Person person, String userId){
        getSocialPerson(googlePlusPerson, person, userId);
        googlePlusPerson.aboutMe = person.getAboutMe();
        googlePlusPerson.birthday = person.getBirthday();
        googlePlusPerson.braggingRights = person.getBraggingRights();
        Person.Cover cover = person.getCover();
        if (cover != null) {
            Person.Cover.CoverPhoto coverPhoto = cover.getCoverPhoto();
            if (coverPhoto != null) {
                String coverPhotoURL = coverPhoto.getUrl();
                if(coverPhotoURL != null){
                    googlePlusPerson.coverURL = coverPhotoURL;
                }
            }
        }
        googlePlusPerson.currentLocation = person.getCurrentLocation();
        googlePlusPerson.gender = person.getGender();
        googlePlusPerson.lang = person.getLanguage();
        googlePlusPerson.nickname = person.getNickname();
        googlePlusPerson.objectType = person.getObjectType();
        List<Person.Organizations> organizations = person.getOrganizations();
        if(organizations != null && organizations.size() > 0) {
            String organizationsName = organizations.get(organizations.size()-1).getName();
            if (organizationsName != null) {
                googlePlusPerson.company = organizationsName;
            }
            String organizationsTitle = organizations.get(organizations.size()-1).getTitle();
            if (organizationsTitle != null) {
                googlePlusPerson.position = organizationsTitle;
            }
        }
        List<Person.PlacesLived> placesLived = person.getPlacesLived();
        if(placesLived != null && placesLived.size() > 0) {
            String placeLivedValue = placesLived.get(placesLived.size()-1).getValue();
            if (placeLivedValue != null) {
                googlePlusPerson.placeLivedValue = placeLivedValue;
            }
        }
        googlePlusPerson.relationshipStatus = person.getRelationshipStatus();
        googlePlusPerson.tagline = person.getTagline();
        return googlePlusPerson;
    }

    /**
     * Not supported via Google plus sdk.
     * @throws com.github.gorbin.asne.core.SocialNetworkException
     * @param message  message that should be shared
     * @param onPostingCompleteListener listener for posting request
     */
    @Override
    public void requestPostMessage(String message, OnPostingCompleteListener onPostingCompleteListener) {
        throw new SocialNetworkException("requestPostMessage isn't allowed for GooglePlusSocialNetwork");
    }

    /**
     * Not supported via Google plus sdk.
     * @throws com.github.gorbin.asne.core.SocialNetworkException
     * @param photo photo that should be shared
     * @param message message that should be shared with photo
     * @param onPostingCompleteListener listener for posting request
     */
    @Override
    public void requestPostPhoto(File photo, String message, OnPostingCompleteListener onPostingCompleteListener) {
        throw new SocialNetworkException("requestPostPhoto isn't allowed for GooglePlusSocialNetwork");
    }

    /**
     * Not supported via Google plus sdk.
     * @throws com.github.gorbin.asne.core.SocialNetworkException
     * @param bundle bundle containing information that should be shared(Bundle constants in {@link com.github.gorbin.asne.core.SocialNetwork})
     * @param message message that should be shared with bundle
     * @param onPostingCompleteListener listener for posting request
     */
    @Override
    public void requestPostLink(Bundle bundle, String message, OnPostingCompleteListener onPostingCompleteListener) {
        throw new SocialNetworkException("requestPostLink isn't allowed for GooglePlusSocialNetwork");
    }

    /**
     * Request Google plus share dialog
     * @param bundle bundle containing information that should be shared(Bundle constants in {@link com.github.gorbin.asne.core.SocialNetwork})
     * @param onPostingCompleteListener listener for posting request
     */
    @Override
    public void requestPostDialog(Bundle bundle, OnPostingCompleteListener onPostingCompleteListener) {
        super.requestPostDialog(bundle, onPostingCompleteListener);
        PlusShare.Builder plusShare =  new PlusShare.Builder(mActivity)
                .setType("text/plain");
        if(bundle != null){
            if(bundle.containsKey(BUNDLE_MESSAGE)){
                plusShare.setText(bundle.getString(BUNDLE_MESSAGE));
            }
            if(bundle.containsKey(BUNDLE_LINK)){
                plusShare.setContentUrl(Uri.parse(bundle.getString(BUNDLE_LINK)));
            }
        }
        Intent shareIntent = plusShare.getIntent();
        mActivity.startActivityForResult(shareIntent, 0);
    }

    /**
     * Not supported via Google plus sdk.
     * @throws com.github.gorbin.asne.core.SocialNetworkException
     * @param userID user id that should be checked as friend of current user
     * @param onCheckIsFriendCompleteListener listener for checking friend request
     */
    @Override
    public void requestCheckIsFriend(String userID, OnCheckIsFriendCompleteListener onCheckIsFriendCompleteListener) {
        throw new SocialNetworkException("requestCheckIsFriend isn't allowed for GooglePlusSocialNetwork");
    }

    /**
     * Get current user friends list
     * @param onRequestGetFriendsCompleteListener listener for getting list of current user friends
     */
    @Override
    public void requestGetFriends(OnRequestGetFriendsCompleteListener onRequestGetFriendsCompleteListener) {
        super.requestGetFriends(onRequestGetFriendsCompleteListener);
        final ArrayList<SocialPerson> socialPersons = new ArrayList<SocialPerson>();
        final ArrayList<String> ids = new ArrayList<String>();
        getAllFriends(null, socialPersons, ids);
    }

    private void getAllFriends(String pageToken, final ArrayList<SocialPerson> socialPersons, final ArrayList<String> ids){
        Plus.PeopleApi.loadVisible(googleApiClient, pageToken).setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
            @Override
            public void onResult(final People.LoadPeopleResult loadPeopleResult) {
                if (loadPeopleResult.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
                    PersonBuffer personBuffer = loadPeopleResult.getPersonBuffer();
                    try {
                        SocialPerson socialPerson = new SocialPerson();
                        for(Person person : personBuffer){
                            getSocialPerson(socialPerson, person, "not me");
                            ids.add(person.getId());
                            socialPersons.add(socialPerson);
                            socialPerson = new SocialPerson();
                        }
                        if(loadPeopleResult.getNextPageToken() != null) {
                            getAllFriends(loadPeopleResult.getNextPageToken(), socialPersons, ids);
                        } else {
                            if (mLocalListeners.get(REQUEST_GET_FRIENDS) != null) {
                                ((OnRequestGetFriendsCompleteListener) mLocalListeners.get(REQUEST_GET_FRIENDS))
                                        .OnGetFriendsIdComplete(getID(), ids.toArray(new String[ids.size()]));
                                ((OnRequestGetFriendsCompleteListener) mLocalListeners.get(REQUEST_GET_FRIENDS))
                                        .OnGetFriendsComplete(getID(), socialPersons);
                                mLocalListeners.remove(REQUEST_GET_FRIENDS);
                            }
                        }
                    } finally {
                        personBuffer.close();
                    }
                } else {
                    if (mLocalListeners.get(REQUEST_GET_FRIENDS) != null) {
                        mLocalListeners.get(REQUEST_GET_FRIENDS)
                                .onError(getID(), REQUEST_GET_DETAIL_PERSON, "Can't get person"
                                        + loadPeopleResult.getStatus(), null);
                    }
                }
            }
        });
    }

    /**
     * Not supported via Google plus sdk.
     * @throws com.github.gorbin.asne.core.SocialNetworkException
     * @param userID id of user that should be invited
     * @param onRequestAddFriendCompleteListener listener for invite result
     */
    @Override
    public void requestAddFriend(String userID, OnRequestAddFriendCompleteListener onRequestAddFriendCompleteListener) {
        throw new SocialNetworkException("requestAddFriend isn't allowed for GooglePlusSocialNetwork");
    }

    /**
     * Not supported via Google plus sdk.
     * @throws com.github.gorbin.asne.core.SocialNetworkException
     * @param userID user id that should be removed from friends
     * @param onRequestRemoveFriendCompleteListener listener to remove friend request response
     */
    @Override
    public void requestRemoveFriend(String userID, OnRequestRemoveFriendCompleteListener onRequestRemoveFriendCompleteListener) {
        throw new SocialNetworkException("requestRemoveFriend isn't allowed for GooglePlusSocialNetwork");
    }

    /**
     * Overrided for Google plus
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = mSocialNetworkManager.getActivity();
        Plus.PlusOptions plusOptions = new Plus.PlusOptions.Builder()
                .addActivityTypes(MomentUtil.ACTIONS)
                .build();
        googleApiClient = new GoogleApiClient.Builder(mActivity)
                .addApi(Plus.API,  plusOptions)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    /**
     * Overrided for Google plus
     */
    @Override
    public void onStart() {
        googleApiClient.connect();
    }

    /**
     * Overrided for Google plus
     */
    @Override
    public void onStop() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    /**
     * Overrided for Google plus
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int sanitizedRequestCode = requestCode % 0x10000;
        if (sanitizedRequestCode == REQUEST_AUTH) {
            if (resultCode == Activity.RESULT_OK && !googleApiClient.isConnected() && !googleApiClient.isConnecting()) {
                // This time, connect should succeed.
                googleApiClient.connect();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                if (mLocalListeners.get(REQUEST_LOGIN) != null) {
                    mLocalListeners.get(REQUEST_LOGIN).onError(getID(), REQUEST_LOGIN,
                            "canceled", null);
                }
            }
        }
    }

    /**
     * After calling connect(), this method will be invoked asynchronously when the connect request has successfully completed.
     * @param bundle Bundle of data provided to clients by Google Play services. May be null if no content is provided by the service.
     */
    @Override
    public void onConnected(Bundle bundle) {
        if (mConnectRequested) {
            if (mLocalListeners.get(REQUEST_LOGIN) != null) {
                mSharedPreferences.edit().putBoolean(SAVE_STATE_KEY_IS_CONNECTED, true).commit();
                ((OnLoginCompleteListener) mLocalListeners.get(REQUEST_LOGIN)).onLoginSuccess(getID());
                return;
            }
            if (mLocalListeners.get(REQUEST_LOGIN) != null) {
                mLocalListeners.get(REQUEST_LOGIN).onError(getID(), REQUEST_LOGIN,
                        "get person == null", null);
            }
        }
        mConnectRequested = false;
    }

    /**
     * Called when the client is temporarily in a disconnected state.
     * @param i The reason for the disconnection. Defined by constants CAUSE_*.
     */
    @Override
    public void onConnectionSuspended(int i) {
        if (mLocalListeners.get(REQUEST_LOGIN) != null) {
            mLocalListeners.get(REQUEST_LOGIN).onError(getID(), REQUEST_LOGIN,
                    "get person == null", null);
        }
        mConnectRequested = false;
    }

    /**
     * Called when the client is disconnected.
     */
    @Override
    public void onDisconnected() {
        mConnectRequested = false;
    }

    /**
     * Called when there was an error connecting the client to the service.
     * @param connectionResult A ConnectionResult that can be used for resolving the error, and deciding what sort of error occurred.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mConnectionResult = connectionResult;
        if (mConnectRequested && mLocalListeners.get(REQUEST_LOGIN) != null) {
            mLocalListeners.get(REQUEST_LOGIN).onError(getID(), REQUEST_LOGIN,
                    "error: " + connectionResult.getErrorCode(), null);
        }

        mConnectRequested = false;
    }
}
