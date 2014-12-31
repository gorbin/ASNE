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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.listener.OnRequestDetailedSocialPersonCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestRemoveFriendCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestSocialPersonCompleteListener;
import com.github.gorbin.asne.core.persons.SocialPerson;
import com.gorbin.androidsocialnetworksextended.asne.utils.ADialogs;
import com.gorbin.androidsocialnetworksextended.asne.utils.Constants;
import com.gorbin.androidsocialnetworksextended.asne.utils.SocialCard;

public class DetailedSocialInfoFragment extends Fragment implements OnRequestDetailedSocialPersonCompleteListener, OnRequestSocialPersonCompleteListener {
    private SocialCard infoCard;
    private SocialNetwork socialNetwork;
    private int socialNetworkID;
    private boolean detailsIsShown;
    private String userId;
    private ADialogs loadingDialog;

    public DetailedSocialInfoFragment() {
    }

    public static DetailedSocialInfoFragment newInstance(int socialNetworkId, String userId) {
        DetailedSocialInfoFragment fragment = new DetailedSocialInfoFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.NETWORK_ID, socialNetworkId);
        args.putString(Constants.USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        socialNetworkID = getArguments().getInt(Constants.NETWORK_ID);
        userId = getArguments().getString(Constants.USER_ID);
        setHasOptionsMenu(true);
        loadingDialog = new ADialogs(getActivity());
        loadingDialog.progress(false, "Loading social person...");
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Social Person");
        View rootView = inflater.inflate(R.layout.card_social_fragment, container, false);
        infoCard = (SocialCard) rootView.findViewById(R.id.info_card);
        int darkColor = getResources().getColor(Constants.color_light[socialNetworkID - 1]);
        int textColor = getResources().getColor(Constants.color[socialNetworkID - 1]);
        int color = getResources().getColor(Constants.color[socialNetworkID - 1]);
        int image = Constants.userPhoto[socialNetworkID - 1];
        socialNetwork = MainFragment.mSocialNetworkManager.getSocialNetwork(socialNetworkID);

        infoCard.setColors(color, textColor, darkColor);
        infoCard.setImageResource(image);
        socialNetwork.setOnRequestDetailedSocialPersonCompleteListener(this);
        socialNetwork.setOnRequestSocialPersonCompleteListener(this);
        updateInfoCard(infoCard, socialNetwork, socialNetworkID, userId);
        return rootView;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if(socialNetworkID != 2 && socialNetworkID != 3 && socialNetworkID != 6) {
            inflater.inflate(R.menu.friend_menu, menu);
            menu.findItem(R.id.action_delete_friend).setIcon(android.R.drawable.ic_menu_delete);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_friend:
                socialNetwork.requestRemoveFriend(userId, new OnRequestRemoveFriendCompleteListener() {
                    @Override
                    public void onRequestRemoveFriendComplete(int socialNetworkID, String userID) {
                        Toast.makeText(getActivity(), "UserId: " + userID + " Removed from your friends", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
                        Toast.makeText(getActivity(), Constants.handleError(socialNetworkID, requestID, errorMessage), Toast.LENGTH_LONG).show();
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onRequestSocialPersonSuccess(int socialNetworkId, SocialPerson socialPerson) {
        setSocialCardFromUser(socialPerson, infoCard, socialNetworkID-1);
    }

    @Override
    public void onRequestDetailedSocialPersonSuccess(int id, SocialPerson socialPerson) {
        setSocialCardFromUser(socialPerson, infoCard, id - 1);
    }

    @Override
    public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
        if(loadingDialog != null) {
            loadingDialog.cancelProgress();
        }
        Toast.makeText(getActivity(), Constants.handleError(socialNetworkID, requestID, errorMessage), Toast.LENGTH_LONG).show();
    }

    private void updateInfoCard(final SocialCard socialCard, final SocialNetwork socialNetwork, final int id, final String userId) {
        if(socialNetwork.isConnected()) {
            socialCard.connect.setVisibility(View.GONE);
            socialCard.friends.setVisibility(View.GONE);
            socialCard.share.setVisibility(View.GONE);
            socialCard.detail.setVisibility(View.VISIBLE);
            socialCard.detail.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    detailsIsShown = !detailsIsShown;
                    socialOrDetailed(socialCard, socialNetwork, userId);
                }
            });
            socialOrDetailed(socialCard, socialNetwork, userId);
        } else {
            socialCard.detail.setVisibility(View.GONE);
            socialCard.connect.setVisibility(View.GONE);
            socialCard.friends.setVisibility(View.GONE);
            defaultSocialCardData(socialCard, id);
        }
    }

    private void socialOrDetailed(SocialCard socialCard, SocialNetwork socialNetwork, String userId){
        if(detailsIsShown) {
            socialCard.detail.setText("hide details");
            socialNetwork.requestDetailedSocialPerson(userId);
        } else {
            socialCard.detail.setText("show details...");
            socialNetwork.requestSocialPerson(userId);
        }
        loadingDialog.showProgress();
    }

    private void defaultSocialCardData(SocialCard socialCard, int id) {
        socialCard.setName("NoName");
        socialCard.setId("unknown");
        socialCard.setImageResource(Constants.userPhoto[id - 1]);
    }

    public void setSocialCardFromUser(SocialPerson socialPerson, SocialCard socialCard, int id){
        socialCard.setName(socialPerson.name);
        String detailedSocialPersonString = socialPerson.toString();
        String infoString = detailedSocialPersonString.substring(detailedSocialPersonString.indexOf("{")+1, detailedSocialPersonString.lastIndexOf("}"));
        socialCard.setId(infoString.replace(", ", "\n"));
        socialCard.setImage(socialPerson.avatarURL, Constants.userPhoto[id], R.drawable.error);
        loadingDialog.cancelProgress();
    }
}