package com.example.gusteau.data.network;

import com.example.gusteau.data.meals.datasource.remote.MealApiService;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MealClient {
    private static volatile Retrofit retrofit = null;

    public MealClient() {

    }

    public static MealApiService getApiServices() {
        return getClient().create(MealApiService.class);
    }

    public static Retrofit getClient() {

        if (retrofit == null) {
            synchronized (MealClient.class) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(MealApiService.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                        .build();
            }
        }
        return retrofit;
    }
}
