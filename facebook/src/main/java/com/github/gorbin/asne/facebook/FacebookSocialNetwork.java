/*******************************************************************************
 * Copyright (c) 2016 Evgeny Gorbin
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

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.internal.Utility;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
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

import com.facebook.FacebookSdk;
import com.facebook.FacebookException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class for Facebook social network integration
 *
 * @author Evgeny Gorbin (gorbin.e.o@gmail.com)
*/
public class FacebookSocialNetwork extends SocialNetwork {
    /*** Social network ID in asne modules, should be unique*/
    public static final int ID = 4;

    private static final String PERMISSION = "publish_actions";
    private Fragment fragment;
    private CallbackManager callbackManager;
    private com.github.gorbin.asne.core.AccessToken accessToken;
    private ShareDialog shareDialog;
    private String mPhotoPath;
    private String mStatus;
    private Bundle mBundle;
    private List<String> permissions;
    private PendingAction mPendingAction = PendingAction.NONE;
    private String requestID;
    private FacebookCallback<LoginResult> LoginCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            if (mLocalListeners.containsKey(REQUEST_LOGIN)) {
                ((OnLoginCompleteListener) mLocalListeners.get(REQUEST_LOGIN)).onLoginSuccess(getID());
                mLocalListeners.remove(REQUEST_LOGIN);
            }
            if(mPendingAction != PendingAction.NONE) {
                handlePendingAction();
            }
            accessToken = new com.github.gorbin.asne.core.AccessToken(loginResult.getAccessToken().getToken(), null);

        }

        @Override
        public void onCancel() {
            if (mLocalListeners.containsKey(REQUEST_LOGIN)) {
                mLocalListeners.get(REQUEST_LOGIN).onError(getID(), REQUEST_LOGIN, null, null);
                mLocalListeners.remove(REQUEST_LOGIN);
            }
            if(mPendingAction != PendingAction.NONE) {
                publishSuccess(requestID, "requestPermissions canceled");
            }

        }

        @Override
        public void onError(FacebookException exception) {

            if (mLocalListeners.containsKey(REQUEST_LOGIN)) {
                mLocalListeners.get(REQUEST_LOGIN).onError(getID(), REQUEST_LOGIN, exception.getMessage(), null);
                mLocalListeners.remove(REQUEST_LOGIN);
            }
            if(mPendingAction != PendingAction.NONE) {
                publishSuccess(requestID, exception.toString());
            }
        }
    };
    private FacebookCallback<Sharer.Result> ShareCallBack = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onSuccess(Sharer.Result result) {
            ((OnPostingCompleteListener) mLocalListeners.get(requestID)).onPostSuccessfully(getID());
            mLocalListeners.remove(requestID);
        }

        @Override
        public void onCancel() {
            mLocalListeners.get(requestID).onError(getID(), requestID, "ShareDialog canceled", null);
        }

        @Override
        public void onError(FacebookException error) {
            mLocalListeners.get(requestID).onError(getID(), requestID, error.getLocalizedMessage(), null);
        }
    };


    //TODO: refactor to use an init that is shared by constructors
    public FacebookSocialNetwork(Fragment fragment, ArrayList<String> permissions) {
        super(fragment);
        this.fragment = fragment;
        FacebookSdk.sdkInitialize(fragment.getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(fragment.getActivity());
        shareDialog.registerCallback(callbackManager, ShareCallBack);
        String applicationID = Utility.getMetadataApplicationId(fragment.getActivity());

        if (applicationID == null) {
            throw new IllegalStateException("applicationID can't be null\n" +
                    "Please check https://developers.facebook.com/docs/android/getting-started/");
        }
        this.permissions = permissions;
    }
//TODO
//    public FacebookSocialNetwork(Fragment fragment, Context context, ArrayList<String> permissions) {
//        super(fragment, context);
//        FacebookSdk.sdkInitialize(fragment.getActivity().getApplicationContext());
//        String applicationID = Utility.getMetadataApplicationId(context);
//
//        if (applicationID == null) {
//            throw new IllegalStateException("applicationID can't be null\n" +
//                    "Please check https://developers.facebook.com/docs/android/getting-started/");
//        }
//        this.permissions = permissions;
//    }

    /**
     * Check is social network connected
     * @return true if connected to Facebook social network and false if not
     */
    @Override
    public boolean isConnected() {
        return com.facebook.AccessToken.getCurrentAccessToken() != null;
    }

    /**
     * Make login request - authorize in Facebook social network
     * @param onLoginCompleteListener listener to trigger when Login complete
     */
    @Override
    public void requestLogin(OnLoginCompleteListener onLoginCompleteListener) {
        super.requestLogin(onLoginCompleteListener);
        LoginManager.getInstance().logInWithReadPermissions(fragment.getActivity(), permissions);
        LoginManager.getInstance().registerCallback(callbackManager, LoginCallback);
    }

    /**
     * Request {@link com.github.gorbin.asne.core.AccessToken} of Facebook social network that you can get from onRequestAccessTokenCompleteListener
     * @param onRequestAccessTokenCompleteListener listener for {@link com.github.gorbin.asne.core.AccessToken} request
     */
    @Override
    public void requestAccessToken(OnRequestAccessTokenCompleteListener onRequestAccessTokenCompleteListener) {
        super.requestAccessToken(onRequestAccessTokenCompleteListener);
        if(com.facebook.AccessToken.getCurrentAccessToken() != null) {
            accessToken = new com.github.gorbin.asne.core.AccessToken(com.facebook.AccessToken.getCurrentAccessToken().getToken(), null);
            ((OnRequestAccessTokenCompleteListener) mLocalListeners.get(REQUEST_ACCESS_TOKEN)).onRequestAccessTokenComplete(getID(), accessToken);
        }
    }

    /**
     * Logout from Facebook social network
     */
    @Override
    public void logout() {
        LoginManager.getInstance().logOut();
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
    public com.github.gorbin.asne.core.AccessToken getAccessToken() {
        if(com.facebook.AccessToken.getCurrentAccessToken() != null) {
            accessToken = new com.github.gorbin.asne.core.AccessToken(com.facebook.AccessToken.getCurrentAccessToken().getToken(), null);
        }
        return accessToken;
    }

    /**
     * Request current user {@link com.github.gorbin.asne.core.persons.SocialPerson}
     * @param onRequestSocialPersonCompleteListener listener for {@link com.github.gorbin.asne.core.persons.SocialPerson} request
     */
    @Override
    public void requestCurrentPerson(OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener) {
        super.requestCurrentPerson(onRequestSocialPersonCompleteListener);

        if (com.facebook.AccessToken.getCurrentAccessToken() == null) {
            if (mLocalListeners.get(REQUEST_GET_CURRENT_PERSON) != null) {
                mLocalListeners.get(REQUEST_GET_CURRENT_PERSON).onError(getID(),
                        REQUEST_GET_PERSON, "Please login first", null);
            }
            return;
        }

        GraphRequest request = GraphRequest.newMeRequest(
                com.facebook.AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject me, GraphResponse response) {
                        if (response.getError() != null) {
                            if (mLocalListeners.get(REQUEST_GET_CURRENT_PERSON) != null) {
                                mLocalListeners.get(REQUEST_GET_CURRENT_PERSON).onError(
                                        getID(), REQUEST_GET_CURRENT_PERSON, response.getError().getErrorMessage(), null);
                            }
                            return;
                        }
                        if (mLocalListeners.get(REQUEST_GET_CURRENT_PERSON) != null) {
                            SocialPerson socialPerson = new SocialPerson();
                                try {
                                    getSocialPerson(socialPerson, me);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    mLocalListeners.get(REQUEST_GET_CURRENT_PERSON).onError(
                                            getID(), REQUEST_GET_CURRENT_PERSON, e.getLocalizedMessage(), null);
                                }
                            ((OnRequestSocialPersonCompleteListener) mLocalListeners.get(REQUEST_GET_CURRENT_PERSON))
                                    .onRequestSocialPersonSuccess(getID(), socialPerson);
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,link");
        request.setParameters(parameters);
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

    //TODO set up right link
    /**
     * Request user {@link com.github.gorbin.asne.facebook.FacebookPerson} by userId - detailed user data
     * @param userId user id in social network
     * @param onRequestDetailedSocialPersonCompleteListener listener for request detailed social person
     */
    @Override
    public void requestDetailedSocialPerson(String userId, OnRequestDetailedSocialPersonCompleteListener onRequestDetailedSocialPersonCompleteListener) {
        super.requestDetailedSocialPerson(userId, onRequestDetailedSocialPersonCompleteListener);

        if(userId != null){
            throw new SocialNetworkException("requestDetailedSocialPerson isn't allowed for FacebookSocialNetwork");
        } else {
            if (com.facebook.AccessToken.getCurrentAccessToken() == null) {
                if (mLocalListeners.get(REQUEST_GET_DETAIL_PERSON) != null) {
                    mLocalListeners.get(REQUEST_GET_DETAIL_PERSON).onError(getID(),
                            REQUEST_GET_DETAIL_PERSON, "Please login first", null);
                }
                return;
            }

            GraphRequest request = GraphRequest.newMeRequest(
                    com.facebook.AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject me, GraphResponse response) {
                            if (response.getError() != null) {
                                if (mLocalListeners.get(REQUEST_GET_DETAIL_PERSON) != null) {
                                    mLocalListeners.get(REQUEST_GET_DETAIL_PERSON).onError(
                                            getID(), REQUEST_GET_DETAIL_PERSON, response.getError().getErrorMessage(), null);
                                }
                                return;
                            }
                            if (mLocalListeners.get(REQUEST_GET_DETAIL_PERSON) != null) {
                                FacebookPerson facebookPerson = new FacebookPerson();
                                try {
                                    getDetailedSocialPerson(facebookPerson, me);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    mLocalListeners.get(REQUEST_GET_DETAIL_PERSON).onError(
                                            getID(), REQUEST_GET_DETAIL_PERSON, e.getLocalizedMessage(), null);
                                }
                                ((OnRequestDetailedSocialPersonCompleteListener) mLocalListeners.get(REQUEST_GET_DETAIL_PERSON))
                                        .onRequestDetailedSocialPersonSuccess(getID(), facebookPerson);
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", " id,about,age_range,birthday,context,cover,currency,devices,education,email,favorite_athletes,favorite_teams,first_name,gender,hometown,inspirational_people,installed,install_type,is_verified,languages,last_name,link,locale,location,meeting_for,middle_name,name,name_format,political,quotes,payment_pricepoints,relationship_status,religion,security_settings,significant_other,sports,test_group,timezone,third_party_id,updated_time,verified,video_upload_limits,viewer_can_send_gift,website,work");
            request.setParameters(parameters);
            request.executeAsync();
        }
    }


    private SocialPerson getSocialPerson(SocialPerson socialPerson, JSONObject user) throws JSONException {

        if(user.has("id")) {
            socialPerson.id = user.getString("id");
            socialPerson.avatarURL = String.format("https://graph.facebook.com/%s/picture?type=large", user.getString("id"));
            if(user.has("link")) {
                socialPerson.profileURL = user.getString("link");
            } else {
                socialPerson.profileURL = String.format("https://www.facebook.com/", user.getString("id"));
            }
        }
        if(user.has("name")) {
            socialPerson.name = user.getString("name");
        }
        if(user.has("email")) {
            socialPerson.email = user.getString("email");
        }
        return socialPerson;
    }

    private FacebookPerson getDetailedSocialPerson(FacebookPerson facebookPerson, JSONObject user) throws JSONException {
        getSocialPerson(facebookPerson, user);
        if(user.has("first_name")) {
            facebookPerson.firstName = user.getString("first_name");
        }
        if(user.has("middle_name")) {
            facebookPerson.middleName = user.getString("middle_name");
        }
        if(user.has("last_name")) {
            facebookPerson.lastName = user.getString("last_name");
        }
        if(user.has("gender")) {
            facebookPerson.gender = user.getString("gender");
        }
        if(user.has("birthday")) {
            facebookPerson.birthday = user.getString("birthday");
        }
        if(user.has("verified")) {
            facebookPerson.verified = user.getString("verified");
        }
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
        requestID = REQUEST_POST_MESSAGE;
        performPublish(PendingAction.POST_STATUS_UPDATE);
        postStatusUpdate(mStatus);
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
        requestID = REQUEST_POST_PHOTO;
        performPublish(PendingAction.POST_PHOTO);
        postPhoto(mPhotoPath, message);
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
        requestID = REQUEST_POST_LINK;
        performPublish(PendingAction.POST_LINK);
        postLink(mBundle);
    }

    /**
     * Request facebook share dialog
     * @param bundle bundle containing information that should be shared(Bundle constants in {@link com.github.gorbin.asne.core.SocialNetwork})
     * @param onPostingCompleteListener listener for posting request
     */
    @Override
    public void requestPostDialog(Bundle bundle, OnPostingCompleteListener onPostingCompleteListener) {
        super.requestPostDialog(bundle, onPostingCompleteListener);
        Uri link = null;
        Uri pictureLink = null;
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            if (bundle.getString(BUNDLE_LINK) != null) {
                link = Uri.parse(bundle.getString(BUNDLE_LINK));
            } else {
                Log.e("FaceboolSocialNetwork:", "requestPostDialog required URL to share!");
            }
            if (bundle.getString(BUNDLE_PICTURE) != null) {
                pictureLink = Uri.parse(bundle.getString(BUNDLE_PICTURE));
            }
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(bundle.getString(BUNDLE_NAME))
                    .setContentDescription(bundle.getString(BUNDLE_MESSAGE))
                    .setContentUrl(link)
                    .setImageUrl(pictureLink)
                    .build();
            shareDialog.show(linkContent);
        } else {
            mLocalListeners.get(REQUEST_POST_DIALOG).onError(
                    getID(), REQUEST_POST_DIALOG, "Can't show share dialog, check login or permissions", null);
        }
    }

    private void performPublish(PendingAction action) {
        if(com.facebook.AccessToken.getCurrentAccessToken() != null) {
            mPendingAction = action;
        } else {
            mLocalListeners.get(requestID).onError(getID(),
                    requestID, "User should be logged first", null);
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
    //TODO: Pagination
    @Override
    public void requestGetFriends(OnRequestGetFriendsCompleteListener onRequestGetFriendsCompleteListener) {
        super.requestGetFriends(onRequestGetFriendsCompleteListener);

        if (com.facebook.AccessToken.getCurrentAccessToken() == null) {
            if (mLocalListeners.get(REQUEST_GET_CURRENT_PERSON) != null) {
                mLocalListeners.get(REQUEST_GET_CURRENT_PERSON).onError(getID(),
                        REQUEST_GET_PERSON, "Please login first", null);
            }
            return;
        }

        GraphRequest request = GraphRequest.newMyFriendsRequest(
                com.facebook.AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray list, GraphResponse response) {
                        if (response.getError() != null) {
                            if (mLocalListeners.get(REQUEST_GET_FRIENDS) != null) {
                                mLocalListeners.get(REQUEST_GET_FRIENDS).onError(
                                        getID(), REQUEST_GET_FRIENDS, response.getError().getErrorMessage(), null);
                            }
                            return;
                        }
                        if (mLocalListeners.get(REQUEST_GET_FRIENDS) != null) {
                            String[] ids = new String[list.length()];
                            ArrayList<SocialPerson> socialPersons = new ArrayList<>();
                            try {
                                SocialPerson socialPerson = new SocialPerson();
                                for(int i = 0; i < list.length(); i++) {
                                    getSocialPerson(socialPerson, list.getJSONObject(i));
                                    socialPersons.add(socialPerson);
                                    socialPerson = new SocialPerson();
                                    if(list.getJSONObject(i).has("id")) {
                                        ids[i] = list.getJSONObject(i).getString("id");
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                mLocalListeners.get(REQUEST_GET_FRIENDS).onError(
                                        getID(), REQUEST_GET_FRIENDS, e.getLocalizedMessage(), null);
                            }
                            ((OnRequestGetFriendsCompleteListener) mLocalListeners.get(REQUEST_GET_FRIENDS))
                                    .onGetFriendsIdComplete(getID(), ids);
                            ((OnRequestGetFriendsCompleteListener) mLocalListeners.get(REQUEST_GET_FRIENDS))
                                    .onGetFriendsComplete(getID(), socialPersons);
                            mLocalListeners.remove(REQUEST_GET_FRIENDS);
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,link");
        request.setParameters(parameters);
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

    /**
     * Overrided for facebook
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
        ShareLinkContent content = new ShareLinkContent.Builder()
                .build();

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            shareDialog.show(content);
        } else {
            if(com.facebook.AccessToken.getCurrentAccessToken().getPermissions().contains(PERMISSION)){
                ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Log.v("FACEBOOK_TEST", "share api success");
                        publishSuccess(REQUEST_POST_MESSAGE, null);
                    }

                    @Override
                    public void onCancel() {
                        Log.v("FACEBOOK_TEST", "share api cancel");
                        publishSuccess(REQUEST_POST_MESSAGE, "postRequestMessage canceled");
                    }

                    @Override
                    public void onError(FacebookException e) {
                        Log.v("FACEBOOK_TEST", "share api error " + e);
                        publishSuccess(REQUEST_POST_MESSAGE, e.toString());
                    }
                });
            } else {
                LoginManager.getInstance().logInWithPublishPermissions(
                        fragment.getActivity(), Collections.singletonList(PERMISSION));//Arrays.asList("publish_actions"));
            }
        }
    }

    private void postPhoto(final String path, final String message) {
        Bitmap image = BitmapFactory.decodeFile(path);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .setCaption(message)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        if (ShareDialog.canShow(SharePhotoContent.class)) {
            shareDialog.show(content);
        } else {
            if(com.facebook.AccessToken.getCurrentAccessToken().getPermissions().contains(PERMISSION)){
                ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Log.v("FACEBOOK_TEST", "share api success");
                        publishSuccess(REQUEST_POST_PHOTO, null);
                    }

                    @Override
                    public void onCancel() {
                        Log.v("FACEBOOK_TEST", "share api cancel");
                        publishSuccess(REQUEST_POST_PHOTO, "postRequestPhoto canceled");
                    }

                    @Override
                    public void onError(FacebookException e) {
                        Log.v("FACEBOOK_TEST", "share api error " + e);
                        publishSuccess(REQUEST_POST_PHOTO, e.toString());
                    }
                });
            } else {
                LoginManager.getInstance().logInWithPublishPermissions(
                        fragment.getActivity(), Collections.singletonList(PERMISSION)); //Arrays.asList("publish_actions"));
            }
        }
    }

    private void postLink(final Bundle bundle) {

//        JSONObject object = new JSONObject();
//        try {
//            object.put("message", "wat");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        GraphRequest request =  GraphRequest.newPostRequest(com.facebook.AccessToken.getCurrentAccessToken(), "me/feed",
//                object, new GraphRequest.Callback(){
//
//                    @Override
//                    public void onCompleted(GraphResponse response) {
//                        Log.v("wat", response.toString());
//                    }
//                });
//        request.executeAsync();

        Uri link = null;
        Uri pictureLink = null;
        if (bundle.getString(BUNDLE_LINK) != null) {
            link = Uri.parse(bundle.getString(BUNDLE_LINK));
        } else {
            Log.e("FaceboolSocialNetwork:", "requestPostLink required URL to share!");
            publishSuccess(REQUEST_POST_LINK, "postRequestLink required URL to share!");
        }
        if (bundle.getString(BUNDLE_PICTURE) != null) {
            pictureLink = Uri.parse(bundle.getString(BUNDLE_PICTURE));
        }
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentTitle(bundle.getString(BUNDLE_NAME))
                .setContentDescription(bundle.getString(BUNDLE_MESSAGE))
                .setContentUrl(link)
                .setImageUrl(pictureLink)
                .build();

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            shareDialog.show(content);
        } else {
            if(com.facebook.AccessToken.getCurrentAccessToken().getPermissions().contains(PERMISSION)){
                ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Log.v("FACEBOOK_TEST", "share api success");
                        publishSuccess(REQUEST_POST_LINK, null);
                    }

                    @Override
                    public void onCancel() {
                        Log.v("FACEBOOK_TEST", "share api cancel");
                        publishSuccess(REQUEST_POST_LINK, "postRequestLink canceled");
                    }

                    @Override
                    public void onError(FacebookException e) {
                        Log.v("FACEBOOK_TEST", "share api error " + e);
                        publishSuccess(REQUEST_POST_LINK, e.toString());
                    }
                });
            } else {
                LoginManager.getInstance().logInWithPublishPermissions(
                        fragment.getActivity(), Collections.singletonList("publish_actions"));
            }
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
