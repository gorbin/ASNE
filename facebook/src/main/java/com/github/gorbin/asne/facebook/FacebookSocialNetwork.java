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
package com.github.gorbin.asne.facebook;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

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
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.internal.SessionTracker;
import com.facebook.internal.Utility;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for Facebook social network integration
 *
 * @author Anton Krasov
 * @author Evgeny Gorbin (gorbin.e.o@gmail.com)
 */
public class FacebookSocialNetwork extends SocialNetwork {
    /*** Social network ID in asne modules, should be unique*/
    public static final int ID = 4;

    private static final String PERMISSION = "publish_actions";
    private SessionTracker mSessionTracker;
    private UiLifecycleHelper mUILifecycleHelper;
    private String mApplicationId;
    private SessionState mSessionState;
    private String mPhotoPath;
    private String mStatus;
    private Bundle mBundle;
    private ArrayList<String> permissions;
    private PendingAction mPendingAction = PendingAction.NONE;
    private Session.StatusCallback mSessionStatusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    public FacebookSocialNetwork(Fragment fragment, ArrayList<String> permissions) {
        super(fragment);
        String applicationID = Utility.getMetadataApplicationId(fragment.getActivity());

        if (applicationID == null) {
            throw new IllegalStateException("applicationID can't be null\n" +
                    "Please check https://developers.facebook.com/docs/android/getting-started/");
        }
        this.permissions = permissions;
    }

    /**
     * Check is social network connected
     * @return true if connected to Facebook social network and false if not
     */
    @Override
    public boolean isConnected() {
        Session session = Session.getActiveSession();
        return (session != null && session.isOpened());
    }

    /**
     * Make login request - authorize in Facebook social network
     * @param onLoginCompleteListener listener to trigger when Login complete
     */
    @Override
    public void requestLogin(OnLoginCompleteListener onLoginCompleteListener) {
        super.requestLogin(onLoginCompleteListener);

        final Session openSession = mSessionTracker.getOpenSession();

        if (openSession != null) {
            if (mLocalListeners.get(REQUEST_LOGIN) != null) {
                mLocalListeners.get(REQUEST_LOGIN).onError(getID(), REQUEST_LOGIN, "Already loginned", null);
            }
        }

        Session currentSession = mSessionTracker.getSession();
        if (currentSession == null || currentSession.getState().isClosed()) {
            mSessionTracker.setSession(null);
            Session session = new Session.Builder(mSocialNetworkManager.getActivity())
                    .setApplicationId(mApplicationId).build();
            Session.setActiveSession(session);
            currentSession = session;
        }

        if (!currentSession.isOpened()) {
            Session.OpenRequest openRequest;
            openRequest = new Session.OpenRequest(mSocialNetworkManager.getActivity());

            openRequest.setDefaultAudience(SessionDefaultAudience.EVERYONE);
            if(permissions != null) {
                openRequest.setPermissions(permissions);
            }
            openRequest.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
            currentSession.openForRead(openRequest);
        }
    }

    /**
     * Request {@link com.github.gorbin.asne.core.AccessToken} of Facebook social network that you can get from onRequestAccessTokenCompleteListener
     * @param onRequestAccessTokenCompleteListener listener for {@link com.github.gorbin.asne.core.AccessToken} request
     */
    @Override
    public void requestAccessToken(OnRequestAccessTokenCompleteListener onRequestAccessTokenCompleteListener) {
        super.requestAccessToken(onRequestAccessTokenCompleteListener);
        ((OnRequestAccessTokenCompleteListener) mLocalListeners.get(REQUEST_ACCESS_TOKEN))
                .onRequestAccessTokenComplete(getID(), new AccessToken(Session.getActiveSession().getAccessToken(), null));
    }

    /**
     * Logout from Facebook social network
     */
    @Override
    public void logout() {
        if (mSessionTracker == null) return;

        final Session openSession = mSessionTracker.getOpenSession();

        if (openSession != null) {
            openSession.closeAndClearTokenInformation();
        }
    }

    /**
     * Get id of Facebook social network
     * @return Social network id for Facebook = 4
     */
    @Override
    public int getID() {
        return ID;
    }

    /**
     * Method to get AccessToken of Facebook social network
     * @return {@link com.github.gorbin.asne.core.AccessToken}
     */
	@Override
    public AccessToken getAccessToken() {
        return new AccessToken(Session.getActiveSession().getAccessToken(), null);
    }

