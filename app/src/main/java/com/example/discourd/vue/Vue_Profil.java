package com.example.discourd.vue;
import com.bumptech.glide.Glide;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.discourd.R;
import com.example.discourd.modele.Utilisateur;
import com.example.discourd.util.ImageLoader;
import com.example.discourd.util.PreferencesManager;

public class Vue_Profil extends AppCompatActivity {
    private PreferencesManager preferencesManager;
    private ImageView avatar;
    private TextView nom, email, dateMembreDepuis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vue_profil);

        preferencesManager = new PreferencesManager(this);

        // Références des vues
        avatar = findViewById(R.id.avatarProfil);
        nom = findViewById(R.id.nomProfil);
        email = findViewById(R.id.emailProfil);
        dateMembreDepuis = findViewById(R.id.dateMembreDepuis);
        Button modifierProfil = findViewById(R.id.modifierProfil);
        Button modifierPhotoProfil = findViewById(R.id.modifierPhotoProfil); // Nouveau bouton

        // Charger les informations utilisateur
        mettreAJourProfil();

        // Action du bouton Modifier Profil
        modifierProfil.setOnClickListener(v -> {
            Intent intent = new Intent(this, Vue_Modifier_Profil.class);
            startActivity(intent);
        });

        // Action du bouton Modifier Photo de Profil
        modifierPhotoProfil.setOnClickListener(v -> {
            Intent intent = new Intent(this, Vue_Modifier_Photo_Profil.class);
            startActivity(intent);
        });

        // Références au bouton PARAMETRES REGLAGES
        ImageView boutonParametres = findViewById(R.id.buttonParametres);

        // Navigation vers Vue_Parametres
        boutonParametres.setOnClickListener(v -> {
            Intent intent = new Intent(Vue_Profil.this, Vue_Parametres.class);
            startActivity(intent);
        });

        // Configuration du footer
        Vue_Footer.setupFooter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recharger les informations utilisateur à chaque retour sur cette activité
        mettreAJourProfil();
    }

    private void mettreAJourProfil() {
        Utilisateur utilisateur = preferencesManager.getUser();

        if (utilisateur == null) {
            // Rediriger vers la page de connexion si aucun utilisateur n'est connecté
            Intent intent = new Intent(this, Vue_Connexion.class);
            startActivity(intent);
            finish();
            return;
        }

        // Mettre à jour les informations utilisateur dans l'interface
        nom.setText(utilisateur.getPseudo());
        email.setText(utilisateur.getEmail());
        dateMembreDepuis.setText(utilisateur.getDateCreation());

        // Charger l'image de profil
        String imagePath = preferencesManager.getUserImage(); // Chemin partiel, ex : "pseudo.jpg"
        ImageLoader.chargerImageProfil(this, imagePath, avatar);

    }


}
