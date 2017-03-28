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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.listener.OnCheckIsFriendCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestAddFriendCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestGetFriendsCompleteListener;
import com.github.gorbin.asne.core.persons.SocialPerson;
import com.gorbin.androidsocialnetworksextended.asne.utils.ADialogs;
import com.gorbin.androidsocialnetworksextended.asne.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class FriendsListFragment extends Fragment implements OnRequestGetFriendsCompleteListener, AdapterView.OnItemClickListener {
    private SocialNetwork socialNetwork;
    private ListView listMenu;
    private int socialNetworkId;
    private final List<SocialPerson> socialPersons = new ArrayList<SocialPerson>();
    private ADialogs editDialog;
    private ADialogs loadingDialog;

    public FriendsListFragment() {
    }

    public static FriendsListFragment newInstance(int id) {
        FriendsListFragment fragment = new FriendsListFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.NETWORK_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        socialNetworkId = getArguments().getInt(Constants.NETWORK_ID);
        View rootView = inflater.inflate(R.layout.list_fragment, container, false);
        if (rootView != null) {
            listMenu = (ListView) rootView.findViewById(R.id.list);
        }
        listMenu.setOnItemClickListener(this);

        editDialog = new ADialogs(getActivity());
        loadingDialog = new ADialogs(getActivity());
        loadingDialog.progress(false, "Loading friends...");
        ADialogs alert = new ADialogs(getActivity());
        if(socialNetworkId == 4){
             alert.alert(true,"Facebook me/friends", Constants.facebookFriends, null, "Continue");
        }

        socialNetwork = MainFragment.mSocialNetworkManager.getSocialNetwork(socialNetworkId);
        socialNetwork.setOnRequestGetFriendsCompleteListener(this);
        socialNetwork.requestGetFriends();
        loadingDialog.showProgress();
        setHasOptionsMenu(true);
        return rootView;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if(socialNetworkId != 4 && socialNetworkId != 3) {
            inflater.inflate(R.menu.friends_list_menu, menu);
            menu.findItem(R.id.action_add).setIcon(android.R.drawable.ic_menu_add);
            menu.findItem(R.id.action_search).setIcon(android.R.drawable.ic_menu_search);
        }
        if (socialNetworkId == 6 || socialNetworkId == 2){
            menu.findItem(R.id.action_add).setVisible(false);
            menu.findItem(R.id.action_add).setEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                editDialog.setADialogsEditListener(new ADialogs.ADialogsEditListener() {
                    @Override
                    public void onADialogsPositiveClick(DialogInterface dialog, String text) {
                        if(text!=null && !TextUtils.isEmpty(text) && checkUserId(text)) {
                            socialNetwork.requestAddFriend(text, new OnRequestAddFriendCompleteListener() {
                                @Override
                                public void onRequestAddFriendComplete(int socialNetworkID, String userID) {
                                    Toast.makeText(getActivity(), userID + " added as friend", Toast.LENGTH_LONG).show();
                                    socialNetwork.requestGetFriends();
                                }

                                @Override
                                public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
                                    Toast.makeText(getActivity(), Constants.handleError(socialNetworkID, requestID, errorMessage), Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "You should enter valid Id", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onADialogsNegativeClick(DialogInterface dialog) {}
                    @Override
                    public void onADialogsCancel(DialogInterface dialog) {}
                });
                editDialog.editDialog(true, "Add friends by id:", "Add", "Cancel");

                return true;
            case R.id.action_search:
                editDialog.setADialogsEditListener(new ADialogs.ADialogsEditListener() {
                    @Override
                    public void onADialogsPositiveClick(DialogInterface dialog, String text) {
                        if(text!=null && !TextUtils.isEmpty(text) && checkUserId(text)) {
                            socialNetwork.requestCheckIsFriend(text, new OnCheckIsFriendCompleteListener() {
                                @Override
                                public void onCheckIsFriendComplete(int socialNetworkID, String userID, boolean isFriend) {
                                    Toast.makeText(getActivity(), "You and " + userID + " are friends - " + isFriend, Toast.LENGTH_LONG).show();
                                }
                                @Override
                                public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
                                    Toast.makeText(getActivity(), Constants.handleError(socialNetworkID, requestID, errorMessage), Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "You should enter valid Id", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onADialogsNegativeClick(DialogInterface dialog) {}
                    @Override
                    public void onADialogsCancel(DialogInterface dialog) {}
                });
                editDialog.editDialog(true, "Check friends by id:", "Search", "Cancel");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onGetFriendsIdComplete(int socialNetworkID, String[] friendsID) {
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(friendsID.length + " Friends");
    }

    @Override
    public void onGetFriendsComplete(int socialNetworkID, List<SocialPerson> socialFriends) {
        this.socialPersons.clear();
        this.socialPersons.addAll(socialFriends);
        FriendsListAdapter adapter = new FriendsListAdapter(getActivity(), socialFriends, socialNetworkID);
        listMenu.setAdapter(adapter);
        loadingDialog.cancelProgress();
    }

    @Override
    public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
        if(loadingDialog != null) {
            loadingDialog.cancelProgress();
        }
        Toast.makeText(getActivity(), Constants.handleError(socialNetworkID, requestID, errorMessage),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(socialNetworkId != 4) {
            DetailedSocialInfoFragment friends = DetailedSocialInfoFragment.newInstance(socialNetworkId, socialPersons.get(i).id);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .addToBackStack("info")
                    .replace(R.id.container, friends)
                    .commit();
        } else{
            Toast.makeText(getActivity(), Constants.socialName[socialNetworkId-1] + "SocialNetwork "
                    + "can't show custom SocialPerson", Toast.LENGTH_LONG).show();
        }
    }
    public boolean checkUserId(String userId){
        switch (socialNetworkId){
            case 1:
                try {
                    Long d = Long.parseLong(userId);
                }
                catch(NumberFormatException nfe) {
                    return false;
                }
                return true;
            case 5:
                try {
                    int d = Integer.parseInt(userId);
                }
                catch(NumberFormatException nfe) {
                    return false;
                }
                return true;
            default:
                return true;
        }
    }
}