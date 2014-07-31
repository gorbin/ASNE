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

package com.vk.sdk.api.httpClient;

import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKDefaultParser;
import com.vk.sdk.api.VKParser;
import com.vk.sdk.api.model.VKApiModel;

import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONObject;

/**
 * Operation based on json operation with object-model parsing possibility
 */
public class VKModelOperation extends VKJsonOperation {
	protected final VKParser mParser;
    public Object parsedModel;

    /**
     * Create new model operation
     * @param uriRequest Prepared request
     * @param modelClass Model for parsing response
     */
    public VKModelOperation(HttpUriRequest uriRequest, Class<? extends VKApiModel> modelClass) {
        super(uriRequest);
        mParser = new VKDefaultParser(modelClass);
    }
    /**
     * Create new model operation
     * @param uriRequest Prepared request
     * @param parser Parser for create response
     */
    public VKModelOperation(HttpUriRequest uriRequest, VKParser parser) {
        super(uriRequest);
        mParser = parser;
    }

    @Override
    protected boolean postExecution() {
        if (!super.postExecution())
            return false;

        if (mParser != null) {
            try {
                JSONObject response = getResponseJson();
                parsedModel = mParser.createModel(response);
                return true;
            } catch (Exception e) {
                if (VKSdk.DEBUG)
                    e.printStackTrace();
            }
        }
        return false;
    }
}
