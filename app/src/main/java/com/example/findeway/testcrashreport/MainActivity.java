package com.example.findeway.testcrashreport;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.example.findeway.pulltozoomlist.BlurZoomRefreshListView;
import com.example.findeway.pulltozoomlist.PullToZoomRefreshListView;

import java.util.ArrayList;
import java.util.zip.Inflater;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends Activity{

    private BlurZoomRefreshListView listView;
    private ArrayList<String> adapterData = new ArrayList<String>();
    private ArrayAdapter<String> listAdapter;
    private ListHeaderView headerView;
    private FrameLayout mFloatingHeaderContainer;
    private int mScrollState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        initListView();
        initUI();
    }

    private void initUI()
    {
        if(mFloatingHeaderContainer == null)
        {
            mFloatingHeaderContainer = (FrameLayout) findViewById(R.id.floating_header);
        }
        if(headerView == null)
        {
            headerView = new ListHeaderView(this);
            mFloatingHeaderContainer.addView(headerView);
        }
        listView.setExtraOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                mScrollState = scrollState;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    //初始化位置
                    if(headerView.isVisible()) {
                        headerView.hide();
                    }
                } else if (firstVisibleItem < 2) {
                    //头部还可见
                    if(!headerView.isVisible()) {
                        headerView.show();
                    }
                    else {
                        headerView.changeToNormal();
                    }
                } else {
                    //头部不可见
                    if(!headerView.isVisible()) {
                        headerView.show();
                    }
                    else {
                        headerView.changeToSimple();
                    }
                }
            }
        });
    }

    protected void initListView()
    {
        listView = (BlurZoomRefreshListView) findViewById(R.id.listview);
        adapterData.add("Activity");
        adapterData.add("Activity1");
        adapterData.add("Activity2");
        adapterData.add("Activity");
        adapterData.add("Activity1");
        adapterData.add("Activity2");
        adapterData.add("Activity");
        adapterData.add("Activity1");
        adapterData.add("Activity2");
        adapterData.add("Activity");
        adapterData.add("Activity1");
        adapterData.add("Activity2");
        adapterData.add("Activity");
        adapterData.add("Activity1");
        adapterData.add("Activity2");

        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, adapterData);
        listView.setAdapter(listAdapter);
        listView.getHeaderBackground().setImageResource(R.drawable.splash01);
        listView.setHeaderBackground(((BitmapDrawable) listView.getHeaderBackground().getDrawable()).getBitmap());

        listView.setOnRefreshListener(new PullToZoomRefreshListView.OnRefreshListener() {
            @Override
            public void onPullDownToRefresh(PullToZoomRefreshListView refreshView) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapterData.add(0, "Down");
                        adapterData.add(1, "Down1");
                        adapterData.add(2, "Down2");
                        listAdapter.notifyDataSetChanged();
                        listView.onRefreshComplete();
                    }
                }, 1000);
            }

            @Override
            public void onPullUpToRefresh(PullToZoomRefreshListView refreshView) {
                if (listAdapter.getCount() < 25) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapterData.add("Up");
                            adapterData.add("Up1");
                            adapterData.add("Up2");
                            listAdapter.notifyDataSetChanged();
                            listView.onRefreshComplete();
                        }
                    }, 1000);
                } else {
                    listView.onLoadComplete();
                }
            }
        });
    }
}
