package edu.njunet.utils.JsonReader;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import edu.njunet.HttpServer;
import edu.njunet.message.Request;
import edu.njunet.message.Response;
import edu.njunet.utils.JsonReader.JavaBean.ServerDataBean;
import edu.njunet.utils.JsonReader.JavaBean.ServerResourceBean;
import edu.njunet.utils.MIME;
import edu.njunet.utils.MonthToNum;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static edu.njunet.utils.SystemTime.systemTime;

public class ServerJsonReader {
    private final static Path FILE_SYS_JSON_PATH = Paths.get(HttpServer.RESOURCES_ROOT + "/resourceManagement.json");
    private static ServerJsonReader serverJsonReader;
    private final ServerResourceBean resourceBean;

    private ServerJsonReader() {
        String jsonStr = FileUtil.readUtf8String(new File(FILE_SYS_JSON_PATH.toUri()));
        resourceBean = JSON.parseObject(jsonStr, ServerResourceBean.class);
    }

    public static ServerJsonReader getInstance() {
        if (serverJsonReader == null) {
            serverJsonReader = new ServerJsonReader();
        }
        return serverJsonReader;
    }

    public Response createResponse(Request request) {
        Response response = new Response();
        response.setVersion(request.getVersion());
        response.setCode(judgeCode(request));
        setOthersByCode(response, request);
        return response;
    }

    /**
     * @param request request报文
     * @return 状态码
     */
    private String judgeCode(Request request) {
        //TODO
        try {
            for (ServerDataBean list : resourceBean.getResourceList()) {
                if (request.getUrl().equals(list.getUrl())) {
                    if (list.getAllow().contains(request.getMethod())) {
                        boolean contains = request.getHeader().containsKey("If-None-Match") ||
                                request.getHeader().containsKey("If-Modified-Since");
                        if (contains) {//304
//                            if (request.getHeader().containsKey("If-None-Match")) {//TODO maybe change
//                                for (ServerDataBean findEtag : resourceBean.getResourceList()) {
//                                    if (request.getHeader().get("If-None-Match").equals(
//                                            findEtag.getModifiedJudge().getEtag())) {//TODO 强比较方法 equals
//                                        return "304";
//                                    }
//                                }
//                                return "200";
//                            }
                            if (request.getHeader().containsKey("If-Modified-Since")) {
                                String[] sModified = list.getModifiedJudge().getLast_modified().split(" ");
                                String[] rModified = request.getHeader().get("If-Modified-Since").split(" ");
                                String sourceModified = sModified[3] + MonthToNum.Month_Map.get(sModified[2])
                                        + sModified[1] + sModified[4];
                                String requestModified = rModified[3] + MonthToNum.Month_Map.get(rModified[2])
                                        + rModified[1] + rModified[4];
                                if (sourceModified.compareTo(requestModified) <= 0) {// 远端资源的 Last-Modified 首部标识的日期
                                    // 比该首部里列出的值更早，
                                    // 条件匹配不成功
                                    return "304";
                                } else {
                                    return "200";//TODO maybe change
                                }
                            }
                        }
                        boolean isMove = list.getReLocationJudge().getIsMove();
                        if (!isMove) {//TODO Maybe Wrong
                            return "200";
                        }
                        boolean isPermanent = list.getReLocationJudge().getIsPermanent();
                        if (isPermanent) {
                            return "301";
                        }
                        return "302";
                    }
                    return "405";
                }
            }
            return "404";
        } catch (Exception e) {
            e.printStackTrace();
            return "500";
        }

    }

