package com.gorbin.androidsocialnetworksextended.asne.utils;

import com.gorbin.androidsocialnetworksextended.asne.R;

public class Constants {

    public static final String USER_ID = "USER_ID";
    public static final String NETWORK_ID = "networkId";

    public enum SharePost {
        NONE,
        POST_MESSAGE,
        POST_PHOTO,
        POST_LINK,
        POST_DIALOG
    }

//    public static final String[] logo = {"{icon-twitter}","{icon-linkedin}","{icon-gplus}",
//            "{icon-facebook}","{icon-vk}","{icon-ok}"};
    public static final int[] logo = {R.drawable.ic_twitter, R.drawable.ic_linkedin,
            R.drawable.ic_googleplus, R.drawable.ic_facebook, R.drawable.ic_vk,
            R.drawable.ic_odnoklassniki};
    public static final String[] socialName = {"Twitter","LinkedIn","Google+","Facebook",
            "Vkontakte","Odnoklassniki"};
    public static final int[] userPhoto = {R.drawable.twitter_user, R.drawable.linkedin_user,
            R.drawable.g_plus_user, R.drawable.com_facebook_profile_picture_blank_square,
            R.drawable.vk_user, R.drawable.ok_user};
    public static final int[] color = {R.color.twitter, R.color.linkedin, R.color.google_plus,
            R.color.facebook, R.color.vk, R.color.ok};

    public static final String[] fbShare ={"Post status update","Post photo","Post Link",
            "Post Dialog"};
    public static final SharePost[] fbShareNum ={SharePost.POST_MESSAGE,SharePost.POST_PHOTO,
            SharePost.POST_LINK,SharePost.POST_DIALOG};
    public static final String[] twShare ={"Tweet","Tweet with image"};
    public static final SharePost[] twShareNum ={SharePost.POST_MESSAGE,SharePost.POST_PHOTO};
    public static final String[] gpShare ={"Share Dialog"};
    public static final SharePost[] gpShareNum ={SharePost.POST_DIALOG};
    public static final String[] inShare ={"Post status update", "Post share Link"};
    public static final SharePost[] inShareNum ={SharePost.POST_MESSAGE,SharePost.POST_LINK};
    public static final String[] vkShare ={"Post message","Post photo to wall","Post Link"};
    public static final SharePost[] vkShareNum ={SharePost.POST_MESSAGE,SharePost.POST_PHOTO,
            SharePost.POST_LINK};
    public static final String[] okShare ={"Post Link"};
    public static final SharePost[] okShareNum ={SharePost.POST_LINK};
    public static final String[][] share = {twShare, inShare, gpShare, fbShare, vkShare, okShare};
    public static final SharePost[][] shareNum = {twShareNum, inShareNum, gpShareNum, fbShareNum,
            vkShareNum, okShareNum};

    public static final String message = "Hello from ASNE!";
    public static final String link = "https://github.com/gorbin";

    public static String handleError(int socialNetworkID, String requestID, String errorMessage){
        return "ERROR: " + errorMessage + "in "
                + socialName[socialNetworkID-1] + "SocialNetwork" + " by " + requestID;
    }
}
