package com.tekdevisal.chelper;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joooonho.SelectableRoundedImageView;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;
import com.tekdevisal.chelper.Helpers.Accessories;
import com.tekdevisal.chelper.LocationUtil.LocationHelper;
import com.tekdevisal.chelper.Services.Incoming_calls_service;

import org.infobip.mobile.messaging.geo.Geo;
import org.infobip.mobile.messaging.geo.GeoEvent;
import org.infobip.mobile.messaging.geo.GeoMessage;
import org.infobip.mobile.messaging.geo.MobileGeo;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
//        implements GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback,
//        LocationListener {

    private FirebaseAuth mauth;
    private String caller_id="", doc_name, caller_name, has_open_video="", has_opened="";
    public  String has_open_dialer="";
    private Snackbar snackbar;
    private Accessories main_accessor;
    private String usertype;
    private ImageView covid_info, complain_, chat_, statistic, about_app, doc_reports;
    private TextView top_text;
    private SelectableRoundedImageView image;
    private LinearLayout is_available_layout;
    private SwitchCompat is_available_switch;
    private LocationHelper locationHelper;
    private Location myLocation;
    double latitudeD,longitudeD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);
        }catch (OutOfMemoryError e){
            e.printStackTrace();
        }

        mauth = FirebaseAuth.getInstance();
        getSupportActionBar().setTitle("Covaid | HOME");

//        locationHelper = new LocationHelper(MainActivity.this);
//        locationHelper.checkpermission();
//
//        if(locationHelper.checkPlayServices()){
//            locationHelper.buildGoogleApiClient();
//        }

        main_accessor = new Accessories(MainActivity.this);
        usertype = main_accessor.getString("user_type");

//        latitudeD = Double.parseDouble(main_accessor.getString("saved_latitude"));
//        longitudeD = Double.parseDouble(main_accessor.getString("saved_longitude"));


        top_text = findViewById(R.id.top_text);
        image = findViewById(R.id.image);
        is_available_layout = findViewById(R.id.is_available_layout);
        covid_info = findViewById(R.id.info);
        complain_ = findViewById(R.id.complain);
        chat_ = findViewById(R.id.chat);
        statistic = findViewById(R.id.statistic);
        about_app = findViewById(R.id.about_app);

        doc_reports = findViewById(R.id.reports);
        is_available_switch = findViewById(R.id.is_available_switch);

        if(usertype.equals("doctor")){
            top_text.setText("Help us aid in the fight against covid 19");
            image.setImageDrawable(getResources().getDrawable(R.drawable.doctors));
            complain_.setVisibility(View.GONE);
            statistic.setVisibility(View.GONE);
            doc_reports.setVisibility(View.VISIBLE);
            is_available_layout.setVisibility(View.VISIBLE);
        }else{
            top_text.setTextSize(25);
        }
            is_available_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        Doctor_is_Available();
                    }else{
                        Doctor_is_UnAvailable();
                    }
                }
            });

        //swipe selections
        SwipeSelector swipeSelector = (SwipeSelector) findViewById(R.id.swipe_selector);
        swipeSelector.setItems(
                // The first argument is the value for that item, and should in most cases be unique for the
                // current SwipeSelector, just as you would assign values to radio buttons.
                // You can use the value later on to check what the selected item was.
                // The value can be any Object, here we're using ints.
                new SwipeItem(0, "Tip 1", "Clean your hands often. Use soap and water, or an alcohol-based hand rub."),
                new SwipeItem(1, "Tip 2", "Maintain a safe distance from anyone who is coughing or sneezing."),
                new SwipeItem(2, "Tip 3", "Don’t touch your eyes, nose or mouth.")
        );

        covid_info.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, About_.class));
        });

        complain_.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Report_.class));
        });

        chat_.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Live_Chat.class));
        });

        statistic.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Statistics.class));
        });

        about_app.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, App_description.class));
        });

        findViewById(R.id.reports).setOnClickListener(v ->
            startActivity(new Intent(MainActivity.this, Reports.class)
        ));

        findViewById(R.id.logout).setOnClickListener(v -> {
            final AlertDialog.Builder logout = new AlertDialog.Builder(MainActivity.this, R.style.Myalert);
            logout.setTitle("Signing Out?");
            logout.setMessage("Leaving us? Please reconsider.");
            logout.setNegativeButton("Sign out", (dialog, which) -> {
                if(isNetworkAvailable()){
                    FirebaseAuth.getInstance().signOut();
                    main_accessor.put("has_named", "false");
                    Doctor_is_UnAvailable();
                    startActivity(new Intent(MainActivity.this,Login_.class));
                }else{
                    snackbar = Snackbar.make(findViewById(android.R.id.content),
                            "No internet connection", Snackbar.LENGTH_LONG);
                    snackbar.show();                }
            });

            logout.setPositiveButton("Stay", (dialog, which) -> dialog.cancel());
            logout.show();
        });
    }

    private void Doctor_is_Available() {
      try {
          if(mauth.getCurrentUser() != null){
              final HashMap<String, Object> is_available = new HashMap<>();
              is_available.put("doc", "here");
              DatabaseReference avail_reference = FirebaseDatabase.getInstance().getReference("available")
                      .child(mauth.getCurrentUser().getUid());
              avail_reference.setValue(is_available).addOnCompleteListener(task -> {
                  if(task.isSuccessful()){
                      main_accessor.put("has_made_available", "yes");
                      snackbar = Snackbar.make(findViewById(android.R.id.content),
                              "You are now available for users", Snackbar.LENGTH_LONG);
                      snackbar.show();
                  }
              });
          }
      }catch (NullPointerException e){

      }
    }

    private void Doctor_is_UnAvailable() {
        try {
            if(mauth.getCurrentUser() != null){
                final HashMap<String, Object> is_available = new HashMap<>();
                is_available.put("doc", "here");
                DatabaseReference remove_avail_reference = FirebaseDatabase.getInstance().getReference("available")
                        .child(mauth.getCurrentUser().getUid());
                remove_avail_reference.removeValue().addOnCompleteListener(task -> {
                    main_accessor.put("has_made_available", "no");
                    snackbar = Snackbar.make(findViewById(android.R.id.content),
                            "Users would not be able to reach you.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                });
            }
        }catch (NullPointerException e){

        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mauth.getCurrentUser() != null){
            if(new Accessories(this).getString("has_named").equals("true")){
                if(main_accessor.getString("has_made_available").equals("yes")){
                    is_available_switch.setChecked(true);
                }
                startService(new Intent(MainActivity.this, Incoming_calls_service.class));
            }else{
                startActivity(new Intent(MainActivity.this, Register.class));
            }
        }else{
            startActivity(new Intent(MainActivity.this, Login_.class));
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        has_open_video = "";
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        has_open_video = "";
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Doctor_is_UnAvailable();
    }
}
