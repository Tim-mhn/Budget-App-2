package hku.cs.sqlitedemo;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
/*import android.support.design.widget.AppBarLayout;*/
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
/*import android.support.v7.widget.Toolbar;*/
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;

public class CollapsingImageLayout extends FrameLayout{
    private static final String TAG = "CollapsingImageLayout";

    private WindowInsetsCompat mLastInsets;

    private int mProgressBarLeftExpanded;

    private int mProgressBarTopExpanded;

    private int mOutcomesLeftExpanded;

    private int mOutcomesTopExpanded;

    private int mIncomesLeftExpanded;

    private int mIncomesTopExpanded;

    private int mExpensesBarLeftCollapsed;

    private int mIncomesBarLeftCollapsed;

    private int mExpensesBarTopCollapsed;

    private int mIncomesBarTopCollapsed;

    private int mOutcomesLeftCollapsed;

    private int mOutcomesTopCollapsed;

    private int mIncomesLeftCollapsed;

    private int mIncomesTopCollapsed;

    private OnOffsetChangedListener mOnOffsetChangedListener;

    public CollapsingImageLayout(Context context) {
        this(context, null);
    }

    public CollapsingImageLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollapsingImageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mExpensesBarLeftCollapsed = getResources().getDimensionPixelOffset(R.dimen.expenses_left_margin_collapsed);
        mIncomesBarLeftCollapsed = getResources().getDimensionPixelOffset(R.dimen.incomes_left_margin_collapsed);
        mExpensesBarTopCollapsed = getResources().getDimensionPixelOffset(R.dimen.expenses_top_margin_collapsed);
        mIncomesBarTopCollapsed = getResources().getDimensionPixelOffset(R.dimen.incomes_top_margin_collapsed);
        mOutcomesLeftCollapsed = getResources().getDimensionPixelOffset(R.dimen.outcomes_left_margin_collapsed);
        mOutcomesTopCollapsed = getResources().getDimensionPixelOffset(R.dimen.outcomes_top_margin_collapsed);


