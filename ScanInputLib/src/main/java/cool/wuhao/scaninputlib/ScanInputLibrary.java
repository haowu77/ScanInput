package cool.wuhao.scaninputlib;

import static cool.wuhao.scaninputlib.Util.getLocalIpAddress;

import android.annotation.SuppressLint;
import android.content.Context;
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
    protected ScanInputCallback inputCallback; // Changed from private to protected
    private final Context context;

    // Factory method to get instance
    public static ScanInputLibrary getInstance(Context context) {
        if (instance == null) {
            instance = new ScanInputLibrary(context);
        }
        return instance;
    }

    private ScanInputLibrary(Context context) {
        this.context = context;
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
        ScanInputWebServer webServer = new ScanInputWebServer(context, webPort);
        SimpleWebSocketServer webSocketServer = new SimpleWebSocketServer(wsPort);

        try {
            webServer.start();
            webSocketServer.start();
            System.out.println("Servers started successfully");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to start servers");
        }
    }

}