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

package com.vk.sdk.api.photo;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.util.VKJsonHelper;
import com.vk.sdk.util.VKUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Special request for upload photos to specified album
 */
public class VKUploadAlbumPhotoRequest extends VKUploadPhotoBase {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5439648671595840976L;

	public VKUploadAlbumPhotoRequest(File image, long albumId, long groupId) {
        super();
        mAlbumId = albumId;
        mGroupId = groupId;
        mImage = image;
    }

    public VKUploadAlbumPhotoRequest(VKUploadImage image, long albumId, long groupId) {
        super();
        mAlbumId = albumId;
        mGroupId = groupId;
        mImage = image.getTmpFile();
    }

    protected VKRequest getServerRequest() {
        if (mAlbumId != 0 && mGroupId != 0)
            return VKApi.photos().getUploadServer(mAlbumId, mGroupId);
        return VKApi.photos().getUploadServer(mAlbumId);
    }

    @Override
    protected VKRequest getSaveRequest(JSONObject response) {
        VKRequest saveRequest;
        try {
            saveRequest = VKApi.photos().save(new VKParameters(VKJsonHelper.toMap(response)));
        } catch (JSONException e) {
            return null;
        }
        if (mAlbumId != 0)
            saveRequest.addExtraParameters(VKUtil.paramsFrom(VKApiConst.ALBUM_ID, mAlbumId));
        if (mGroupId != 0)
            saveRequest.addExtraParameters(VKUtil.paramsFrom(VKApiConst.GROUP_ID, mGroupId));
        return saveRequest;

    }

}
