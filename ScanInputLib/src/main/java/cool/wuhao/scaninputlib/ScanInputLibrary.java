package cool.wuhao.scaninputlib;

import static cool.wuhao.scaninputlib.Util.getLocalIpAddress;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.IOException;

import fi.iki.elonen.NanoWSD;

public class ScanInputLibrary {
    private static ScanInputLibrary instance;
    private ScanInputWebSocketServer webSocketServer;
    private ScanInputWebServer webServer;
    protected ScanInputCallback inputCallback; // Changed from private to protected
    private String webSocketUrl;

    // Factory method to get instance
    public static ScanInputLibrary getInstance() {
        if (instance == null) {
            instance = new ScanInputLibrary();
        }
        return instance;
    }

    // Method to set the input callback
    public void setInputCallback(ScanInputCallback callback) {
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


    public void startServer(int webPort, int wsPort, ScanInputCallback callback) {
        this.inputCallback = callback;
        webServer = new ScanInputWebServer(webPort);
        webSocketServer = new ScanInputWebSocketServer(wsPort);

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


    // Method to stop the server
    public void stopServer() {
        if (webServer != null) {
            webServer.stop();
        }
        if (webSocketServer != null) {
            webSocketServer.stop();
        }
    }

}