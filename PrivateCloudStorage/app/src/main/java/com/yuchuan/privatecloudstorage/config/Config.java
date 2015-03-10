package com.yuchuan.privatecloudstorage.config;

/**
 * Created by haroldmiao on 2015/2/24.
 */
public class Config {

    public static final String URL = "http://54.187.96.250:10000/api/v1/storage";
    public static final String PLAY_URL = "http://54.187.96.250:10000";
    public static final String CHARSET = "utf-8";
    public static final String KEY_STATUS = "status";
    public static final String KEY_ACTION = "action";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_PATH = "path";
    public static final String KEY_DIR = "dir";
    public static final String KEY_OLD_DIR = "old_dir";
    public static final String KEY_NEW_DIR = "new_dir";

    public static final String ACTION_LOGIN = "login";
    public static final String ACTION_GET_SERVER_FILE_LIST = "get_server_file_list";
    public static final String ACTION_MKDIR = "mkdir";
    public static final String ACTION_RMDIR = "rm_file";
    public static final String ACTION_RENAME = "rename";
    public static final int STATUS_OK = 1;
    public static final int STATUS_FAIL = 0;
    public static final int STATUS_INVALID_TOKEN = 2;


}
