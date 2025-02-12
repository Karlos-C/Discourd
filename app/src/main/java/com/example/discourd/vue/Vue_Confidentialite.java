package com.example.discourd.vue;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.content.pm.PackageManager;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.app.ActivityCompat;

import com.example.discourd.R;

public class Vue_Confidentialite extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.vue_confidentialite);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Récupérer le bouton par son ID
        Button buttonRetourParametres = findViewById(R.id.buttonRetourParametres);

        // Définir un OnClickListener
        buttonRetourParametres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Créer une intention pour changer de page
                Intent intent = new Intent(Vue_Confidentialite.this, Vue_Parametres.class);
                startActivity(intent); // Lancer l'activité
            }
        });

        // Boutons pour gérer la localisation
        Button buttonActiverLocalisation = findViewById(R.id.buttonActiverLocalisation);
        Button buttonDesactiverLocalisation = findViewById(R.id.buttonDesactiverLocalisation);

        buttonActiverLocalisation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Vérifier si la permission est refusée
                if (ActivityCompat.checkSelfPermission(Vue_Confidentialite.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Afficher un message pour expliquer la redirection
                    Toast.makeText(Vue_Confidentialite.this,
                            "Veuillez activer la localisation dans les paramètres pour utiliser cette fonctionnalité.",
                            Toast.LENGTH_LONG).show();
                    // Rediriger l'utilisateur vers les paramètres de l'application
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                } else {
                    // La permission est déjà accordée
                    Toast.makeText(Vue_Confidentialite.this, "La localisation est déjà activée", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonDesactiverLocalisation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Créer une boîte de dialogue de confirmation
                new AlertDialog.Builder(Vue_Confidentialite.this)
                        .setTitle("Confirmation")
                        .setMessage("Êtes-vous sûr de vouloir désactiver la localisation ?")
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Rediriger vers les paramètres de l'application
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                                Toast.makeText(Vue_Confidentialite.this,
                                        "Désactivez la localisation dans les paramètres.",
                                        Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("Non", null) // Fermer la pop-up si "Non"
                        .show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Vérifier si la permission est accordée
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission accordée !", Toast.LENGTH_SHORT).show();
                // Ici, vous pouvez ajouter le code pour accéder au GPS si nécessaire
            } else {
                Toast.makeText(this, "Permission refusée.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}