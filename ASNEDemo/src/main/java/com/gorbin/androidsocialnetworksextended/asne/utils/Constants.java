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
package com.gorbin.androidsocialnetworksextended.asne.utils;

import com.gorbin.androidsocialnetworksextended.asne.R;

public class Constants {

    public static final String USER_ID = "USER_ID";
    public static final String NETWORK_ID = "networkId";
    public static final int[] logo = {R.drawable.ic_twitter, R.drawable.ic_linkedin,
            R.drawable.ic_googleplus, R.drawable.ic_facebook, R.drawable.ic_vk,
            R.drawable.ic_odnoklassniki, R.drawable.ic_instagram};
    public static final String[] socialName = {"Twitter","LinkedIn","GooglePlus","Facebook",
            "Vkontakte","Odnoklassniki", "Instagram"};
    public static final int[] userPhoto = {R.drawable.twitter_user, R.drawable.linkedin_user,
            R.drawable.g_plus_user, R.drawable.user,
            R.drawable.vk_user, R.drawable.ok_user, R.drawable.instagram_user};
    public static final int[] color = {R.color.twitter, R.color.linkedin, R.color.google_plus,
            R.color.facebook, R.color.vk, R.color.ok, R.color.instagram};
    public static final int[] color_light = {R.color.twitter_light, R.color.linkedin_light, R.color.google_plus_light,
            R.color.facebook_light, R.color.vk_light, R.color.ok_light, R.color.instagram_light};
    public static final String[] fbShare ={"Post status update","Post photo","Post Link",
            "Post Dialog"};
    public static final SharePost[] fbShareNum ={SharePost.POST_MESSAGE,SharePost.POST_PHOTO,
            SharePost.POST_LINK,SharePost.POST_DIALOG};
    public static final String[] twShare ={"Tweet","Tweet with image","Tweet with link"};
    public static final SharePost[] twShareNum ={SharePost.POST_MESSAGE,SharePost.POST_PHOTO,SharePost.POST_LINK};
    public static final String[] gpShare ={"Share Dialog"};
    public static final SharePost[] gpShareNum ={SharePost.POST_DIALOG};
    public static final String[] inShare ={"Share message", "Post share Link"};
    public static final SharePost[] inShareNum ={SharePost.POST_MESSAGE,SharePost.POST_LINK};
    public static final String[] vkShare ={"Post message","Post photo to wall","Post Link"};
    public static final SharePost[] vkShareNum ={SharePost.POST_MESSAGE,SharePost.POST_PHOTO,
            SharePost.POST_LINK};
    public static final String[] okShare ={"Post Link"};
    public static final SharePost[] okShareNum ={SharePost.POST_LINK};
    public static final String[] instagramShare ={"Post Photo"};
    public static final String[][] share = {twShare, inShare, gpShare, fbShare, vkShare, okShare, instagramShare};
    public static final SharePost[] instagramShareNum ={SharePost.POST_PHOTO};
    public static final SharePost[][] shareNum = {twShareNum, inShareNum, gpShareNum, fbShareNum,
            vkShareNum, okShareNum, instagramShareNum};
    public static final String message = "Hello from ASNE!";
    public static final String title = "Android social networks library";
    public static final String link = "https://github.com/gorbin/ASNE";
    public static final String facebookShare = "Some Facebook permissions you can get only after " +
            "Facebook submission, so my demo app wasn't submitted due low functionality. So if you " +
            "want to use it with all functionality send me your FacebookID and I add you as tester " +
            "- this is easy way to to fully use demo app email: gorbin.e.o@gmail.com";
    public static final String facebookFriends =  "Apps are no longer able to retrieve the full " +
            "list of a user's friends (only those friends who have specifically authorized " +
            "your app using the user_friends permission) but if you add me as friend you " +
            "will see me in friendlist: facebook.com/evgeny.gorbin";

    public static String handleError(int socialNetworkID, String requestID, String errorMessage){
        return "ERROR: " + errorMessage + "in "
                + socialName[socialNetworkID-1] + "SocialNetwork" + " by " + requestID;
    }

    public enum SharePost {
        NONE,
        POST_MESSAGE,
        POST_PHOTO,
        POST_LINK,
        POST_DIALOG
    }
}