package com.example.a18arid2982qno4;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(30, 69);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        getAPIMarkers();
    }

    public void getAPIMarkers() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.trackcorona.live/api/countries";
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject res = new JSONObject(response);
                        JSONArray data = res.getJSONArray("data");
                        Log.d("DATA", data.toString());
                        JSONObject obj;
                        LatLng myPos;
                        for (int i = 0; i < data.length(); i++) {
                            obj = new JSONObject(data.get(i).toString());
                            myPos = new LatLng(obj.getDouble("latitude"),obj.getDouble("longitude"));

                            mMap.addMarker(new MarkerOptions().position(myPos).title(obj.getString("location") + ": " + obj.getString("confirmed") + " Confirmed").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        }
                    } catch (JSONException e) {
                        Log.d("ERROR", "error => " + e.toString());
                    }
                },
                error -> Log.d("ERROR", "error => " + error.toString())
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-type", "application/json");
                return params;
            }
        };
        queue.add(getRequest);
    }
}