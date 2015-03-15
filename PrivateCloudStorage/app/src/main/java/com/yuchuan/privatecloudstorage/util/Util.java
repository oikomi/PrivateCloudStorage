package com.yuchuan.privatecloudstorage.util;

/**
 * Created by haroldmiao on 2015/2/25.
 */
public class Util {
    public static String getFilePostfix(String name) {
        String postfix=name.substring(name.lastIndexOf(".")+1);

        return postfix;
    }

    public static String timeStamp2Date(String timestampString, String formats){
        Long timestamp = Long.parseLong(timestampString)*1000;
        String date = new java.text.SimpleDateFormat(formats).format(new java.util.Date(timestamp));
        return date;
    }
}
