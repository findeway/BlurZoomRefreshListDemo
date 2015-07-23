package com.example.findeway.pulltozoomlist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.example.findeway.blur.FastBlur;
import com.matrixxun.pulltozoomlistsimple.PullToZoomListView;

import java.util.ArrayList;

/**
 * Created by collinzhang on 15/7/22.
 */
public class BlurZoomRefreshListView extends PullToZoomRefreshListView {

    private Context mContext;
    private static final int DEFAULT_BITMAP_ARRAY_SIZE = 4;
    private static final float MAX_BLUR_RADIUS = 25.0f;
    private Bitmap mOriginalBitmap;
    private int mHeaderResId;
    private ArrayList<Bitmap> mBlurBitmapList = new ArrayList<Bitmap>();
    private int mBlurArraySize = DEFAULT_BITMAP_ARRAY_SIZE;

    public BlurZoomRefreshListView(Context paramContext) {
        this(paramContext, null);
    }

    public BlurZoomRefreshListView(Context paramContext, AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, 0);
    }

    public BlurZoomRefreshListView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        mContext = paramContext;
        setBlurArraySize(DEFAULT_BITMAP_ARRAY_SIZE);
    }

    public void setBlurArraySize(int blurArraySize)
    {
        mBlurArraySize = blurArraySize;
        initHeaderBlur();
    }

    private void initHeaderBlur()
    {
        getHeaderView().setScaleType(ImageView.ScaleType.CENTER_CROP);
        setOnScaleListener(new PullToZoomListView.OnScaleListener() {
            @Override
            public void onScaling(float scale) {
                for (int index = mBlurArraySize; index >= 0; index--) {
                    if (scale >= 1.0f / mBlurArraySize * index) {
                        if (mBlurBitmapList != null && mBlurBitmapList.size() > index) {
                            getHeaderView().setImageBitmap(mBlurBitmapList.get(index));
                            return;
                        }
                    }
                }
                if (mBlurBitmapList != null && mBlurBitmapList.size() > 0) {
                    getHeaderView().setImageBitmap(mBlurBitmapList.get(0));
                }
            }

            @Override
            public void onEndScale() {
                if (mBlurBitmapList != null && mBlurBitmapList.size() > 0) {
                    getHeaderView().setImageBitmap(mBlurBitmapList.get(0));
                }
            }
        });
    }

    public void setHeaderImage(Bitmap bitmap)
    {
        mOriginalBitmap = bitmap;
        mHeaderResId = 0;
        getHeaderView().setImageBitmap(mOriginalBitmap);
        mBlurBitmapList.clear();
        new AsyncTask<Void,Void,Bitmap>(){
            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap firstBlurBitmap = FastBlur.getBlurBitmap(mContext, mOriginalBitmap, MAX_BLUR_RADIUS);
                mBlurBitmapList.add(firstBlurBitmap);
                return firstBlurBitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                getHeaderView().setImageBitmap(bitmap);
                preloadBlurBitmapList();
            }
        }.execute();
    }

    public void setHeaderImage(int resId)
    {
        mHeaderResId = resId;
        getHeaderView().setImageResource(mHeaderResId);
        mBlurBitmapList.clear();
        new AsyncTask<Void,Void,Bitmap>(){
            @Override
            protected Bitmap doInBackground(Void... params) {
                mOriginalBitmap = BitmapFactory.decodeResource(mContext.getResources(), mHeaderResId);
                Bitmap firstBlurBitmap = FastBlur.getBlurBitmap(mContext, mOriginalBitmap, MAX_BLUR_RADIUS);
                mBlurBitmapList.add(firstBlurBitmap);
                return firstBlurBitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                getHeaderView().setImageBitmap(bitmap);
                preloadBlurBitmapList();
            }
        }.execute();
    }

    private void preloadBlurBitmapList()
    {
        new AsyncTask<Void,Void,Boolean>()
        {
            @Override
            protected Boolean doInBackground(Void... params) {
                if(mOriginalBitmap != null)
                {
                    for(int index = mBlurArraySize - 1; index >= 0; index--)
                    {
                        float blurValue = MAX_BLUR_RADIUS * index / mBlurArraySize;
                        mBlurBitmapList.add(FastBlur.getBlurBitmap(mContext,mOriginalBitmap,blurValue));
                    }
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean bResult) {
                super.onPostExecute(bResult);
            }
        }.execute();
    }
}
