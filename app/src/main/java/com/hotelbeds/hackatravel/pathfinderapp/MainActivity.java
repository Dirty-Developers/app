package com.hotelbeds.hackatravel.pathfinderapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hotelbeds.hackatravel.pathfinderapp.api.PathFinderClient;
import com.hotelbeds.hackatravel.pathfinderapp.api.PathFinderUsage;
import com.hotelbeds.hackatravel.pathfinderapp.common.Constants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.MissingFormatArgumentException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap = null;
    private PathFinderUsage clientUsage = new PathFinderUsage();
    private JSONArray activities;
    private JSONArray hotels;
    private JSONArray restaurants;
    private ArrayList<Marker> markerList = new ArrayList<>();
    private TextView txtLatitud;
    private TextView txtLongitud;
    private TextView txtLatitudDes;
    private TextView txtLongitudDes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUpTabHost();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.modal_data);
                dialog.setTitle("Select Origin and Destination:");
                Button dialogButton = (Button) dialog.findViewById(R.id.btnGo);

                LinearLayout lyCoordenades = dialog.findViewById(R.id.lyCoordenades);
                LinearLayout lyDestination = dialog.findViewById(R.id.lyDestination);
                if (markerList.size() == 2) {
                    lyCoordenades.setVisibility(View.VISIBLE);
                    lyDestination.setVisibility(View.GONE);

                    txtLatitud = dialog.findViewById(R.id.txtLatitudOrg);
                    txtLatitud.setText(String.valueOf(markerList.get(0).getPosition().latitude));
                    txtLongitud = dialog.findViewById(R.id.txtLongitudOrg);
                    txtLongitud.setText(String.valueOf(markerList.get(0).getPosition().longitude));

                    txtLatitudDes = dialog.findViewById(R.id.txtLatitudDes);
                    txtLatitudDes.setText(String.valueOf(markerList.get(1).getPosition().latitude));
                    txtLongitudDes = dialog.findViewById(R.id.txtLongitudDes);
                    txtLongitudDes.setText(String.valueOf(markerList.get(1).getPosition().longitude));
                } else {
                    lyDestination.setVisibility(View.VISIBLE);
                    lyCoordenades.setVisibility(View.GONE);
                }
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            JSONObject rq = new JSONObject();

                            JSONObject coordenadasOri = new JSONObject();
                            coordenadasOri.put("lon", Double.parseDouble(txtLongitud.getText().toString()));
                            coordenadasOri.put("lat", Double.parseDouble(txtLatitud.getText().toString()));

                            rq.put("origin", coordenadasOri);

                            JSONObject coordenadasDes = new JSONObject();
                            coordenadasDes.put("lon", Double.parseDouble(txtLongitudDes.getText().toString()));
                            coordenadasDes.put("lat", Double.parseDouble(txtLatitudDes.getText().toString()));
                            JSONObject destiantion = new JSONObject();
                            rq.put("destination", coordenadasDes);
                            rq.put("checkin", "");
                            rq.put("checkout", "");

                            StringEntity entity = new StringEntity(rq.toString());
                            PathFinderClient.post("activities", entity, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    try {
                                        activities = ((JSONArray) response.get("activities"));
                                        for (int i = 0; i < activities.length(); i++) {
                                            JSONObject activity = (JSONObject) activities.get(i);
                                            BitmapDescriptor iconActivity = BitmapDescriptorFactory.fromResource(R.drawable.compass);
                                            LatLng latLng = new LatLng(Double.parseDouble(activity.getString("lat")), Double.parseDouble(activity.getString("lon")));
                                            activity.put("type", "ACTIVYTY");
                                            mMap.addMarker(new MarkerOptions().position(latLng).title(activity.toString()).icon(iconActivity));
                                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                        }
                                        mMap.animateCamera(CameraUpdateFactory.zoomTo(10.0f));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                                }
                            });

                            PathFinderClient.post("ancillaries", entity, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    try {
                                        hotels = ((JSONArray) response.get("hotels"));
                                        for (int i = 0; i < hotels.length(); i++) {
                                            JSONObject hotel = (JSONObject) hotels.get(i);
                                            BitmapDescriptor iconHotel = BitmapDescriptorFactory.fromResource(R.drawable.hotel);
                                            LatLng marker = new LatLng(Double.parseDouble(hotel.getString("lat")), Double.parseDouble(hotel.getString("lon")));
                                            hotel.put("type", "HOTEL");
                                            mMap.addMarker(new MarkerOptions().position(marker).title(hotel.toString()).icon(iconHotel));
                                            mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
                                        }

                                        restaurants = ((JSONArray) response.get("restaurants"));
                                        for (int i = 0; i < restaurants.length(); i++) {
                                            JSONObject restaurant = (JSONObject) restaurants.get(i);
                                            BitmapDescriptor iconRestaurants = BitmapDescriptorFactory.fromResource(R.drawable.restaurant);
                                            LatLng marker = new LatLng(Double.parseDouble(restaurant.getString("lat")), Double.parseDouble(restaurant.getString("lon")));
                                            restaurant.put("type", "RESTAURANT");
                                            mMap.addMarker(new MarkerOptions().position(marker).title(restaurant.toString()).icon(iconRestaurants));
                                            mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
                                        }
                                        mMap.animateCamera(CameraUpdateFactory.zoomTo(10.0f));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                                }
                            });

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.g_map);
        mapFragment.getMapAsync(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void setUpTabHost() {

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (markerList.size() >= 2) {
                    mMap.clear();
                    markerList.clear();
                } else if (markerList.size() == 0) {
                    Marker myMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("Origin")
                            .snippet("This is my spot!")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    markerList.add(myMarker);
                } else {
                    Marker myMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("Destination")
                            .snippet("This is my spot!")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    markerList.add(myMarker);

                    mMap.addPolyline(new PolylineOptions()
                            .add(new LatLng(markerList.get(0).getPosition().latitude, markerList.get(0).getPosition().longitude),
                                    new LatLng(markerList.get(1).getPosition().latitude, markerList.get(1).getPosition().longitude))
                            .width(5).color(Color.RED).geodesic(true));

                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker m) {
//                Toast.makeText(MainActivity.this, "YOU CLICKED ON " + m.getTitle(), Toast.LENGTH_LONG).show();
                try {
                    final JSONObject detail = new JSONObject(m.getTitle());
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.modal_detail);
                    dialog.setTitle(detail.getString("name"));
                    Button detailButton = (Button) dialog.findViewById(R.id.btnDetail);
                    final ImageView detailImg = (ImageView) dialog.findViewById(R.id.imgDetail);
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.get(detail.getString("photo"), new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                            ProgressBar loading = (ProgressBar) dialog.findViewById(R.id.lgDetail);
                            loading.setVisibility(View.GONE);
                            Bitmap bmp = BitmapFactory.decodeStream(new ByteArrayInputStream(response));
                            detailImg.setImageBitmap(bmp);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                            ProgressBar loading = (ProgressBar) dialog.findViewById(R.id.lgDetail);
                            loading.setVisibility(View.GONE);
                            String uri = "@drawable/imagenotfound";  // where myresource (without the extension) is the file
                            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                            Drawable res = getResources().getDrawable(imageResource);
                            detailImg.setImageDrawable(res);
                        }

                    });
                    // if button is clicked, close the custom dialog
                    detailButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                            Bundle b = new Bundle();
                            b.putString("detailData", detail.toString()); //Your id
                            intent.putExtras(b); //Put your id to your next Intent
                            startActivity(intent);
                        }
                    });

                    Button cancelButton = (Button) dialog.findViewById(R.id.btnCancelDetatil);
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
    }

}