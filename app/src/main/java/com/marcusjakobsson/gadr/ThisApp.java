package com.marcusjakobsson.gadr;

import android.app.Application;

/**
 * Created by carlbratt on 2017-11-13.
 */

/*
        // set
        ((ThisApp) getActivity().getApplication()).setTestString("foo");

        // get
        String s = ((ThisApp) getActivity().getApplication()).getTestString();
 */

public class ThisApp extends Application {

    private String testString = "TestVariable";


    public String getTestString() {
        return  testString;
    }

    public void setTestString(String string) {
        this.testString = string;
    }
}
