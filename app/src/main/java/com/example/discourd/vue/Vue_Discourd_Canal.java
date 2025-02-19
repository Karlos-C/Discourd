package com.example.discourd.vue;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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
    private TextView typingIndicator;

    private Utilisateur currentUser;
    private int targetUserId;
    private String targetUserPseudo;

    private String lastMessageDate = null;

    private Handler handler = new Handler();
    private Runnable fetchMessagesRunnable;

    // Indicateur de saisie
    private boolean isTyping = false;
    private Handler typingHandler = new Handler();
    private Runnable stopTypingRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vue_discourd_canal);

        recyclerView = findViewById(R.id.recycler_view_messages);
        inputMessage = findViewById(R.id.input_message);
        sendButton = findViewById(R.id.send_button);
        typingIndicator = findViewById(R.id.typing_indicator); // Ajout de l’indicateur

        PreferencesManager preferencesManager = new PreferencesManager(this);
        currentUser = preferencesManager.getUser();

        if (currentUser == null) {
            Toast.makeText(this, "Erreur : aucun utilisateur connecté", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        targetUserId = getIntent().getIntExtra("cibleId", -1);
        targetUserPseudo = getIntent().getStringExtra("ciblePseudo");

        if (targetUserId == -1) {
            Toast.makeText(this, "Erreur : utilisateur cible non défini", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Utilisateur actuel : " + currentUser.getId() + ", Pseudo : " + currentUser.getPseudo());
        Log.d(TAG, "Utilisateur cible : " + targetUserId);

        adapter = new MessageAdapter(this, messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fetchMessages();

        sendButton.setOnClickListener(v -> sendMessage());

        // ➕ Détection de frappe
        inputMessage.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isTyping) {
                    isTyping = true;
                    sendTypingStatus(true);
                }
                typingHandler.removeCallbacks(stopTypingRunnable);
                typingHandler.postDelayed(stopTypingRunnable, 2000); // Stop après 2s d'inactivité
            }
        });

        stopTypingRunnable = () -> {
            isTyping = false;
            sendTypingStatus(false);
        };

        Vue_Footer.setupFooter(this);
    }

    private void sendTypingStatus(boolean typing) {
        // 👇 Ici tu peux aussi envoyer l'info à ton backend
        runOnUiThread(() -> {
            typingIndicator.setVisibility(typing ? View.VISIBLE : View.GONE);
        });
    }

    private void fetchMessages() {
        ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
        HashMap<String, String> body = new HashMap<>();
        body.put("envoyeurId", String.valueOf(currentUser.getId()));
        body.put("destinataireId", String.valueOf(targetUserId));
        if (lastMessageDate != null) body.put("lastMessageDate", lastMessageDate);

        Call<ApiResponse> call = apiService.getMessages(body);
        call.enqueue(new Callback<ApiResponse>() {
            @Override public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if ("success".equals(apiResponse.getStatus())) {
                        List<Message> newMessages = apiResponse.getMessages();
                        if (newMessages != null && !newMessages.isEmpty()) {
                            for (Message msg : newMessages) {
                                if (!messageList.contains(msg)) messageList.add(msg);
                            }
                            adapter.notifyDataSetChanged();
                            lastMessageDate = newMessages.get(newMessages.size() - 1).getDateMessage();
                            recyclerView.smoothScrollToPosition(messageList.size() - 1);
                        }
                    }
                    if (fetchMessagesRunnable == null) setupLiveUpdates();
                }
            }

            @Override public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
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

        ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
        HashMap<String, String> body = new HashMap<>();
        body.put("envoyeurId", String.valueOf(currentUser.getId()));
        body.put("destinataireId", String.valueOf(targetUserId));
        body.put("message", messageContent);

        Call<ApiResponse> call = apiService.envoyerMessage(body);
        call.enqueue(new Callback<ApiResponse>() {
            @Override public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
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
                        messageList.add(sentMessage);
                        adapter.notifyDataSetChanged();
                        recyclerView.smoothScrollToPosition(messageList.size() - 1);
                        inputMessage.setText("");
                    }
                }
            }

            @Override public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "sendMessage() - Erreur réseau : " + t.getMessage(), t);
            }
        });
    }

    private void setupLiveUpdates() {
        fetchMessagesRunnable = () -> {
            checkForNewMessages();
            handler.postDelayed(fetchMessagesRunnable, 5000);
        };
        handler.post(fetchMessagesRunnable);
    }

    private void checkForNewMessages() {
        ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
        HashMap<String, String> body = new HashMap<>();
        body.put("envoyeurId", String.valueOf(currentUser.getId()));
        body.put("destinataireId", String.valueOf(targetUserId));

        Call<ApiResponse> call = apiService.getDernierMessage(body);
        call.enqueue(new Callback<ApiResponse>() {
            @Override public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if ("success".equals(apiResponse.getStatus())) {
                        DernierMessage dernierMessageData = apiResponse.getDernierMessageData();
                        if (dernierMessageData != null) {
                            Message dernierMessage = new Message(
                                    dernierMessageData.getLastMessageId(),
                                    dernierMessageData.getLastMessageContent(),
                                    dernierMessageData.getLastMessageDate(),
                                    currentUser.getId(),
                                    targetUserId,
                                    null,
                                    null
                            );
                            if (!messageList.contains(dernierMessage)) {
                                dernierMessage.setEnvoyeurId(targetUserId);
                                dernierMessage.setDestinataireId(currentUser.getId());
                                dernierMessage.setEnvoyeurPseudo(targetUserPseudo);
                                messageList.add(dernierMessage);
                                adapter.notifyDataSetChanged();
                                recyclerView.smoothScrollToPosition(messageList.size() - 1);
                            }
                        }
                    }
                }
            }

            @Override public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "checkForNewMessages() - Erreur réseau : " + t.getMessage(), t);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(fetchMessagesRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(fetchMessagesRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (messageList != null && !messageList.isEmpty()) {
            setupLiveUpdates();
        }
    }
}