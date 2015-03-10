package com.yuchuan.privatecloudstorage.activity;

import android.graphics.Bitmap;

public class Progress {

	// Download address for apk file
    public String mUrl;
	
	// Download address for apk icon
    public String mIconUrl;
	
	// Donwload progress
	public int progress;
	
	// Download speed
	public String speed;
	
	// Progress title
    public String title;
	
	// File icon image
    public Bitmap iconImg;
	
	// Indicate whether the task for this item has started or not
    public boolean isStarted;

    public int postion;
	
//	Progress(String title, String apkUrl, String iconUrl){
//		this.mUrl = apkUrl;
//		this.mIconUrl = iconUrl;
//		this.progress = 0;
//		this.speed = 0;
//		this.title = title;
//		this.iconImg = null;
//		this.isStarted = false;
//	}

    Progress(String title){
        this.progress = 0;
        this.speed = "";
        this.title = title;
        this.iconImg = null;
        this.isStarted = false;
    }
	@Override
	public String toString() {
		return "进度：" + speed;
	}
}
