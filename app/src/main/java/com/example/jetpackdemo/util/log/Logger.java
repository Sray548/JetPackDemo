/*
 * *****************************************************************************
 *  Copyright (c) 2020 VIA Technologies, Inc. All Rights Reserved.
 *  This PROPRIETARY SOFTWARE is the property of VIA Technologies, Inc.
 *  and may contain trade secrets and/or other confidential information of
 *  VIA Technologies, Inc. This file shall not be disclosed to any third
 *  party, in whole or in part, without prior written consent of VIA.
 *  THIS PROPRIETARY SOFTWARE AND ANY RELATED DOCUMENTATION ARE PROVIDED AS IS,
 *  WITH ALL FAULTS, AND WITHOUT WARRANTY OF ANY KIND EITHER EXPRESS OR IMPLIED,
 *  AND VIA TECHNOLOGIES, INC. DISCLAIMS ALL EXPRESS OR IMPLIED
 *  WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, QUIET
 *  ENJOYMENT OR NON-INFRINGEMENT.
 * ****************************************************************************
 */

package com.example.jetpackdemo.util.log;

import java.util.HashMap;

/**
 * 一个类似WLogger的实现
 * <p>
 * 初始化建议在Application.onCreate中，
 * 1. 调用 Logger.setTagPrefix("XXX_");
 * 2. 调用 Logger.setSetting("=d,TAG1=v") 设置默认级别为debug，TAG1级别为verbose，
 * 建议将这个log的设置字符串放在一个 debug.log.yourapp 的property中
 * 然后registerReceiver一个broadcast，这样可以通过在命令行下
 * setprop debug.log.yourapp =d,TAG1=v,TAG2=i
 * am broadcast -a debug.log.yourapp
 * 通知Log的配置发生了改变。
 * <p>
 * 然后每个Java class，可以
 * private static final Logger Log = Logger.Create("TAG");
 * 后续可以
 * Log.i("hello,world);
 * 或者
 * if(Log.v())
 * Log.v("verbose message here" + someObject.toString());
 * <p>
 * 可以参考 LogSetting.init 函数
 */
public final class Logger {
    private static Logger sHeader = null;
    private static HashMap<String, Integer> sPendingLevelSetting;

    public static final int VERBOSE = android.util.Log.VERBOSE;     //2
    public static final int DEBUG = android.util.Log.DEBUG;         //3
    public static final int INFO = android.util.Log.INFO;           //4
    public static final int WARN = android.util.Log.WARN;           //5
    public static final int ERROR = android.util.Log.ERROR;         //6

    private static int sDefaultLevel = DEBUG;

    //统一加的logcat消息的TAG前缀
    private static String sTagPrefix = "";
    private static Writer sWriter;

    private static final Logger Log = Logger.create("Logger");

    // class members
    private volatile int mLevel;
    private String mFullTag;        //prefix + tag
    private Logger mNext;

    //一个额外的Writer，可以用于将log显示在某个TextView中。（或者保存到文件中）
    public interface Writer {
        //留意这个函数可能被多线程调用
        void doWriteLog(int level, String tag, String msg, Throwable tr);
    }

    public static void setWriter(Writer writer) {
        sWriter = writer;
    }

    public static Logger create(String tag) {
        Logger log = find(tag);
        if (log != null) {
            return log;
        }

        int level;
        String tagLowCase = tag.toLowerCase();
        Integer levelObj = sPendingLevelSetting != null ? sPendingLevelSetting.get(tagLowCase) : null;
        if (levelObj != null) {
            level = levelObj;
            sPendingLevelSetting.remove(tagLowCase);
            if (sPendingLevelSetting.size() == 0) {
                //free memory
                sPendingLevelSetting = null;
            }
        } else {
            level = sDefaultLevel;
        }

        log = new Logger(level, sTagPrefix + tag);
        //add to list
        log.mNext = sHeader;
        sHeader = log;
        return log;
    }


    // logSettingString is sth like "prefix=MyApp_,=e,GPSService=d,video=i,audio=w"
    public static void setSetting(String logSettingString) {
        try {
            String[] fields = logSettingString.split(",");
            for (String field : fields) {
                if (field.startsWith("=")) {
                    //=e
                    int level = string2Level(field.substring(1));
                    setAllLevel(level);
                } else {
                    //a=b
                    String[] taglevel = field.split("=");
                    String name = taglevel[0].toLowerCase();
                    String value = taglevel[1];
                    if (name.equalsIgnoreCase("prefix")) {
                        setTagPrefix(value);
                    } else {
                        int level = string2Level(value);
                        setLogLevel(name, level);
                    }
                }
            }
            Log.i("Setting string: '" + logSettingString + "'");
        } catch (Exception e) {
            Log.e("Wrong Setting string: '" + logSettingString + "', err:", e);
        }
    }

