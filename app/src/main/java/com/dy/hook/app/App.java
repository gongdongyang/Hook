package com.dy.hook.app;

import android.app.Application;
import android.content.Context;

import com.dy.hook.HookHelper;

/**
 * Created by gongdongyang 2019/9/5
 * Describe:
 */
public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try{
            HookHelper.hookActivityThreadInstrumentation(this);

        }catch (Exception e){

        }
    }
}
