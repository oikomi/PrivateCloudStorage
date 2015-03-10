package com.yuchuan.privatecloudstorage.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.yuchuan.privatecloudstorage.R;
import com.yuchuan.privatecloudstorage.config.Config;
import com.yuchuan.privatecloudstorage.json.FileData;
import com.yuchuan.privatecloudstorage.net.GetServerFileList;
import com.yuchuan.privatecloudstorage.net.Mkdir;
import com.yuchuan.privatecloudstorage.net.Rename;
import com.yuchuan.privatecloudstorage.net.RmFile;
import com.yuchuan.privatecloudstorage.util.IntentClassify;
import com.yuchuan.privatecloudstorage.util.Util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends Activity {
    private ListView fileList;
    private BootstrapButton mkdir;
    private BootstrapButton upload;
    private BootstrapButton transfer;
    private TextView root;
    private TextView yunpan;
    private TextView source;
    private TextView me;
    private List<FileData> fileDatas = new ArrayList<FileData>();
    private String dir;
    private int selectPos;

    private GlobalSettings settings = GlobalSettings.getInstance();
    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    private ArrayList<HashMap<String, Object>> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
        getServerFileList("/");
    }

    public class FileItemComparator implements Comparator<FileData> {

        @Override
        public int compare(FileData lhs, FileData rhs) {
            if (! lhs.getType().equals(rhs.getType())) {
                // 如果一个是文件，一个是文件夹，优先按照类型排序
                if (lhs.getType().equals("dir")) {
                    return -1;
                } else {
                    return 1;
                }
            } else {
                // 如果同是文件夹或者文件，则按名称排序
                return lhs.getName().toLowerCase()
                        .compareTo(rhs.getName().toLowerCase());
            }
        }
    }

    public void sortList(List<FileData> list) {
        FileItemComparator comparator = new FileItemComparator();
        Collections.sort(list, comparator);
    }

    private void getServerFileList(String path) {
        try {
            new GetServerFileList(URLEncoder.encode(path, "UTF-8"), "kkk", new GetServerFileList.SuccessCallback() {

                @Override
                public void onSuccess(String result) {
                    Log.i("MainActivity", "onSuccess");
                    if (result.equals("{}")) {

                    } else {
                        Gson gson = new Gson();
                        fileDatas = gson.fromJson(result, new TypeToken<List<FileData>>() {
                        }.getType());
                        sortList(fileDatas);
                        data = getData();
                        MyAdapter mAdapter = new MyAdapter(MainActivity.this);
                        fileList.setAdapter(mAdapter);
                    }
                }
            }, new GetServerFileList.FailCallback() {
                @Override
                public void onFail() {
                    Log.i("MainActivity", "onFail");
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                Toast.makeText(MainActivity.this,
                        "删除",
                        Toast.LENGTH_SHORT).show();
                HashMap<String, Object> f = (HashMap<String, Object>) fileList.getAdapter().getItem(selectPos);
                try {
                    new RmFile("/" + URLEncoder.encode(f.get("FilePath").toString(), "UTF-8"), "kkk", new RmFile.SuccessCallback() {
                        @Override
                        public void onSuccess(String result) {
                            Log.i("MainActivity", "onSuccess");
                            getServerFileList("/");
                        }
                    }, new RmFile.FailCallback() {
                        @Override
                        public void onFail() {
                            Log.i("MainActivity", "onFail");
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                break;

            case 1:
                RenameDialog dialog = new RenameDialog(MainActivity.this,
                        new RenameDialog.LeaveMyDialogListener() {
                            @Override
                            public void onClick(View view, String newName) {
                                //Log.i("MainActivity---dir", dirD);
                                HashMap<String, Object> f2 = (HashMap<String, Object>) fileList.getAdapter().getItem(selectPos);
                                try {
                                    new Rename("/" + URLEncoder.encode(f2.get("FilePath").toString(), "UTF-8"),
                                            URLEncoder.encode(newName, "UTF-8"), "kkk", new Rename.SuccessCallback() {
                                        @Override
                                        public void onSuccess(String result) {
                                            Log.i("MainActivity", "onSuccess");

                                            getServerFileList("/");
                                        }
                                    }, new Rename.FailCallback() {

                                        @Override
                                        public void onFail() {
                                            Log.i("MainActivity", "onFail");
                                        }
                                    });
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                dialog.show();
                break;

            case 2:
                HashMap<String, Object> f3 = (HashMap<String, Object>) fileList.getAdapter().getItem(selectPos);
                String downUrl = Config.PLAY_URL + f3.get("FilePath").toString();

                if (settings.downFlag.containsKey(downUrl) == false) {
                    settings.downFlag.put(downUrl, true);
                    downloadFile(downUrl, "/storage/emulated/0/Download" + f3.get("FilePath").toString());
                } else {
                    Toast.makeText(MainActivity.this,
                            "已经在下载中。。。",
                            Toast.LENGTH_SHORT).show();
                }


                Toast.makeText(MainActivity.this,
                        "正在为您加密传输",
                        Toast.LENGTH_SHORT).show();

                break;

            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    private synchronized void publishProgress(LocalBroadcastManager mBroadcastManager,
                                              String fileName, int progresses, String speeds, boolean status) {
        Intent i = new Intent();
        i.setAction("DownloadingProgress");
        i.putExtra("fileName", fileName);
        i.putExtra("progress", progresses);
        i.putExtra("speeds", speeds);
        i.putExtra("status", status);
        mBroadcastManager.sendBroadcast(i);
    }

    private synchronized void publishProgressUpload(LocalBroadcastManager mBroadcastManager,
                                              String fileName, int progresses, String speeds, boolean status) {
        Intent i = new Intent();
        i.setAction("UploadingProgress");
        i.putExtra("fileName", fileName);
        i.putExtra("progress", progresses);
        i.putExtra("speeds", speeds);
        i.putExtra("status", status);
        mBroadcastManager.sendBroadcast(i);
    }

    private synchronized long calcSpeed(long curSize) {
        long curTime = System.nanoTime();
        long speed = (curSize - settings.preSzie) / ((curTime - settings.preTime));
        settings.preSzie = curSize;
        settings.preTime = curTime;

        Log.i("MainActivity", String.valueOf(speed));

        return speed;
    }

    private void downloadFile(final String url, String storePath) {
        HttpUtils http = new HttpUtils();
        final LocalBroadcastManager mBroadcastManager = LocalBroadcastManager.getInstance(this);
        HttpHandler handler = http.download(url,
                storePath,
                false, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                new RequestCallBack<File>() {

                    @Override
                    public void onStart() {
                        settings.preTime = System.nanoTime();
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        Log.i("MainActivity", current + "/" + total);

                        publishProgress(mBroadcastManager, url, (int)((current*100/total)), current + "/" + total, true);
                        Log.i("MainActivity", String.valueOf((current*100/total)));
                    }

                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        Toast.makeText(MainActivity.this, "下载文件: " + "成功", Toast.LENGTH_LONG).show();
                        settings.downFlag.remove(url);

                    }


                    @Override
                    public void onFailure(HttpException error, String msg) {
                        Toast.makeText(MainActivity.this, "下载文件: " + "失败", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void uploadFile(final String url, final String path) {
        RequestParams params = new RequestParams();
        //params.addHeader("name", "value");
        params.addQueryStringParameter("action", "upload");
        final String names[] = path.split("/");
        params.addQueryStringParameter("path", "/" + names[names.length -1]);
        // 只包含字符串参数时默认使用BodyParamsEntity，
        // 类似于UrlEncodedFormEntity（"application/x-www-form-urlencoded"）。
        //params.addBodyParameter("name", "value");

        // 加入文件参数后默认使用MultipartEntity（"multipart/form-data"），
        // 如需"multipart/related"，xUtils中提供的MultipartEntity支持设置subType为"related"。
        // 使用params.setBodyEntity(httpEntity)可设置更多类型的HttpEntity（如：
        // MultipartEntity,BodyParamsEntity,FileUploadEntity,InputStreamUploadEntity,StringEntity）。
        // 例如发送json参数：params.setBodyEntity(new StringEntity(jsonStr,charset));
        params.addBodyParameter("file", new File(path));

        final LocalBroadcastManager mBroadcastManagerUpload = LocalBroadcastManager.getInstance(this);

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                url,
                params,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        Log.i("MainActivity", "onStart");
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        if (isUploading) {
                            Log.i("MainActivity", "upload: " + current + "/" + total);
                            publishProgressUpload(mBroadcastManagerUpload, path, (int) ((current * 100 / total)),
                                    current + "/" + total, true);
                        } else {
                            //testTextView.setText("reply: " + current + "/" + total);
                        }
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        //testTextView.setText("reply: " + responseInfo.result);
                        Log.i("MainActivity", "onSuccess");
                        Toast.makeText(MainActivity.this, "上传文件: [" + names[names.length -1] + "]成功", Toast.LENGTH_LONG).show();
                        getServerFileList("/");
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        //testTextView.setText(error.getExceptionCode() + ":" + msg);
                        Log.i("MainActivity", "onFailure");
                    }
                });
    }

    private void initEvent() {
        fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> parent, View view,
                                     int position, long id) {
                 FileData f = fileDatas.get(position);
                 String path = f.getPath();
                 String type = f.getType();
                 String name = f.getName();
                 if (type.equals("dir")) {
                     Intent intent;
                     intent = new Intent(MainActivity.this, FileBrowseActivity.class);
                     intent.putExtra("PATH", path + "/");
                     startActivity(intent);
                 } else {
                     String postfix = Util.getFilePostfix(name);
                     if (postfix.equals("mp4")) {
                         Intent intent = IntentClassify.getVideoFileIntent(path);
                         startActivity(intent);
                     }
                     if (postfix.equals("pdf")) {
                         Intent intent = IntentClassify.getPdfFileIntent(path);
                         startActivity(intent);
                     }
                     if (postfix.equals("png") || postfix.equals("jpg")) {
                         Intent intent = IntentClassify.getImageFileIntent(path);
                         startActivity(intent);
                     }
                 }
            }
        });

        fileList.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 0, 0, "删除");
                menu.add(0, 1, 0, "重命名");
                menu.add(0, 2, 0, "下载");
            }
        });

        fileList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectPos = position;

                return false;
            }
        });


        mkdir.setOnClickListener(new View.OnClickListener() {
            String tmp;

            @Override
            public void onClick(View v) {
                MkdirDialog dialog = new MkdirDialog(MainActivity.this,
                        new MkdirDialog.LeaveMyDialogListener() {
                            @Override
                            public void onClick(View view, String dirD) {
                                try {
                                    tmp = URLEncoder.encode(dirD, "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                new Mkdir("/" + tmp, "kkk", new Mkdir.SuccessCallback() {

                                    @Override
                                    public void onSuccess(String result) {
                                        Log.i("MainActivity", "onSuccess");

                                        //Toast.makeText(MainActivity.this, "创建文件夹: " + tmp + ", 请回退刷新查看", Toast.LENGTH_LONG).show();
                                        getServerFileList("/");

                                    }
                                }, new Mkdir.FailCallback() {

                                    @Override
                                    public void onFail() {
                                        Log.i("MainActivity", "onFail");
                                    }
                                });
                            }
                        });
                dialog.show();
            }
        });


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "后面再慢慢开发。。。 ", Toast.LENGTH_LONG).show();
                FileDialog dialog = new FileDialog.Builder(MainActivity.this)
                        .setFileMode(FileDialog.FILE_MODE_OPEN_MULTI)
                        .setCancelable(true).setCanceledOnTouchOutside(false)
                        .setTitle("选择要上传的文件")
                        .setFileSelectListener(new FileDialog.FileDialogListener() {
                            @Override
                            public void onFileSelected(ArrayList<File> files) {
                                if (files.size() > 0) {
                                    Log.i("MainActivity", files.get(0).toString());

                                    uploadFile(Config.URL, files.get(0).toString());
                                }
                            }

                            @Override
                            public void onFileCanceled() {
                                //ToastUtil.showToast(getActivity(), "Copy Cancelled!");
                            }
                        }).create(MainActivity.this);
                dialog.show();
            }
        });

        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, DownloadListActivity.class);
