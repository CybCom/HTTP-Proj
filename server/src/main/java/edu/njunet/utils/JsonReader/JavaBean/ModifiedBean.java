package edu.njunet.utils.JsonReader.JavaBean;

public class ModifiedBean {
    private String Last_modified;
    private String Etag;

    public String getLast_modified() {
        return Last_modified;
    }

    public void setLast_modified(String timeStamp) {
        Last_modified = timeStamp;
    }

    public String getEtag() {
        return Etag;
    }

    public void setEtag(String etag) {
        Etag = etag;
    }
}