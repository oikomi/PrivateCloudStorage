package com.yuchuan.privatecloudstorage.net;

import com.yuchuan.privatecloudstorage.config.Config;

/**
 * Created by haroldmiao on 2015/3/3.
 */
public class RmFile {
    public RmFile(String dir, String token, final SuccessCallback successCallback, final FailCallback failCallback) {
        new NetConnection(Config.URL, HttpMethod.GET, new NetConnection.SuccessCallback() {

            @Override
            public void onSuccess(String result) {
                if (successCallback != null) {
                    successCallback.onSuccess(result);
                }
            }
        }, new NetConnection.FailCallback() {

            @Override
            public void onFail() {
                if (failCallback!=null) {
                    failCallback.onFail();
                }
            }
        }, Config.KEY_ACTION, Config.ACTION_RMDIR, Config.KEY_DIR, dir, Config.KEY_TOKEN, token);
    }

    public static interface SuccessCallback{
        void onSuccess(String result);
    }

    public static interface FailCallback{
        void onFail();
    }
}
