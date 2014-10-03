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
package com.github.gorbin.asne.core.listener;

import com.github.gorbin.asne.core.listener.base.SocialNetworkListener;
import com.github.gorbin.asne.core.persons.SocialPerson;

import java.util.ArrayList;
/**
 * Interface definition for a callback to be invoked when friends list request complete.
 *
 * @author Evgeny Gorbin (gorbin.e.o@gmail.com)
 */
public interface OnRequestGetFriendsCompleteListener extends SocialNetworkListener {
    /**
     * Called when friends list request complete.
     * @param socialNetworkID id of social network where request was complete
     * @param friendsID array of friends list's user ids
     */
    public void OnGetFriendsIdComplete(int socialNetworkID, String[] friendsID);

    /**
     * Called when friends list request complete.
     * @param socialNetworkID id of social network where request was complete
     * @param socialFriends ArrayList of of friends list's social persons
     */
    public void OnGetFriendsComplete(int socialNetworkID, ArrayList<SocialPerson> socialFriends);
}
