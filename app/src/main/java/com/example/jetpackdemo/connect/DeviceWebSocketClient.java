package com.example.jetpackdemo.connect;

import static com.example.jetpackdemo.connect.DeviceConn.*;

import com.example.jetpackdemo.util.log.Logger;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.nio.ByteBuffer;

public class DeviceWebSocketClient extends WebSocketClient {

    private Logger logger = Logger.create("DeviceConn");
    private DeviceConn.DeviceConnHandler mHandler;

    public interface WebSocketListener {
        void onOpen();

        void onClose();

        void onError();

        void onMessage(String message);
    }

    private WebSocketListener mListener;

    public void setListener(WebSocketListener mListener) {
        this.mListener = mListener;
    }

    public DeviceWebSocketClient(URI serverUri, DeviceConn.DeviceConnHandler handler) {
        super(serverUri);
        this.mHandler = handler;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        logger.d("onOpen");
        setConnectionLostTimeout(0);
        mListener.onOpen();
    }

    @Override
    public void onMessage(String message) {
        logger.d("onMessage:" + message);
        mListener.onMessage(message);
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        super.onMessage(bytes);
//        logger.d("onMessage: buffer");

        int position = bytes.position();
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
        bytes.position(position);

//        EventBus.getDefault().postSticky(new WebSocketEvent(cmd, bytes));
    }

    private void handleOut(String message) {
        try {
            JSONObject jso = new JSONObject(message);
            String cmd = jso.optString("cmd");
            WebSocketEvent event = new WebSocketEvent(message);

            if (cmd.equals(PREVIEW_COMMAND)) {
                event.setType(WebSocketEvent.Type.PREVIEW);
            }
            if (cmd.equals(PLAYBACK_COMMAND)) {
                event.setType(WebSocketEvent.Type.PLAYBACK);
            }
            if (cmd.equals(TRIPINFO_COMMAND)) {
                event.setType(WebSocketEvent.Type.TRIP_INFO);
            }
            if (cmd.equals(FILELIST_COMMAND)) {
                event.setType(WebSocketEvent.Type.FILE_LIST);
            }
            if (cmd.equals(SETDEVICEINFO_COMMAND)) {
                event.setType(WebSocketEvent.Type.SET_DEVICE_INFO);
            }
            if (cmd.equals(GETDEVICEINFO_COMMAND)) {
                event.setType(WebSocketEvent.Type.GET_DEVICE_INFO);
            }
            if (cmd.equals(GETGPSINFO_COMMAND)) {
                event.setType(WebSocketEvent.Type.GET_GPS_INFO);
            }
            if (cmd.equals(GETINFO_COMMAND)) {
                event.setType(WebSocketEvent.Type.GET_INFO);
            }
            if (cmd.equals(CONTROL_COMMAND)) {
                event.setType(WebSocketEvent.Type.CONTROL);
            }
            if (cmd.equals(REPORT_COMMAND)) {
                event.setType(WebSocketEvent.Type.REPORT);
            }
            if (cmd.equals(UPGRADE_COMMAND)) {
                event.setType(WebSocketEvent.Type.UPGRADE);
            }
            if (cmd.equals(UPGRADEPROGRESS)) {
                event.setType(WebSocketEvent.Type.UPGRADE_PROGRESS);
            }
            if (cmd.equals(AUDIOPLAY)) {
                event.setType(WebSocketEvent.Type.AUDIO_PLAY);
            }
            if (cmd.equals(FIRMWARE_UPGRADE)) {
                event.setType(WebSocketEvent.Type.FIRMWARE_UPGRADE);
            }
            if (cmd.equals(PHOTO)) {
                event.setType(WebSocketEvent.Type.PHOTO);
            }
            if (cmd.equals(GETDEBUG)) {
                event.setType(WebSocketEvent.Type.GETDEBUG);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            logger.e("handleOut:" + e.getMessage());
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.d("onClose:" + reason);
        mHandler.sendEmptyMessageDelayed(WEBSOCKET_CONNECT, 3000);
        mListener.onClose();
    }

    @Override
    public void onError(Exception ex) {
        logger.d("onError:" + ex.getMessage());
        mListener.onError();
    }
}