//                startActivity(intent);

                Intent intent = new Intent(MainActivity.this, TransferActivity.class);
                startActivity(intent);
            }
        });


        yunpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        fileList = (ListView) findViewById(R.id.lv_file_list);
        mkdir = (BootstrapButton) findViewById(R.id.btn_mkdir);
        upload = (BootstrapButton) findViewById(R.id.btn_upload);
        transfer = (BootstrapButton) findViewById(R.id.btn_transfer);
        yunpan = (TextView) findViewById(R.id.tv_cloud);
        source = (TextView) findViewById(R.id.tv_source);
        me = (TextView) findViewById(R.id.tv_me);

    }


    private ArrayList<HashMap<String, Object>> getData(){
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        for(int i = 0; i < fileDatas.size() ; i++)
        {
            FileData f = fileDatas.get(i);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("FileName", f.getName());
            map.put("FileSize", f.getSize());
            map.put("FilePath", f.getPath());
            map.put("FileType", f.getType());
            map.put("ModifyData", f.getModify());
            listItem.add(map);
        }
        return listItem;
    }

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            //Log.i("MainActivity", "getView");
            convertView = mInflater.inflate(R.layout.activity_main_list_item, null);
            holder = new ViewHolder();
            holder.img = (ImageView) convertView.findViewById(R.id.iv_img);
            holder.fileName = (TextView) convertView.findViewById(R.id.tv_file_name);
            holder.fileSize = (TextView) convertView.findViewById(R.id.tv_file_size);
            /*设置TextView显示的内容，即我们存放在动态数组中的数据*/
            holder.fileName.setText(data.get(position).get("FileName").toString());
            String type = data.get(position).get("FileType").toString();

            if (type.equals("dir")) {
                holder.fileSize.setText(
                        Util.timeStamp2Date(data.get(position).get("ModifyData").toString(), "yyyy-MM-dd HH:mm:ss"));

            } else {
                holder.fileSize.setText(data.get(position).get("FileSize").toString() + "  " +
                        Util.timeStamp2Date(data.get(position).get("ModifyData").toString(), "yyyy-MM-dd HH:mm:ss")
                        );
            }


            if (type.equals("dir")) {
                if (holder.img != null) {
                    holder.img.setImageResource(R.drawable.folder);
                }
            } else {
                String postfix = Util.getFilePostfix(data.get(position).get("FileName").toString());
                if (postfix.equals("mp4") || postfix.equals("avi") || postfix.equals("ts")
                        || postfix.equals("mpg") || postfix.equals("mpeg") || postfix.equals("rmvb")) {
                    holder.img.setImageResource(R.drawable.video);
                } else if (postfix.equals("pdf")) {
                    holder.img.setImageResource(R.drawable.pdf);
                } else if (postfix.equals("txt")) {
                    holder.img.setImageResource(R.drawable.txt);
                } else if (postfix.equals("zip") || postfix.equals("gz")) {
                    holder.img.setImageResource(R.drawable.yasuo);
                } else if (postfix.equals("png") || postfix.equals("jpg")) {
                    holder.img.setImageResource(R.drawable.picture);
                }
                else {
                    holder.img.setImageResource(R.drawable.file);
                }
            }

            return convertView;
        }

    }

    /*存放控件*/
    static  class ViewHolder{
        public ImageView img;
        public TextView fileName;
        public TextView fileSize;
    }

}
