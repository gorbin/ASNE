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

import android.content.Intent;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKObject;
import com.vk.sdk.VKOpenAuthActivity;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkVersion;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.httpClient.VKAbstractOperation;
import com.vk.sdk.api.httpClient.VKHttpClient;
import com.vk.sdk.api.httpClient.VKJsonOperation;
import com.vk.sdk.api.httpClient.VKJsonOperation.VKJSONOperationCompleteListener;
import com.vk.sdk.api.httpClient.VKModelOperation;
import com.vk.sdk.api.model.VKApiModel;
import com.vk.sdk.util.VKStringJoiner;
import com.vk.sdk.util.VKUtil;

import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Class for execution API-requests.
 */
public class VKRequest extends VKObject {


    public enum VKProgressType {
        Download,
        Upload
    }

    public enum HttpMethod {
        GET,
        POST
    }

    /**
     * Selected method name
     */
    public final String methodName;
    /**
     * HTTP method for loading
     */
    public final HttpMethod httpMethod;
    /**
     * Passed parameters for method
     */
    private final VKParameters mMethodParameters;
    /**
     * Method parametes with common parameters
     */
    private VKParameters mPreparedParameters;
    /**
     * HTTP loading operation
     */
    private VKAbstractOperation mLoadingOperation;
    /**
     * How much times request was loaded
     */
    private int mAttemptsUsed;

    /**
     * Requests that should be called after current request.
     */
    private ArrayList<VKRequest> mPostRequestsQueue;
    /**
     * Class for model parsing
     */
    private Class<? extends VKApiModel> mModelClass;

    /**
     * Response parser
     */
    private VKParser mModelParser;

    /**
     * Specify language for API request
     */
    private String mPreferredLang;

    /**
     * Specify listener for current request
     */
    public VKRequestListener requestListener;
    /**
     * Specify attempts for request loading if caused HTTP-error. 0 for infinite
     */
    public int attempts;
    /**
     * Use HTTPS requests (by default is YES). If http-request is impossible (user denied no https access), SDK will load https version
     */
    public boolean secure;
    /**
     * Sets current system language as default for API data
     */
    public boolean useSystemLanguage;
    /**
     * Set to false if you don't need automatic model parsing
     */
    public boolean parseModel;

    /**
     * @return Returns HTTP-method for current request
     */
    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    /**
     * @return Returns list of method parameters (without common parameters)
     */
    public VKParameters getMethodParameters() {
        return mMethodParameters;
    }

    /**
     * Creates new request with parameters. See documentation for methods here https://vk.com/dev/methods
     *
     * @param method API-method name, e.g. audio.get
     */
    public VKRequest(String method) {
        this(method, null);
    }

    /**
     * Creates new request with parameters. See documentation for methods here https://vk.com/dev/methods
     *
     * @param method     API-method name, e.g. audio.get
     * @param parameters method parameters
     */
    public VKRequest(String method, VKParameters parameters) {
        this(method, parameters, HttpMethod.GET);
    }

    /**
     * Creates new request with parameters. See documentation for methods here https://vk.com/dev/methods
     *
     * @param method     API-method name, e.g. audio.get
     * @param parameters method parameters
     * @param httpMethod HTTP method for execution, e.g. GET, POST
     */
    public VKRequest(String method, VKParameters parameters, HttpMethod httpMethod) {
        this.methodName = method;
        if (parameters == null) {
            parameters = new VKParameters();
        }
        this.mMethodParameters = new VKParameters(parameters);
        if (httpMethod == null)
            httpMethod = HttpMethod.GET;
        this.httpMethod = httpMethod;
        this.mAttemptsUsed = 0;

        this.secure = true;
        //By default there is 1 attempt for loading.
        this.attempts = 1;

        //If system language is not supported, we use english
        this.mPreferredLang = "en";
        //By default we use system language.
        this.useSystemLanguage = true;
    }

    /**
     * Creates new request with parameters. See documentation for methods here https://vk.com/dev/methods
     *
     * @param method     API-method name, e.g. audio.get
     * @param parameters method parameters
     * @param httpMethod HTTP method for execution, e.g. GET, POST
     * @param modelClass class for automatic parse
     */
    public VKRequest(String method, VKParameters parameters, HttpMethod httpMethod,
                     Class<? extends VKApiModel> modelClass) {
        this(method, parameters, httpMethod);
        setModelClass(modelClass);
    }

    /**
     * Executes that request, and returns result to blocks
     *
     * @param listener listener for request events
     */
    public void executeWithListener(VKRequestListener listener) {
        this.requestListener = listener;
        start();
    }

    public void setRequestListener(VKRequestListener listener) {
        this.requestListener = listener;
    }

    /**
     * Register current request for execute after passed request, if passed request is successful. If it's not, errorBlock will be called.
     *
     * @param request  after which request must be called that request
     * @param listener listener for request events
     */
    public void executeAfterRequest(VKRequest request, VKRequestListener listener) {
        this.requestListener = listener;
        request.addPostRequest(this);
    }

