package com.tekdevisal.chelper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tekdevisal.chelper.Helpers.Accessories;
import com.tekdevisal.chelper.LocationUtil.LocationHelper;

import java.util.HashMap;

public class Register extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private EditText name;
    private Accessories register_accessor;
    private String name_string, user_type;
    private Snackbar snackbar;
    private ProgressDialog progressBar;
    private DatabaseReference user_reference, doctor_reference;
    private FirebaseAuth mauth;
    private LocationHelper locationHelper;
    private Location myLocation;
    double latitudeD,longitudeD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Covaid | Register");
        mauth = FirebaseAuth.getInstance();

        register_accessor = new Accessories(Register.this);

        locationHelper = new LocationHelper(Register.this);
        locationHelper.checkpermission();

        if(locationHelper.checkPlayServices()){
            locationHelper.buildGoogleApiClient();

        }

        progressBar = new ProgressDialog(this);

        user_type = register_accessor.getString("user_type");
        name = findViewById(R.id.name);

        findViewById(R.id.continueNextButton).setOnClickListener(v -> {
            progressBar.dismiss();
            name_string = name.getText().toString().trim();
            if(name_string.equals("")){
                progressBar.dismiss();
                snackbar = Snackbar.make(findViewById(android.R.id.content),
                        "Name required", Snackbar.LENGTH_LONG);
                snackbar.show();
            }else{
                progressBar.setTitle("Saving name");
                progressBar.setMessage("Please Wait...");
                progressBar.setCanceledOnTouchOutside(false);
                progressBar.show();
                Save_name(name_string);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void Save_name(String name) {
        try {
            myLocation = locationHelper.getLocation();

            latitudeD = myLocation.getLatitude();
            longitudeD = myLocation.getLongitude();

            final HashMap<String, Object> register = new HashMap<>();
            register.put("name", name);
            register.put("image", "None");
            register.put("latitude", latitudeD);
            register.put("longitude", longitudeD);

            if (user_type.equals("normal_user")){
                if(mauth.getCurrentUser() != null){
                    user_reference = FirebaseDatabase.getInstance().getReference("users").child(mauth.getCurrentUser().getUid());
                    user_reference.setValue(register).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            register_accessor.put("has_named", "true");
                            register_accessor.put("saved_latitude", String.valueOf(latitudeD));
                            register_accessor.put("saved_longitude", String.valueOf(longitudeD));
                            startActivity(new Intent(Register.this, MainActivity.class));
                            finish();
                        }
                    });

                }
                }
            else{
                if(mauth.getCurrentUser() != null){
                    doctor_reference = FirebaseDatabase.getInstance().getReference("doctors").child(mauth.getCurrentUser().getUid());
                    doctor_reference.setValue(register).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            register_accessor.put("has_named", "true");
                            register_accessor.put("saved_latitude", String.valueOf(latitudeD));
                            register_accessor.put("saved_longitude", String.valueOf(longitudeD));
                            startActivity(new Intent(Register.this, MainActivity.class));
                            finish();
                        }
                    });

                }
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        myLocation = locationHelper.getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        locationHelper.connectApiClient();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("Connection Failure","Connnection Error = " +
                connectionResult.getErrorCode());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        locationHelper.onActivityResult(requestCode,resultCode,data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationHelper.onRequestPermissionsResult(requestCode,permissions,grantResults);

    }

    @Override
    protected void onResume() {
        super.onResume();
        locationHelper.checkPlayServices();

    }
}
