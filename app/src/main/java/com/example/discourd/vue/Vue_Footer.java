package com.example.discourd.vue;

import android.app.Activity;
import android.content.Intent;
import android.widget.LinearLayout;

import com.example.discourd.R;

public class Vue_Footer {

    // Méthode statique pour configurer les actions des boutons du footer
    public static void setupFooter(Activity activity) {
        LinearLayout footerButtonProfile = activity.findViewById(R.id.footer_button_profile);
        LinearLayout footerButtonHome = activity.findViewById(R.id.footer_button_home);
        LinearLayout footerButtonMinimap = activity.findViewById(R.id.footer_button_minimap);

        if (footerButtonProfile != null) {
            footerButtonProfile.setOnClickListener(v -> {
                Intent intent = new Intent(activity, Vue_Profil.class);
                activity.startActivity(intent);
            });
        }

        if (footerButtonHome != null) {
            footerButtonHome.setOnClickListener(v -> {
                Intent intent = new Intent(activity, Vue_Accueil.class);
                activity.startActivity(intent);
            });
        }

        if (footerButtonMinimap != null) {
            footerButtonMinimap.setOnClickListener(v -> {
                Intent intent = new Intent(activity, Vue_Map.class);
                activity.startActivity(intent);
            });
        }
    }
}
