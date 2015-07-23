package com.example.findeway.pulltozoomlist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;

import com.matrixxun.pulltozoomlistsimple.PullToZoomListView;

/**
 * Created by collinzhang on 15/7/21.
 */
public class PullToZoomRefreshListView extends PullToZoomListView {
    private OnRefreshListener mOnRefreshListener;
    private OnScrollListener mOnScrollListener;

    private Mode mMode = Mode.BOTH;
    private State mState = State.NONE;                     //标记当前状态

    private int mFirstVisibleItem = 0;
    private int mVisibleItemCount;
    private int mScrollState;
    private int mStartY = 0;                                //标记按下时的坐标
    private boolean mIsBeingDragged = false;                //标记是否在列表顶端被按下
    private boolean mNeedLoadMore = false;                  //标记是否已经滚动到底部需要加载更多

    private FooterLoadingView  mFooterLoadingView = null;

    public PullToZoomRefreshListView(Context paramContext) {
        this(paramContext,null);
    }

    public PullToZoomRefreshListView(Context paramContext, AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, 0);
    }

    public PullToZoomRefreshListView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(mFooterLoadingView == null) {
            mFooterLoadingView = getFooterLoadingView();
            if (mFooterLoadingView != null) {
                mFooterLoadingView.setVisibility(View.GONE);
                addFooterView(mFooterLoadingView);
            }
        }
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {

        this.mOnRefreshListener = onRefreshListener;
    }

    public void setExtraOnScrollListener(OnScrollListener onScrollListener) {
        this.mOnScrollListener = onScrollListener;
    }

    public void setMode(Mode mode) {
        this.mMode = mode;
    }

    public void setState(State state) {
        this.mState = state;
        refreshState();
    }

    public static enum Mode {

        /**
         * Disable all Pull-to-Refresh gesture and Refreshing handling
         */
        DISABLED(0x0),

        /**
         * Only allow the user to Pull from the start of the Refreshable View to
         * refresh. The start is either the Top or Left, depending on the
         * scrolling direction.
         */
        PULL_FROM_START(0x1),

        /**
         * Only allow the user to Pull from the end of the Refreshable View to
         * refresh. The start is either the Bottom or Right, depending on the
         * scrolling direction.
         */
        PULL_FROM_END(0x2),

        /**
         * Allow the user to both Pull from the start, from the end to refresh.
         */
        BOTH(0x3);

        /**
         * Maps an int to a specific mode. This is needed when saving state, or
         * inflating the view from XML where the mode is given through a attr
         * int.
         *
         * @param modeInt - int to map a Mode to
         * @return Mode that modeInt maps to, or PULL_FROM_START by default.
         */
        static Mode getInstance(final int modeInt) {
            for (Mode value : Mode.values()) {
                if (modeInt == value.getIntValue()) {
                    return value;
                }
            }
            // If not, return default
            return getDefault();
        }

        static Mode getDefault() {
            return PULL_FROM_START;
        }

        private int mIntValue;

        // The modeInt values need to match those from attrs.xml
        Mode(int modeInt) {
            mIntValue = modeInt;
        }

        /**
         * @return true if the mode permits Pull-to-Refresh
         */
        boolean permitsPullToRefresh() {
            return !(this == DISABLED);
        }

        /**
         * @return true if this mode wants the Loading Layout Footer to be shown
         */
        public boolean showFooterLoadingLayout() {
            return this == PULL_FROM_END || this == BOTH;
        }

        int getIntValue() {
            return mIntValue;
        }
    }

    public static enum State {

        /**
         * When the UI is in a state which means that user is not interacting
         * with the Pull-to-Refresh function.
         */
        NONE(0x0),

        /**
         * When the UI is being pulled by the user, but has not been pulled far
         * enough so that it refreshes when released.
         */
        PULL(0x1),

        /**
         * When the UI is being pulled by the user, and <strong>has</strong>
         * been pulled far enough so that it will refresh when released.
         */
        RELEASE(0x2),

        /**
         * 下拉刷新
         */
        REFRESHING(0x3),

        /**
         * 上拉加载
         */
        LOADING(0x4);

        static State getInstance(final int stateInt) {
            for (State value : State.values()) {
                if (stateInt == value.getIntValue()) {
                    return value;
                }
            }

            // If not, return default
            return NONE;
        }

        private int mIntValue;

        State(int intValue) {
            mIntValue = intValue;
        }

        int getIntValue() {
            return mIntValue;
        }
    }

    public static interface OnRefreshListener {
        /**
         * onPullDownToRefresh will be called only when the user has Pulled from
         * the start, and released.
         */
        public void onPullDownToRefresh(final PullToZoomRefreshListView refreshView);

        /**
         * onPullUpToRefresh will be called only when the user has Pulled from
         * the end, and released.
         */
        public void onPullUpToRefresh(final PullToZoomRefreshListView refreshView);

    }

    @Override
    public void onScroll(AbsListView paramAbsListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        super.onScroll(paramAbsListView, firstVisibleItem, visibleItemCount, totalItemCount);
        if(mOnScrollListener != null) {
            mOnScrollListener.onScroll(paramAbsListView,firstVisibleItem,visibleItemCount,totalItemCount);
        }
        mFirstVisibleItem = firstVisibleItem;
        mVisibleItemCount = visibleItemCount;
        //防止重复加载
        if(mState == State.REFRESHING || mState == State.LOADING)
        {
            return;
        }
        mNeedLoadMore = getLastVisiblePosition() == totalItemCount - 1;
    }

    @Override
    public void onScrollStateChanged(AbsListView paramAbsListView, int scrollState) {
        super.onScrollStateChanged(paramAbsListView, scrollState);
        if(mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(paramAbsListView,scrollState);
        }
        mScrollState = scrollState;
        if(mNeedLoadMore && mScrollState == SCROLL_STATE_IDLE)
        {
            setState(State.LOADING);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent paramMotionEvent) {
        boolean result = super.onTouchEvent(paramMotionEvent);
        switch (paramMotionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mFirstVisibleItem == 0) {
                    mIsBeingDragged = true;
                    mStartY = (int)paramMotionEvent.getY();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                onMove(paramMotionEvent);
                break;
            case MotionEvent.ACTION_UP:
                if(mState == State.RELEASE)
                {
                    setState(State.REFRESHING);
                }
                break;
        }
        return result;
    }

    protected int getRefreshHeight()
    {
        return getHeaderHeight()/3;
    }
    /**
     * 拖拽移动过程中的操作
     * @param event
     */
    protected void onMove(MotionEvent event)
    {
        if(!mIsBeingDragged)
        {
            return;
        }
        int curY = (int)event.getY();
        int movedDistance = curY - mStartY;
        switch (mState) {
            case NONE:
                if(movedDistance > 0)
                {
                    setState(State.PULL);
                }
                break;
            case PULL:
                if(movedDistance >= getRefreshHeight())
                {
                    setState(State.RELEASE);
                }
                break;
            case RELEASE:
                if(movedDistance < getRefreshHeight())
                {
                    setState(State.PULL);
                }
                else if(movedDistance <= 0)
                {
                    mIsBeingDragged = false;
                    setState(State.NONE);
                }
                break;
            case REFRESHING:
                break;
        }
    }

    private void refreshState()
    {
        switch(mState) {
            case NONE:
                onReset();
                break;
            case PULL:
                onPull();
                break;
            case RELEASE:
                onRelease();
                break;
            case REFRESHING:
                onRefreshing();
                break;
            case LOADING:
                onLoading();
                break;
        }
    }

    /**
     * 默认没有footerview，需要重写此方法进行定制
     * @return
     */
    protected FooterLoadingView getFooterLoadingView()
    {
        return null;
    }

    public void onRefreshComplete()
    {
        setState(State.NONE);
    }

    public void onLoadComplete()
    {
        setState(State.NONE);
    }

    protected void onReset()
    {
        if(mFooterLoadingView != null)
        {
            mFooterLoadingView.onLoadComplete();
        }
    }

    protected void onPull()
    {

    }

    protected void onRelease()
    {

    }

    protected void onRefreshing()
    {
        if (mOnRefreshListener != null) {
            mOnRefreshListener.onPullDownToRefresh(this);
        }
    }

    protected void onLoading()
    {
        if(mFooterLoadingView != null)
        {
            mFooterLoadingView.onLoading();
        }
        if (mOnRefreshListener != null) {
            mOnRefreshListener.onPullUpToRefresh(this);
        }
    }
}
