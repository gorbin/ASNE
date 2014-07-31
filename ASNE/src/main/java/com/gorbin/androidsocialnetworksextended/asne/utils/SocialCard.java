package com.gorbin.androidsocialnetworksextended.asne.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.IconButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gorbin.androidsocialnetworksextended.asne.R;
import com.squareup.picasso.Picasso;

public class SocialCard extends RelativeLayout {
    private TextView name;
	private TextView id;
	public IconButton connect;
	public IconButton share;
    public IconButton friends;
    private ImageView image;
	private ImageView divider;
	private LinearLayout buttonLayout;
	private int darkColor;
	private int textColor;
	private int color;
    private int buttonTextColor;
	private int profileImage;
    private Context context;
    public Button detail;

    public SocialCard(Context context, AttributeSet st) {
        super(context, st);
        this.context = context;
        if(!isInEditMode()){
            TypedArray typedAttrs = context.obtainStyledAttributes(st,
            R.styleable.social_card, 0, 0);
            color = typedAttrs.getColor(R.styleable.social_card_color,
            getResources().getColor(R.color.green));
            textColor = typedAttrs.getColor(R.styleable.social_card_text_color,
            getResources().getColor(R.color.green));
            darkColor = typedAttrs.getColor(R.styleable.social_card_dark_color,
            getResources().getColor(R.color.grey_light));
            buttonTextColor = typedAttrs.getColor(R.styleable.social_card_button_text_color,
            getResources().getColor(R.color.gallery_white));
            profileImage = typedAttrs.getResourceId(R.styleable.social_card_profile_image,
            R.drawable.user);
    //		typedAttrs.recycle();
            init(context);}
    }

    private void init(Context context) {
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.card_social, this);
		name = (TextView) findViewById(R.id.name);
        id = (TextView) findViewById(R.id.id);
        detail = (Button) findViewById(R.id.minfo);
		connect = (IconButton) findViewById(R.id.connect);
        friends = (IconButton) findViewById(R.id.info);
		share = (IconButton) findViewById(R.id.share);
        image = (ImageView) findViewById(R.id.image);
		divider = (ImageView) findViewById(R.id.divider);
		buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);

        setColors(color, textColor, darkColor, buttonTextColor);
        image.setImageResource(profileImage);
		share.setText("{icon-share} Share");
        friends.setText("{icon-friends} Friends");
	}
    public void setColors(int color, int textColor, int darkColor){
        setColors(textColor, color, darkColor, getResources().getColor(R.color.gallery_white));
    }
	public void setColors(int color, int textColor, int darkColor, int buttonTextColor){
        name.setTextColor(textColor);
        divider.setBackgroundColor(color);
        detail.setTextColor(color);
        buttonLayout.setBackgroundColor(darkColor);
        connect.setBackgroundColor(color);
        share.setBackgroundColor(color);
        friends.setBackgroundColor(color);
        friends.setTextColor(buttonTextColor);
        connect.setTextColor(buttonTextColor);
        share.setTextColor(buttonTextColor);
        image.setBackgroundColor(color);
    }
	public void setName(String nameText){
		name.setText(nameText);
	}
	
	public void setId(String idString){
		id.setText(idString);
	}
	
	public void setConnectButtonText(String connectText){
		connect.setText(connectText);
	}
	
	public void setShareButtonText(String shareText){
		share.setText(shareText);
	}

    public void setDetailButtonText(String detailText){
        detail.setText(detailText);
    }
	
	public void setImage(String uri, int placeholder, int error){
		Picasso.with(context)
            .load(uri)
            .placeholder(placeholder)
            .error(error)
            .into(image);
	}
    public void setImageResource(int imageRes){
        image.setImageResource(imageRes);
    }

}