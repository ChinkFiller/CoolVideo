package com.ruoyi.web.appWeb;

import fi.iki.elonen.NanoHTTPD;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class AppWeb extends NanoHTTPD {
    // classpath 中静态资源目录
    private final String staticPathPrefix = "webApp";

    // 代理路径前缀
    private final String proxyPath="/webApp/";
    // 反向代理目标地址
    private String proxyTarget;

    public AppWeb(Integer servicePort,Integer managerPort) throws IOException {
        super(managerPort);
        start(SOCKET_READ_TIMEOUT, false);
        this.proxyTarget = "http://localhost:"+servicePort;
        System.out.println("Web前端已启动在: http://localhost:"+managerPort);
    }

    @Override
    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
        String uri = session.getUri();

        // API 反向代理
        if (uri.startsWith(proxyPath)) {
            return proxy(session);
        }

        // 静态资源（包含 Vue SPA 伪静态支持）
        return staticFile(uri);
    }

    private NanoHTTPD.Response staticFile(String uri) {
        if (uri.equals("/")) {
            return readClasspathFile("/index.html");
        }

        // 尝试读取静态文件
        NanoHTTPD.Response fileResp = readClasspathFile(uri);

        // 文件不存在 → vue history 模式处理
        if (fileResp == null) {
            return readClasspathFile("/index.html");  // 伪静态回退
        }

        return fileResp;
    }

    private NanoHTTPD.Response readClasspathFile(String uri) {
        String filePath = staticPathPrefix + (uri.startsWith("/") ? uri : "/" + uri);

        InputStream is = getClass().getClassLoader().getResourceAsStream(filePath);
        if (is == null) {
            return null; // 让上层逻辑处理
        }

        String mime = getMimeType(uri);

        return newChunkedResponse(NanoHTTPD.Response.Status.OK, mime, is);
    }

    private String getMimeType(String uri) {
        if (uri.endsWith(".html")) return "text/html";
        if (uri.endsWith(".js")) return "application/javascript";
        if (uri.endsWith(".css")) return "text/css";
        if (uri.endsWith(".png")) return "image/png";
        if (uri.endsWith(".jpg") || uri.endsWith(".jpeg")) return "image/jpeg";
        if (uri.endsWith(".svg")) return "image/svg+xml";
        if (uri.endsWith(".json")) return "application/json";
        if (uri.endsWith(".woff")) return "font/woff";
        if (uri.endsWith(".woff2")) return "font/woff2";
        return "application/octet-stream";
    }


    // =====================
    // 反向代理
    // =====================
    private NanoHTTPD.Response proxy(NanoHTTPD.IHTTPSession session) {
        try {
            String uri = session.getUri().replace(proxyPath, "/");
            String query = session.getQueryParameterString();
            String targetUrl = proxyTarget + uri + (query == null ? "" : "?" + query);

            HttpURLConnection conn = (HttpURLConnection) new URL(targetUrl).openConnection();
            conn.setRequestMethod(session.getMethod().name());

            // 复制请求头
            for (Map.Entry<String, String> h : session.getHeaders().entrySet()) {
                // 必须过滤 Host，否则可能导致后端报错
                if (!h.getKey().equalsIgnoreCase("host")) {
                    conn.setRequestProperty(h.getKey(), h.getValue());
                }
            }


            // ============================
            // 处理 POST/PUT 请求转发
            // ============================
            if (session.getMethod() == NanoHTTPD.Method.PUT || session.getMethod() == NanoHTTPD.Method.POST) {
                conn.setDoOutput(true);
                try {
                    int contentLength = Integer.parseInt(session.getHeaders().get("content-length"));
                    byte[] buffer = new byte[contentLength];

                    InputStream is = session.getInputStream();
                    int readTotal = 0;
                    while (readTotal < contentLength) {
                        int r = is.read(buffer, readTotal, contentLength - readTotal);
                        if (r == -1) break; // 避免死循环
                        readTotal += r;
                    }

                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(buffer, 0, readTotal);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            int respCode = conn.getResponseCode();
            InputStream respStream = respCode >= 400
                    ? conn.getErrorStream()
                    : conn.getInputStream();

            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int len;
            while ((len = respStream.read(buf)) != -1) {
                bao.write(buf, 0, len);
            }


            NanoHTTPD.Response response = newFixedLengthResponse(
                    NanoHTTPD.Response.Status.lookup(respCode),
                    conn.getContentType(),
                    new ByteArrayInputStream(bao.toByteArray()),
                    bao.size()
            );

            // 转发响应头
            conn.getHeaderFields().forEach((k, v) -> {
                if (k != null && v != null) {
                    for (String hv : v) response.addHeader(k, hv);
                }
            });

            // 防止NanoHTTPD自动GZIP
            response.setGzipEncoding(false);

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR,
                    "text/plain",
                    "Proxy Error");
        }
    }
}
