package utils.JsonReader.JavaBean;

public class ClientDataBean {
    private String Url;
    private String Path;
    private ModifiedBean modifiedBean;

    public String getUrl() {return Url; }

    public void setUrl(String url) {Url = url; }

    public String getPath() {return Path; }

    public void setPath(String path) {Path = path; }

    public ModifiedBean getModifiedBean() {return modifiedBean; }

    public void setModifiedBean(ModifiedBean mb) {modifiedBean = mb; }
}
