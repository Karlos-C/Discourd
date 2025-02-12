package com.example.discourd.vue;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.discourd.R;
import com.example.discourd.donnee.ApiService;
import com.example.discourd.donnee.RetrofitClient;
import com.example.discourd.modele.ApiResponse;
import com.example.discourd.modele.Utilisateur;
import com.example.discourd.util.PreferencesManager;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Vue_Demandes_Amis extends AppCompatActivity {

    private static final String TAG = "Vue_Demandes_Amis";
    private RecyclerView recyclerView;
    private DemandesAmisAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vue_demandes_amis);

        recyclerView = findViewById(R.id.recycler_demandes_amis);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        PreferencesManager preferencesManager = new PreferencesManager(this);
        Utilisateur utilisateurConnecte = preferencesManager.getUser();

        if (utilisateurConnecte == null) {
            Toast.makeText(this, "Erreur : utilisateur non connecté", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Utilisateur connecté : " + utilisateurConnecte.getPseudo() + ", ID : " + utilisateurConnecte.getId());

        chargerDemandesAmis(utilisateurConnecte.getId());

        // Configuration du footer
        Vue_Footer.setupFooter(this);
    }

    private void chargerDemandesAmis(int userId) {
        ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);

        HashMap<String, Integer> body = new HashMap<>();
        body.put("userId", userId);

        Call<ApiResponse> call = apiService.getDemandesAmis(body);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String rawJson = new Gson().toJson(response.body());
                        Log.d(TAG, "Réponse brute API : " + rawJson);

                        List<Utilisateur> demandes = response.body().getDemandes(); // Utilisation de la clé `demandes`
                        if (demandes != null && !demandes.isEmpty()) {
                            adapter = new DemandesAmisAdapter(Vue_Demandes_Amis.this, demandes, userId);
                            recyclerView.setAdapter(adapter);
                            Log.d(TAG, "Demandes d'amis chargées : " + demandes.size());
                        } else {
                            Log.w(TAG, "Aucune demande reçue.");
                            Toast.makeText(Vue_Demandes_Amis.this, "Aucune demande reçue", Toast.LENGTH_SHORT).show();
                            recyclerView.setAdapter(null); // Gestion d'une liste vide
                        }
                    } else {
                        Log.e(TAG, "Erreur lors du chargement des demandes : " + response.errorBody());
                        Toast.makeText(Vue_Demandes_Amis.this, "Erreur lors du chargement", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Erreur lors du traitement de la réponse API", e);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Toast.makeText(Vue_Demandes_Amis.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Erreur réseau : ", t);
            }
        });
    }

}
