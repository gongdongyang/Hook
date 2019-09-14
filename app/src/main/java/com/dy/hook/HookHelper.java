package com.dy.hook;

import android.app.Instrumentation;
import android.content.Context;

import java.lang.reflect.Field;

/**
 * Created by gongdongyang 2019/9/5
 * Describe:
 */
public class HookHelper {

    public static final String TARGET_INTENT = "target_intent";

    public static void hookInstrumentation(Context context) throws Exception {
        Class<?> contextImplClass = Class.forName("android.app.ContextImpl");
        Object activityThread = ReflectUtil.getField(contextImplClass, context, "mMainThread");

        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Object mInstrumentation = ReflectUtil.getField(activityThreadClass, activityThread, "mInstrumentation");
        // 用代理Instrumentation来替换mMainThread中的mInstrumentation，从而接管Instrumentation的任务
        ReflectUtil.setField(activityThreadClass, activityThread, "mInstrumentation",
                new InstrumentationProxy((Instrumentation) mInstrumentation, context.getPackageManager()));


    }

    public static void hookActivityThreadInstrumentation(Context context){
        try {
            Class<?> activityThreadClass=Class.forName("android.app.ActivityThread");
            Field activityThreadField=activityThreadClass.getDeclaredField("sCurrentActivityThread");
            activityThreadField.setAccessible(true);
            //获取ActivityThread对象sCurrentActivityThread
            Object activityThread=activityThreadField.get(null);

            Field instrumentationField=activityThreadClass.getDeclaredField("mInstrumentation");
            instrumentationField.setAccessible(true);
            //从sCurrentActivityThread中获取成员变量mInstrumentation
            Instrumentation instrumentation= (Instrumentation) instrumentationField.get(activityThread);
            //创建代理对象InstrumentationProxy
            InstrumentationProxy proxy=new InstrumentationProxy(instrumentation,context.getPackageManager());
            //将sCurrentActivityThread中成员变量mInstrumentation替换成代理类InstrumentationProxy
            instrumentationField.set(activityThread,proxy);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
