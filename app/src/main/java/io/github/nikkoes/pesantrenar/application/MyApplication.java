package io.github.nikkoes.pesantrenar.application;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.enableLogging(com.androidnetworking.interceptors.HttpLoggingInterceptor.Level.BODY);
    }
}