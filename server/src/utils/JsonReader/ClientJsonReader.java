package utils.JsonReader;

import HttpClient.HttpClient;
import HttpServer.Message.Request;
import HttpServer.Message.Response;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import utils.JsonReader.JavaBean.ClientDataBean;
import utils.JsonReader.JavaBean.ClientResourceBean;
import utils.JsonReader.JavaBean.ModifiedBean;

import java.io.*;
import java.util.List;
import java.util.Map;

public class ClientJsonReader {
    private final static String file_path = "C:/Users/lenovo-002/HTTP-Proj/client/src/HttpClient/cache/resourceManagement.json";

    private final static String stored_path = "C:/Users/lenovo-002/HTTP-Proj/client/src/HttpClient/cache/";
    private final ClientResourceBean resourceBean;

    public ClientJsonReader() {
        String jsonStr = FileUtil.readUtf8String(new File(file_path));
        resourceBean = JSON.parseObject(jsonStr, ClientResourceBean.class);
    }

    public void searchUrl(Request request) {
        String url = request.getUrl();
        List<ClientDataBean> resourceBeanList = resourceBean.getResourceList();
        for (ClientDataBean clientDataBean: resourceBeanList) {
            if (url.equals(clientDataBean.getUrl())) {
                Map<String, String> requestHeader = request.getHeader();
                requestHeader.put("If-Modified-Since", clientDataBean.getModifiedBean().getLast_modified());
                requestHeader.put("If-None-Match", clientDataBean.getModifiedBean().getEtag());
                break;
            }
        }
    }

    public void updateResource(Response response, String url) throws IOException {
        int res = update(response, url);
        if (res == 1) {
            FileUtil.writeUtf8String(JSON.toJSONString(resourceBean), new File(file_path)); //若json文件有更新，则写回
        }
    }

    private int update(Response response, String url) throws IOException {
        int code = response.getCode();
        switch (code) {
            case 200:
            case 302: {
                List<ClientDataBean> resourceList = resourceBean.getResourceList();
                for (ClientDataBean clientDataBean : resourceList) {
                    if (url.equals(clientDataBean.getUrl())) {
                        clientDataBean.getModifiedBean().setLast_modified(String.valueOf(System.currentTimeMillis()/1000));
                        FileUtil.writeUtf8String(response.getMessage(), new File(clientDataBean.getPath()));
                        return 1;
                    }
                }

                addOneDataBean(url);
                FileUtil.writeUtf8String(response.getMessage(), new File(stored_path+url));
                return 1;
            }
            case 301: {
                String new_url = response.getHeader().get("Location");
                List<ClientDataBean> resourceList = resourceBean.getResourceList();
                for (ClientDataBean clientDataBean : resourceList) {
                    if (url.equals(clientDataBean.getUrl())) {
                        clientDataBean.setUrl(new_url);
                        clientDataBean.getModifiedBean().setLast_modified(String.valueOf(System.currentTimeMillis()/1000));
                        FileUtil.writeUtf8String(response.getMessage(), new File(clientDataBean.getPath()));
                        return 1;
                    }
                }

                addOneDataBean(new_url);
                FileUtil.writeUtf8String(response.getMessage(), new File(stored_path+new_url));
                return 1;
            }
            case 304: {
                String resource_path = null;
                List<ClientDataBean> resourceList = resourceBean.getResourceList();
                for (ClientDataBean clientDataBean : resourceList) {
                    if (url.equals(clientDataBean.getUrl())) {
                        clientDataBean.getModifiedBean().setLast_modified(String.valueOf(System.currentTimeMillis()/1000));
                        resource_path = clientDataBean.getPath();
                    }
                }
                assert resource_path != null;
                InputStream resource = HttpClient.class.getResourceAsStream(resource_path);
                assert resource != null;
                BufferedReader bf = new BufferedReader(new InputStreamReader(resource));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bf.readLine()) != null) {
                    sb.append(line);
                    sb.append("\r\n");
                }
                String message = sb.toString();
                response.setMessage(message);
                return 0;
            }
            default:
                return 0;
        }
    }

    private void addOneDataBean(String url) {
        ClientDataBean clientDataBean = new ClientDataBean();
        ModifiedBean modifiedBean = new ModifiedBean();
        modifiedBean.setLast_modified(String.valueOf(System.currentTimeMillis() / 1000));
        modifiedBean.setEtag("1");
        clientDataBean.setUrl(url);
        clientDataBean.setModifiedBean(modifiedBean);
        clientDataBean.setPath(stored_path + url);
        resourceBean.getResourceList().add(clientDataBean);
    }

}
