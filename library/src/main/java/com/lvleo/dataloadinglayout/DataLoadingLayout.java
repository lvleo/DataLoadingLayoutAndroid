package com.lvleo.dataloadinglayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by lyx on 16/10/8 14:43.
 * Contact:     lvyongxu@gmail.com
 * Description: This class is a custom view for data loading.
 */
public class DataLoadingLayout extends RelativeLayout {

    private static final String TAG = DataLoadingLayout.class.getSimpleName();

    private String mTextContent ="No Data"; // TODO: use a default from R.string...
    private float mTextSize = 14; // TODO: use a default from R.dimen...
    private int mTextColor = Color.GRAY; // TODO: use a default from R.color...

    private int mProgressBarColor = Color.BLUE; // TODO: use a default from R.color...
    private float mProgressBarSize = 48; // TODO: use a default from R.dimen...

    private int padding = 16;

    private OnViewTouchListener mOnViewTouchListener = null;

    private boolean canRefresh = false;

    private View dataView;

    private TextView textViewStatus;
    private ProgressBar progressBarLoading;

    public DataLoadingLayout(Context context) {
        super(context);
        init(null, 0);
    }

    public DataLoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DataLoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {

        // Load attributes
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(
                attrs, R.styleable.DataLoadingLayout, defStyle, 0);

        if (typedArray != null) {
            mTextContent = typedArray.getString(R.styleable.DataLoadingLayout_statusText);
            mTextColor = typedArray.getColor(R.styleable.DataLoadingLayout_statusTextColor, Color.BLACK);

            mTextSize = typedArray.getDimensionPixelSize(R.styleable.DataLoadingLayout_statusTextSize,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()));

            mTextSize /= getResources().getDisplayMetrics().density;

            mProgressBarColor = typedArray.getColor(R.styleable.DataLoadingLayout_loadingBarColor, Color.BLUE);

            mProgressBarSize = typedArray.getDimensionPixelSize(R.styleable.DataLoadingLayout_loadingBarSize,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics()));

//            int dataViewResId = typedArray.getResourceId(R.styleable.DataLoadingLayout_dataViewId, -1);
//            Log.e(TAG, "init: dataViewResId=" + dataViewResId);
//
//            if (dataViewResId > 0) {
//                dataView = findViewById(dataViewResId);
//                dataView.setVisibility(GONE);
//            } else {
//                throw new IllegalStateException("The app:dataViewId attribute is must");
//            }

//            typedArray.getInteger(R.styleable.DataLoadingLayout_dataViewId, -1);

