package com.hotelbeds.hackatravel.pathfinderapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hotelbeds.hackatravel.pathfinderapp.api.PathFinderClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class DetailActivity extends AppCompatActivity {

    private AsyncHttpClient client = new AsyncHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        clickEvents(this);

        try {
            Bundle b = getIntent().getExtras();
            JSONObject detailData = new JSONObject(b.getString("detailData"));
            client.get(detailData.getString("photo"), new AsyncHttpResponseHandler() {

                final ImageView detailImg = (ImageView) findViewById(R.id.imgDetail);

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    Bitmap bmp = BitmapFactory.decodeStream(new ByteArrayInputStream(response));
                    detailImg.setImageBitmap(bmp);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    String uri = "@drawable/imagenotfound";  // where myresource (without the extension) is the file
                    int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                    Drawable res = getResources().getDrawable(imageResource);
                    detailImg.setImageDrawable(res);
                }

            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    JSONObject rq = new JSONObject();
                    JSONObject coordenadasOri = new JSONObject();
                    coordenadasOri.put("lon", "1321321");
                    coordenadasOri.put("lat", "36843218");

                    rq.put("origin", coordenadasOri);

                    JSONObject coordenadasDes = new JSONObject();
                    coordenadasDes.put("lon", "1321321");
                    coordenadasDes.put("lat", "36843218");
                    JSONObject destiantion = new JSONObject();
                    rq.put("destination", coordenadasDes);
                    rq.put("checkin", "");
                    rq.put("checkout", "");

                    StringEntity entity = new StringEntity(rq.toString());
                    PathFinderClient.post("agenda", entity, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                        }
                    });
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void clickEvents(DetailActivity detailActivity) {
        cancelButtonClickEvent(detailActivity);
    }

    private void cancelButtonClickEvent(final DetailActivity detailActivity) {
        Button cancellButton = (Button) detailActivity.findViewById(R.id.btnCancelAddDetail);
        cancellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailActivity.finish();
            }
        });

        FloatingActionButton fabDel = (FloatingActionButton) findViewById(R.id.fabDel);
        fabDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailActivity.finish();
            }
        });
    }

}
