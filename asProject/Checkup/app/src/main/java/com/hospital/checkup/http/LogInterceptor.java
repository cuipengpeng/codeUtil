package com.hospital.checkup.http;


import com.hospital.checkup.utils.LogUtils;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * 日志拦截
 */

public class LogInterceptor implements Interceptor {
    public static final String TAG = "LogInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        //the request url
        String url = request.url().toString();
        //the request method
        String method = request.method();
        long t1 = System.nanoTime();
        LogUtils.printLog(String.format(Locale.getDefault(), "Sending %s request [url = %s]", method, url));

        Headers headers = request.headers();
        LogUtils.printLog(String.format(Locale.getDefault(), "%s %s", method,
                headers.toString()));



        //the request body
        RequestBody requestBody = request.body();

        if (requestBody != null) {
            StringBuilder sb = new StringBuilder("Request Body [");
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset = Charset.forName("UTF-8");
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(charset);
            }
            if (isPlaintext(buffer)) {
                sb.append(buffer.readString(charset));
                sb.append(" (Content-Type = ").append(contentType.toString()).append(",")
                        .append(requestBody.contentLength()).append("-byte body)");
            } else {
                sb.append(" (Content-Type = ").append(contentType.toString())
                        .append(",binary ").append(requestBody.contentLength()).append("-byte body omitted)");
            }
            sb.append("]");
            LogUtils.printLog(String.format(Locale.getDefault(), "%s %s", method, java.net.URLDecoder.decode(sb.toString(), "utf-8")));
           }
        Response response = chain.proceed(request);
        long t2 = System.nanoTime();
        //the response time
        LogUtils.printLog(String.format(Locale.getDefault(), "Received response for [url = %s] in %.1fms", url, (t2 - t1) / 1e6d));

        //the response state
        LogUtils.printLog(String.format(Locale.CHINA, "Received response is %s ,message[%s],code[%d]", response.isSuccessful() ? "success" : "fail", response.message(), response.code()));

        //the response data
        ResponseBody body = response.body();

        BufferedSource source = body.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.buffer();
        Charset charset = Charset.defaultCharset();
        MediaType contentType = body.contentType();
        if (contentType != null) {
            charset = contentType.charset(charset);
        }
        String bodyString = buffer.clone().readString(charset);
        LogUtils.printLog(String.format("Received response json string [%s]", bodyString));
//        request.headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8");
        return response;
    }

    private boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

}
