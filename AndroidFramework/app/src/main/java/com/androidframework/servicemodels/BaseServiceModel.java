package com.androidframework.servicemodels;

import android.content.Context;

import com.androidframework.application.MyApplication;
import com.androidframework.pref.SharedPref;
import com.androidframework.volly.APIHandlerCallback;
import com.androidframework.volly.ErrorResponse;
import com.google.gson.Gson;


public class BaseServiceModel implements APIHandlerCallback {
    protected Context context;
    protected APIHandlerCallback apiCallback;
    protected SharedPref sharedPref;
    protected Gson gson = null;


    public BaseServiceModel(Context context, APIHandlerCallback apiCallback) {
        this.context = context;
        this.apiCallback = apiCallback;
        sharedPref = MyApplication.getInstance().getSharedPref();
        gson = new Gson();
    }


    @Override
    public void onAPIHandlerResponse(int requestId, boolean isSuccess, Object result, ErrorResponse
            errorResponse) {
        try {
            switch (requestId) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
