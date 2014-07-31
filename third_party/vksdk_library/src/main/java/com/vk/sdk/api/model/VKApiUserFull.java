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

import org.json.JSONArray;
import org.json.JSONObject;

import static com.vk.sdk.api.model.ParseUtils.parseBoolean;
import static com.vk.sdk.api.model.ParseUtils.parseLong;

/**
 * Represents full user profile.
 */
@SuppressWarnings("unused")
public class VKApiUserFull extends VKApiUser implements android.os.Parcelable {

    /**
     * Filed last_seen from VK fields set
     */
    public static final String LAST_SEEN = "last_seen";

    /**
     * Filed bdate from VK fields set
     */
    public static final String BDATE = "bdate";

    /**
     * Filed city from VK fields set
     */
    public static final String CITY = "city";

    /**
     * Filed country from VK fields set
     */
    public static final String COUNTRY = "country";

    /**
     * Filed universities from VK fields set
     */
    public static final String UNIVERSITIES = "universities";

    /**
     * Filed schools from VK fields set
     */
    public static final String SCHOOLS = "schools";

    /**
     * Filed activity from VK fields set
     */
    public static final String ACTIVITY = "activity";

    /**
     * Filed personal from VK fields set
     */
    public static final String PERSONAL = "personal";

    /**
     * Filed sex from VK fields set
     */
    public static final String SEX = "sex";

    /**
     * Filed site from VK fields set
     */
    public static final String SITE = "site";

    /**
     * Filed contacts from VK fields set
     */
    public static final String CONTACTS = "contacts";

    /**
     * Filed can_post from VK fields set
     */
    public static final String CAN_POST = "can_post";

    /**
     * Filed can_see_all_posts from VK fields set
     */
    public static final String CAN_SEE_ALL_POSTS = "can_see_all_posts";

    /**
     * Filed can_write_private_message from VK fields set
     */
    public static final String CAN_WRITE_PRIVATE_MESSAGE = "can_write_private_message";

    /**
     * Filed relation from VK fields set
     */
    public static final String RELATION = "relation";

    /**
     * Filed counters from VK fields set
     */
    public static final String COUNTERS = "counters";

    /**
     * Filed activities from VK fields set
     */
    public static final String ACTIVITIES = "activities";

    /**
     * Filed interests from VK fields set
     */
    public static final String INTERESTS = "interests";

    /**
     * Filed movies from VK fields set
     */
    public static final String MOVIES = "movies";

    /**
     * Filed tv from VK fields set
     */
    public static final String TV = "tv";

    /**
     * Filed books from VK fields set
     */
    public static final String BOOKS = "books";

    /**
     * Filed games from VK fields set
     */
    public static final String GAMES = "games";

    /**
     * Filed about from VK fields set
     */
    public static final String ABOUT = "about";

    /**
     * Filed quotes from VK fields set
     */
    public static final String QUOTES = "quotes";

    /**
     * Filed connections from VK fields set
     */
    public static final String CONNECTIONS = "connections";

    /**
     * Filed relatives from VK fields set
     */
    public static final String RELATIVES = "relatives";

    /**
     * Filed wall_default from VK fields set
     */
    public static final String WALL_DEFAULT = "wall_default";

    /**
     * Filed verified from VK fields set
     */
    public static final String VERIFIED = "verified";

    /**
     * Filed screen_name from VK fields set
     */
    public static final String SCREEN_NAME = "screen_name";

    /**
     * Filed blacklisted_by_me from VK fields set
     */
    public static final String BLACKLISTED_BY_ME = "blacklisted_by_me";

    /**
     * Text of user status.
     */
    public String activity;

    /**
     * Audio which broadcasting to status.
     */
    public VKApiAudio status_audio;

    /**
     * User's date of birth.  Returned as DD.MM.YYYY or DD.MM (if birth year is hidden).
     */
    public String bdate;

    /**
     * City specified on user's page in "Contacts" section.
     */
    public VKApiCity city;

    /**
     * Country specified on user's page in "Contacts" section.
     */
    public VKApiCountry country;

    /**
     * Last visit date(in Unix time).
     */
    public long last_seen;

    /**
     * List of user's universities
     */
    public VKList<VKApiUniversity> universities;

    /**
     * List of user's schools
     */
    public VKList<VKApiSchool> schools;

    /**
     * Views on smoking.
     * @see {@link Attitude}
     */
    public int smoking;

