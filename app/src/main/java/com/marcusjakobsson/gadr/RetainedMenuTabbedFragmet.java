package com.marcusjakobsson.gadr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;

/**
 * Created by lenovo on 2017-12-12.
 */

public class RetainedMenuTabbedFragmet extends Fragment {

    public RoundedBitmapDrawable drawable;
    public String drawerUserName;
    public String userStatus;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //retain this fragment
        setRetainInstance(true);
    }


    public RoundedBitmapDrawable getDrawable() {
        return drawable;
    }

    public void setDrawable(RoundedBitmapDrawable drawable) {
        this.drawable = drawable;
    }
}
