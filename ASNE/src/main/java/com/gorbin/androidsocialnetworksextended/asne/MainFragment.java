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
package com.gorbin.androidsocialnetworksextended.asne;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.SocialNetworkException;
import com.github.gorbin.asne.core.SocialNetworkManager;
import com.github.gorbin.asne.core.listener.OnLoginCompleteListener;
import com.github.gorbin.asne.core.listener.OnPostingCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestDetailedSocialPersonCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestSocialPersonCompleteListener;
import com.github.gorbin.asne.core.persons.SocialPerson;
import com.github.gorbin.asne.facebook.FacebookSocialNetwork;
import com.github.gorbin.asne.googleplus.GooglePlusSocialNetwork;
import com.github.gorbin.asne.instagram.InstagramSocialNetwork;
import com.github.gorbin.asne.linkedin.LinkedInSocialNetwork;
import com.github.gorbin.asne.odnoklassniki.OkSocialNetwork;
import com.github.gorbin.asne.twitter.TwitterSocialNetwork;
import com.github.gorbin.asne.vk.VkSocialNetwork;
import com.gorbin.androidsocialnetworksextended.asne.utils.ADialogs;
import com.gorbin.androidsocialnetworksextended.asne.utils.Constants;
import com.gorbin.androidsocialnetworksextended.asne.utils.SocialCard;
import com.vk.sdk.VKScope;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import ru.ok.android.sdk.util.OkScope;

