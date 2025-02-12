package com.example.discourd.vue;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.discourd.R;
import com.example.discourd.donnee.ApiService;
import com.example.discourd.donnee.RetrofitClient;
import com.example.discourd.modele.ApiResponse;
import com.example.discourd.modele.Utilisateur;
import com.example.discourd.util.PreferencesManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Vue_Accueil extends AppCompatActivity {
    private static final String TAG = "Vue_Accueil";
    private List<Utilisateur> utilisateurs = new ArrayList<>();
    private UtilisateurAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vue_accueil);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_discussion);
        Button boutonNouveauMessage = findViewById(R.id.boutonNouveauMessage);

        // Configurer l'adaptateur pour le RecyclerView
        adapter = new UtilisateurAdapter(this, utilisateurs, utilisateur -> {
            Intent intent = new Intent(Vue_Accueil.this, Vue_Discourd_Canal.class);
            intent.putExtra("cibleId", utilisateur.getId());
            intent.putExtra("ciblePseudo", utilisateur.getPseudo());
            intent.putExtra("cibleImage", utilisateur.getImage());
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Gestion du clic sur le bouton Nouveau Message
        boutonNouveauMessage.setOnClickListener(v -> {
            Intent intent = new Intent(Vue_Accueil.this, Vue_Mes_Amis.class);
            startActivity(intent);
        });

        // Charger les conversations depuis l'API
        chargerConversations();

        // Configurer le footer
        Vue_Footer.setupFooter(this);

        // Configurer le swipe vers la gauche
        setupSwipeGesture(recyclerView);
    }
    private void setupSwipeGesture(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // Pas de déplacement des éléments
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Obtenir la position de l'élément swipé
                int position = viewHolder.getAdapterPosition();

                // Obtenir l'utilisateur correspondant
                Utilisateur utilisateur = utilisateurs.get(position);

                // Lancer l'activité Vue_Discourd_Canal
                Intent intent = new Intent(Vue_Accueil.this, Vue_Discourd_Canal.class);
                intent.putExtra("cibleId", utilisateur.getId());
                intent.putExtra("pseudo", utilisateur.getPseudo());
                intent.putExtra("image", utilisateur.getImage());
                startActivity(intent);

                // Réinitialiser la position de l'élément dans le RecyclerView
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // Ajouter un effet visuel pour le swipe
                Paint paint = new Paint();
                paint.setColor(ContextCompat.getColor(Vue_Accueil.this, R.color.SwipeGris)); // Couleur de fond
                RectF background = new RectF((float) viewHolder.itemView.getRight() + dX,
                        (float) viewHolder.itemView.getTop(),
                        (float) viewHolder.itemView.getRight(),
                        (float) viewHolder.itemView.getBottom());
                c.drawRect(background, paint);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        // Attacher l'ItemTouchHelper au RecyclerView
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
    }
    private void chargerConversations() {
        Log.d(TAG, "Chargement des conversations...");

        PreferencesManager preferencesManager = new PreferencesManager(this);
        Utilisateur utilisateurConnecte = preferencesManager.getUser();

        if (utilisateurConnecte == null) {
            Log.e(TAG, "Utilisateur non connecté !");
            Toast.makeText(this, "Erreur : Aucun utilisateur connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = utilisateurConnecte.getId();
        Log.d(TAG, "Chargement des conversations pour l'utilisateur ID : " + userId);

        ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);

        HashMap<String, Integer> body = new HashMap<>();
        body.put("userId", userId);

        Call<ApiResponse> call = apiService.getConversations(body);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Réponse API reçue : " + response.body().toString());

                    if ("success".equals(response.body().getStatus())) {
                        List<Utilisateur> listeUtilisateurs = response.body().getUtilisateurs();
                        if (listeUtilisateurs != null && !listeUtilisateurs.isEmpty()) {
                            utilisateurs.clear();
                            utilisateurs.addAll(listeUtilisateurs);
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "Liste des utilisateurs mise à jour !");
                        } else {
                            Log.w(TAG, "Aucun utilisateur trouvé.");
                            Toast.makeText(Vue_Accueil.this, "Aucun utilisateur trouvé.", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                    }
                } else {
                    try {
                        Log.e(TAG, "Erreur de bite : " + (response.errorBody() != null ? response.errorBody().string() : "null"));
                    } catch (Exception e) {
                        Log.e(TAG, "Erreur lors de la lecture de la réponse d'erreur", e);
                    }
                    Toast.makeText(Vue_Accueil.this, "Erreur de connard", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Erreur réseau : " + t.getMessage(), t);
                Toast.makeText(Vue_Accueil.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
