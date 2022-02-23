package com.example.jetpackdemo.connect;

import java.nio.ByteBuffer;

public class WebSocketEvent {

    public enum Type {
        OPEN,
        CLOSE,
        ERROR,

        DEVICEINFO,
        PREVIEW,
        PLAYBACK,
        TRIP_INFO,
        FILE_LIST,
        SET_DEVICE_INFO,
        GET_DEVICE_INFO,
        GET_GPS_INFO,
        GET_INFO,
        CONTROL,
        REPORT,
        UPGRADE,
        UPGRADE_PROGRESS,
        AUDIO_PLAY,
        FIRMWARE_UPGRADE,
        PHOTO,
        GETDEBUG,

        BUFFER
    }

    private Type type;
    private String msg;
    private ByteBuffer buffer;

    public WebSocketEvent(Type type, String msg, ByteBuffer buffer) {
        this.type = type;
        this.msg = msg;
        this.buffer = buffer;
    }

    public WebSocketEvent(Type type) {
        this(type, "", null);
    }

    public WebSocketEvent(String msg) {
        this(Type.DEVICEINFO, msg, null);
    }

    public WebSocketEvent(String msg, ByteBuffer buffer) {
        this(Type.BUFFER, msg, buffer);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }
}
