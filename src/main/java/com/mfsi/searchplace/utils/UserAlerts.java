package com.mfsi.searchplace.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by Bhaskar Pande on 2/21/2017.
 */
public class UserAlerts {

    interface UserAlertsListener{
        public void onUserAlertOkClicked();
    }

    /**
     * Create a simple {@link Dialog} with an 'OK' button and a message.
     *
     * @param activity the Activity in which the Dialog should be displayed.
     * @param text the message to display on the Dialog.
     * @return an instance of {@link android.app.AlertDialog}
     */
    public static Dialog makeSimpleDialog(Activity activity, String text, DialogInterface.OnDismissListener listener) {
        return (new AlertDialog.Builder(activity)).setMessage(text)
                .setNeutralButton(android.R.string.ok, null).setOnDismissListener(listener).create();
    }

}
