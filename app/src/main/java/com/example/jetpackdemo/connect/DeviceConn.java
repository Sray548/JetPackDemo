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

package com.example.jetpackdemo.connect;


import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.example.jetpackdemo.util.log.Logger;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DeviceConn {

    public static final int WEBSOCKET_CONNECT = 1;
    public static final int WEBSOCKET_DISCONNECT = 2;

    private final DeviceConnHandler mDeviceConnHandler;
    private HandlerThread mWorkThread = null;

    //global unique device connection instance
    static DeviceConn sDeviceConn = null;
    private Logger Log = Logger.create(getClass().getSimpleName());
    private boolean mConnectted = false;

    private String mDeviceHttpUrl;

    private MyWebSocketClient mWebSocketClient = null;
    public static final String DEVICE_HTTP_URL = "http://192.168.9.1:1234/";
    private final String WEBSOCKET_URL = "ws://192.168.9.1:1234/webcam_preview";
    public static final String PREVIEW_COMMAND = "PREVIEW";
    public static final String PLAYBACK_COMMAND = "PLAYBACK";
    public static final String TRIPINFO_COMMAND = "TRIPINFO";
    public static final String FILELIST_COMMAND = "FILELIST";
    public static final String SETDEVICEINFO_COMMAND = "SETDEVICEINFO";
    public static final String GETDEVICEINFO_COMMAND = "GETDEVICEINFO";
    public static final String GETGPSINFO_COMMAND = "GETGPSINFO";
    public static final String GETINFO_COMMAND = "GETINFO";
    public static final String CONTROL_COMMAND = "CONTROL";
    public static final String REPORT_COMMAND = "REPORT";
    public static final String UPGRADE_COMMAND = "UPGRADE";
    public static final String UPGRADEPROGRESS = "UPGRADEPROGRESS";
    public static final String AUDIOPLAY = "AUDIOPLAY";
    public static final String DMOD_TYPE = "dmod";
    public static final String WAYTRONIC_TYPE = "waytronic_param";
    public static final String OVERSPEED = "overspeed";
    public static final String WARNING = "warning";
    public static final String BRAKING = "braking";
    public static final String VOLUME = "volume";
    public static final String WHEEL = "wheel";
    public static final String SNR_LOCATION = "snr_location";
    public static final String REVERSE = "reverse";
    public static final String UNLOCK = "unlock";
    public static final String UNL_TIME = "unl_time";
    public static final String LANGUAGE = "language";
    public static final String THROTTLE = "throttle";
    public static final String SPEED_SNR = "speed_snr";
    public static final String SEATBELT = "seatbelt";
    public static final String DMOD_CUSTOM_TYPE = "dmodcus";
    public static final String DMOD_EXPRESS_TYPE = "dmodexp";
    public static final String TIRE_BELT_TYPE = "tire_belt";
    public static final String SNR_STAT_THIS_BOOT = "snr_stat_this_boot";
    public static final String CONTROL_RESET_TYPE = "factory";
    public static final String CONTROL_CONFIRM_SNR = "confirm_snr";
    public static final String CONTROL_DISABLE_SNR = "disable_snr";
    public static final String FORMAT_SD = "sdcard_format";

    public static final String CENTER_DMS_FAIL = "center_dms_fail";
    public static final String FIRMWARE_UPGRADE = "FIRMWARE_UPGRADE";
    public static final String FIRMWARE_UPGRADE_FILE = "FIRMWARE_UPGRADE_FILE";
    public static final String PHOTO = "PHOTO";

    public static final String SETDEBUG = "SETDEBUG";
    public static final String GETDEBUG = "GETDEBUG";

    public static final int PORT = 1250;

    private final int CAMERA_CHANNEL_0 = 0;
    private final int CAMERA_CHANNEL_1 = 1;
    private final int CAMERA_INDEX_ARRAY[] = {CAMERA_CHANNEL_0, CAMERA_CHANNEL_1};
    private int mCameraIdx = 0;
    private final int CAMERA_NUMBER = 2;
    private final Object mLock = new Object();
    private int mCookie = 0;        //the cookie will be passed from device, if cookie not the same with us, drop the binary packet.

    public static DeviceConn getInstance(Context context) {
        if (sDeviceConn == null) {
            sDeviceConn = new DeviceConn(context);
        }
        return sDeviceConn;
    }

    private static boolean sIsLoaded;

//    static {
//        if (!sIsLoaded) {
//            System.loadLibrary("ffmpeg");
//            System.loadLibrary("wmtutil");
//            System.loadLibrary("wmtmedia");
//            System.loadLibrary("turbojpeg");
//            System.loadLibrary("MediaToolJNI");
//            sIsLoaded = true;
//        }
//    }

    private void sendMsg(String msg) {
        if (mWebSocketClient == null) return;
        Log.d("send cmd: " + msg);
        mWebSocketClient.send(msg);
    }

    public void stopCameraPlayback() {
        new Thread() {
            @Override
            public void run() {
                if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("cmd", PLAYBACK_COMMAND);
                        jsonObject.put("action", 0);
                    } catch (JSONException e) {
                        Log.e("json exception:" + e);
                    }
                    sendMsg(jsonObject.toString());
                }
            }
        }.start();
    }

    public void radarUIOnOff(final boolean onOff) {
        new Thread() {
            @Override
            public void run() {
                if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("cmd", SETDEBUG);
                        jsonObject.put("radar", onOff ? 1 : 0);
                    } catch (JSONException e) {
                        Log.e("json exception:" + e);
                    }
                    sendMsg(jsonObject.toString());
                }
            }
        }.start();
    }

    public void getRadarUIOnOff() {
        new Thread() {
            @Override
            public void run() {
                if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("cmd", GETDEBUG);
                    } catch (JSONException e) {
                        Log.e("json exception:" + e);
                    }
                    sendMsg(jsonObject.toString());
                }
            }
        }.start();
    }


    public void radarDis(final double distance) {
        new Thread() {
            @Override
            public void run() {
                if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("cmd", SETDEVICEINFO_COMMAND);
                        jsonObject.put("radar", formatData(distance));
                    } catch (JSONException e) {
                        Log.e("json exception:" + e);
                    }
                    sendMsg(jsonObject.toString());
                }
            }
        }.start();
    }

    public void setDriverLeaveAlert(final int time) {
        new Thread() {
            @Override
            public void run() {
                if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("cmd", SETDEVICEINFO_COMMAND);
                        jsonObject.put("driver_leave", time == 0 ? -1 : time);
                    } catch (JSONException e) {
                        Log.e("json exception:" + e);
                    }
                    sendMsg(jsonObject.toString());
                }
            }
        }.start();
    }

    public void startCameraPlayback(final int cameraidx, final int starttime, final int endtime, final int seektime) {
        new Thread() {
            @Override
            public void run() {
                if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        mCookie++;

                        jsonObject.put("cmd", PLAYBACK_COMMAND);
                        jsonObject.put("action", 1);
                        jsonObject.put("starttime", starttime);
                        jsonObject.put("endtime", endtime);
                        jsonObject.put("time", seektime);
                        jsonObject.put("channel", cameraidx);
                        jsonObject.put("cookie", mCookie);
                    } catch (JSONException e) {
                        Log.e("json exception:" + e);
                    }
                    sendMsg(jsonObject.toString());
                }
            }
        }.start();
    }

    public void setSysTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");// HH:mm:ss
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT + 00:00"));
        Date date = new Date(System.currentTimeMillis());
        String format = simpleDateFormat.format(date);
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", SETDEVICEINFO_COMMAND);
                jsonObject.put("time", format);
            } catch (JSONException e) {
                Log.e("json exception:" + e);
            }
            sendMsg(jsonObject.toString());
        }
    }

    private String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte[] buffer = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        StringBuilder str = new StringBuilder(bigInt.toString(16));
        for (int i = 0; i < 32 - str.length(); i++) {
            str.insert(0, '0');
        }
        return str.toString();
    }

    public void sendFile(byte[] data, String cmd) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            int bytelen = (data != null) ? data.length : 0;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", cmd);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String header = jsonObject.toString();

            Log.d("Send binary:" + header + ",datalen=" + bytelen);

            ByteBuffer bb = ByteBuffer.allocate(header.getBytes().length + bytelen + 1);
            bb.put(header.getBytes());
            bb.put((byte) 0);
            if (bytelen > 0) {
                bb.put(data);
            }
            byte[] binary = bb.array();
            Log.d("send cmd: " + binary.toString());
            mWebSocketClient.send(binary);
        }
    }

    public void sendUpgradeFile(byte[] data) {
        sendFile(data, FIRMWARE_UPGRADE_FILE);
    }

    public void sendPhoto(byte[] data) {
        sendFile(data, PHOTO);
    }

    public void reset() {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", CONTROL_COMMAND);
                jsonObject.put("type", CONTROL_RESET_TYPE);
            } catch (JSONException e) {
                Log.e("json exception:" + e);
            }
            sendMsg(jsonObject.toString());
        }
    }

    public void format_sd() {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", CONTROL_COMMAND);
                jsonObject.put("type", FORMAT_SD);
            } catch (JSONException e) {
                Log.e("json exception:" + e);
            }
            sendMsg(jsonObject.toString());
        }
    }

    public void setWiFiInfo(String name, String psd) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", SETDEVICEINFO_COMMAND);
                jsonObject.put("ssid", name);
                jsonObject.put("passwd", psd);
            } catch (JSONException e) {
                Log.e("json exception:" + e);
            }
