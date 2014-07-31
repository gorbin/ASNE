package com.vk.sdk.api.methods;

import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.model.VKUsersArray;

/**
 * Section friends
 * Created by alex_xpert on 29.01.14.
 */
public class VKApiFriends extends VKApiBase {

    public VKRequest get(VKParameters params) {
        if (params.get("fields") != null) {
            return prepareRequest("get", params, VKRequest.HttpMethod.GET, VKUsersArray.class);
        } else {
            return prepareRequest("get", params);
        }
    }

    public VKRequest getOnline(VKParameters params) {
        return prepareRequest("getOnline", params);
    }

    public VKRequest getMutual(VKParameters params) {
        return prepareRequest("getMutual", params);
    }

    public VKRequest getRecent(VKParameters params) {
        return prepareRequest("getRecent", params);
    }

    public VKRequest getRequests(VKParameters params) {
        return prepareRequest("getRequests", params);
    }

    public VKRequest add(VKParameters params) {
        return prepareRequest("add", params);
    }

    public VKRequest edit(VKParameters params) {
        return prepareRequest("edit", params);
    }

    public VKRequest delete(VKParameters params) {
        return prepareRequest("delete", params);
    }

    public VKRequest getLists(VKParameters params) {
        return prepareRequest("getLists", params);
    }

    public VKRequest addList(VKParameters params) {
        return prepareRequest("addList", params);
    }

    public VKRequest editList(VKParameters params) {
        return prepareRequest("editList", params);
    }

    public VKRequest deleteList(VKParameters params) {
        return prepareRequest("deleteList", params);
    }

    public VKRequest getAppUsers(VKParameters params) {
        return prepareRequest("getAppUsers", params);
    }

    public VKRequest getByPhones(VKParameters params) {
        return prepareRequest("getByPhones", params, VKRequest.HttpMethod.GET, VKUsersArray.class);
    }

    public VKRequest deleteAllRequests(VKParameters params) {
        return prepareRequest("deleteAllRequests", params);
    }

    public VKRequest getSuggestions(VKParameters params) {
        return prepareRequest("getSuggestions", params);
    }

    public VKRequest areFriends(VKParameters params) {
        return prepareRequest("areFriends", params);
    }

}
