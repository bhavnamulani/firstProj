package com.androidframework.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.androidframework.screens.login.LoginActivity;
import com.androidframework.R;
import com.androidframework.application.MyApplication;

import java.util.Locale;
import java.util.Random;

/**
 * Class to keep utility common methods
 * Singleton class
 */
public class Utils {
    public static Utils instance;

    public static Utils getInstance() {
        if (instance == null) {
            instance = new Utils();
        }
        return instance;
    }

    //this will return meters if distance is less than 1 KM
    public String refineDistanceText(String distance) {
        //System.out.println(">>dis>>" + distance);
        double distanceVal = Double.parseDouble(distance);

        if (distanceVal < 1) {
            int distanceInMeter = (int) Math.floor((distanceVal * 1000));
            //setting meter with no value after point
            return distanceInMeter + " m";
        }
        //setting km with single digit
        return round(distanceVal, 1) + " Km";
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public void openBrowser(Context context, String URL) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(URL));
        context.startActivity(i);
    }

    public void openMap(Context context,
                        double sourceLatitude, double sourceLongitude,
                        double destinationLatitude, double destinationLongitude, String title) {

        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?&daddr=%f,%f (%s)",
                destinationLatitude, destinationLongitude, title);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            try {

                uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f",
                        sourceLatitude, sourceLongitude, destinationLatitude, destinationLongitude);
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                context.startActivity(intent);

            } catch (ActivityNotFoundException innerEx) {
                Toast.makeText(context, "Please install google map application", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void hideKeyboard(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


//    public void showSnackBar(Activity activity, String textToShow) {
//        final Snackbar snack = Snackbar.make(activity.findViewById(android.R.id.content), textToShow, Snackbar.LENGTH_LONG);
//        snack.setDuration(6000);
//        snack.setAction("Dismiss", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                snack.dismiss();
//            }
//        });
//        snack.setActionTextColor(ContextCompat.getColor(activity, R.color.white));
//        View view = snack.getView();
//        view.setBackgroundColor(ContextCompat.getColor(activity, R.color.green));
//        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
//        params.gravity = Gravity.TOP;
//        view.setLayoutParams(params);
//        snack.show();
//    }

//    public void showSnackBar(boolean success, Activity activity, String textToShow) {
//        final Snackbar snack = Snackbar.make(activity.findViewById(android.R.id.content), textToShow, Snackbar.LENGTH_LONG);
//        snack.setDuration(6000);
//        snack.setAction("Dismiss", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                snack.dismiss();
//            }
//        });
//        snack.setActionTextColor(ContextCompat.getColor(activity, R.color.white));
//        View view = snack.getView();
//        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
//
//
//        tv.setMaxLines(6);
//        if (!success) {
//            view.setBackgroundColor(ContextCompat.getColor(activity, R.color.error));
//        } else {
//            view.setBackgroundColor(ContextCompat.getColor(activity, R.color.green));
//        }
//
//
//        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
//        params.gravity = Gravity.TOP;
//        view.setLayoutParams(params);
//        snack.show();
//    }


    public void showLocalNotification(String title, String description) {
        Intent intent = new Intent(MyApplication.getInstance(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(MyApplication.getInstance(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder b = new NotificationCompat.Builder(MyApplication.getInstance());
        Bitmap largeIcon = BitmapFactory.decodeResource(MyApplication.getInstance().getResources(), R.mipmap.ic_launcher);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(title);
        bigTextStyle.bigText(description);

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(largeIcon)
                .setTicker(title + ": " + description)
                .setContentTitle(title)
                .setStyle(bigTextStyle)
                .setContentText(description)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent);


        NotificationManager notificationManager = (NotificationManager) MyApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(generateRandomNum(), b.build());
    }

    public int generateRandomNum() {
        int min = 1;
        int max = 100;
        Random r = new Random();
        return r.nextInt(max - min + 1) + min;
    }

}
