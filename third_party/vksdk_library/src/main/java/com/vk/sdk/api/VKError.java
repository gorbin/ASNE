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

import android.net.Uri;

import com.vk.sdk.VKObject;
import com.vk.sdk.util.VKJsonHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * Class for presenting VK SDK and VK API errors
 */
public class VKError extends VKObject {
	public static final int VK_API_ERROR = -101;
    public static final int VK_API_CANCELED = -102;
    public static final int VK_API_REQUEST_NOT_PREPARED = -103;
    public static final int VK_API_JSON_FAILED = -104;
    public static final int VK_API_REQUEST_HTTP_FAILED = -105;

    /**
     * Contains system HTTP error
     */
    public Exception httpError;
    /**
     * Describes API error
     */
    public VKError apiError;
    /**
     * Request which caused error
     */
    public VKRequest request;
    /**
     * May contains such errors:<br/>
     * <b>HTTP status code</b> if HTTP error occured;<br/>
     * <b>VK_API_ERROR</b> if API error occured;<br/>
     * <b>VK_API_CANCELED</b> if request was canceled;<br/>
     * <b>VK_API_REQUEST_NOT_PREPARED</b> if error occured while preparing request;
     */
    public int errorCode;
    /**
     * API error message
     */
    public String errorMessage;
    /**
     * Reason for authorization fail
     */
    public String errorReason;
    /**
     * API parameters passed to request
     */
    public ArrayList<Map<String, String>> requestParams;
    /**
     * Captcha identifier for captcha-check
     */
    public String captchaSid;
    /**
     * Image for captcha-check
     */
    public String captchaImg;
    /**
     * Redirection address if validation check required
     */
    public String redirectUri;

    /**
     * Generate new error with code
     *
     * @param errorCode positive if it's an HTTP error. Negative if it's API or SDK error
     */
    public VKError(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Generate API error from JSON
     *
     * @param json Json description of VK API error
     */
    @SuppressWarnings("unchecked")
	public VKError(JSONObject json) throws JSONException {
        VKError internalError = new VKError(json.getInt(VKApiConst.ERROR_CODE));
        internalError.errorMessage = json.getString(VKApiConst.ERROR_MSG);
        internalError.requestParams = (ArrayList<Map<String, String>>) VKJsonHelper.toList(
                json.getJSONArray(VKApiConst.REQUEST_PARAMS));
        if (internalError.errorCode == 14) {
            internalError.captchaImg = json.getString(VKApiConst.CAPTCHA_IMG);
            internalError.captchaSid = json.getString(VKApiConst.CAPTCHA_SID);
        }
        if (internalError.errorCode == 17) {
            internalError.redirectUri = json.getString(VKApiConst.REDIRECT_URI);
        }

        this.errorCode = VK_API_ERROR;
        this.apiError = internalError;
    }

    /**
     * Generate API error from HTTP-query
     *
     * @param queryParams key-value parameters
     */
    public VKError(Map<String, String> queryParams) {
        this.errorCode = VK_API_ERROR;
        this.errorReason = queryParams.get("error_reason");
        this.errorMessage = Uri.decode(queryParams.get("error_description"));
    }

    /**
     * Repeats failed captcha request with user entered answer to captcha
     *
     * @param userEnteredCode answer for captcha
     */
    public void answerCaptcha(String userEnteredCode) {
        VKParameters params = new VKParameters();
        params.put(VKApiConst.CAPTCHA_SID, captchaSid);
        params.put(VKApiConst.CAPTCHA_KEY, userEnteredCode);
        request.addExtraParameters(params);
        request.repeat();
    }
    public static VKError getRegisteredError(long requestId) {
        return (VKError) getRegisteredObject(requestId);
    }

	@Override public String toString()
	{
		StringBuilder errorString = new StringBuilder("VKError (");
		switch (this.errorCode) {
			case VK_API_ERROR:
				errorString.append("API error: " + apiError.toString());
				break;
			case VK_API_CANCELED:
				errorString.append("Canceled");
				break;
			case VK_API_REQUEST_NOT_PREPARED:
				errorString.append("Request wasn't prepared");
				break;
			case VK_API_JSON_FAILED:
				errorString.append("JSON failed: ");
				if (errorReason != null)
					errorString.append(String.format("%s; ", errorReason));
				if (errorMessage != null)
					errorString.append(String.format("%s; ", errorMessage));
				break;
			case VK_API_REQUEST_HTTP_FAILED:
				errorString.append("HTTP failed: ");
				if (errorReason != null)
					errorString.append(String.format("%s; ", errorReason));
				if (errorMessage != null)
					errorString.append(String.format("%s; ", errorMessage));
				break;

			default:
				errorString.append(String.format("code: %d; ", errorCode));
				if (errorReason != null)
					errorString.append(String.format("reason: %s; ", errorReason));
				if (errorMessage != null)
					errorString.append(String.format("message: %s; ", errorMessage));
				break;

		}
		errorString.append(")");
		return errorString.toString();
	}
}
