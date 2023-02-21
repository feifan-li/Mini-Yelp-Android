package com.example.yelp;
//The implementation of tabs refers to:https://www.codewithrish.com/creating-whatsapp-like-tabs-with-new-viewpager2-android#heading-now-update-your-mainactivity-with-the-following-code
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class BusinessDetailsFragment extends Fragment {
    private RequestQueue mRequestQueue;
    private JsonObjectRequest jsonObjectRequestDetails;

    String backend_url = "https://yelp-search-nodejs-backend.wl.r.appspot.com/backend/search";

    View view;
    ViewPager2 viewPager2;

    String id,name;
    String link="";
    String display_address="N/A";
    String price_range="N/A";
    String phone_number="N/A";
    boolean is_open_now=false;
    String category="N/A";
//    Double latitude,longitude;
    ArrayList<String> photos;
    //The implementation of image carousel refers to:https://www.youtube.com/watch?v=iJtTN5BHpzw
    ImageCarouselAdapter mAdapter;
    //
    private int mYear,mMonth,mDay,mHour,mMinute;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_business_details, container, false);

        DetailsActivity mom = (DetailsActivity) getActivity();
        Bundle r = mom.sendId();
        id = r.getString("id");
        name = r.getString("name");
        getDetails(id);

        TextView linkTextView = view.findViewById(R.id.LinkTextView);
        linkTextView.setText(Html.fromHtml("<u><font color=#05fafa>Business Link</font></u>",Html.FROM_HTML_MODE_LEGACY));
        linkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(android.content.Intent.ACTION_VIEW);
                shareIntent.setData(Uri.parse(link));
                startActivity(shareIntent);
            }
        });

        Button buttonReserve = view.findViewById(R.id.buttonReserve);
        buttonReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialogv2);
                TextView nameDialog = dialog.findViewById(R.id.NameDialog);
                nameDialog.setText(name);


                EditText email = dialog.findViewById(R.id.editTextEmailAddressDialog);
                EditText date = dialog.findViewById(R.id.editTextDateDialog);
                EditText time = dialog.findViewById(R.id.editTextTimeDialog);
                TextView cancel = dialog.findViewById(R.id.CancelTextViewDialog);
                TextView submit = dialog.findViewById(R.id.SubmitTextViewDialog);

                dialog.show();

                //The set up of the datepicker refers to:
                //https://stackoverflow.com/questions/14933330/datepicker-how-to-popup-datepicker-when-click-on-edittext
                //https://www.geeksforgeeks.org/calendar-settimezone-method-in-java-with-examples/
                //https://www.geeksforgeeks.org/how-to-disable-previous-or-future-dates-in-datepicker-in-android/
                date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar c = Calendar.getInstance();
//                        c.setTimeZone(TimeZone.getTimeZone("PST"));
                        mYear = c.get(Calendar.YEAR);
                        mMonth = c.get(Calendar.MONTH);
                        mDay = c.get(Calendar.DAY_OF_MONTH);


                        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {

                                        date.setText( (monthOfYear + 1)  + "-" +dayOfMonth+ "-" + year);

                                    }
                                }, mYear, mMonth, mDay);
                        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                        datePickerDialog.show();
                    }
                });
                time.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar c = Calendar.getInstance();
