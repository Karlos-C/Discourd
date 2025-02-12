package com.example.discourd.vue;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.discourd.R;
import com.example.discourd.donnee.ApiService;
import com.example.discourd.donnee.RetrofitClient;
import com.example.discourd.modele.ApiResponse;
import com.example.discourd.util.PreferencesManager;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Vue_Ajout_Ami extends AppCompatActivity {

    private static final String TAG = "Vue_Ajout_Ami";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vue_ajout_ami);

        EditText inputPseudoAmi = findViewById(R.id.input_pseudo_ami);
        Button boutonEnvoyerDemande = findViewById(R.id.bouton_envoyer_demande);

        PreferencesManager preferencesManager = new PreferencesManager(this);
        int utilisateurId = preferencesManager.getUser().getId();

        boutonEnvoyerDemande.setOnClickListener(v -> {
            String pseudoAmi = inputPseudoAmi.getText().toString().trim();

            if (pseudoAmi.isEmpty()) {
                Toast.makeText(this, "Veuillez entrer un pseudo", Toast.LENGTH_SHORT).show();
                return;
            }

            envoyerDemandeAmi(utilisateurId, pseudoAmi);
        });

        // Configuration du footer
        Vue_Footer.setupFooter(this);
    }

    private void envoyerDemandeAmi(int userId, String pseudoAmi) {
        ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);

        HashMap<String, String> body = new HashMap<>();
        body.put("demandeur", String.valueOf(userId));
        body.put("pseudo_destinataire", pseudoAmi);

        Call<ApiResponse> call = apiService.envoyerDemandeAmi(body); // Utilisation du bon nom de méthode
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equals(response.body().getStatus())) {
                        Toast.makeText(Vue_Ajout_Ami.this, "Demande envoyée avec succès !", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(Vue_Ajout_Ami.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Vue_Ajout_Ami.this, "Erreur lors de l'envoi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Toast.makeText(Vue_Ajout_Ami.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Erreur réseau : ", t);
            }
        });
    }
}