    private void addPostRequest(VKRequest postRequest) {
        if (mPostRequestsQueue == null) {
            mPostRequestsQueue = new ArrayList<VKRequest>();
        }
        mPostRequestsQueue.add(postRequest);
    }

    public VKParameters getPreparedParameters() {
        if (mPreparedParameters == null) {
            mPreparedParameters = new VKParameters(mMethodParameters);

            //Set current access token from SDK object
            VKAccessToken token = VKSdk.getAccessToken();
	        if (token != null)
                mPreparedParameters.put(VKApiConst.ACCESS_TOKEN, token.accessToken);
            if (!this.secure)
	            if (token != null && (token.secret != null || token.httpsRequired)) {
                    this.secure = true;
                }
            //Set actual version of API
            mPreparedParameters.put(VKApiConst.VERSION, VKSdkVersion.API_VERSION);
            //Set preferred language for request
            mPreparedParameters.put(VKApiConst.LANG, getLang());

            if (this.secure) {
                //If request is secure, we need all urls as https
                mPreparedParameters.put(VKApiConst.HTTPS, "1");
            }
            if (token != null && token.secret != null) {
                //If it not, generate signature of request
                String sig = generateSig(token);
                mPreparedParameters.put(VKApiConst.SIG, sig);
            }
            //From that moment you cannot modify parameters.
            //Specially for http loading
        }
        return mPreparedParameters;
    }

    /**
     * Prepares request for loading
     *
     * @return Prepared HttpUriRequest for that VKRequest
     */
    public HttpUriRequest getPreparedRequest() {
        HttpUriRequest request = VKHttpClient.requestWithVkRequest(this);
        if (request == null) {
            VKError error = new VKError(VKError.VK_API_REQUEST_NOT_PREPARED);
            provideError(error);
            return null;
        }
        return request;
    }

