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
package com.github.gorbin.asne.vk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

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
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiLink;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKPhotoArray;
import com.vk.sdk.api.model.VKWallPostResult;
import com.vk.sdk.api.photo.VKImageParameters;
import com.vk.sdk.api.photo.VKUploadImage;
import com.vk.sdk.dialogs.VKCaptchaDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class for VK social network integration
 *
 * @author Evgeny Gorbin (gorbin.e.o@gmail.com)
 */
public class VkSocialNetwork extends SocialNetwork {
    /*** Social network ID in asne modules, should be unique*/
    public static final int ID = 5;
    private static final String SAVE_STATE_KEY_OAUTH_TOKEN = "VkSocialNetwork.SAVE_STATE_KEY_OAUTH_TOKEN";
    private static final String SAVE_STATE_KEY_OAUTH_SECRET = "VkSocialNetwork.SAVE_STATE_KEY_OAUTH_SECRET";
    private static final String SAVE_STATE_KEY_USER_ID = "VkSocialNetwork.SAVE_STATE_KEY_USER_ID";
    /*** Developer activity*/
    private Activity activity;
    /*** VK app id*/
    private String key;
    /*** VK access token*/
    private VKAccessToken accessToken;
    /*** Id of current user*/
    private String userId;
    /*** Permissions array*/
    private String[] permissions;
    /*** VK SDK listener to catch authorization @see <a href="http://vkcom.github.io/vk-android-sdk/com/vk/sdk/VKSdkListener.html">VKSdkListener</a>*/
    private final VKSdkListener vkSdkListener = new VKSdkListener() {
        @Override
        public void onCaptchaError(VKError captchaError) {
            new VKCaptchaDialog(captchaError).show();
        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {
            VKSdk.authorize(permissions, true, false);
        }

        @Override
        public void onAccessDenied(VKError authorizationError) {
            mLocalListeners.get(REQUEST_LOGIN).onError(getID(), REQUEST_LOGIN,
                    authorizationError.toString(), null);
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            accessToken = newToken;
            mSharedPreferences.edit()
                    .putString(SAVE_STATE_KEY_OAUTH_TOKEN, accessToken.accessToken)
                    .putString(SAVE_STATE_KEY_OAUTH_SECRET, accessToken.secret)
                    .putString(SAVE_STATE_KEY_USER_ID, accessToken.userId)
                    .apply();
            if (mLocalListeners.get(REQUEST_LOGIN) != null) {
                ((OnLoginCompleteListener) mLocalListeners.get(REQUEST_LOGIN)).onLoginSuccess(getID());
                mLocalListeners.remove(REQUEST_LOGIN);
            }
            requestIdPerson();
        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            accessToken = token;
            mSharedPreferences.edit()
                    .putString(SAVE_STATE_KEY_OAUTH_TOKEN, accessToken.accessToken)
                    .putString(SAVE_STATE_KEY_OAUTH_SECRET, accessToken.secret)
                    .putString(SAVE_STATE_KEY_USER_ID, accessToken.userId)
                    .apply();
            requestIdPerson();
        }
    };

    public VkSocialNetwork(Fragment fragment, String key, String[] permissions) {
        super(fragment);
        this.key = key;
        this.permissions = permissions;
    }

    private static boolean stringToBool(String s) {
        if (s.equals("1"))
            return true;
        if (s.equals("0"))
            return false;
        throw new IllegalArgumentException(s+" is not a bool. Only 1 and 0 are.");
    }

    /*** Get current user id after authorization for inner use*/
    private void requestIdPerson() {
        VKRequest request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS,"id"));
        request.secure = false;
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                try {
                    JSONObject jsonResponse = response.json.getJSONArray("response").getJSONObject(0);
                    userId = jsonResponse.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(VKError error) {
                throw new SocialNetworkException("Error in id request! " + error);
            }
            @Override
            public void onProgress(VKRequest.VKProgressType progressType,
                                   long bytesLoaded,
                                   long bytesTotal) {
            }
            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                throw new SocialNetworkException("Fail in id request!");
            }
        });
    }

    /**
     * Check is social network connected
     * @return true if connected to VK social network and false if not
     */
    @Override
    public boolean isConnected() {
        return VKSdk.isLoggedIn();
    }

    /**
     * Make login request - authorize in VK social network
     * @param onLoginCompleteListener listener to trigger when Login complete
     */
    @Override
    public void requestLogin(OnLoginCompleteListener onLoginCompleteListener) {
        super.requestLogin(onLoginCompleteListener);
        VKSdk.authorize(permissions);
    }

    /**
     * Logout from VK social network
     */
    @Override
    public void logout() {
        VKSdk.logout();
    }

    /**
     * Get id of VK social network
     * @return Social network id for VK = 5
     */
    @Override
    public int getID() {
        return ID;
    }

    /**
     * Method to get AccessToken of VK social network
     * @return {@link com.github.gorbin.asne.core.AccessToken}
     */
    @Override
    public AccessToken getAccessToken() {
        return new AccessToken(mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null),
                mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_SECRET, null));
    }

    /**
     * Request {@link com.github.gorbin.asne.core.AccessToken} of VK social network that you can get from onRequestAccessTokenCompleteListener
     * @param onRequestAccessTokenCompleteListener listener for {@link com.github.gorbin.asne.core.AccessToken} request
     */
    @Override
    public void requestAccessToken(OnRequestAccessTokenCompleteListener onRequestAccessTokenCompleteListener) {
        super.requestAccessToken(onRequestAccessTokenCompleteListener);
        ((OnRequestAccessTokenCompleteListener) mLocalListeners.get(REQUEST_ACCESS_TOKEN))
                .onRequestAccessTokenComplete(getID(),
                        new AccessToken(mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_TOKEN, null),
                        mSharedPreferences.getString(SAVE_STATE_KEY_OAUTH_SECRET, null)));
    }

    /**
     * Request current user {@link com.github.gorbin.asne.core.persons.SocialPerson}
     * @param onRequestSocialPersonCompleteListener listener for {@link com.github.gorbin.asne.core.persons.SocialPerson} request
     */
    @Override
    public void requestCurrentPerson(OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener) {
        super.requestCurrentPerson(onRequestSocialPersonCompleteListener);
        requestSocialPerson(null, onRequestSocialPersonCompleteListener);
    }

    /**
     * Request {@link com.github.gorbin.asne.core.persons.SocialPerson} by user id
     * @param userID id of VK user
     * @param onRequestSocialPersonCompleteListener listener for {@link com.github.gorbin.asne.core.persons.SocialPerson} request
     */
    @Override
    public void requestSocialPerson(String userID, OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener) {
        super.requestSocialPerson(userID, onRequestSocialPersonCompleteListener);
        VKRequest request;
        final boolean current;
        if(userID == null){
            request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS,
                    "id,first_name,last_name,photo_max_orig"
            ));
            current = true;
        } else {
            request = VKApi.users().get(VKParameters.from(VKApiConst.USER_IDS, userID, VKApiConst.FIELDS,
                    "id,first_name,last_name,photo_max_orig"
            ));
            current = false;
        }
        request.secure = false;
        request.useSystemLanguage = true;
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                SocialPerson socialPerson = new SocialPerson();
                try {
                    JSONObject jsonResponse = response.json.getJSONArray("response").getJSONObject(0);
                    getSocialPerson(socialPerson, jsonResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(current){
                    ((OnRequestSocialPersonCompleteListener) mLocalListeners.get(REQUEST_GET_CURRENT_PERSON))
                            .onRequestSocialPersonSuccess(getID(), socialPerson);
                } else {
                    ((OnRequestSocialPersonCompleteListener) mLocalListeners.get(REQUEST_GET_PERSON))
                        .onRequestSocialPersonSuccess(getID(), socialPerson);
                }
            }
            @Override
            public void onError(VKError error) {
                throw new SocialNetworkException("Error in person request! " + error);
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                throw new SocialNetworkException("Fail in attempt person request!");
            }
        });
    }

    /**
     * Request ArrayList of {@link com.github.gorbin.asne.core.persons.SocialPerson} by array of userIds
     * @param userID array of VK users id
     * @param onRequestSocialPersonsCompleteListener listener for array of {@link com.github.gorbin.asne.core.persons.SocialPerson} request
     */
    @Override
    public void requestSocialPersons(String[] userID, OnRequestSocialPersonsCompleteListener onRequestSocialPersonsCompleteListener) {
        super.requestSocialPersons(userID, onRequestSocialPersonsCompleteListener);
        String userIds = TextUtils.join(",", userID);
        VKRequest request = VKApi.users().get(VKParameters.from(VKApiConst.USER_IDS, userIds, VKApiConst.FIELDS,
                "id,first_name,last_name,photo_max_orig"
        ));
        request.secure = false;
        request.useSystemLanguage = true;
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                SocialPerson socialPerson = new SocialPerson();
                ArrayList<SocialPerson> socialPersons = new ArrayList<SocialPerson>();
                try {

                    JSONArray jsonArray = response.json.getJSONArray("response");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        getSocialPerson(socialPerson, jsonArray.getJSONObject(i));
                        socialPersons.add(socialPerson);
                        socialPerson = new SocialPerson();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ((OnRequestSocialPersonsCompleteListener) mLocalListeners.get(REQUEST_GET_PERSONS))
                        .onRequestSocialPersonsSuccess(getID(), socialPersons);
                mLocalListeners.remove(REQUEST_GET_PERSONS);
            }
            @Override
            public void onError(VKError error) {
                mLocalListeners.remove(REQUEST_GET_PERSONS);
                throw new SocialNetworkException("Error in person request! " + error);
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                mLocalListeners.remove(REQUEST_GET_PERSONS);
                throw new SocialNetworkException("Fail in attempt person request!");
            }
        });
    }

    /**
     * Request user {@link com.github.gorbin.asne.vk.VKPerson} by userId - detailed user data
     * @param userId id of VK user
     * @param onRequestDetailedSocialPersonCompleteListener listener for {@link com.github.gorbin.asne.vk.VKPerson} request
     */
    @Override
    public void requestDetailedSocialPerson(String userId, OnRequestDetailedSocialPersonCompleteListener onRequestDetailedSocialPersonCompleteListener) {
        super.requestDetailedSocialPerson(userId, onRequestDetailedSocialPersonCompleteListener);
        VKRequest request;
        if(userId == null){
            request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS,
                    "id,first_name,last_name,photo_max_orig,contacts,sex,bdate,city,country," +
                            "photo_max_orig,online,screen_name,has_mobile,education,can_post," +
                            "can_see_all_posts,can_write_private_message,status"
            ));
        } else {
            request = VKApi.users().get(VKParameters.from(VKApiConst.USER_IDS, userId, VKApiConst.FIELDS,
                "id,first_name,last_name,photo_max_orig,contacts,sex,bdate,city,country," +
                "photo_max_orig,online,screen_name,has_mobile,education,can_post," +
                "can_see_all_posts,can_write_private_message,status"
        ));
        }
        request.secure = false;
        request.useSystemLanguage = true;
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                VKPerson vkPerson = new VKPerson();
                try {
                    JSONObject jsonResponse = response.json.getJSONArray("response").getJSONObject(0);
                    getDetailedSocialPerson(vkPerson, jsonResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ((OnRequestDetailedSocialPersonCompleteListener) mLocalListeners.get(REQUEST_GET_DETAIL_PERSON))
                        .onRequestDetailedSocialPersonSuccess(getID(), vkPerson);
            }

            @Override
            public void onError(VKError error) {
                throw new SocialNetworkException("Error in detailed person request! " + error);
            }

            @Override
            public void onProgress(VKRequest.VKProgressType progressType,
                                   long bytesLoaded,
                                   long bytesTotal) {
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                throw new SocialNetworkException("Fail detailed person request!");
            }
        });
    }

    /**
     * Get {@link com.github.gorbin.asne.core.persons.SocialPerson} from JSON response of VK
     * @param socialPerson object that would be filled
     * @param jsonResponse VK response
     * @return filled {@link com.github.gorbin.asne.core.persons.SocialPerson}
     * @throws JSONException
     */
    private SocialPerson getSocialPerson(SocialPerson socialPerson, JSONObject jsonResponse) throws JSONException {
        String firstName = null;
        String lastName = null;
        if(jsonResponse.has("id")) {
            socialPerson.id = jsonResponse.getString("id");
            socialPerson.profileURL = "http://vk.com/id" + jsonResponse.getString("id");
        }
        if(jsonResponse.has("first_name")) {
            firstName = jsonResponse.getString("first_name");
        }
        if(jsonResponse.has("last_name")) {
            lastName = jsonResponse.getString("last_name");
        }
        socialPerson.name = firstName + " " + lastName;
        if (jsonResponse.has("photo_max_orig")) {
            socialPerson.avatarURL = jsonResponse.getString("photo_max_orig");
        }
        return socialPerson;
    }

    /**
     * Get {@link com.github.gorbin.asne.vk.VKPerson} from JSON response of VK
     * @param vkPerson object that would be filled
     * @param jsonResponse VK response
     * @return filled {@link com.github.gorbin.asne.vk.VKPerson}
     * @throws JSONException
     */
    private VKPerson getDetailedSocialPerson(VKPerson vkPerson, JSONObject jsonResponse) throws JSONException {
        getSocialPerson(vkPerson, jsonResponse);
        if(jsonResponse.has("sex")) {
            vkPerson.sex = Integer.parseInt(jsonResponse.getString("sex"));
        }
        if(jsonResponse.has("bdate")) {
            vkPerson.birthday = jsonResponse.getString("bdate");
        }
        if((jsonResponse.has("city"))&&(jsonResponse.getJSONObject("city").has("title"))) {
            vkPerson.city = jsonResponse.getJSONObject("city").getString("title");
        }
        if((jsonResponse.has("country"))&&(jsonResponse.getJSONObject("country").has("title"))) {
            vkPerson.country = jsonResponse.getJSONObject("country").getString("title");
        }
        if(jsonResponse.has("photo_max_orig")) {
            vkPerson.photoMaxOrig = jsonResponse.getString("photo_max_orig");
        }
        if(jsonResponse.has("online")) {
            vkPerson.online = stringToBool(jsonResponse.getString("online"));
        }
        if(jsonResponse.has("screen_name")) {
            vkPerson.username = jsonResponse.getString("screen_name");
        }
        if(jsonResponse.has("has_mobile")) {
            vkPerson.hasMobile = stringToBool(jsonResponse.getString("has_mobile"));
        }
        if(jsonResponse.has("mobile_phone")) {
            vkPerson.mobilePhone = jsonResponse.getString("mobile_phone");
        }
        if(jsonResponse.has("home_phone")) {
            vkPerson.homePhone = jsonResponse.getString("home_phone");
        }
        if(jsonResponse.has("university_name")) {
            vkPerson.universityName = jsonResponse.getString("university_name");
        }
        if(jsonResponse.has("faculty_name")) {
            vkPerson.facultyName = jsonResponse.getString("faculty_name");
        }
        if(jsonResponse.has("graduation")) {
            vkPerson.graduationYear = jsonResponse.getString("graduation");
        }
        if(jsonResponse.has("status")) {
            vkPerson.status = jsonResponse.getString("status");
        }
        if(jsonResponse.has("can_post")) {
            vkPerson.canPost = stringToBool(jsonResponse.getString("can_post"));
        }
        if(jsonResponse.has("can_see_all_posts")) {
            vkPerson.canSeeAllPosts = stringToBool(jsonResponse.getString("can_see_all_posts"));
        }
        if(jsonResponse.has("can_write_private_message")) {
            vkPerson.canWritePrivateMessage = stringToBool(jsonResponse.getString("can_write_private_message"));
        }
        return vkPerson;
    }

    /**
     * Post message to social network
     * @param message message that should be shared
     * @param onPostingCompleteListener listener for posting request
     */
    @Override
    public void requestPostMessage(String message, OnPostingCompleteListener onPostingCompleteListener) {
        super.requestPostMessage(message, onPostingCompleteListener);
        makePost(null, message, REQUEST_POST_MESSAGE);
    }

    /**
     * Post photo to social network
     * @param photo photo that should be shared
     * @param message message that should be shared with photo
     * @param onPostingCompleteListener listener for posting request
     */
	@Override
    public void requestPostPhoto(File photo, final String message, OnPostingCompleteListener onPostingCompleteListener) {
        super.requestPostPhoto(photo, message, onPostingCompleteListener);
        final Bitmap vkPhoto = getPhoto(photo);
        VKRequest request = VKApi.uploadWallPhotoRequest(new VKUploadImage(vkPhoto, VKImageParameters.pngImage()), 0, Integer.parseInt(userId));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                VKApiPhoto photoModel = ((VKPhotoArray) response.parsedModel).get(0);
                makePost(new VKAttachments(photoModel), message, REQUEST_POST_PHOTO);
                vkPhoto.recycle();
            }
            @Override
            public void onError(VKError error) {
                throw new SocialNetworkException("Error in posting! " + error);
            }
        });
    }
    
	private Bitmap getPhoto(File photo) {
        Bitmap b = null;
        try {
            b = BitmapFactory.decodeStream(new FileInputStream(photo));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
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
        VKApiLink vkLink = new VKApiLink();
        String link = bundle.getString(BUNDLE_LINK);
        if((link != null) && (link.length() != 0)) vkLink.url = link;
        String name = bundle.getString(BUNDLE_NAME);
        if((name != null) && (name.length() != 0)) vkLink.title = name;
        String description = bundle.getString(BUNDLE_MESSAGE);
        if((description != null) && (description.length() != 0)) vkLink.description = description;
        String picture = bundle.getString(BUNDLE_PICTURE);
        if((picture != null) && (picture.length() != 0)) vkLink.image_src = picture;

        VKAttachments attachments = new VKAttachments();
        attachments.add(vkLink);
        makePost(attachments, message, REQUEST_POST_LINK);
    }

    /**
     * Not supported via vk sdk - in development
     * @throws SocialNetworkException
     * @param bundle bundle containing information that should be shared(Bundle constants in {@link com.github.gorbin.asne.core.SocialNetwork})
     * @param onPostingCompleteListener listener for posting request
     */
    @Override
    public void requestPostDialog(Bundle bundle, OnPostingCompleteListener onPostingCompleteListener) {
        throw new SocialNetworkException("requestPostDialog isn't allowed for VKSocialNetwork");
    }
   
	private void makePost(VKAttachments attachments, final String message, final String requestID) {
        VKRequest post = VKApi.wall().post(VKParameters.from(VKApiConst.ATTACHMENTS, attachments, VKApiConst.MESSAGE, message));
        post.setModelClass(VKWallPostResult.class);
        post.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                ((OnPostingCompleteListener) mLocalListeners.get(requestID)).onPostSuccessfully(getID());
            }

            @Override
            public void onError(VKError error) {
                mLocalListeners.get(requestID).onError(getID(), requestID, error.toString(), null);
                throw new SocialNetworkException("Error in posting! " + error);
            }
        });
    }

    /**
     * Check if user by id is friend of current user
     * @param userID user id that should be checked as friend of current user
     * @param onCheckIsFriendCompleteListener listener for checking friend request
     */
    @Override
    public void requestCheckIsFriend(final String userID, OnCheckIsFriendCompleteListener onCheckIsFriendCompleteListener) {
        super.requestCheckIsFriend(userID, onCheckIsFriendCompleteListener);
        VKRequest request = VKApi.friends().areFriends(VKParameters.from(VKApiConst.USER_IDS, userID, VKApiConst.FIELDS,
                "id"));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                boolean isFriend = false;
                JSONObject jsonResponse;
                try {
                    jsonResponse = response.json.getJSONArray("response").getJSONObject(0);
                    int friendStatus2 = jsonResponse.getInt("friend_status");
                    switch(friendStatus2) {
                        case 0:
                            isFriend = false;
                            break;
                        case 1:
                            isFriend = false;
                            break;
                        case 2:
                            isFriend = false;
                            break;
                        case 3:
                            isFriend = true;
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ((OnCheckIsFriendCompleteListener) mLocalListeners.get(REQUEST_CHECK_IS_FRIEND))
                        .onCheckIsFriendComplete(
                                getID(),
                                userID,
                                isFriend
                        );
                mLocalListeners.remove(REQUEST_CHECK_IS_FRIEND);
            }
            @Override
            public void onError(VKError error) {
                throw new SocialNetworkException("Error in getting friends! " + error);
            }
        });

    }

    /**
     * Get current user friends list
     * @param onRequestGetFriendsCompleteListener listener for getting list of current user friends
     */
	@Override
    public void requestGetFriends(OnRequestGetFriendsCompleteListener onRequestGetFriendsCompleteListener) {
        super.requestGetFriends(onRequestGetFriendsCompleteListener);
        VKRequest request = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS,
                "id,first_name,last_name,photo_max_orig"));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                JSONObject jsonResponse;
                String[] ids = new String[0];
                SocialPerson socialPerson = new SocialPerson();
                ArrayList<SocialPerson> socialPersons = new ArrayList<SocialPerson>();
                try {
                    jsonResponse = response.json.getJSONObject("response");
                    JSONArray jsonArray = jsonResponse.getJSONArray("items");
                    ids = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ids[i] = jsonArray.getJSONObject(i).getString("id");
                        getSocialPerson(socialPerson, jsonArray.getJSONObject(i));
                        socialPersons.add(socialPerson);
                        socialPerson = new SocialPerson();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ((OnRequestGetFriendsCompleteListener) mLocalListeners.get(REQUEST_GET_FRIENDS))
                    .OnGetFriendsIdComplete(getID(), ids);
                ((OnRequestGetFriendsCompleteListener) mLocalListeners.get(REQUEST_GET_FRIENDS))
                        .OnGetFriendsComplete(getID(), socialPersons);
                mLocalListeners.remove(REQUEST_GET_FRIENDS);
            }
            @Override
            public void onError(VKError error) {
                throw new SocialNetworkException("Error in getting friends! " + error);
            }
        });
    }

    /**
     * Invite friend by id to current user
     * @param userID id of user that should be invited
     * @param onRequestAddFriendCompleteListener listener for invite result
     */
    @Override
    public void requestAddFriend(final String userID, OnRequestAddFriendCompleteListener onRequestAddFriendCompleteListener) {
        super.requestAddFriend(userID, onRequestAddFriendCompleteListener);
        VKRequest request = VKApi.friends().add(VKParameters.from(VKApiConst.USER_ID, userID));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                ((OnRequestAddFriendCompleteListener) mLocalListeners.get(REQUEST_ADD_FRIEND))
                        .onRequestAddFriendComplete(
                                getID(),
                                userID
                        );
                mLocalListeners.remove(REQUEST_ADD_FRIEND);
            }
            @Override
            public void onError(VKError error) {
                throw new SocialNetworkException("Error in getting friends! " + error);
            }
        });
    }

    /**
     * Remove friend by id from current user friends
     * @param userID user id that should be removed from friends
     * @param onRequestRemoveFriendCompleteListener listener to remove friend request response
     */
    @Override
    public void requestRemoveFriend(final String userID, OnRequestRemoveFriendCompleteListener onRequestRemoveFriendCompleteListener) {
        super.requestRemoveFriend(userID, onRequestRemoveFriendCompleteListener);
        VKRequest request = VKApi.friends().delete(VKParameters.from(VKApiConst.USER_ID, userID));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                ((OnRequestRemoveFriendCompleteListener) mLocalListeners.get(REQUEST_REMOVE_FRIEND))
                        .onRequestRemoveFriendComplete(
                                getID(),
                                userID
                        );
                mLocalListeners.remove(REQUEST_REMOVE_FRIEND);
            }
            @Override
            public void onError(VKError error) {
                throw new SocialNetworkException("Error in getting friends! " + error);
            }
        });
    }

    /**
     * Overrided for connect vk to activity
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = mSocialNetworkManager.getActivity();
        VKUIHelper.onCreate(activity);
        VKSdk.initialize(vkSdkListener, key);
        VKSdk.wakeUpSession();
        if(isConnected()) {
            requestIdPerson();
        }
    }

    /**
     * Overrided for VK support
     */
    @Override
    public void onResume() {
        super.onResume();
        VKUIHelper.onResume(activity);
    }

    /**
     * Overrided for VK support
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(activity);
    }

    /**
     * Overrided for VK support
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int sanitizedRequestCode = requestCode % 0x10000;
        VKUIHelper.onActivityResult(sanitizedRequestCode, resultCode, data);
    }
}
