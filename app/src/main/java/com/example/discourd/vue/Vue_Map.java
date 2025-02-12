package com.example.discourd.vue;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;
import org.osmdroid.util.GeoPoint;

import com.example.discourd.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import org.osmdroid.views.overlay.Marker; // Import correct de Marker

public class Vue_Map extends AppCompatActivity {

    private MapView map;
    private FusedLocationProviderClient fusedLocationClient; // Déclaration du FusedLocationProviderClient

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        EdgeToEdge.enable(this);
        setContentView(R.layout.vue_map);

        // Appliquer des paramètres d'insets pour la gestion de l'écran
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialiser la MapView
        map = findViewById(R.id.map); // Assurez-vous que l'ID dans le layout est correct
        map.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        // Centrer la carte sur une position spécifique
        GeoPoint startPoint = new GeoPoint(48.8588443, 2.2943506); // Coordonnée de la Tour Eiffel
        map.getController().setZoom(15.0);
        map.getController().setCenter(startPoint);

        // Initialiser le FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Vérifier les permissions de localisation
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Demander les permissions si nécessaire
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // Si les permissions sont accordées, récupérer la localisation
            getCurrentLocation();
        }

        // Configurer le footer
        Vue_Footer.setupFooter(this);
    }

    private void getCurrentLocation() {
        // Vérifier de nouveau les permissions avant d'essayer de récupérer la localisation
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                // Si une localisation est trouvée, mettre à jour la carte
                                GeoPoint userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                                map.getController().setCenter(userLocation);
                                map.getController().setZoom(15); // Zoom sur la position

                                // Ajouter un marqueur à la position actuelle
                                Marker marker = new Marker(map);
                                marker.setPosition(userLocation);
                                marker.setTitle("Vous êtes ici");
                                map.getOverlays().add(marker);
                            }
                        }
                    });
        } else {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (map != null) {
            map.onPause(); // Gérer la pause
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (map != null) {
            map.onResume(); // Gérer la reprise
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si la permission est accordée, récupérer la localisation
                getCurrentLocation();
            } else {
                // Si la permission est refusée, afficher un message à l'utilisateur
                // Vous pouvez demander la permission à nouveau ou afficher un message
            }
        }
    }


}