package com.yuchuan.privatecloudstorage.json;

/**
 * Created by haroldmiao on 2015/3/9.
 */
public class LoginData {
    private int status;

    private String token;

    public LoginData() {

    }
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
