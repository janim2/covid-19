package com.tekdevisal.chelper;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tekdevisal.chelper.LocationUtil.LocationHelper;

import java.util.HashMap;

public class Report_ extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,ActivityCompat.OnRequestPermissionsResultCallback{

    private String title, message, location_address;
    private EditText title_edittext, message_edittext;
    private Snackbar snackbar;
    private LocationHelper locationHelper;
    private Location myLocation;
    double latitudeD,longitudeD;
    private FirebaseAuth myauth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_);
        getSupportActionBar().setTitle("Covaid | REPORT");

        myauth = FirebaseAuth.getInstance();

        locationHelper = new LocationHelper(Report_.this);
        locationHelper.checkpermission();

        if(locationHelper.checkPlayServices()){
            locationHelper.buildGoogleApiClient();

        }

        title_edittext = findViewById(R.id.subject);
        message_edittext = findViewById(R.id.message);

        findViewById(R.id.call).setOnClickListener(v -> {
            openDialer(v,"112");
        });

        findViewById(R.id.whatsapp).setOnClickListener(v -> {
            openWhatsApp();
        });

        findViewById(R.id.send).setOnClickListener(v -> {
            title = title_edittext.getText().toString().trim();
            message = message_edittext.getText().toString().trim();

            if(title.equals("")){
                snackbar = Snackbar.make(findViewById(android.R.id.content),
                        "Title required", Snackbar.LENGTH_LONG);
                snackbar.show();
            }

            else if(message.equals("")){
                snackbar = Snackbar.make(findViewById(android.R.id.content),
                        "Message required", Snackbar.LENGTH_LONG);
                snackbar.show();
            }

            else{
                Upload_report(title, message);
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        myLocation = locationHelper.getLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationHelper.checkPlayServices();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        locationHelper.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationHelper.onRequestPermissionsResult(requestCode,permissions,grantResults);
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

    private void openDialer(View v, String call_number){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + call_number));
        v.getContext().startActivity(intent);
    }

    private void openWhatsApp() {
        String smsNumber = "233268977129"; // E164 format without '+' sign
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Suspected covid 19 case");
        sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net"); //phone number without "+" prefix
        sendIntent.setPackage("com.whatsapp");
        if (sendIntent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this, "Whatsapp not installed", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(sendIntent);
    }

    private void Upload_report(String title, String message) {

        myLocation = locationHelper.getLocation();

        latitudeD = myLocation.getLatitude();
        longitudeD = myLocation.getLongitude();
//        location_address = locationHelper.getAddress(latitudeD, longitudeD).toString();
        reference = FirebaseDatabase.getInstance().getReference("reports")
                .child(myauth.getCurrentUser().getUid());
        if(myLocation != null){

            final HashMap<String, Object> report_ = new HashMap<>();
            report_.put("title", title);
            report_.put("message", message);
            report_.put("latitude", latitudeD);
            report_.put("longitude", longitudeD);
//            report_.put("address", location_address);

            reference.push().setValue(report_).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    snackbar = Snackbar.make(findViewById(android.R.id.content),
                            "Report Submitted", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            });
           }else {
            snackbar = Snackbar.make(findViewById(android.R.id.content),
                    "Location not found", Snackbar.LENGTH_LONG);
            snackbar.show();        }
    }
}
