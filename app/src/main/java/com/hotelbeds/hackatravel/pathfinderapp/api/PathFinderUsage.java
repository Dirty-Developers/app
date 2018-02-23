package com.hotelbeds.hackatravel.pathfinderapp.api;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by jariza on 23/02/2018.
 */
public class PathFinderUsage extends PathFinderClient{
    public void getHealth() throws JSONException {
        PathFinderClient.get("health", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
            }
        });
    }
}