    public VKAbstractOperation getOperation() {
        if (this.parseModel) {
            if (this.mModelClass != null) {
                mLoadingOperation = new VKModelOperation(getPreparedRequest(), this.mModelClass);
            } else if (this.mModelParser != null){
                mLoadingOperation = new VKModelOperation(getPreparedRequest(), this.mModelParser);
            }
        }
        if (mLoadingOperation == null)
            mLoadingOperation = new VKJsonOperation(getPreparedRequest());
        ((VKJsonOperation) mLoadingOperation).setJsonOperationListener(
                new VKJSONOperationCompleteListener() {
                    @Override
                    public void onComplete(VKJsonOperation operation, JSONObject response) {
                        if (response.has("error")) {
                            try {
                                VKError error = new VKError(response.getJSONObject("error"));
                                if (processCommonError(error)) return;
                                provideError(error);
                            } catch (JSONException e) {
                                if (VKSdk.DEBUG)
                                    e.printStackTrace();
                            }

                            return;
                        }
                        provideResponse(response,
                                mLoadingOperation instanceof VKModelOperation ?
                                        ((VKModelOperation) mLoadingOperation).parsedModel :
                                        null);
                    }

                    @Override
                    public void onError(VKJsonOperation operation, VKError error) {
                        if (error.errorCode != VKError.VK_API_ERROR &&
                                operation != null && operation.response != null &&
                                operation.response.getStatusLine().getStatusCode() == 200) {
                            provideResponse(operation.getResponseJson(), null);
                            return;
                        }
                        if (attempts == 0 || ++mAttemptsUsed < attempts) {
                            if (requestListener != null)
                                requestListener.attemptFailed(VKRequest.this, mAttemptsUsed, attempts);
                            VKAbstractOperation.postInMainQueueDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    start();
                                }
                            });
                            return;
                        }
                        provideError(error);
                    }
                });
        return mLoadingOperation;
    }

    /**
     * Starts loading of prepared request. You can use it instead of executeWithResultBlock
     */
    public void start() {
        if ((mLoadingOperation = getOperation()) == null) {
            return;
        }
        VKHttpClient.enqueueOperation(mLoadingOperation);
    }

    /**
     * Repeats this request with initial parameters and blocks.
     * Used attempts will be set to 0.
     */
    public void repeat() {
        this.mAttemptsUsed = 0;
        this.mPreparedParameters = null;
        start();
    }

    /**
     * Cancel current request. Result will be not passed. errorBlock will be called with error code
     */
    public void cancel() {
        if (mLoadingOperation != null)
            mLoadingOperation.cancel();
        else
            provideError(new VKError(VKError.VK_API_CANCELED));
    }

    /**
     * Method used for errors processing
     *
     * @param error error caused by this request
     */
    private void provideError(final VKError error) {
        error.request = this;
        if (requestListener != null) {
            requestListener.onError(error);
        }
        if (mPostRequestsQueue != null && mPostRequestsQueue.size() > 0) {
            for (VKRequest postRequest : mPostRequestsQueue)
                if (postRequest.requestListener != null) postRequest.requestListener.onError(error);
        }
    }

    /**
     * Method used for response processing
     *
     * @param jsonResponse response from API
     * @param parsedModel  model parsed from json
     */
    private void provideResponse(final JSONObject jsonResponse, Object parsedModel) {
        final VKResponse response = new VKResponse();
        response.request = this;
        response.json = jsonResponse;
        response.parsedModel = parsedModel;

        if (mPostRequestsQueue != null && mPostRequestsQueue.size() > 0) {
            for (VKRequest request : mPostRequestsQueue) {
                request.start();
            }
        }

        if (requestListener != null) {
            requestListener.onComplete(response);
        }
    }

    /**
     * Adds additional parameter to that request
     *
     * @param key   parameter name
     * @param value parameter value
     */
    public void addExtraParameter(String key, Object value) {
        mMethodParameters.put(key, value);
    }

    /**
     * Adds additional parameters to that request
     *
     * @param extraParameters parameters supposed to be added
     */
    public void addExtraParameters(VKParameters extraParameters) {
        mMethodParameters.putAll(extraParameters);
    }

    private String generateSig(VKAccessToken token) {
        //Read description here https://vk.com/dev/api_nohttps
        //At first, we need key-value pairs in order of request
        String queryString = VKStringJoiner.joinParams(mPreparedParameters);
        //Then we generate "request string" /method/{METHOD_NAME}?{GET_PARAMS}{POST_PARAMS}
        queryString = String.format(Locale.US, "/method/%s?%s", methodName, queryString);
        return VKUtil.md5(queryString + token.secret);

    }

    private boolean processCommonError(VKError error) {
        if (error.errorCode == VKError.VK_API_ERROR) {
            if (error.apiError.errorCode == 14) {
                error.apiError.request = this;
                this.mLoadingOperation = null;
                VKSdk.instance().sdkListener().onCaptchaError(error.apiError);
                return true;
            } else if (error.apiError.errorCode == 16) {
                VKAccessToken token = VKSdk.getAccessToken();
                token.httpsRequired = true;
                repeat();
                return true;
            } else if (error.apiError.errorCode == 17) {
	            Intent i = new Intent(VKUIHelper.getTopActivity(), VKOpenAuthActivity.class);
	            i.putExtra(VKOpenAuthActivity.VK_EXTRA_VALIDATION_URL, error.apiError.redirectUri);
				VKUIHelper.getTopActivity().startActivityForResult(i, VKSdk.VK_SDK_REQUEST_CODE);
                return true;
            }
        }

        return false;
    }

    private String getLang() {
        String result = mPreferredLang;
        if (useSystemLanguage) {
            result = Locale.getDefault().getLanguage();
            if (result.equals("uk")) {
                result = "ua";
            }

            if (!Arrays.asList(new String[]{"ru", "en", "ua", "es", "fi", "de", "it"})
                    .contains(result)) {
                result = mPreferredLang;
            }
        }
        return result;
    }

    /**
     * Sets preferred language for api results.
     * @param lang Two letter language code. May be "ru", "en", "ua", "es", "fi", "de", "it"
     */
    public void setPreferredLang(String lang) {
        useSystemLanguage = false;
        mPreferredLang = lang;
    }

    /**
     * Sets class for parse object model
     * @param modelClass Class extends VKApiModel
     */
    public void setModelClass(Class<? extends VKApiModel> modelClass) {
        mModelClass = modelClass;
        if (mModelClass != null)
            parseModel = true;
    }

    public void setResponseParser(VKParser parser) {
        mModelParser = parser;
        if (mModelParser != null)
            parseModel = true;
    }

    /**
     * Extend listeners for requests from that class
     * Created by Roman Truba on 02.12.13.
     * Copyright (c) 2013 VK. All rights reserved.
     */
    public static abstract class VKRequestListener implements Serializable {
        /**
         * Called if there were no HTTP or API errors, returns execution result.
         *
         * @param response response from VKRequest
         */
        public void onComplete(VKResponse response) {
        }

        /**
         * Called when request has failed attempt, and ready to do next attempt
         *
         * @param request       Failed request
         * @param attemptNumber Number of failed attempt, started from 1
         * @param totalAttempts Total request attempts defined for request
         */
        public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
        }

        /**
         * Called immediately if there was API error, or after <b>attempts</b> tries if there was an HTTP error
         *
         * @param error error for VKRequest
         */
        public void onError(VKError error) {
        }

        /**
         * Specify progress for uploading or downloading. Useless for text requests (because gzip encoding bytesTotal will always return -1)
         *
         * @param progressType type of progress (upload or download)
         * @param bytesLoaded  total bytes loaded
         * @param bytesTotal   total bytes suppose to be loaded
         */
        public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
        }
    }
    public static VKRequest getRegisteredRequest(long requestId) {
        return (VKRequest) getRegisteredObject(requestId);
    }
}
