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
import com.vk.sdk.api.VKParser;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.model.VKApiModel;

import java.util.Locale;

/**
 * Basic class for all API-requests builders (parts)
 */
class VKApiBase {
    /**
     * Selected methods group
     */
    private String mMethodGroup;

    VKApiBase() {
        String className = this.getClass().getSimpleName();
        if (className == null) {
            throw new ClassCastException("Enclosing classes denied");
        }
        mMethodGroup = className.substring("VKApi".length()).toLowerCase();
    }

    VKRequest prepareRequest(String methodName, VKParameters methodParameters) {
        return prepareRequest(methodName, methodParameters, VKRequest.HttpMethod.GET);
    }

    VKRequest prepareRequest(String methodName, VKParameters methodParameters,
                             VKRequest.HttpMethod httpMethod) {
        return prepareRequest(methodName, methodParameters, httpMethod, (VKParser)null);
    }

    VKRequest prepareRequest(String methodName, VKParameters methodParameters,
                             VKRequest.HttpMethod httpMethod,
                             Class<? extends VKApiModel> modelClass) {
        return new VKRequest(String.format(Locale.US, "%s.%s", mMethodGroup, methodName),
                methodParameters, httpMethod, modelClass);
    }
    VKRequest prepareRequest(String methodName, VKParameters methodParameters,
                             VKRequest.HttpMethod httpMethod,
                             VKParser responseParser) {
        VKRequest result = new VKRequest(String.format(Locale.US, "%s.%s", mMethodGroup, methodName),
                methodParameters, httpMethod);
        result.setResponseParser(responseParser);
        return result;
    }
}
