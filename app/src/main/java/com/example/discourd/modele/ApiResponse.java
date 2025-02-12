package com.example.discourd.modele;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message; // Message d'erreur ou de succès

    @SerializedName("utilisateur")
    private Utilisateur utilisateur; // Pour un utilisateur unique

    @SerializedName("utilisateurs")
    private List<Utilisateur> utilisateurs; // Pour une liste d'utilisateurs

    @SerializedName("demandes")
    private List<Utilisateur> demandes; // Pour les demandes d'amis

    // Attributs pour les messages
    @SerializedName("messageId")
    private int messageId; // ID du message envoyé

    @SerializedName("messageDate")
    private String messageDate; // Date du message envoyé

    @SerializedName("dernierMessage")
    private Message dernierMessage; // Dernier message récupéré

    @SerializedName("messages")
    private List<Message> messages; // Liste des messages

    // Nouveau champ pour gérer la réponse contenant "data"
    @SerializedName("data")
    private DernierMessage dernierMessageData;

    // Getter et Setter pour `status`
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getter et Setter pour `message` (erreurs ou messages de confirmation)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Getter et Setter pour un utilisateur unique
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    // Getter et Setter pour une liste d'utilisateurs
    public List<Utilisateur> getUtilisateurs() {
        return utilisateurs;
    }

    public void setUtilisateurs(List<Utilisateur> utilisateurs) {
        this.utilisateurs = utilisateurs;
    }

    // Getter et Setter pour les demandes d'amis
    public List<Utilisateur> getDemandes() {
        return demandes;
    }

    public void setDemandes(List<Utilisateur> demandes) {
        this.demandes = demandes;
    }

    // Getter et Setter pour `messageId`
    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    // Getter et Setter pour `messageDate`
    public String getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }

    // Getter et Setter pour le dernier message
    public Message getDernierMessage() {
        return dernierMessage;
    }

    public void setDernierMessage(Message dernierMessage) {
        this.dernierMessage = dernierMessage;
    }

    // Getter et Setter pour la liste des messages
    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    // Getter et Setter pour le dernier message via "data"
    public DernierMessage getDernierMessageData() {
        return dernierMessageData;
    }

    public void setDernierMessageData(DernierMessage dernierMessageData) {
        this.dernierMessageData = dernierMessageData;
    }
}