public class MainFragment  extends Fragment
        implements SocialNetworkManager.OnInitializationCompleteListener, OnLoginCompleteListener,
        OnRequestSocialPersonCompleteListener, OnRequestDetailedSocialPersonCompleteListener {

    //Keys
    public static final String TWITTER_CONSUMER_KEY = "TWITTER_CONSUMER_KEY";
    public static final String TWITTER_CONSUMER_SECRET = "TWITTER_CONSUMER_SECRET";
    public static final String LINKEDIN_CONSUMER_KEY = "LINKEDIN_CONSUMER_KEY";
    public static final String LINKEDIN_CONSUMER_SECRET = "LINKEDIN_CONSUMER_SECRET";
    public static final String VK_KEY =  "VK_KEY";
    public static final String OK_APP_ID =  "OK_APP_ID";
    public static final String OK_PUBLIC_KEY =  "OK_PUBLIC_KEY";
    public static final String OK_SECRET_KEY =  "OK_SECRET_KEY";
    public static final String INSTAGRAM_CLIENT_KEY = "INSTAGRAM_KEY";
    public static final String INSTAGRAM_CLIENT_SECRET = "INSTAGRAM_SECRET";

    //redirect urls
    public static final String TWITTER_CALLBACK_URL = "http://github.com/gorbin/ASNE";
    public static final String LINKEDIN_CALLBACK_URL = "https://asne";
    public static final String INSTAGRAM_CALLBACK_URL = "oauth://ASNE";

    public static final String SOCIAL_NETWORK_TAG = "SocialIntegrationMain.SOCIAL_NETWORK_TAG";
    public static SocialNetworkManager mSocialNetworkManager;
    ADialogs loginProgressDialog;
    private SocialCard socialCards[] =  new SocialCard[Constants.logo.length];
    private boolean isDetailed[] = new boolean[Constants.logo.length];
    private OnPostingCompleteListener postingComplete = new OnPostingCompleteListener() {
        @Override
        public void onPostSuccessfully(int socialNetworkID) {
            Toast.makeText(getActivity(), "Sent", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
            Toast.makeText(getActivity(), "Error while sending: " + errorMessage, Toast.LENGTH_LONG).show();
        }
    };

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_social_network, container, false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name);

        loginProgressDialog = new ADialogs(getActivity());
        loginProgressDialog.progress(false, "Login in progress...");

//      Get VK fingerprints for app
//        String[] fingerprints = VKUtil.getCertificateFingerprint(getActivity(), getActivity().getPackageName());
//        Log.d("TAG via vk ", "Fingerprint: " + fingerprints[0]);
//        printHashKey();

        socialCards[0] = (SocialCard) rootView.findViewById(R.id.tw_card);
        socialCards[1] = (SocialCard) rootView.findViewById(R.id.in_card);
        socialCards[2] = (SocialCard) rootView.findViewById(R.id.gp_card);
        socialCards[3] = (SocialCard) rootView.findViewById(R.id.fb_card);
        socialCards[4] = (SocialCard) rootView.findViewById(R.id.vk_card);
        socialCards[5] = (SocialCard) rootView.findViewById(R.id.ok_card);
        socialCards[6] = (SocialCard) rootView.findViewById(R.id.insta_card);


//      Connect ASNE SocialNetworks

        ArrayList<String> fbScope = new ArrayList<String>();
        fbScope.addAll(Arrays.asList("public_profile, email, user_friends, user_location, user_birthday"));
        String linkedInScope = "r_basicprofile+r_fullprofile+rw_nus+r_network+w_messages+r_emailaddress+r_contactinfo";
        String[] okScope = new String[] {
                OkScope.VALUABLE_ACCESS
        };
        String[] vkScope = new String[] {
                VKScope.FRIENDS,
                VKScope.WALL,
                VKScope.PHOTOS,
                VKScope.NOHTTPS,
                VKScope.STATUS,
        };
        String instagramScope = "likes+comments+relationships";

        mSocialNetworkManager = (SocialNetworkManager) getFragmentManager().findFragmentByTag(SOCIAL_NETWORK_TAG);

        if (mSocialNetworkManager == null) {
            mSocialNetworkManager = new SocialNetworkManager();

            FacebookSocialNetwork fbNetwork = new FacebookSocialNetwork(this, fbScope);
            mSocialNetworkManager.addSocialNetwork(fbNetwork);

            TwitterSocialNetwork twNetwork = new TwitterSocialNetwork(this, TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET, TWITTER_CALLBACK_URL);
            mSocialNetworkManager.addSocialNetwork(twNetwork);

            LinkedInSocialNetwork liNetwork = new LinkedInSocialNetwork(this, LINKEDIN_CONSUMER_KEY, LINKEDIN_CONSUMER_SECRET, LINKEDIN_CALLBACK_URL, linkedInScope);
            mSocialNetworkManager.addSocialNetwork(liNetwork);

            GooglePlusSocialNetwork gpNetwork = new GooglePlusSocialNetwork(this);
            mSocialNetworkManager.addSocialNetwork(gpNetwork);

            VkSocialNetwork vkNetwork = new VkSocialNetwork(this, VK_KEY, vkScope);
            mSocialNetworkManager.addSocialNetwork(vkNetwork);

            OkSocialNetwork okNetwork = new OkSocialNetwork(this, OK_APP_ID, OK_PUBLIC_KEY, OK_SECRET_KEY, okScope);
            mSocialNetworkManager.addSocialNetwork(okNetwork);

            InstagramSocialNetwork instagramNetwork = new InstagramSocialNetwork(this, INSTAGRAM_CLIENT_KEY, INSTAGRAM_CLIENT_SECRET, INSTAGRAM_CALLBACK_URL, instagramScope);
            mSocialNetworkManager.addSocialNetwork(instagramNetwork);

            getFragmentManager().beginTransaction().add(mSocialNetworkManager, SOCIAL_NETWORK_TAG).commit();
            mSocialNetworkManager.setOnInitializationCompleteListener(this);
        } else {
            if(!mSocialNetworkManager.getInitializedSocialNetworks().isEmpty()) {
                for (SocialNetwork socialNetwork : mSocialNetworkManager.getInitializedSocialNetworks()) {
                    socialNetwork.setOnLoginCompleteListener(this);
                    socialNetwork.setOnRequestCurrentPersonCompleteListener(this);
                    socialNetwork.setOnRequestDetailedSocialPersonCompleteListener(this);
                }
                for (int i = 0; i < socialCards.length; i++){
                    updateSocialCard(socialCards[i], i + 1);
                }
            }
        }
        return rootView;
    }

// Print hashKey for FB app
//    public void printHashKey() {
//    try {
//    PackageInfo info = getActivity().getPackageManager().getPackageInfo("com.gorbin.androidsocialnetworksextended.asne",
//    PackageManager.GET_SIGNATURES);
//    for (Signature signature : info.signatures) {
//    MessageDigest md = MessageDigest.getInstance("SHA");
//    md.update(signature.toByteArray());
//    Log.d("TEMPTAGHASH KEY:",
//    Base64.encodeToString(md.digest(), Base64.DEFAULT));
//    }
//    } catch (PackageManager.NameNotFoundException e) {
//
//    } catch (NoSuchAlgorithmException e) {
//
//    }
//
//    }

