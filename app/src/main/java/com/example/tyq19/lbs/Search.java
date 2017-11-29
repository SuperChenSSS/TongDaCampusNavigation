package com.example.tyq19.lbs;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by tyq19 on 2017/3/17.
 */

public class Search extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private ListView listView;
    private SearchView searchView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> titleList = new ArrayList<String>() {
        {
            add("公交站");
            add("食堂南门");
            add("食堂东门");
            add("食堂西门");
            //add("宿舍(三号楼)南门");
            //add("宿舍(三号楼)北门");
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
        setContentView(R.layout.search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        searchView = (SearchView) findViewById(R.id.search);
        searchView.setIconifiedByDefault(true);
        searchView.onActionViewExpanded();
        searchView.setOnQueryTextListener(this);
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        listView = (ListView) findViewById(R.id.searchList);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titleList);
        listView.setAdapter(arrayAdapter);
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Search.this.setResult(position);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                this.setResult(-1);
                finish();
                overridePendingTransition(R.anim.layout_in , R.anim.layout_out);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        this.setResult(-1);
        finish();
        overridePendingTransition(R.anim.layout_in , R.anim.layout_out);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        for (String text : titleList) {
            if (text.equals(query)) {
                this.setResult(titleList.indexOf(text));
                finish();
            }
        }
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titleList);
            listView.setAdapter(arrayAdapter);
        } else {
            switch (newText) {
                case "教室":
                case "教学":
                case "教学楼":
                    arrayAdapter.getFilter().filter("扬教");
                    break;
                case "实验":
                case "实验楼":
                case "实验室":
                    arrayAdapter.getFilter().filter("扬实");
                    break;
                default:
                    arrayAdapter.getFilter().filter(newText);
                    break;
            }
        }
        return true;
    }
}