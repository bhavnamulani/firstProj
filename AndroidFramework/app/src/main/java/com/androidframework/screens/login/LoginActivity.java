package com.androidframework.screens.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidframework.R;
import com.androidframework.application.MyApplication;
import com.androidframework.base_classes.AbstractBaseActivity;
import com.androidframework.constants.Constants;
import com.androidframework.controllers.ActivityController;
import com.androidframework.pref.SharedPref;
import com.androidframework.volly.ErrorResponse;
import com.androidframework.xmpp.XMPPConnectCallback;
import com.androidframework.xmpp.XMPPManager;

public class LoginActivity extends AbstractBaseActivity
        implements View.OnClickListener, XMPPConnectCallback {
    private TextView mSignupButton;
    private Button mLoginButton;

    private EditText mETUserName;
    private EditText mETPwd;

    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = new SharedPref(this);
        checkIfUserAlreadyLoggedIn();
        initViews();
        registerEvent();
    }

    @Override
    public void onRightMostButtonClick() {

    }

    @Override
    public void onPermissionResult(int requestCode, boolean isGranted, Object extras) {

    }


    @Override
    public void onConnectXMPPResponse(boolean isLoginSuccess, String userName, String pwd) {
        if (isLoginSuccess) {
            sharedPref.put(SharedPref.KEY_USER_NAME, userName + "@" + Constants.SERVICE);
            sharedPref.put(SharedPref.KEY_PWD, pwd);
            Toast.makeText(LoginActivity.this, "Login Successful !!", Toast.LENGTH_SHORT)
                    .show();
            finish();
            ActivityController.getInstance().handleEvent(LoginActivity.this,
                    ActivityController.ACTIVITY_USERS_LIST, false);
        }
    }

    private void checkIfUserAlreadyLoggedIn() {
        String userName = sharedPref.getString(SharedPref.KEY_USER_NAME);
        String pwd = sharedPref.getString(SharedPref.KEY_PWD);
        if (userName != null && pwd != null) {
            finish();
            ActivityController.getInstance().handleEvent(LoginActivity.this,
                    ActivityController.ACTIVITY_USERS_LIST, true);
        }
    }

    private void registerEvent() {
        mSignupButton.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
    }

    private void initViews() {
        mSignupButton = (TextView) findViewById(R.id.tv_signup);
        mLoginButton = (Button) findViewById(R.id.loginButton);
        mETUserName = (EditText) findViewById(R.id.et_user_name);
        mETPwd = (EditText) findViewById(R.id.et_pwd);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_signup:
                ActivityController.getInstance().handleEvent(this,
                        ActivityController.ACTIVITY_SIGN_UP);
                break;
            case R.id.loginButton:
                if (isValid()) {
                    String userName = mETUserName.getText().toString().trim();
                    String pwd = mETPwd.getText().toString().trim();
                    XMPPManager.getINSTANCE().connectXMPP(this, this, userName, pwd, true);
                }
                break;
        }
    }

    private boolean isValid() {
        if (mETUserName.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Please enter user name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mETPwd.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!MyApplication.getInstance().checkConnection(this)) {
            Toast.makeText(this, "No internet connection found.", Toast.LENGTH_SHORT).show();
        }
        return true;
    }


    @Override
    public void onAPIHandlerResponse(int requestId, boolean isSuccess, Object result,
                                     ErrorResponse errorResponse) {

    }
}

