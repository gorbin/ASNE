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

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;

import com.vk.sdk.VKSdkVersion;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.util.VKStringJoiner;
import com.vk.sdk.util.VKUtil;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Class provides configured http client for API request loading
 */
public class VKHttpClient extends DefaultHttpClient {
    private static VKHttpClient sInstance;

    /**
     * Default constructor from basic class
     *
     * @param conman The connection manager
     * @param params The parameters
     */
    private VKHttpClient(ClientConnectionManager conman, HttpParams params) {
        super(conman, params);
    }

    /**
     * Creates the http client (if need). Returns reusing client
     *
     * @return Prepared client used for API requests loading
     */
    public static VKHttpClient getClient() {
        if (sInstance == null) {
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("http",
                    PlainSocketFactory.getSocketFactory(), 80));
            schemeRegistry.register(new Scheme("https",
                    SSLSocketFactory.getSocketFactory(), 443));
            HttpParams params = new BasicHttpParams();
            Context ctx = VKUIHelper.getTopActivity();

            try {
                if (ctx != null)
                {
                    PackageManager packageManager = ctx.getPackageManager();
                    if (packageManager != null) {
                        PackageInfo info = packageManager.getPackageInfo(ctx.getPackageName(), 0);
                        params.setParameter(CoreProtocolPNames.USER_AGENT,
                                String.format(Locale.US,
                                        "%s/%s (%s; Android %d; Scale/%.2f; VK SDK %s; %s)",
                                        VKUtil.getApplicationName(ctx), info.versionName,
                                        Build.MODEL, Build.VERSION.SDK_INT,
                                        ctx.getResources().getDisplayMetrics().density,
                                        VKSdkVersion.SDK_VERSION,
                                        info.packageName));
                    }
                }
            } catch (Exception ignored) {
            }
            sInstance = new VKHttpClient(new ThreadSafeClientConnManager(params, schemeRegistry),
                    params);
        }
        return sInstance;
    }

    /**
     * Prepares new "normal" request from VKRequest
     *
     * @param vkRequest Request, created for some method
     * @return Prepared request for creating VKHttpOperation
     */
    public static HttpUriRequest requestWithVkRequest(VKRequest vkRequest) {
        HttpUriRequest request = null;
        VKParameters preparedParameters = vkRequest.getPreparedParameters();
        StringBuilder urlStringBuilder = new StringBuilder(
                String.format(Locale.US, "http%s://api.vk.com/method/%s",
                        vkRequest.secure ? "s" : "", vkRequest.methodName));
        switch (vkRequest.httpMethod) {
            case GET:
                if (preparedParameters.size() > 0) {
                    urlStringBuilder.append("?").append(VKStringJoiner.joinUriParams(preparedParameters));
                }
                request = new HttpGet(urlStringBuilder.toString());
                break;

            case POST:
                HttpPost post = new HttpPost(urlStringBuilder.toString());
                ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>(preparedParameters.size());
                for (Map.Entry<String, Object> entry : preparedParameters.entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof VKAttachments) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), ((VKAttachments)value).toAttachmentsString()));
                    }
                    else if (value instanceof Collection) {
                        Collection<?> values = (Collection<?>) value;
                        for (Object v : values) {
                            // This will add a parameter for each value in the Collection/List
                            pairs.add(new BasicNameValuePair(String.format("%s[]", entry.getKey()), v == null ? null : String.valueOf(v)));
                        }
                    } else {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value == null ? null : String.valueOf(value)));
                    }
                }
                try {
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, "UTF-8");
                    post.setEntity(entity);
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
                request = post;
                break;
        }
        Map<String, String> defaultHeaders = getDefaultHeaders();
        for (String key : defaultHeaders.keySet()) {
            request.addHeader(key, defaultHeaders.get(key));
        }

        return request;
    }

    public static HttpPost fileUploadRequest(String uploadUrl, File... files) {
        HttpPost post = new HttpPost(uploadUrl);
        post.setEntity(new VKMultipartEntity(files));
        return post;
    }

    /**
     * Returns map of default headers for any request
     *
     * @return Map of default headers
     */
    private static Map<String, String> getDefaultHeaders() {
        return new HashMap<String, String>() {/**
			 * 
			 */
			private static final long serialVersionUID = 200199014417610665L;

		{
            put("Accept-Encoding", "gzip");
        }};
    }

    /**
     * Executor for performing requests in background
     */
    private static final Executor mBackgroundExecutor = Executors.newCachedThreadPool();

    /**
     * Starts operation in the one of network threads
     *
     * @param operation Operation to start
     */
    public static void enqueueOperation(final VKAbstractOperation operation) {
        //Check thread
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            mBackgroundExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    enqueueOperation(operation);
                }
            });
            return;
        }

        operation.start();
    }


}
