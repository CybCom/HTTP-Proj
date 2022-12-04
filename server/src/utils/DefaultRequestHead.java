package utils;

import java.util.HashMap;
import java.util.Map;

public class DefaultRequestHead {
    public static final Map<String, String> DEFAULT_HEADER = new HashMap<>() {
        {
            put("Accept", " text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            put("Connection", " keep-alive");
            put("User-Agent", " Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36 SLBrowser/8.0.0.9231 SLBChan/103");
            put("Sec-Fetch-Site", " none");
            put("Sec-Fetch-Dest", " document");
            put("Host", " localhost");
            put("Accept-Encoding", " gzip, deflate, br");
            put("Sec-Fetch-Mode", " navigate");
            put("sec-ch-ua", " \" Not A;Brand\";v=\"99\", \"Chromium\";v=\"8\"");
            put("sec-ch-ua-mobile", " ?0");
            put("Upgrade-Insecure-Requests", " 1");
            put("Sec-Fetch-User", " ?1");
            put("Accept-Language", " zh-CN,zh;q=0.9");
        }
    };
}
