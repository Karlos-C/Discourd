package com.example.discourd.vue;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.discourd.R;
import com.example.discourd.donnee.ApiService;
import com.example.discourd.donnee.RetrofitClient;
import com.example.discourd.modele.ApiResponse;
import com.example.discourd.modele.DernierMessage;
import com.example.discourd.modele.Message;
import com.example.discourd.modele.Utilisateur;
import com.example.discourd.util.PreferencesManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Vue_Discourd_Canal extends AppCompatActivity {

    private static final String TAG = "Vue_Discourd_Canal";
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList = new ArrayList<>();
    private EditText inputMessage;
    private ImageButton sendButton;

    private Utilisateur currentUser;
    private int targetUserId;
    private String targetUserPseudo;

    private String lastMessageDate = null;

    private Handler handler = new Handler();
    private Runnable fetchMessagesRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vue_discourd_canal);

        recyclerView = findViewById(R.id.recycler_view_messages);
        inputMessage = findViewById(R.id.input_message);
        sendButton = findViewById(R.id.send_button);

        // Récupérer l'utilisateur connecté
        PreferencesManager preferencesManager = new PreferencesManager(this);
        currentUser = preferencesManager.getUser();

        if (currentUser == null) {
            Toast.makeText(this, "Erreur : aucun utilisateur connecté", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Récupérer les données passées par Intent
        targetUserId = getIntent().getIntExtra("cibleId", -1);
        targetUserPseudo = getIntent().getStringExtra("ciblePseudo");

        if (targetUserId == -1) {
            Toast.makeText(this, "Erreur : utilisateur cible non défini", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Utilisateur actuel : " + currentUser.getId() + ", Pseudo : " + currentUser.getPseudo());
        Log.d(TAG, "Utilisateur cible : " + targetUserId);

        // Configurer le RecyclerView
        adapter = new MessageAdapter(this, messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Charger les messages initiaux
        fetchMessages();

        // Bouton Envoyer
        sendButton.setOnClickListener(v -> sendMessage());



        // Configurer le footer
        Vue_Footer.setupFooter(this);
    }

    private void fetchMessages() {
        Log.d(TAG, "fetchMessages() - Chargement des messages...");
        ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);

        HashMap<String, String> body = new HashMap<>();
        body.put("envoyeurId", String.valueOf(currentUser.getId()));
        body.put("destinataireId", String.valueOf(targetUserId));

        if (lastMessageDate != null) {
            body.put("lastMessageDate", lastMessageDate);
        }

        Call<ApiResponse> call = apiService.getMessages(body);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Log de la réponse brute pour vérifier le JSON
                    Log.d(TAG, "Réponse JSON brute : " + response.raw().toString());

                    ApiResponse apiResponse = response.body();
                    if ("success".equals(apiResponse.getStatus())) {
                        List<Message> newMessages = apiResponse.getMessages();
                        if (newMessages != null && !newMessages.isEmpty()) {
                            // Log des messages mappés
                            for (Message msg : newMessages) {
                                Log.d(TAG, "Message chargé : ID=" + msg.getIdMessage() +
                                        ", Envoyeur=" + msg.getEnvoyeurId() +
                                        ", Destinataire=" + msg.getDestinataireId() +
                                        ", Contenu=" + msg.getMessage());
                                if (!messageList.contains(msg)) { // Éviter les doublons
                                    messageList.add(msg);
                                }
                            }

                            adapter.notifyDataSetChanged();

                            // Mettre à jour la dernière date de message
                            lastMessageDate = newMessages.get(newMessages.size() - 1).getDateMessage();

                            // Scroll en bas de la conversation
                            recyclerView.smoothScrollToPosition(messageList.size() - 1);
                        } else {
                            Log.d(TAG, "fetchMessages() - Aucun message trouvé.");
                        }
                    } else {
                        Log.e(TAG, "fetchMessages() - Erreur API : " + apiResponse.getMessage());
                    }

                    // Démarrer les mises à jour après avoir chargé les messages (si ce n'est pas déjà fait)
                    if (fetchMessagesRunnable == null) {
                        setupLiveUpdates();
                    }

                } else {
                    Log.e(TAG, "fetchMessages() - Réponse API non réussie.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "fetchMessages() - Erreur réseau : " + t.getMessage(), t);
            }
        });
    }




    private void sendMessage() {
        String messageContent = inputMessage.getText().toString().trim();
        if (messageContent.isEmpty()) {
            Toast.makeText(this, "Le message ne peut pas être vide", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "sendMessage() - Envoi du message : " + messageContent);

        ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);

        HashMap<String, String> body = new HashMap<>();
        body.put("envoyeurId", String.valueOf(currentUser.getId()));
        body.put("destinataireId", String.valueOf(targetUserId));
        body.put("message", messageContent);

        Call<ApiResponse> call = apiService.envoyerMessage(body);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Log de la réponse brute
                    Log.d(TAG, "Réponse JSON brute après envoi : " + response.raw().toString());

                    ApiResponse apiResponse = response.body();
                    if ("success".equals(apiResponse.getStatus())) {
                        Message sentMessage = new Message(
                                apiResponse.getMessageId(),
                                messageContent,
                                apiResponse.getMessageDate(),
                                currentUser.getId(),
                                targetUserId,
                                currentUser.getPseudo(),
                                null
                        );

                        Log.d(TAG, "sendMessage() - Message envoyé : ID=" + sentMessage.getIdMessage());
                        messageList.add(sentMessage);
                        adapter.notifyDataSetChanged();

                        // Scroll en bas de la conversation
                        recyclerView.smoothScrollToPosition(messageList.size() - 1);

                        // Réinitialiser le champ d'entrée
                        inputMessage.setText("");
                    } else {
                        Log.e(TAG, "sendMessage() - Erreur API : " + apiResponse.getMessage());
                    }
                } else {
                    Log.e(TAG, "sendMessage() - Réponse API non réussie.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "sendMessage() - Erreur réseau : " + t.getMessage(), t);
            }
        });
    }




    /*
    private void updateLastMessageTable(Message sentMessage) {
        Log.d(TAG, "updateLastMessageTable() - Mise à jour de la table dernier_message_utilisateurs...");

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

        HashMap<String, String> body = new HashMap<>();
        body.put("envoyeurId", String.valueOf(sentMessage.getEnvoyeurId()));
        body.put("destinataireId", String.valueOf(sentMessage.getDestinataireId()));
        body.put("lastMessageId", String.valueOf(sentMessage.getIdMessage()));

        Call<ApiResponse> call = apiService.updateDernierMessage(body);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equals(response.body().getStatus())) {
                        Log.d(TAG, "updateLastMessageTable() - Mise à jour réussie.");
                    } else {
                        Log.e(TAG, "updateLastMessageTable() - Erreur : " + response.body().getMessage());
                    }
                } else {
                    Log.e(TAG, "updateLastMessageTable() - Réponse API non réussie.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "updateLastMessageTable() - Erreur réseau : " + t.getMessage(), t);
            }
        });
    }
    */

    private void setupLiveUpdates() {
        fetchMessagesRunnable = () -> {
            Log.d(TAG, "setupLiveUpdates() - Vérification des nouveaux messages...");
            checkForNewMessages();
            handler.postDelayed(fetchMessagesRunnable, 5000); // Appel toutes les 5 secondes
        };
        handler.post(fetchMessagesRunnable);
    }

    private void checkForNewMessages() {
        Log.d(TAG, "checkForNewMessages() - Vérification des nouveaux messages...");

        ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);

        HashMap<String, String> body = new HashMap<>();
        body.put("envoyeurId", String.valueOf(currentUser.getId()));
        body.put("destinataireId", String.valueOf(targetUserId));

        Log.d(TAG, "checkForNewMessages() - Paramètres envoyés : " + body.toString());

        Call<ApiResponse> call = apiService.getDernierMessage(body);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "checkForNewMessages() - Réponse brute : " + response.body().toString());

                    ApiResponse apiResponse = response.body();
                    if ("success".equals(apiResponse.getStatus())) {
                        DernierMessage dernierMessageData = apiResponse.getDernierMessageData();

                        if (dernierMessageData != null) {
                            Log.d(TAG, "checkForNewMessages() - Dernier message détecté : ID=" + dernierMessageData.getLastMessageId() + ", Contenu=" + dernierMessageData.getLastMessageContent());

                            // Créer un nouvel objet `Message` pour le RecyclerView
                            Message dernierMessage = new Message(
                                    dernierMessageData.getLastMessageId(),
                                    dernierMessageData.getLastMessageContent(),
                                    dernierMessageData.getLastMessageDate(),
                                    currentUser.getId(),  // Envoyeur ID actuel (pour simplification)
                                    targetUserId,        // Destinataire ID
                                    null,                // Pseudo de l'envoyeur (optionnel)
                                    null                 // Pseudo du destinataire (optionnel)
                            );

                            if (dernierMessage != null && !messageList.contains(dernierMessage)) {
                                // Forcer les messages chargés par refresh à être considérés comme "reçus"
                                dernierMessage.setEnvoyeurId(targetUserId);
                                dernierMessage.setDestinataireId(currentUser.getId());

                                // Ajouter le pseudo de l'envoyeur (l'utilisateur cible dans ce contexte)
                                dernierMessage.setEnvoyeurPseudo(targetUserPseudo);

                                messageList.add(dernierMessage);
                                adapter.notifyDataSetChanged();
                                recyclerView.smoothScrollToPosition(messageList.size() - 1);
                                Log.d(TAG, "checkForNewMessages() - Nouveau message ajouté comme message reçu.");
                            }

                            else {
                                Log.d(TAG, "checkForNewMessages() - Message déjà présent, pas d'ajout.");
                            }
                        } else {
                            Log.d(TAG, "checkForNewMessages() - Aucun nouveau message détecté.");
                        }
                    } else {
                        Log.e(TAG, "checkForNewMessages() - Erreur API : " + apiResponse.getMessage());
                    }
                } else {
                    Log.e(TAG, "checkForNewMessages() - Réponse non réussie : " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "checkForNewMessages() - Erreur réseau : " + t.getMessage(), t);
            }
        });
    }






    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(fetchMessagesRunnable); // Stopper les mises à jour
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(fetchMessagesRunnable); // Arrêter les mises à jour
        Log.d(TAG, "onPause() - Mises à jour arrêtées.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (messageList != null && !messageList.isEmpty()) {
            setupLiveUpdates(); // Redémarrer les mises à jour si nécessaire
        }
        Log.d(TAG, "onResume() - Mises à jour redémarrées.");
    }

}
