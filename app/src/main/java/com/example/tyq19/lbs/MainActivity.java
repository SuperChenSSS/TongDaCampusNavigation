package com.example.tyq19.lbs;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.*;
import com.baidu.mapapi.bikenavi.BikeNavigateHelper;
import com.baidu.mapapi.bikenavi.adapter.IBEngineInitListener;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity implements BaiduMap.OnMapLoadedCallback, OnGetRoutePlanResultListener {
    static String msg = "没有获取到更新信息";
    static AlertDialog alertDialog;
    static final int VERSION = 111;
    static final String SERVER_IP = "123.206.187.205";
    static char[] VER_STR = new char[5];
    private MySensorEventListener accelerometerListener;
    private MySensorEventListener magneticListener;
    private SensorManager mSensorManager;
    private Sensor accelerometer; // 加速度传感器
    private Sensor magnetic; // 地磁场传感器
    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];
    Thread timeOutThread = null;
    boolean timeOut = false;
    boolean progressDismissed = false;
    ArrayList<Marker> markerList = new ArrayList<>();
    ArrayList<Overlay> textList = new ArrayList<>();
    Thread naviThread = null;
    WalkingRouteOverlay preOverlay = null;
    RoutePlanSearch mSearch = null;
    int nodeIndex = -1;
    private Vibrator vibrator;
    WalkingRouteLine route = null;
    OverlayManager routeOverlay = null;
    BikeNavigateHelper mNaviHelper = null;
    ProgressDialog progressDialog = null;
    private Snackbar snackbar = null;
    private MapView mMapView = null;
    LocationClient mLocationClient = null;
    BDLocationListener myListener = new MyLocationListener();
    BaiduMap baiduMap;
    boolean isFirstLocate = true;
    private boolean GPS_ONLY;
    private boolean DOOR_LOC;
    private boolean RADIUS_MODE;
    private Marker nowMarker = null;
    private Marker preMarker = null;
    private boolean follow = false;

    private final LatLng ll_busStation = new LatLng(32.33806164243768, 119.4019868406561);//公交站
    private final LatLng ll_canteen_south = new LatLng(32.338126, 119.406144);//食堂南门
    private final LatLng ll_canteen_north = new LatLng(32.338553, 119.406405);//食堂东门
    private final LatLng ll_canteen_western = new LatLng(32.338385, 119.405758);//食堂西门
    //private final LatLng ll_dormitory3_south = new LatLng(32.337783, 119.403552);//宿舍(三号楼)南门
    //private final LatLng ll_dormitory3_north = new LatLng(32.33848, 119.403391);//宿舍(三号楼)北门
    private final LatLng ll_westGate = new LatLng(32.337573, 119.402102);//南京邮电大学通达学院(西门)
    private final LatLng ll_yangJiao2 = new LatLng(32.339937807453886, 119.40834684366673);//扬教2西门
    private final LatLng ll_yangJiao2_east = new LatLng(32.33993018084633, 119.40748447037716);//扬教2东门
    private final LatLng ll_yangJiao1 = new LatLng(32.339091250052896, 119.40844565727284);//扬教1西门
    private final LatLng ll_yangJiao1_east = new LatLng(32.33911413008778, 119.40764616536894);//扬教1东门
    private final LatLng ll_yangJiao3 = new LatLng(32.33925141017448, 119.4097482002623);//扬教3
    private final LatLng ll_yangShi1 = new LatLng(32.33922853017462, 119.40910142029512);//扬实1
    private final LatLng ll_yangShi2 = new LatLng(32.339991193688626, 119.40896667446862);//扬实2
    private final LatLng ll_playground = new LatLng(32.33951834307444, 119.40634362237948);//操场
    private final LatLng ll_library = new LatLng(32.33796249441339, 119.41007159024589);//图书馆
    private final LatLng ll_dormitory3 = new LatLng(32.33803113536496, 119.40299294282727);//宿舍(三号楼)
    private final LatLng ll_dormitory2 = new LatLng(32.338183, 119.404038);//宿舍(二号楼)
    private final LatLng ll_dormitory1 = new LatLng(32.338278, 119.405093);//宿舍(一号楼)
    private final LatLng ll_dormitory45 = new LatLng(32.337886226627774, 119.40682870735488);//宿舍(四五号楼)
    private final LatLng ll_dormitory6 = new LatLng(32.33748200628054, 119.40668497847328);//宿舍(六号楼)
    private final LatLng ll_dormitory78 = new LatLng(32.33777182482767, 119.4055620965858);//宿舍(七八号楼)
    private final LatLng ll_dormitory9 = new LatLng(32.337642169277686, 119.40462785885542);//宿舍(九号楼)
    private final LatLng ll_studentCenter = new LatLng(32.33773369086184, 119.40832887755653);//大活
    private final LatLng ll_northGate = new LatLng(32.340368, 119.408637);//北门
    private final LatLng ll_basketballFiled = new LatLng(32.339320050138966, 119.40705328373237);//篮球场
    private final LatLng ll_teacherDorm = new LatLng(32.33731421616706, 119.40354090918835);//教师公寓
    private final LatLng ll_canteen2 = new LatLng(32.338496, 119.407056);//二号食堂(在建)
    private final LatLng ll_incubationCenter = new LatLng(32.337725, 119.40636);//创业中心
    private final LatLng ll_reportCenter = new LatLng(32.338488740365435, 119.41038599717439);//学术报告厅
    private final LatLng ll_adminBuilding = new LatLng(32.33862602141001, 119.4099188783092);//行政楼
    private final LatLng ll_tennis = new LatLng(32.33993018084633, 119.40696345318136);//网球场
    private final LatLng ll_expressEast = new LatLng(32.337527767166065, 119.40587650351429);//菜鸟驿站(小河东)
    private final LatLng ll_expressWest = new LatLng(32.33749725991164, 119.4056968424123);//菜鸟驿站(小河西)
    private ArrayList<String> titleList = new ArrayList<String>() {
        {
            add("公交站");
            add("食堂南门");
            add("食堂东门");
            add("食堂西门");
            //      add("宿舍(三号楼)南门");
            //      add("宿舍(三号楼)北门");
            add("南京邮电大学通达学院(西门)");
            add("扬教1东门");
            add("扬教1西门");
            add("扬教2东门");
            add("扬教2西门");
            add("扬教3");
            add("扬实1");
            add("扬实2");
            add("操场");
            add("图书馆");
            add("宿舍(三号楼)");
            add("宿舍(二号楼)");
            add("宿舍(一号楼)");
            add("宿舍(四五号楼)");
            add("宿舍(六号楼)");
            add("宿舍(七八号楼)");
            add("宿舍(九号楼)");
            add("大学生活动中心");
            add("南京邮电大学通达学院(北门)");
            add("篮球场");
            add("教师公寓");
            add("二号食堂(在建)");
            add("创业中心");
            add("学术报告厅");
            add("行政楼");
            add("网球场");
            add("菜鸟驿站(小河东)");
            add("菜鸟驿站(小河西)");
        }
    };
    private ArrayList<LatLng> latLngList = new ArrayList<LatLng>() {
        {
            add(ll_busStation);
            add(ll_canteen_south);
            add(ll_canteen_north);
            add(ll_canteen_western);
            //        add(ll_dormitory3_south);
            //       add(ll_dormitory3_north);
            add(ll_westGate);
            add(ll_yangJiao1);
            add(ll_yangJiao1_east);
            add(ll_yangJiao2);
            add(ll_yangJiao2_east);
            add(ll_yangJiao3);
            add(ll_yangShi1);
            add(ll_yangShi2);
            add(ll_playground);
            add(ll_library);
            add(ll_dormitory3);
            add(ll_dormitory2);
            add(ll_dormitory1);
            add(ll_dormitory45);
            add(ll_dormitory6);
            add(ll_dormitory78);
            add(ll_dormitory9);
            add(ll_studentCenter);
            add(ll_northGate);
            add(ll_basketballFiled);
            add(ll_teacherDorm);
            add(ll_canteen2);
            add(ll_incubationCenter);
            add(ll_reportCenter);
            add(ll_adminBuilding);
            add(ll_tennis);
            add(ll_expressEast);
            add(ll_expressWest);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try (Socket socket = new Socket(SERVER_IP, 8190)) {
                    InputStream inputStream = socket.getInputStream();
                    OutputStream outputStream = socket.getOutputStream();
                    final Scanner scanner = new Scanner(inputStream, "GBK");
                    final PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream, "GBK"), true);
                    printWriter.println(2);
                    printWriter.println(VERSION);
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
                        final Uri uri = Uri.parse(scanner.nextLine());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (VERSION < SER_VER) {
                                    alertDialog = new AlertDialog.Builder(MainActivity.this).setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    final Intent it = new Intent(Intent.ACTION_VIEW, uri);
                                                    MainActivity.this.startActivity(it);
                                                }
                                            }).start();
                                        }
                                    }).setNegativeButton("稍后更新", null)
                                            .setTitle("有新版更新").setMessage("当前版本: " + new String(VER_STR) + "\n最新版本: " + new String(SER_VER_STR) + "\n\n" + msg)
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
                                                    runOnUiThread(new Runnable() {
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
                                }
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("尚未开启GPS");
            builder.setMessage("是否开启GPS以获得更精准的定位？");
            builder.setPositiveButton("开启", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        }
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        for (int i = 0; i < 5; i++) {
            switch (i) {
                case 0:
                    VER_STR[i] = (char) (MainActivity.VERSION / 100 % 10 + '0');
                    break;
                case 2:
                    VER_STR[i] = (char) (MainActivity.VERSION / 10 % 10 + '0');
                    break;
                case 4:
                    VER_STR[i] = (char) (MainActivity.VERSION % 10 + '0');
                    break;
                case 1:
                case 3:
                    VER_STR[i] = '.';
                    break;
            }
        }

            /*---------------------------------------------------------------------*/
        // 实例化传感器管理者
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // 初始化加速度传感器
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // 初始化地磁场传感器
        magnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        baiduMapInit();
        initListener();
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        mNaviHelper = BikeNavigateHelper.getInstance();
        mNaviHelper.initNaviEngine(this, new IBEngineInitListener() {
            @Override
            public void engineInitSuccess() {
            }

            @Override
            public void engineInitFail() {
            }
        });
        if (baiduMap.getMapType() == BaiduMap.MAP_TYPE_NORMAL) textBuilder(Color.BLACK);
        else textBuilder(Color.CYAN);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void initListener() {
        final FloatingActionButton fab_mark = (FloatingActionButton) findViewById(R.id.fab_mark);
        final FloatingActionButton fab_loc = (FloatingActionButton) findViewById(R.id.fab_loc);
        final FloatingActionButton fab_navigate = (FloatingActionButton) findViewById(R.id.fab_navigate);
        final FloatingActionButton fab_panorama = (FloatingActionButton) findViewById(R.id.fab_panorama);
        final TextView naviStop = (TextView) findViewById(R.id.naviStop);
        naviStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                naviThread.interrupt();
                if (preOverlay != null) preOverlay.removeFromMap();
                Animation animation = new AlphaAnimation(1.0f, 0.0f);
                animation.setDuration(200);
                findViewById(R.id.naviView).startAnimation(animation);
                findViewById(R.id.naviView).setVisibility(View.INVISIBLE);
                baiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null));
                preOverlay.removeFromMap();
            }
        });
        fab_panorama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preMarker != null) {
                    Intent intent = new Intent(MainActivity.this, Panorama.class);
                    intent.putExtra("Latitude", preMarker.getPosition().latitude);
                    intent.putExtra("Longitude", preMarker.getPosition().longitude);
                    intent.putExtra("title", latLngList.indexOf(preMarker.getPosition()));
                    startActivity(intent);
                }
            }
        });
        fab_loc.setOnClickListener(new View.OnClickListener() {
            int flag = 0;

            @Override
            public void onClick(View view) {
                LatLng south;
                LatLng north;
                switch (flag) {
                    case 0:
                        baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                        // MainActivity.this.onMapLoaded();
                        fab_loc.setImageResource(R.drawable.ic_action_normalmap);
                        Snackbar.make(findViewById(R.id.bmapView), "已切换到卫星模式", Snackbar.LENGTH_SHORT).show();
                        if (fab_mark.getBackgroundTintList() == ColorStateList.valueOf(Color.parseColor("#F97298"))
                                || fab_mark.getBackgroundTintList() == ColorStateList.valueOf(Color.WHITE)) {
                            textDestroy();
                            textBuilder(Color.CYAN);
                        }
                        south = new LatLng(32.342485058070636, 119.39612090567593);
                        north = new LatLng(32.333744788436164, 119.41228142180043);
                        baiduMap.setMapStatusLimits(new LatLngBounds.Builder()
                                .include(south).include(north).build());
                        flag = 1;
                        break;
                    case 1:
                        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                        //MainActivity.this.onMapLoaded();
                        fab_loc.setImageResource(R.drawable.ic_action_satellite);
                        Snackbar.make(findViewById(R.id.bmapView), "已切换到地图模式", Snackbar.LENGTH_SHORT).show();
                        if (fab_mark.getBackgroundTintList() == ColorStateList.valueOf(Color.parseColor("#F97298"))
                                || fab_mark.getBackgroundTintList() == ColorStateList.valueOf(Color.WHITE)) {
                            textDestroy();
                            textBuilder(Color.BLACK);
                        }
                        south = new LatLng(32.342485058070636, 119.39612090567593);
                        north = new LatLng(32.333744788436164, 119.41228142180043);
                        baiduMap.setMapStatusLimits(new LatLngBounds.Builder()
                                .include(south).include(north).build());
                        flag = 0;
                        break;
                    default:
                }
            }
        });

        fab_mark.setOnClickListener(new View.OnClickListener() {
            int flag = 1;

            @Override
            public void onClick(View v) {
                if (flag == 1) {
                    markerBuilder();
                    fab_mark.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F97298")));
                    flag = 2;
                } else if (flag == 2) {
                    //baiduMap.clear();
                    textDestroy();
                    markerDestroy();
                    preMarker = null;
                    if (snackbar != null) snackbar.dismiss();
                    fab_mark.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, android.R.color.darker_gray)));
                    fab_panorama.setClickable(false);
                    fab_panorama.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, android.R.color.darker_gray)));
                    flag = 0;
                } else if (flag == 0) {
                    if (baiduMap.getMapType() == BaiduMap.MAP_TYPE_NORMAL) textBuilder(Color.BLACK);
                    else textBuilder(Color.CYAN);
                    fab_mark.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    flag = 1;
                }
            }
        });

        fab_navigate.setOnClickListener(new View.OnClickListener() {
            boolean flag = false;

            @Override
            public void onClick(View v) {
                if (!flag) {
                    try {
                        BDLocation location = mLocationClient.getLastKnownLocation();
                        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                        MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(ll, 18f);
                        baiduMap.animateMapStatus(update);
                        fab_navigate.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0084FF")));
                        Snackbar.make(v, "启用跟随模式", Snackbar.LENGTH_SHORT).show();
                        follow = true;
                        flag = true;
                    } catch (Exception ex) {
                        Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    fab_navigate.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    follow = false;
                    flag = false;
                }
            }
        });
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                try {


                    if (nowMarker != null) {
                        nowMarker.remove();
                        fab_panorama.setClickable(false);
                        fab_panorama.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, android.R.color.darker_gray)));
                    }
                    if (marker.getTitle() == null) {
                        marker.remove();
                        snackbar.dismiss();
                        fab_panorama.setClickable(false);
                        fab_panorama.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, android.R.color.darker_gray)));
                    } else {
                        if (preMarker != null) {
                            preMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_mark));
                        }
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_selected));
                        preMarker = marker;
                        fab_panorama.setClickable(true);
                        fab_panorama.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                        BDLocation myLocation = mLocationClient.getLastKnownLocation();
                        LatLng myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                        snackbar = Snackbar.make(findViewById(R.id.fab_mark), "选中位置 - " + marker.getTitle() + "[" + (int) DistanceUtil.getDistance(myLatLng, marker.getPosition()) + " 米]", Snackbar.LENGTH_INDEFINITE).setAction("导航", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startNavi(marker.getPosition());
                            }
                        });
                    }
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
                snackbar.show();
                return true;
            }
        });
        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                fab_panorama.setClickable(false);
                fab_panorama.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, android.R.color.darker_gray)));
                Log.d("MapClickPosition", latLng.toString());
                if (preMarker != null) {
                    preMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_mark));
                }
                if (nowMarker != null) {
                    nowMarker.remove();
                }
                BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_action_selected);
                final MarkerOptions markerOptions = new MarkerOptions().position(latLng).icon(descriptor);
                markerOptions.animateType(MarkerOptions.MarkerAnimateType.grow);


                BDLocation myLocation = mLocationClient.getLastKnownLocation();
                LatLng myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                snackbar = Snackbar.make(findViewById(R.id.fab_mark), "选中位置 - 自定义位置[" +
                        (int) DistanceUtil.getDistance(myLatLng, latLng) + " 米]", Snackbar.LENGTH_INDEFINITE).setAction("导航", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startNavi(latLng);
                    }
                });
                snackbar.show();
                Log.i("Position:", latLng.toString());
                nowMarker = (Marker) baiduMap.addOverlay(markerOptions);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                onMapClick(mapPoi.getPosition());
                BDLocation myLocation = mLocationClient.getLastKnownLocation();
                LatLng myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                snackbar.setText("选中位置 - " + mapPoi.getName() + "[" + (int) DistanceUtil.getDistance(myLatLng, mapPoi.getPosition()) + " 米]");
                Log.i("Position:", mapPoi.getPosition().toString());
                return true;
            }
        });
    }

    private void markerBuilder() {
        int i = 0;
        for (LatLng ll : latLngList) {
            BitmapDescriptor descriptor = BitmapDescriptorFactory
                    .fromResource(R.drawable.ic_action_mark);
            MarkerOptions markerOptions = new MarkerOptions().position(ll).icon(descriptor);
            markerOptions.animateType(MarkerOptions.MarkerAnimateType.grow);
            Marker marker = (Marker) baiduMap.addOverlay(markerOptions);
            marker.setTitle(titleList.get(i++));
            markerList.add(marker);
        }

    }

    private void markerDestroy() {
        for (Marker marker : markerList) {
            marker.remove();
        }
    }

    private void textBuilder(int color) {
        for (int i = 0; i < titleList.size(); i++) {
            OverlayOptions overlayOptions = new TextOptions().fontSize(25).text(titleList.get(i)).position(latLngList.get(i)).typeface(Typeface.DEFAULT_BOLD).fontColor(color);
            textList.add(baiduMap.addOverlay(overlayOptions));
        }
        OverlayOptions overlayOptions = new TextOptions().fontSize(25).text("交通银行ATM").position(new LatLng(32.337466752646826, 119.40521175743692)).typeface(Typeface.DEFAULT_BOLD).fontColor(color);
        textList.add(baiduMap.addOverlay(overlayOptions));
    }

    private void textDestroy() {
        for (Overlay text : textList) {
            text.remove();
        }
    }

    private void baiduMapInit() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        GPS_ONLY = sharedPreferences.getBoolean("gps_only", false);
        DOOR_LOC = sharedPreferences.getBoolean("door_loc", true);
        RADIUS_MODE = sharedPreferences.getBoolean("radius_mode", false);
        mMapView = (MapView) findViewById(R.id.bmapView);
        baiduMap = mMapView.getMap();
        baiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null));
        baiduMap.setOnMapLoadedCallback(this);
        baiduMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(getApplicationContext());//声明LocationClient类
        mLocationClient.registerLocationListener(myListener);//注册监听函数
        ArrayList<String> arrayList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            arrayList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            arrayList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (arrayList.size() > 0)
            ActivityCompat.requestPermissions(this, arrayList.toArray(new String[arrayList.size()]), 1);
        requestLocation();
    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setScanSpan(1000);
        option.setCoorType("bd09ll");
        if (!GPS_ONLY) option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        else option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        if (DOOR_LOC) mLocationClient.startIndoorMode();
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mMapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
        mSensorManager.unregisterListener(accelerometerListener);
        mSensorManager.unregisterListener(magneticListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(accelerometerListener);
        mSensorManager.unregisterListener(magneticListener);
    }

    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        accelerometerListener = new MySensorEventListener();
        magneticListener = new MySensorEventListener();
        mSensorManager.registerListener(accelerometerListener,
                accelerometer, Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(magneticListener, magnetic,
                Sensor.TYPE_MAGNETIC_FIELD);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.setting_menu:
                Intent settingIntent = new Intent(MainActivity.this, Setting.class);
                startActivityForResult(settingIntent, 0);
                overridePendingTransition(R.anim.layout_in_move, R.anim.layout_out_move);
                break;
            case R.id.search_btn:
                Intent searchIntent = new Intent(MainActivity.this, Search.class);
                startActivityForResult(searchIntent, 1);
                overridePendingTransition(R.anim.layout_in, R.anim.layout_out);
                break;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0)
            for (int x : grantResults) {
                if (x == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "必要权限无法获取，请授权后使用。", Toast.LENGTH_LONG).show();
                    finish();
                }
            }


    }

    private void navigateTo(BDLocation location) {
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        if (RADIUS_MODE) {
            locationBuilder.accuracy(location.getRadius());
        }
        locationBuilder.speed(location.getSpeed());
        locationBuilder.direction(location.getDirection());
        locationBuilder.satellitesNum(location.getSatelliteNumber());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);

        if (isFirstLocate || follow) {
            final LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            if (isFirstLocate) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(ll, 18f);
                        baiduMap.animateMapStatus(update);
                    }
                }, 1000);
                isFirstLocate = false;
            } else {
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
                baiduMap.animateMapStatus(update);
            }
        }

    }

    @Override
    public void onMapLoaded() {
        LatLng south = new LatLng(32.342485058070636, 119.39612090567593);
        LatLng north = new LatLng(32.333744788436164, 119.41228142180043);
        baiduMap.setMapStatusLimits(new LatLngBounds.Builder()
                .include(south).include(north).build());
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MainActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
        if (result != null) {
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                // result.getSuggestAddrInfo()
                return;
            }
            if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                nodeIndex = -1;
                route = result.getRouteLines().get(0);
                WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(baiduMap);
                baiduMap.setOnMarkerClickListener(overlay);
                routeOverlay = overlay;
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                progressDialog.dismiss();
                progressDismissed = true;
                timeOutThread.interrupt();
                if (preOverlay != null) preOverlay.removeFromMap();
                preOverlay = overlay;
                if (!naviThread.isAlive()) preOverlay.removeFromMap();
                //overlay.zoomToSpan();
            }
        }

    }

    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {
        private MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.alpha);
        }
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }


    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            navigateTo(location);
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == 1) {
                    Snackbar.make(findViewById(R.id.bmapView), "正在重新应用设置", Snackbar.LENGTH_SHORT).show();
                    if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("gps_only", false) != GPS_ONLY) {
                        LocationClientOption option = new LocationClientOption();
                        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
                        mLocationClient.setLocOption(option);
                        GPS_ONLY = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("gps_only", false);
                    }
                    if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("door_loc", false) != DOOR_LOC) {
                        mLocationClient.startIndoorMode();
                        DOOR_LOC = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("door_loc", false);
                    }
                    if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("radius_mode", false) != RADIUS_MODE) {
                        navigateTo(mLocationClient.getLastKnownLocation());
                        RADIUS_MODE = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("radius_mode", false);
                    }
                    isFirstLocate = true;
                }
                break;
            case 1:
                if (resultCode >= 0) startNavi(latLngList.get(resultCode));
                break;
        }
    }

    private void startNavi(final LatLng latLng) {
        final LatLng ll = new LatLng(mLocationClient.getLastKnownLocation().getLatitude(), mLocationClient.getLastKnownLocation().getLongitude());
        if (DistanceUtil.getDistance(ll, latLng) <= 30) {
            Toast.makeText(MainActivity.this, "就在附近，不用导航啦><", Toast.LENGTH_SHORT).show();
        } else if (DistanceUtil.getDistance(ll, latLng) >= 1000) {
            Toast.makeText(MainActivity.this, "定位异常or离学校太远啦~\n请在学校范围内使用~", Toast.LENGTH_SHORT).show();
        } else {
            final TextView naviText = (TextView) findViewById(R.id.naviText);
            if (naviThread != null) {
                naviThread.interrupt();
            }
            Animation animation = new AlphaAnimation(1f, 0.3f);
            animation.setRepeatCount(Animation.INFINITE);
            animation.setDuration(1000);
            animation.setRepeatMode(Animation.REVERSE);
            naviText.setAnimation(animation);
            naviThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        baiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.COMPASS, true, null));
                        while (!Thread.interrupted()) {
                            final LatLng ll = new LatLng(mLocationClient.getLastKnownLocation().getLatitude(), mLocationClient.getLastKnownLocation().getLongitude());
                            if ((int) DistanceUtil.getDistance(ll, latLng) <= 30) {
                                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                vibrator.vibrate(1000);
                                throw new Exception();
                            }
                            PlanNode stNode = PlanNode.withLocation(ll);
                            PlanNode enNode = PlanNode.withLocation(latLng);
                            mSearch.walkingSearch((new WalkingRoutePlanOption())
                                    .from(stNode).to(enNode));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (naviText.getVisibility() == View.VISIBLE) {
                                        naviText.setText("导航中..." + "[剩余 " + (int) DistanceUtil.getDistance(ll, latLng) + " 米]");
                                    }

                                }
                            });
                            Thread.sleep(1000);
                        }
                        if (naviThread.isInterrupted())
                            preOverlay.removeFromMap();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "已经到达目的位置\\>.</", Toast.LENGTH_SHORT).show();
                                if (preOverlay != null) preOverlay.removeFromMap();
                                Animation animation = new AlphaAnimation(1.0f, 0.0f);
                                animation.setDuration(200);
                                findViewById(R.id.naviView).startAnimation(animation);
                                findViewById(R.id.naviView).setVisibility(View.INVISIBLE);
                                baiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null));
                            }
                        });
                    }
                }
            });
            naviThread.start();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("导航");
            progressDialog.setMessage("正在发起导航");
            progressDialog.setCancelable(false);
            progressDismissed = false;
            progressDialog.show();
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (!Thread.interrupted()) {
                            if (progressDismissed) {
                                Animation animation = new AlphaAnimation(0.0f, 1.0f);
                                animation.setDuration(200);
                                findViewById(R.id.naviView).setVisibility(View.VISIBLE);
                                findViewById(R.id.naviView).startAnimation(animation);
                                progressDismissed = false;
                                throw new Exception();
                            }
                            if (timeOut) {
                                naviThread.interrupt();
                                throw new Exception();
                            }
                        }
                    } catch (Exception ex) {
                        if (timeOut) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "发起导航超时，请检查网络连接！", Toast.LENGTH_SHORT).show();
                                    baiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null));
                                    progressDialog.dismiss();
                                    timeOut = false;
                                }
                            });

                        }
                    }

                }
            });
            thread.start();
            timeOutThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                        if (!progressDismissed) {
                            timeOut = true;
                        }
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            timeOutThread.start();
        }
    }

    private class MySensorEventListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values = new float[3];
            float[] R = new float[9];
            SensorManager.getRotationMatrix(R, null, accelerometerValues,
                    magneticFieldValues);
            SensorManager.getOrientation(R, values);
            values[0] = (float) Math.toDegrees(values[0]);
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelerometerValues = event.values;
            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticFieldValues = event.values;
            }
            try {
                MyLocationData locationData = baiduMap.getLocationData();
                baiduMap.setMyLocationData(new MyLocationData.Builder().direction(values[0]).longitude(locationData.longitude).latitude(locationData.latitude).accuracy(locationData.accuracy).build());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {


        }

    }
}

