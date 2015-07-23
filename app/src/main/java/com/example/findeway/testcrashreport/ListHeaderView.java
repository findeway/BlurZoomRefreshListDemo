package com.example.findeway.testcrashreport;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by collinzhang on 15/7/22.
 */
public class ListHeaderView extends FrameLayout {

    private Context mContext;
    private LinearLayout mNormalView;
    private LinearLayout mSimpleView;
    private ImageView mBKImage;
    private HeaderViewState mState = HeaderViewState.None;

    public ListHeaderView(Context context) {
        this(context, null);
    }

    public ListHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListHeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initUI();
    }

    protected static enum HeaderViewState{
        None,Normal,Simple,Hiden
    }

    private void initUI()
    {
        LayoutInflater.from(mContext).inflate(R.layout.layout_list_header, this);
        mNormalView = (LinearLayout) findViewById(R.id.normal_view);
        mSimpleView = (LinearLayout) findViewById(R.id.simple_view);
        mBKImage = (ImageView) findViewById(R.id.bg_image);
    }

    public void hide()
    {
        if(mState != HeaderViewState.Hiden) {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
            animation.setDuration(0);
            startAnimation(animation);
        }
        mState = HeaderViewState.Hiden;
    }

    public void show()
    {
        if(mState == HeaderViewState.Hiden) {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
            startAnimation(animation);
            Animation simpleAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
            simpleAnimation.setDuration(0);
            mSimpleView.startAnimation(simpleAnimation);
        }
        mState = HeaderViewState.Normal;
    }

    public boolean isVisible()
    {
        return mState != HeaderViewState.Hiden;
    }

    public ScaleAnimation getScaleHeightAnimation(float start,float end)
    {
        ScaleAnimation animation = new ScaleAnimation(1.0f,1.0f,start,end);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        animation.setFillEnabled(true);
        animation.setFillBefore(true);
        return animation;
    }

    public void changeToSimple()
    {
        if(mState != HeaderViewState.Normal)
        {
            return;
        }
        mState = HeaderViewState.Simple;

        Animation normalAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        Animation simpleAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        Animation scaleAnimation = getScaleHeightAnimation(1.0f, 0.5f);

        mNormalView.startAnimation(normalAnimation);
        mSimpleView.startAnimation(simpleAnimation);
        mBKImage.startAnimation(scaleAnimation);
    }

    public void changeToNormal()
    {
        if(mState != HeaderViewState.Simple)
        {
            return;
        }
        mState = HeaderViewState.Normal;

        Animation normalAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        Animation simpleAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        Animation scaleAnimation = getScaleHeightAnimation(0.5f, 1.0f);

        mNormalView.startAnimation(normalAnimation);
        mSimpleView.startAnimation(simpleAnimation);
        mBKImage.startAnimation(scaleAnimation);
    }
}
