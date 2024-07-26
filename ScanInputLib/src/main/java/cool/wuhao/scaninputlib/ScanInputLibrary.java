package cool.wuhao.scaninputlib;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;

public class ScanInputLibrary {
    private static ScanInputLibrary instance;
    private WebSocketServer webSocketServer;
    private SimpleWebServer webServer;
    protected InputCallback inputCallback; // Changed from private to protected
    private String webSocketUrl;

    // Factory method to get instance
    public static ScanInputLibrary getInstance() {
        if (instance == null) {
            instance = new ScanInputLibrary();
        }
        return instance;
    }

    // Method to set the input callback
    public void setInputCallback(InputCallback callback) {
        this.inputCallback = callback;
    }

    // Method to get QR code image
    public Bitmap generateQrCode(String data) {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 200, 200);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bitmap;
    }


    public void startServer(int webPort, int wsPort, InputCallback callback) {
        this.inputCallback = callback;
        webServer = new SimpleWebServer(webPort);
        webSocketServer = new WebSocketServer(wsPort);

        String ipAddress = getLocalIpAddress(); // 获取本地 IP 地址
        webSocketUrl = "ws://" + ipAddress + ":" + wsPort;

        try {
            webServer.start();
            webSocketServer.start();
            System.out.println("Servers started successfully");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to start servers");
        }
    }

    // 获取本地 IP 地址
//    public String getLocalIpAddress() {
//        try {
//            // Iterate through all network interfaces
//            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
//                NetworkInterface networkInterface = en.nextElement();
//                // Iterate through all IP addresses associated with the network interface
//                for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
//                    InetAddress inetAddress = enumIpAddr.nextElement();
//                    // Check if the address is an IPv4 address, not a loopback address, and is a site-local address
//                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress()) {
//                        return inetAddress.getHostAddress();
//                    }
//                }
//            }
//        } catch (SocketException ex) {
//            ex.printStackTrace();
//        }
//        return "Cannot get IP address!";
//    }
    public String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            if (networkInterfaces == null) {
                System.out.println("No network interfaces found");
                return "Cannot get IP address!";
            }

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                if (inetAddresses == null) {
                    System.out.println("No IP addresses found for network interface: " + networkInterface.getName());
                    continue;
                }

                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress()) {
                        String ipAddress = inetAddress.getHostAddress();
                        System.out.println("Local IP Address: " + ipAddress);
                        return ipAddress;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("SocketException occurred while getting IP address");
        }

        System.out.println("Cannot get IP address!");
        return "Cannot get IP address!";
    }

    // Method to stop the server
    public void stopServer() {
        if (webServer != null) {
            webServer.stop();
        }
        if (webSocketServer != null) {
            webSocketServer.stop();
        }
    }

    // Web server to serve the HTML page
    private class SimpleWebServer extends NanoHTTPD {
        public SimpleWebServer(int port) {
            super(port);
        }

        @Override
        public Response serve(IHTTPSession session) {
            String html = "<html><body>" +
                    "<p>Scan Input Test</p>" +
                    "<input type='text' id='inputField' oninput='sendData()'>" +
                    "<script>" +
                    "function sendData() {" +
                    "var input = document.getElementById('inputField').value;" +
                    "var ws = new WebSocket('" + webSocketUrl + "');" +
                    "ws.onopen = function() {ws.send(input);};}" +
                    "</script></body></html>";
            return newFixedLengthResponse(html);
        }
    }

    // WebSocket server to handle input
    private class WebSocketServer extends NanoWSD {
        private final Handler handler = new Handler(Looper.getMainLooper());

        public WebSocketServer(int port) {
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

    // Interface for input callback
    public interface InputCallback {
        void onInputReceived(String input);
    }
}