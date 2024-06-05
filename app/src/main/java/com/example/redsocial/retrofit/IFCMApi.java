package com.example.redsocial.retrofit;

import com.example.redsocial.models.FCMBody;
import com.example.redsocial.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=nJYMj6_YFxnkXj_ij8KyI3G4y2-HQ30cII-RaZmkUQw"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
