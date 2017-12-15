package com.marcusjakobsson.gadr;

import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Mäki on 2017-12-14.
 */

class MySnackbarProvider {

    public static void showSnackBar(View view, Integer stringID)
    {
        // make snackbar
        Snackbar mSnackbar = Snackbar.make(view, stringID, Snackbar.LENGTH_LONG);
        // get snackbar view
        View mView = mSnackbar.getView();
        // get textview inside snackbar view
        TextView mTextView = mView.findViewById(android.support.design.R.id.snackbar_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTextView.setTextColor(view.getResources().getColor(R.color.colorAccent,view.getContext().getTheme()));
        }else {
            mTextView.setTextColor(ContextCompat.getColor(view.getContext(), R.color.colorAccent));
        }
        mTextView.setTextSize(24);
        // set text to center
        mTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        // show the snackbar
        mSnackbar.show();
    }

    public static void showSnackBar(View view, String message)
    {
        // make snackbar
        Snackbar mSnackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        // get snackbar view
        View mView = mSnackbar.getView();
        // get textview inside snackbar view
        TextView mTextView = mView.findViewById(android.support.design.R.id.snackbar_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTextView.setTextColor(view.getResources().getColor(R.color.colorAccent,view.getContext().getTheme()));
        }else {
            mTextView.setTextColor(ContextCompat.getColor(view.getContext(), R.color.colorAccent));
        }
        mTextView.setTextSize(24);
        // set text to center
        mTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        // show the snackbar
        mSnackbar.show();
    }
}
