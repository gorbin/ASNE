package com.gorbin.androidsocialnetworksextended.asne;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidsocialnetworks.lib.SocialNetwork;
import com.androidsocialnetworks.lib.SocialNetworkException;
import com.androidsocialnetworks.lib.SocialNetworkManager;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.androidsocialnetworks.lib.listener.OnPostingCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestDetailedSocialPersonCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestSocialPersonCompleteListener;
import com.androidsocialnetworks.lib.persons.SocialPerson;
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

    private SocialCard socialCards[] =  new SocialCard[Constants.logo.length];
    private boolean isDetailed[] = new boolean[Constants.logo.length];
    public static final String SOCIAL_NETWORK_TAG = "SocialIntegrationMain.SOCIAL_NETWORK_TAG";
    public static SocialNetworkManager mSocialNetworkManager;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_social_network, container, false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name);

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

//      Connect ASNE SocialNetworks

        ArrayList<String> fbScope = new ArrayList<String>();
        fbScope.addAll(Arrays.asList("public_profile, email, user_friends, user_location, user_birthday"));
        String linkedInScope = "r_basicprofile+rw_nus+r_network+w_messages";
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
        mSocialNetworkManager = (SocialNetworkManager) getFragmentManager().findFragmentByTag(SOCIAL_NETWORK_TAG);

        if (mSocialNetworkManager == null) {
            mSocialNetworkManager = SocialNetworkManager.Builder.from(getActivity())
                    .twitter(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET)
                    .facebook(fbScope)
                    .googlePlus()
                    .linkedIn(LINKEDIN_CONSUMER_KEY, LINKEDIN_CONSUMER_SECRET, linkedInScope)
                    .vk(VK_KEY, vkScope)
                    .ok(OK_APP_ID, OK_PUBLIC_KEY, OK_SECRET_KEY, okScope)
                    .build();
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

    private void defaultSocialCardData(SocialCard socialCard, int id) {
        socialCard.setName("NoName");
        socialCard.setId("unknown");
        socialCard.setImageResource(Constants.userPhoto[id-1]);
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

    @Override
    public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
        Log.d("TAG Login failed: ", "onLoginFailed: " + requestID + " : " + errorMessage);
        Toast.makeText(getActivity(), Constants.handleError(socialNetworkID, requestID, errorMessage), Toast.LENGTH_LONG).show();
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
    private void updateSocialCard(final SocialCard socialCard, final int networkId) {
        final SocialNetwork socialNetwork = mSocialNetworkManager.getSocialNetwork(networkId);//getSpecialSocialCard(networkId);
        if((socialNetwork != null) && (socialNetwork.isConnected())) {

            socialCard.setConnectButtonIcon(Constants.logo[networkId-1]);
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
                }
            });
            socialCard.friends.setVisibility(View.VISIBLE);
            socialCard.friends.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    FriendsListFragment friends = FriendsListFragment.newInstannce(networkId);
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
