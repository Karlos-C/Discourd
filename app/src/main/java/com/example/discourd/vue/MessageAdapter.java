package com.example.discourd.vue;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.discourd.R;
import com.example.discourd.modele.Message;
import com.example.discourd.util.ImageLoader;
import com.example.discourd.util.PreferencesManager;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1; // Type de vue pour les messages envoyés
    private static final int VIEW_TYPE_RECEIVED = 2; // Type de vue pour les messages reçus

    private Context context;
    private List<Message> messageList;
    private int currentUserId;

    public MessageAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;

        // Récupérer l'utilisateur actuel via PreferencesManager
        PreferencesManager preferencesManager = new PreferencesManager(context);
        this.currentUserId = preferencesManager.getUser().getId();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        int envoyeurId = message.getEnvoyeurId();

        // Déterminer si le message est envoyé ou reçu
        return (envoyeurId == currentUserId) ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            // Layout pour les messages envoyés
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_envoye, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            // Layout pour les messages reçus
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_recu, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (holder.getItemViewType() == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).bind(message);
        } else {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // ViewHolder pour les messages envoyés
    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageContent, messageDate;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageContent = itemView.findViewById(R.id.text_message_content);
            messageDate = itemView.findViewById(R.id.text_message_date);
        }

        public void bind(Message message) {
            messageContent.setText(message.getMessage());
            messageDate.setText(message.getDateMessage());
        }
    }

    // ViewHolder pour les messages reçus
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageContent, messageDate, senderPseudo;
        ImageView avatar; // ImageView pour la photo de profil

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatarMessageRecu); // ImageView pour la photo de profil
            senderPseudo = itemView.findViewById(R.id.text_message_sender);
            messageContent = itemView.findViewById(R.id.text_message_content);
            messageDate = itemView.findViewById(R.id.text_message_date);
        }

        public void bind(Message message) {
            // Afficher les données textuelles
            messageContent.setText(message.getMessage());
            messageDate.setText(message.getDateMessage());
            senderPseudo.setText(message.getEnvoyeurPseudo());

            // Charger dynamiquement la photo de profil avec ImageLoader
            String imageProfilName = message.getEnvoyeurPseudo() + ".jpg"; // Exemple : pseudo.jpg
            ImageLoader.chargerImageProfil(itemView.getContext(), imageProfilName, avatar);
        }
    }
}
