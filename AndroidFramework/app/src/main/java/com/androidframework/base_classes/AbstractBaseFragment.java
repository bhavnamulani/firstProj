package com.androidframework.base_classes;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.androidframework.application.MyApplication;
import com.androidframework.controllers.FragmentNavigationViewController;
import com.androidframework.pref.SharedPref;
import com.androidframework.utils.Utils;
import com.androidframework.volly.APIHandlerCallback;

/**
 * Base Fragment class to keep the common code and abstraction
 * NOTE: Please extend this with each Fragment you create
 */
public abstract class AbstractBaseFragment extends Fragment implements View.OnClickListener, APIHandlerCallback {
    protected Object parcelExtras;
    protected SharedPref sharedPref;

    protected FragmentNavigationViewController navigationController;


    public FragmentNavigationViewController getNavigationController() {
        return navigationController;
    }

    public void setNavigationController(FragmentNavigationViewController navigationController) {
        this.navigationController = navigationController;
    }


    public Object getParcelExtras() {
        return parcelExtras;
    }

    public void setParcelExtras(Object parcelExtras) {
        this.parcelExtras = parcelExtras;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = MyApplication.getInstance().getSharedPref();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    protected void showAleartPosBtnOnly(final MyApplication.DailogCallback dailogCallback,
                                        String title, String bodyText) {
        Utils.getInstance().hideKeyboard(getActivity());
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage(bodyText);
        builder1.setTitle(title);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (dailogCallback != null)
                            dailogCallback.onDailogYesClick();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    protected void showLogoutDialog(final MyApplication.DailogCallback dailogCallback) {
        Utils.getInstance().hideKeyboard(getActivity());
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage("Do you want to logout ?");
        builder1.setTitle("Logout");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Logout",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (dailogCallback != null)
                            dailogCallback.onDailogYesClick();
                    }
                });

        builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


}
