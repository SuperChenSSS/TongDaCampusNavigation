package com.example.tyq19.lbs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.util.Scanner;

/**
 * Created by tyq19 on 2017/2/17.
 */

public class Setting extends AppCompatActivity {
    private boolean GPS_ONLY;
    private boolean DOOR_LOC;
    private boolean RADIUS_MODE;
    private boolean COMPASS_MODE;
    static String msg = "没有获取到更新信息";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        GPS_ONLY = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("gps_only", false);
        RADIUS_MODE = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("radius_mode", false);
        DOOR_LOC = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("door_loc", false);
        COMPASS_MODE = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("compass_mode", false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        PrefFragment prefFragment = new PrefFragment();
        getFragmentManager().beginTransaction().add(R.id.frag_container, prefFragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("gps_only", false) != GPS_ONLY
                || PreferenceManager.getDefaultSharedPreferences(this).getBoolean("door_loc", false) != DOOR_LOC
                || PreferenceManager.getDefaultSharedPreferences(this).getBoolean("radius_mode", false) != RADIUS_MODE
                || PreferenceManager.getDefaultSharedPreferences(this).getBoolean("compass_mode", false) != COMPASS_MODE) {
            setResult(1);
        }
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
            addPreferencesFromResource(R.xml.setting);
            findPreference("update").setSummary("当前版本 : " + new String(MainActivity.VER_STR));
            findPreference("about").setOnPreferenceClickListener(this);
            findPreference("feedback").setOnPreferenceClickListener(this);
            findPreference("update").setOnPreferenceClickListener(this);
        }

        public boolean onPreferenceClick(Preference preference) {
            switch (preference.getKey()) {
                case "about":
                    startActivity(new Intent(getActivity(), SettingSAST.class));
                    getActivity().overridePendingTransition(R.anim.layout_in_move, R.anim.layout_out_move);
                    break;
                case "feedback":
                    startActivity(new Intent(getActivity(), FeedBack.class));
                    break;
                case "update":
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try (Socket socket = new Socket(MainActivity.SERVER_IP, 8190)) {
                                InputStream inputStream = socket.getInputStream();
                                OutputStream outputStream = socket.getOutputStream();
                                final Scanner scanner = new Scanner(inputStream, "GBK");
                                final PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream, "GBK"), true);
                                printWriter.println(2);
                                printWriter.println(MainActivity.VERSION);
                                while (scanner.hasNextLine()) {
                                    final int SER_VER = Integer.parseInt(scanner.nextLine());
                                    final char[] SER_VER_STR = new char[5];
                                    for (int i = 0; i < 5; i++) {
                                        switch (i) {
                                            case 0:
                                                SER_VER_STR[i] = (char) (SER_VER / 100 % 10 + '0');
                                                break;
                                            case 2:
                                                SER_VER_STR[i] = (char) (SER_VER / 10 % 10 + '0');
                                                break;
                                            case 4:
                                                SER_VER_STR[i] = (char) (SER_VER % 10 + '0');
                                                break;
                                            case 1:
                                            case 3:
                                                SER_VER_STR[i] = '.';
                                                break;
                                        }
                                    }
                                    Looper.prepare();
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            findPreference("update").setSummary("当前版本 : " + new String(MainActivity.VER_STR) + " 最新版本 : " + new String(SER_VER_STR));
                                            if (MainActivity.VERSION >= SER_VER) {
                                                findPreference("update").setTitle("暂无更新");
                                                findPreference("update").setEnabled(false);
                                                Toast.makeText(getActivity(), "已经是最新版了哟φ(゜▽゜*)♪", Toast.LENGTH_SHORT).show();
                                            } else {
                                                if (findPreference("update").getTitle().equals("检查更新")) {
                                                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    if (scanner.hasNextLine()) {
                                                                        final Uri uri = Uri.parse(scanner.nextLine());
                                                                        final Intent it = new Intent(Intent.ACTION_VIEW, uri);
                                                                        getActivity().startActivity(it);
                                                                    }
                                                                }
                                                            }).start();
                                                        }
                                                    }).setNegativeButton("稍后更新", null)
                                                            .setTitle("有新版更新").setMessage("当前版本: " + new String(MainActivity.VER_STR) + "\n最新版本: " + new String(SER_VER_STR) + "\n\n" + msg)
                                                            .create();
                                                    alertDialog.show();
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try (Socket socket = new Socket(MainActivity.SERVER_IP, 8190)) {
                                                                final Scanner scanner = new Scanner(socket.getInputStream());
                                                                final PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                                                                printWriter.println(3);
                                                                msg = "";
                                                                while (true) {
                                                                    if (scanner.hasNextLine()) {
                                                                        msg += scanner.nextLine() + "\n";
                                                                    }
                                                                    getActivity().runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            alertDialog.setMessage("当前版本: " + new String(MainActivity.VER_STR) + "\n最新版本: " + new String(SER_VER_STR) + "\n\n" + msg);

                                                                        }
                                                                    });
                                                                      Thread.sleep(250);
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }).start();


                                                    findPreference("update").setTitle("立即更新");
                                                } else {
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (scanner.hasNextLine()) {
                                                                final Uri uri = Uri.parse(scanner.nextLine());
                                                                final Intent it = new Intent(Intent.ACTION_VIEW, uri);
                                                                getActivity().startActivity(it);
                                                            }
                                                        }
                                                    }).start();

                                                }
                                            }
                                        }
                                    });
                                    Looper.loop();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                default:
            }
            return true;
        }
    }
}
