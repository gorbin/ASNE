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

package com.vk.sdk.api.methods;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKParser;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKList;
import com.vk.sdk.api.model.VKUsersArray;

import org.json.JSONObject;

/**
 * Builds requests for API.users part
 */
public class VKApiUsers extends VKApiBase {
    /**
     * Returns basic information about current user
     *
     * @return Request for load
     */
    public VKRequest get() {
        return get(null);
    }

    /**
     * https://vk.com/dev/users.get
     *
     * @param params use parameters from description with VKApiConst class
     * @return Request for load
     */
    public VKRequest get(VKParameters params) {
        return prepareRequest("get", params, VKRequest.HttpMethod.GET, new VKParser() {
            @Override
            public Object createModel(JSONObject object) {
                return new VKList<VKApiUserFull>(object, VKApiUserFull.class);
            }
        });
    }

    /**
     * https://vk.com/dev/users.search
     *
     * @param params use parameters from description with VKApiConst class
     * @return Request for load
     */
    public VKRequest search(VKParameters params) {
        return prepareRequest("search", params, VKRequest.HttpMethod.GET, VKUsersArray.class);
    }

    /**
     * https://vk.com/dev/users.isAppUser
     *
     * @return Request for load
     */
    public VKRequest isAppUser() {
        return prepareRequest("isAppUser", null);
    }

    /**
     * https://vk.com/dev/users.isAppUser
     *
     * @param userID ID of user to check
     * @return Request for load
     */
    public VKRequest isAppUser(final int userID) {
        return prepareRequest("isAppUser",
                new VKParameters() {/**
					 * 
					 */
					private static final long serialVersionUID = 7458591447441581671L;

				{
                    put(VKApiConst.USER_ID, String.valueOf(userID));
                }});
    }

    /**
     * https://vk.com/dev/users.getSubscriptions
     *
     * @return Request for load
     */
    public VKRequest getSubscriptions() {
        return getSubscriptions(null);
    }

    /**
     * https://vk.com/dev/users.getSubscriptions
     *
     * @param params use parameters from description with VKApiConst class
     * @return Request for load
     */
    public VKRequest getSubscriptions(VKParameters params) {
        return prepareRequest("getSubscriptions", params);
    }

    /**
     * https://vk.com/dev/users.getFollowers
     *
     * @return Request for load
     */
    public VKRequest getFollowers() {
        return getFollowers(null);
    }

    /**
     * https://vk.com/dev/users.getFollowers
     *
     * @param params use parameters from description with VKApiConst class
     * @return Request for load
     */
    public VKRequest getFollowers(VKParameters params) {
        return prepareRequest("getFollowers", params);
    }

    /**
     * https://vk.com/dev/users.report
     * Created on 29.01.14.
     *
     * @param params use parameters from description with VKApiConst class
     * @return Request for load
     */
    public VKRequest report(VKParameters params) {
        return prepareRequest("report", params);
    }
}
