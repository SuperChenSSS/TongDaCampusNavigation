package com.example.tyq19.lbs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class SplashScreen extends Activity {
    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);

        setContentView(R.layout.splashscreen);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Animation animation = new AlphaAnimation(1.0f, 0.0f);
                animation.setDuration(200);
                findViewById(R.id.splashScreenLinearLayout).startAnimation(animation);
                setContentView(R.layout.aboutsast);
            }
        }, 750);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                SplashScreen.this.startActivity(mainIntent);
                overridePendingTransition(R.anim.layout_in_long, 0);
                SplashScreen.this.finish();
            }
        }, 2000);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try (Socket socket = new Socket("123.206.187.205", 8190)) {
                    OutputStream outputStream = socket.getOutputStream();
                    PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream, "GBK"), true);
                    printWriter.println(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
