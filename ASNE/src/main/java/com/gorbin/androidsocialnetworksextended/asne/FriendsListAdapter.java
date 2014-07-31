package com.gorbin.androidsocialnetworksextended.asne;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidsocialnetworks.lib.persons.SocialPerson;
import com.gorbin.androidsocialnetworksextended.asne.utils.Constants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendsListAdapter extends BaseAdapter {
    private final Activity context;
    private ViewHolder holder;
    private ArrayList<SocialPerson> friends;
    private int socialNetworkID;
    private boolean net_err;

    public FriendsListAdapter(Activity context, ArrayList<SocialPerson> friends, int socialNetworkID) {
        this.context = context;
        this.friends = friends;
        this.socialNetworkID = socialNetworkID;
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Object getItem(int i) {
        return friends.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        net_err = false;
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.friend_row, null, true);
            holder = new ViewHolder();
            holder.id = (TextView) convertView.findViewById(R.id.id);
            holder.label = (TextView) convertView.findViewById(R.id.label);
            holder.imageView = (ImageView) convertView.findViewById(R.id.image);
            holder.progress = (ProgressBar) convertView.findViewById(R.id.progress);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.label.setTextColor(context.getResources().getColor(Constants.color[socialNetworkID - 1]));
        holder.imageView.setBackgroundColor(context.getResources().getColor(Constants.color[socialNetworkID - 1]));
        holder.id.setText(friends.get(position).id);
        holder.label.setText(friends.get(position).name);
        holder.progress.setVisibility(View.VISIBLE);
        if (friends.get(position).avatarURL != null) {
            Picasso.with(context)
                    .load(friends.get(position).avatarURL)
                    .placeholder(Constants.userPhoto[socialNetworkID - 1])
                    .error(R.drawable.error)
                    .into(holder.imageView, new Callback() {

                        @Override
                        public void onSuccess() {
                            holder.progress.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError() {
                            net_err = true;
                            holder.progress.setVisibility(View.INVISIBLE);
                        }
                    });
        } else {
            holder.imageView.setImageResource(Constants.userPhoto[socialNetworkID-1]);
            holder.progress.setVisibility(View.INVISIBLE);
        }

        if (net_err)
            Toast.makeText(context,
                    "ERROR" + net_err, Toast.LENGTH_SHORT).show();
        return convertView;
    }

    static class ViewHolder {
        public ImageView imageView;
        public TextView id;
        public TextView label;
        public ProgressBar progress;
    }


}
