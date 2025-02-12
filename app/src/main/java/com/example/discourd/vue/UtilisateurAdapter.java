package com.example.discourd.vue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.discourd.R;
import com.example.discourd.modele.Utilisateur;
import com.example.discourd.util.ImageLoader;

import java.util.List;

public class UtilisateurAdapter extends RecyclerView.Adapter<UtilisateurAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Utilisateur utilisateur);
    }

    private final Context context;
    private final List<Utilisateur> utilisateurs;
    private final OnItemClickListener listener;

    public UtilisateurAdapter(Context context, List<Utilisateur> utilisateurs, OnItemClickListener listener) {
        this.context = context;
        this.utilisateurs = utilisateurs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_utilisateur, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Utilisateur utilisateur = utilisateurs.get(position);

        // Définir les données
        holder.pseudo.setText(utilisateur.getPseudo());

        // Charger l'image de profil avec la méthode utilitaire
        String imageProfilName = utilisateur.getImage(); // Nom du fichier d'image
        ImageLoader.chargerImageProfil(context, imageProfilName, holder.avatar);

        // Gérer le clic
        holder.itemView.setOnClickListener(v -> listener.onItemClick(utilisateur));
    }


    @Override
    public int getItemCount() {
        return utilisateurs.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView pseudo;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatarUtilisateur);
            pseudo = itemView.findViewById(R.id.pseudoUtilisateur);
        }
    }
}