    /**
     * Views on alcohol.
     * @see {@link Attitude}
     */
    public int alcohol;

    /**
     * Views on policy.
     * @see {@link com.vk.sdk.api.model.VKApiUserFull.Political}
     */
    public int political;

    /**
     * Life main stuffs.
     * @see {@link com.vk.sdk.api.model.VKApiUserFull.LifeMain}
     */
    public int life_main;

    /**
     * People main stuffs.
     * @see {@link com.vk.sdk.api.model.VKApiUserFull.PeopleMain}
     */
    public int people_main;

    /**
     * Stuffs that inspire the user.
     */
    public String inspired_by;

    /**
     * List of user's languages
     */
    public String[] langs;

    /**
     * Religion of user
     */
    public String religion;

    /**
     * Name of user's account in Facebook
     */
    public String facebook;

    /**
     * ID of user's facebook
     */
    public String facebook_name;

    /**
     * Name of user's account in LiveJournal
     */
    public String livejournal;

    /**
     * Name of user's account in Skype
     */
    public String skype;

    /**
     * URL of user's site
     */
    public String site;

    /**
     * Name of user's account in Twitter
     */
    public String twitter;

    /**
     * Name of user's account in Instagram
     */
    public String instagram;

    /**
     * User's mobile phone number
     */
    public String mobile_phone;

    /**
     * User's home phone number
     */
    public String home_phone;

    /**
     * Page screen name.
     */
    public String screen_name;

    /**
     * Nickname of user.
     */
    public String nickname;

    /**
     * User's activities
     */
    public String activities;

    /**
     * User's interests
     */
    public String interests;

    /**
     * User's favorite movies
     */
    public String movies;

    /**
     * User's favorite TV Shows
     */
    public String tv;

    /**
     * User's favorite books
     */
    public String books;

    /**
     * User's favorite games
     */
    public String games;

    /**
     * User's about information
     */
    public String about;

    /**
     * User's favorite quotes
     */
    public String quotes;

    /**
     * Information whether others can posts on user's wall.
     */
    public boolean can_post;

    /**
     * Information whether others' posts on user's wall can be viewed
     */
    public boolean can_see_all_posts;

    /**
     * Information whether private messages can be sent to this user.
     */
    public boolean can_write_private_message;

    /**
     * Information whether user can comment wall posts.
     */
    public boolean wall_comments;

    /**
     * Information whether the user is banned in VK.
     */
    public boolean is_banned;

    /**
     * Information whether the user is deleted in VK.
     */
    public boolean is_deleted;

    /**
     * Information whether the user's post of wall shows by default.
     */
    public boolean wall_default_owner;

    /**
     * Information whether the user has a verified page in VK
     */
    public boolean verified;

    /**
     * User sex.
     * @see {@link com.vk.sdk.api.model.VKApiUserFull.Sex}
     */
    public int sex;

    /**
     * Set of user's counters.
     */
    public Counters counters;

    /**
     * Relationship status.
     * @see {@link com.vk.sdk.api.model.VKApiUserFull.Relation}
     */
    public int relation;

    /**
     * List of user's relatives
     */
    public VKList<Relative> relatives;

    /**
     * Information whether the current user has add this user to the blacklist.
     */
    public boolean blacklisted_by_me;

