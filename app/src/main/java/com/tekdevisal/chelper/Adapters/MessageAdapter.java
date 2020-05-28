package com.tekdevisal.chelper.Adapters;

import android.content.Context;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.tekdevisal.chelper.Chat.MessageObject;
import com.tekdevisal.chelper.R;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    ArrayList<MessageObject> itemList;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public MessageAdapter(Context context,ArrayList<MessageObject> itemList){
        this.itemList  = itemList;
        this.context = context;
    }

    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.senders_chatitem, parent, false);
            return new ViewHolder(view);
        }
        else if (viewType == 2) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receivers_chatitem, parent, false);
            return new ViewHolder(view);
        } else {
            throw new RuntimeException("The type has to be ONE or TWO");
        }

    }

    @Override
    public void onBindViewHolder(MessageAdapter.ViewHolder holder, final int position) {

        switch (holder.getItemViewType()) {
            case 1:
                initLayoutOne((ViewHolder)holder, position);
                break;
            case 2:
                initLayoutTwo((ViewHolder) holder, position);
                break;
            default:
                break;
        }
    }

    private void initLayoutOne(ViewHolder holder, final int pos) {

        // Get references to the views of message.xml
        //senders chat item
        TextView senders_name = holder.view.findViewById(R.id.sender_text_view);
        TextView message = holder.view.findViewById(R.id.message_text_view);
        TextView time = holder.view.findViewById(R.id.timestamp_text_view);

        message.setText(itemList.get(pos).getMessage());

    }

    private void initLayoutTwo(ViewHolder holder, final int pos) {

        // Get references to the views of message.xml
        //receivers chat item
        TextView receivers_name = holder.view.findViewById(R.id.sender_text_view);
        TextView message = holder.view.findViewById(R.id.message_text_view);
        TextView time = holder.view.findViewById(R.id.timestamp_text_view);

        message.setText(itemList.get(pos).getMessage());
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        MessageObject item = itemList.get(position);
        if (item.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return 1;
        }
//        else if (item.getMessageUser().equals("customerCare")) {
//            return 2;
//        }
        else {
            return 2;
        }
    }

}