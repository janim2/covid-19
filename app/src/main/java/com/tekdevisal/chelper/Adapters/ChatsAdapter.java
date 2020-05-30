package com.tekdevisal.chelper.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tekdevisal.chelper.ChatActivity;
import com.tekdevisal.chelper.Helpers.Accessories;
import com.tekdevisal.chelper.Models.Chats;
import com.tekdevisal.chelper.Models.Doctors;
import com.tekdevisal.chelper.R;
import com.tekdevisal.chelper.VideoChat_Activity;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder>{
    ArrayList<Chats> itemList;
    Context context;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    Accessories adapater;

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public ChatsAdapter(ArrayList<Chats> itemList, Context context){
        this.itemList  = itemList;
        this.context  = context;
    }

    @Override
    public ChatsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mychats_attachment,parent,false);
        ViewHolder vh = new ViewHolder(layoutView);
        return vh;
    }


    @Override
    public void onBindViewHolder(ChatsAdapter.ViewHolder holder, final int position) {
        LinearLayout chat_card = holder.view.findViewById(R.id.chat_layout);
        TextView chat_name = holder.view.findViewById(R.id.chats);
        adapater = new Accessories(context);

        chat_name.setText(itemList.get(position).getName());

        chat_card.setOnClickListener(view -> {
            Intent chat_activity = new Intent(context, ChatActivity.class);
            adapater.put("person_iam_chattingID", itemList.get(position).getReceipient_ID());
            adapater.put("current_chat_ID", itemList.get(position).getChat_id());
            adapater.put("current_chat_name", itemList.get(position).getName());
            context.startActivity(chat_activity);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


}
