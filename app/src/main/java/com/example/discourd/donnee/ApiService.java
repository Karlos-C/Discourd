package com.example.discourd.donnee;

import com.example.discourd.modele.ApiResponse;
import com.example.discourd.modele.Utilisateur;
import com.example.discourd.modele.ConnexionUtilisateur;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @POST("inscription.php")
    Call<ApiResponse> inscrireUtilisateur(@Body Utilisateur utilisateur);

    @POST("connexion_utilisateur.php")
    Call<ApiResponse> connecterUtilisateur(@Body ConnexionUtilisateur connexionUtilisateur);



    @POST("get_conversations.php")
    Call<ApiResponse> getConversations(@Body HashMap<String, Integer> userId);

    @POST("get_messages.php")
    Call<ApiResponse> getMessages(@Body HashMap<String, String> body); // Modifié pour inclure lastMessageDate

    @POST("envoi_message.php")
    Call<ApiResponse> envoyerMessage(@Body HashMap<String, String> body);

    //@POST("update_dernier_message_utilisateurs.php") // Nouvelle méthode pour mettre à jour dernier_message_utilisateurs
    //Call<ApiResponse> updateDernierMessage(@Body HashMap<String, String> body);

    @POST("get_dernier_message_utilisateurs.php")
    Call<ApiResponse> getDernierMessage(@Body HashMap<String, String> body);


    @POST("modifier_profil.php")
    Call<ApiResponse> modifierProfil(@Body HashMap<String, String> body);

    @POST("envoyer_demande_ami.php")
    Call<ApiResponse> envoyerDemandeAmi(@Body HashMap<String, String> body);

    @POST("get_demandes_amis.php")
    Call<ApiResponse> getDemandesAmis(@Body HashMap<String, Integer> body);

    @POST("modifier_demande_ami.php")
    Call<ApiResponse> modifierDemandeAmi(@Body HashMap<String, Object> body);

    @POST("get_mes_amis.php")
    Call<ApiResponse> getMesAmis(@Body HashMap<String, Integer> body);

    @Multipart
    @POST("modifier_photo_profil.php")
    Call<ApiResponse> modifierPhotoProfil(
            @Part MultipartBody.Part file,
            @Part("pseudo") RequestBody pseudo
    );

}
