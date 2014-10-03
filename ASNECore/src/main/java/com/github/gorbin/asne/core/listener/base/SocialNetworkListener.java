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
package com.github.gorbin.asne.core.listener.base;

/**
 * Base interface definition for a callback to be invoked when any social network request complete.
 *
 * @author Anton Krasov
 * @author Evgeny Gorbin (gorbin.e.o@gmail.com)
 *
 * @see com.github.gorbin.asne.core.listener.OnCheckIsFriendCompleteListener
 * @see com.github.gorbin.asne.core.listener.OnLoginCompleteListener
 * @see com.github.gorbin.asne.core.listener.OnPostingCompleteListener
 * @see com.github.gorbin.asne.core.listener.OnRequestAccessTokenCompleteListener
 * @see com.github.gorbin.asne.core.listener.OnRequestAddFriendCompleteListener
 * @see com.github.gorbin.asne.core.listener.OnRequestDetailedSocialPersonCompleteListener
 * @see com.github.gorbin.asne.core.listener.OnRequestGetFriendsCompleteListener
 * @see com.github.gorbin.asne.core.listener.OnRequestRemoveFriendCompleteListener
 * @see com.github.gorbin.asne.core.listener.OnRequestSocialPersonCompleteListener
 * @see com.github.gorbin.asne.core.listener.OnRequestSocialPersonsCompleteListener
 */
public interface SocialNetworkListener {
    /**
     * Called when social network request complete with error.
     * @param socialNetworkID id of social network where request was complete with error
     * @param requestID id of request where request was complete with error
     * @param errorMessage error message where request was complete with error
     * @param data data of social network where request was complete with error
     */
    public void onError(int socialNetworkID, String requestID, String errorMessage, Object data);
}
