package com.hotelbeds.hackatravel.pathfinderapp.api;

import android.support.design.widget.Snackbar;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jariza on 23/02/2018.
 */
public class ServiceCalls {

    private AsyncHttpClient client;
    private String baseUri = "http://dirtydevelopers.org/";

    public ServiceCalls(){
        this.client = new AsyncHttpClient();
        client.addHeader("Content-Type", "application/json");
    }

    public AsyncHttpClient getClient() {
        return client;
    }

    public void setClient(AsyncHttpClient client) {
        this.client = client;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }
}
