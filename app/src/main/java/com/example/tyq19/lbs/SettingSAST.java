package com.example.tyq19.lbs;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

/**
 * Created by tyq19 on 2017/2/17.
 */

public class SettingSAST extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        PrefFragment prefFragment = new PrefFragment();
        getFragmentManager().beginTransaction().add(R.id.frag_container, prefFragment).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.layout_in_move2, R.anim.layout_out_move2);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static class PrefFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settingsast);
            findPreference("website").setOnPreferenceClickListener(this);
            findPreference("QQ").setOnPreferenceClickListener(this);
            findPreference("wechat").setOnPreferenceClickListener(this);
        }

        public boolean onPreferenceClick(Preference preference) {
            switch (preference.getKey()) {
                case "website":
                    Uri uri = Uri.parse("http://www.tdsast.cn");
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    break;
                case "QQ":
                       try{
                           startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin="+"2944381453"+"&version=1")));
                       } catch(Exception e)
                       {
                           e.printStackTrace();
                       }
                    break;
                case "wechat":
                    try {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        ComponentName cmp = new ComponentName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");

                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setComponent(cmp);
                        startActivity(intent);
                    } catch (ActivityNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                    break;
            }
            return true;
        }
    }
}