//==================================================================================================

    private void defaultSocialCardData(SocialCard socialCard, int id) {
        socialCard.setName("NoName");
        socialCard.setId("unknown");
        socialCard.setImageResource(Constants.userPhoto[id - 1]);
    }

    @Override
    public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
        if(loginProgressDialog != null) {
            loginProgressDialog.cancelProgress();
        }
        Toast.makeText(getActivity(), Constants.handleError(socialNetworkID, requestID, errorMessage), Toast.LENGTH_LONG).show();
        updateSocialCard(socialCards[socialNetworkID-1], socialNetworkID);
    }

    @Override
    public void onSocialNetworkManagerInitialized() {
        for (SocialNetwork socialNetwork : mSocialNetworkManager.getInitializedSocialNetworks()) {
            socialNetwork.setOnLoginCompleteListener(this);
            socialNetwork.setOnRequestCurrentPersonCompleteListener(this);
            socialNetwork.setOnRequestDetailedSocialPersonCompleteListener(this);
        }
        for(int i = 0; i < socialCards.length; i++){
            updateSocialCard(socialCards[i], i+1);
        }
    }

    @Override
    public void onLoginSuccess(int id) {
        if(id != 6) {
            loginProgressDialog.cancelProgress();
        }
        updateSocialCard(socialCards[id-1], id);

    }

    @Override
    public void onRequestSocialPersonSuccess(int socialNetworkId, SocialPerson socialPerson) {
        setSocialCardFromUser(socialPerson, socialCards[socialNetworkId - 1], socialNetworkId - 1);
    }

    @Override
    public void onRequestDetailedSocialPersonSuccess(int socialNetworkId, SocialPerson socialPerson) {
        setSocialCardFromUser(socialPerson, socialCards[socialNetworkId - 1], socialNetworkId - 1);
    }

