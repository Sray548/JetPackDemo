package com.example.jetpackdemo.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SwitchCompat;

import com.example.jetpackdemo.R;


/**
 * 作者：Leon
 * 时间：2016/12/21 10:32
 */
public class LSettingItem extends RelativeLayout {
    /*左侧显示文本*/
    private String mLeftText;
    /*左侧图标*/
    private Drawable mLeftIcon;
    /*右侧图标*/
    private Drawable mRightIcon;
    /*左侧显示文本大小*/
    private int mTextSize;
    /*左侧显示文本颜色*/
    private int mTextColor;
    /*右侧显示文本大小*/
    private float mRightTextSize;
    /*右侧显示文本颜色*/
    private int mRightTextColor;
    /*整体根布局view*/
    private View mView;
    /*根布局*/
    private RelativeLayout mRootLayout;
    /*左侧文本控件*/
    private TextView mTvLeftText;
    /*右侧文本控件*/
    private TextView mTvRightText;
    /*分割线*/
    private View mUnderLine;

    private TextView mCover;
    /*左侧图标控件*/
    private ImageView mIvLeftIcon;
    /*左侧图标大小*/
    private int mLeftIconSzie;
    /*右侧图标控件区域,默认展示图标*/
    private FrameLayout mRightLayout;
    private LinearLayout mConfirmLL;
    private ImageView mConfirmStatusImg;
    private TextView mConfirmText;
    private SwitchCompat mConfirmReset;
    /*右侧图标控件,默认展示图标*/
    private ImageView mIvRightIcon;
    /*右侧图标控件,默认update*/
    private TextView mFunc;
    /*右侧图标控件,选择样式图标*/
    private AppCompatCheckBox mRightIcon_check;
    /*右侧图标控件,开关样式图标*/
    private SwitchCompat mRightIcon_switch;
    /*右侧图标展示风格*/
    private int mRightStyle = 0;
    public static final int UNDETECTED = 7;
    public static final int DETECTED = 8;
    public static final int CONFIRMED_NORMAL = 9;
    public static final int CONFIRMED_EXCEPTION = 12;
    private int mLevel = 0;
    /*选中状态*/
    private boolean mChecked;
    /*点击事件*/
    private OnLSettingItemClick mOnLSettingItemClick;

    public LSettingItem(Context context) {
        this(context, null);
    }

