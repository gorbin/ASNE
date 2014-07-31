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

import android.app.Activity;
import android.content.Intent;

/**
 * Class for VK authorization and dialogs helping
 */
public class VKUIHelper {
    private static Activity mTopActivity;

    public static Activity getTopActivity() {
        return mTopActivity;
    }

    /**
     * Call it in onCreate for of activities where you using VK SDK
     * @param activity Your activity
     */
    public static void onCreate(Activity activity) {
        if (mTopActivity != activity)
            mTopActivity = activity;
    }
    /**
     * Call it in onResume for of activities where you using VK SDK
     * @param activity Your activity
     */
    public static void onResume(Activity activity) {
        if (mTopActivity != activity)
            mTopActivity = activity;
    }
    /**
     * Call it in onDestroy for of activities where you using VK SDK
     * @param activity Your activity
     */
    public static void onDestroy(Activity activity) {
        if (mTopActivity == activity)
            mTopActivity = null;
    }

    /**
     * Call it in onActivityResult of all activities where you using VK SDK
     * @param requestCode Request code for startActivityForResult
     * @param resultCode Result code of finished activity
     * @param data Result data
     */
    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VKSdk.VK_SDK_REQUEST_CODE) {
            VKSdk.processActivityResult(resultCode, data);
        }
    }
}