    /**
     * @param response 回复报文
     *                 返回根据状态码改变的报文
     */
    private void setOthersByCode(Response response, Request request) {
        //TODO
        switch (response.getCode()) {
            case 200 -> {          //头部有 Cache-Control, Content-Location, Date, ETag, Expires，和 Vary.
                response.setStatus("OK");
                if (response.getHeader() == null) {
                    response.setHeader(new HashMap<>());
                }
                response.getHeader().put("Vary", "Accept-Encoding");
                response.getHeader().put("Content-Type", MIME.getMimeList().getMimeType(request.getUrl()));
                response.setMessage(ReturnMessage(request.getUrl()));
            }
            case 301 -> {
                response.setStatus("Moved Permanently");
                for (ServerDataBean list : resourceBean.getResourceList()) {
                    if (list.getUrl().equals(request.getUrl())) {
                        if (response.getHeader() == null) {
                            response.setHeader(new HashMap<>());
                        }
                        response.getHeader().put("Location", list.getReLocationJudge().getNew_url());
                        response.getHeader().put("Content-Type", MIME.getMimeList().getMimeType(list.getReLocationJudge().getNew_url()));
                    }
                }
                response.setMessage(ReturnMessage(response.getHeader().get("Location")));
            }
            case 302 -> {
                response.setStatus("Found");
                for (ServerDataBean list : resourceBean.getResourceList()) {
                    if (list.getUrl().equals(request.getUrl())) {
                        response.setMessage(ReturnMessage(list.getReLocationJudge().getNew_url()));
                        response.getHeader().put("Content-Type", MIME.getMimeList().getMimeType(list.getReLocationJudge().getNew_url()));
                    }
                }

            }
            case 304 -> {
                response.setStatus("Not Modified");
                if (response.getHeader() == null) {
                    response.setHeader(new HashMap<>());
                }
                response.getHeader().put("Vary", "Accept-Encoding");
                response.getHeader().put("Content-Type", MIME.getMimeList().getMimeType(request.getUrl()));
            }
            case 404 -> response.setStatus("Not Found");
            case 405 -> response.setStatus("Method Not Allowed");
            case 500 -> response.setStatus("Internal Server Error");
            default -> {
            }

        }

        Map<String, String> map = response.getHeader();
        if (map == null) {
            map = new HashMap<>();
        }
        map.put("Connection", "keep-alive");
        if (response.content() != null) {
            map.put("Content-Length", String.valueOf(response.content().length));
        }
        for (ServerDataBean list : resourceBean.getResourceList()) {
            if (list.getUrl().equals(request.getUrl())) {
                map.put("Date", systemTime());
                int maxAge = maxAge(list.getModifiedJudge().getLast_modified(), systemTime());
                map.put("Cache-Control", "public," + maxAge);
                map.put("Expires", expiresTime(maxAge));
            }
        }
        response.setHeader(map);
    }

    private byte[] ReturnMessage(String url) {//TODO

        try (InputStream resource = new FileInputStream(HttpServer.RESOURCES_ROOT + url)) {
            int remainingByte = resource.available();
            byte[] buffer = new byte[remainingByte];
            int i = 0;
            while (remainingByte > 0) {
                int alreadyRead = resource.read(buffer, i, remainingByte);
                remainingByte -= alreadyRead;
                i += alreadyRead;
            }
            return buffer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int maxAge(String Last_modified, String Date) {
        String[] date = Date.split(" ");
        String[] last_modified = Last_modified.split(" ");
        String[] dateFic = date[4].split(":");
        String[] lastFic = last_modified[4].split(":");
        int res;
        res = (Integer.parseInt(date[3]) - Integer.parseInt(last_modified[3])) * 365 * 24 * 60 * 60
                + (Integer.parseInt(MonthToNum.Month_Map.get(date[2])) - Integer.parseInt(MonthToNum.Month_Map.get(last_modified[2]))) * 30 * 24 * 60 * 60
                + (Integer.parseInt(date[1]) - Integer.parseInt(last_modified[1])) * 24 * 60 * 60
                + (Integer.parseInt(dateFic[0]) - Integer.parseInt(lastFic[0])) * 60 * 60
                + (Integer.parseInt(dateFic[1]) - Integer.parseInt(lastFic[1])) * 60
                + (Integer.parseInt(dateFic[2]) - Integer.parseInt(lastFic[2]));
        return res / 10;
    }

    private String expiresTime(int maxAge) {//todo 默认一年
        java.util.Date date = new Date();
        date.setTime(System.currentTimeMillis() + maxAge * 1000L);
        String dateStr = date.toString();
        String[] dateSt = dateStr.split(" ");
        return dateSt[0] + ", " + dateSt[2] + " " + dateSt[1]
                + " " + dateSt[5] + " " + dateSt[3] + " GMT";
    }
}