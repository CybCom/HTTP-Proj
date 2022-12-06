package utils.JsonReader;
import HttpServer.Message.Request;
import HttpServer.Message.Response;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import utils.JsonReader.JavaBean.ServerDataBean;
import utils.JsonReader.JavaBean.ServerResourceBean;

import java.io.File;
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
        setOthersByCode(response);
        return response;
    }

    /**
     * @param request request报文
     * @return 状态码
     */
    private String judgeCode(Request request) {
        //TODO
        for(ServerDataBean list : resourceBean.getResourceList()){
            if(Objects.equals(request.getUrl(), list.getUrl())){
                if(Objects.equals(request.getMethod(), list.getAllow())){
                    boolean contains = request.getHeader().containsKey("If-None-Match") ||
                            request.getHeader().containsKey("If-Modified-Since");
                    if(contains){//304
                        //TODO client 缓存 304
                    }
                    boolean isMove = list.getReLocationJudge().getIsMove();
                    if(isMove) return "200";
                    boolean isPermanent = list.getReLocationJudge().getIsPermanent();
                    if(isPermanent) return "301";
                    return "302";
                }
                return "405";
            }
        }
        return "404";
    }

    private void setOthersByCode(Response response) {
        //TODO
        switch (response.getCode()) {
            case 200:
                break;
            case 301:
                break;
            case 302:
                break;
            case 304:
                break;
            case 404:
                break;
            case 405:
                break;
            case 500:
                break;
            default:
                break;
        }
    }

    public static void main(String[] args) {
        ServerJsonReader serverJsonReader = ServerJsonReader.getInstance();
        System.out.println(JSON.toJSONString(serverJsonReader.resourceBean));
    }


}