        ViewCompat.setOnApplyWindowInsetsListener(this,
                new androidx.core.view.OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                        return setWindowInsets(insets);
                    }
                });
    }

    @TargetApi(21)
    public CollapsingImageLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        // Add an OnOffsetChangedListener if possible
        final ViewParent parent = getParent();
        if (parent instanceof AppBarLayout) {
            if (mOnOffsetChangedListener == null) {
                mOnOffsetChangedListener = new OnOffsetChangedListener();
            }
            ((AppBarLayout) parent).addOnOffsetChangedListener(mOnOffsetChangedListener);
        }

        // We're attached, so lets request an inset dispatch
        ViewCompat.requestApplyInsets(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        // Remove our OnOffsetChangedListener if possible and it exists
        final ViewParent parent = getParent();
        if (mOnOffsetChangedListener != null && parent instanceof AppBarLayout) {
            ((AppBarLayout) parent).removeOnOffsetChangedListener(mOnOffsetChangedListener);
        }

        super.onDetachedFromWindow();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // Update our child view offset helpers
        for (int i = 0, z = getChildCount(); i < z; i++) {
            final View child = getChildAt(i);

            if (mLastInsets != null && !ViewCompat.getFitsSystemWindows(child)) {
                final int insetTop = mLastInsets.getSystemWindowInsetTop();
                if (child.getTop() < insetTop) {
                    // If the child isn't set to fit system windows but is drawing within the inset
                    // offset it down
                    ViewCompat.offsetTopAndBottom(child, insetTop);
                }
            }

            getViewOffsetHelper(child).onViewLayout();

            switch (child.getId()) {
                case R.id.relativeLayoutExpenses:
                    mProgressBarLeftExpanded = child.getLeft();
                    mProgressBarTopExpanded = child.getTop();
                    break;
            }
        }
    }

    private WindowInsetsCompat setWindowInsets(WindowInsetsCompat insets) {
        if (mLastInsets != insets) {
            mLastInsets = insets;
            requestLayout();
        }
        return insets.consumeSystemWindowInsets();
    }

    class OnOffsetChangedListener implements AppBarLayout.OnOffsetChangedListener {

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

            final int insetTop = mLastInsets != null ? mLastInsets.getSystemWindowInsetTop() : 0;
            final int scrollRange = appBarLayout.getTotalScrollRange();
            float offsetFactor = (float) (-verticalOffset) / (float) scrollRange;
            final int heightDiff = getHeight() - getMinimumHeight();
            Log.d(TAG, "onOffsetChanged(), offsetFactor = " + offsetFactor);


            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                final ViewOffsetHelper offsetHelper = getViewOffsetHelper(child);

                if (child instanceof Toolbar) {
                    if (getHeight() - insetTop + verticalOffset >= child.getHeight()) {
                        offsetHelper.setTopAndBottomOffset(-verticalOffset); // pin
                    }
                }

                if (child.getId() == R.id.relativeLayoutExpenses) {

                    float scaleFactor = 1F - offsetFactor * .95F ;
                    child.setScaleX(scaleFactor);
                    child.setScaleY(scaleFactor);

                    int topOffset = (int) ((mExpensesBarTopCollapsed - mProgressBarTopExpanded) * offsetFactor) - verticalOffset;
                    int leftOffset = (int) ((mExpensesBarLeftCollapsed - mProgressBarLeftExpanded) * offsetFactor);
                    child.setPivotX(240);
                    child.setPivotY(-110);
                    offsetHelper.setTopAndBottomOffset(topOffset);
                    offsetHelper.setLeftAndRightOffset(leftOffset);
                }

                if (child.getId() == R.id.relativeLayoutIncomes) {

                    float scaleFactor = 1F - offsetFactor * .95F ;
                    child.setScaleX(scaleFactor);
                    child.setScaleY(scaleFactor);

                    int topOffset = (int) ((mIncomesBarTopCollapsed - mProgressBarTopExpanded) * offsetFactor) - verticalOffset;
                    int leftOffset = (int) ((mIncomesBarLeftCollapsed - mProgressBarLeftExpanded) * offsetFactor);
                    child.setPivotX(240);
                    child.setPivotY(-110);
                    offsetHelper.setTopAndBottomOffset(topOffset);
                    offsetHelper.setLeftAndRightOffset(leftOffset);
                }

                /*
                if (child.getId() == R.id.intIncomes) {

                    int topOffset = (int) ((mIncomesTopCollapsed - mIncomesTopExpanded) * offsetFactor - verticalOffset);
                    int leftOffset = (int) ((mIncomesLeftCollapsed - mIncomesLeftExpanded) * offsetFactor);
                    // se queda quieto con topOffset = -verticalOffset; letOffset = 0;
                    offsetHelper.setTopAndBottomOffset(topOffset);
                    offsetHelper.setLeftAndRightOffset(leftOffset);
                    Log.d(TAG, "onOffsetChanged(), offsetting title top = " + topOffset + ", left = " + leftOffset + ", vericalOffset = " + verticalOffset);
                    Log.d(TAG, "onOffsetChanged(), offsetting title mOutcomesLeftCollapsed = " + mIncomesLeftCollapsed + ", mOutcomesLeftExpanded = " + mIncomesLeftExpanded);
                }

                if (child.getId() == R.id.intOutcomes) {

                    int topOffset = (int) ((mOutcomesTopCollapsed - mOutcomesTopExpanded) * offsetFactor - verticalOffset);
                    int leftOffset = (int) ((mOutcomesLeftCollapsed - mOutcomesLeftExpanded) * offsetFactor);
                    // se queda quieto con topOffset = -verticalOffset; letOffset = 0;
                    offsetHelper.setTopAndBottomOffset(topOffset);
                    offsetHelper.setLeftAndRightOffset(leftOffset);
                    Log.d(TAG, "onOffsetChanged(), offsetting title top = " + topOffset + ", left = " + leftOffset + ", vericalOffset = " + verticalOffset);
                    Log.d(TAG, "onOffsetChanged(), offsetting title mOutcomesLeftCollapsed = " + mOutcomesLeftCollapsed + ", mOutcomesLeftExpanded = " + mOutcomesLeftExpanded);
                }

                if (child.getId() == R.id.txtIncomes) {

                    int topOffset = (int) ((mIncomesTopCollapsed - mIncomesTopExpanded) * offsetFactor - verticalOffset);
                    int leftOffset = (int) ((mIncomesLeftCollapsed - mIncomesLeftExpanded) * offsetFactor);
                    offsetHelper.setTopAndBottomOffset(topOffset);
                    offsetHelper.setLeftAndRightOffset(leftOffset);
                    Log.d(TAG, "onOffsetChanged(), offsetting title top = " + topOffset + ", left = " + leftOffset + ", vericalOffset = " + verticalOffset);
                    Log.d(TAG, "onOffsetChanged(), offsetting title mOutcomesLeftCollapsed = " + mIncomesLeftCollapsed + ", mOutcomesLeftExpanded = " + mIncomesLeftExpanded);
                }

                if (child.getId() == R.id.txtOutcomes) {

                    int topOffset = (int) ((mOutcomesTopCollapsed - mOutcomesTopExpanded) * offsetFactor - verticalOffset);
                    int leftOffset = (int) ((mOutcomesLeftCollapsed - mOutcomesLeftExpanded) * offsetFactor);
                    offsetHelper.setTopAndBottomOffset(topOffset);
                    offsetHelper.setLeftAndRightOffset(leftOffset);
                    Log.d(TAG, "onOffsetChanged(), offsetting title top = " + topOffset + ", left = " + leftOffset + ", vericalOffset = " + verticalOffset);
                    Log.d(TAG, "onOffsetChanged(), offsetting title mOutcomesLeftCollapsed = " + mOutcomesLeftCollapsed + ", mOutcomesLeftExpanded = " + mOutcomesLeftExpanded);
                }
                */
            }
        }
    }

    private static ViewOffsetHelper getViewOffsetHelper(View view) {
        ViewOffsetHelper offsetHelper = (ViewOffsetHelper) view.getTag(R.id.view_offset_helper);
        if (offsetHelper == null) {
            offsetHelper = new ViewOffsetHelper(view);
            view.setTag(R.id.view_offset_helper, offsetHelper);
        }
        return offsetHelper;
    }

    static class ViewOffsetHelper {

        private final View mView;

        private int mLayoutTop;
        private int mLayoutLeft;
        private int mOffsetTop;
        private int mOffsetLeft;

        public ViewOffsetHelper(View view) {
            mView = view;
        }

        public void onViewLayout() {
            // Now grab the intended top
            mLayoutTop = mView.getTop();
            mLayoutLeft = mView.getLeft();

            // And offset it as needed
            updateOffsets();
        }

        private void updateOffsets() {
            ViewCompat.offsetTopAndBottom(mView, mOffsetTop - (mView.getTop() - mLayoutTop));
            ViewCompat.offsetLeftAndRight(mView, mOffsetLeft - (mView.getLeft() - mLayoutLeft));

            // Manually invalidate the view and parent to make sure we get drawn pre-M
            if (Build.VERSION.SDK_INT < 23) {
                tickleInvalidationFlag(mView);
                final ViewParent vp = mView.getParent();
                if (vp instanceof View) {
                    tickleInvalidationFlag((View) vp);
                }
            }
        }

        private static void tickleInvalidationFlag(View view) {
            final float y = ViewCompat.getTranslationY(view);
            ViewCompat.setTranslationY(view, y + 1);
            ViewCompat.setTranslationY(view, y);
        }

        /**
         * Set the top and bottom offset for this {@link ViewOffsetHelper}'s view.
         *
         * @param offset the offset in px.
         * @return true if the offset has changed
         */
        public boolean setTopAndBottomOffset(int offset) {
            if (mOffsetTop != offset) {
                mOffsetTop = offset;
                updateOffsets();
                return true;
            }
            return false;
        }

        /**
         * Set the left and right offset for this {@link ViewOffsetHelper}'s view.
         *
         * @param offset the offset in px.
         * @return true if the offset has changed
         */
        public boolean setLeftAndRightOffset(int offset) {
            if (mOffsetLeft != offset) {
                mOffsetLeft = offset;
                updateOffsets();
                return true;
            }
            return false;
        }

        public int getTopAndBottomOffset() {
            return mOffsetTop;
        }

        public int getLeftAndRightOffset() {
            return mOffsetLeft;
        }
    }
}
