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

import com.vk.sdk.VKObject;

/**
 * Parameters used for uploading image into VK servers
 */
public class VKImageParameters extends VKObject {
    /**
     * Describes image representation type
     */
    enum VKImageType {
        /// Sets jpeg representation of image
        Jpg,
        /// Sets png representation of image
        Png
    }

    /**
     * Type of image compression. Can be <b>VKImageType.Jpg</b> or <b>VKImageType.Png</b>.
     */
    public VKImageType mImageType = VKImageType.Png;
    /**
     * Quality used for jpg compression. From 0.0 to 1.0
     */
    public float mJpegQuality;

    public static VKImageParameters pngImage() {
        VKImageParameters result = new VKImageParameters();
        result.mImageType = VKImageType.Png;
        return result;
    }

    public static VKImageParameters jpgImage(float quality) {
        VKImageParameters result = new VKImageParameters();
        result.mImageType = VKImageType.Jpg;
        result.mJpegQuality = quality;
        return result;
    }

    /**
     * Returns the file extension for specified parameters
     * @return "jpg", "png" or "file" if unknown
     */
    public String fileExtension() {
        switch (mImageType) {
            case Jpg:
                return "jpg";
            case Png:
                return "png";
            default:
                return "file";
        }
    }

    /**
     * Returns the mime type for specified parameters
     * @return "mage/jpeg", "mage/png" or "application/octet-stream"
     */
    public String mimeType() {
        switch (mImageType) {
            case Jpg:
                return "image/jpeg";
            case Png:
                return "image/png";
            default:
                return "application/octet-stream";
        }
    }
}
