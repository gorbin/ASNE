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

import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.model.VKCommentArray;
import com.vk.sdk.api.model.VKPostArray;

/**
 * Builds requests for API.wall part
 */
public class VKApiWall extends VKApiBase {

    public VKRequest get(VKParameters params) {
        if (((Integer) params.get("extended")) == 1) {
            return prepareRequest("get", params, VKRequest.HttpMethod.GET, VKPostArray.class);
        } else {
            return prepareRequest("get", params);
        }
    }

    public VKRequest getById(VKParameters params) {
        if (((Integer) params.get("extended")) == 1) {
            return prepareRequest("get", params, VKRequest.HttpMethod.GET, VKPostArray.class);
        } else {
            return prepareRequest("get", params);
        }
    }

    public VKRequest savePost(VKParameters params) {
        return prepareRequest("savePost", params);
    }


    public VKRequest post(VKParameters parameters) {
        return prepareRequest("post", parameters, VKRequest.HttpMethod.POST);
    }

    public VKRequest repost(VKParameters params) {
        return prepareRequest("repost", params);
    }

    public VKRequest getReposts(VKParameters params) {
        return prepareRequest("getReposts", params);
    }

    public VKRequest edit(VKParameters params) {
        return prepareRequest("edit", params);
    }

    public VKRequest delete(VKParameters params) {
        return prepareRequest("delete", params);
    }

    public VKRequest restore(VKParameters params) {
        return prepareRequest("restore", params);
    }

    public VKRequest getComments(VKParameters params) {
        return prepareRequest("getComments", params, VKRequest.HttpMethod.GET, VKCommentArray.class);
    }

    public VKRequest addComment(VKParameters params) {
        return prepareRequest("addComment", params);
    }

    public VKRequest editComment(VKParameters params) {
        return prepareRequest("editComment", params);
    }

    public VKRequest deleteComment(VKParameters params) {
        return prepareRequest("deleteComment", params);
    }

    public VKRequest restoreComment(VKParameters params) {
        return prepareRequest("restoreComment", params);
    }

    public VKRequest reportPost(VKParameters params) {
        return prepareRequest("reportPost", params);
    }

    public VKRequest reportComment(VKParameters params) {
        return prepareRequest("reportComment", params);
    }
}
