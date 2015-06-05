
package com.yuchuan.privatecloudstorage.net;

import com.yuchuan.privatecloudstorage.config.Config;


/**
 * Created by haroldmiao on 2015/2/24.
 */
public class IsFileOnYunpan {

     public  IsFileOnYunpan(String path, String token, final SuccessCallback successCallback, final FailCallback failCallback) {
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
        }, Config.KEY_ACTION, Config.ACTION_IS_FILE_EXIST, Config.KEY_PATH, path, Config.KEY_TOKEN, token);
    }

    public static interface SuccessCallback{
        void onSuccess(String result);
    }

    public static interface FailCallback{
        void onFail();
    }
}

