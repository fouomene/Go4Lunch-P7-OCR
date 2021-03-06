package com.delombaertdamien.go4lunch.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.delombaertdamien.go4lunch.BuildConfig;
import com.delombaertdamien.go4lunch.models.Users;
import com.delombaertdamien.go4lunch.models.notification.Data;
import com.delombaertdamien.go4lunch.models.notification.MyResponse;
import com.delombaertdamien.go4lunch.models.notification.Notification;
import com.delombaertdamien.go4lunch.service.MessageAPIService;

import org.jetbrains.annotations.NotNull;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Create By Damien De Lombaert
 * 2020
 */
public class MyAlarmReceiver extends BroadcastReceiver implements FirestoreCall.CallbackFirestoreUser {

    private static final String API_KEY_NOTIFY = BuildConfig.ApiKeyN;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "NOTIFY", Toast.LENGTH_LONG).show();
        FirestoreCall.getCurrentUser(this);
        Log.d("ALARM", "NOTIFIY");
    }
    public static void sendNotification (Users user){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://fcm.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        MessageAPIService mApiServiceMessaging = retrofit.create(MessageAPIService.class);

        Log.d("MainActivity", "Send notification");

        Log.d("MainActivity", "token :" + user.getToken());
        Data data = new Data("Hello");
        Notification notification = new Notification(user.getToken(), data);

        mApiServiceMessaging.sendNotification(notification).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(@NotNull Call<MyResponse> call, @NotNull Response<MyResponse> response) {
                Log.d("AlarmReceiver", response.message());
            }

            @Override
            public void onFailure(@NotNull Call<MyResponse> call, @NotNull Throwable t) {
                Log.e("AlarmReceiver", t.getMessage());
            }
        });
    }

    @Override
    public void onSuccessGetCurrentUser(Users user) {
        sendNotification(user);
    }
    @Override
    public void onFailureGetCurrentUser() {
        Log.e("AlarmManager", "Failed get current user");
    }
}
