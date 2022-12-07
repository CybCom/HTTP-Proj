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

    private final static String stored_path = "C:/Users/lenovo-002/HTTP-Proj/client/src/HttpClient/cache";
    private final ClientResourceBean resourceBean;

    public ClientJsonReader() {
        String jsonStr = FileUtil.readUtf8String(new File(file_path));
        resourceBean = JSON.parseObject(jsonStr, ClientResourceBean.class);
    }

    /***
     * 在client的缓存json文件中遍历，看有没有对应的url，若要则改为条件请求
     * @param request 原请求
     */
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

    /***
     * 根据响应报文来对client缓存做处理
     * @param response 响应报文
     * @param url client请求的url
     * @throws IOException
     */
    public void updateResource(Response response, String url) throws IOException {
        int res = update(response, url);
        if (res == 1) {
            FileUtil.writeUtf8String(JSON.toJSONString(resourceBean), new File(file_path)); //若json文件有更新，则写回
        }
    }

    /***
     * updateResource()方法的内部实现
     * @param response 响应报文
     * @param url client请求的url
     * @return 1 表示json文件对应的ClientResourceBean类有更新，需写回json文件 ，0 表示不需要
     * @throws IOException
     */

    private int update(Response response, String url) throws IOException {
        int code = response.getCode();
        switch (code) {
            case 200:
            case 302: {
                List<ClientDataBean> resourceList = resourceBean.getResourceList();
                for (ClientDataBean clientDataBean : resourceList) {
                    if (url.equals(clientDataBean.getUrl())) {
                        clientDataBean.getModifiedBean().setLast_modified(String.valueOf(System.currentTimeMillis()/1000)); //TODO
                        FileUtil.writeBytes(response.content(), new File(clientDataBean.getPath()));
                        return 1;
                    }
                }

                addOneDataBean(url);
                FileUtil.writeBytes(response.content(), new File(stored_path+url));
                return 1;
            }
            case 301: {
                String new_url = response.getHeader().get("Location");
                List<ClientDataBean> resourceList = resourceBean.getResourceList();
                for (ClientDataBean clientDataBean : resourceList) {
                    if (url.equals(clientDataBean.getUrl())) {
                        clientDataBean.setUrl(new_url);
                        clientDataBean.getModifiedBean().setLast_modified(String.valueOf(System.currentTimeMillis()/1000)); //TODO
                        FileUtil.writeBytes(response.content(), new File(clientDataBean.getPath()));
                        return 1;
                    }
                }

                addOneDataBean(new_url);
                FileUtil.writeBytes(response.content(), new File(stored_path+new_url));
                return 1;
            }
            case 304: {
                String resource_path = null;
                List<ClientDataBean> resourceList = resourceBean.getResourceList();
                for (ClientDataBean clientDataBean : resourceList) {
                    if (url.equals(clientDataBean.getUrl())) {
                        clientDataBean.getModifiedBean().setLast_modified(String.valueOf(System.currentTimeMillis()/1000)); //TODO
                        resource_path = clientDataBean.getPath();
                    }
                }
                assert resource_path != null;
                InputStream resource = HttpClient.class.getResourceAsStream(resource_path);
                assert resource != null;
                int remainingByte = resource.available();
                byte[] buffer = new byte[remainingByte];
                int i = 0;
                while (remainingByte > 0) {
                    int alreadyRead = resource.read(buffer, i, remainingByte);
                    remainingByte -= alreadyRead;
                    i += alreadyRead;
                }
                response.setMessage(buffer);
                return 0;
            }
            default:
                return 0;
        }
    }

    /***
     * 向ClientResourceBean的ResourceList中加入一条DataBean
     * @param url client请求的url
     */
    private void addOneDataBean(String url) {
        ClientDataBean clientDataBean = new ClientDataBean();
        ModifiedBean modifiedBean = new ModifiedBean();
        modifiedBean.setLast_modified(String.valueOf(System.currentTimeMillis() / 1000));  //TODO
        modifiedBean.setEtag("1");
        clientDataBean.setUrl(url);
        clientDataBean.setModifiedBean(modifiedBean);
        clientDataBean.setPath(stored_path + url);
        resourceBean.getResourceList().add(clientDataBean);
    }

}