//            sendMsg(jsonObject.toString());
            Log.d("send cmd: " + jsonObject);
        }
    }

    public void audioMute(boolean b) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", SETDEVICEINFO_COMMAND);
                jsonObject.put("audioMute", b ? 1 : 0);
            } catch (JSONException e) {
                Log.e("json exception:" + e);
            }
            sendMsg(jsonObject.toString());
        }
    }

    public void setHeightDis(double vehicleHeight, double camToRoofDis, double carWidth, double camToFront) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", SETDEVICEINFO_COMMAND);
                jsonObject.put("height", vehicleHeight);
                jsonObject.put("distance", camToRoofDis);
                jsonObject.put("car_width", carWidth);
                jsonObject.put("cam_to_front", camToFront);
            } catch (JSONException e) {
                Log.e("json exception:" + e);
            }
            sendMsg(jsonObject.toString());
        }
    }

    public void adasCalibration() {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", CONTROL_COMMAND);
                jsonObject.put("type", "adas");
            } catch (JSONException e) {
                Log.e("json exception:" + e);
            }
            sendMsg(jsonObject.toString());
        }
    }

    public void setAIFeatures(int alert, int module, int dmsAudioFeatures) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", SETDEVICEINFO_COMMAND);
                jsonObject.put("alert", alert);
                jsonObject.put("module", module);
                jsonObject.put("dmsdisable", dmsAudioFeatures);
            } catch (JSONException e) {
                Log.e("json exception:" + e);
            }
            sendMsg(jsonObject.toString());
        }
    }

    public void setCallVol(int progress) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", SETDEVICEINFO_COMMAND);
                jsonObject.put("callVolume", progress);
            } catch (JSONException e) {
                Log.e("json exception:" + e);
            }
            sendMsg(jsonObject.toString());
        }
    }

    public void setAlertVol(int progress) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", SETDEVICEINFO_COMMAND);
                jsonObject.put("alertVolume", progress);
            } catch (JSONException e) {
                Log.e("json exception:" + e);
            }
            sendMsg(jsonObject.toString());
        }
    }

    public void setDetectionSen(double progress) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", SETDEVICEINFO_COMMAND);
                jsonObject.put("pd_sensitivity", progress);
            } catch (JSONException e) {
                Log.e("json exception:" + e);
            }
            sendMsg(jsonObject.toString());
        }
    }

    public void setFrontPd(int pd) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", SETDEVICEINFO_COMMAND);
                jsonObject.put("pd_sens", pd);
                jsonObject.put("cam", "front");
            } catch (JSONException e) {
                Log.e("json exception:" + e);
            }
            sendMsg(jsonObject.toString());
        }
    }

    public void setRearPd(int pd) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", SETDEVICEINFO_COMMAND);
                jsonObject.put("pd_sens", pd);
                jsonObject.put("cam", "rear");
            } catch (JSONException e) {
                Log.e("json exception:" + e);
            }
            sendMsg(jsonObject.toString());
        }
    }

    public void setThirdPd(int pd) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", SETDEVICEINFO_COMMAND);
                jsonObject.put("pd_sens", pd);
                jsonObject.put("cam", "third");
            } catch (JSONException e) {
                Log.e("json exception:" + e);
            }
            sendMsg(jsonObject.toString());
        }
    }

    public void startDMSCalibration() {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", CONTROL_COMMAND);
                jsonObject.put("type", "center_dms_start");
            } catch (JSONException e) {
                Log.e("json exception:" + e);
            }
            sendMsg(jsonObject.toString());
        }
    }

    public void cancelDMSCalibration() {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", CONTROL_COMMAND);
                jsonObject.put("type", "center_dms_cancel");
            } catch (JSONException e) {
                Log.e("json exception:" + e);
            }
            sendMsg(jsonObject.toString());
        }
    }

    public void endDMSCalibration() {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", CONTROL_COMMAND);
                jsonObject.put("type", "dms");
            } catch (JSONException e) {
                Log.e("json exception:" + e);
            }
            sendMsg(jsonObject.toString());
        }
    }

    public void setFCWLevel(final int level) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("cmd", SETDEVICEINFO_COMMAND);
                        jsonObject.put("fcw_level", level);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendMsg(jsonObject.toString());
                } else {
                    Log.e("setFCWLevel failed, WS disconnect");
                }
            }
        }, "setFCWLevel").start();
    }

    public void setLanguage(String language) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", SETDEVICEINFO_COMMAND);

                jsonObject.put(LANGUAGE, language);
