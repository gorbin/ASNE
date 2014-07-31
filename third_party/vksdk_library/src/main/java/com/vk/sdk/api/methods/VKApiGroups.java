package com.vk.sdk.api.methods;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.model.VKApiCommunityArray;
import com.vk.sdk.api.model.VKUsersArray;

/**
 * Section groups
 * Created by alex_xpert on 29.01.14.
 */
public class VKApiGroups extends VKApiBase {

    public VKRequest isMember(VKParameters params) {
        return prepareRequest("isMember", params);
    }

    public VKRequest getById(VKParameters params) {
        return prepareRequest("getById", params, VKRequest.HttpMethod.GET, VKApiCommunityArray.class);
    }

    public VKRequest get(VKParameters params) {
        if (((Integer) params.get("extended")) == 1) {
            return prepareRequest("get", params, VKRequest.HttpMethod.GET, VKApiCommunityArray.class);
        } else {
            return prepareRequest("get", params);
        }
    }

    public VKRequest getMembers(VKParameters params) {
        return prepareRequest("getMembers", params);
    }

    public VKRequest join(VKParameters params) {
        return prepareRequest("join", params);
    }

    public VKRequest leave(VKParameters params) {
        return prepareRequest("leave", params);
    }

    public VKRequest leave(final int group_id) {
        return prepareRequest("leave", new VKParameters() {
            {
                put(VKApiConst.GROUP_ID, String.valueOf(group_id));
            }
        });
    }

    public VKRequest search(VKParameters params) {
        return prepareRequest("search", params, VKRequest.HttpMethod.GET, VKApiCommunityArray.class);
    }

    public VKRequest getInvites(VKParameters params) {
        return prepareRequest("getInvites", params, VKRequest.HttpMethod.GET, VKApiCommunityArray.class);
    }

    public VKRequest banUser(VKParameters params) {
        return prepareRequest("banUser", params);
    }

    public VKRequest unbanUser(VKParameters params) {
        return prepareRequest("unbanUser", params);
    }

    public VKRequest getBanned(VKParameters params) {
        return prepareRequest("getBanned", params, VKRequest.HttpMethod.GET, VKUsersArray.class);
    }
}
