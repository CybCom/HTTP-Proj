package utils.JsonReader.JavaBean;

import java.util.List;

public class ClientResourceBean {
    private List<ClientDataBean> resourceList;

    public List<ClientDataBean> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<ClientDataBean> list) {
        resourceList = list;
    }
}
