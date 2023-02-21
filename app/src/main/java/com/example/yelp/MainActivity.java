package com.example.yelp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private JsonArrayRequest jsonArrayRequestKeywords;
    private JsonObjectRequest jsonObjectRequestCoordinate;
    private JsonObjectRequest jsonObjectRequestResults;
    private JsonObjectRequest jsonObjectRequestDetails;
    String backend_url = "https://yelp-search-nodejs-backend.wl.r.appspot.com/backend/search";
    String ipinfo_url = "https://ipinfo.io/json?token=1f32afdd229fc2";
    AutoCompleteTextView autoCompleteTextViewKeyword;
    EditText editTextDistance;
    TextView textCategory;
    Spinner spinnerCategory;
    ArrayAdapter adapterCategory;
    ArrayAdapter adapterAutocomplete;
    EditText editTextLocation;
    CheckBox checkboxLocation;
    Button buttonSubmit;
    Button buttonClear;
    //search params:
    String keyword="";
    String distance="";
    String category="";
    String location="";
    String lat="",lng="";
    //Search Results:
    ArrayList<JSONObject> searchResults = new ArrayList<>();
    TextView noResultsTextView;
    RecyclerView resultsRecyclerView;
    ResultsRecyclerAdapter resultsRecyclerAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_reservation) {
            //process your onClick here
            startActivity(new Intent(MainActivity.this,DisplayReservationActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        noResultsTextView = findViewById(R.id.noResultsTextView);

        autoCompleteTextViewKeyword = findViewById(R.id.autoCompleteTextViewKeyword);
        String keywordInputHint = "<font color=#8f9193>Keyword</font> <font color=#fc2828>*</font>";
        autoCompleteTextViewKeyword.setHint(Html.fromHtml(keywordInputHint, Html.FROM_HTML_MODE_LEGACY));

//        if(autoCompleteTextViewKeyword.getText().toString()!=""){
//            getAutoCompleteText(autoCompleteTextViewKeyword.getText().toString());
//        }
//        autocompleteResults= new String[]{    "Donuts",
//                "Donation Center",
//                "Organ & Tissue Donor Services",
//                "Donut Shop",
//                "Donuts Near Me",
//                "Mac Donalds"};
        autoCompleteTextViewKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                getAutoCompleteText(autoCompleteTextViewKeyword.getText().toString());
            }
        });

        editTextDistance = findViewById(R.id.editTextDistance);
        String distanceInputHint = "<font color=#8f9193>Distance</font>";
        editTextDistance.setHint(Html.fromHtml(distanceInputHint,Html.FROM_HTML_MODE_LEGACY));

        textCategory = findViewById(R.id.textCategory);
        textCategory.setText(Html.fromHtml("<font color=#fc28f1>Category</font> <font color=#fc2828>*</font>",Html.FROM_HTML_MODE_LEGACY));
        /****Set spinner and set up an adapter*****/
        spinnerCategory = findViewById(R.id.spinnerCategory);
        adapterCategory = ArrayAdapter.createFromResource(this,R.array.categories, android.R.layout.simple_spinner_item);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapterCategory);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = adapterView.getItemAtPosition(i).toString();
                if(category.equals("Default")){
                    category = "default";
                }else if (category.equals("Arts & Entertainment")){
                    category = "arts and entertainment";
                }else if(category.equals("Health & Medical")){
                    category = "health and medical";
                }else if(category.equals("Hotels & Travel")){
                    category = "hotels and travel";
                }else if(category.equals("Food")){
                    category = "food";
                }else if(category.equals("Professional Services")){
                    category = "professional services";
                }else{
                    category = "default";
                }
//                Toast.makeText(getApplicationContext(),category,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        /******************************************/

        editTextLocation = findViewById(R.id.editTextLocation);
        String locationInputHint = "<font color=#8f9193>Location</font> <font color=#fc2828>*</font>";
        editTextLocation.setHint(Html.fromHtml(locationInputHint,Html.FROM_HTML_MODE_LEGACY));

        checkboxLocation = findViewById(R.id.checkBoxLocation);
        checkboxLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(checkboxLocation.isChecked()){
                    editTextLocation.setText("");
                    location="";
                    editTextLocation.setVisibility(View.INVISIBLE);
                    getCoordinate();
                }else{
                    editTextLocation.setVisibility(View.VISIBLE);
                }
            }
        });

        buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonClear = findViewById(R.id.buttonClear);

        noResultsTextView.setVisibility(View.GONE);
        noResultsTextView.setVisibility(View.INVISIBLE);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  noResultsTextView.setVisibility(View.GONE);
                  noResultsTextView.setVisibility(View.INVISIBLE);
                  searchResults = new ArrayList<>();

                  keyword = autoCompleteTextViewKeyword.getText().toString();
                  distance = editTextDistance.getText().toString();
                  location = editTextLocation.getText().toString();

