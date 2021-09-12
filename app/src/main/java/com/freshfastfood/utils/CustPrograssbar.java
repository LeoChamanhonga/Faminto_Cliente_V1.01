package com.freshfastfood.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class CustPrograssbar {
    public static ProgressDialog progressDialog;

    public void prograssCreate(Context context) {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                return;
            } else {
                progressDialog = new ProgressDialog(context);
                if (progressDialog != null && !progressDialog.isShowing()) {

                    progressDialog.setMessage("Progress...");
                    progressDialog.show();
                }
            }
        } catch (Exception e) {

        }
    }

    public void closePrograssBar() {
        if (progressDialog != null) {
            try {
                progressDialog.cancel();
            } catch (Exception e) {
            }
        }
    }
}