            typedArray.recycle();
        }

        this.setPadding(padding, padding, padding, padding);

        textViewStatus = new TextView(getContext());
        textViewStatus.setText(mTextContent);
        textViewStatus.setTextColor(mTextColor);
        textViewStatus.setTextSize(mTextSize);

        textViewStatus.setGravity(Gravity.CENTER);

        textViewStatus.setVisibility(GONE);

        LayoutParams textViewParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewParams.addRule(RelativeLayout.CENTER_IN_PARENT, -1);
        this.addView(textViewStatus, textViewParams);

        progressBarLoading = new ProgressBar(getContext());

        Log.e(TAG, "init: mProgressBarSize=" + mProgressBarSize);
        LayoutParams barLoadingParams = new LayoutParams((int) mProgressBarSize, (int) mProgressBarSize);

        barLoadingParams.addRule(RelativeLayout.CENTER_IN_PARENT, -1);

        progressBarLoading.getIndeterminateDrawable().setColorFilter(mProgressBarColor, PorterDuff.Mode.SRC_IN);

        this.addView(progressBarLoading, barLoadingParams);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mOnViewTouchListener != null) {
                if (canRefresh) {
                    mOnViewTouchListener.onTouchUp();
                    canRefresh = false;
                } else {
                    Log.e(TAG, "onTouchEvent: Please call setCanRefresh(true)method first");
//                    Toast.makeText(getContext(), "请调用setCanRefresh()方法", Toast.LENGTH_SHORT).show();
                }
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * Interface definition for a callback to be invoked when a touch event is
     * dispatched to this view. The callback will be invoked before the touch
     * event is given to the view.
     */
    public interface OnViewTouchListener {

        /**
         * Called when a touch event is dispatched to a view and the event action equal MotionEvent.ACTION_DOWN.
         * This allows listeners to get a chance to respond before the target view.
         *
         * @see #onTouchEvent(MotionEvent)
         */
        void onTouchUp();
    }

    /**
     * Register a callback to be invoked when a touch event is sent to this view.
     *
     * @param listener the touch listener to attach to this view
     */
    public void setOnMyViewTouchListener(@NonNull OnViewTouchListener listener) {
        this.mOnViewTouchListener = listener;
    }

    /**
     * Gets data view.
     *
     * @return the data view
     */
    public View getDataView() {
        return dataView;
    }

    /**
     * Sets data view.
     *
     * @param dataView the data view
     */
    public void setDataView(View dataView) {
        this.dataView = dataView;
    }

    /**
     * Is can refresh boolean.
     *
     * @return the boolean
     */
    public boolean isCanRefresh() {
        return canRefresh;
    }

    /**
     * Sets this view can refresh data by onTouchUp method.
     *
     * @param canRefresh the can refresh
     */
    public void setCanRefresh(boolean canRefresh) {
        this.canRefresh = canRefresh;
    }

    /**
     * Sets status content.
     *
     * @param content the content
     */
    public void setStatusContent(String content) {
        textViewStatus.setText(content);
    }

    /**
     * Sets status content.
     *
     * @param contentId the content id
     */
    public void setStatusContent(int contentId) {
        textViewStatus.setText(contentId);
    }

    /**
     * Data loading.
     */
    public void loading() {
        textViewStatus.setVisibility(View.GONE);
        progressBarLoading.setVisibility(View.VISIBLE);
        this.setVisibility(View.VISIBLE);
        if (dataView != null) {
            dataView.setVisibility(View.GONE);
        } else {
            throw new IllegalStateException("The dataView is must not null");
        }

        canRefresh = false;

    }

    /**
     * Data load finished and success,show you data.
     */
    public void loadSuccess() {
        textViewStatus.setVisibility(View.GONE);
        progressBarLoading.setVisibility(View.GONE);
        this.setVisibility(View.GONE);
        if (dataView != null) {
            dataView.setVisibility(View.VISIBLE);
        } else {
            throw new IllegalStateException("The dataView is must not null");
        }

        canRefresh = false;

    }

    /**
     * Data load finished, but data is empty or null,need show status(eg: data is empty) to user.
     *
     * @param status the status
     */
    public void loadSuccess(@NonNull String status) {
        if (status != null && !TextUtils.isEmpty(status)) {
            textViewStatus.setVisibility(View.VISIBLE);
            textViewStatus.setText(status);
        } else {
            textViewStatus.setVisibility(View.GONE);
            this.setVisibility(View.GONE);
        }
        progressBarLoading.setVisibility(View.GONE);
        if (dataView != null) {
            dataView.setVisibility(View.GONE);
        } else {
            throw new IllegalStateException("The dataView is must not null");
        }

        canRefresh = true;

    }

    /**
     * Data load finished, but data is empty or null,need show status(eg: data is empty) to user.
     *
     * @param statusId the status id
     */
    public void loadSuccess(@StringRes int statusId) {
        if (statusId > 0) {
            textViewStatus.setVisibility(View.VISIBLE);
            textViewStatus.setText(statusId);
        } else {
            textViewStatus.setVisibility(View.GONE);
            this.setVisibility(View.GONE);
        }
        progressBarLoading.setVisibility(View.GONE);
        if (dataView != null) {
            dataView.setVisibility(View.GONE);
        } else {
            throw new IllegalStateException("The dataView is must not null");
        }

        canRefresh = true;

    }

    /**
     * Data load error, need show status(server error or timeout...) to user.
     *
     * @param status the status
     */
    public void loadError(@NonNull String status) {
        if (status != null && !TextUtils.isEmpty(status)) {
            textViewStatus.setVisibility(View.VISIBLE);
//            textViewStatus.setTextColor(Color.RED);
            textViewStatus.setText(status);
        } else {
            textViewStatus.setVisibility(View.GONE);
            this.setVisibility(View.GONE);
        }
        progressBarLoading.setVisibility(View.GONE);
        if (dataView != null) {
            dataView.setVisibility(View.GONE);
        } else {
            throw new IllegalStateException("The dataView is must not null");
        }

        canRefresh = true;

    }

    /**
     * Data load error, need show status(server error or timeout...) to user.
     *
     * @param statusId the status id
     */
    public void loadError(@StringRes int statusId) {
        if (statusId > 0) {
            textViewStatus.setVisibility(View.VISIBLE);
//            textViewStatus.setTextColor(Color.RED);
            textViewStatus.setText(statusId);
        } else {
            textViewStatus.setVisibility(View.GONE);
            this.setVisibility(View.GONE);
        }
        progressBarLoading.setVisibility(View.GONE);
        if (dataView != null) {
            dataView.setVisibility(View.GONE);
        } else {
            throw new IllegalStateException("The dataView is must not null");
        }

        canRefresh = true;

    }

}
