package com.example.discourd.vue;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.discourd.R;
import com.example.discourd.donnee.ApiService;
import com.example.discourd.donnee.RetrofitClient;
import com.example.discourd.modele.ApiResponse;
import com.example.discourd.modele.Utilisateur;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DemandesAmisAdapter extends RecyclerView.Adapter<DemandesAmisAdapter.ViewHolder> {

    private static final String TAG = "DemandesAmisAdapter";
    private final Context context;
    private final List<Utilisateur> demandes;
    private final int userId;

    public DemandesAmisAdapter(Context context, List<Utilisateur> demandes, int userId) {
        this.context = context;
        this.demandes = demandes;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_demande_ami, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Utilisateur demande = demandes.get(position);

        holder.pseudo.setText(demande.getPseudo());

        // Vérifier si l'image est null
        if (demande.getImage() != null) {
            int imageResource = context.getResources().getIdentifier(
                    demande.getImage(), "drawable", context.getPackageName()
            );
            holder.avatar.setImageResource(imageResource != 0 ? imageResource : R.drawable.icone_profil);
        } else {
            // Utiliser une image par défaut si aucune image n'est spécifiée
            holder.avatar.setImageResource(R.drawable.icone_profil);
        }

        // Gérer le clic sur Accepter
        holder.boutonAccepter.setOnClickListener(v -> modifierDemande(demande.getId(), "accepte", position));

        // Gérer le clic sur Refuser
        holder.boutonRefuser.setOnClickListener(v -> modifierDemande(demande.getId(), "rejete", position));
    }


    private void modifierDemande(int demandeId, String action, int position) {

        ApiService apiService = RetrofitClient.getInstance(context).create(ApiService.class);

        HashMap<String, Object> body = new HashMap<>();
        body.put("demandeId", demandeId);
        body.put("action", action); // Changez "statut" en "action"

        apiService.modifierDemandeAmi(body).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equals(response.body().getStatus())) {
                        Toast.makeText(context, action.equals("accepte") ? "Demande acceptée" : "Demande refusée", Toast.LENGTH_SHORT).show();
                        demandes.remove(position);
                        notifyItemRemoved(position);
                    } else {
                        Log.e("DemandesAmisAdapter", "Erreur API : " + response.body().getMessage());
                        Toast.makeText(context, "Erreur : " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e("DemandesAmisAdapter", "Erreur lors de l'opération : " + errorBody);
                        Toast.makeText(context, "Erreur lors de l'opération", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("DemandesAmisAdapter", "Erreur lors de la lecture de la réponse", e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Log.e("DemandesAmisAdapter", "Erreur réseau : " + t.getMessage(), t);
                Toast.makeText(context, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return demandes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView pseudo;
        Button boutonAccepter, boutonRefuser;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar_demande_ami);
            pseudo = itemView.findViewById(R.id.pseudo_demande_ami);
            boutonAccepter = itemView.findViewById(R.id.bouton_accepter_demande);
            boutonRefuser = itemView.findViewById(R.id.bouton_refuser_demande);
        }
    }
}