//                        c.setTimeZone(TimeZone.getTimeZone("PST"));
                        mHour = c.get(Calendar.HOUR_OF_DAY);
                        mMinute = c.get(Calendar.MINUTE);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                                new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {

                                        time.setText(hourOfDay + ":" + minute);
                                    }
                                }, mHour, mMinute, false);
                        timePickerDialog.show();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });


                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int hh =Integer.parseInt( time.getText().toString().split(":")[0]);
                        String mm =time.getText().toString().split(":")[1];
                        if(!patternMatches(email.getText().toString())){
                            Toast.makeText(getActivity(),"InValid Email Address.",Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }else if(hh<10 || hh>17 || (hh==17 && !mm.equals("00"))){
                            Toast.makeText(getActivity(),"Time should be between 10AM AND 5PM", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }else{
//                            TODO: WRITE INTO SHARED PREFERENCE
                            SharedPreferences pref = mom.getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                            SharedPreferences.Editor editor = pref.edit();
                            Set<String> names;
                            if(pref.contains("business_name")){
                                names = pref.getStringSet("business_name",new HashSet<String>());
                                names.add(name);
                                editor.remove("business_name");
                                editor.putStringSet("business_name",names);
                                if(pref.contains(name)){
                                    editor.remove(name);
                                }
                                editor.putString(name,email.getText().toString()+"&"+date.getText().toString()+"&"+time.getText().toString());
                                editor.commit();
                            }else{
                                names = new HashSet<>();
                                names.add(name);
                                editor.putStringSet("business_name",names);
                                if(pref.contains(name)){
                                    editor.remove(name);
                                }
                                editor.putString(name,email.getText().toString()+"&"+date.getText().toString()+"&"+time.getText().toString());
                                editor.commit();
                            }

                            Toast.makeText(getActivity(),"Reservation Booked", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        }
                    }
                });


            }
        });
        // Inflate the layout for this fragment
        return view;
    }
    public void getDetails(String id){
        mRequestQueue = Volley.newRequestQueue(getActivity());
        String request_url = backend_url+"/"+id;
        jsonObjectRequestDetails = new JsonObjectRequest
                (Request.Method.GET, request_url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if(response.has("price")){
                                price_range = response.getString("price");
                            }else{
                                price_range = "N/A";
                            }
                            TextView priceTextView = view.findViewById(R.id.PriceTextView);
                            priceTextView.setText(price_range);

                            JSONArray displayAddressArray = response.getJSONObject("location").getJSONArray("display_address");
                            StringBuilder displayAddressSb=new StringBuilder();
                            for(int i=0;i<displayAddressArray.length();++i){
                                displayAddressSb.append(displayAddressArray.get(i));
                                if(i!=displayAddressArray.length()-1) displayAddressSb.append(" ");
                            }
                            display_address = displayAddressSb.toString();
                            TextView addressTextView = view.findViewById(R.id.AddressTextView);
                            addressTextView.setText(display_address);
                            if(response.has("url")){
                                link = response.getString("url");
                            }
                            if(response.has("display_phone")){
                                phone_number = response.getString("display_phone");
                            }
                            if(phone_number.length()<=1){
                                phone_number = "N/A";
                            }

                            TextView phoneTextView = view.findViewById(R.id.PhoneTextView);
                            phoneTextView.setText(phone_number);

                            JSONObject hoursObj = (JSONObject) response.getJSONArray("hours").get(0);
                            is_open_now = hoursObj.getBoolean("is_open_now");
                            TextView statusTextView = view.findViewById(R.id.StatusTextView);
                            if(is_open_now==true){
                                statusTextView.setText(Html.fromHtml("<font color=#2bdf32>Open Now</font>",Html.FROM_HTML_MODE_LEGACY));
                            }else{
                                statusTextView.setText(Html.fromHtml("<font color=#fc2828>Closed</font>",Html.FROM_HTML_MODE_LEGACY));
                            }
                            JSONArray categoriesArray = response.getJSONArray("categories");
                            StringBuilder categoriesSb = new StringBuilder();
                            for(int i=0;i<categoriesArray.length();++i){
                                JSONObject obj = (JSONObject) categoriesArray.get(i);
                                categoriesSb.append(obj.getString("title"));
                                if(i!=categoriesArray.length()-1) categoriesSb.append(" | ");
                            }
                            category = categoriesSb.toString();

                            TextView categoryTextView = view.findViewById(R.id.CategoryTextView);
                            categoryTextView.setText(category);

//                            latitude = response.getJSONObject("coordinates").getDouble("latitude");
//                            longitude = response.getJSONObject("coordinates").getDouble("longitude");
                            JSONArray photosJSONArray = response.getJSONArray("photos");
                            photos = new ArrayList<>();
                            for(int i=0;i<photosJSONArray.length();++i){
                                photos.add((String) photosJSONArray.get(i));
                            }
                            viewPager2 = view.findViewById(R.id.imageCarousel);
                            mAdapter = new ImageCarouselAdapter(photos,getActivity());
                            viewPager2.setAdapter(mAdapter);

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
    // THIS IMPLEMENTATION OF STRICTLY VALIDATING EMAIL ADDRESS REFERS TO:
    // https://www.baeldung.com/java-email-validation-regex
    public boolean patternMatches(String emailAddress) {
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }
}