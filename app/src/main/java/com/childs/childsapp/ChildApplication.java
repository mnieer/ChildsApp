package com.childs.childsapp;


import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.childs.operations.LocaleManager;
import com.childs.session.SessionManager;

public class ChildApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        //get language code
        SessionManager sessionManager = new SessionManager(base);
        super.attachBaseContext(LocaleManager.setLocale(base,sessionManager.getStringValue("app_lang")));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //get language code
        SessionManager sessionManager = new SessionManager(this);
        LocaleManager.setLocale(this,sessionManager.getStringValue("app_lang"));
    }

}