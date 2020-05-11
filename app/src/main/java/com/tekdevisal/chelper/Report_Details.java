package com.tekdevisal.chelper;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tekdevisal.chelper.Helpers.Accessories;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Report_Details extends AppCompatActivity implements OnMapReadyCallback{

    private String report_id,patient_id, title, message, latitude, longitude,user_address,
            patient_name_string,patient_phone_number,add;
    private Accessories report_accessor;
    private GoogleMap mMap;
    private TextView patient_name, patient_number, patient_address, report_detail;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report__details);

        getSupportActionBar().setTitle("Covaid | Report Details");

        patient_name = findViewById(R.id.patient_name);
        patient_number = findViewById(R.id.patient_phone);
        patient_address =findViewById(R.id.patient_address);
        report_detail =findViewById(R.id.report_detail);

        report_accessor = new Accessories(Report_Details.this);
        report_id = report_accessor.getString("report_id");
        patient_id = report_accessor.getString("reporter_id");
        patient_name_string = report_accessor.getString("patient_name");
        title = report_accessor.getString("title");
        message = report_accessor.getString("message");
        latitude = report_accessor.getString("latitude");
        longitude = report_accessor.getString("longitude");
        patient_phone_number = report_accessor.getString("phone_number");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        patient_name.setText(patient_name_string);
        patient_number.setText(patient_phone_number);
        patient_address.setText(user_address);
        report_detail.setText(message);
        getAddress(Double.parseDouble(latitude), Double.parseDouble(longitude),patient_address);

        findViewById(R.id.call_patient_layout).setOnClickListener(v -> openDialer(v, patient_phone_number));

        findViewById(R.id.message_patient_layout).setOnClickListener(v -> Toast.makeText(Report_Details.this, "Messaging functionality would be added soon", Toast.LENGTH_LONG).show());

        findViewById(R.id.call_ambulance_layout).setOnClickListener(v -> {
            final AlertDialog.Builder logout = new AlertDialog.Builder(Report_Details.this, R.style.Myalert);
            logout.setTitle("Dispatch Ambulance?");
            logout.setMessage("Are you sure you want to dispatch ambulance to "+add);
            logout.setNegativeButton("Confirm", (dialog, which) -> {
                if(isNetworkAvailable()){
                    snackbar = Snackbar.make(findViewById(android.R.id.content),
                            "Ambulance has been dispatched", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }else{
                    snackbar = Snackbar.make(findViewById(android.R.id.content),
                            "No internet connection", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            });

            logout.setPositiveButton("Cancel", (dialog, which) -> dialog.cancel());
            logout.show();
        });

        findViewById(R.id.dismiss_patient_layout).setOnClickListener(v -> {
            final AlertDialog.Builder logout = new AlertDialog.Builder(Report_Details.this, R.style.Myalert);
            logout.setTitle("Dismiss Report?");
            logout.setMessage("Are you sure you want to dismiss this report from "+patient_name_string);
            logout.setNegativeButton("Confirm", (dialog, which) -> {
                if(isNetworkAvailable()){
                    RemoveReport(patient_id,report_id);
                }else{
                    snackbar = Snackbar.make(findViewById(android.R.id.content),
                            "No internet connection", Snackbar.LENGTH_LONG);
                    snackbar.show();                }
            });

            logout.setPositiveButton("Cancel", (dialog, which) -> dialog.cancel());
            logout.show();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng user_location = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(user_location)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.addMarker(new MarkerOptions().position(user_location).title("Patient"));



    }

    public void RemoveReport(String the_patient_id,String the_report_id){
        FirebaseDatabase.getInstance().getReference("reports").child(the_patient_id)
                .child(the_report_id).removeValue().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        snackbar = Snackbar.make(findViewById(android.R.id.content),
                                "Report Dismissed", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        startActivity(new Intent(Report_Details.this, Reports.class));
                        finish();
                    }
                });
    }

    public void getAddress(double lat, double lng,TextView addressView) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            add = obj.getAddressLine(0);

            addressView.setText(add);


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openDialer(View v, String call_number){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + call_number));
        v.getContext().startActivity(intent);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
