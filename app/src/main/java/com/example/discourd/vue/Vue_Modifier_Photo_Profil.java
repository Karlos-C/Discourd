package com.example.discourd.vue;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.discourd.R;
import com.example.discourd.donnee.ApiService;
import com.example.discourd.donnee.RetrofitClient;
import com.example.discourd.modele.ApiResponse;
import com.example.discourd.modele.Utilisateur;
import com.example.discourd.util.ImageLoader;
import com.example.discourd.util.PreferencesManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Vue_Modifier_Photo_Profil extends AppCompatActivity {
    private static final String TAG = "Vue_Modifier_Photo_Profil";
    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private ImageView imageActuelle;
    private ImageView imagePreview;
    private Button ouvrirGalerie;
    private Button confirmerChangement;
    private PreferencesManager preferencesManager;
    private Uri selectedImageUri;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vue_modifier_photo_profil);

        preferencesManager = new PreferencesManager(this);

        // Références des vues
        imageActuelle = findViewById(R.id.imageActuelle);
        imagePreview = findViewById(R.id.imagePreview);
        ouvrirGalerie = findViewById(R.id.ouvrirGalerie);
        confirmerChangement = findViewById(R.id.confirmerChangement);

        // Charger l'image actuelle
        chargerImageActuelle();

        // Options pour la sélection ou capture d'image
        ouvrirGalerie.setOnClickListener(v -> ouvrirOptionsPhoto());

        // Configuration du footer
        Vue_Footer.setupFooter(this);

        // Confirmer les changements
        confirmerChangement.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                envoyerImageAuServeur(selectedImageUri);
            } else {
                Toast.makeText(this, "Veuillez sélectionner une image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void chargerImageActuelle() {
        Utilisateur utilisateur = preferencesManager.getUser();
        String imageProfilName = utilisateur.getImage();
        ImageLoader.chargerImageProfil(this, imageProfilName, imageActuelle);
    }

    private void ouvrirOptionsPhoto() {
        CharSequence[] options = {"Prendre une photo", "Choisir depuis la galerie"};
        new AlertDialog.Builder(this)
                .setTitle("Sélectionner une option")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        ouvrirAppareilPhoto();
                    } else if (which == 1) {
                        ouvrirGalerie();
                    }
                })
                .show();
    }

    private void ouvrirGalerie() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void ouvrirAppareilPhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile;
            try {
                photoFile = creerFichierImage();
                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(this, "com.example.discourd.fileprovider", photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
            } catch (IOException e) {
                Log.e(TAG, "Erreur lors de la création du fichier photo : ", e);
                Toast.makeText(this, "Erreur lors de l'ouverture de l'appareil photo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File creerFichierImage() throws IOException {
        String nomFichier = "photo_profil";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(nomFichier, ".jpg", storageDir);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                selectedImageUri = data.getData();
                afficherImage(selectedImageUri);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                if (photoUri != null) {
                    selectedImageUri = photoUri;
                    afficherImage(photoUri);
                }
            }
        }
    }

    private void afficherImage(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            imagePreview.setImageBitmap(bitmap);
        } catch (IOException e) {
            Log.e(TAG, "Erreur lors de la récupération de l'image : ", e);
            Toast.makeText(this, "Erreur lors de l'affichage de l'image", Toast.LENGTH_SHORT).show();
        }
    }

    private void envoyerImageAuServeur(Uri imageUri) {
        try {
            File file = new File(getCacheDir(), "photo_profil.jpg");
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            try (FileOutputStream out = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            RequestBody pseudo = RequestBody.create(MediaType.parse("text/plain"), preferencesManager.getUser().getPseudo());

            ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
            Call<ApiResponse> call = apiService.modifierPhotoProfil(body, pseudo);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                        Toast.makeText(Vue_Modifier_Photo_Profil.this, "Photo modifiée avec succès", Toast.LENGTH_SHORT).show();
                        preferencesManager.updateUserImage(response.body().getMessage());
                        Glide.get(Vue_Modifier_Photo_Profil.this).clearMemory();
                        new Thread(() -> Glide.get(Vue_Modifier_Photo_Profil.this).clearDiskCache()).start();
                        finish();
                    } else {
                        Toast.makeText(Vue_Modifier_Photo_Profil.this, "Erreur lors de l'upload", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.e(TAG, "Erreur réseau : ", t);
                    Toast.makeText(Vue_Modifier_Photo_Profil.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "Erreur lors de la création du fichier temporaire : ", e);
            Toast.makeText(this, "Erreur interne", Toast.LENGTH_SHORT).show();
        }
    }
}