//==================================================================================================
// You can get any SocialNetwork like:
//    public SocialNetwork getSpecialSocialCard(int networkId) {
//        SocialNetwork socialNetwork = null;
//        switch (networkId) {
//            case 1:
//                socialNetwork = mSocialNetworkManager.getTwitterSocialNetwork();
//                break;
//            case 2:
//                socialNetwork = mSocialNetworkManager.getLinkedInSocialNetwork();
//                break;
//            case 3:
//                socialNetwork = mSocialNetworkManager.getGooglePlusSocialNetwork();
//                break;
//            case 4:
//                socialNetwork = mSocialNetworkManager.getFacebookSocialNetwork();
//                break;
//            case 5:
//                socialNetwork = mSocialNetworkManager.getVKSocialNetwork();
//                break;
//            case 6:
//                socialNetwork = mSocialNetworkManager.getOKSocialNetwork();
//                break;
//        }
//        return socialNetwork;
//    }
//==================================================================================================

    private void updateSocialCard(final SocialCard socialCard, final int networkId) {
        final SocialNetwork socialNetwork = mSocialNetworkManager.getSocialNetwork(networkId);//getSpecialSocialCard(networkId);
        if((socialNetwork != null) && (socialNetwork.isConnected())) {

            socialCard.setConnectButtonIcon(Constants.logo[networkId - 1]);
            socialCard.setConnectButtonText("Logout");
            socialCard.connect.setVisibility(View.VISIBLE);
            socialCard.connect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    socialNetwork.logout();
                    updateSocialCard(socialCard, networkId);
                }
            });
            final ADialogs alertDialog = new ADialogs(getActivity());
            socialCard.share.setVisibility(View.VISIBLE);
            socialCard.share.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    ADialogs listDialog = new ADialogs(getActivity());
                    listDialog.setADialogsListListener(new ADialogs.ADialogsListListener() {
                        @Override
                        public void onADialogsListDialog(DialogInterface dialog, int id, Constants.SharePost type) {
                            switch(type){
                                case POST_MESSAGE:
                                    alertDialog.alert(true, "Would you like to post message:", Constants.message, "Post Message", "Cancel");
                                    alertDialog.setADialogsListener(new ADialogs.ADialogsListener() {
                                        @Override
                                        public void onADialogsPositiveClick(DialogInterface dialog) {
                                            socialNetwork.requestPostMessage(Constants.message, postingComplete);
                                        }

                                        @Override
                                        public void onADialogsNegativeClick(DialogInterface dialog) {
                                            dialog.cancel();
                                        }

                                        @Override
                                        public void onADialogsCancel(DialogInterface dialog) {
                                            dialog.cancel();
                                        }
                                    });
                                    break;
                                case POST_PHOTO:
                                    alertDialog.customImageDialog(getActivity(), true, "Would you like to post photo:", getPhotoFile(), "Post Photo", "Cancel");
                                    alertDialog.setADialogsImageAlertListener(new ADialogs.ADialogsImageAlertListener() {
                                        @Override
                                        public void onADialogsImageAlertPositiveClick(DialogInterface dialog, int id) {
                                            socialNetwork.requestPostPhoto(getPhotoFile(), Constants.message, postingComplete);
                                        }
                                    });
                                    break;
                                case POST_LINK:
                                    alertDialog.alert(true, "Would you like to post Link:", Constants.link, "Post Link", "Cancel");
                                    alertDialog.setADialogsListener(new ADialogs.ADialogsListener() {
                                        @Override
                                        public void onADialogsPositiveClick(DialogInterface dialog) {
                                            Bundle postParams = new Bundle();
                                            postParams.putString(SocialNetwork.BUNDLE_LINK, Constants.link);
                                            postParams.putString(SocialNetwork.BUNDLE_NAME, Constants.title);
                                            socialNetwork.requestPostLink(postParams, Constants.message, postingComplete);
                                        }

                                        @Override
                                        public void onADialogsNegativeClick(DialogInterface dialog) {
                                            dialog.cancel();
                                        }

                                        @Override
                                        public void onADialogsCancel(DialogInterface dialog) {
                                            dialog.cancel();
                                        }
                                    });
                                    break;
                                case POST_DIALOG:
                                    Bundle linkParams = new Bundle();
                                    linkParams.putString(SocialNetwork.BUNDLE_LINK, Constants.link);
                                    socialNetwork.requestPostDialog(linkParams, postingComplete);
                                    break;
                                case NONE:
                                    Toast.makeText(getActivity(), Constants.socialName[networkId-1] + "SocialNetwork "
                                            + "sharing error", Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    });
                    listDialog.listDialog(true, "Share via " + Constants.socialName[networkId-1],
                            Constants.share[networkId-1], Constants.shareNum[networkId-1]);
                    ADialogs alert = new ADialogs(getActivity());
                    if(networkId == 4){
                        alert.alert(true,"Facebook share warning", Constants.facebookShare, null, "Continue");
                    }
                }
            });
            socialCard.friends.setVisibility(View.VISIBLE);
            socialCard.friends.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    FriendsListFragment friends = FriendsListFragment.newInstance(networkId);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .addToBackStack("friends")
                            .replace(R.id.container, friends)
                            .commit();
                }
            });
            socialCard.detail.setVisibility(View.VISIBLE);
            socialCard.detail.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    isDetailed[networkId - 1] = !isDetailed[networkId - 1];
                    socialOrDetailed(networkId, socialCard, socialNetwork);
                }
            });
            socialOrDetailed(networkId, socialCard, socialNetwork);
        } else {
            socialCard.detail.setVisibility(View.GONE);
            socialCard.setConnectButtonIcon(Constants.logo[networkId-1]);
            socialCard.setConnectButtonText("Login");
            socialCard.connect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    socialNetwork.requestLogin();
                    if(networkId != 6) {
                        loginProgressDialog.showProgress();
                    }
                }
            });
            socialCard.share.setVisibility(View.GONE);
            socialCard.friends.setVisibility(View.GONE);
            defaultSocialCardData(socialCard, networkId);
        }
    }

    private void socialOrDetailed(int networkId, SocialCard socialCard, SocialNetwork socialNetwork){
        if(isDetailed[networkId-1]) {
            socialCard.detail.setText("hide details");
            try {
                socialNetwork.requestDetailedCurrentPerson();
            } catch (SocialNetworkException e) {
                e.printStackTrace();
            }
        } else {
            socialCard.detail.setText("show details...");
            try {
                socialNetwork.requestCurrentPerson();
            } catch (SocialNetworkException e) {
                e.printStackTrace();
            }
        }
    }

    public void setSocialCardFromUser(SocialPerson socialPerson, SocialCard socialCard, int id){
        socialCard.setName(socialPerson.name);
        String detailedSocialPersonString = socialPerson.toString();
        String infoString = detailedSocialPersonString.substring(detailedSocialPersonString.indexOf("{")+1, detailedSocialPersonString.lastIndexOf("}"));
        socialCard.setId(infoString.replace(", ", "\n"));
        socialCard.setImage(socialPerson.avatarURL, Constants.userPhoto[id], R.drawable.error);
    }

    public File getPhotoFile(){
        File f = new File(getActivity().getCacheDir(), "logo.png");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.raw.logo);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        byte[] bitmapdata = bos.toByteArray();
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(f);
            fos.write(bitmapdata);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }
}
