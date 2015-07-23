package com.example.findeway.pulltozoomlist;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by collinzhang on 15/7/21.
 */
public class FooterLoadingView extends FrameLayout implements ILoadingView {

    public FooterLoadingView(Context context) {
        super(context);
    }

    public FooterLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FooterLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onLoadComplete() {

    }
}
