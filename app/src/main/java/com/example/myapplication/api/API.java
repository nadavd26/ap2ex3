package com.example.myapplication.api;

import android.content.Intent;
import android.content.SharedPreferences;

import com.example.myapplication.ContactList;
import com.example.myapplication.MainActivity;
import com.example.myapplication.adapters.ContactItemAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class API {
    private String baseUrl;
    private WebServiceAPI webServiceAPI;
    private Retrofit retrofit;

    private static API instance = null;
    private API() {
        SharedPreferences sharedPreferences = MainActivity.context.getSharedPreferences("MyPrefs", MainActivity.context.MODE_PRIVATE);
        baseUrl = sharedPreferences.getString("serverIP", "http://10.0.2.2:5000/api/");
        if(baseUrl.equals("")){
            SharedPreferences.Editor editor = MainActivity.context.getSharedPreferences("MyPrefs", MainActivity.context.MODE_PRIVATE).edit();
            editor.putString("serverIP", "http://10.0.2.2:5000/api/");
            editor.apply();
            baseUrl = "http://10.0.2.2:5000/api/";
        }
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public static API getInstance() {
        if (instance == null) {
            instance = new API();
        }

        return  instance;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        int a =1;
    }

    public void register(String username, String password, String displayName, String profilePic, Callback<Void> callback) {
        Call<Void> call = webServiceAPI.serverRegisterAccount(new AccountData(username, password, displayName, profilePic));
        call.enqueue(callback);
    }



    public void getToken(String username, String password, String fireBaseToken, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = webServiceAPI.serverGetToken(new TokenRequestData(username, password, fireBaseToken));
        call.enqueue(callback);
    }

    public void getContactList(String token, Callback<List<ContactServer>> callback) {
        token = "bearer " + token;
        Call<List<ContactServer>> call = webServiceAPI.serverGetContactList(token);
        call.enqueue(callback);
    }

    public void getUsernameInfo(String token, String username, Callback<User> callback) {
        token = "bearer " + token;
        Call<User> call = webServiceAPI.serverGetAccountInfo(username, token);
        call.enqueue(callback);
    }

    public void getMessages(String token, String id, Callback<List<MessageServer>> callback) {
        token = "bearer " + token;
        Call<List<MessageServer>> call = webServiceAPI.serverGetMessages(id, token);
        call.enqueue(callback);
    }

    public void sendMessage(String token, String id, String msg, Callback<MessageServer> callback) {
        token = "bearer " + token;
        Call<MessageServer> call = webServiceAPI.serverSendMessage(id, new MessageData(msg), token);
        call.enqueue(callback);
    }

    public void addChat(String token, String username, Callback<AddChat> callback) {
        token = "bearer " + token;
        Call<AddChat> call = webServiceAPI.serverAddChat(new ChatData(username), token);
        call.enqueue(callback);
    }
}
