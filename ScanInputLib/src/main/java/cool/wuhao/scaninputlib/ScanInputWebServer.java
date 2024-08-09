package cool.wuhao.scaninputlib;

import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class ScanInputWebServer extends NanoHTTPD {

    public ScanInputWebServer(int port) {
        super(port);
    }

    public ScanInputWebServer(String hostname, int port) {
        super(hostname, port);
    }

//    @Override
//    public Response serve(IHTTPSession session) {
//        String html = "<html><body>" +
//                "<p>Scan Input Test</p>" +
//                "<input type='text' id='inputField' oninput='sendData()'>" +
//                "<script>" +
//                "function sendData() {" +
//                "var input = document.getElementById('inputField').value;" +
//                "var ws = new WebSocket('" + webSocketUrl + "');" +
//                "ws.onopen = function() {ws.send(input);};}" +
//                "</script></body></html>";
//        return newFixedLengthResponse(html);
//    }


    @Override
    public Response serve(IHTTPSession session) {
        String msg = "<html><body><h1>Hello server</h1>\n";
        Map<String, String> parms = session.getParms();
        if (parms.get("username") == null) {
            msg += "<form action='?' method='get'>\n  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
        } else {
            msg += "<p>Hello, " + parms.get("username") + "!</p>";
        }
        return newFixedLengthResponse(msg + "</body></html>\n");
    }
}
