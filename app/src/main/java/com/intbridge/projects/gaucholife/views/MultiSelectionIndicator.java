package com.intbridge.projects.gaucholife.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intbridge.projects.gaucholife.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Derek on 8/8/2015.
 */
public class MultiSelectionIndicator extends HorizontalScrollView {
    public static final int DEF_VALUE_TAB_TEXT_ALPHA = 150;
    private static final int[] ANDROID_ATTRS = new int[]{
            android.R.attr.textColorPrimary,
            android.R.attr.padding,
            android.R.attr.paddingLeft,
            android.R.attr.paddingRight,
    };

    //These indexes must be related with the ATTR array above
    private static final int TEXT_COLOR_PRIMARY = 0;
    private static final int PADDING_INDEX = 1;
    private static final int PADDING_LEFT_INDEX = 2;
    private static final int PADDING_RIGHT_INDEX = 3;

    private LinearLayout mTabsContainer;
    private LinearLayout.LayoutParams mTabLayoutParams;

    public ViewPager.OnPageChangeListener mDelegatePageListener;
    private ViewPager mPager;

    private int mTabCount;

    private int mCurrentPosition = 0;
    private float mCurrentPositionOffset = 0f;

    private Paint mRectPaint;
    private Paint mDividerPaint;

    private int mIndicatorColor;
    private int mIndicatorHeight = 2;

    private int mUnderlineHeight = 0;
    private int mUnderlineColor;

    private int mDividerWidth = 0;
    private int mDividerPadding = 0;
    private int mDividerColor;

    private int mTabPadding = 12;
    private int mTabTextSize = 18;
    private ColorStateList mTabTextColor = null;

    private int mPaddingLeft = 0;
    private int mPaddingRight = 0;

    private boolean isExpandTabs = true;
    private boolean isCustomTabs;
    private boolean isPaddingMiddle = true;
    private boolean isTabTextAllCaps = false;

    private Typeface mTabTextTypeface = null;
    private int mTabTextTypefaceStyle = Typeface.BOLD;

    private int mScrollOffset;
    private int mLastScrollX = 0;

    //private int mTabBackgroundResId = R.drawable.psts_background_tab;

    private List<String> mDatas = Arrays.asList("stub1", "stub2", "stub3");


    public MultiSelectionIndicator(Context context) {
        this(context, null);
    }

    public MultiSelectionIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiSelectionIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFillViewport(true);
        setWillNotDraw(false);
        mTabsContainer = new LinearLayout(context);
        mTabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        addView(mTabsContainer);

        mRectPaint = new Paint();
        mRectPaint.setAntiAlias(true);
        mRectPaint.setStyle(Paint.Style.FILL);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mScrollOffset, dm);
        mIndicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mIndicatorHeight, dm);
        mUnderlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mUnderlineHeight, dm);
        mDividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mDividerPadding, dm);
        mTabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mTabPadding, dm);
        mDividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mDividerWidth, dm);
        mTabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTabTextSize, dm);

        mDividerPaint = new Paint();
        mDividerPaint.setAntiAlias(true);
        mDividerPaint.setStrokeWidth(mDividerWidth);

        // get system attrs for container
        TypedArray a = context.obtainStyledAttributes(attrs, ANDROID_ATTRS);

        int textPrimaryColor = a.getColor(TEXT_COLOR_PRIMARY, getResources().getColor(android.R.color.white));
        mUnderlineColor = textPrimaryColor;
        mDividerColor = textPrimaryColor;
        mIndicatorColor = textPrimaryColor;
        int padding = a.getDimensionPixelSize(PADDING_INDEX, 0);
        mPaddingLeft = padding > 0 ? padding : a.getDimensionPixelSize(PADDING_LEFT_INDEX, 0);
        mPaddingRight = padding > 0 ? padding : a.getDimensionPixelSize(PADDING_RIGHT_INDEX, 0);
        a.recycle();

        //mPaddingLeft = getWidth()/2;
        String tabTextTypefaceName = "sans-serif";
        // Use Roboto Medium as the default typeface from API 21 onwards
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tabTextTypefaceName = "sans-serif-medium";
            mTabTextTypefaceStyle = Typeface.NORMAL;
        }

        // get custom attrs for tabs and container
