package com.example.discourd.vue;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Vue_Mes_Amis extends AppCompatActivity {
    private static final String TAG = "Vue_Mes_Amis";

    private RecyclerView recyclerView;
    private UtilisateurAdapter adapter;
    private List<Utilisateur> amis = new ArrayList<>();
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vue_mes_amis);

        preferencesManager = new PreferencesManager(this);
        Utilisateur utilisateurConnecte = preferencesManager.getUser();

        if (utilisateurConnecte == null) {
            Toast.makeText(this, "Utilisateur non connecté !", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Utilisateur connecté : " + utilisateurConnecte.getPseudo() + ", ID : " + utilisateurConnecte.getId());

        recyclerView = findViewById(R.id.recycler_view_mes_amis);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UtilisateurAdapter(this, amis, utilisateur -> {
            Log.d(TAG, "Ami sélectionné : Pseudo = " + utilisateur.getPseudo() + ", ID = " + utilisateur.getId());
            Intent intent = new Intent(Vue_Mes_Amis.this, Vue_Discourd_Canal.class);
            intent.putExtra("cibleId", utilisateur.getId());
            intent.putExtra("pseudo", utilisateur.getPseudo());
            intent.putExtra("image", utilisateur.getImage());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        // Bouton Ajouter un ami
        Button boutonAjouterAmi = findViewById(R.id.bouton_ajouter_ami);
        boutonAjouterAmi.setOnClickListener(v -> {
            Log.d(TAG, "Redirection vers Vue_Ajout_Ami");
            Intent intent = new Intent(Vue_Mes_Amis.this, Vue_Ajout_Ami.class);
            startActivity(intent);
        });

        // Bouton Demandes d'amis
        Button boutonDemandesAmis = findViewById(R.id.bouton_demandes_amis);
        boutonDemandesAmis.setOnClickListener(v -> {
            Log.d(TAG, "Redirection vers Vue_Demandes_Amis");
            Intent intent = new Intent(Vue_Mes_Amis.this, Vue_Demandes_Amis.class);
            startActivity(intent);
        });

        // Charger les amis de l'utilisateur connecté
        chargerAmis(utilisateurConnecte.getId());

        // Configurer le footer
        Vue_Footer.setupFooter(this);
    }

    private void chargerAmis(int userId) {
        Log.d(TAG, "Chargement des amis pour l'utilisateur ID : " + userId);

        ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
        HashMap<String, Integer> body = new HashMap<>();
        body.put("userId", userId);

        Call<ApiResponse> call = apiService.getMesAmis(body);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Réponse API reçue : " + response.body().toString());
                    if ("success".equals(response.body().getStatus())) {
                        List<Utilisateur> listeAmis = response.body().getUtilisateurs();
                        amis.clear();
                        if (listeAmis != null) {
                            amis.addAll(listeAmis);
                            for (Utilisateur ami : listeAmis) {
                                Log.d(TAG, "Ami chargé : Pseudo = " + ami.getPseudo() + ", ID = " + ami.getId());
                            }
                        } else {
                            Log.w(TAG, "Aucun ami trouvé.");
                            Toast.makeText(Vue_Mes_Amis.this, "Aucun ami trouvé.", Toast.LENGTH_SHORT).show();
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Erreur API : " + response.body().getMessage());
                        Toast.makeText(Vue_Mes_Amis.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e(TAG, "Erreur de serveur : " + errorBody);
                    } catch (IOException e) {
                        Log.e(TAG, "Erreur lors de la lecture de la réponse d'erreur", e);
                    }
                    Toast.makeText(Vue_Mes_Amis.this, "Erreur lors du chargement des amis", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Erreur réseau : " + t.getMessage(), t);
                Toast.makeText(Vue_Mes_Amis.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
