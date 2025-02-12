package com.example.discourd.modele;

import com.google.gson.annotations.SerializedName;

public class Message {
    @SerializedName("Id_Message")
    private int idMessage;

    @SerializedName("message")
    private String message;

    @SerializedName("dateMessage")
    private String dateMessage;

    @SerializedName("envoyeur")
    private int envoyeurId;

    @SerializedName("destinataire")
    private int destinataireId;

    @SerializedName("envoyeurPseudo")
    private String envoyeurPseudo;

    @SerializedName("destinatairePseudo")
    private String destinatairePseudo;

    // Constructeur complet
    public Message(int idMessage, String message, String dateMessage, int envoyeurId, int destinataireId, String envoyeurPseudo, String destinatairePseudo) {
        this.idMessage = idMessage;
        this.message = message;
        this.dateMessage = dateMessage;
        this.envoyeurId = envoyeurId;
        this.destinataireId = destinataireId;
        this.envoyeurPseudo = envoyeurPseudo;
        this.destinatairePseudo = destinatairePseudo;
    }

    // Getters et setters
    public int getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(int idMessage) {
        this.idMessage = idMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateMessage() {
        return dateMessage;
    }

    public void setDateMessage(String dateMessage) {
        this.dateMessage = dateMessage;
    }

    public int getEnvoyeurId() {
        return envoyeurId;
    }

    public void setEnvoyeurId(int envoyeurId) {
        this.envoyeurId = envoyeurId;
    }

    public int getDestinataireId() {
        return destinataireId;
    }

    public void setDestinataireId(int destinataireId) {
        this.destinataireId = destinataireId;
    }

    public String getEnvoyeurPseudo() {
        return envoyeurPseudo;
    }

    public void setEnvoyeurPseudo(String envoyeurPseudo) {
        this.envoyeurPseudo = envoyeurPseudo;
    }

    public String getDestinatairePseudo() {
        return destinatairePseudo;
    }

    public void setDestinatairePseudo(String destinatairePseudo) {
        this.destinatairePseudo = destinatairePseudo;
    }

    // Implémentation de equals() et hashCode() pour comparer les messages
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return idMessage == message.idMessage;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idMessage);
    }
}
