package cool.wuhao.scaninputlib;

import android.content.Context;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import fi.iki.elonen.NanoHTTPD;

public class ScanInputWebServer extends NanoHTTPD {

    private final Context context;

    public ScanInputWebServer(Context context, int port) {
        super(port);
        this.context = context;
    }

    @Override
    public Response serve(IHTTPSession session) {
        try {
            int htmlRawId = R.raw.websocket_client;
            InputStream inputStream = context.getResources().openRawResource(htmlRawId); // 读取assets目录下的HTML文件
            byte[] fileContent = IOUtils.toByteArray(inputStream); // 使用Apache Commons IO库将输入流转换为字节数组
            String htmlContent = new String(fileContent, StandardCharsets.UTF_8); // 转换为字符串
            return newFixedLengthResponse(Response.Status.OK, MIME_HTML, htmlContent); // 返回HTML内容
        } catch (IOException e) {
            e.printStackTrace();
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Error loading HTML file");
        }
    }

}
