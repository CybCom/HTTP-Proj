package utils.JsonReader;
import Message.Request;
import Message.Response;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import utils.JsonReader.JavaBean.ServerResourceBean;

import java.io.File;

public class ServerJsonReader {
    private final static String file_path = "C:/Users/lenovo-002/HTTP-Proj/server/src/HttpServer/webRoot/resourceManagement.json";

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

    private String judgeCode(Request request) {
        //TODO
        return null;
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