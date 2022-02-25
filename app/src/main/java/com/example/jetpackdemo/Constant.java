package com.example.jetpackdemo;

import android.os.Environment;

public class Constant {
    //camera id
    public static final int DRIVER_CAM = 0;
    public static final int FRONT_CAM = 1;
    public static final int REAR_CAM = 2;

    //calibration mode
    public static final int EXPRESS = 0;
    public static final int CUSTOM = 1;

    //intent key
    public static final String CAM_ID = "cam_id";
    public static final String CALI_MODE = "cali_mode";
    public static final String DEVICE_FEATURE = "device_feature";
    public static final String CAMERA_HEIGHT = "camera_height";
    public static final String BOUNDARY1 = "boundary1";
    public static final String BOUNDARY2 = "boundary2";
    public static final String CUR_BOUNDARY_COUNT = "cur_boundary_count";
    public static final String TARGET_BOUNDARY_COUNT = "target_boundary_count";
    public static final String RESET_CALIBRATION = "reset_calibration";

    //directory
    public static final String WORKX_HOME = Environment.getExternalStorageDirectory().getAbsolutePath() + "/VIA_M500/";
    public static String WORKX_HOME_FW = Environment.getExternalStorageDirectory().getAbsolutePath() + "/VIA_M500/firmware/";

    //unit
    public static final String UNIT_TYPE = "unittype";
    public static final String UNIT = "unit";

    //language
    public static final String EN_US = "en-US";
    public static final String ZH_CN = "zh-CN";
    public static final String ZH_TW = "zh-TW";
    public static final String JA_JP = "ja-JP";

}
