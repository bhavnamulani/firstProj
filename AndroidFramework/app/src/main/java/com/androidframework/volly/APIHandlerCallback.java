package com.androidframework.volly;

/**
 * Created by sid on 07/08/2016.
 */
public interface APIHandlerCallback {
    void onAPIHandlerResponse(int requestId, boolean isSuccess, Object result, ErrorResponse errorResponse);
}
