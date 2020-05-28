package com.tekdevisal.chelper;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tekdevisal.chelper.Adapters.MessageAdapter;
import com.tekdevisal.chelper.Chat.MessageObject;
import com.tekdevisal.chelper.Helpers.Accessories;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private Accessories chat_accessor;
    private String current_chatter_name, current_chatter_ID;
    private EditText message_edittext;
    private FloatingActionButton sendButton;

    //recycler view items

    private RecyclerView mChat, mMedia;
    private RecyclerView.Adapter mChatAdapter, mMediaAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager, mMediaLayoutManager;

    int totalMediaUploaded = 0;
    private ArrayList<String> mediaIdList = new ArrayList<>();

    private ArrayList<MessageObject> messageList;
    private String chat_id, message;

    private DatabaseReference mChatMessagesDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chat_accessor = new Accessories(this);

        chat_id = chat_accessor.getString("current_chat_ID");
        current_chatter_name = chat_accessor.getString("current_chat_name");
        current_chatter_ID = chat_accessor.getString("person_iam_chattingID");

        getSupportActionBar().setTitle("Covaid | " + current_chatter_name);
        mChatMessagesDb = FirebaseDatabase.getInstance()
                .getReference("chat_messages");
        message_edittext = findViewById(R.id.input_edit_text);
        sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(view -> {
            message = message_edittext.getText().toString().trim();

            if(!message.equals("")){
                sendMessage(message);
            }else{

            }
        });

        initializeMessage();
        getChatMessages();
    }

    private void getChatMessages() {
        mChatMessagesDb.child(chat_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    String  text = "",
                            senderID = "";
                    ArrayList<String> mediaUrlList = new ArrayList<>();

                    if(dataSnapshot.child("text").getValue() != null)
                        text = dataSnapshot.child("text").getValue().toString();
                    if(dataSnapshot.child("sender_ID").getValue() != null)
                        senderID = dataSnapshot.child("sender_ID").getValue().toString();

                    MessageObject mMessage = new MessageObject(dataSnapshot.getKey(),
                            senderID, text, mediaUrlList);
                    messageList.add(mMessage);
                    mChatLayoutManager.setItemPrefetchEnabled(true);
                    mChatLayoutManager.scrollToPosition(messageList.size()-1);
                    mChatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initializeMessage() {
        messageList = new ArrayList<>();
        mChat= findViewById(R.id.chat_list);
        mChat.setNestedScrollingEnabled(false);
        mChat.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        mChat.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new MessageAdapter(this,messageList);
        mChat.setAdapter(mChatAdapter);
    }

    private void sendMessage(String the_message){
        String messageId = mChatMessagesDb.child(chat_id).push().getKey();
        DatabaseReference newMessageDb = mChatMessagesDb
                .child(chat_id).child(messageId);

        final Map newMessageMap = new HashMap<>();
        newMessageMap.put("text", the_message);
        newMessageMap.put("sender_ID", FirebaseAuth.getInstance().getUid());
        newMessageMap.put("time_stamp", new Date().getTime());
        newMessageDb.updateChildren(newMessageMap).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                message_edittext.setText("");
            }
        });
    }
}

