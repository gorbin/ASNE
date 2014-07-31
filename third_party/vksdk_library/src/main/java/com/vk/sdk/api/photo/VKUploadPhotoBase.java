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

import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.httpClient.VKAbstractOperation;
import com.vk.sdk.api.httpClient.VKHttpClient;
import com.vk.sdk.api.httpClient.VKJsonOperation;
import com.vk.sdk.api.httpClient.VKJsonOperation.VKJSONOperationCompleteListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Provides common part of photo upload process
 */
public abstract class VKUploadPhotoBase extends VKRequest {
	private static final long serialVersionUID = -4566961568409572159L;
	/**
     * ID of album to upload
     */
    protected long mAlbumId;
    /**
     * ID of group to upload
     */
    protected long mGroupId;
    /**
     * ID of user wall to upload
     */
    protected long mUserId;
    /**
     * Image to upload
     */
    protected File mImage;

    protected abstract VKRequest getServerRequest();

    protected abstract VKRequest getSaveRequest(JSONObject response);

    public VKUploadPhotoBase() {
        super(null);
    }

    @Override
    public VKAbstractOperation getOperation() {
        return new VKUploadImageOperation();
    }

    protected class VKUploadImageOperation extends VKAbstractOperation {
		protected VKAbstractOperation lastOperation;

        @Override
        public void start() {
            final VKRequestListener originalListener = VKUploadPhotoBase.this.requestListener;

            VKUploadPhotoBase.this.requestListener = new VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    setState(VKOperationState.Finished);
                    response.request = VKUploadPhotoBase.this;
                    if (originalListener != null)
                        originalListener.onComplete(response);
                }

                @Override
                public void onError(VKError error) {
                    setState(VKOperationState.Finished);
                    error.request = VKUploadPhotoBase.this;
                    if (originalListener != null)
                        originalListener.onError(error);
                }

                @Override
                public void onProgress(VKProgressType progressType, long bytesLoaded,
                                       long bytesTotal) {
                    if (originalListener != null)
                        originalListener.onProgress(progressType, bytesLoaded, bytesTotal);
                }
            };
            setState(VKOperationState.Executing);

            VKRequest serverRequest = getServerRequest();

            serverRequest.setRequestListener(new VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    try {
                        VKJsonOperation postFileRequest = new VKJsonOperation(
                                VKHttpClient.fileUploadRequest(response.json.getJSONObject("response").getString("upload_url"), mImage));
                        postFileRequest.setJsonOperationListener(new VKJSONOperationCompleteListener() {
                            @Override
                            public void onComplete(VKJsonOperation operation,
                                                   JSONObject response) {

                                VKRequest saveRequest = getSaveRequest(response);
                                saveRequest.setRequestListener(new VKRequestListener() {
                                    @Override
                                    public void onComplete(VKResponse response) {
                                        requestListener.onComplete(response);
                                        setState(VKOperationState.Finished);
                                    }

                                    @Override
                                    public void onError(VKError error) {
                                        requestListener.onError(error);
                                    }
                                });
                                lastOperation = saveRequest.getOperation();
                                VKHttpClient.enqueueOperation(lastOperation);
                            }

                            @Override
                            public void onError(VKJsonOperation operation, VKError error) {
                                requestListener.onError(error);
                            }
                        });

                        lastOperation = postFileRequest;
                        VKHttpClient.enqueueOperation(lastOperation);
                    } catch (JSONException e) {
                        if (VKSdk.DEBUG)
                            e.printStackTrace();
                        VKError error = new VKError(VKError.VK_API_JSON_FAILED);
                        error.httpError = e;
                        error.errorMessage = e.getMessage();
                        requestListener.onError(error);
                    }
//					postFileRequest.progressBlock = _uploadRequest.progressBlock;
                }

                @Override
                public void onError(VKError error) {
                    if (requestListener != null)
                        requestListener.onError(error);
                }
            });
            lastOperation = serverRequest.getOperation();
            VKHttpClient.enqueueOperation(lastOperation);
        }

        @Override
        public void cancel() {
            if (lastOperation != null)
                lastOperation.cancel();
            super.cancel();
        }

        @Override
        public void finish() {
            super.finish();
            lastOperation = null;
        }
    }


}