    public VKApiUserFull parse(JSONObject user) {
        super.parse(user);

        // general
        last_seen = parseLong(user.optJSONObject(LAST_SEEN), "time");
        bdate = user.optString(BDATE);

        JSONObject city = user.optJSONObject(CITY);
        if(city != null) {
            this.city = new VKApiCity().parse(city);
        }
        JSONObject country = user.optJSONObject(COUNTRY);
        if(country != null) {
            this.country = new VKApiCountry().parse(country);
        }

        // education
        universities = new VKList<VKApiUniversity>(user.optJSONArray(UNIVERSITIES), VKApiUniversity.class);
        schools = new VKList<VKApiSchool>(user.optJSONArray(SCHOOLS), VKApiSchool.class);

        // status
        activity = user.optString(ACTIVITY);

        JSONObject status_audio = user.optJSONObject("status_audio");
        if(status_audio != null) this.status_audio = new VKApiAudio().parse(status_audio);

        // personal views
        JSONObject personal = user.optJSONObject(PERSONAL);
        if (personal != null) {
            smoking = personal.optInt("smoking");
            alcohol = personal.optInt("alcohol");
            political = personal.optInt("political");
            life_main = personal.optInt("life_main");
            people_main = personal.optInt("people_main");
            inspired_by = personal.optString("inspired_by");
            religion = personal.optString("religion");
            if (personal.has("langs")) {
                JSONArray langs = personal.optJSONArray("langs");
                if (langs != null) {
                    this.langs = new String[langs.length()];
                    for (int i = 0; i < langs.length(); i++) {
                        this.langs[i] = langs.optString(i);
                    }
                }
            }
        }

        // contacts
        facebook = user.optString("facebook");
        facebook_name = user.optString("facebook_name");
        livejournal = user.optString("livejournal");
        site = user.optString(SITE);
        screen_name = user.optString("screen_name", "id" + id);
        skype = user.optString("skype");
        mobile_phone = user.optString("mobile_phone");
        home_phone = user.optString("home_phone");
        twitter = user.optString("twitter");
        instagram = user.optString("instagram");

        // personal info
        about = user.optString(ABOUT);
        activities = user.optString(ACTIVITIES);
        books = user.optString(BOOKS);
        games = user.optString(GAMES);
        interests = user.optString(INTERESTS);
        movies = user.optString(MOVIES);
        quotes = user.optString(QUOTES);
        tv = user.optString(TV);

        // settings
        nickname = user.optString("nickname", null);
        can_post = parseBoolean(user, CAN_POST);
        can_see_all_posts = parseBoolean(user, CAN_SEE_ALL_POSTS);
        blacklisted_by_me = parseBoolean(user, BLACKLISTED_BY_ME);
        can_write_private_message = parseBoolean(user, CAN_WRITE_PRIVATE_MESSAGE);
        wall_comments = parseBoolean(user, WALL_DEFAULT);
        String deactivated = user.optString("deactivated");
        is_deleted = "deleted".equals(deactivated);
        is_banned = "banned".equals(deactivated);
        wall_default_owner = "owner".equals(user.optString(WALL_DEFAULT));
        verified = parseBoolean(user, VERIFIED);

        // other
        sex = user.optInt(SEX);
        JSONObject counters = user.optJSONObject(COUNTERS);
        if (counters != null) this.counters = new Counters(counters);

        relation = user.optInt(RELATION);

        if (user.has(RELATIVES)) {
            if (relatives == null) {
                relatives = new VKList<Relative>();
            }
            relatives.fill(user.optJSONArray(RELATIVES), Relative.class);
        }
        return this;
    }

    public static class Relative extends VKApiModel implements android.os.Parcelable, Identifiable {

        public int id;
        public String name;

        @Override
        public int getId() {
            return id;
        }

        @Override
        public Relative parse(JSONObject response) {
            id = response.optInt("id");
            name = response.optString("name");
            return this;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeString(this.name);
        }

        private Relative(Parcel in) {
            this.id = in.readInt();
            this.name = in.readString();
        }

        public static Creator<Relative> CREATOR = new Creator<Relative>() {
            public Relative createFromParcel(Parcel source) {
                return new Relative(source);
            }

            public Relative[] newArray(int size) {
                return new Relative[size];
            }
        };
    }

    public static class Counters implements android.os.Parcelable {
        /**
         * Count was not in server response.
         */
        public final static int NO_COUNTER = -1;

        public int albums = NO_COUNTER;
        public int videos = NO_COUNTER;
        public int audios = NO_COUNTER;
        public int notes = NO_COUNTER;
        public int friends = NO_COUNTER;
        public int photos = NO_COUNTER;
        public int groups = NO_COUNTER;
        public int online_friends = NO_COUNTER;
        public int mutual_friends = NO_COUNTER;
        public int user_videos = NO_COUNTER;
        public int followers = NO_COUNTER;
        public int subscriptions = NO_COUNTER;
        public int pages = NO_COUNTER;

