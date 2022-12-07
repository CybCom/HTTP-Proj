package edu.njunet.utils;

import java.util.HashMap;

public class MIME {
    private static HashMap<String , String> MIMEList = new HashMap<>();
    private static HashMap<String, String> reverseMiMeList = new HashMap<>(); // key value 反转
    private static MIME mime = null;
    /**
     * 构造函数，将MIME类型载入
     */
    private MIME(){
        // text
        MIMEList.put(".htm","text/html");
        MIMEList.put(".html", "text/html");
        MIMEList.put(".css","text/css");
        MIMEList.put(".csv","text/csv");
        MIMEList.put(".ics","text/calendar");
        MIMEList.put(".js","text/javascript");
        MIMEList.put(".mjs", "text/javascript");
        MIMEList.put(".txt", "text/plain; charset=utf-8");
        // image
        MIMEList.put(".png", "image/png");
        MIMEList.put(".jpg", "image/jpeg");
        MIMEList.put(".jpeg","image/jpeg");
        MIMEList.put(".gif", "image/gif");
        MIMEList.put(".svg", "image/svg+xml");
        MIMEList.put(".tif", "image/tiff");
        MIMEList.put(".tiff", "image/tiff");
        MIMEList.put(".webp","image/webp");
        MIMEList.put(".ico","image/vnd.microsoft.icon");
        MIMEList.put(".bmp","image/bmp");
        // video
        MIMEList.put(".avi","video/x-msvideo");
        MIMEList.put(".mp4", "video/mp4");
        MIMEList.put(".mpeg", "video/mpeg");
        MIMEList.put(".ogv", "video/ogg");
        MIMEList.put(".ts", "video/mp2t");
        MIMEList.put(".webm", "video/webm");
        MIMEList.put(".3gp", "video/3gpp");
        MIMEList.put(".3g2", "video/3gpp2");
        // audio
        MIMEList.put(".mp3", "audio/mpeg");
        MIMEList.put(".wav", "audio/wav");
        MIMEList.put(".aac", "audio/aac");
        MIMEList.put(".oga", "audio/ogg");
        MIMEList.put(".mid", "audio/midi");
        MIMEList.put(".midi", "audio/x-midi");
        MIMEList.put(".opus", "audio/opus");
        MIMEList.put(".weba", "audio/webm");
        for (String val :MIMEList.keySet()){
            reverseMiMeList.put(MIMEList.get(val), val);
        }

    }

    public static MIME getMimeList(){
        if (MIME.mime == null){
            MIME.mime = new MIME();
        }
        return MIME.mime;
    }

    /**
     * return mime_type according to the uri
     * if not found, return application/octet-stream
     * @param Uri
     * @return
     */
    public String getMimeType(String Uri){
        int loc_point = Uri.lastIndexOf(".");
        if (loc_point == -1) return "application/octet-stream";
        String end = Uri.substring(loc_point);
        return MIMEList.getOrDefault(end, "application/octet-stream");
    }

    /**
     * return key according to the value
     * if not found, return .bin (application/octet*stream)
     * @param mime
     * @return
     */
    public String getReverseMimeType(String mime){return reverseMiMeList.getOrDefault(mime, ".bin");}


}