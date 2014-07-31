//
//  Copyright (c) 2014 VK.com
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy of
//  this software and associated documentation files (the "Software"), to deal in
//  the Software without restriction, including without limitation the rights to
//  use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
//  the Software, and to permit persons to whom the Software is furnished to do so,
//  subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in all
//  copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
//  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
//  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
//  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
//  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//

package com.vk.sdk.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.vk.sdk.api.model.VKAttachments.TYPE_ALBUM;

/**
 * Describes a photo album
 */
@SuppressWarnings("unused")
public class VKApiPhotoAlbum extends VKAttachments.VKApiAttachment implements Parcelable, Identifiable {

    /**
     * URL for empty album cover with max width at 75px
     */
    public final static String COVER_S = "http://vk.com/images/s_noalbum.png";

    /**
     * URL of empty album cover with max width at 130px
     */
    public final static String COVER_M = "http://vk.com/images/m_noalbum.png";

    /**
     * URL of empty album cover with max width at 604px
     */
    public final static String COVER_X = "http://vk.com/images/x_noalbum.png";

    /**
     * Album ID.
     */
    public int id;

    /**
     * Album title.
     */
    public String title;

    /**
     * Number of photos in the album.
     */
    public int size;

    /**
     * Privacy settings for the album.
     */
    public int privacy;

    /**
     * Album description.
     */
    public String description;

    /**
     * ID of the user or community that owns the album.
     */
    public int owner_id;

    /**
     * Whether a user can upload photos to this album(false — cannot, true — can).
     */
    public boolean can_upload;

    /**
     * Date (in Unix time) the album was last updated.
     */
    public long updated;

    /**
     * Album creation date (in Unix time).
     */
    public long created;

    /**
     * ID of the photo which is the cover.
     */
    public int thumb_id;

    /**
     * Link to album cover photo.
     */
    public String thumb_src;

    /**
     * Links to to cover photo.
     */
    public VKPhotoSizes photo = new VKPhotoSizes();

    /**
     * Creates a PhotoAlbum instance from JSONObject.
     */
    public VKApiPhotoAlbum parse(JSONObject from) {
        id = from.optInt("id");
        thumb_id = from.optInt("thumb_id");
        owner_id = from.optInt("owner_id");
        title = from.optString("title");
        description = from.optString("description");
        created = from.optLong("created");
        updated = from.optLong("updated");
        size = from.optInt("size");
        can_upload = ParseUtils.parseBoolean(from, "can_upload");
        thumb_src = from.optString("thumb_src");
        if(from.has("privacy")) {
            privacy = from.optInt("privacy");
        } else {
            privacy = VKPrivacy.parsePrivacy(from.optJSONObject("privacy_view"));
        }
        JSONArray sizes = from.optJSONArray("sizes");
        if(sizes != null) {
            photo.fill(sizes);
        } else {
            photo.add(VKApiPhotoSize.create(COVER_S, 75, 55));
            photo.add(VKApiPhotoSize.create(COVER_M, 130, 97));
            photo.add(VKApiPhotoSize.create(COVER_X, 432, 249));
            photo.sort();
        }
        return this;
    }

    /**
     * Creates a PhotoAlbum instance from Parcel.
     */
    public VKApiPhotoAlbum(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.size = in.readInt();
        this.privacy = in.readInt();
        this.description = in.readString();
        this.owner_id = in.readInt();
        this.can_upload = in.readByte() != 0;
        this.updated = in.readLong();
        this.created = in.readLong();
        this.thumb_id = in.readInt();
        this.thumb_src = in.readString();
        this.photo = in.readParcelable(VKPhotoSizes.class.getClassLoader());
    }

    /**
     * Creates empty PhotoAlbum instance.
     */
    public VKApiPhotoAlbum() {

    }

    public boolean isClosed() {
        return privacy != VKPrivacy.PRIVACY_ALL;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public CharSequence toAttachmentString() {
        return new StringBuilder(TYPE_ALBUM).append(owner_id).append('_').append(id);
    }

    @Override
    public String getType() {
        return TYPE_ALBUM;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeInt(this.size);
        dest.writeInt(this.privacy);
        dest.writeString(this.description);
        dest.writeInt(this.owner_id);
        dest.writeByte(can_upload ? (byte) 1 : (byte) 0);
        dest.writeLong(this.updated);
        dest.writeLong(this.created);
        dest.writeInt(this.thumb_id);
        dest.writeString(this.thumb_src);
        dest.writeParcelable(this.photo, flags);
    }

    public static Creator<VKApiPhotoAlbum> CREATOR = new Creator<VKApiPhotoAlbum>() {
        public VKApiPhotoAlbum createFromParcel(Parcel source) {
            return new VKApiPhotoAlbum(source);
        }

        public VKApiPhotoAlbum[] newArray(int size) {
            return new VKApiPhotoAlbum[size];
        }
    };

}
