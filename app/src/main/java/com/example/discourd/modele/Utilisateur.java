package com.example.discourd.modele;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Utilisateur implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("pseudo")
    private String pseudo;

    @SerializedName("email")
    private String email;

    @SerializedName("motsDePasse") // Non utilisé dans la réponse API actuelle, mais tu peux l'ajuster si besoin
    private String motsDePasse;

    @SerializedName("image")
    private String image;

    @SerializedName("dateCreation")
    private String dateCreation;

    @SerializedName("token")
    private String token;

    // Constructeur complet
    public Utilisateur(int id, String pseudo, String email, String motsDePasse, String image, String dateCreation, String token) {
        this.id = id;
        this.pseudo = pseudo;
        this.email = email;
        this.motsDePasse = motsDePasse;
        this.image = image;
        this.dateCreation = dateCreation;
        this.token = token;
    }

    // Constructeurs simplifiés (à adapter selon les besoins)

    public Utilisateur(String pseudo, String image) {
        this.pseudo = pseudo;
        this.image = image;
    }

    public Utilisateur(String pseudo, String email, String motsDePasse, String image, String dateCreation) {
        this.pseudo = pseudo;
        this.email = email;
        this.motsDePasse = motsDePasse;
        this.image = image;
        this.dateCreation = dateCreation;
    }

    // Getters et Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotsDePasse() {
        return motsDePasse;
    }

    public void setMotsDePasse(String motsDePasse) {
        this.motsDePasse = motsDePasse;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
