package com.example.myapplication.api;

import com.example.myapplication.api.MessageData;
import com.example.myapplication.api.TokenRequestData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import okhttp3.ResponseBody;

public interface WebServiceAPI {
    @POST("Tokens")
    Call<ResponseBody> serverGetToken(@Body TokenRequestData requestData);

    @GET("Chats")
    @Headers("Content-Type: application/json")
    Call<List<ContactServer>> serverGetContactList(@Header("Authorization") String token);

    @GET("Chats/{id}/Messages")
    @Headers("Content-Type: application/json")
    Call<List<MessageServer>> serverGetMessages(@Path("id") String id, @Header("Authorization") String token);

    @POST("Chats/{id}/Messages")
    @Headers("Content-Type: application/json")
    Call<MessageServer> serverSendMessage(@Path("id") String id, @Body MessageData messageData, @Header("Authorization") String token);


    @POST("Chats")
    @Headers("Content-Type: application/json")
    Call<AddChat> serverAddChat(@Body ChatData chatData, @Header("Authorization") String token);

    @POST("Users")
    Call<Void> serverRegisterAccount(@Body AccountData accountData);

    @GET("Users/{username}")
    @Headers("Content-Type: application/json")
    Call<User> serverGetAccountInfo(@Path("username") String username, @Header("Authorization") String token);

}
