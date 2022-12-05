package utils.JsonReader.JavaBean;

import java.util.List;

public class ServerResourceBean {
    private List<ServerDataBean> resourceList;

    private List<LoginDataBean> Login;

    public List<ServerDataBean> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<ServerDataBean> list) {
        resourceList = list;
    }

    public List<LoginDataBean> getLogin() {return Login; }

    public void setLogin(List<LoginDataBean> lb) {Login = lb; }
}
