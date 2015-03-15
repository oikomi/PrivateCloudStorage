package com.yuchuan.privatecloudstorage.json;

/**
 * Created by haroldmiao on 2015/3/11.
 */
public class StorageInfo {
    private String status;
    private String all;
    private String used;

    public StorageInfo(){

    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAll() {
        return all;
    }

    public void setAll(String all) {
        this.all = all;
    }

    public String getUsed() {
        return used;
    }

    public void setUsed(String used) {
        this.used = used;
    }

}
