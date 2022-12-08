package edu.njunet.utils.JsonOps;

import java.util.HashMap;
import java.util.Map;

public class MonthToNum {
    public static final Map<String, String> Month_Map = new HashMap<>() {
        {
            put("Jan", "01");
            put("Feb", "02");
            put("Mar", "03");
            put("Apr", "04");
            put("May", "05");
            put("Jun", "06");
            put("Jul", "07");
            put("Aug", "08");
            put("Sep", "09");
            put("Oct", "10");
            put("Nov", "11");
            put("Dec", "12");
        }
    };
}
