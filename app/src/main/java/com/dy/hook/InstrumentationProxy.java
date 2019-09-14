package com.dy.hook;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by gongdongyang 2019/9/5
 * Describe:
 */
public class InstrumentationProxy extends Instrumentation {

    private Instrumentation mInstrumentation;
    private PackageManager mPackageManager;

    public InstrumentationProxy(Instrumentation instrumentation, PackageManager packageManager) {
        mInstrumentation = instrumentation;
        mPackageManager = packageManager;
    }

    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target,
                                            Intent intent, int requestCode, Bundle options) {
        List<ResolveInfo> infos = mPackageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
        if (infos == null || infos.size() == 0) {
            // 保存要启动的插件Activity的类名
            intent.putExtra(HookHelper.TARGET_INTENT, intent.getComponent().getClassName());
            // 构建插桩Activity的Intent
            intent.setClassName(who, "com.dy.hook.StubActivity");
        }

        try {

            Method execMethod = Instrumentation.class.getDeclaredMethod("execStartActivity",
                    Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class, int.class, Bundle.class);

            // 将插桩Activity的Intent传给ASM验证
            return (ActivityResult) execMethod.invoke(mInstrumentation, who,
                    contextThread, token, target, intent, requestCode, options);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        String intentName=intent.getStringExtra(HookHelper.TARGET_INTENT);
        if(!TextUtils.isEmpty(intentName)){
            return super.newActivity(cl,intentName,intent);
        }
        return super.newActivity(cl,className,intent);

    }
}
