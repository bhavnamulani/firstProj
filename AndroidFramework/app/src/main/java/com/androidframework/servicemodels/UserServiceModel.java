package com.androidframework.servicemodels;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.androidframework.application.MyApplication;
import com.androidframework.constants.Constants;
import com.androidframework.constants.RequestConstants;
import com.androidframework.constants.URLConstants;
import com.androidframework.entities.UserDO;
import com.androidframework.pref.SharedPref;
import com.androidframework.volly.APIHandler;
import com.androidframework.volly.APIHandlerCallback;
import com.androidframework.volly.ErrorResponse;
import com.androidframework.volly.GenericRequest;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class UserServiceModel extends BaseServiceModel {

    private String requestBody;
    private String loggedInUser;

    public UserServiceModel(Context context, APIHandlerCallback apiCallback) {
        super(context, apiCallback);
        loggedInUser = sharedPref.getString(SharedPref.KEY_USER_NAME);
    }

    public void requestSignUp(String username, String password, String email, String name) {
        requestBody = formSignUpJsonBody(username, password, email, name).toString();
        APIHandler apiHandler = new APIHandler(context, this,
                RequestConstants.REQUEST_ID_POST_SIGNUP, Request.Method.POST,
                URLConstants.URL_SINGUP_USER, true, "Signing up..", requestBody);
        apiHandler.setNeedTokenHeader(true);
        apiHandler.setHeaders(formHeader());
        apiHandler.requestAPI();
    }

    public void requestUsers() {
        APIHandler apiHandler = new APIHandler(context, this,
                RequestConstants.REQUEST_ID_GET_USERS, Request.Method.GET,
                URLConstants.URL_SINGUP_USER, true, "Requesting Users..", null);
        apiHandler.setNeedTokenHeader(true);
        apiHandler.setHeaders(formHeader());
        apiHandler.requestAPI();
    }


    private Map<String, String> formHeader() {
        HashMap headers = new HashMap<>();
        headers.put("Authorization", Constants.OF_SECRET_KEY);
        headers.put("Content-Type", "application/json");
        return headers;
    }


    public JSONObject formSignUpJsonBody(String username, String password, String email, String name) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("password", password);
            jsonObject.put("email", email);
            jsonObject.put("name", name);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    @Override
    public void onAPIHandlerResponse(int requestId, boolean isSuccess, Object result
            , ErrorResponse errorResponse) {
        if (isSuccess) {
            switch (requestId) {
                case RequestConstants.REQUEST_ID_POST_SIGNUP:
                    GenericRequest.GenericResponse genericResponse = (GenericRequest.GenericResponse)
                            result;
                    if (genericResponse != null && genericResponse.getStatusCode() ==
                            Constants.STATUS_CODE_CREATED) {
                        Toast.makeText(context, "SignUp Successful please login now",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "SignUp failed, please try again",
                                Toast.LENGTH_SHORT).show();
                    }

                    if (apiCallback != null) {
                        apiCallback.onAPIHandlerResponse(requestId, isSuccess, result, errorResponse);
                    }
                    break;
                case RequestConstants.REQUEST_ID_GET_USERS:
                    GenericRequest.GenericResponse genericResponses = (GenericRequest.
                            GenericResponse)
                            result;
                    if (genericResponses.getStatusCode() == Constants.STATUS_CODE_OK) {
                        InputStream inputStream = convertStringToDocument(genericResponses.
                                getResponse());
                        ArrayList<UserDO> userDOList = parse(inputStream);

                        //saving all user
                        MyApplication.getInstance().getDatabaseManager().getUserTable()
                                .insertAllUser(userDOList);

                        if (apiCallback != null) {
                            apiCallback.onAPIHandlerResponse(requestId, isSuccess, userDOList,
                                    errorResponse);
                        }
                    } else {
                        if (apiCallback != null) {
                            apiCallback.onAPIHandlerResponse(requestId, isSuccess, null,
                                    errorResponse);
                        }
                    }
                    break;
            }
        }

    }

    /**
     * This is for parsing Get users response which we are getting in xml form.
     *
     * @param is
     * @return
     */
    public ArrayList<UserDO> parse(InputStream is) {
        ArrayList<UserDO> userDOList = new ArrayList<>();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(is, null);
            UserDO userDO = null;
            String text = null;

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("user")) {
                            // create a new instance of employee
                            userDO = new UserDO();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("user")) {
                            // add employee object to list
                            //condition to ignore self
                            if (!userDO.getUsername().equals(loggedInUser)) {
                                userDOList.add(userDO);
                            }
                        } else if (tagname.equalsIgnoreCase("email")) {
                            userDO.setEmail(text);
                        } else if (tagname.equalsIgnoreCase("name")) {
                            userDO.setName(text);
                        } else if (tagname.equalsIgnoreCase("username")) {
                            userDO.setUsername(text + "@" + Constants.SERVICE);
                        }
                        break;

                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userDOList;
    }

    private static InputStream convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlStr)));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Source xmlSource = new DOMSource(doc);
            Result outputTarget = new StreamResult(outputStream);
            TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
            InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
            return is;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