    /**
     * Request current user {@link com.github.gorbin.asne.core.persons.SocialPerson}
     * @param onRequestSocialPersonCompleteListener listener for {@link com.github.gorbin.asne.core.persons.SocialPerson} request
     */
    @Override
    public void requestCurrentPerson(OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener) {
        super.requestCurrentPerson(onRequestSocialPersonCompleteListener);

        final Session currentSession = mSessionTracker.getOpenSession();

        if (currentSession == null) {
            if (mLocalListeners.get(REQUEST_GET_CURRENT_PERSON) != null) {
                mLocalListeners.get(REQUEST_GET_CURRENT_PERSON).onError(getID(),
                        REQUEST_GET_PERSON, "Please login first", null);
            }
            return;
        }

        Request request = Request.newMeRequest(currentSession, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser me, Response response) {
                if (response.getError() != null) {
                    if (mLocalListeners.get(REQUEST_GET_CURRENT_PERSON) != null) {
                        mLocalListeners.get(REQUEST_GET_CURRENT_PERSON).onError(
                                getID(), REQUEST_GET_CURRENT_PERSON, response.getError().getErrorMessage()
                                , null);
                    }
                    return;
                }
                if (mLocalListeners.get(REQUEST_GET_CURRENT_PERSON) != null) {
                    SocialPerson socialPerson = new SocialPerson();
                    getSocialPerson(socialPerson, me);
                    ((OnRequestSocialPersonCompleteListener) mLocalListeners.get(REQUEST_GET_CURRENT_PERSON))
                            .onRequestSocialPersonSuccess(getID(), socialPerson);
                }
            }
        });
        request.executeAsync();
    }

    /**
     * Not supported via Facebook sdk.
     * @throws com.github.gorbin.asne.core.SocialNetworkException
     * @param userID user id in social network
     * @param onRequestSocialPersonCompleteListener listener for request {@link com.github.gorbin.asne.core.persons.SocialPerson}
     */
    @Override
    public void requestSocialPerson(String userID, OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener) {
        throw new SocialNetworkException("requestSocialPerson isn't allowed for FacebookSocialNetwork");
    }

    /**
     * Not supported via Facebook sdk.
     * @throws com.github.gorbin.asne.core.SocialNetworkException
     * @param userID array of user ids in social network
     * @param onRequestSocialPersonsCompleteListener listener for request ArrayList of {@link com.github.gorbin.asne.core.persons.SocialPerson}
     */
	@Override
    public void requestSocialPersons(String[] userID, OnRequestSocialPersonsCompleteListener onRequestSocialPersonsCompleteListener) {
        throw new SocialNetworkException("requestSocialPersons isn't allowed for FacebookSocialNetwork");
    }

    /**
     * Request user {@link com.github.gorbin.asne.facebook.FacebookPerson} by userId - detailed user data
     * @param userId user id in social network
     * @param onRequestDetailedSocialPersonCompleteListener listener for request detailed social person
     */
    @Override
    public void requestDetailedSocialPerson(String userId, OnRequestDetailedSocialPersonCompleteListener onRequestDetailedSocialPersonCompleteListener) {
        super.requestDetailedSocialPerson(userId, onRequestDetailedSocialPersonCompleteListener);
        final Session currentSession = mSessionTracker.getOpenSession();

        if (currentSession == null) {
            if (mLocalListeners.get(REQUEST_GET_DETAIL_PERSON) != null) {
                mLocalListeners.get(REQUEST_GET_DETAIL_PERSON).onError(getID(),
                        REQUEST_GET_DETAIL_PERSON, "Please login first", null);
            }

            return;
        }

        Request request = Request.newMeRequest(currentSession, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser me, Response response) {
                if (response.getError() != null) {
                    if (mLocalListeners.get(REQUEST_GET_DETAIL_PERSON) != null) {
                        mLocalListeners.get(REQUEST_GET_DETAIL_PERSON).onError(
                                getID(), REQUEST_GET_DETAIL_PERSON, response.getError().getErrorMessage()
                                , null);
                    }
                    return;
                }

                if (mLocalListeners.get(REQUEST_GET_DETAIL_PERSON) != null) {
                    FacebookPerson facebookPerson = new FacebookPerson();
                    getDetailedSocialPerson(facebookPerson, me);

                    ((OnRequestDetailedSocialPersonCompleteListener) mLocalListeners.get(REQUEST_GET_DETAIL_PERSON))
                            .onRequestDetailedSocialPersonSuccess(getID(), facebookPerson);
                }
            }
        });
        request.executeAsync();
    }

    private SocialPerson getSocialPerson(SocialPerson socialPerson, GraphUser user){
        socialPerson.id = user.getId();
        socialPerson.name = user.getName();
        socialPerson.avatarURL = String.format("https://graph.facebook.com/%s/picture?type=large", user.getId());
        if(user.getLink() != null) {
            socialPerson.profileURL = user.getLink();
        } else {
            socialPerson.profileURL = String.format("https://www.facebook.com/", user.getId());
        }
        if(user.getProperty("email") != null){
            socialPerson.email = user.getProperty("email").toString();
        }
        return socialPerson;
    }

	private FacebookPerson getDetailedSocialPerson(FacebookPerson facebookPerson, GraphUser user){
        getSocialPerson(facebookPerson, user);
        facebookPerson.firstName = user.getFirstName();
        facebookPerson.middleName = user.getMiddleName();
        facebookPerson.lastName = user.getLastName();
        if(user.getProperty("gender") != null) {
            facebookPerson.gender = user.getProperty("gender").toString();
        }
        facebookPerson.birthday = user.getBirthday();
        if(user.getLocation() != null) {
            facebookPerson.city = user.getLocation().getProperty("name").toString();
        }
        facebookPerson.verified = user.getProperty("verified").toString();
        return facebookPerson;
    }

    /**
     * Post message to social network
     * @param message  message that should be shared
     * @param onPostingCompleteListener listener for posting request
     */
    @Override
    public void requestPostMessage(String message, OnPostingCompleteListener onPostingCompleteListener) {
        super.requestPostMessage(message, onPostingCompleteListener);
        mStatus = message;
        performPublish(PendingAction.POST_STATUS_UPDATE);
    }

    /**
     * Post photo with comment to social network
     * @param photo photo that should be shared
     * @param message message that should be shared with photo
     * @param onPostingCompleteListener listener for posting request
     */
    @Override
    public void requestPostPhoto(File photo, String message, OnPostingCompleteListener onPostingCompleteListener) {
        super.requestPostPhoto(photo, message, onPostingCompleteListener);
        mPhotoPath = photo.getAbsolutePath();
        mStatus = message;
        performPublish(PendingAction.POST_PHOTO);
    }

    /**
     * Post link with message to social network
     * @param bundle bundle containing information that should be shared(Bundle constants in {@link com.github.gorbin.asne.core.SocialNetwork})
     * @param message message that should be shared with bundle
     * @param onPostingCompleteListener listener for posting request
     */
    @Override
    public void requestPostLink(Bundle bundle, String message, OnPostingCompleteListener onPostingCompleteListener) {
        super.requestPostLink(bundle, message, onPostingCompleteListener);
        mBundle = bundle;
        performPublish(PendingAction.POST_LINK);
    }

    /**
     * Request facebook share dialog
     * @param bundle bundle containing information that should be shared(Bundle constants in {@link com.github.gorbin.asne.core.SocialNetwork})
     * @param onPostingCompleteListener listener for posting request
     */
    @Override
    public void requestPostDialog(Bundle bundle, OnPostingCompleteListener onPostingCompleteListener) {
        super.requestPostDialog(bundle, onPostingCompleteListener);
        if (FacebookDialog.canPresentShareDialog(mSocialNetworkManager.getActivity(),
                FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
            FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(mSocialNetworkManager.getActivity())
                    .setLink(bundle.getString(BUNDLE_LINK))
                    .setDescription(bundle.getString(BUNDLE_MESSAGE))
                    .setName(bundle.getString(BUNDLE_NAME))
                    .setApplicationName(bundle.getString(BUNDLE_APP_NAME))
                    .setCaption(bundle.getString(BUNDLE_CAPTION))
                    .setPicture(bundle.getString(BUNDLE_PICTURE))
//                    .setFriends(bundle.getStringArrayList(DIALOG_FRIENDS))
                    .build();
            mUILifecycleHelper.trackPendingDialogCall(shareDialog.present());
        } else {
            publishFeedDialog(bundle);
        }
    }

    private void publishFeedDialog(Bundle bundle) {
        Bundle params = new Bundle();
        params.putString("name", bundle.getString(BUNDLE_NAME));
        params.putString("caption", bundle.getString(BUNDLE_CAPTION));
        params.putString("description", bundle.getString(BUNDLE_MESSAGE));
        params.putString("link", bundle.getString(BUNDLE_LINK));
        params.putString("picture", bundle.getString(BUNDLE_PICTURE));

        WebDialog feedDialog = (
                new WebDialog.FeedDialogBuilder(mSocialNetworkManager.getActivity(),
                        Session.getActiveSession(),
                        params))
                .setOnCompleteListener(new WebDialog.OnCompleteListener() {
                    @Override
                    public void onComplete(Bundle values,
                                           FacebookException error) {
                        if (error == null) {
                            final String postId = values.getString("post_id");
                            if (postId != null) {
                                ((OnPostingCompleteListener) mLocalListeners.get(REQUEST_POST_DIALOG)).onPostSuccessfully(getID());
                            } else {
                                mLocalListeners.get(REQUEST_POST_DIALOG).onError(getID(),
                                        REQUEST_POST_DIALOG, "Canceled", null);
                            }
                        } else {
                            mLocalListeners.get(REQUEST_POST_DIALOG).onError(getID(),
                                    REQUEST_POST_DIALOG, "Canceled: " + error.toString(), null);
                        }
                        mLocalListeners.remove(REQUEST_POST_DIALOG);
                    }
                })
                .build();
        feedDialog.show();
    }

    private void performPublish(PendingAction action) {
        Session session = Session.getActiveSession();
        if (session != null) {
            mPendingAction = action;
            if (session.isPermissionGranted(PERMISSION)) {
                // We can do the action right away.
                handlePendingAction();
                return;
            } else if (session.isOpened()) {
                // We need to get new permissions, then complete the action when we get called back.
                session.requestNewPublishPermissions(new Session.NewPermissionsRequest(mSocialNetworkManager.getActivity(), PERMISSION));
                return;
            }
        }

        if (action == PendingAction.POST_STATUS_UPDATE) {
            if (mLocalListeners.get(REQUEST_POST_MESSAGE) != null) {
                mLocalListeners.get(REQUEST_POST_MESSAGE).onError(getID(),
                        REQUEST_POST_MESSAGE, "no session", null);
            }
        }

        if (action == PendingAction.POST_PHOTO) {
            if (mLocalListeners.get(REQUEST_POST_PHOTO) != null) {
                mLocalListeners.get(REQUEST_POST_PHOTO).onError(getID(),
                        REQUEST_POST_PHOTO, "no session", null);
            }
        }

        if (action == PendingAction.POST_LINK) {
            if (mLocalListeners.get(REQUEST_POST_LINK) != null) {
                mLocalListeners.get(REQUEST_POST_LINK).onError(getID(),
                        REQUEST_POST_LINK, "no session", null);
            }
        }
    }

    /**
     * Not supported via Facebook sdk
     * @param userID user id that should be checked as friend of current user
     * @param onCheckIsFriendCompleteListener listener for checking friend request
     */
    @Override
    public void requestCheckIsFriend(String userID, OnCheckIsFriendCompleteListener onCheckIsFriendCompleteListener) {
        throw new SocialNetworkException("requestCheckIsFriend isn't allowed for FacebookSocialNetwork");
    }

    /**
     * Get current user friends list
     * @param onRequestGetFriendsCompleteListener listener for getting list of current user friends
     */
	@Override
    public void requestGetFriends(OnRequestGetFriendsCompleteListener onRequestGetFriendsCompleteListener) {
        super.requestGetFriends(onRequestGetFriendsCompleteListener);
		final Session currentSession = mSessionTracker.getOpenSession();

        if (currentSession == null) {
            if (mLocalListeners.get(REQUEST_GET_FRIENDS) != null) {
                mLocalListeners.get(REQUEST_GET_FRIENDS).onError(getID(),
                        REQUEST_GET_FRIENDS, "Please login first", null);
            }

            return;
        }

        Request request = Request.newMyFriendsRequest(currentSession, new Request.GraphUserListCallback() {
            @Override
            public void onCompleted(List<GraphUser> users, Response response) {
                String[] ids = new String[users.size()];
                ArrayList<SocialPerson> socialPersons = new ArrayList<SocialPerson>();
                SocialPerson socialPerson = new SocialPerson();
                int i = 0;
                for(GraphUser user : users) {
                    getSocialPerson(socialPerson, user);
                    socialPersons.add(socialPerson);
                    socialPerson = new SocialPerson();
                    ids[i] = user.getId();
                    i++;
                }
                ((OnRequestGetFriendsCompleteListener) mLocalListeners.get(REQUEST_GET_FRIENDS))
                        .OnGetFriendsIdComplete(getID(), ids);
                ((OnRequestGetFriendsCompleteListener) mLocalListeners.get(REQUEST_GET_FRIENDS))
                        .OnGetFriendsComplete(getID(), socialPersons);
                mLocalListeners.remove(REQUEST_GET_FRIENDS);
            }
        });
        request.executeAsync();
    }

    /**
     * Not supported via Facebook sdk.
     * @throws com.github.gorbin.asne.core.SocialNetworkException
     * @param userID id of user that should be invited
     * @param onRequestAddFriendCompleteListener listener for invite result
     */
    @Override
    public void requestAddFriend(String userID, OnRequestAddFriendCompleteListener onRequestAddFriendCompleteListener) {
        throw new SocialNetworkException("requestAddFriend isn't allowed for FacebookSocialNetwork");
    }

    /**
     * Not supported via Facebook sdk.
     * @throws com.github.gorbin.asne.core.SocialNetworkException
     * @param userID user id that should be removed from friends
     * @param onRequestRemoveFriendCompleteListener listener to remove friend request response
     */
    @Override
    public void requestRemoveFriend(String userID, OnRequestRemoveFriendCompleteListener onRequestRemoveFriendCompleteListener) {
        throw new SocialNetworkException("requestRemoveFriend isn't allowed for FacebookSocialNetwork");
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {

        if (mSessionState == SessionState.OPENING && state == SessionState.OPENED) {
            if (mLocalListeners.get(REQUEST_LOGIN) != null) {
                ((OnLoginCompleteListener) mLocalListeners.get(REQUEST_LOGIN)).onLoginSuccess(getID());
                mLocalListeners.remove(REQUEST_LOGIN);
            }
        }

        if (state == SessionState.CLOSED_LOGIN_FAILED) {
            if (mLocalListeners.get(REQUEST_LOGIN) != null) {
                mLocalListeners.get(REQUEST_LOGIN).onError(getID(), REQUEST_LOGIN, exception.getMessage(), null);
                mLocalListeners.remove(REQUEST_LOGIN);
            }
        }

        mSessionState = state;

        if (mPendingAction != PendingAction.NONE &&
                (exception instanceof FacebookOperationCanceledException ||
                        exception instanceof FacebookAuthorizationException)) {
            mPendingAction = PendingAction.NONE;

            if (mLocalListeners.get(REQUEST_POST_MESSAGE) != null) {
                mLocalListeners.get(REQUEST_POST_MESSAGE).onError(getID(),
                        REQUEST_POST_MESSAGE, "permission not granted", null);
            }

            if (mLocalListeners.get(REQUEST_POST_PHOTO) != null) {
                mLocalListeners.get(REQUEST_POST_PHOTO).onError(getID(),
                        REQUEST_POST_PHOTO, "permission not granted", null);
            }

            if (mLocalListeners.get(REQUEST_POST_LINK) != null) {
                mLocalListeners.get(REQUEST_POST_LINK).onError(getID(),
                        REQUEST_POST_LINK, "permission not granted", null);
            }
        }

        if (session.isPermissionGranted(PERMISSION)
                && state == SessionState.OPENED_TOKEN_UPDATED) {
            handlePendingAction();
        }
    }

    /**
     * Overrided for connect facebook to activity
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUILifecycleHelper = new UiLifecycleHelper(mSocialNetworkManager.getActivity(), mSessionStatusCallback);
        mUILifecycleHelper.onCreate(savedInstanceState);

        initializeActiveSessionWithCachedToken(mSocialNetworkManager.getActivity());
        finishInit();
    }

    private boolean initializeActiveSessionWithCachedToken(Context context) {
        if (context == null) {
            return false;
        }

        Session session = Session.getActiveSession();
        if (session != null) {
            return session.isOpened();
        }

        mApplicationId = Utility.getMetadataApplicationId(context);
        return mApplicationId != null && Session.openActiveSessionFromCache(context) != null;

    }

    private void finishInit() {
        mSessionTracker = new SessionTracker(
                mSocialNetworkManager.getActivity(), mSessionStatusCallback, null, false);
    }

    /**
     * Overrided for facebook connect in resume activity
     */
    @Override
    public void onResume() {
        super.onResume();
        mUILifecycleHelper.onResume();
    }

    /**
     * Overrided for facebook connect in pause
     */
    @Override
    public void onPause() {
        super.onPause();
        mUILifecycleHelper.onPause();
    }

    /**
     * Overrided for destroying facebook session
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mUILifecycleHelper.onDestroy();
    }

    /**
     * Overrided for facebook
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mUILifecycleHelper.onSaveInstanceState(outState);
    }

    /**
     * Overrided for facebook
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mUILifecycleHelper.onActivityResult(requestCode, resultCode, data, null);

        Session session = Session.getActiveSession();
        int sanitizedRequestCode = requestCode % 0x10000;
        if (session != null) {
            session.onActivityResult(mSocialNetworkManager.getActivity(), sanitizedRequestCode, resultCode, data);
        }

        mUILifecycleHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                Log.e("Activity", String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
            }
        });
    }

    private void handlePendingAction() {
        PendingAction previouslyPendingAction = mPendingAction;
        // These actions may re-set pendingAction if they are still pending, but we assume they
        // will succeed.
        mPendingAction = PendingAction.NONE;

        switch (previouslyPendingAction) {
            case POST_PHOTO:
                postPhoto(mPhotoPath, mStatus);
                break;
            case POST_STATUS_UPDATE:
                postStatusUpdate(mStatus);
                break;
            case POST_LINK:
                postLink(mBundle);
                break;
        }
    }

    private void postStatusUpdate(String message) {
        if (isConnected() && Session.getActiveSession().isPermissionGranted(PERMISSION)){
            Request request = Request
                    .newStatusUpdateRequest(Session.getActiveSession(), message, null, null, new Request.Callback() {
                        @Override
                        public void onCompleted(Response response) {
                            publishSuccess(REQUEST_POST_MESSAGE,
                                    response.getError() == null ? null : response.getError().getErrorMessage());
                        }
                    });
            request.executeAsync();
        } else {
            mPendingAction = PendingAction.POST_STATUS_UPDATE;
        }
    }

    private void postPhoto(final String path, final String message) {
        if (Session.getActiveSession().isPermissionGranted(PERMISSION)){
            Bitmap image = BitmapFactory.decodeFile(path);
            Request request = Request.newUploadPhotoRequest(Session.getActiveSession(), image, new Request.Callback() {
                @Override
                public void onCompleted(Response response) {
                    publishSuccess(REQUEST_POST_PHOTO,
                            response.getError() == null ? null : response.getError().getErrorMessage());
                }
            });
            if(message != null && message.length()>0) {
                Bundle parameters = request.getParameters();
                parameters.putString("message", message);
                request.setParameters(parameters);
            }
            request.executeAsync();
        } else {
            mPendingAction = PendingAction.POST_PHOTO;
        }
    }

    private void postLink(final Bundle bundle) {
        if (Session.getActiveSession().isPermissionGranted(PERMISSION)){
            Request request = new Request(Session.getActiveSession(), "me/feed", bundle,
                    HttpMethod.POST, new Request.Callback(){
                @Override
                public void onCompleted(Response response) {
                    publishSuccess(REQUEST_POST_LINK,
                            response.getError() == null ? null : response.getError().getErrorMessage());
                }
            });
            request.executeAsync();
        } else {
            mPendingAction = PendingAction.POST_PHOTO;
        }
    }

    private void publishSuccess(String requestID, String error) {
        if (mLocalListeners.get(requestID) == null) return;

        if (error != null) {
            mLocalListeners.get(requestID).onError(getID(), requestID, error, null);
            return;
        }

        ((OnPostingCompleteListener) mLocalListeners.get(requestID)).onPostSuccessfully(getID());
        mLocalListeners.remove(requestID);
    }

    private enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE,
        POST_LINK
    }
}
