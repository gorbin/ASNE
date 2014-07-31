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

package com.vk.sdk;

import com.vk.sdk.api.VKError;

/**
 * Global SDK events delegate interface.
 */
public abstract class VKSdkListener {
	/**
	 * Calls when user must perform captcha-check
	 * 
	 * @param captchaError error returned from API. You can load captcha image from
	 * <b>captchaImg</b> property. After user answered current
	 * captcha, call answerCaptcha: method with user entered answer.
	 */
	public abstract void onCaptchaError(VKError captchaError);

	/**
	 * Notifies listener about existing token has expired
	 * 
	 * @param expiredToken old token that has expired
	 */
	public abstract void onTokenExpired(VKAccessToken expiredToken);

	/**
	 * Notifies listener about user authorization cancelation
	 * 
	 * @param authorizationError error that describes authorization error
	 */
	public abstract void onAccessDenied(VKError authorizationError);

	/**
	 * Notifies listener about receiving new access token
	 * 
	 * @param newToken new token for API requests
	 */
	public void onReceiveNewToken(VKAccessToken newToken) {
	}

	/**
	 * Notifies listener about receiving predefined token
	 * 
	 * @param token used token for API requests
	 */
	public void onAcceptUserToken(VKAccessToken token) {
	}

	/**
	 * Notifies listener about renewing access token (for example, user passed validation)
	 *
	 * @param token used token for API requests
	 */
	public void onRenewAccessToken(VKAccessToken token) {
	}

}
