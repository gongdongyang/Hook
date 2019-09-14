package com.dy.hook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,TargetActivity.class);
                startActivity(intent);
            }
        });

        hookOnClickListener(btn);

    }

    class HookedOnClickListener implements View.OnClickListener {
        private View.OnClickListener origin;

        HookedOnClickListener(View.OnClickListener origin) {
            this.origin = origin;
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this, "hook click", Toast.LENGTH_SHORT).show();
            Log.i("MainActivity","Before click, do what you want to to.");
            if (origin != null) {
                origin.onClick(v);
            }
            Log.i("MainActivity","After click, do what you want to to.");
        }
    }


    private void hookOnClickListener(View view) {
        try{
            //得到 View 的 ListenerInfo 对象
            Method getListenerInfo = View.class.getDeclaredMethod("getListenerInfo");
            getListenerInfo.setAccessible(true);

            Object listenerInfo = getListenerInfo.invoke(view);
            Class<?> listenerInfoClz = Class.forName("android.view.View$ListenerInfo");

            Field mOnClickListener = listenerInfoClz.getDeclaredField("mOnClickListener");
            mOnClickListener.setAccessible(true);
            View.OnClickListener originOnClickListener = (View.OnClickListener) mOnClickListener.get(listenerInfo);

            View.OnClickListener hookedOnClickListener = new HookedOnClickListener(originOnClickListener);
            mOnClickListener.set(listenerInfo, hookedOnClickListener);

        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
