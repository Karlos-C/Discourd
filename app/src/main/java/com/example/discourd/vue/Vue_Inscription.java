package com.example.discourd.vue;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.discourd.R;
import com.example.discourd.donnee.ApiService;
import com.example.discourd.donnee.RetrofitClient;
import com.example.discourd.modele.ApiResponse;
import com.example.discourd.modele.Utilisateur;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Vue_Inscription extends AppCompatActivity {
    private EditText editTextPseudo;
    private EditText editTextMail;
    private EditText editTextMotDePasse;
    private ImageView imageSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vue_inscription);

        editTextPseudo = findViewById(R.id.pseudoInscription);
        editTextMail = findViewById(R.id.emailInscription);
        editTextMotDePasse = findViewById(R.id.mdpInscription);
        Button buttonInscrire = findViewById(R.id.boutonInscription);

        buttonInscrire.setOnClickListener(v -> {
            String pseudo = editTextPseudo.getText().toString().trim();
            String email = editTextMail.getText().toString().trim();
            String motDePasse = editTextMotDePasse.getText().toString().trim();

            // Vérification des champs
            if (pseudo.isEmpty() || email.isEmpty() || motDePasse.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Veuillez entrer une adresse email valide", Toast.LENGTH_SHORT).show();
                return;
            }

            // Créer l'objet Utilisateur avec les champs requis
            Utilisateur utilisateur = new Utilisateur(
                    pseudo,
                    email,
                    motDePasse,
                    null, // Image non fournie ici
                    null  // La date de création est générée côté serveur
            );

            // Envoyer les données à l'API
            ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
            Call<ApiResponse> call = apiService.inscrireUtilisateur(utilisateur);

            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("Inscription", "Réponse API : " + response.body().getMessage());
                        if ("success".equals(response.body().getStatus())) {
                            Toast.makeText(Vue_Inscription.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Vue_Inscription.this, Vue_Connexion.class)); // Redirige vers la vue de connexion
                            finish();
                        } else {
                            Log.d("Inscription", "Échec de l'inscription : " + response.body().getMessage());
                            Toast.makeText(Vue_Inscription.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        try {
                            Log.e("Inscription", "Erreur API : " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("Inscription", "Erreur lors de la lecture du corps de la réponse", e);
                        }
                        Toast.makeText(Vue_Inscription.this, "Erreur de serveur", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.e("Inscription", "Erreur réseau : " + t.getMessage(), t);
                    Toast.makeText(Vue_Inscription.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
