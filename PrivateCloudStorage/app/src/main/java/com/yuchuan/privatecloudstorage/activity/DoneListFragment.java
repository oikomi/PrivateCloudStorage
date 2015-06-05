package com.yuchuan.privatecloudstorage.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yuchuan.privatecloudstorage.R;

/**
 * Created by haroldmiao on 2015/3/20.
 */
public class DoneListFragment extends android.support.v4.app.Fragment {
    private ListView mListView;
    private boolean mReceiversRegistered;
    //private MyWebRequestReceiver mDoneProgressReceiver;
    private GlobalSettings settings = GlobalSettings.getInstance();

    @Override
    public void onAttach(Activity activity) {
        Log.i("DoneListFragment", "onAttach");
        super.onAttach(activity);
        settings.mDoneProgressReceiver = new DoneListRequestReceiver();
        registerReceiver();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("DoneListFragment", "onCreateView");
        View view = inflater.inflate(R.layout.activity_done_list, container, false);
        mListView = (ListView) view.findViewById(R.id.list);

        settings.mFragmentAdapterDone = new MyAdapter(getActivity());
        mListView.setAdapter(settings.mFragmentAdapterDone);


        //registerReceiver();

        return view;
    }


    private void getData(String fileName)
    {
        settings.dataDone.add(new Progress(fileName));
    }

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return settings.dataDone.size();
        }

        @Override
        public Progress getItem(int position) {
            return settings.dataDone.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            //Log.i("MainActivity", "getView");
            convertView = mInflater.inflate(R.layout.row_done, null);
            holder = new ViewHolder();
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.fileName = (TextView) convertView.findViewById(R.id.file_name);
            /*设置TextView显示的内容，即我们存放在动态数组中的数据*/
            holder.fileName.setText(settings.dataDone.get(position).toString());

            return convertView;
        }
    }

    /*存放控件*/
    static  class ViewHolder{
        public ImageView img;
        public TextView fileName;
        public TextView info;
        public Button btn;
    }

    @Override
    public void onResume() {
        registerReceiver();
        super.onResume();
    }

    @Override
    public void onPause() {
        //unregisterReceiver();
        super.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void registerReceiver() {
        unregisterReceiver();
        IntentFilter intentToReceiveFilter = new IntentFilter();
        intentToReceiveFilter
                .addAction("DoneProgress");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                settings.mDoneProgressReceiver, intentToReceiveFilter);
        mReceiversRegistered = true;
    }

    private void unregisterReceiver() {
        if (mReceiversRegistered) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
                    settings.mDoneProgressReceiver);
            mReceiversRegistered = false;
        }
    }

    private void upadteRow(Progress p, View v) {
        ImageView iv = (ImageView) v.findViewById(R.id.img);
        //iv.setImageBitmap(p.iconImg);
        //iv.setImageResource(R.layout.clouddown);
        TextView tv = (TextView) v.findViewById(R.id.file_name);
        tv.setText(p.title);
        tv = (TextView) v.findViewById(R.id.info);
        tv.setText("完成");
    }

    // don't call notifyDatasetChanged() too frequently, have a look at
    // following url http://stackoverflow.com/a/19090832/1112882
    protected void onProgressUpdate(String fileName, String type) {
        final ListView listView = mListView;
        int first = listView.getFirstVisiblePosition();
        int last = listView.getLastVisiblePosition();
        View convertView = mListView.getChildAt(settings.dataMapDone.get(fileName) - first);
        upadteRow((Progress)(settings.mFragmentAdapterDone.getItem(settings.dataMapDone.get(fileName))), convertView);
    }

    protected void onProgressUpdateOneShot(String fileName, String type) {
        onProgressUpdate(fileName, type);
    }

    public class DoneListRequestReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("DoneListFragment", "onReceive");
            if (intent.getAction().equals(
                    "DoneProgress")) {//DownloadingService.PROGRESS_UPDATE_ACTION)) {
                final String fileName = intent.getStringExtra("fileName");
                final String type = intent.getStringExtra("type");
                getData(fileName);

                mListView.setAdapter(settings.mFragmentAdapterDone);

                //onProgressUpdateOneShot(fileName, type);
            }
        }
    }
}
