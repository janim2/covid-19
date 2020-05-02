package com.tekdevisal.chelper.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tekdevisal.chelper.Models.Doctors;
import com.tekdevisal.chelper.Models.Report_Model;
import com.tekdevisal.chelper.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder>{
    ArrayList<Report_Model> itemList;
    Context context;
    FirebaseAuth auth = FirebaseAuth.getInstance();

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
        TextView title = holder.view.findViewById(R.id.title);
        ImageView mapimage = holder.view.findViewById(R.id.map_image);
//
//        try{
//            Glide
//                .with(context)
//                .load("http://maps.google.com/maps/api/staticmap?center=" +
//                        itemList.get(position).getUserlocation().latitude +
//                        "," +
//                        itemList.get(position).getUserlocation().longitude +
//                        "&zoom=15&size=200x200&sensor=false" +
//                        "&markers=color:red%7Clabel:C%" +
//                        itemList.get(position).getUserlocation().latitude +
//                        "," +
//                        itemList.get(position).getUserlocation().longitude)
//                    .into(mapimage);
//       }catch (NullPointerException e){
//            e.printStackTrace();
//        }
        mapimage.setImageDrawable(context.getResources().getDrawable(R.drawable.map_));
        title.setText(itemList.get(position).getTitle());
        report_cardView.setOnClickListener(v -> {
            Toast.makeText(context,"will be added soon", Toast.LENGTH_LONG).show();
        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


}