//                sendMsg(jsonObject.toString());
                Log.d("send cmd: " + jsonObject);
            } catch (JSONException ignored) {
            }
        }
    }

    public void confirmSpeedSensor() {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", CONTROL_COMMAND);
                jsonObject.put("type", CONTROL_CONFIRM_SNR);
                jsonObject.put("sensor", "speed");
                sendMsg(jsonObject.toString());
            } catch (JSONException ignored) {
            }
        }
    }

    public void confirmLightSensor() {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", CONTROL_COMMAND);
                jsonObject.put("type", CONTROL_CONFIRM_SNR);
                jsonObject.put("sensor", "light");
                sendMsg(jsonObject.toString());
            } catch (JSONException ignored) {
            }
        }
    }

    public void confirmSafetyBeltSensor() {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", CONTROL_COMMAND);
                jsonObject.put("type", CONTROL_CONFIRM_SNR);
                jsonObject.put("sensor", "belt");
                sendMsg(jsonObject.toString());
            } catch (JSONException ignored) {
            }
        }
    }

    public void disableSpeedSensor() {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", CONTROL_COMMAND);
                jsonObject.put("type", CONTROL_DISABLE_SNR);
                jsonObject.put("sensor", "speed");
                sendMsg(jsonObject.toString());
            } catch (JSONException ignored) {
            }
        }
    }

    public void disableLightSensor() {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", CONTROL_COMMAND);
                jsonObject.put("type", CONTROL_DISABLE_SNR);
                jsonObject.put("sensor", "light");
                sendMsg(jsonObject.toString());
            } catch (JSONException ignored) {
            }
        }
    }

    public void disableSafetyBeltSensor() {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", CONTROL_COMMAND);
                jsonObject.put("type", CONTROL_DISABLE_SNR);
                jsonObject.put("sensor", "belt");
                sendMsg(jsonObject.toString());
            } catch (JSONException ignored) {
            }
        }
    }

    public interface DeviceConnListener {
        int onDeviceConnect();

        int onDeviceDisconnect();

        int onDeviceMessage(String msg, ByteBuffer data);
    }

    private DeviceConnListener mListener;

    public void setListener(DeviceConnListener listener) {
        synchronized (mLock) {
            mListener = listener;
        }
    }

    private DeviceConn(Context context) {
        this.mContext = context;
        mWorkThread = new HandlerThread("DeviceConn");
        mWorkThread.start();
        mDeviceConnHandler = new DeviceConnHandler(mWorkThread.getLooper());
        mDeviceConnHandler.sendEmptyMessage(WEBSOCKET_CONNECT);
    }

    public boolean isDeviceConnect() {
        return mWebSocketClient.getConnection().isOpen();
    }

    private Context mContext;

    private String getWebSocketUrl() {
        WifiManager service = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (service == null) return "";
        DhcpInfo dhcpInfo = service.getDhcpInfo();
        if (dhcpInfo == null) return "";
        String ip = longToIP(dhcpInfo.gateway);
        setDeviceHttpUrl(ip);
        return "ws://" + ip + ":" + PORT + "/webcam_preview";
    }

    public String getDeviceHttpUrl() {
        return mDeviceHttpUrl;
    }

    private void setDeviceHttpUrl(String ip) {
        this.mDeviceHttpUrl = "http://" + ip + ":" + PORT + "/";
    }

    private String longToIP(long longIp) {
        StringBuffer sb = new StringBuffer("");
        sb.append(longIp & 0x000000FF);
        sb.append(".");
        sb.append((longIp & 0x0000FFFF) >>> 8);
        sb.append(".");
        sb.append((longIp & 0x00FFFFFF) >>> 16);
        sb.append(".");
        sb.append(longIp >>> 24);
        return sb.toString();
    }

    private void connectWebSocket() {
        try {
            mWebSocketClient = new MyWebSocketClient(new URI(getWebSocketUrl()));
            mWebSocketClient.connect();
            Log.d("connectWebSocket:" + getWebSocketUrl());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    void disconnectWebSocket() {
        new Thread() {
            @Override
            public void run() {
                if (mWebSocketClient != null) {
                    mWebSocketClient.close();
                    mWebSocketClient = null;
                    Log.d("disconnectWebSocket:" + getWebSocketUrl());
                }
            }
        }.start();
    }

    public void renameTrip(String name, int tripstarttime) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", TRIPINFO_COMMAND);
                jsonObject.put("alias", name);
                jsonObject.put("time", tripstarttime);
                sendMsg(jsonObject.toString());
            } catch (JSONException ignored) {
            }
        }
    }

    public void stopCameraPreview() {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", PREVIEW_COMMAND);
                jsonObject.put("action", 0);
                sendMsg(jsonObject.toString());
            } catch (JSONException ignored) {
            }
        }
    }

    //特别声明小数点分隔符为"."，不根据系统语言变化而变化
    private double formatData(double data) {
        return (double) Math.round(data * 1000000) / 1000000;
    }

    public void setMaxSpeed(Double speed) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", SETDEVICEINFO_COMMAND);
                jsonObject.put("maxhallspeed", formatData(speed));
                sendMsg(jsonObject.toString());
            } catch (JSONException ignored) {
            }
        }
    }

    public void setOverSpeed(Double speed) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", CONTROL_COMMAND);
                jsonObject.put("type", WAYTRONIC_TYPE);
                JSONObject param = new JSONObject();
                jsonObject.put("param", param);
                param.put(OVERSPEED, speed);
                sendMsg(jsonObject.toString());
            } catch (JSONException ignored) {
            }
        }
    }

    public void setWarningSpeed(Double speed) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", CONTROL_COMMAND);
                jsonObject.put("type", WAYTRONIC_TYPE);
                JSONObject param = new JSONObject();
                jsonObject.put("param", param);
                param.put(WARNING, speed);
                sendMsg(jsonObject.toString());
            } catch (JSONException ignored) {
            }
        }
    }

    public void setDangerSpeed(Double speed) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", CONTROL_COMMAND);
                jsonObject.put("type", WAYTRONIC_TYPE);
                JSONObject param = new JSONObject();
                jsonObject.put("param", param);
                param.put(BRAKING, speed);
                sendMsg(jsonObject.toString());
            } catch (JSONException ignored) {
            }
        }
    }

    public void setWaytronicTireDimension(Double wheel) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", CONTROL_COMMAND);
                jsonObject.put("type", WAYTRONIC_TYPE);
                JSONObject param = new JSONObject();
                jsonObject.put("param", param);
                param.put(WHEEL, wheel);
                sendMsg(jsonObject.toString());
            } catch (JSONException ignored) {
            }
        }
    }

    public void setUnlTime(int time) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", CONTROL_COMMAND);
                jsonObject.put("type", WAYTRONIC_TYPE);
                JSONObject param = new JSONObject();
                jsonObject.put("param", param);
                param.put(UNL_TIME, time);
                sendMsg(jsonObject.toString());
            } catch (JSONException ignored) {
            }
        }
    }

    public void startThrottle() {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", CONTROL_COMMAND);
                jsonObject.put("type", WAYTRONIC_TYPE);
                JSONObject param = new JSONObject();
                jsonObject.put("param", param);
                param.put(THROTTLE, 1);
                sendMsg(jsonObject.toString());
            } catch (JSONException ignored) {
            }
        }
    }

    public void startSpeed_snr() {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", CONTROL_COMMAND);
                jsonObject.put("type", WAYTRONIC_TYPE);
                JSONObject param = new JSONObject();
                jsonObject.put("param", param);
                param.put(SPEED_SNR, 1);
                sendMsg(jsonObject.toString());
            } catch (JSONException ignored) {
            }
        }
    }

    public void startSeatbelt() {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", CONTROL_COMMAND);
                jsonObject.put("type", WAYTRONIC_TYPE);
                JSONObject param = new JSONObject();
                jsonObject.put("param", param);
                param.put(SEATBELT, 1);
                sendMsg(jsonObject.toString());
            } catch (JSONException ignored) {
            }
        }
    }

    public void setSnrLocation(int position) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", CONTROL_COMMAND);
                jsonObject.put("type", WAYTRONIC_TYPE);
                JSONObject param = new JSONObject();
                jsonObject.put("param", param);
                param.put(SNR_LOCATION, position);
                sendMsg(jsonObject.toString());
            } catch (JSONException ignored) {
            }
        }
    }

    public void setReverse(int position) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", CONTROL_COMMAND);
                jsonObject.put("type", WAYTRONIC_TYPE);
                JSONObject param = new JSONObject();
                jsonObject.put("param", param);
                param.put(REVERSE, position);
                sendMsg(jsonObject.toString());
            } catch (JSONException ignored) {
            }
        }
    }

    public void setUnlockMode(int position) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", CONTROL_COMMAND);
                jsonObject.put("type", WAYTRONIC_TYPE);
                JSONObject param = new JSONObject();
                jsonObject.put("param", param);
                param.put(UNLOCK, position);
                sendMsg(jsonObject.toString());
            } catch (JSONException ignored) {
            }
        }
    }

    public void setLang(int lang) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", CONTROL_COMMAND);
                jsonObject.put("type", WAYTRONIC_TYPE);
                JSONObject param = new JSONObject();
                jsonObject.put("param", param);
                param.put("language", lang);
                sendMsg(jsonObject.toString());
            } catch (JSONException ignored) {
            }
        }
    }

    public void setWaytronicVolume(int volume) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", CONTROL_COMMAND);
                jsonObject.put("type", WAYTRONIC_TYPE);
                JSONObject param = new JSONObject();
                jsonObject.put("param", param);
                param.put(VOLUME, volume);
                sendMsg(jsonObject.toString());
            } catch (JSONException ignored) {
            }
        }
    }

    public void setWarningAlertAudio(int onOff) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", SETDEVICEINFO_COMMAND);
                jsonObject.put("dmod_warning_disable", onOff);
                sendMsg(jsonObject.toString());
            } catch (JSONException ignored) {
            }
        }
    }

    public void setDetectionBoxes(boolean onOff) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", SETDEVICEINFO_COMMAND);
                jsonObject.put("detection_boxes", onOff ? 1 : 0);
                sendMsg(jsonObject.toString());
            } catch (JSONException ignored) {
            }
        }
    }

    public void setWarningAlertType(int position) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", SETDEVICEINFO_COMMAND);
                jsonObject.put("dmod_warning_type", position == 0 ? "sound" : "voice");
                sendMsg(jsonObject.toString());
            } catch (JSONException ignored) {
            }
        }
    }

    public void setCriticalAlertType(int position) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", SETDEVICEINFO_COMMAND);
                jsonObject.put("dmod_critical_type", position == 0 ? "sound" : "voice");
                sendMsg(jsonObject.toString());
            } catch (JSONException ignored) {
            }
        }
    }

    public void setTireDimension(Double tire_dimension) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", SETDEVICEINFO_COMMAND);
                jsonObject.put("tirediameter", formatData(tire_dimension));
                sendMsg(jsonObject.toString());
            } catch (JSONException ignored) {
            }
        }
    }


    public int startCameraPreview(int channel) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                mCookie++;
                jsonObject.put("cmd", PREVIEW_COMMAND);
                jsonObject.put("action", 1);
                jsonObject.put("channel", channel);
                jsonObject.put("cookie", mCookie);
                mCameraIdx = channel;
            } catch (JSONException e) {
                Log.e("json exception:" + e);
            }
            sendMsg(jsonObject.toString());
            return 0;
        } else {
            Log.e("startCameraPreview failed, no connecttion");
            return -1;
        }
    }

    public void getGPSInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("cmd", GETGPSINFO_COMMAND);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendMsg(jsonObject.toString());
                } else {
                    Log.e("getGPSInfo failed, WS disconnect");
                }
            }
        }, "getGPSInfo").start();
    }

    public void getInfo(final String type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("cmd", GETINFO_COMMAND);
                        jsonObject.put("type", type);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendMsg(jsonObject.toString());
                } else {
                    Log.e("getInfo failed, WS disconnect");
                }
            }
        }, "getInfo").start();
    }

    public void getTireBeltInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("cmd", GETINFO_COMMAND);
                        jsonObject.put("type", TIRE_BELT_TYPE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendMsg(jsonObject.toString());
                } else {
                    Log.e("getTireBeltInfo failed, WS disconnect");
                }
            }
        }, "getTireBeltInfo").start();
    }

