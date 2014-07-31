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

import com.vk.sdk.api.VKError;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;

/**
 * Class for loading any data by HTTP request
 */
public class VKHttpOperation extends VKAbstractOperation {
	/**
     * Request initialized this object
     */
    private final HttpUriRequest mUriRequest;
    /**
     * Last exception throws while loading or parsing
     */
    protected Exception mLastException;
    /**
     * Bytes of HTTP response
     */
    private byte[] mResponseBytes;

    /**
     * Stream for output result of HTTP loading
     */
    public OutputStream outputStream;
    /**
     * Standard HTTP response
     */
    public HttpResponse response;

    /**
     * String representation of response
     */
    private String mResponseString;

    /**
     * Create new operation for loading prepared Http request. Requests may be prepared in VKHttpClient
     *
     * @param uriRequest Prepared request
     */
    public VKHttpOperation(HttpUriRequest uriRequest) {
        mUriRequest = uriRequest;
    }


    /**
     * Start current prepared http-operation for result
     */
    @Override
    public void start() {
        setState(VKOperationState.Executing);
        try {
            response = VKHttpClient.getClient().execute(mUriRequest);
            InputStream inputStream = response.getEntity().getContent();
            Header contentEncoding = response.getFirstHeader("Content-Encoding");
            if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                inputStream = new GZIPInputStream(inputStream);
            }

            if (outputStream == null) {
                outputStream = new ByteArrayOutputStream();
            }

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1)
                outputStream.write(buffer, 0, bytesRead);
            inputStream.close();
            outputStream.flush();
            if (outputStream instanceof ByteArrayOutputStream) {
                mResponseBytes = ((ByteArrayOutputStream) outputStream).toByteArray();
            }
            outputStream.close();
        } catch (Exception e) {
            mLastException = e;
        }
        finish();
    }

    @Override
    public void finish() {
        postExecution();
        super.finish();
    }

    /**
     * Calls before providing result, but after response loads
     * @return true is post execution succeed
     */
    protected boolean postExecution() {
        return true;
    }

    /**
     * Cancel current operation execution
     */
    @Override
    public void cancel() {
        mUriRequest.abort();
        super.cancel();
    }

    /**
     * Get operation response data
     * @return Bytes of response
     */
    public byte[] getResponseData() {
        return mResponseBytes;
    }

    /**
     * Get operation response string, if possible
     * @return Encoded string from response data bytes
     */
    public String getResponseString() {
        if (mResponseBytes == null)
            return null;
        if (mResponseString == null) {
            try {
                mResponseString = new String(mResponseBytes, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                mLastException = e;
            }
        }
        return mResponseString;
    }

    /**
     * Generates VKError about that request fails
     * @param e Exception for error
     * @return New generated error
     */
    protected VKError generateError(Exception e) {
        VKError error = new VKError(VKError.VK_API_REQUEST_HTTP_FAILED);
        error.errorMessage = e.getMessage();
        if (error.errorMessage == null)
            error.errorMessage = e.toString();
        error.httpError = e;
        return error;
    }

    /**
     * Set listener for current operation
     * @param listener Listener subclasses VKHTTPOperationCompleteListener
     */
    public void setHttpOperationListener(final VKHTTPOperationCompleteListener listener) {
        this.setCompleteListener(new VKOperationCompleteListener() {
            @Override
            public void onComplete() {
                if (mLastException != null) {
                    listener.onError(VKHttpOperation.this, generateError(mLastException));
                } else {
                    listener.onComplete(VKHttpOperation.this, mResponseBytes);
                }
            }
        });
    }

    /**
     * Class representing operation listener for VKHttpOperation
     */
    public static abstract class VKHTTPOperationCompleteListener extends VKAbstractCompleteListener<VKHttpOperation, byte[]>
    {
        @Override
        public void onComplete(VKHttpOperation operation, byte[] response) {
        }

        @Override
        public void onError(VKHttpOperation operation, VKError error) {
        }
    }
}
