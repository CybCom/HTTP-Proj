package edu.njunet.utils.JsonOps;

import java.util.Date;

public class SystemTime {
    public static String systemTime() {
        Date date = new Date();
        date.setTime(System.currentTimeMillis());
        String dateStr = date.toString();
        String[] dateSt = dateStr.split(" ");
        return dateSt[0] + ", " + dateSt[2] + " " + dateSt[1]
                + " " + dateSt[5] + " " + dateSt[3] + " GMT";
    }
}
