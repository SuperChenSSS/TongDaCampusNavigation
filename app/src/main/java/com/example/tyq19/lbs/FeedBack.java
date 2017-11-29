package com.example.tyq19.lbs;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

/**
 * Created by tyq19 on 2017/7/7.
 */

public class FeedBack extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        Button commit = (Button) findViewById(R.id.commit);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final EditText editText = (EditText) findViewById(R.id.feedbackText);
                        if (editText.getText().length() > 0) {
                            try (Socket socket = new Socket(MainActivity.SERVER_IP, 8190)) {
                                OutputStream outputStream = socket.getOutputStream();
                                PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream, "GBK"), true);
                                Spinner spinner = (Spinner) findViewById(R.id.feedbackSpinner);
                                printWriter.println(1);
                                printWriter.println(spinner.getSelectedItem() + " ("+ new Date().toString()+ ")" + " - " + editText.getText());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        editText.setText("");
                                        Toast.makeText(FeedBack.this, "成功提交！o(*￣▽￣*)ブ\n感谢你对本软件的建议与反馈！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(FeedBack.this, "请不要空白提交啦！(✿◡‿◡)", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}
