package com.example.yelp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsFragment extends Fragment {
    private RequestQueue mRequestQueue;
    String backend_url = "https://yelp-search-nodejs-backend.wl.r.appspot.com/backend/search";
    private JsonObjectRequest jsonObjectRequestDetails;
    Double latitude,longitude;
    String id;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            DetailsActivity master = (DetailsActivity) getActivity();
            Bundle r = master.sendId();
            id = r.getString("id");
            getDetails(googleMap,id);
//            LatLng sydney = new LatLng(-34, 151);
//            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
    public void getDetails(GoogleMap googleMap,String id){
        mRequestQueue = Volley.newRequestQueue(getActivity());
        String request_url = backend_url+"/"+id;
        jsonObjectRequestDetails = new JsonObjectRequest
                (Request.Method.GET, request_url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            latitude = response.getJSONObject("coordinates").getDouble("latitude");
                            longitude = response.getJSONObject("coordinates").getDouble("longitude");
                            LatLng marker = new LatLng(latitude,longitude);
                            googleMap.addMarker(new MarkerOptions().position(marker).title("Location"));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker,15));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });
        mRequestQueue.add(jsonObjectRequestDetails);
    }
}