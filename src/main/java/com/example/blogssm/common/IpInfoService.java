package com.example.blogssm.common;

import cn.hutool.json.JSONObject;
import okhttp3.*;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
@Component
public class IpInfoService {
    private static final String URL = "https://apis.map.qq.com/ws/location/v1/ip";
    private static final String KEY = "MH2BZ-4WTK3-2D63K-YZRHP-HM537-HHBD3";

    public String getIpInfo(HttpServletRequest request) throws IOException {
        //本地测试不会有地址信息
        String ip = getIpFromRequest(request);
//        String ip = "47.243.232.38";
//        System.out.println(ip);
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(URL).newBuilder();
        urlBuilder.addQueryParameter("key", KEY);
        urlBuilder.addQueryParameter("ip", ip);

        Request req = new Request.Builder()
                .url(urlBuilder.build().toString())
                .build();
        try (Response response = client.newCall(req).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            JSONObject jsonResponse = new JSONObject(response.body().string());
            JSONObject jsonResult = jsonResponse.getJSONObject("result");
//            System.out.println(jsonResult);
            // Convert the "result" JSONObject to a string
            String resultString = jsonResult.toString();

            return resultString;
        }
    }

    private String getIpFromRequest(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if ("127.0.0.1".equals(ip)) {
                try {
                    ip = InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }
}
