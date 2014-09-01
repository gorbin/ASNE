package com.gorbin.androidsocialnetworksextended.asne;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class FriendsListFragment extends Fragment implements OnRequestGetFriendsCompleteListener, AdapterView.OnItemClickListener {
    private SocialNetwork socialNetwork;
    private ListView listMenu;
    private int socialNetworkId;
    private ArrayList<SocialPerson> socialPersons = new ArrayList<SocialPerson>();
    private ADialogs editDialog;

    public static FriendsListFragment newInstannce(int id) {
        FriendsListFragment fragment = new FriendsListFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.NETWORK_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    public FriendsListFragment() {
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

        socialNetwork = MainFragment.mSocialNetworkManager.getSocialNetwork(socialNetworkId);
        socialNetwork.setOnRequestGetFriendsCompleteListener(this);
        socialNetwork.requestGetFriends();
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
        if (socialNetworkId == 6){
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
    public void OnGetFriendsIdComplete(int socialNetworkID, String[] friendsID) {
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(friendsID.length + " Friends");
    }

    @Override
    public void OnGetFriendsComplete(int socialNetworkID, ArrayList<SocialPerson> socialFriends) {
        this.socialPersons = socialFriends;
        FriendsListAdapter adapter = new FriendsListAdapter(getActivity(), socialFriends, socialNetworkID);
        listMenu.setAdapter(adapter);
    }

    @Override
    public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
        Toast.makeText(getActivity(), Constants.handleError(socialNetworkID, requestID, errorMessage),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(socialNetworkId != 4) {
            DetailedSocialInfoFragment friends = DetailedSocialInfoFragment.newInstannce(socialNetworkId, socialPersons.get(i).id);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .addToBackStack("info")
                    .replace(R.id.container, friends)
                    .commit();
        } else{
            Toast.makeText(getActivity(), Constants.socialName[socialNetworkId-1] + "SocialNetwork "
                    + "can't show custom SocialPerson", Toast.LENGTH_LONG).show();
        }
    }
}