    public LSettingItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LSettingItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        getCustomStyle(context, attrs);
        //获取到右侧展示风格，进行样式切换
        switchRightStyle(mRightStyle);
        switchlevel(mLevel);
    }

    public int getRightStyle() {
        return mRightStyle;
    }

    public void setmOnLSettingItemClick(OnLSettingItemClick mOnLSettingItemClick) {
        this.mOnLSettingItemClick = mOnLSettingItemClick;
    }

    /**
     * 初始化自定义属性
     *
     * @param context
     * @param attrs
     */
    public void getCustomStyle(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LSettingView);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.LSettingView_leftText) {
                mLeftText = a.getString(attr);
                mTvLeftText.setText(mLeftText);
            } else if (attr == R.styleable.LSettingView_leftIcon) {
                // 左侧图标
                mLeftIcon = a.getDrawable(attr);
                if (null != mLeftIcon) {
                    mIvLeftIcon.setImageDrawable(mLeftIcon);
                    mIvLeftIcon.setVisibility(VISIBLE);
                }
            } else if (attr == R.styleable.LSettingView_leftIconSize) {
                mLeftIconSzie = (int) a.getDimension(attr, 16);
                LayoutParams layoutParams = (LayoutParams) mIvLeftIcon.getLayoutParams();
                layoutParams.width = mLeftIconSzie;
                layoutParams.height = mLeftIconSzie;
                mIvLeftIcon.setLayoutParams(layoutParams);
            } else if (attr == R.styleable.LSettingView_leftTextMarginLeft) {
                int leftMargin = (int) a.getDimension(attr, 8);
                LayoutParams layoutParams = (LayoutParams) mTvLeftText.getLayoutParams();
                layoutParams.leftMargin = leftMargin;
                mTvLeftText.setLayoutParams(layoutParams);
            } else if (attr == R.styleable.LSettingView_rightIcon) {
                // 右侧图标
                mRightIcon = a.getDrawable(attr);
                mIvRightIcon.setImageDrawable(mRightIcon);
            } else if (attr == R.styleable.LSettingView_LtextSize) {
                // 默认设置为16sp
                float textSize = a.getFloat(attr, 16);
                mTvLeftText.setTextSize(textSize);
            } else if (attr == R.styleable.LSettingView_LtextColor) {
                //文字默认灰色
                mTextColor = a.getColor(attr, Color.LTGRAY);
                mTvLeftText.setTextColor(mTextColor);
            } else if (attr == R.styleable.LSettingView_rightStyle) {
                mRightStyle = a.getInt(attr, 0);
            } else if (attr == R.styleable.LSettingView_level) {
                mLevel = a.getInt(attr, 0);
            } else if (attr == R.styleable.LSettingView_isShowUnderLine) {
                //默认显示分割线
                if (!a.getBoolean(attr, true)) {
                    mUnderLine.setVisibility(View.GONE);
                }
            } else if (attr == R.styleable.LSettingView_isShowRightText) {
                //默认不显示右侧文字
                if (a.getBoolean(attr, false)) {
                    mTvRightText.setVisibility(View.VISIBLE);
                }
            } else if (attr == R.styleable.LSettingView_rightText) {
                mTvRightText.setText(a.getString(attr));
            } else if (attr == R.styleable.LSettingView_rightTextSize) {

                // 默认设置为16sp
                mRightTextSize = a.getFloat(attr, 14);
                mTvRightText.setTextSize(mRightTextSize);
            } else if (attr == R.styleable.LSettingView_rightTextColor) {
                //文字默认灰色
                mRightTextColor = a.getColor(attr, Color.GRAY);
                mTvRightText.setTextColor(mRightTextColor);
            }
        }
        a.recycle();
    }

    public void switchlevel(int level) {
        LayoutParams layoutParams = (LayoutParams) mTvLeftText.getLayoutParams();
        switch (level) {
            case 0:
                break;
            case 1:
                layoutParams.setMarginStart(96);
                break;
            case 2:
                layoutParams.setMarginStart(144);
                break;
        }
        mTvLeftText.setLayoutParams(layoutParams);
    }

    /**
     * 根据设定切换右侧展示样式，同时更新点击事件处理方式
     *
     * @param rightStyle
     */
    public void switchRightStyle(int rightStyle) {
        mRightStyle = rightStyle;
        mRightLayout.setVisibility(View.VISIBLE);
        mFunc.setVisibility(GONE);
        mRightIcon_check.setVisibility(View.GONE);
        mRightIcon_switch.setVisibility(View.GONE);
        mIvRightIcon.setVisibility(View.GONE);
        mConfirmLL.setVisibility(GONE);
        mConfirmText.setVisibility(GONE);
        mConfirmReset.setVisibility(GONE);
        switch (mRightStyle) {
            case 0:
                //默认展示样式，只展示一个图标
                mIvRightIcon.setVisibility(View.VISIBLE);
                break;
            case 1:
                //隐藏右侧图标
                mRightLayout.setVisibility(View.INVISIBLE);
                break;
            case 2:
                //显示选择框样式
                mRightIcon_check.setVisibility(View.VISIBLE);
                break;
            case 3:
                //显示开关切换样式
                mRightIcon_switch.setVisibility(View.VISIBLE);
                break;
            case 4:
                //显示update
                mFunc.setVisibility(VISIBLE);
                mFunc.setText(R.string.update);
                break;
            case 5:
                //显示学习
                mFunc.setVisibility(VISIBLE);
                mFunc.setText(R.string.learn);
                break;
            case 6:
                //显示配对
                mFunc.setVisibility(VISIBLE);
                mFunc.setText(R.string.couple);
                break;
            case UNDETECTED:
                mRightLayout.setVisibility(View.INVISIBLE);
                mConfirmLL.setVisibility(VISIBLE);
                mConfirmText.setVisibility(VISIBLE);
                mConfirmText.setBackgroundColor(getResources().getColor(R.color.unclick));
                mConfirmStatusImg.setImageResource(R.drawable.grey_light);
                mConfirmText.setOnClickListener(null);
                break;
            case DETECTED:
                mRightLayout.setVisibility(View.INVISIBLE);
                mConfirmLL.setVisibility(VISIBLE);
                mConfirmText.setVisibility(VISIBLE);
                mConfirmText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mConfirmStatusImg.setImageResource(R.drawable.green_light);
//                mConfirmText.setOnClickListener(this);
                break;
            case CONFIRMED_NORMAL:
                mRightLayout.setVisibility(View.INVISIBLE);
                mConfirmLL.setVisibility(VISIBLE);
                mConfirmReset.setVisibility(VISIBLE);
                mConfirmReset.setChecked(true);
                mConfirmStatusImg.setImageResource(R.drawable.green_light);
//                mConfirmText.setOnClickListener(this);
                break;
            case 10:
                /*
                    speed undetected && light detected
                    speed status
                */
                mRightLayout.setVisibility(View.INVISIBLE);
                mConfirmLL.setVisibility(VISIBLE);
                mConfirmText.setVisibility(VISIBLE);
                mConfirmText.setBackgroundColor(getResources().getColor(R.color.unclick));
                mConfirmStatusImg.setImageResource(R.drawable.red_light);
                mConfirmText.setOnClickListener(null);
                break;
            case 11:
                /*
                    speed undetected && light detected
                    light status
                */
                mRightLayout.setVisibility(View.INVISIBLE);
                mConfirmLL.setVisibility(VISIBLE);
                mConfirmText.setVisibility(VISIBLE);
                mConfirmText.setBackgroundColor(getResources().getColor(R.color.unclick));
                mConfirmStatusImg.setImageResource(R.drawable.green_light);
                mConfirmText.setOnClickListener(null);
                break;
            case CONFIRMED_EXCEPTION:
                mRightLayout.setVisibility(View.INVISIBLE);
                mConfirmLL.setVisibility(VISIBLE);
                mConfirmReset.setVisibility(VISIBLE);
                mConfirmStatusImg.setImageResource(R.drawable.grey_light);
//                mConfirmText.setOnClickListener(this);
                break;
        }
    }

    private void initView(Context context) {
        mView = View.inflate(context, R.layout.settingitem, this);
        mRootLayout = (RelativeLayout) mView.findViewById(R.id.rootLayout);
        mUnderLine = (View) mView.findViewById(R.id.underline);
        mCover = mView.findViewById(R.id.cover);
        mTvLeftText = (TextView) mView.findViewById(R.id.tv_lefttext);
        mTvRightText = (TextView) mView.findViewById(R.id.tv_righttext);
        mIvLeftIcon = (ImageView) mView.findViewById(R.id.iv_lefticon);
        mIvRightIcon = (ImageView) mView.findViewById(R.id.iv_righticon);
        mFunc = (TextView) mView.findViewById(R.id.func);
        mRightLayout = (FrameLayout) mView.findViewById(R.id.rightlayout);
        mConfirmLL = mView.findViewById(R.id.confirm_ll);
        mConfirmStatusImg = mView.findViewById(R.id.confirm_status);
        mConfirmText = mView.findViewById(R.id.confirm_txt);
        mConfirmReset = mView.findViewById(R.id.reset_confirm);
        mRightIcon_check = (AppCompatCheckBox) mView.findViewById(R.id.rightcheck);
        mRightIcon_switch = (SwitchCompat) mView.findViewById(R.id.rightswitch);

//        mFunc.setOnClickListener(this);
//        mConfirmReset.setOnClickListener(this);
    }

    private FuncClickListener mListener;
    private int mClickId;

    public void setFuncClickListener(int id, FuncClickListener listener) {
        mListener = listener;
        mClickId = id;
    }