        Counters(JSONObject counters) {
            albums = counters.optInt("albums", albums);
            audios = counters.optInt("audios", audios);
            followers = counters.optInt("followers", followers);
            photos = counters.optInt("photos", photos);
            friends = counters.optInt("friends", friends);
            groups = counters.optInt("groups", groups);
            mutual_friends = counters.optInt("mutual_friends", mutual_friends);
            notes = counters.optInt("notes", notes);
            online_friends = counters.optInt("online_friends", online_friends);
            user_videos = counters.optInt("user_videos", user_videos);
            videos = counters.optInt("videos", videos);
            subscriptions = counters.optInt("subscriptions", subscriptions);
            pages = counters.optInt("pages", pages);
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.albums);
            dest.writeInt(this.videos);
            dest.writeInt(this.audios);
            dest.writeInt(this.notes);
            dest.writeInt(this.friends);
            dest.writeInt(this.photos);
            dest.writeInt(this.groups);
            dest.writeInt(this.online_friends);
            dest.writeInt(this.mutual_friends);
            dest.writeInt(this.user_videos);
            dest.writeInt(this.followers);
            dest.writeInt(this.subscriptions);
            dest.writeInt(this.pages);
        }

        private Counters(Parcel in) {
            this.albums = in.readInt();
            this.videos = in.readInt();
            this.audios = in.readInt();
            this.notes = in.readInt();
            this.friends = in.readInt();
            this.photos = in.readInt();
            this.groups = in.readInt();
            this.online_friends = in.readInt();
            this.mutual_friends = in.readInt();
            this.user_videos = in.readInt();
            this.followers = in.readInt();
            this.subscriptions = in.readInt();
            this.pages = in.readInt();
        }

