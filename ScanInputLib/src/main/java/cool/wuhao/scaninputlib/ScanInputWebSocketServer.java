package cool.wuhao.scaninputlib;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import fi.iki.elonen.NanoWSD;

public class ScanInputWebSocketServer extends NanoWSD {
    ScanInputCallback inputCallback;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public ScanInputWebSocketServer(int port) {
        super(port);
    }

    @Override
    protected WebSocket openWebSocket(IHTTPSession handshake) {
        return new WebSocket(handshake) {
            @Override
            protected void onMessage(WebSocketFrame message) {
                final String input = message.getTextPayload();
                handler.post(() -> {
                    if (inputCallback != null) {
                        inputCallback.onInputReceived(input);
                    }
                });
            }

            @Override
            protected void onPong(WebSocketFrame pong) {
            }

            @Override
            protected void onOpen() {
            }

            @Override
            protected void onClose(WebSocketFrame.CloseCode code, String reason, boolean initiatedByRemote) {
            }

            @Override
            protected void onException(IOException exception) {
                exception.printStackTrace();
            }
        };
    }
}
