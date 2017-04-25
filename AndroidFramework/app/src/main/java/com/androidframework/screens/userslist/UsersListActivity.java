package com.androidframework.screens.userslist;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.androidframework.R;
import com.androidframework.application.MyApplication;
import com.androidframework.base_classes.AbstractBaseActivity;
import com.androidframework.controllers.ActivityController;
import com.androidframework.entities.UserDO;
import com.androidframework.pref.SharedPref;
import com.androidframework.servicemodels.UserServiceModel;
import com.androidframework.volly.ErrorResponse;
import com.androidframework.xmpp.XMPPConnectCallback;
import com.androidframework.xmpp.XMPPManager;
import com.androidframework.xmpp.XMPPMessageCallback;

import java.util.ArrayList;

public class UsersListActivity extends AbstractBaseActivity implements XMPPConnectCallback
        , XMPPMessageCallback {
    private static final String TAG = UsersListActivity.class.getSimpleName();
    private ListView mlistView;
    private ArrayList<UserDO> userDOArrayList = new ArrayList<UserDO>();
    private UserListAdapter userListAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        XMPPManager.getINSTANCE().registerXMPPCallback(this);
        checkIfNeedToConnectXMPP();
        initActionBarView();
        setTitleAndSubTitle();
        requestUserList();
        registerViews();
    }


    private void checkIfNeedToConnectXMPP() {
        boolean isNeedToConnectXMPP = getIntent().getBooleanExtra
                (ActivityController.KEY_PARCEL_EXTRAS,
                false);
        if (isNeedToConnectXMPP) {
            String userName = sharedPref.getString(SharedPref.KEY_USER_NAME);
            String pwd = sharedPref.getString(SharedPref.KEY_PWD);
            showActionBarLoading();
            XMPPManager.getINSTANCE().connectXMPP(this, this, userName, pwd, false);
        }
    }

    @Override
    public void onRightMostButtonClick() {
        finish();
        sharedPref.clearSharedPref();
        ActivityController.getInstance().handleEvent(UsersListActivity.this,
                ActivityController.ACTIVITY_SIGN_IN);

    }

    private void setTitleAndSubTitle() {
        setTitle("VChat");
        setSubTitle(sharedPref.getString(SharedPref.KEY_USER_NAME));
    }

    private void registerViews() {
        mlistView = (ListView) findViewById(R.id.listView);
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserDO userDAO = userDOArrayList.get(position);
                launchProfileActivity(userDAO);
            }
        });

    }

    private void launchProfileActivity(UserDO userDO) {
        ActivityController.getInstance().handleEvent(this,
                ActivityController.ACTIVITY_CHAT_SCREEN, userDO);
    }


    private void requestUserList() {
        UserServiceModel userServiceModelModel = new UserServiceModel(this, this);
        userServiceModelModel.requestUsers();
    }

    @Override
    public void onPermissionResult(int requestCode, boolean isGranted, Object extras) {

    }

    @Override
    public void onMessageReceived(final String fromUserName, final String message) {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onMessageReceived:" + message);

                int previousCountOfUser = MyApplication.getInstance().getDatabaseManager()
                        .getUserTable().getUnreadMessageCount(fromUserName);
                MyApplication.getInstance().getDatabaseManager()
                        .getUserTable().updateLastMsgAndMsgCount(fromUserName, message,
                        previousCountOfUser + 1);
                refreshAdapter();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //showing user from database first
        refreshAdapter();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onConnectXMPPResponse(boolean isLoginSuccess, String userName, String pwd) {
        hideActionBarLoading();
    }

    @Override
    public void onAPIHandlerResponse(int requestId, boolean isSuccess, Object result, ErrorResponse
            errorResponse) {
       refreshAdapter();

    }


    @Override
    public void onClick(View view) {
    }


    private void refreshAdapter() {
        userDOArrayList = MyApplication.getInstance().getDatabaseManager()
                .getUserTable().getAllUser();
        if (userListAdapter == null) {
            userListAdapter = new UserListAdapter(this, userDOArrayList);
            mlistView.setAdapter(userListAdapter);
        } else {
            userListAdapter.refreshAdapter(userDOArrayList);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XMPPManager.getINSTANCE().unregisterXMPPCallback(this);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
