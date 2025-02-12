package com.example.discourd.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.discourd.R;

public class ImageLoader {

    /**
     * Méthode statique pour charger une image de profil.
     *
     * @param context    Le contexte de l'application ou de l'activité.
     * @param imagePath  Le chemin partiel de l'image (ex : "pseudo.jpg").
     * @param imageView  La vue ImageView où l'image doit être affichée.
     */
    public static void chargerImageProfil(Context context, String imagePath, ImageView imageView) {
        if (imagePath != null && !imagePath.isEmpty()) {
            // URL complète avec un paramètre de temps pour contourner le cache
            String imageUrl = "https://gauthierpl.fr/DiscourdScriptsPHP/DiscourdPhotosProfilUtilisateurs/"
                    + imagePath + "?t=" + System.currentTimeMillis();

            Glide.with(context)
                    .load(imageUrl) // URL complète
                    .placeholder(R.drawable.icone_profil) // Image par défaut pendant le chargement
                    .error(R.drawable.icone_profil) // Image par défaut si une erreur survient
                    .skipMemoryCache(true) // Désactiver le cache en mémoire
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Désactiver le cache disque
                    .into(imageView);
        } else {
            // Si aucun chemin d'image n'est fourni, affiche l'image par défaut
            imageView.setImageResource(R.drawable.icone_profil);
        }
    }


}