    public static void setLogLevel(String name, int level) {
        Logger log = find(name);
        if (log != null) {
            log.mLevel = level;
        } else {
            //this logger object not created yet, save it now.
            //will set the correct mLevel when create this log object later
            if (sPendingLevelSetting == null)
                sPendingLevelSetting = new HashMap<>();
            sPendingLevelSetting.put(name.toLowerCase(), level);
        }
    }

    /**
     * 设置所有的Log都有一个固定的前缀，方便logcat | grep XXX_
     *
     * @param prefix 例如 XXX_
     */
    public static void setTagPrefix(String prefix) {
        if (!sTagPrefix.equals(prefix)) {
            for (Logger log = sHeader; log != null; log = log.mNext) {
                log.changePrefix(sTagPrefix, prefix);
            }
            sTagPrefix = prefix;
        }
    }

    public static void setAllLevel(int level) {
        for (Logger log = sHeader; log != null; log = log.mNext) {
            log.mLevel = level;
        }
        sDefaultLevel = level;
    }

    public static String dump() {
        final StringBuilder sb = new StringBuilder(256);
        sb.append("Prefix=").append(sTagPrefix).append("\n");
        sb.append("DefaultLevel=").append(levelString(sDefaultLevel)).append("\n");

        for (Logger log = sHeader; log != null; log = log.mNext) {
            sb.append("  ").append(log.getTag()).append(": ");
            sb.append(levelString(log.getLevel()));
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * 是否verbose级别的log需要打印
     *
     * @return true if verbose if enabled for this Logger object
     */
    public boolean v() {
        return this.mLevel <= VERBOSE;
    }

    public int v(String msg) {
        return writeLog(VERBOSE, msg, null);
    }

    public boolean d() {
        return this.mLevel <= DEBUG;
    }

    public int d(String msg) {
        return writeLog(DEBUG, msg, null);
    }

    public boolean i() {
        return this.mLevel <= INFO;
    }

    public int i(String msg) {
        return writeLog(INFO, msg, null);
    }

    public int w(String msg) {
        return writeLog(WARN, msg, null);
    }

    public int w(String msg, Throwable tr) {
        return writeLog(WARN, msg, tr);
    }

    public int e(String msg) {
        return writeLog(ERROR, msg, null);
    }

    public int e(String msg, Throwable tr) {
        return writeLog(ERROR, msg, tr);
    }

    public String getTag() {
        return mFullTag.substring(sTagPrefix.length());
    }

    public int getLevel() {
        return mLevel;
    }

    @Override
    public String toString() {
        return getTag() + "=" + levelString(mLevel);
    }


    public static char levelChar(int level) {
        switch (level) {
            case VERBOSE:
                return 'V';
            case DEBUG:
                return 'D';
            case INFO:
                return 'I';
            case WARN:
                return 'W';
            case ERROR:
                return 'E';
            default:
                return 'X';
        }
    }

    public static String levelString(int level) {
        switch (level) {
            case VERBOSE:
                return "Verbose";
            case DEBUG:
                return "Debug";
            case INFO:
                return "Info";
            case WARN:
                return "Warn";
            case ERROR:
                return "Error";
            default:
                return "Level:" + level;
        }
    }

    public static int string2Level(String levelString) throws Exception {
        switch (levelString.charAt(0)) {
            case 'V':
            case 'v':
                return VERBOSE;
            case 'd':
            case 'D':
                return DEBUG;
            case 'i':
            case 'I':
                return INFO;
            case 'w':
            case 'W':
                return WARN;
            case 'e':
            case 'E':
                return ERROR;
            default:
                throw new RuntimeException("Invalid level string:" + levelString);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //use create to new the object
    private Logger(int level, String prefixAndTag) {
        this.mLevel = level;
        this.mFullTag = prefixAndTag;
    }

    private void changePrefix(String oldPrefix, String newPrefix) {
        mFullTag = newPrefix + mFullTag.substring(oldPrefix.length());
    }

    private int writeLog(int level, String msg, Throwable tr) {
        if (level < this.mLevel)
            return 0;

        if (sWriter != null) {
            sWriter.doWriteLog(level, this.getTag(), msg, tr);
        }
        //some android don't print verbose log
//        if (mLevel == VERBOSE) mLevel = DEBUG;

        //call android default log function
        if (tr == null)
            return android.util.Log.println(level, this.mFullTag, msg);
        if (level == WARN)
            return android.util.Log.w(this.mFullTag, msg, tr);
        return android.util.Log.e(this.mFullTag, msg, tr);
    }


    /**
     * find the log object by tag from list
     */
    private static Logger find(String tag) {
        for (Logger log = sHeader; log != null; log = log.mNext) {
            if (log.getTag().equalsIgnoreCase(tag))
                return log;
        }
        return null;
    }
}
