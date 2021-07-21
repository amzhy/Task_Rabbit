package com.example.myapplication;

import retrofit2.http.Headers;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.Call;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAjJw0PzM:APA91bHCmcii0tOgQgYaMvM--s_5ryimrK9PYBoNw56UbpiRMV58FRi45NkmX1N3zCwUSVHWy11uWpNbCsTZ7YAdDyFvJ3mZawzysA6nl-qMu_OJHxPmlc9px04-wmACEF9ZHBNSMD0G"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body NotificationSender body);

}
