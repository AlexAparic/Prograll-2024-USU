package com.example.redsocial.providers;

import com.example.redsocial.models.FCMBody;
import com.example.redsocial.models.FCMResponse;
import com.example.redsocial.retrofit.IFCMApi;
import com.example.redsocial.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {

    private String url = "https://fcm.googleapis.com";

    public NotificationProvider() {

    }

    public Call<FCMResponse> sendNotification(FCMBody body) {
        return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
    }

}
