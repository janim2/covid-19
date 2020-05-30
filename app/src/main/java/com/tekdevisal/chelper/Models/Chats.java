package com.tekdevisal.chelper.Models;

public class Chats {
    public String receipient_ID;
    public String chat_id;
    public String name;

    public Chats(String receipient_ID, String chat_id, String name) {
        this.receipient_ID = receipient_ID;
        this.chat_id = chat_id;
        this.name = name;
    }

    public String getReceipient_ID() {
        return receipient_ID;
    }

    public String getChat_id() {
        return chat_id;
    }

    public String getName() {
        return name;
    }
}
