package com.example.yelp;
//The implementation of tabs refers to:https://www.codewithrish.com/creating-whatsapp-like-tabs-with-new-viewpager2-android#heading-now-update-your-mainactivity-with-the-following-code
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReviewsFragment extends Fragment {
    View view;
    RecyclerView reviewsRecyclerView;
    private RequestQueue mRequestQueue;
    private JsonArrayRequest jsonArrayRequestReviews;

    String backend_url = "https://yelp-search-nodejs-backend.wl.r.appspot.com/backend/search";

    String id;
    ArrayList<String> names;
    ArrayList<Integer> ratings;
    ArrayList<String> comments;
    ArrayList<String> dates;
    ReviewsRecyclerAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_reviews, container, false);

        DetailsActivity master = (DetailsActivity) getActivity();
        id = master.sendId().getString("id");

        reviewsRecyclerView = view.findViewById(R.id.ReviewsRecyclerView);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));

        showReviewsOf(id);

        return view;
    }
    public void showReviewsOf(String id){
        mRequestQueue = Volley.newRequestQueue(getActivity());
        String request_url = backend_url+"/"+id+"/reviews";
        jsonArrayRequestReviews = new JsonArrayRequest
                (Request.Method.GET, request_url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            names = new ArrayList<String>();
                            ratings = new ArrayList<Integer>();
                            comments = new ArrayList<String>();
                            dates = new ArrayList<String>();
                            for(int i=0;i<response.length();++i){
                                String name=response.getJSONObject(i).getJSONObject("user").getString("name");
                                names.add(name);
                                ratings.add(response.getJSONObject(i).getInt("rating"));
                                comments.add(response.getJSONObject(i).getString("text"));
                                dates.add(response.getJSONObject(i).getString("time_created").split(" ")[0]);
                            }

                            adapter = new ReviewsRecyclerAdapter(names,ratings,comments,dates,getActivity());
                            reviewsRecyclerView.setAdapter(adapter);
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
        mRequestQueue.add(jsonArrayRequestReviews);
    }
}