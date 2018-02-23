package com.hotelbeds.hackatravel.pathfinderapp.api;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ContentType;

/**
 * Created by jariza on 23/02/2018.
 */

public class PathFinderClient {
    private static final String BASE_URL = "http://dirtydevelopers.org/";
//    private static final String BASE_URL = "http://10.175.48.192:8080/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, HttpEntity entity, AsyncHttpResponseHandler responseHandler) {
        client.post(null, getAbsoluteUrl(url), entity, ContentType.APPLICATION_JSON.toString(), responseHandler);
    }

    public static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
