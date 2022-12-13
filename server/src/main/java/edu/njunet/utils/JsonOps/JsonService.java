package edu.njunet.utils.JsonOps;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import edu.njunet.protocol.MIME;
import edu.njunet.protocol.Request;
import edu.njunet.protocol.Response;
import edu.njunet.protocol.Servlet;
import edu.njunet.session.HttpServer;
import edu.njunet.utils.JsonOps.JavaBean.LoginDataBean;
import edu.njunet.utils.JsonOps.JavaBean.ServerDataBean;
import edu.njunet.utils.JsonOps.JavaBean.ServerResourceBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static edu.njunet.utils.JsonOps.SystemTime.systemTime;

public class JsonService implements Servlet {
    private static final Path FILE_SYS_JSON_PATH = Paths.get(HttpServer.RESOURCES_ROOT + "/resourceManagement.json");
    private static final JsonService JSON_SERVICE = new JsonService();

    private static final ServerResourceBean resourceBean =
            JSON.parseObject(FileUtil.readUtf8String(new File(FILE_SYS_JSON_PATH.toUri())), ServerResourceBean.class);

    public static JsonService getInstance() {
        return JSON_SERVICE;
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

    @Override
    public void service(Request request, Response response) {
        if (request.getUrl().equals("/register")) {
            register(request, response);
        } else if (request.getUrl().equals("/login")) {
            login(request, response);
        } else {
            response.setVersion(request.getVersion());
            response.setCode(judgeCode(request));
            setOthersByCode(response, request);
        }
    }

    private void register(Request request, Response response) {
        response.setVersion(request.getVersion());
        if (!request.getMethod().equals("POST")) {
            response.setCode("405");
            response.setStatus("Method Not Allowed");
        } else {
            response.setCode("200");
            response.setStatus("OK");
            String[] userInformation = request.text().split("\r\n", 2);
            String password = userInformation[1].split(":", 2)[1];
            String userName = userInformation[0].split(":", 2)[1];
            boolean isExist = false;
            for (LoginDataBean loginDataBean: resourceBean.getLogin()) {
                if (userName.equals(loginDataBean.getUser_name())) {
                    isExist = true;
                    break;
                }
            }
            if (isExist) {
                response.setMessage("Account have existed!".getBytes());
            } else {
                LoginDataBean newUser = new LoginDataBean();
                newUser.setUser_name(userName);
                newUser.setPassword(password);
                resourceBean.getLogin().add(newUser);
                response.setMessage("Register complete!".getBytes());
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
        response.setHeader(map);

    }

    private void login(Request request, Response response) {
        response.setVersion(request.getVersion());
        if (!request.getMethod().equals("POST")) {
            response.setCode("405");
            response.setStatus("Method Not Allowed");
        } else {
            response.setCode("200");
            response.setStatus("OK");
            String[] userInformation = request.text().split("\r\n", 2);
            String password = userInformation[1].split(":", 2)[1];
            String userName = userInformation[0].split(":", 2)[1];

            boolean isExist = false;
            for (LoginDataBean loginDataBean: resourceBean.getLogin()) {
                if (userName.equals(loginDataBean.getUser_name())) {
                    isExist = true;
                    if (password.equals(loginDataBean.getPassword())) {
                        response.setMessage(("Welcome back, " + userName + "!").getBytes());
                    } else {
                        response.setMessage("Password error!".getBytes());
                    }
                    break;
                }
            }
            if (!isExist) {
                response.setMessage("Account not exist!".getBytes());
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
        response.setHeader(map);
    }
}