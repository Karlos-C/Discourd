package com.example.discourd.vue;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.discourd.R;
import com.example.discourd.donnee.ApiService;
import com.example.discourd.donnee.RetrofitClient;
import com.example.discourd.modele.ApiResponse;
import com.example.discourd.modele.Utilisateur;
import com.example.discourd.util.ImageLoader;
import com.example.discourd.util.PreferencesManager;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Vue_Modifier_Profil extends AppCompatActivity {
    private static final String TAG = "Vue_Modifier_Profil";

    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vue_modifier_profil);

        preferencesManager = new PreferencesManager(this);

        // Récupérer les données utilisateur
        Utilisateur utilisateur = preferencesManager.getUser();
        if (utilisateur == null) {
            Toast.makeText(this, "Erreur : Utilisateur introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Références des vues
        ImageView avatar = findViewById(R.id.avatarModifierProfil);
        TextView displayPseudo = findViewById(R.id.displayPseudo);
        TextView displayEmail = findViewById(R.id.displayEmail);
        EditText inputPseudo = findViewById(R.id.inputPseudo);
        EditText inputEmail = findViewById(R.id.inputEmail);
        Button btnConfirmer = findViewById(R.id.btnConfirmerModification);

        // Préremplir les vues
        displayPseudo.setText(utilisateur.getPseudo());
        displayEmail.setText(utilisateur.getEmail());
        inputPseudo.setText(utilisateur.getPseudo());
        inputEmail.setText(utilisateur.getEmail());

        // Charger une image de profil si définie
        // Charger une image de profil si définie
        String imageProfilName = utilisateur.getImage();
        ImageLoader.chargerImageProfil(this, imageProfilName, avatar);


        // Action du bouton Confirmer
        btnConfirmer.setOnClickListener(v -> {
            String nouveauPseudo = inputPseudo.getText().toString().trim();
            String nouvelEmail = inputEmail.getText().toString().trim();

            if (nouveauPseudo.isEmpty() || nouvelEmail.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            modifierProfil(utilisateur.getId(), nouveauPseudo, nouvelEmail);
        });

        // Configurer le footer
        Vue_Footer.setupFooter(this);
    }

    private void modifierProfil(int userId, String pseudo, String email) {
        ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);

        HashMap<String, String> body = new HashMap<>();
        body.put("userId", String.valueOf(userId));
        body.put("pseudo", pseudo);
        body.put("email", email);

        Call<ApiResponse> call = apiService.modifierProfil(body);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equals(response.body().getStatus())) {
                        Toast.makeText(Vue_Modifier_Profil.this, "Profil modifié avec succès", Toast.LENGTH_SHORT).show();

                        // Mettre à jour les préférences
                        Utilisateur utilisateur = preferencesManager.getUser();
                        if (utilisateur != null) {
                            utilisateur.setPseudo(pseudo);
                            utilisateur.setEmail(email);
                            preferencesManager.saveUser(utilisateur);
                            preferencesManager.updateUser(pseudo, email);

                        }

                        finish();
                    } else {
                        Toast.makeText(Vue_Modifier_Profil.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Vue_Modifier_Profil.this, "Erreur lors de la modification", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Erreur de réponse : " + response.errorBody());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Toast.makeText(Vue_Modifier_Profil.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Erreur réseau : ", t);
            }
        });
    }

}
