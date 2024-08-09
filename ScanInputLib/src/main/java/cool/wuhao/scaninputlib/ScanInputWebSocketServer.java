package cool.wuhao.scaninputlib;


import android.util.Log;

import java.net.InetSocketAddress;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class ScanInputWebSocketServer extends WebSocketServer {

    public static final String TAG = ScanInputWebSocketServer.class.getName();
    ScanInputCallback scanInputCallback;

    public ScanInputWebSocketServer(int port, ScanInputCallback callback) {
        super(new InetSocketAddress(port));
        scanInputCallback = callback;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "Web Socket Server start: ");
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Log.d(TAG, "onOpen: " + "New connection from " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Log.d(TAG, "onClose: " + "Connection closed by " + (remote ? "remote peer" : "us"));
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Log.d(TAG, "onMessage: "+ message);
//        broadcast(message);
        scanInputCallback.onInputReceived(message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        Log.d(TAG, "onError: " + ex.getMessage());
    }

}