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
import com.gorbin.androidsocialnetworksextended.asne.utils.Constants;
import com.gorbin.androidsocialnetworksextended.asne.utils.SocialCard;

public class DetailedSocialInfoFragment extends Fragment implements OnRequestDetailedSocialPersonCompleteListener, OnRequestSocialPersonCompleteListener {
    private SocialCard infoCard;
    private SocialNetwork socialNetwork;
    private int socialNetworkID;
    private boolean detailsIsShown;
    private String userId;

    public static DetailedSocialInfoFragment newInstannce(int socialNetworkId, String userId) {
        DetailedSocialInfoFragment fragment = new DetailedSocialInfoFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.NETWORK_ID, socialNetworkId);
        args.putString(Constants.USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailedSocialInfoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        socialNetworkID = getArguments().getInt(Constants.NETWORK_ID);
        userId = getArguments().getString(Constants.USER_ID);
        setHasOptionsMenu(true);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Social Person");
        View rootView = inflater.inflate(R.layout.card_social_fragment, container, false);
        infoCard = (SocialCard) rootView.findViewById(R.id.info_card);
        int darkColor = getResources().getColor(R.color.grey_light);
        int textColor = getResources().getColor(R.color.dark);
        int color = getResources().getColor(R.color.dark);
        int image = R.drawable.user;
        socialNetwork = MainFragment.mSocialNetworkManager.getSocialNetwork(socialNetworkID);
        switch (socialNetworkID) {
            case 1:
                color = getResources().getColor(R.color.twitter);
                textColor = getResources().getColor(R.color.twitter);
                darkColor = getResources().getColor(R.color.twitter_light);
                image = R.drawable.twitter_user;
                break;
            case 2:
                color = getResources().getColor(R.color.linkedin);
                textColor = getResources().getColor(R.color.linkedin);
                darkColor = getResources().getColor(R.color.linkedin_light);
                image = R.drawable.linkedin_user;
                break;
            case 3:
                color = getResources().getColor(R.color.google_plus);
                textColor = getResources().getColor(R.color.google_plus);
                darkColor = getResources().getColor(R.color.google_plus_light);
                image = R.drawable.g_plus_user;
                break;
            case 4:
                color = getResources().getColor(R.color.facebook);
                textColor = getResources().getColor(R.color.facebook);
                darkColor = getResources().getColor(R.color.facebook_light);
                image = R.drawable.com_facebook_profile_picture_blank_square;
                break;
            case 5:
                color = getResources().getColor(R.color.vk);
                textColor = getResources().getColor(R.color.vk);
                darkColor = getResources().getColor(R.color.vk_light);
                image = R.drawable.vk_user;
                break;
            case 6:
                color = getResources().getColor(R.color.ok);
                textColor = getResources().getColor(R.color.ok);
                darkColor = getResources().getColor(R.color.ok_light);
                image = R.drawable.ok_user;
                break;
        }
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
    }

    private void defaultSocialCardData(SocialCard socialCard, int id) {
        socialCard.setName("NoName");
        socialCard.setId("unknown");
        socialCard.setImageResource(Constants.userPhoto[id-1]);
    }

    public void setSocialCardFromUser(SocialPerson socialPerson, SocialCard socialCard, int id){
        socialCard.setName(socialPerson.name);
        String detailedSocialPersonString = socialPerson.toString();
        String infoString = detailedSocialPersonString.substring(detailedSocialPersonString.indexOf("{")+1, detailedSocialPersonString.lastIndexOf("}"));
        socialCard.setId(infoString.replace(", ", "\n"));
        socialCard.setImage(socialPerson.avatarURL, Constants.userPhoto[id], R.drawable.error);
    }

}