//        a = context.obtainStyledAttributes(attrs, R.styleable.MultiSelectionIndicator);
//        mIndicatorColor = a.getColor(R.styleable.MultiSelectionIndicator_pstsIndicatorColor, mIndicatorColor);
//        mIndicatorHeight = a.getDimensionPixelSize(R.styleable.MultiSelectionIndicator_pstsIndicatorHeight, mIndicatorHeight);
//        mUnderlineColor = a.getColor(R.styleable.MultiSelectionIndicator_pstsUnderlineColor, mUnderlineColor);
//        mUnderlineHeight = a.getDimensionPixelSize(R.styleable.MultiSelectionIndicator_pstsUnderlineHeight, mUnderlineHeight);
//        mDividerColor = a.getColor(R.styleable.MultiSelectionIndicator_pstsDividerColor, mDividerColor);
//        mDividerWidth = a.getDimensionPixelSize(R.styleable.MultiSelectionIndicator_pstsDividerWidth, mDividerWidth);
//        mDividerPadding = a.getDimensionPixelSize(R.styleable.MultiSelectionIndicator_pstsDividerPadding, mDividerPadding);
//        isExpandTabs = a.getBoolean(R.styleable.MultiSelectionIndicator_pstsShouldExpand, isExpandTabs);
//        mScrollOffset = a.getDimensionPixelSize(R.styleable.MultiSelectionIndicator_pstsScrollOffset, mScrollOffset);
//        isPaddingMiddle = a.getBoolean(R.styleable.MultiSelectionIndicator_pstsPaddingMiddle, isPaddingMiddle);
//        mTabPadding = a.getDimensionPixelSize(R.styleable.MultiSelectionIndicator_pstsTabPaddingLeftRight, mTabPadding);
//        mTabBackgroundResId = a.getResourceId(R.styleable.MultiSelectionIndicator_pstsTabBackground, mTabBackgroundResId);
//        mTabTextSize = a.getDimensionPixelSize(R.styleable.MultiSelectionIndicator_pstsTabTextSize, mTabTextSize);
//        mTabTextColor = a.hasValue(R.styleable.MultiSelectionIndicator_pstsTabTextColor) ? a.getColorStateList(R.styleable.MultiSelectionIndicator_pstsTabTextColor) : null;
//        mTabTextTypefaceStyle = a.getInt(R.styleable.MultiSelectionIndicator_pstsTabTextStyle, mTabTextTypefaceStyle);
//        isTabTextAllCaps = a.getBoolean(R.styleable.MultiSelectionIndicator_pstsTabTextAllCaps, isTabTextAllCaps);
//        int tabTextAlpha = a.getInt(R.styleable.MultiSelectionIndicator_pstsTabTextAlpha, DEF_VALUE_TAB_TEXT_ALPHA);
//        String fontFamily = a.getString(R.styleable.MultiSelectionIndicator_pstsTabTextFontFamily);
//        a.recycle();
        int tabTextAlpha = 128;
        String fontFamily = "sans-serif";
        //Tab text color selector
        textPrimaryColor = Color.parseColor("#ffffff");
        if (mTabTextColor == null) {
            mTabTextColor = createColorStateList(
                    textPrimaryColor,
                    textPrimaryColor,
                    Color.argb(tabTextAlpha,
                            Color.red(textPrimaryColor),
                            Color.green(textPrimaryColor),
                            Color.blue(textPrimaryColor)));
        }

        //Tab text typeface and style
        if (fontFamily != null) {
            tabTextTypefaceName = fontFamily;
        }
        mTabTextTypeface = Typeface.create(tabTextTypefaceName, mTabTextTypefaceStyle);

        //Bottom padding for the tabs container parent view to show indicator and underline
        setTabsContainerParentViewPaddings();

        //Configure tab's container LayoutParams for either equal divided space or just wrap tabs
        mTabLayoutParams = isExpandTabs ?
                new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f) :
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        
        mTabCount = mDatas.size();
        View tabView;
        for (int i = 0; i < mTabCount; i++) {

            tabView = LayoutInflater.from(getContext()).inflate(R.layout.tab_multiselectionindicator, this, false);

            String title = mDatas.get(i);
            addTab(i, title, tabView);
        }

        updateTabStyles();
    }
    
    public void setTabItemTitles(List<String> data){
        this.mDatas = data;
        mTabsContainer.removeAllViews();
        mTabCount = mDatas.size();
        View tabView;
        for (int i = 0; i < mTabCount; i++) {

            tabView = LayoutInflater.from(getContext()).inflate(R.layout.tab_multiselectionindicator, this, false);

            String title = mDatas.get(i);
            addTab(i, title, tabView);
        }

        updateTabStyles();
    }

    private void setTabsContainerParentViewPaddings() {
        int bottomMargin = mIndicatorHeight >= mUnderlineHeight ? mIndicatorHeight : mUnderlineHeight;
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), bottomMargin);
    }

