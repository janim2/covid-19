package com.tekdevisal.chelper.Adapters;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tekdevisal.chelper.Helpers.Accessories;
import com.tekdevisal.chelper.Models.Doctors;
import com.tekdevisal.chelper.Models.Report_Model;
import com.tekdevisal.chelper.R;
import com.tekdevisal.chelper.Report_Details;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder>{
    ArrayList<Report_Model> itemList;
    Context context;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    Accessories report_accessor;

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public ReportAdapter(ArrayList<Report_Model> itemList, Context context){
        this.itemList  = itemList;
        this.context  = context;
    }

    @Override
    public ReportAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.reports_attachment,parent,false);
        ViewHolder vh = new ViewHolder(layoutView);
        return vh;
    }


    @Override
    public void onBindViewHolder(ReportAdapter.ViewHolder holder, final int position) {
        CardView  report_cardView = holder.view.findViewById(R.id.report_cardView);
        TextView address = holder.view.findViewById(R.id.address);
        TextView patient_name = holder.view.findViewById(R.id.patient_name);
        ImageView call_button = holder.view.findViewById(R.id.call_button);

        DatabaseReference userdetails = FirebaseDatabase.getInstance().getReference("users")
                .child(itemList.get(position).getPerson_who_reported_id());
        userdetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()) {
                        if(child.getKey().equals("name")){
                            String patient_name_string = child.getValue().toString();
                            patient_name.setText(patient_name_string);
                            new Accessories(context).put("patient_name", patient_name_string);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        getAddress(itemList.get(position).getUserlocation().latitude,
                itemList.get(position).getUserlocation().longitude, address);

        call_button.setOnClickListener(v -> {
            openDialer(v,itemList.get(position).getPhone_number());
        });

        report_cardView.setOnClickListener(v -> {
            Intent report_details_intent = new Intent(context, Report_Details.class);
            report_accessor = new Accessories(context);
            report_accessor.put("report_id", itemList.get(position).getReport_Id());
            report_accessor.put("reporter_id", itemList.get(position).getPerson_who_reported_id());
            report_accessor.put("title", itemList.get(position).getTitle());
            report_accessor.put("message", itemList.get(position).getMessage());
            report_accessor.put("phone_number", itemList.get(position).getPhone_number());
            report_accessor.put("latitude", String.valueOf(itemList.get(position).getUserlocation().latitude));
            report_accessor.put("longitude", String.valueOf(itemList.get(position).getUserlocation().longitude));


            v.getContext().startActivity(report_details_intent);
        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void getAddress(double lat, double lng,TextView addressView) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
//            add = add + "\n" + obj.getCountryName();
//            add = add + "\n" + obj.getCountryCode();
//            add = add + "\n" + obj.getAdminArea();
//            add = add + "\n" + obj.getPostalCode();
//            add = add + "\n" + obj.getSubAdminArea();
//            add = add + "\n" + obj.getLocality();
//            add = add + "\n" + obj.getSubThoroughfare();

//            Log.v("IGA", "Address" + add);
//             Toast.makeText(context, "Address=>" + add,
//             Toast.LENGTH_SHORT).show();
            addressView.setText(add);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openDialer(View v, String call_number){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + call_number));
        v.getContext().startActivity(intent);
    }


}
