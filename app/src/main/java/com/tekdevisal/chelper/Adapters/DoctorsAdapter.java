package com.tekdevisal.chelper.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tekdevisal.chelper.ChatActivity;
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
        ImageView start_message = holder.view.findViewById(R.id.start_message);
        adapater = new Accessories(context);

        title.setText(itemList.get(position).getName());
        if(!adapater.getString("user_type").equals("doctor")){
            start_message.setVisibility(View.VISIBLE);
        }

        start_message.setOnClickListener(view -> {
            createChat(auth.getUid(),itemList.get(position).getId(),itemList.get(position).getName());
        });

        title.setOnClickListener(v -> {
            if(adapater.getString("user_type").equals("doctor")){
                createChat(auth.getUid(),itemList.get(position).getId(),itemList.get(position).getName());
            }else{
                Intent togoCall = new Intent(context, VideoChat_Activity.class);
                adapater.put("doc_id", itemList.get(position).getId());
                adapater.put("doc_name", itemList.get(position).getName());
                adapater.put("action", "icalled");
                v.getContext().startActivity(togoCall);
            }

        });
    }

    private void createChat(String myID, String person_to_chatID, String person_toChatName){
        String chat_key = FirebaseDatabase.getInstance().getReference("chat").push().getKey();
        DatabaseReference createchat_reference = FirebaseDatabase.getInstance()
                .getReference("chat");
        final HashMap<String, Object> create_chat = new HashMap<>();
        create_chat.put("chat_ID", chat_key);

        createchat_reference.child(myID).child(person_to_chatID).child(chat_key)
                .setValue(create_chat).addOnCompleteListener(task -> {
            if(task.isSuccessful()){

                //adding the receipent chat details
                DatabaseReference creating_receipent = FirebaseDatabase.getInstance()
                        .getReference("chat");
                final HashMap<String, Object> create_other_chat = new HashMap<>();
                create_other_chat.put("chat_ID", chat_key);

                creating_receipent.child(person_to_chatID).child(myID).child(chat_key)
                        .setValue(create_other_chat).addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()){
                        Intent chat_activity = new Intent(context, ChatActivity.class);
                        adapater.put("current_chat_ID", chat_key);
                        adapater.put("current_chat_name", person_toChatName);
                        adapater.put("person_iam_chattingID", person_to_chatID);
                        context.startActivity(chat_activity);
                    }
                });
            }
        });

    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }


}
