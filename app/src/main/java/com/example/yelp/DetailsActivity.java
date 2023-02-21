package com.example.yelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {
    private RequestQueue mRequestQueue;
    private JsonObjectRequest jsonObjectRequestDetails;
    private JsonObjectRequest jsonObjectRequestReviews;
    ActionBar actionBarDetailsLayout;
    String backend_url = "https://yelp-search-nodejs-backend.wl.r.appspot.com/backend/search";
    LinearLayout detailsLayout;
    TabLayout detailsTabLayout;
    ViewPager2 viewPager;
    ViewPagerFragmentAdapter adapter;
    private String[] labels = new String[]{"BUSINESS DETAILS", "MAP LOCATION", "REVIEWS"};

    String id,name;
    String url="";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        detailsLayout = findViewById(R.id.detailsLayout);

        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        actionBarDetailsLayout = getSupportActionBar();
        actionBarDetailsLayout.setTitle(name);
        actionBarDetailsLayout.setDisplayHomeAsUpEnabled(true);

        getDetails(id);

        //The implementation of tabs refers to:https://www.codewithrish.com/creating-whatsapp-like-tabs-with-new-viewpager2-android#heading-now-update-your-mainactivity-with-the-following-code
        // call function to initialize views
        init();

        // bind and set tabLayout to viewPager and set labels for every tab
        new TabLayoutMediator(detailsTabLayout, viewPager, true,true,(tab, position) -> {
            tab.setText(labels[position]);
        }).attach();

        //To avoid conflicting with image carousel, disable swiping
        //Reference: https://stackoverflow.com/questions/54978846/how-to-disable-swiping-in-viewpager2
        viewPager.setUserInputEnabled(false);
        // set default position to 1 instead of default 0
        viewPager.setCurrentItem(0, false);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.facebook, menu);
        getMenuInflater().inflate(R.menu.twitter, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }else if (id == R.id.action_facebook) {
            String facebookShareLink = "https://www.facebook.com/sharer/sharer.php?u="+url;
            Intent shareIntent = new Intent(android.content.Intent.ACTION_VIEW);
            shareIntent.setData(Uri.parse(facebookShareLink));
            startActivity(shareIntent);
            return true;
        }else if (id == R.id.action_twitter){
            String tweetShareLink = "https://twitter.com/intent/tweet?text=Check Out "+name+" on Yelp."+"&url="+url;
            Intent shareIntent = new Intent(android.content.Intent.ACTION_VIEW);
            shareIntent.setData(Uri.parse(tweetShareLink));
            startActivity(shareIntent);
            return true;
        }
        return true;

    }
    // create adapter to attach fragments to viewpager2 using FragmentStateAdapter
    private void init() {
        detailsTabLayout = findViewById(R.id.detailsTabLayout);
        viewPager = findViewById(R.id.viewPager);
        // create adapter instance
        adapter = new ViewPagerFragmentAdapter(this);
        // set adapter to viewPager
        viewPager.setAdapter(adapter);
        // remove default elevation of actionbar
//        getSupportActionBar().setElevation(0);
    }
    private class ViewPagerFragmentAdapter extends FragmentStateAdapter {

        public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        // return fragments at every position
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    BusinessDetailsFragment businessDetailsFragment = new BusinessDetailsFragment();
                    return businessDetailsFragment;
                case 1:
                    return new MapsFragment();
                case 2:
                    return new ReviewsFragment();
            }
            return new BusinessDetailsFragment();
        }

        @Override
        public int getItemCount() {
            return labels.length;
        }
    }
    public void getDetails(String id){
        mRequestQueue = Volley.newRequestQueue(this);
        String request_url = backend_url+"/"+id;
        jsonObjectRequestDetails = new JsonObjectRequest
                (Request.Method.GET, request_url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if(response.has("url")){
                                url = response.getString("url");
                            }
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
    public void getReviews(String id){
        mRequestQueue = Volley.newRequestQueue(this);
        String url = backend_url+"/"+id+"/reviews";

        mRequestQueue.add(jsonObjectRequestReviews);
    }
    public Bundle sendId(){
        Bundle b = new Bundle();
        b.putString("id",id);
        b.putString("name",name);
        return b;
    }
}