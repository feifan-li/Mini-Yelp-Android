package com.example.yelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DisplayReservationActivity extends AppCompatActivity {
    LinearLayout displayReservationLayout;
    TextView emptyNotice;
//    TextView helper;
    RecyclerView recyclerView;
    RecyclerViewAdapter mAdapter;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ArrayList<String> nameList;
    ArrayList<String> reservationList;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_reservation);

        displayReservationLayout = findViewById(R.id.displayReservationLayout);
        emptyNotice = findViewById(R.id.EmptyNotice);
//        helper = findViewById(R.id.helper);
        recyclerView = findViewById(R.id.ReservationRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        if(!pref.contains("business_name")||pref.getStringSet("business_name",new HashSet<String>()).size()==0){
            emptyNotice.setVisibility(View.VISIBLE);
        }else{
            emptyNotice.setVisibility(View.INVISIBLE);
            emptyNotice.setVisibility(View.GONE);
            reservationList = new ArrayList<>();
            nameList = new ArrayList<>();
            Set<String> names = pref.getStringSet("business_name",new HashSet<>());
            for(String name:names){
                nameList.add(name);
                reservationList.add(pref.getString(name,""));
            }
//            TODO: Set Adapter
            mAdapter = new RecyclerViewAdapter(nameList,reservationList);
            recyclerView.setAdapter(mAdapter);
            enableSwipeToDelete();
            editor.commit();
        }


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }
    //Reference:https://www.journaldev.com/23164/android-recyclerview-swipe-to-delete-undo#code
    private void enableSwipeToDelete() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();

                String removed_name = mAdapter.removeItem(position);
                //        TODO: REMOVE FROM SHARED PREFERENCE
//                if(pref.contains(removed_name)){
                editor.remove(removed_name);
//                }
                Set<String> nameSet = pref.getStringSet("business_name", new HashSet<String>());
//                if(nameSet.contains(removed_name)){
                    nameSet.remove(removed_name);
//                }
                editor.remove("business_name");
                if(nameSet.size()>=1){
                    editor.putStringSet("business_name",nameSet);
                }else {
                    emptyNotice.setVisibility(View.VISIBLE);
                    editor.clear();
                }
                editor.commit();


                Snackbar snackbar = Snackbar
                        .make(displayReservationLayout, "Removing Existing Reservation", Snackbar.LENGTH_SHORT);

                snackbar.show();
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }
}
