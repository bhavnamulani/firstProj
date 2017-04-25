package com.androidframework.screens.signup;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidframework.R;
import com.androidframework.base_classes.AbstractBaseActivity;
import com.androidframework.constants.Constants;
import com.androidframework.servicemodels.UserServiceModel;
import com.androidframework.volly.ErrorResponse;
import com.androidframework.volly.GenericRequest;

import org.jivesoftware.smack.XMPPConnection;

public class SignUpActivity extends AbstractBaseActivity {

    private Button mSignUpUserButton;
    private EditText mName;
    private EditText mUsername;
    private EditText mPassword;
    private EditText mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initViews();
        registerEvent();
    }

    @Override
    public void onRightMostButtonClick() {

    }

    @Override
    public void onPermissionResult(int requestCode, boolean isGranted, Object extras) {

    }


    private void registerEvent() {
        mSignUpUserButton.setOnClickListener(this);
    }

    private void initViews() {
        mSignUpUserButton = (Button) findViewById(R.id.signupUserButton);
        mName = (EditText) findViewById(R.id.et_name);
        mEmail = (EditText) findViewById(R.id.et_email_id);
        mPassword = (EditText) findViewById(R.id.et_password);
        mUsername = (EditText) findViewById(R.id.et_username);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signupUserButton:
                onClickOfSignUp();
                break;
        }
    }

    private void onClickOfSignUp() {
        //We have to save username and password n directly launch a chat screen of that user who
        //has signed up(i.e now that user is logged in).

        if (isValidField()) {
            UserServiceModel userServiceModelModel = new UserServiceModel(this, this);
            userServiceModelModel.requestSignUp(mUsername.getText().toString()
                            .trim(),
                    mPassword.getText().toString().trim(),
                    mEmail.getText().toString().trim(),
                    mName.getText().toString().trim());
        }
    }


    private boolean isValidField() {
        if (mUsername.getText().toString().trim().length() == 0) {
            showAleartPosBtnOnly(null, "Alert", "Please enter your Username");
            return false;

        } else if (mPassword.getText().toString().trim().length() == 0) {
            showAleartPosBtnOnly(null, "Alert", "Please enter your Password");
            return false;

        } else if (mEmail.getText().toString().trim().length() == 0) {
            showAleartPosBtnOnly(null, "Alert", "Please enter your Email");
            return false;

        } else if (mName.getText().toString().trim().length() == 0) {
            showAleartPosBtnOnly(null, "Alert", "Please enter confirm your Name");
            return false;
        }
        return true;
    }

    @Override
    public void onAPIHandlerResponse(int requestId, boolean isSuccess, Object result, ErrorResponse
            errorResponse) {
        System.out.println(">>result" + result);
        GenericRequest.GenericResponse genericResponse = (GenericRequest.GenericResponse) result;
        if (genericResponse != null && genericResponse.getStatusCode() == Constants.STATUS_CODE_CREATED) {
            finish();
        }

    }

}