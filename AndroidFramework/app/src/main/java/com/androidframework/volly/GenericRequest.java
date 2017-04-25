package com.androidframework.volly;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by sid on 14/08/2016.
 */
public class GenericRequest<T> extends JsonRequest<T> {

    private final Gson gson = new Gson();
    private Map<String, String> headers = null;

    /**
     * Basically, this is the constructor which is called by the others.
     * It allows you to send an object of type A to the server and expect a JSON representing a object of type B.
     * The problem with the #JsonObjectRequest is that you expect a JSON at the end.
     * We can do better than that, we can directly receive our POJO.
     * That's what this class does.
     *
     * @param method:        HTTP Method
     * @param url:           url to be called
     * @param requestBody:   The body being sent
     * @param listener:      Listener of the request
     * @param errorListener: Error handler of the request
     * @param headers:       Added headers
     */
    public GenericRequest(int method, String url, String requestBody,
                          Response.Listener<T> listener, Response.ErrorListener errorListener, Map<String, String> headers) {
        super(method, url, requestBody, listener,
                errorListener);
        this.headers = headers;
        configureRequest();
    }


    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            // If it's not muted; we just need to create our POJO from the returned JSON and handle correctly the errors
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            GenericResponse genericResponse = new GenericResponse(response.statusCode, json);
            T parsedObject = (T) genericResponse;
            return Response.success(parsedObject, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    public class GenericResponse {
        int statusCode;
        String response;

        public GenericResponse(int statusCode, String response) {
            this.statusCode = statusCode;
            this.response = response;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getResponse() {
            return response;
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    private void configureRequest() {
        // Set retry policy
        // Add headers, for auth for example
        // ...
    }
}
