package com.example.findeway.testcrashreport;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import com.example.findeway.pulltozoomlist.FooterLoadingView;
import com.example.findeway.pulltozoomlist.PullToZoomRefreshListView;

/**
 * Created by collinzhang on 15/7/21.
 */
public class MTPullToZoomRefreshView extends PullToZoomRefreshListView {

    private Context mContext;

    public MTPullToZoomRefreshView(Context paramContext) {
        this(paramContext, null);
    }

    public MTPullToZoomRefreshView(Context paramContext, AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, 0);
    }

    public MTPullToZoomRefreshView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        mContext = paramContext;
    }

    @Override
    protected FooterLoadingView getFooterLoadingView() {
        return new MTFooterLoadingView(mContext);
    }

    public class MTFooterLoadingView extends FooterLoadingView{

        private TextView loadingText;

        public MTFooterLoadingView(Context context) {
            this(context, null);
        }

        public MTFooterLoadingView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public MTFooterLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        public void init()
        {
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
            setLayoutParams(layoutParams);

            loadingText = new TextView(mContext);
            AbsListView.LayoutParams textLayoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
            loadingText.setLayoutParams(textLayoutParams);
            loadingText.setGravity(Gravity.CENTER);
            loadingText.setText("正在加载...");
            addView(loadingText);
        }

        @Override
        public void onLoading() {
            super.onLoading();
            if(getVisibility() != View.VISIBLE) {
                setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onLoadComplete() {
            super.onLoadComplete();
            if(getVisibility() == View.VISIBLE) {
                setVisibility(View.GONE);
            }
        }
    }
}
