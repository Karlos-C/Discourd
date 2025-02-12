package com.example.discourd.donnee;

import android.content.Context;
import android.util.Log;

import com.example.discourd.modele.Utilisateur;
import com.example.discourd.util.PreferencesManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://gauthierpl.fr/DiscourdScriptsPHP/";
    private static Retrofit retrofit;

    // Méthode pour récupérer l'instance Retrofit
    public static Retrofit getInstance(Context context) {
        PreferencesManager preferencesManager = new PreferencesManager(context);
        Utilisateur utilisateur = preferencesManager.getUser();

        if (utilisateur == null || utilisateur.getToken() == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        if (retrofit == null) {
            String token = utilisateur.getToken();
            String userId = String.valueOf(utilisateur.getId());

            Log.d("RetrofitClient", "Création Retrofit : Token=" + token + ", UserId=" + userId);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(token, userId))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

}
