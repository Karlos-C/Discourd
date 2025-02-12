package com.example.discourd.vue;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.discourd.R;
import com.example.discourd.donnee.ApiService;
import com.example.discourd.donnee.RetrofitClient;
import com.example.discourd.modele.ApiResponse;
import com.example.discourd.modele.ConnexionUtilisateur;
import com.example.discourd.modele.Utilisateur;
import com.example.discourd.util.PreferencesManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Vue_Connexion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.vue_connexion);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Récupération des vues
        EditText editTextEmail = findViewById(R.id.emailConnexion);
        EditText editTextMotDePasse = findViewById(R.id.mdpConnexion);
        Button buttonSeConnecter = findViewById(R.id.boutonConnexion);
        TextView textViewInscription = findViewById(R.id.allerVueInscription);

        // Bouton de connexion
        buttonSeConnecter.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String motDePasse = editTextMotDePasse.getText().toString().trim();

            // Vérification des champs
            if (email.isEmpty() || motDePasse.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            // Créer un objet ConnexionUtilisateur
            ConnexionUtilisateur connexionUtilisateur = new ConnexionUtilisateur(email, motDePasse);

            // Envoyer la requête à l'API
            ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
            Call<ApiResponse> call = apiService.connecterUtilisateur(connexionUtilisateur);

            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse apiResponse = response.body();

                        Log.d("Vue_Connexion", "Réponse API brute : " + response.body());

                        if ("success".equals(apiResponse.getStatus())) {
                            Utilisateur utilisateur = apiResponse.getUtilisateur();
                            if (utilisateur != null) {
                                Log.d("Vue_Connexion", "Utilisateur reçu : ID=" + utilisateur.getId() + ", Token=" + utilisateur.getToken());

                                PreferencesManager preferencesManager = new PreferencesManager(Vue_Connexion.this);
                                preferencesManager.saveUser(utilisateur);

                                Log.d("Vue_Connexion", "Utilisateur sauvegardé dans PreferencesManager");

                                Intent intent = new Intent(Vue_Connexion.this, Vue_Accueil.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.e("Vue_Connexion", "Erreur : utilisateur null ou token manquant");
                            }
                        } else {
                            Log.e("Vue_Connexion", "Erreur API : " + apiResponse.getMessage());
                        }
                    } else {
                        Log.e("Vue_Connexion", "Erreur lors de la connexion : " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.e("Vue_Connexion", "Erreur réseau : " + t.getMessage());
                }
            });






        });

        // Lien vers l'écran d'inscription
        textViewInscription.setOnClickListener(v -> {
            Intent intent = new Intent(this, Vue_Inscription.class);
            startActivity(intent);
        });
    }
}
