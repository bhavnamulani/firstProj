package com.androidframework.volly;

/**
 * Created by ashishkumarpatel on 05/02/17.
 */

public class ErrorResponse {

    //Register all the error code here
    public static int EROOR_CODE_INTERENT_NOT_FOUND = 9000;
    public static int EROOR_CODE_ERROR_FROM_SERVER = 5000;
    public static int EROOR_CODE_UNABLE_TO_FETCH_DATA = 5001;
    public static int EROOR_CODE_FAILED_WITH_NO_MSG = 5002;


    private int errorCode;
    private String errorString;

    public ErrorResponse() {
    }

    public ErrorResponse(int errorCode, String errorString) {
        this.errorCode = errorCode;
        this.errorString = errorString;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorString() {
        return errorString;
    }

    public void setErrorString(String errorString) {
        this.errorString = errorString;
    }
}
