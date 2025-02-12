package com.example.discourd.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.discourd.modele.Utilisateur;

public class PreferencesManager {
    private static final String PREF_NAME = "DiscourdPreferences";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_PSEUDO = "pseudo";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_DATE_CREATION = "date_creation";

    private static final String KEY_TOKEN = "token";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public PreferencesManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    // Sauvegarder les informations de l'utilisateur
    public void saveUser(Utilisateur utilisateur) {
        Log.d("PreferencesManager", "Sauvegarde utilisateur : ID=" + utilisateur.getId() + ", Token=" + utilisateur.getToken());
        editor.putInt(KEY_USER_ID, utilisateur.getId());
        editor.putString(KEY_PSEUDO, utilisateur.getPseudo());
        editor.putString(KEY_EMAIL, utilisateur.getEmail());
        editor.putString(KEY_IMAGE, utilisateur.getImage());
        editor.putString(KEY_DATE_CREATION, utilisateur.getDateCreation());
        editor.putString(KEY_TOKEN, utilisateur.getToken());
        editor.apply();
        Log.d("PreferencesManager", "Utilisateur sauvegardé avec succès");
    }






    // Mettre à jour des champs spécifiques pour l'utilisateur
    public void updateUser(String pseudo, String email) {
        if (pseudo != null) {
            editor.putString(KEY_PSEUDO, pseudo);
        }
        if (email != null) {
            editor.putString(KEY_EMAIL, email);
        }
        editor.apply();
    }

    // Récupérer l'utilisateur
    public Utilisateur getUser() {
        int id = preferences.getInt(KEY_USER_ID, -1);
        String pseudo = preferences.getString(KEY_PSEUDO, null);
        String email = preferences.getString(KEY_EMAIL, null);
        String image = preferences.getString(KEY_IMAGE, null);
        String dateCreation = preferences.getString(KEY_DATE_CREATION, null);
        String token = preferences.getString(KEY_TOKEN, null);

        Log.d("PreferencesManager", "Récupération utilisateur : ID=" + id + ", Token=" + token);

        if (id == -1 || pseudo == null || email == null || token == null) {
            Log.e("PreferencesManager", "Utilisateur non valide ou données manquantes");
            return null;
        }

        return new Utilisateur(id, pseudo, email, null, image, dateCreation, token);
    }





    // Effacer les données utilisateur
    public void clearUser() {
        editor.clear();
        editor.apply();
    }

    // Mettre à jour uniquement l'image
    public void updateUserImage(String image) {
        editor.putString(KEY_IMAGE, image);
        editor.apply();
    }

    // Récupérer l'image de l'utilisateur
    public String getUserImage() {
        return preferences.getString(KEY_IMAGE, null);
    }


}
