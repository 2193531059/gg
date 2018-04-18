package com.ezreal.chat;

import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Hero on 2018/4/17.
 */

public class HttpUtils {
    public static final String HOST = "http://192.168.1.101:8080/VideoUpload/";
    private static final String TAG = "HttpUtils";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static HttpUtils instance;
    private OkHttpClient mHttpClient;

    public static HttpUtils getInstance() {
        if (instance == null) {
            instance = new HttpUtils();
        }
        return instance;
    }

    private HttpUtils() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS);
        mHttpClient = builder.build();
    }

    public Response request(String url, Object parameters) {
        String      json        = new Gson().toJson(parameters);
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        try {
            Response response = mHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                return null;
            } else {
                return response;
            }
        } catch (Exception err) {
            return null;
        }
    }

    public Response request(String url){
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = mHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                return null;
            } else {
                return response;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Response request(String url, Object parameters, HashMap<String, String> headers) {
        String      json        = new Gson().toJson(parameters);
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json");

        if (null != headers) {
            for (String key : headers.keySet()) {
                builder.addHeader(key, headers.get(key));
            }
        }

        Request request = builder.build();

        try {
            Response response = mHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                return null;
            } else {
                return response;
            }
        } catch (Exception err) {
            return null;
        }
    }
}
