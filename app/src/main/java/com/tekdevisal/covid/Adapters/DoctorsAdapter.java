package com.tekdevisal.covid.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
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
import com.tekdevisal.covid.Dialer;
import com.tekdevisal.covid.Models.Doctors;
import com.tekdevisal.covid.R;

import java.util.ArrayList;
import java.util.HashMap;

public class DoctorsAdapter extends RecyclerView.Adapter<DoctorsAdapter.ViewHolder>{
    ArrayList<Doctors> itemList;
    Context context;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("calls");

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
            final HashMap<String, Object> calling = new HashMap<>();
            calling.put("ringing", auth.getCurrentUser().getUid());

            reference.child(itemList.get(position).getId()).setValue(calling).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Intent togoCall = new Intent(context, Dialer.class);
                    togoCall.putExtra("doc_id", itemList.get(position).getId());
                    togoCall.putExtra("doc_name", itemList.get(position).getName());
                    togoCall.putExtra("action", "iamcalling");
                    v.getContext().startActivity(togoCall);
                }
            });

        });


    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }


}
