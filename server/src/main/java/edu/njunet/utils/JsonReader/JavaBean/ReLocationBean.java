package edu.njunet.utils.JsonReader.JavaBean;

public class ReLocationBean {
    private Boolean IsMove;
    private String New_url;
    private Boolean IsPermanent;

    public Boolean getIsMove() {
        return IsMove;
    }

    public void setIsMove(Boolean judge) {
        IsMove = judge;
    }

    public String getNew_url() {
        return New_url;
    }

    public void setNew_url(String url) {
        New_url = url;
    }

    public Boolean getIsPermanent() {
        return IsPermanent;
    }

    public void setIsPermanent(Boolean judge) {
        IsPermanent = judge;
    }

}
