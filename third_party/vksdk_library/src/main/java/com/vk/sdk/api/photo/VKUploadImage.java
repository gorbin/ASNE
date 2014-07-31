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

import android.content.Context;
import android.graphics.Bitmap;

import com.vk.sdk.VKObject;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKUIHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Contains image data with image description
 */
public class VKUploadImage extends VKObject {
    /**
     * Bitmap representation of image
     */
    public final Bitmap mImageData;
    /**
     * Image basic info
     */
    public final VKImageParameters mParameters;

    public VKUploadImage(Bitmap data, VKImageParameters params) {
        mImageData = data;
        mParameters = params;
    }

    public File getTmpFile() {
        Context ctx = VKUIHelper.getTopActivity();
        File outputDir = null;
        if (ctx != null) {
            outputDir = ctx.getExternalCacheDir();
            if (outputDir == null)
                outputDir = ctx.getCacheDir();
        }
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile("tmpImg", String.format(".%s", mParameters.fileExtension()), outputDir);
            FileOutputStream fos = new FileOutputStream(tmpFile);
            if (mParameters.mImageType == VKImageParameters.VKImageType.Png)
                mImageData.compress(Bitmap.CompressFormat.PNG, 100, fos);
            else
                mImageData.compress(Bitmap.CompressFormat.JPEG, (int) (mParameters.mJpegQuality * 100), fos);
            fos.close();
        } catch (IOException ignored) {
            if (VKSdk.DEBUG)
                ignored.printStackTrace();
        }
        return tmpFile;
    }
}
