package com.hotelbeds.hackatravel.pathfinderapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

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
    private JSONObject detailData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        clickEvents(this);

        try {
            Bundle b = getIntent().getExtras();
            detailData = new JSONObject(b.getString("detailData"));
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
            public void onClick(final View view) {
                try {

////                                {
////                                    id: [optional]
////                                    name: [optional]
////                                    event: [
////                                    {
////                                        id, checkin, checkout, name,  lon, lat, type
////                                    }
////                                    ],...
////                                }

                    JSONObject agenda = new JSONObject();
                    agenda.put("title", detailData.getString("agendaName"));
                    agenda.put("user_id", 1);
                    JSONArray events = new JSONArray();
                    JSONObject event = new JSONObject();
                    event.put("title", detailData.getString("name"));
                    event.put("checkin", ((JSONObject) ((JSONArray) detailData.get("avail")).get(0)).get("date"));
                    event.put("checkout", ((JSONObject) ((JSONArray) detailData.get("avail")).get(0)).get("date"));
                    event.put("id", detailData.get("id"));
                    event.put("type", detailData.get("type"));
                    event.put("longitude", detailData.get("lon"));
                    event.put("latitude", detailData.get("lat"));
                    events.put(event);
                    agenda.put("events", events);
                    StringEntity entity = new StringEntity(agenda.toString());
                    PathFinderClient.post("agenda", entity, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                SharedPreferences sharedPref = DetailActivity.this.getPreferences(Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putInt("agendaId", Integer.parseInt(response.getString("id"))).apply();
                                Toast.makeText(DetailActivity.this, "The agenda was added succesfully!!!", Toast.LENGTH_LONG).show();
                                DetailActivity.this.finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            try {
                                Snackbar.make(view, errorResponse.getString("error"), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

//                    JSONObject rq = new JSONObject();
//                    rq.put("lon", Double.parseDouble(detailData.getString("lon")));
//                    rq.put("lat", Double.parseDouble(detailData.getString("lat")));
//                    rq.put("checkin", "15-05-2018");
//                    rq.put("checkout", "20-05-2018");
//
//                    StringEntity entity = new StringEntity(rq.toString());
//                    PathFinderClient.post("ancillaries", entity, new JsonHttpResponseHandler() {
//                        @Override
//                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                            try {
//
//                                JSONArray hotels = ((JSONArray) response.get("hotels"));
//                                for (int i = 0; i < hotels.length(); i++) {
//                                    JSONObject hotel = (JSONObject) hotels.get(i);
//                                    BitmapDescriptor iconHotel = BitmapDescriptorFactory.fromResource(R.drawable.hotel);
//                                    LatLng marker = new LatLng(Double.parseDouble(hotel.getString("lat")), Double.parseDouble(hotel.getString("lon")));
//                                    hotel.put("type", "HOTEL");
//                                }
//
//                                JSONArray restaurants = ((JSONArray) response.get("restaurants"));
//                                for (int i = 0; i < restaurants.length(); i++) {
//                                    JSONObject restaurant = (JSONObject) restaurants.get(i);
//                                    BitmapDescriptor iconRestaurants = BitmapDescriptorFactory.fromResource(R.drawable.restaurant);
//                                    LatLng marker = new LatLng(Double.parseDouble(restaurant.getString("lat")), Double.parseDouble(restaurant.getString("lon")));
//                                    restaurant.put("type", "RESTAURANT");
//                                }
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                            try {
//                                Snackbar.make(view, errorResponse.getString("error"), Snackbar.LENGTH_LONG).setAction("Action", null).show();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
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
