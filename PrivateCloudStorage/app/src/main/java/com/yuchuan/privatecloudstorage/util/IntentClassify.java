package com.yuchuan.privatecloudstorage.util;

import android.content.Intent;
import android.net.Uri;

import com.yuchuan.privatecloudstorage.config.Config;

import java.io.File;

/**
 * Created by haroldmiao on 2015/2/25.
 */
public class IntentClassify {
    //android获取一个用于打开HTML文件的intent

    public static Intent getHtmlFileIntent( String param )

    {

        Uri uri = Uri.parse(param ).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(param ).build();

        Intent intent = new Intent("android.intent.action.VIEW");

        intent.setDataAndType(uri, "text/html");

        return intent;

    }


    //android获取一个用于打开图片文件的intent

    public static Intent getImageFileIntent( String url )
    {
        Intent intent = new Intent("android.intent.action.VIEW");

        intent.addCategory("android.intent.category.DEFAULT");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri uri = Uri.parse(Config.PLAY_URL + url);

        intent.setDataAndType(uri, "image/*");

        return intent;

    }

    //android获取一个用于打开PDF文件的intent

    public static Intent getPdfFileIntent(String url) {

        Intent intent = new Intent("android.intent.action.VIEW");

        intent.addCategory("android.intent.category.DEFAULT");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Uri uri = Uri.parse(Config.PLAY_URL + url);

        intent.setDataAndType(uri, "application/pdf");

        return intent;

    }

//
//    //android获取一个用于打开文本文件的intent
//
//    public static Intent getTextFileIntent( String paramString, boolean paramBoolean)
//    {
//        Intent intent = new Intent("android.intent.action.VIEW");
//        intent.addCategory("android.intent.category.DEFAULT");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        if (paramBoolean)
//        {
//
//            Uri uri1 = Uri.parse(paramString);
//
//            intent.setDataAndType(uri1, "text/plain");
//
//        }
//
//        while (true)
//
//        {
//            return intent;
//            Uri uri2 = Uri.fromFile(new File(paramString));
//            intent.setDataAndType(uri2, "text/plain");
//        }
//
//    }


    //android获取一个用于打开音频文件的intent

    public static Intent getAudioFileIntent( String param )

    {

        Intent intent = new Intent("android.intent.action.VIEW");

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra("oneshot", 0);

        intent.putExtra("configchange", 0);

        Uri uri = Uri.fromFile(new File(param ));

        intent.setDataAndType(uri, "audio/*");

        return intent;

    }


    //android获取一个用于打开视频文件的intent

    public static Intent getVideoFileIntent(String url)

    {
        Intent intent = new Intent("android.intent.action.VIEW");

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra("oneshot", 0);

        intent.putExtra("configchange", 0);

        Uri uri = Uri.parse(Config.PLAY_URL + url);

        intent.setDataAndType(uri, "video/*");

        return intent;

    }


    //android获取一个用于打开CHM文件的intent

    public static Intent getChmFileIntent( String param )

    {

        Intent intent = new Intent("android.intent.action.VIEW");

        intent.addCategory("android.intent.category.DEFAULT");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri uri = Uri.fromFile(new File(param ));

        intent.setDataAndType(uri, "application/x-chm");

        return intent;

    }


    //android获取一个用于打开Word文件的intent

    public static Intent getWordFileIntent( String param )

    {

        Intent intent = new Intent("android.intent.action.VIEW");

        intent.addCategory("android.intent.category.DEFAULT");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri uri = Uri.fromFile(new File(param ));

        intent.setDataAndType(uri, "application/msword");

        return intent;

    }

    //android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent( String param )
    {
        Intent intent = new Intent("android.intent.action.VIEW");

        intent.addCategory("android.intent.category.DEFAULT");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri uri = Uri.fromFile(new File(param ));

        intent.setDataAndType(uri, "application/vnd.ms-excel");

        return intent;

    }

    //android获取一个用于打开PPT文件的intent

    public static Intent getPptFileIntent( String param )

    {
        Intent intent = new Intent("android.intent.action.VIEW");

        intent.addCategory("android.intent.category.DEFAULT");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri uri = Uri.fromFile(new File(param));

        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");

        return intent;

    }

}
