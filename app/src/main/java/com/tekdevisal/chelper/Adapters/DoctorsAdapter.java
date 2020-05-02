package com.tekdevisal.chelper.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tekdevisal.chelper.Helpers.Accessories;
import com.tekdevisal.chelper.Models.Doctors;
import com.tekdevisal.chelper.R;
import com.tekdevisal.chelper.VideoChat_Activity;

import java.util.ArrayList;
import java.util.HashMap;

public class DoctorsAdapter extends RecyclerView.Adapter<DoctorsAdapter.ViewHolder>{
    ArrayList<Doctors> itemList;
    Context context;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("calls");
    Accessories adapater;

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public DoctorsAdapter(ArrayList<Doctors> itemList, Context context){
        this.itemList  = itemList;
        this.context  = context;
    }

    @Override
    public DoctorsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctors_attachment,parent,false);
        ViewHolder vh = new ViewHolder(layoutView);
        return vh;
    }


    @Override
    public void onBindViewHolder(DoctorsAdapter.ViewHolder holder, final int position) {
        CardView doc_card = holder.view.findViewById(R.id.doc_card);
        TextView title = holder.view.findViewById(R.id.doctor_name);

        title.setText(itemList.get(position).getName());

        doc_card.setOnClickListener(v -> {
            adapater = new Accessories(context);
            if(adapater.getString("user_type").equals("doctor")){
                Toast.makeText(context,"messaging functionality will be added soon", Toast.LENGTH_LONG).show();
            }else{
                Intent togoCall = new Intent(context, VideoChat_Activity.class);
                adapater.put("doc_id", itemList.get(position).getId());
                adapater.put("doc_name", itemList.get(position).getName());
                adapater.put("action", "icalled");
                v.getContext().startActivity(togoCall);
            }

        });


    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }


}
