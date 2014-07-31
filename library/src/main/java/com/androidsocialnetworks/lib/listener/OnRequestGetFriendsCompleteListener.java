package com.androidsocialnetworks.lib.listener;

import com.androidsocialnetworks.lib.listener.base.SocialNetworkListener;
import com.androidsocialnetworks.lib.persons.SocialPerson;

import java.util.ArrayList;

public interface OnRequestGetFriendsCompleteListener extends SocialNetworkListener {
    public void OnGetFriendsIdComplete(int socialNetworkID, String[] friendsID);
    public void OnGetFriendsComplete(int socialNetworkID, ArrayList<SocialPerson> socialFriends);
}