        public static Creator<Counters> CREATOR = new Creator<Counters>() {
            public Counters createFromParcel(Parcel source) {
                return new Counters(source);
            }

            public Counters[] newArray(int size) {
                return new Counters[size];
            }
        };
    }

    public static class Sex {
        private Sex() {
        }

        public static final int FEMALE = 1;
        public static final int MALE = 2;
    }

    public static class Relation {
        private Relation() {
        }

        public static final int SINGLE = 1;
        public static final int RELATIONSHIP = 2;
        public static final int ENGAGED = 3;
        public static final int MARRIED = 4;
        public static final int COMPLICATED = 5;
        public static final int SEARCHING = 6;
        public static final int IN_LOVE = 7;
    }

    public static class Attitude {
        private Attitude() {
        }

        public static final int VERY_NEGATIVE = 1;
        public static final int NEGATIVE = 2;
        public static final int COMPROMISABLE = 3;
        public static final int NEUTRAL = 4;
        public static final int POSITIVE = 5;
    }

    public static class Political {
        private Political() {
        }

        public static final int COMMUNNIST = 1;
        public static final int SOCIALIST = 2;
        public static final int CENTRIST = 3;
        public static final int LIBERAL = 4;
        public static final int CONSERVATIVE = 5;
        public static final int MONARCHIST = 6;
        public static final int ULTRACONSERVATIVE = 7;
        public static final int LIBERTARIAN = 8;
        public static final int APATHETIC = 9;
    }

    public static class LifeMain {
        private LifeMain() {
        }

        public static final int FAMILY_AND_CHILDREN = 1;
        public static final int CAREER_AND_MONEY = 2;
        public static final int ENTERTAINMENT_AND_LEISURE = 3;
        public static final int SCIENCE_AND_RESEARCH = 4;
        public static final int IMPROOVING_THE_WORLD = 5;
        public static final int PERSONAL_DEVELOPMENT = 6;
        public static final int BEAUTY_AND_ART = 7;
        public static final int FAME_AND_INFLUENCE = 8;
    }

    public static class PeopleMain {
        private PeopleMain() {
        }

        public static final int INTELLECT_AND_CREATIVITY = 1;
        public static final int KINDNESS_AND_HONESTLY = 2;
        public static final int HEALTH_AND_BEAUTY = 3;
        public static final int WEALTH_AND_POWER = 4;
        public static final int COURAGE_AND_PERSISTENCE = 5;
        public static final int HUMOR_AND_LOVE_FOR_LIFE = 6;
    }

    public static class RelativeType {
        private RelativeType() {
        }

        public static final String PARTNER = "partner";
        public static final String GRANDCHILD = "grandchild";
        public static final String GRANDPARENT = "grandparent";
        public static final String CHILD = "child";
        public static final String SUBLING = "sibling";
        public static final String PARENT = "parent";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.activity);
        dest.writeParcelable(this.status_audio, flags);
        dest.writeString(this.bdate);
        dest.writeParcelable(this.city, flags);
        dest.writeParcelable(this.country, flags);
        dest.writeLong(this.last_seen);
        dest.writeParcelable(this.universities, flags);
        dest.writeParcelable(this.schools, flags);
        dest.writeInt(this.smoking);
        dest.writeInt(this.alcohol);
        dest.writeInt(this.political);
        dest.writeInt(this.life_main);
        dest.writeInt(this.people_main);
        dest.writeString(this.inspired_by);
        dest.writeStringArray(this.langs);
        dest.writeString(this.religion);
        dest.writeString(this.facebook);
        dest.writeString(this.facebook_name);
        dest.writeString(this.livejournal);
        dest.writeString(this.skype);
        dest.writeString(this.site);
        dest.writeString(this.twitter);
        dest.writeString(this.instagram);
        dest.writeString(this.mobile_phone);
        dest.writeString(this.home_phone);
        dest.writeString(this.screen_name);
        dest.writeString(this.activities);
        dest.writeString(this.interests);
        dest.writeString(this.movies);
        dest.writeString(this.tv);
        dest.writeString(this.books);
        dest.writeString(this.games);
        dest.writeString(this.about);
        dest.writeString(this.quotes);
        dest.writeByte(can_post ? (byte) 1 : (byte) 0);
        dest.writeByte(can_see_all_posts ? (byte) 1 : (byte) 0);
        dest.writeByte(can_write_private_message ? (byte) 1 : (byte) 0);
        dest.writeByte(wall_comments ? (byte) 1 : (byte) 0);
        dest.writeByte(is_banned ? (byte) 1 : (byte) 0);
        dest.writeByte(is_deleted ? (byte) 1 : (byte) 0);
        dest.writeByte(wall_default_owner ? (byte) 1 : (byte) 0);
        dest.writeByte(verified ? (byte) 1 : (byte) 0);
        dest.writeInt(this.sex);
        dest.writeParcelable(this.counters, flags);
        dest.writeInt(this.relation);
        dest.writeParcelable(this.relatives, flags);
        dest.writeByte(blacklisted_by_me ? (byte) 1 : (byte) 0);
    }
    public VKApiUserFull() {}
    public VKApiUserFull(Parcel in) {
        super(in);
        this.activity = in.readString();
        this.status_audio = in.readParcelable(VKApiAudio.class.getClassLoader());
        this.bdate = in.readString();
        this.city = in.readParcelable(VKApiCity.class.getClassLoader());
        this.country = in.readParcelable(VKApiCountry.class.getClassLoader());
        this.last_seen = in.readLong();
        this.universities = in.readParcelable(VKList.class.getClassLoader());
        this.schools = in.readParcelable(VKList.class.getClassLoader());
        this.smoking = in.readInt();
        this.alcohol = in.readInt();
        this.political = in.readInt();
        this.life_main = in.readInt();
        this.people_main = in.readInt();
        this.inspired_by = in.readString();
        this.langs = in.createStringArray();
        this.religion = in.readString();
        this.facebook = in.readString();
        this.facebook_name = in.readString();
        this.livejournal = in.readString();
        this.skype = in.readString();
        this.site = in.readString();
        this.twitter = in.readString();
        this.instagram = in.readString();
        this.mobile_phone = in.readString();
        this.home_phone = in.readString();
        this.screen_name = in.readString();
        this.activities = in.readString();
        this.interests = in.readString();
        this.movies = in.readString();
        this.tv = in.readString();
        this.books = in.readString();
        this.games = in.readString();
        this.about = in.readString();
        this.quotes = in.readString();
        this.can_post = in.readByte() != 0;
        this.can_see_all_posts = in.readByte() != 0;
        this.can_write_private_message = in.readByte() != 0;
        this.wall_comments = in.readByte() != 0;
        this.is_banned = in.readByte() != 0;
        this.is_deleted = in.readByte() != 0;
        this.wall_default_owner = in.readByte() != 0;
        this.verified = in.readByte() != 0;
        this.sex = in.readInt();
        this.counters = in.readParcelable(Counters.class.getClassLoader());
        this.relation = in.readInt();
        this.relatives = in.readParcelable(VKList.class.getClassLoader());
        this.blacklisted_by_me = in.readByte() != 0;
    }

    public static Creator<VKApiUserFull> CREATOR = new Creator<VKApiUserFull>() {
        public VKApiUserFull createFromParcel(Parcel source) {
            return new VKApiUserFull(source);
        }

        public VKApiUserFull[] newArray(int size) {
            return new VKApiUserFull[size];
        }
    };
}
