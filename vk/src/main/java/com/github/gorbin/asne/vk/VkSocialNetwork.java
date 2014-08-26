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

public class VkSocialNetwork extends SocialNetwork {
    public static final int ID = 5;
    private Activity activity;
    private String key;
    private VKAccessToken accessToken;
    private String userId;
    private String[] permissions;
    public VkSocialNetwork(Fragment fragment, String key, String[] permissions) {
        super(fragment);
        this.key = key;
        this.permissions = permissions;
    }

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
            new AlertDialog.Builder(VKUIHelper.getTopActivity())
                    .setMessage(authorizationError.toString())
                    .show();
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            accessToken = newToken;
            if (mLocalListeners.get(REQUEST_LOGIN) != null) {
                ((OnLoginCompleteListener) mLocalListeners.get(REQUEST_LOGIN)).onLoginSuccess(getID());
                mLocalListeners.remove(REQUEST_LOGIN);
            }
            requestIdPerson();
        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            accessToken = token;
            requestIdPerson();
        }
    };

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

    @Override
    public boolean isConnected() {
        return VKSdk.isLoggedIn();
    }

    @Override
    public void requestLogin(OnLoginCompleteListener onLoginCompleteListener) {
        super.requestLogin(onLoginCompleteListener);
        VKSdk.authorize(permissions);
    }

    @Override
    public void logout() {
        VKSdk.logout();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public AccessToken getAccessToken() {
        return new AccessToken(accessToken.toString(), null);
    }

    @Override
    public void requestAccessToken(OnRequestAccessTokenCompleteListener onRequestAccessTokenCompleteListener) {
        super.requestAccessToken(onRequestAccessTokenCompleteListener);
        ((OnRequestAccessTokenCompleteListener) mLocalListeners.get(REQUEST_ACCESS_TOKEN))
                .onRequestAccessTokenComplete(getID(), new AccessToken(accessToken.toString(), null));
    }

    @Override
    public void requestCurrentPerson(OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener) {
        super.requestCurrentPerson(onRequestSocialPersonCompleteListener);
        requestSocialPerson(null, onRequestSocialPersonCompleteListener);
    }

    @Override
    public void requestSocialPerson(String userID, OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener) {
        super.requestSocialPerson(userID, onRequestSocialPersonCompleteListener);
        VKRequest request;
        final boolean current;
        if(userID == null){
            request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS,
                    "id,first_name,last_name,photo_200"
            ));
            current = true;
        } else {
            request = VKApi.users().get(VKParameters.from(VKApiConst.USER_IDS, userID, VKApiConst.FIELDS,
                    "id,first_name,last_name,photo_200"
            ));
            current = false;
        }
        request.secure = false;
        request.useSystemLanguage = false;
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

    @Override
    public void requestSocialPersons(String[] userID, OnRequestSocialPersonsCompleteListener onRequestSocialPersonsCompleteListener) {
        super.requestSocialPersons(userID, onRequestSocialPersonsCompleteListener);
        String userIds = TextUtils.join(",", userID);
        VKRequest request = VKApi.users().get(VKParameters.from(VKApiConst.USER_IDS, userIds, VKApiConst.FIELDS,
                "id,first_name,last_name,photo_200"
        ));
        request.secure = false;
        request.useSystemLanguage = false;
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

    @Override
    public void requestDetailedSocialPerson(String userId, OnRequestDetailedSocialPersonCompleteListener onRequestDetailedSocialPersonCompleteListener) {
        super.requestDetailedSocialPerson(userId, onRequestDetailedSocialPersonCompleteListener);
        VKRequest request;
        if(userId == null){
            request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS,
                    "id,first_name,last_name,photo_200,contacts,sex,bdate,city,country," +
                            "photo_max_orig,online,screen_name,has_mobile,education,can_post," +
                            "can_see_all_posts,can_write_private_message,status"
            ));
        } else {
            request = VKApi.users().get(VKParameters.from(VKApiConst.USER_IDS, userId, VKApiConst.FIELDS,
                "id,first_name,last_name,photo_200,contacts,sex,bdate,city,country," +
                "photo_max_orig,online,screen_name,has_mobile,education,can_post," +
                "can_see_all_posts,can_write_private_message,status"
        ));
        }
        request.secure = false;
        request.useSystemLanguage = false;
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
        if(jsonResponse.has("photo_200_orig")) {
            socialPerson.avatarURL = jsonResponse.getString("photo_200_orig");
        }
        if(jsonResponse.has("photo_200")) {
            socialPerson.avatarURL = jsonResponse.getString("photo_200");
        }
        return socialPerson;
    }

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

    private static boolean stringToBool(String s) {
        if (s.equals("1"))
            return true;
        if (s.equals("0"))
            return false;
        throw new IllegalArgumentException(s+" is not a bool. Only 1 and 0 are.");
    }

    @Override
    public void requestPostMessage(String message, OnPostingCompleteListener onPostingCompleteListener) {
        super.requestPostMessage(message, onPostingCompleteListener);
        makePost(null, message, REQUEST_POST_MESSAGE);
    }
    
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
    
	@Override
    public void requestGetFriends(OnRequestGetFriendsCompleteListener onRequestGetFriendsCompleteListener) {
        super.requestGetFriends(onRequestGetFriendsCompleteListener);
        VKRequest request = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS,
                "id,first_name,last_name,photo_200_orig"));
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

    @Override
    public void onResume() {
        super.onResume();
        VKUIHelper.onResume(activity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(activity);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int sanitizedRequestCode = requestCode % 0x10000;
        VKUIHelper.onActivityResult(sanitizedRequestCode, resultCode, data);
    }
}
