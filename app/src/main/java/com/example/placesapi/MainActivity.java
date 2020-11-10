package com.example.placesapi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    double lat, lng;
    ImageView loc, search;
    String selection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SmartLocation.with(getApplicationContext()).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {

                    @Override
                    public void onLocationUpdated(Location location) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        lat = location.getLatitude();
                        lng = location.getLongitude();
                        Log.d("loca", "location :" + location.getLatitude() + ", " + location.getLongitude() + " " + latLng.longitude + latLng.latitude);
                        mMap.addMarker(new MarkerOptions().position(latLng).title("you are here"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));

                        Geocoder geocoder = new Geocoder(MainActivity.this);
                        try {
                            ArrayList<Address> addr = (ArrayList<Address>) geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                            Log.d("addr", String.valueOf(addr.get(0).getLocality()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Spinner spinner = findViewById(R.id.spinner);
        loc = findViewById(R.id.loc);
        search = findViewById(R.id.search);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);
        mapFragment.getMapAsync(this);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selection = String.valueOf(parent.getItemAtPosition(position));


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                LatLng location = new LatLng(lat, lng);
                hiturl(selection, location);
                Log.d("message", "control here");
                Log.d("message", lat + String.valueOf(lng));
            }
        });

        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmartLocation.with(getApplicationContext()).location()
                        .oneFix()
                        .start(new OnLocationUpdatedListener() {

                            @Override
                            public void onLocationUpdated(Location location) {
                                Log.d("message", "control here");
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                lat = location.getLatitude();
                                lng = location.getLongitude();
                                Log.d("message", lat + String.valueOf(lng));
                                mMap.clear();
                                mMap.addMarker(new MarkerOptions().position(latLng).title("you are here"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));


                            }
                        });

            }
        });

    }



    public void hiturl(String type, LatLng location) {
        AndroidNetworking.initialize(this);





        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + location.latitude + "," + location.longitude + "&radius=1500&type=" + type + "&keyword=&key=AIzaSyAFQT_29FubAg3ZiEjewYlw-tUDUXm44HM";
        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            JSONArray arrResult = response.getJSONArray("results");

                            for (int i = 0; i < arrResult.length(); i++) {
                                JSONObject objResult = arrResult.getJSONObject(i);

                                String name = objResult.getString("name");
                                double lat = arrResult.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                                double lng = arrResult.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng");


                                LatLng latLng = new LatLng(lat, lng);
                                Log.d("loca", "" + lat + ", " + lng + " : " + name);
                                mMap.addMarker(new MarkerOptions().position(latLng).title(name));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {

                    }


                });


    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }


public void locationget() {

}

}