//    public void setViewPager(ViewPager pager) {
//        this.mPager = pager;
//        if (pager.getAdapter() == null) {
//            throw new IllegalStateException("ViewPager does not have adapter instance.");
//        }
//
//        isCustomTabs = pager.getAdapter() instanceof PagerSlidingTabStrip.CustomTabProvider;
//        pager.setOnPageChangeListener(mPageListener);
//        pager.getAdapter().registerDataSetObserver(mAdapterObserver);
//        mAdapterObserver.setAttached(true);
//        notifyDataSetChanged();
//    }

//    public void notifyDataSetChanged() {
//        mTabsContainer.removeAllViews();
//        mTabCount = mPager.getAdapter().getCount();
//        View tabView;
//        for (int i = 0; i < mTabCount; i++) {
//            
//            tabView = LayoutInflater.from(getContext()).inflate(R.layout.psts_tab, this, false);
//
//            CharSequence title = mPager.getAdapter().getPageTitle(i);
//            addTab(i, title, tabView);
//        }
//
//        updateTabStyles();
//    }

    private void addTab(final int position, String title, View tabView) {
        TextView textView = (TextView) tabView.findViewById(R.id.tab_multiselectionindicator_title);
        if (textView != null) {
            if (title != null) textView.setText(title);
        }
        // not working?
        //textView.setAllCaps(false);
        //Log.e("The title when add: ", title);
        tabView.setFocusable(true);
        tabView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentPosition != position){
                    View tab = mTabsContainer.getChildAt(mCurrentPosition);
                    unSelect(tab);
                    mCurrentPosition = position;
                    tab = mTabsContainer.getChildAt(mCurrentPosition);
                    select(tab);
                    callbackManager.notifyChange(mCurrentPosition);
                    //invalidate();
                }
