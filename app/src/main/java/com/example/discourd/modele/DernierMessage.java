package com.example.discourd.modele;

import com.google.gson.annotations.SerializedName;

public class DernierMessage {
    @SerializedName("lastMessageId")
    private int lastMessageId;

    @SerializedName("lastMessageContent")
    private String lastMessageContent;

    @SerializedName("lastMessageDate")
    private String lastMessageDate;

    // Getters et Setters
    public int getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(int lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public String getLastMessageContent() {
        return lastMessageContent;
    }

    public void setLastMessageContent(String lastMessageContent) {
        this.lastMessageContent = lastMessageContent;
    }

    public String getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(String lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }
}