//                TODO:Form Validation
                  boolean isValid = formValidation();
                  if(isValid){
                      getSearchResult(keyword,distance,category,location,lat,lng);
                  }

            }
        });
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchResults = new ArrayList<>();
                noResultsTextView.setVisibility(View.GONE);
                noResultsTextView.setVisibility(View.INVISIBLE);
                autoCompleteTextViewKeyword.setText("");
                editTextDistance.setText("");
                spinnerCategory.setAdapter(adapterCategory);
                editTextLocation.setVisibility(View.VISIBLE);
                editTextLocation.setText("");
                checkboxLocation.setChecked(false);

                resultsRecyclerView = findViewById(R.id.ResultsRecyclerView);
                resultsRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false));

                resultsRecyclerAdapter = new ResultsRecyclerAdapter(searchResults,MainActivity.this);
                resultsRecyclerView.setAdapter(resultsRecyclerAdapter);
            }
        });
    }
    private boolean formValidation(){
        if(autoCompleteTextViewKeyword.length()==0){
            autoCompleteTextViewKeyword.setError("This field is required");
            return false;
        }
        if(editTextLocation.length()==0 &&!checkboxLocation.isChecked()){
            editTextLocation.setError("This field is required");
            return false;
        }
        return true;
    }
    private void getAutoCompleteText(String input){
        //Reference:https://www.tabnine.com/code/java/methods/org.json.JSONArray/getString
        //Reference:https://abhiandroid.com/programming/volley
        //Reference:https://stackoverflow.com/questions/1568762/accessing-members-of-items-in-a-jsonarray-with-java
        mRequestQueue = Volley.newRequestQueue(this);
        String url = backend_url+"/autocomplete?text="+input;

        jsonArrayRequestKeywords = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<String> list = new ArrayList<>();
                        try {
                            for(int i=0;i<response.length();++i){
                                list.add(response.getString(i));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String[] autocompleteResults = new String[list.size()];
                        for(int i=0;i<list.size();++i){
                            autocompleteResults[i] = list.get(i);
                        }
                        adapterAutocomplete = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,autocompleteResults);
                        autoCompleteTextViewKeyword.setAdapter(adapterAutocomplete);
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                    }
                });

        mRequestQueue.add(jsonArrayRequestKeywords);
    }
    private void getSearchResult(String keyword,String distance,String category,String location,String lat,String lng){
        mRequestQueue = Volley.newRequestQueue(this);
        searchResults = new ArrayList<>();
        resultsRecyclerView = findViewById(R.id.ResultsRecyclerView);
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false));

        resultsRecyclerAdapter = new ResultsRecyclerAdapter(searchResults,MainActivity.this);
        resultsRecyclerView.setAdapter(resultsRecyclerAdapter);
        String url = backend_url+"?key="+keyword+"&lat="+lat+"&lng="+lng+"&address="+location+"&distance="+distance+"&category="+category;
//        String url = backend_url+"?key=Concerts&lat=&lng=&address=University%20of%20Southern%20California&distance=2&category=food";
//        String bad_url = backend_url+"?key=gbfdsbhgtdz&lat=&lng=&address=bgdfbgfdbfdzd&distance=2&category=food";
        jsonObjectRequestResults = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if(response.length()==0){

                            noResultsTextView.setVisibility(View.VISIBLE);
                            return;
                        }
                        String name = "";
                        try {
                            for(int i=0;i<response.names().length();++i){
                                String key = response.names().get(i).toString();
                                JSONObject obj = response.getJSONObject(key);
                                searchResults.add(obj);
                            }
                            name = searchResults.get(2).getString("name");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        Toast.makeText(getApplicationContext(),"Length: "+searchResults.size()+" First: "+name,Toast.LENGTH_LONG).show();
                        //                TODO:RESULT NULL

                        if(searchResults.size()>=1){
                            noResultsTextView.setVisibility(View.GONE);
                            noResultsTextView.setVisibility(View.INVISIBLE);

                            resultsRecyclerAdapter = new ResultsRecyclerAdapter(searchResults,MainActivity.this);
                            resultsRecyclerView.setAdapter(resultsRecyclerAdapter);

                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });
        mRequestQueue.add(jsonObjectRequestResults);
    }
    private void getCoordinate(){
        mRequestQueue = Volley.newRequestQueue(this);
        jsonObjectRequestCoordinate = new JsonObjectRequest
                (Request.Method.GET, ipinfo_url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String loc = response.getString("loc");
                            lat = loc.split(",")[0];
                            lng = loc.split(",")[1];
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        Toast.makeText(getApplicationContext(),"Lat: "+lat+" Lng: "+lng,Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });
        mRequestQueue.add(jsonObjectRequestCoordinate);
    }
}












