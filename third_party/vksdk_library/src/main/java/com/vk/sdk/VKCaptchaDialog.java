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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vk.sdk.api.VKError;
import com.vk.sdk.api.httpClient.VKHttpClient;
import com.vk.sdk.api.httpClient.VKHttpOperation;
import com.vk.sdk.api.httpClient.VKHttpOperation.VKHTTPOperationCompleteListener;

import org.apache.http.client.methods.HttpGet;

/**
 * Dialog fo displaying captcha
 */
public class VKCaptchaDialog {
    private final VKError mCaptchaError;
    private EditText mCaptchaAnswer;
    private ImageView mCaptchaImage;
    private ProgressBar mProgressBar;
    private float mDensity;

    public VKCaptchaDialog(VKError captchaError) {
        mCaptchaError = captchaError;
    }

    /**
     * Prepare, create and show dialog for displaying captcha
     */
    public void show() {
        Context context = VKUIHelper.getTopActivity();
        View innerView = LayoutInflater.from(context).inflate(R.layout.dialog_vkcaptcha, null);
        assert innerView != null;
        mCaptchaAnswer = (EditText) innerView.findViewById(R.id.captchaAnswer);
        mCaptchaImage  = (ImageView) innerView.findViewById(R.id.imageView);
        mProgressBar   = (ProgressBar) innerView.findViewById(R.id.progressBar);

        mDensity = context.getResources().getDisplayMetrics().density;
        final AlertDialog dialog = new AlertDialog.Builder(context).setView(innerView).create();
        mCaptchaAnswer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        mCaptchaAnswer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendAnswer();
                    return true;
                }
                return false;
            }
        });

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sendAnswer();
                    }
                });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                mCaptchaError.request.cancel();
            }
        });
        loadImage();
        dialog.show();
    }
    private void sendAnswer() {
        mCaptchaError.answerCaptcha(mCaptchaAnswer.getText() != null ? mCaptchaAnswer.getText().toString() : "");
    }
    private void loadImage() {
        VKHttpOperation imageOperation = new VKHttpOperation(new HttpGet(mCaptchaError.captchaImg));
        imageOperation.setHttpOperationListener(new VKHTTPOperationCompleteListener() {
            @Override
            public void onComplete(VKHttpOperation operation, byte[] response) {
                Bitmap captchaImage = BitmapFactory.decodeByteArray(response, 0, response.length);
                captchaImage = Bitmap.createScaledBitmap(captchaImage, (int) (captchaImage.getWidth() * mDensity), (int) (captchaImage.getHeight() * mDensity), true);
                mCaptchaImage.setImageBitmap(captchaImage);
                mCaptchaImage.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(VKHttpOperation operation,VKError error) {
                loadImage();
            }
        });
        VKHttpClient.enqueueOperation(imageOperation);
    }
}
