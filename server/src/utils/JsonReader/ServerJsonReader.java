package utils.JsonReader;
import HttpServer.HttpServer;
import HttpServer.Message.Request;
import HttpServer.Message.Response;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import utils.JsonReader.JavaBean.ServerDataBean;
import utils.JsonReader.JavaBean.ServerResourceBean;
import utils.MonthToNum;

import java.io.*;
import java.util.Map;
import java.util.Objects;

public class ServerJsonReader {
    private final static String file_path = "D:/HTTP/HTTP-Proj/server/src/HttpServer/webRoot/resourceManagement.json";

    private final ServerResourceBean resourceBean;

    private static ServerJsonReader serverJsonReaderInstance; //单例模式

    private ServerJsonReader() {
        String jsonStr = FileUtil.readUtf8String(new File(file_path));
        resourceBean = JSON.parseObject(jsonStr, ServerResourceBean.class);
    }

    public static ServerJsonReader getInstance() {
        if (serverJsonReaderInstance == null) {
            serverJsonReaderInstance = new ServerJsonReader();
        }
        return serverJsonReaderInstance;
    }

    public Response createResponse(Request request) {
        Response response = new Response();
        response.setVersion(request.getVersion());
        response.setCode(judgeCode(request));
        setOthersByCode(response,request);
        return response;
    }

    /**
     * @param request request报文
     * @return 状态码
     */
    private String judgeCode(Request request) {
        //TODO
        try{
            for(ServerDataBean list : resourceBean.getResourceList()){
                if(Objects.equals(request.getUrl(), list.getUrl())){
                    if(Objects.equals(request.getMethod(), list.getAllow())){
                        boolean contains = request.getHeader().containsKey("If-None-Match") ||
                                request.getHeader().containsKey("If-Modified-Since");
                        if(contains){//304
                            if(request.getHeader().containsKey("If-None-Match")){//TODO maybe change
                                for(ServerDataBean findEtag : resourceBean.getResourceList()){
                                    if(Objects.equals(request.getHeader().get("If-None-Match"),
                                            findEtag.getModifiedJudge().getEtag())){//TODO 弱比较方法
                                        return "304";
                                    }
                                }
                                return "200";
                            }
                            if(request.getHeader().containsKey("If-Modified-Since")){
                                String[] sModified = list.getModifiedJudge().getLast_modified().split(" ");
                                String[] rModified = request.getHeader().get("Last-Modified").split(" ");
                                String sourceModified = sModified[3]+ MonthToNum.Month_Map.get(sModified[2])
                                        + sModified[1]+ sModified[4];
                                String requestModified = rModified[3]+ MonthToNum.Month_Map.get(rModified[2])
                                        + rModified[1]+ rModified[4];
                                if(sourceModified.compareTo(requestModified) <= 0){//远端资源的 Last-Modified 首部标识的日期比在该首部中列出的值要更早，
                                                                                   // 条件匹配不成功
                                    return "304";
                                }else {
                                    return "200";//TODO maybe change
                                }
                            }
                        }
                        boolean isMove = list.getReLocationJudge().getIsMove();
                        if(!isMove) {//TODO Maybe Wrong
                            return "200";
                        }
                        boolean isPermanent = list.getReLocationJudge().getIsPermanent();
                        if(isPermanent){
                            return "301";
                        }
                        return "302";
                    }
                    return "405";
                }
            }
            return "404";
        }catch (Exception e){
            return "500";
        }

    }

    /**
     * @param response 回复报文
     * 返回根据状态码改变的报文
     * */
    private void setOthersByCode(Response response, Request request) {
        //TODO
        switch (response.getCode()) {
            case 200 -> {          //头部有 Cache-Control, Content-Location, Date, ETag, Expires，和 Vary.
                response.setStatus("OK");
                response.setMessage(ReturnMessage(request.getUrl()));
            }
            case 301 -> {
                response.setStatus("Moved Permanently");
                for (ServerDataBean list : resourceBean.getResourceList()) {
                    if (Objects.equals(list.getUrl(), request.getUrl())) {
                        response.getHeader().put("Location", list.getReLocationJudge().getNew_url());
                    }
                }
                response.setMessage(ReturnMessage(request.getUrl()));
            }
            case 302 -> {
                response.setStatus("Found");
                response.setMessage(ReturnMessage(request.getUrl()));
            }
            case 304 -> response.setStatus("Not Modified");
            case 404 -> response.setStatus("Not Found");
            case 405 -> response.setStatus("Method Not Allowed");
            case 500 -> response.setStatus("Internal Server Error");
            default -> {
            }
        }
    }

    private String ReturnMessage(String url){//TODO
        InputStream resource = HttpServer.class.getResourceAsStream(HttpServer.ROOT_PATH + url);
        StringBuilder sb = new StringBuilder();
        try{
            assert resource != null;
            BufferedReader bf = new BufferedReader(new InputStreamReader(resource));
            String line;
            while ((line = bf.readLine()) != null) {
                sb.append(line);
                sb.append("\r\n");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        ServerJsonReader serverJsonReader = ServerJsonReader.getInstance();
        System.out.println(JSON.toJSONString(serverJsonReader.resourceBean));
    }


}