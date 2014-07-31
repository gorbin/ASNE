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

package com.vk.sdk.api;

/**
 * Constants for api. List is not full
 */
public class VKApiConst {
    //Commons
    public static final String USER_ID = "user_id";
    public static final String USER_IDS = "user_ids";
    public static final String FIELDS = "fields";
    public static final String SORT = "sort";
    public static final String OFFSET = "offset";
    public static final String COUNT = "count";
    public static final String OWNER_ID = "owner_id";

    //auth
    public static final String VERSION = "v";
    public static final String HTTPS = "https";
    public static final String LANG = "lang";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String SIG = "sig";

    //get users
    public static final String NAME_CASE = "name_case";

    //Get subscriptions
    public static final String EXTENDED = "extended";

    //Search
    public static final String Q = "q";
    public static final String CITY = "city";
    public static final String COUNTRY = "country";
    public static final String HOMETOWN = "hometown";
    public static final String UNIVERSITY_COUNTRY = "university_country";
    public static final String UNIVERSITY = "university";
    public static final String UNIVERSITY_YEAR = "university_year";
    public static final String SEX = "sex";
    public static final String STATUS = "status";
    public static final String AGE_FROM = "age_from";
    public static final String AGE_TO = "age_to";
    public static final String BIRTH_DAY = "birth_day";
    public static final String BIRTH_MONTH = "birth_month";
    public static final String BIRTH_YEAR = "birth_year";
    public static final String ONLINE = "online";
    public static final String HAS_PHOTO = "has_photo";
    public static final String SCHOOL_COUNTRY = "school_country";
    public static final String SCHOOL_CITY = "school_city";
    public static final String SCHOOL = "school";
    public static final String SCHOOL_YEAR = "school_year";
    public static final String RELIGION = "religion";
    public static final String INTERESTS = "interests";
    public static final String COMPANY = "company";
    public static final String POSITION = "position";
    public static final String GROUP_ID = "group_id";

    public static final String FRIENDS_ONLY = "friends_only";
    public static final String FROM_GROUP = "from_group";
    public static final String MESSAGE = "message";
    public static final String ATTACHMENTS = "attachments";
    public static final String SERVICES = "services";
    public static final String SIGNED = "signed";
    public static final String PUBLISH_DATE = "publish_date";
    public static final String LAT = "lat";
    public static final String LONG = "long";
    public static final String PLACE_ID = "place_id";
    public static final String POST_ID = "post_id";

    //Errors
    public static final String ERROR_CODE = "error_code";
    public static final String ERROR_MSG = "error_msg";
    public static final String REQUEST_PARAMS = "request_params";

    //Captcha
    public static final String CAPTCHA_IMG = "captcha_img";
    public static final String CAPTCHA_SID = "captcha_sid";
    public static final String CAPTCHA_KEY = "captcha_key";
    public static final String REDIRECT_URI = "redirect_uri";

    //Photos
    public static final String PHOTO = "photo";
    public static final String ALBUM_ID = "album_id";
    public static final String PHOTO_IDS = "photo_ids";
    public static final String PHOTO_SIZES = "photo_sizes";
    public static final String REV = "rev";
    public static final String FEED_TYPE = "feed_type";
    public static final String FEED = "feed";

    //Enums
    enum VKProgressType {
        VKProgressTypeUpload,
        VKProgressTypeDownload
    }

    //Events
    public static final String VKCaptchaAnsweredEvent = "VKCaptchaAnsweredEvent";
}
