package com.androidsocialnetworks.lib.impl;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.androidsocialnetworks.lib.AccessToken;
import com.androidsocialnetworks.lib.MomentUtil;
import com.androidsocialnetworks.lib.SocialNetwork;
import com.androidsocialnetworks.lib.SocialNetworkException;
import com.androidsocialnetworks.lib.listener.OnCheckIsFriendCompleteListener;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.androidsocialnetworks.lib.listener.OnPostingCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestAccessTokenCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestAddFriendCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestDetailedSocialPersonCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestGetFriendsCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestRemoveFriendCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestSocialPersonCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestSocialPersonsCompleteListener;
import com.androidsocialnetworks.lib.persons.GooglePlusPerson;
import com.androidsocialnetworks.lib.persons.SocialPerson;
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

public class GooglePlusSocialNetwork extends SocialNetwork implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int ID = 3;

    private static final String TAG = GooglePlusSocialNetwork.class.getSimpleName();
    // max 16 bit to use in startActivityForResult
    private static final int REQUEST_AUTH = UUID.randomUUID().hashCode() & 0xFFFF;
    /**
     * googleApiClient.isConntected() works really strange, it returs false right after init and then true,
     * so let's handle state by ourselves
     */
    private static final String SAVE_STATE_KEY_IS_CONNECTED = "GooglePlusSocialNetwork.SAVE_STATE_KEY_OAUTH_TOKEN";
    private GoogleApiClient googleApiClient;
    private ConnectionResult mConnectionResult;

    private boolean mConnectRequested;

    private Handler mHandler = new Handler();

    public GooglePlusSocialNetwork(Fragment fragment) {
        super(fragment);
    }

    @Override
    public boolean isConnected() {
//        return googleApiClient.isConnecting() || googleApiClient.isConnected();
        return mSharedPreferences.getBoolean(SAVE_STATE_KEY_IS_CONNECTED, false);
    }

    @Override
    public void requestLogin(OnLoginCompleteListener onLoginCompleteListener) {
        super.requestLogin(onLoginCompleteListener);
        mConnectRequested = true;
        try {
            mConnectionResult.startResolutionForResult(mSocialNetworkManager.getActivity(), REQUEST_AUTH);
        } catch (Exception e) {
            Log.e(TAG, "ERROR", e);
            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        }
    }

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

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public AccessToken getAccessToken() {
        throw new SocialNetworkException("Not supported for GooglePlusSocialNetwork");
    }

    @Override
    public void requestAccessToken(OnRequestAccessTokenCompleteListener onRequestAccessTokenCompleteListener) {
        super.requestAccessToken(onRequestAccessTokenCompleteListener);

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String scope = "oauth2:" + Scopes.PLUS_LOGIN;
                String token;
                try {
                    token = GoogleAuthUtil.getToken(mSocialNetworkManager.getActivity(),
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
                    mLocalListeners.get(REQUEST_LOGIN).onError(getID(), REQUEST_ACCESS_TOKEN, token, null);
                }
            }
        };
        task.execute();
    }

    @Override
    public void requestCurrentPerson(OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener) {
        super.requestCurrentPerson(onRequestSocialPersonCompleteListener);
        requestPerson("me", onRequestSocialPersonCompleteListener);
    }

    @Override
    public void requestSocialPerson(String userID, OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener) {
        super.requestSocialPerson(userID, onRequestSocialPersonCompleteListener);
        requestPerson(userID, onRequestSocialPersonCompleteListener);
    }

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
                    Log.e(TAG, "Error requesting people data: " + loadPeopleResult.getStatus());
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
                    Log.e(TAG, "Error requesting people data: " + loadPeopleResult.getStatus());
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
//                    Log.e(TAG, "Error requesting people data: " + loadPeopleResult.getStatus());
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

    @Override
    public void requestPostMessage(String message, OnPostingCompleteListener onPostingCompleteListener) {
        throw new SocialNetworkException("requestPostMessage isn't allowed for GooglePlusSocialNetwork");
    }

    @Override
    public void requestPostPhoto(File photo, String message, OnPostingCompleteListener onPostingCompleteListener) {
        throw new SocialNetworkException("requestPostPhoto isn't allowed for GooglePlusSocialNetwork");
    }

    @Override
    public void requestPostLink(Bundle bundle, String message, OnPostingCompleteListener onPostingCompleteListener) {
        throw new SocialNetworkException("requestPostLink isn't allowed for GooglePlusSocialNetwork");
    }

    @Override
    public void requestPostDialog(Bundle bundle, OnPostingCompleteListener onPostingCompleteListener) {
        super.requestPostDialog(bundle, onPostingCompleteListener);

        Intent shareIntent = new PlusShare.Builder(mSocialNetworkManager.getActivity())
            .setType("text/plain")
            .setText(bundle.getString(BUNDLE_MESSAGE))
            .setContentUrl(Uri.parse(bundle.getString(BUNDLE_LINK)))
            .getIntent();

        mSocialNetworkManager.getActivity().startActivityForResult(shareIntent, 0);
    }

    @Override
    public void requestCheckIsFriend(String userID, OnCheckIsFriendCompleteListener onCheckIsFriendCompleteListener) {
        throw new SocialNetworkException("requestCheckIsFriend isn't allowed for GooglePlusSocialNetwork");
    }

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
                    Log.e(TAG, "Error requesting people data: " + loadPeopleResult.getStatus());
                    if (mLocalListeners.get(REQUEST_GET_FRIENDS) != null) {
                        mLocalListeners.get(REQUEST_GET_FRIENDS)
                                        .onError(getID(), REQUEST_GET_DETAIL_PERSON, "Can't get person"
                                                + loadPeopleResult.getStatus(), null);
                    }
                }
            }
        });
    }
    @Override
    public void requestAddFriend(String userID, OnRequestAddFriendCompleteListener onRequestAddFriendCompleteListener) {
        throw new SocialNetworkException("requestAddFriend isn't allowed for GooglePlusSocialNetwork");
    }

    @Override
    public void requestRemoveFriend(String userID, OnRequestRemoveFriendCompleteListener onRequestRemoveFriendCompleteListener) {
        throw new SocialNetworkException("requestRemoveFriend isn't allowed for GooglePlusSocialNetwork");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Plus.PlusOptions plusOptions = new Plus.PlusOptions.Builder()
                .addActivityTypes(MomentUtil.ACTIONS)
                .build();
        googleApiClient = new GoogleApiClient.Builder(mSocialNetworkManager.getActivity())
                .addApi(Plus.API,  plusOptions)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onStart() {
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_AUTH) {
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

    @Override
    public void onConnectionSuspended(int i) {
        if (mLocalListeners.get(REQUEST_LOGIN) != null) {
            mLocalListeners.get(REQUEST_LOGIN).onError(getID(), REQUEST_LOGIN,
                    "get person == null", null);
        }
        mConnectRequested = false;
    }

    @Override
    public void onDisconnected() {
        mConnectRequested = false;
    }

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