//                if (mPager.getCurrentItem() != position) {
//                    View tab = mTabsContainer.getChildAt(mPager.getCurrentItem());
//                    unSelect(tab);
//                    mPager.setCurrentItem(position);
//                } else if (mTabReselectedListener != null) {
//                    mTabReselectedListener.onTabReselected(position);
//                }
            }
        });

        mTabsContainer.addView(tabView, position, mTabLayoutParams);
    }

    //public abstract void notifyChangeToHost();

    private void updateTabStyles() {
        //Log.e("updateTabStyles: ","start");
        for (int i = 0; i < mTabCount; i++) {
            View v = mTabsContainer.getChildAt(i);
            //v.setBackgroundResource(mTabBackgroundResId);
            v.setPadding(mTabPadding, v.getPaddingTop(), mTabPadding, v.getPaddingBottom());
            TextView tab_title = (TextView) v.findViewById(R.id.tab_multiselectionindicator_title);
            if (tab_title != null) {
                tab_title.setTextColor(mTabTextColor);
                tab_title.setTypeface(mTabTextTypeface, mTabTextTypefaceStyle);
                tab_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTabTextSize);
                // setAllCaps() is only available from API 14, so the upper case is made manually if we are on a
                // pre-ICS-build
                if (isTabTextAllCaps) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        tab_title.setAllCaps(true);
                    } else {
                        tab_title.setText(tab_title.getText().toString().toUpperCase(getResources().getConfiguration().locale));
                    }
                }
            }
        }
    }

    private void scrollToChild(int position, int offset) {
        //Log.e("scrollToChild: ","start");
        if (mTabCount == 0) {
            return;
        }
        View v = mTabsContainer.getChildAt(position);
        int newScrollX = mTabsContainer.getChildAt(position).getLeft() + offset;
        if (position > 0 || offset > 0) {
            //Half screen offset.
            //- Either tabs start at the middle of the view scrolling straight away
            //- Or tabs start at the begging (no padding) scrolling when indicator gets
            //  to the middle of the view width
            newScrollX -= mScrollOffset;
            Pair<Float, Float> lines = getIndicatorCoordinates();
            newScrollX += ((lines.second - lines.first) / 2);
        }

        if (newScrollX != mLastScrollX) {
            mLastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }
    }

    private Pair<Float, Float> getIndicatorCoordinates() {
        //Log.e("getIndicatorCoordi: ","start");
        // default: line below current tab
        View currentTab = mTabsContainer.getChildAt(mCurrentPosition);
        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();
        // if there is an offset, start interpolating left and right coordinates between current and next tab
//        if (mCurrentPositionOffset > 0f && mCurrentPosition < mTabCount - 1) {
//            View nextTab = mTabsContainer.getChildAt(mCurrentPosition + 1);
//            final float nextTabLeft = nextTab.getLeft();
//            final float nextTabRight = nextTab.getRight();
//            lineLeft = (mCurrentPositionOffset * nextTabLeft + (1f - mCurrentPositionOffset) * lineLeft);
//            lineRight = (mCurrentPositionOffset * nextTabRight + (1f - mCurrentPositionOffset) * lineRight);
//        }

        return new Pair<Float, Float>(lineLeft, lineRight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //Log.e("onLayout: ","start");
        if (isPaddingMiddle || mPaddingLeft > 0 || mPaddingRight > 0) {
            int width;
            if (isPaddingMiddle) {
                width = getWidth();
            } else {
                // Account for manually set padding for offsetting tab start and end positions.
                width = getWidth() - mPaddingLeft - mPaddingRight;
            }

            //Make sure tabContainer is bigger than the HorizontalScrollView to be able to scroll
            mTabsContainer.setMinimumWidth(width);
            //Clipping padding to false to see the tabs while we pass them swiping
            setClipToPadding(false);
        }

        if (mTabsContainer.getChildCount() > 0) {
            mTabsContainer
                    .getChildAt(0)
                    .getViewTreeObserver()
                    .addOnGlobalLayoutListener(firstTabGlobalLayoutListener);
        }

        super.onLayout(changed, l, t, r, b);
    }

    private ViewTreeObserver.OnGlobalLayoutListener firstTabGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

        @Override
        public void onGlobalLayout() {
            //Log.e("onGlobalLayout: ","start");
            View view = mTabsContainer.getChildAt(0);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                removeGlobalLayoutListenerPreJB();
            } else {
                removeGlobalLayoutListenerJB();
            }

            if (isPaddingMiddle) {
                int mHalfWidthFirstTab = view.getWidth() / 2;
                mPaddingLeft = mPaddingRight = getWidth() / 2 - mHalfWidthFirstTab;
            }

            setPadding(mPaddingLeft, getPaddingTop(), mPaddingRight, getPaddingBottom());
            if (mScrollOffset == 0) mScrollOffset = getWidth() / 2 - mPaddingLeft;
            //mCurrentPosition = mPager.getCurrentItem();
            mCurrentPositionOffset = 0f;
            scrollToChild(mCurrentPosition, 0);
            updateSelection(mCurrentPosition);
        }

        @SuppressWarnings("deprecation")
        private void removeGlobalLayoutListenerPreJB() {
            getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        private void removeGlobalLayoutListenerJB() {
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        //Log.e("onDraw: ","start");
        super.onDraw(canvas);
        if (isInEditMode() || mTabCount == 0) {
            return;
        }

        final int height = getHeight();
        // draw divider
        if (mDividerWidth > 0) {
            mDividerPaint.setStrokeWidth(mDividerWidth);
            mDividerPaint.setColor(mDividerColor);
            for (int i = 0; i < mTabCount - 1; i++) {
                View tab = mTabsContainer.getChildAt(i);
                canvas.drawLine(tab.getRight(), mDividerPadding, tab.getRight(), height - mDividerPadding, mDividerPaint);
            }
        }

        // draw underline
        if (mUnderlineHeight > 0) {
            mRectPaint.setColor(mUnderlineColor);
            canvas.drawRect(mPaddingLeft, height - mUnderlineHeight, mTabsContainer.getWidth() + mPaddingRight, height, mRectPaint);
        }

        //Log.e("onDraw: ","draw indicator line");
        // draw indicator line
        if (mIndicatorHeight > 0) {
            //mRectPaint.setColor(mIndicatorColor);
            //Pair<Float, Float> lines = getIndicatorCoordinates();
            //Log.e("onDraw: ","Pair is "+ lines.first+", "+lines.second);
            //canvas.drawRect(lines.first + mPaddingLeft, height - mIndicatorHeight, lines.second + mPaddingLeft, height, mRectPaint);
        }
    }


    public void updateSelection(int position) {
        for (int i = 0; i < mTabCount; ++i) {
            View tv = mTabsContainer.getChildAt(i);
            final boolean selected = i == position;
            if (selected) {
                select(tv);
            } else {
                unSelect(tv);
            }
        }
    }

    private void unSelect(View tab) {
        if (tab != null) {
            TextView tab_title = (TextView) tab.findViewById(R.id.tab_multiselectionindicator_title);
            if (tab_title != null) {
                tab_title.setSelected(false);
            }
        }
    }

    private void select(View tab) {
        if (tab != null) {
            TextView tab_title = (TextView) tab.findViewById(R.id.tab_multiselectionindicator_title);
            if (tab_title != null) {
                tab_title.setSelected(true);
            }
        }

    }


    @Override
    public void onRestoreInstanceState(Parcelable state) {
        //Log.e("onRestoreInstance: ","start");
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mCurrentPosition = savedState.currentPosition;
        if (mCurrentPosition != 0 && mTabsContainer.getChildCount() > 0) {
            unSelect(mTabsContainer.getChildAt(0));
            select(mTabsContainer.getChildAt(mCurrentPosition));
        }
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        //Log.e("onSaveInstanceState: ","start");
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = mCurrentPosition;
        return savedState;
    }

    private ColorStateList createColorStateList(int color_state_pressed, int color_state_selected, int color_state_default) {
        return new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_pressed}, //pressed
                        new int[]{android.R.attr.state_selected}, // enabled
                        new int[]{} //default
                },
                new int[]{
                        color_state_pressed,
                        color_state_selected,
                        color_state_default
                }
        );
    }

    static class SavedState extends BaseSavedState {
        int currentPosition;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    private CallbackManager callbackManager = null;


    public void setCallbackManager(CallbackManager c){
        this.callbackManager = c;
    }
    
    public interface CallbackManager{
        void notifyChange(int position);
    }
}