//    public void getSnrStatThisReboot() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
//                    JSONObject jsonObject = new JSONObject();
//                    try {
//                        jsonObject.put("cmd", GETINFO_COMMAND);
//                        jsonObject.put("type", SNR_STAT_THIS_BOOT);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    sendMsg(jsonObject.toString());
//                } else {
//                    Log.e("getTireBeltInfo failed, WS disconnect");
//                }
//            }
//        }, "getTireBeltInfo").start();
//    }

    public void getDmodInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("cmd", GETINFO_COMMAND);
                        jsonObject.put("type", DMOD_TYPE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendMsg(jsonObject.toString());
                } else {
                    Log.e("getDmodInfo failed, WS disconnect");
                }
            }
        }, "getDmodInfo").start();
    }

    public void stopSnrStat() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("cmd", CONTROL_COMMAND);
                        jsonObject.put("type", "stop_snr_stat");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendMsg(jsonObject.toString());
                } else {
                    Log.e("stopSnrStat failed, WS disconnect");
                }
            }
        }, "stopSnrStat").start();
    }

    public void startSnrStat() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("cmd", CONTROL_COMMAND);
                        jsonObject.put("type", "start_snr_stat");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendMsg(jsonObject.toString());
                } else {
                    Log.e("startSnrStat failed, WS disconnect");
                }
            }
        }, "startSnrStat").start();
    }

    public void getWaytroincInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("cmd", GETINFO_COMMAND);
                        jsonObject.put("type", WAYTRONIC_TYPE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendMsg(jsonObject.toString());
                } else {
                    Log.e("getWaytroincInfo failed, WS disconnect");
                }
            }
        }, "getWaytroincInfo").start();
    }

    private final String AVAILABLE_DATE = "availdate";
    private final String AVAILABLE_TRIP = "date";
    private final String ALL = "all";

    public void getTrip(final String date) {
        new Thread() {
            @Override
            public void run() {
                if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
                    JSONObject jso = new JSONObject();
                    try {
                        jso.put("cmd", DeviceConn.TRIPINFO_COMMAND);
                        jso.put(AVAILABLE_TRIP, date);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendMsg(jso.toString());
                }
            }
        }.start();
    }

    public void getTripFromTo(int starttime, int endtime) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jso = new JSONObject();
            try {
                jso.put("cmd", DeviceConn.TRIPINFO_COMMAND);
                jso.put("starttime", starttime);
                jso.put("endtime", endtime);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sendMsg(jso.toString());
        }
    }

    public void getTripDate() {
        new Thread() {
            @Override
            public void run() {
                if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
                    JSONObject jso = new JSONObject();
                    try {
                        jso.put("cmd", DeviceConn.TRIPINFO_COMMAND);
                        jso.put(AVAILABLE_DATE, ALL);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendMsg(jso.toString());
                }
            }
        }.start();
    }

    //download hole trip
    public void downHoleTrip(final long tripstarttime) {
        new Thread() {
            @Override
            public void run() {
                if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("cmd", FILELIST_COMMAND);
                        jsonObject.put("tripstarttime", tripstarttime);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendMsg(jsonObject.toString());
                } else {
                    Log.e("downHoleTrip failed, WS disconnect");
                }
            }
        }.start();
    }

    public void getFileList(final String date) {
        new Thread() {
            @Override
            public void run() {
                if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("cmd", FILELIST_COMMAND);
                        jsonObject.put("date", date);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendMsg(jsonObject.toString());
                } else {
                    Log.e("getFileList failed, WS disconnect");
                }
            }
        }.start();
    }

    public void getDeviceInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("cmd", GETDEVICEINFO_COMMAND);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendMsg(jsonObject.toString());
                } else {
                    Log.e("getDeviceInfo failed, WS disconnect");
                }
            }
        }, "getDeviceInfo").start();
    }


    public void setAlarmTime(final int post) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("cmd", SETDEVICEINFO_COMMAND);
                        JSONObject media = new JSONObject();
                        jsonObject.put("media", media);
                        JSONObject front = new JSONObject();
                        JSONObject rear = new JSONObject();
                        JSONObject inside = new JSONObject();
                        media.put("Front", front);
                        media.put("Rear", rear);
                        media.put("Inside", inside);
                        front.put("prev", 10);
                        front.put("post", post);
                        rear.put("prev", 10);
                        rear.put("post", post);
                        inside.put("prev", 10);
                        inside.put("post", post);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendMsg(jsonObject.toString());
                } else {
                    Log.e("setAlarmTime failed, WS disconnect");
                }
            }
        }, "setAlarmTime").start();
    }

    public void setCollSens(final double collSens) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("cmd", SETDEVICEINFO_COMMAND);
                        jsonObject.put("gsensorsensitivity", collSens);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendMsg(jsonObject.toString());
                } else {
                    Log.e("setCollSens failed, WS disconnect");
                }
            }
        }, "setCollSens").start();
    }

    public void setResolution(final int ch, final int w, final int h) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("cmd", SETDEVICEINFO_COMMAND);
                        JSONObject media = new JSONObject();
                        jsonObject.put("media", media);
                        JSONObject cam = new JSONObject();
                        if (ch == 1) { //rear cam
                            media.put("Rear", cam);
                        } else { //front cam
                            media.put("Front", cam);
                        }
                        cam.put("w", w);
                        cam.put("h", h);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendMsg(jsonObject.toString());
                } else {
                    Log.e("setResolution failed, WS disconnect");
                }
            }
        }, "setResolution").start();
    }

    public void control(final String type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("cmd", CONTROL_COMMAND);
                        jsonObject.put("type", type);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendMsg(jsonObject.toString());
                } else {
                    Log.e("control failed, WS disconnect");
                }
            }
        }, "control").start();
    }

    public void setFulTankSize(double fueltanksize) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", SETDEVICEINFO_COMMAND);
                jsonObject.put("carinfo", 1);
                jsonObject.put("fueltanksize", fueltanksize);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sendMsg(jsonObject.toString());
        } else {
            Log.e("setFulTankSize failed, WS disconnect");
        }
    }

    public void setDisplacement(double displacement) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", SETDEVICEINFO_COMMAND);
                jsonObject.put("carinfo", 1);
                jsonObject.put("enginedisplacement", displacement);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sendMsg(jsonObject.toString());
        } else {
            Log.e("setDisplacement failed, WS disconnect");
        }
    }

    public void setCarType(int cartype, double tanksize, double displacement) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", SETDEVICEINFO_COMMAND);
                jsonObject.put("carinfo", 1);
                jsonObject.put("cartype", cartype);
                jsonObject.put("fueltanksize", tanksize);
                jsonObject.put("enginedisplacement", displacement);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sendMsg(jsonObject.toString());
        } else {
            Log.e("setCarType failed, WS disconnect");
        }
    }

    public void setWiFiMode(final int mode) {  //1/0 表示开启和关闭
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("cmd", SETDEVICEINFO_COMMAND);
                        jsonObject.put("5GMode", mode);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    sendMsg(jsonObject.toString());
                    Log.d("send cmd: " + jsonObject);
                } else {
                    Log.e("setWiFiMode failed, WS disconnect");
                }
            }
        }, "setWiFiMode").start();
    }

    //websocket implement
    class MyWebSocketClient extends WebSocketClient {
        public MyWebSocketClient(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            Log.d("WebSocket onOpen");
            synchronized (mLock) {
                if (mListener != null) {
                    mListener.onDeviceConnect();
                }
                mConnectted = true;
            }
        }

        @Override
        public void onMessage(String message) {
            Log.d("recv cmd: " + message);
//            synchronized (mLock) {
            if (mListener != null) {
                mListener.onDeviceMessage(message, null);
                try {
                    JSONObject jso = new JSONObject(message);
                    String cmd = jso.optString("cmd");
                    if (cmd.equals(DeviceConn.REPORT_COMMAND)) {
                        int status = jso.optInt("status");
//                        Config.getInstance().setSDStatus(status);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
//            }
        }

        @Override
        public void onMessage(ByteBuffer bytes) {
//            Log.d(  "WebSocket onMessage size: " + bytes.array().length);
//            synchronized (mLock) {

            //probe the cjson header, if cookie not equal to mCookie, drop the packet.
            int position = bytes.position();
            int drop = 0;
            StringBuilder sb = new StringBuilder();
            char c = 1;
            while (bytes.hasRemaining()) {
                c = (char) bytes.get();
                if (c == 0) {
                    break;
                }
                sb.append(c);
            }
            String cmd = sb.toString();
            try {
                JSONObject jso = new JSONObject(cmd);
                int cookie = jso.getInt("cookie");
                if ((cookie != mCookie) && (cookie != -1)) {
                    Log.w("cookie = " + cookie + " mCookie = " + mCookie + " not equal, drop packet");
                    drop = 1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            bytes.position(position);
            if ((drop == 0) && (mListener != null)) {
                mListener.onDeviceMessage(null, bytes);
            }
//            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            Log.d("WebSocket onClose, re-connect 3s later");
            synchronized (mLock) {
                if (mListener != null) {
                    mListener.onDeviceDisconnect();
                }
                mConnectted = false;
            }
            mDeviceConnHandler.sendEmptyMessageDelayed(WEBSOCKET_CONNECT, 3000);
        }

        @Override
        public void onError(Exception ex) {
            Log.d("WebSocket onError");
        }
    }

    public class DeviceConnHandler extends Handler {
        public DeviceConnHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WEBSOCKET_CONNECT:
                    removeMessages(WEBSOCKET_CONNECT);
                    connectWebSocket();
                    break;
            }
        }
    }
}
