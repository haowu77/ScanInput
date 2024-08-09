package cool.wuhao.scaninputlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.IOException;

public class ScanInputLibrary {

    public static final String TAG = ScanInputLibrary.class.getName();
    protected ScanInputCallback inputCallback; // Changed from private to protected
    private final Context context;

    public ScanInputLibrary(Context context) {
        this.context = context;
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
        ScanInputWebSocketServer webSocketServer = new ScanInputWebSocketServer(wsPort, callback);

        try {
            webServer.start();
            webSocketServer.start();
            Log.d(TAG, "startServer: Servers started successfully");
        } catch (IOException e) {
            Log.d(TAG, "exception: " + e.getMessage());
        }
    }

}