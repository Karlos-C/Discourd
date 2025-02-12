package com.example.discourd.vue;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.discourd.R;
import com.example.discourd.util.PreferencesManager;

public class Vue_Parametres extends AppCompatActivity {
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.vue_parametres);

        // Initialiser le gestionnaire de préférences
        preferencesManager = new PreferencesManager(this);

        // Récupère l'élément ScrollView principal avec l'ID "main"
        ScrollView mainLayout = findViewById(R.id.main);

        LinearLayout layoutCompte = findViewById(R.id.layoutCompte);
        LinearLayout layoutConfidentialite = findViewById(R.id.layoutConfidentialite);
        LinearLayout layoutDemandesAmis = findViewById(R.id.layoutDemandeAmis);
        LinearLayout layoutApparence = findViewById(R.id.layoutApparence);
        LinearLayout layoutNotifications = findViewById(R.id.layoutNotifications);

        // Configurer les actions pour les boutons du menu
        layoutCompte.setOnClickListener(v -> {
            Intent intent = new Intent(Vue_Parametres.this, Vue_Compte.class);
            startActivity(intent);
        });

        layoutConfidentialite.setOnClickListener(v -> {
            Intent intent = new Intent(Vue_Parametres.this, Vue_Confidentialite.class);
            startActivity(intent);
        });

        layoutDemandesAmis.setOnClickListener(v -> {
            Intent intent = new Intent(Vue_Parametres.this, Vue_Demandes_Amis.class);
            startActivity(intent);
        });

        layoutApparence.setOnClickListener(v -> {
            Intent intent = new Intent(Vue_Parametres.this, Vue_Apparence.class);
            startActivity(intent);
        });

        layoutNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(Vue_Parametres.this, Vue_Notifications.class);
            startActivity(intent);
        });

        // Applique les marges dynamiques en fonction des barres de navigation et de statut
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configurer le bouton de déconnexion
        Button boutonDeconnexion = findViewById(R.id.buttonDeconnexion);
        boutonDeconnexion.setOnClickListener(v -> {
            // Vider les préférences
            preferencesManager.clearUser();

            // Naviguer vers Vue_Connexion
            Intent intent = new Intent(Vue_Parametres.this, Vue_Connexion.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Configurer le footer
        Vue_Footer.setupFooter(this);
    }
}