//    @Override
//    public void onClick(View v) {
//        if (mListener == null) return;
//        switch (v.getId()) {
//            case R.id.func:
//                mListener.funcCLick(mClickId);
//                break;
//            case R.id.confirm_txt:
//                mListener.confirmSensor(mClickId);
//                break;
//            case R.id.reset_confirm:
//                mListener.resetSensor(mClickId);
//                break;
//        }
//    }

    public interface FuncClickListener {
        void funcCLick(int id);

        void confirmSensor(int id);

        void resetSensor(int id);
    }

    /**
     * 处理点击事件
     */
    public void clickOn() {
        switch (mRightStyle) {
            case 0:
            case 1:
                if (null != mOnLSettingItemClick) {
                    mOnLSettingItemClick.click(mChecked);
                }
                break;
            case 2:
                //选择框切换选中状态
                mRightIcon_check.setChecked(!mRightIcon_check.isChecked());
                mChecked = mRightIcon_check.isChecked();
                break;
            case 3:
                //开关切换状态
                mRightIcon_switch.setChecked(!mRightIcon_switch.isChecked());
                mChecked = mRightIcon_check.isChecked();
                break;
        }
    }

    /**
     * 获取根布局对象
     *
     * @return
     */
    public RelativeLayout getmRootLayout() {
        return mRootLayout;
    }

    /**
     * 更改左侧文字
     */
    public void setLeftText(String info) {
        mTvLeftText.setText(info);
    }

    /**
     * 更改右侧文字
     */
    public void setRightText(String info) {
        mTvRightText.setText(info);
    }

    /**
     * 更改右侧文字颜色
     */
    public void setRightTextColor(int color) {
        mTvRightText.setTextColor(color);
    }

    /**
     * 更改右侧图标
     */
    public void setRightIcon(Drawable drawable) {
        mIvRightIcon.setImageDrawable(drawable);
    }

    public void setBackground(int color) {
        mRootLayout.setBackgroundColor(color);
    }

    public void setChecked(boolean check) {
        switch (mRightStyle) {
            case 2:
                mRightIcon_check.setChecked(check);
                break;
            case 3:
                mRightIcon_switch.setChecked(check);
                break;
        }
    }

    public boolean isChecked() {
        if (mRightStyle == 2) {
            return mRightIcon_check.isChecked();
        } else {
            return mRightIcon_switch.isChecked();
        }
    }

    public interface OnLSettingItemClick {
        public void click(boolean isChecked);
    }

    public void setCover(boolean cover) {
        mCover.setVisibility(cover ? VISIBLE : GONE);
    }
}

