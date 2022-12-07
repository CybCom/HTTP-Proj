package edu.njunet.utils.JsonReader.JavaBean;

public class ServerDataBean {
    private String Url;
    private String Allow;
    private ModifiedBean ModifiedJudge;
    private ReLocationBean ReLocationJudge;

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getAllow() {
        return Allow;
    }

    public void setAllow(String allow) {
        Allow = allow;
    }

    public ModifiedBean getModifiedJudge() {
        return ModifiedJudge;
    }

    public void setModifiedJudge(ModifiedBean mb) {
        ModifiedJudge = mb;
    }

    public ReLocationBean getReLocationJudge() {
        return ReLocationJudge;
    }

    public void setReLocationJudge(ReLocationBean rb) {
        ReLocationJudge = rb;
    }
}
