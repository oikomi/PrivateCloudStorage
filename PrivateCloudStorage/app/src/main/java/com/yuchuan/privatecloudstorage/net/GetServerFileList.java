package com.yuchuan.privatecloudstorage.net;

import com.yuchuan.privatecloudstorage.config.Config;


/**
 * Created by haroldmiao on 2015/2/24.
 */
public class GetServerFileList {

    public GetServerFileList(String path, String token, final SuccessCallback successCallback, final FailCallback failCallback) {
        new NetConnection(Config.URL, HttpMethod.GET, new NetConnection.SuccessCallback() {

            @Override
            public void onSuccess(String result) {
                if (successCallback != null) {
                    successCallback.onSuccess(result);
                }

//                    JSONObject obj = new JSONObject(result);
//
//                    switch (obj.getInt(Config.KEY_STATUS)) {
//                        case 1:
//                            if (successCallback != null) {
//                                successCallback.onSuccess(obj.getString(Config.KEY_TOKEN));
//                            }
//                            break;
//                        default:
//                            if (failCallback != null) {
//                                failCallback.onFail();
//                            }
//                            break;
//                    }

//                } catch (JSONException e) {
//                    e.printStackTrace();
//
//                    if (failCallback!=null) {
//                        failCallback.onFail();
//                    }
//                }
            }
        }, new NetConnection.FailCallback() {

            @Override
            public void onFail() {
                if (failCallback!=null) {
                    failCallback.onFail();
                }
            }
        }, Config.KEY_ACTION, Config.ACTION_GET_SERVER_FILE_LIST, Config.KEY_PATH, path, Config.KEY_TOKEN, token);
    }

    public static interface SuccessCallback{
        void onSuccess(String result);
    }

    public static interface FailCallback{
        void onFail();
    }
}
