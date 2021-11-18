package com.ehsancharities.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Belal Jaradat on 12/19/2020.
 */
public class Session {

    private final String IS_LOGIN = "isLogin";

    private final String TYPE_ACCOUNT = "accountType";




    private static Session session;

    private Session() {
    }

    public static Session getInstance() {
        if (session == null)
            session = new Session();

        return session;
    }

    public static SharedPreferences getPrefs(Context context) {


        return context.getSharedPreferences("Login", Context.MODE_PRIVATE);

    }

    public Boolean isLogin(Context context) {

        return getPrefs(context).getBoolean(IS_LOGIN, false);
    }


    public void setIsLogin(Context context, boolean value) {
        // perform validation etc..
        getPrefs(context).edit().putBoolean(IS_LOGIN, value).apply();
    }


    public String getTYPE_ACCOUNT(Context context) {

        return getPrefs(context).getString(TYPE_ACCOUNT, "");
    }


    public void setTYPE_ACCOUNT(Context context, String value) {
        // perform validation etc..
        getPrefs(context).edit().putString(TYPE_ACCOUNT, value).apply();
    }






}
