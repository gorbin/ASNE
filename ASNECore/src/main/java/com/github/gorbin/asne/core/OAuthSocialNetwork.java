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
package com.github.gorbin.asne.core;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.gorbin.asne.core.listener.base.SocialNetworkListener;

import java.util.HashMap;
import java.util.Map;

import static com.github.gorbin.asne.core.Consts.TAG;

/**
 * Class for oauth connection to social networks
 *
 * @author Anton Krasov
 * @author Evgeny Gorbin (gorbin.e.o@gmail.com)
 */
public abstract class OAuthSocialNetwork extends SocialNetwork {

    protected Map<String, SocialNetworkAsyncTask> mRequests = new HashMap<String, SocialNetworkAsyncTask>();

    protected OAuthSocialNetwork(Fragment fragment) {
        super(fragment);
    }

    protected void executeRequest(SocialNetworkAsyncTask request, Bundle params, String requestID) {
        checkRequestState(mRequests.get(requestID));
        mRequests.put(requestID, request);
        request.execute(params == null ? new Bundle() : params);
    }

    private void cancelRequest(String requestID) {
        SocialNetworkAsyncTask request = mRequests.get(requestID);

        if (request != null) {
            request.cancel(true);
        }

        mRequests.remove(requestID);
    }
    /**
     * Cancel login request
     */
    @Override
    public void cancelLoginRequest() {
        super.cancelLoginRequest();

        cancelRequest(REQUEST_LOGIN);
        cancelRequest(REQUEST_LOGIN2);
    }

    /**
     * Cancel {@link com.github.gorbin.asne.core.AccessToken} request
     */
    @Override
    public void cancelAccessTokenRequest() {
        super.cancelAccessTokenRequest();

        cancelRequest(REQUEST_ACCESS_TOKEN);
    }

    /**
     * Cancel current user {@link com.github.gorbin.asne.core.persons.SocialPerson} request
     */
    @Override
    public void cancelGetCurrentPersonRequest() {
        super.cancelGetCurrentPersonRequest();

        cancelRequest(REQUEST_GET_PERSON);
    }

    /**
     * Cancel user by id {@link com.github.gorbin.asne.core.persons.SocialPerson} request
     */
    @Override
    public void cancelGetSocialPersonRequest() {
        super.cancelGetSocialPersonRequest();

        cancelRequest(REQUEST_GET_PERSON);
    }

    /**
     * Cancel detailed user request
     */
    @Override
    public void cancelGetDetailedSocialRequest() {
        super.cancelGetDetailedSocialRequest();

        cancelRequest(REQUEST_GET_DETAIL_PERSON);
    }

    /**
     * Cancel users by array of ids request
     */
    @Override
    public void cancelGetSocialPersonsRequest() {
        super.cancelGetSocialPersonsRequest();

        cancelRequest(REQUEST_GET_PERSON);
    }

    /**
     * Cancel post message request
     */
    @Override
    public void cancelPostMessageRequest() {
        super.cancelPostMessageRequest();

        cancelRequest(REQUEST_POST_MESSAGE);
    }

    /**
     * Cancel post photo request
     */
    @Override
    public void cancelPostPhotoRequest() {
        super.cancelPostPhotoRequest();

        cancelRequest(REQUEST_POST_PHOTO);
    }

    /**
     * Cancel post link request
     */
    @Override
    public void cancelPostLinkRequest() {
        super.cancelPostLinkRequest();

        cancelRequest(REQUEST_POST_LINK);
    }

    /**
     * Cancel share dialog request
     */
    @Override
    public void cancelPostDialogRequest() {
        super.cancelPostDialogRequest();

        cancelRequest(REQUEST_POST_DIALOG);
    }

    /**
     * Cancel check friend request
     */
    @Override
    public void cancelCheckIsFriendRequest() {
        super.cancelCheckIsFriendRequest();

        cancelRequest(REQUEST_CHECK_IS_FRIEND);
    }

    /**
     * Cancel friends list request
     */
    @Override
    public void cancelGetFriendsRequest() {
        super.cancelGetFriendsRequest();

        cancelRequest(REQUEST_GET_FRIENDS);
    }

    /**
     * Cancel add friend request
     */
    @Override
    public void cancelAddFriendRequest() {
        super.cancelAddFriendRequest();

        cancelRequest(REQUEST_ADD_FRIEND);
    }

    /**
     * Cancel remove friend request
     */
    @Override
    public void cancelRemoveFriendRequest() {
        super.cancelRemoveFriendRequest();

        cancelRequest(REQUEST_REMOVE_FRIEND);
    }

    protected boolean handleRequestResult(Bundle result, String requestID) {
        return handleRequestResult(result, requestID, null);
    }

    protected boolean handleRequestResult(Bundle result, String requestID, Object data) {
        mRequests.remove(requestID);

        SocialNetworkListener socialNetworkListener = mLocalListeners.get(requestID);

        // 1: user didn't set listener, or pass null, this doesn't have any sence
        // 2: request was canceled...
        if (socialNetworkListener == null) {
            return false;
        }

        String error = result.getString(SocialNetworkAsyncTask.RESULT_ERROR);

        if (error != null) {
            socialNetworkListener.onError(getID(), requestID, error, data);
            mLocalListeners.remove(requestID);
            return false;
        }

        return true;
    }

}
