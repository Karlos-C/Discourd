package com.example.discourd.util;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.discourd.vue.Vue_Accueil;
import com.example.discourd.vue.Vue_Connexion;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Vérifiez si un utilisateur est connecté
        PreferencesManager preferencesManager = new PreferencesManager(this);

        if (preferencesManager.getUser() != null) {
            // Utilisateur connecté : redirige vers l'accueil
            Intent intent = new Intent(this, Vue_Accueil.class);
            startActivity(intent);
        } else {
            // Aucun utilisateur connecté : redirige vers la page de connexion
            Intent intent = new Intent(this, Vue_Connexion.class);
            startActivity(intent);
        }

        // Finir MainActivity pour ne pas pouvoir revenir dessus
        finish();
    }
}
