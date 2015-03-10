package com.yuchuan.privatecloudstorage.activity;

import java.util.ArrayList;
import java.util.HashMap;

public class GlobalSettings {

	public ArrayList<DownloadingService.DownloadTask> mTasks;
	public DownloadListActivity.MyAdapter mAdapter;

    public DownloadListFragment.MyAdapter mFragmentAdapter;
    public UploadListFragment.MyAdapter mFragmentAdapterUpload;
    public ArrayList<Progress> data = new ArrayList<Progress>();

    public HashMap<String, Integer> dataMap = new HashMap<String, Integer>();

    public HashMap<String, Integer> dataIncMap = new HashMap<String, Integer>();

    public HashMap<String, Boolean> downFlag = new HashMap<String, Boolean>();

    public HashMap<String, Boolean> upFlag = new HashMap<String, Boolean>();


    public int postInc = 0;


    public ArrayList<Progress> dataUpload = new ArrayList<Progress>();

    public HashMap<String, Integer> dataMapUpload = new HashMap<String, Integer>();

    public HashMap<String, Integer> dataIncMapUpload = new HashMap<String, Integer>();

    public HashMap<String, Boolean> downFlagUpload = new HashMap<String, Boolean>();



    public int postIncUpload = 0;


    public long preSzie;
    public long preSzieUpload;

    public long preTime;
    public long preTimeUpload;


    private GlobalSettings() {

    }

    private static GlobalSettings instance = new GlobalSettings();
    public static GlobalSettings getInstance(){
        return instance;
    }

}
