package com.example.tyq19.lbs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.panoramaview.PanoramaView;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;

/**
 * Created by tyq19 on 2017/4/8.
 *
 */
public class Panorama extends AppCompatActivity {
    private ArrayList<String> titleList = new ArrayList<String>() {
        {
            add("公交站");
            add("食堂南门");
            add("食堂东门");
            add("食堂西门");
//          add("宿舍(三号楼)南门");
//          add("宿舍(三号楼)北门");
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBMapManager();
        setContentView(R.layout.panorama);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean highDefinition = sharedPreferences.getBoolean("pano_def",false);
        Intent intent = getIntent();
        setTitle(titleList.get(intent.getIntExtra("title",-1)));
        LatLng latLng = new LatLng(intent.getDoubleExtra("Latitude",0),intent.getDoubleExtra("Longitude",0));
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        PanoramaView panoramaView = (PanoramaView)findViewById(R.id.panorama);
        panoramaView.setPanorama(latLng.longitude,latLng.latitude);
        if(highDefinition)panoramaView.setPanoramaImageLevel(PanoramaView.ImageDefinition.ImageDefinitionHigh);
        else panoramaView.setPanoramaImageLevel(PanoramaView.ImageDefinition.ImageDefinitionMiddle);
    }
    private void initBMapManager() {
        PanoApplication app = (PanoApplication) this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(app);
            app.mBMapManager.init(new PanoApplication.MyGeneralListener());
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}