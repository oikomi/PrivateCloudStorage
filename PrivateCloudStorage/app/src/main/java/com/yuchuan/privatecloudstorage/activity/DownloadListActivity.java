package com.yuchuan.privatecloudstorage.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yuchuan.privatecloudstorage.R;

/*
 * http://stackoverflow.com/questions/19875838/download-multiple-files-with-a-progress-bar-in-listview-android
 */

public class DownloadListActivity extends Activity {
	private ListView mListView;
	private boolean mReceiversRegistered;
	private MyWebRequestReceiver mDownloadingProgressReceiver;
	private GlobalSettings settings = GlobalSettings.getInstance();

    //private ArrayList<Progress> data = new ArrayList<Progress>();

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("DownloadListActivity", "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download_list);

		mListView = (ListView) findViewById(R.id.list);

//        settings.mAdapter = new ArrayAdapter<Progress>(this,
//                R.layout.row, R.id.title, new Progress[] {
//                new Progress("同程旅游-特价"),
//                new Progress("英雄之剑"),
//                new Progress("智商球")
//        }) {
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                View v = super.getView(position, convertView, parent);
//                upadteRow(getItem(position), v);
//                return v;
//            }
//        };

//        settings.mAdapter = new ArrayAdapter<Progress>(
//                this,
//                R.layout.row, R.id.title,
//                data);

        //data = getData();

        settings.mAdapter = new MyAdapter(DownloadListActivity.this);
        mListView.setAdapter(settings.mAdapter);

//		Log.i("DownloadListActivity", "savedInstanceState: "
//				+ (savedInstanceState == null));
//		if (savedInstanceState == null) {
//			Intent intent = new Intent(this, DownloadingService.class);
//			intent.putExtra("files", settings.mAdapter.getCount());
//			startService(intent);
//		}
		mDownloadingProgressReceiver = new MyWebRequestReceiver();
	}

    private void getData(String fileName)
    {
        settings.data.add(new Progress(fileName));
    }



    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return settings.data.size();
        }

        @Override
        public Progress getItem(int position) {
            return settings.data.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            //Log.i("MainActivity", "getView");
            convertView = mInflater.inflate(R.layout.row, null);
            holder = new ViewHolder();
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.fileName = (TextView) convertView.findViewById(R.id.file_name);
            /*设置TextView显示的内容，即我们存放在动态数组中的数据*/
            holder.fileName.setText(settings.data.get(position).toString());

            return convertView;
        }
    }

    /*存放控件*/
    static  class ViewHolder{
        public ImageView img;
        public TextView fileName;
        public TextView info;
        public ProgressBar bar;
        public Button btn;
    }

	@Override
	protected void onResume() {
		registerReceiver();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		unregisterReceiver();
		super.onPause();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i("DownloadListActivity", "onDestroy");
	}

	private void registerReceiver() {
		unregisterReceiver();
		IntentFilter intentToReceiveFilter = new IntentFilter();
		intentToReceiveFilter
				.addAction("DownloadingProgress");
		LocalBroadcastManager.getInstance(this).registerReceiver(
				mDownloadingProgressReceiver, intentToReceiveFilter);
		mReceiversRegistered = true;
		Log.i("DownloadListActivity", "registerReceiver");
	}

	private void unregisterReceiver() {
		if (mReceiversRegistered) {
			LocalBroadcastManager.getInstance(this).unregisterReceiver(
					mDownloadingProgressReceiver);
			mReceiversRegistered = false;
			Log.i("DownloadListActivity", "unregisterReceiver");
		}
	}
	
	private void upadteRow(Progress p, View v) {
		ImageView iv = (ImageView) v.findViewById(R.id.img); 
		//iv.setImageBitmap(p.iconImg);
        //iv.setImageResource(R.layout.clouddown);
		ProgressBar bar = (ProgressBar) v.findViewById(R.id.progressBar);
		bar.setProgress(p.progress);
		TextView tv = (TextView) v.findViewById(R.id.file_name);
		tv.setText(p.title);
		tv = (TextView) v.findViewById(R.id.info);
		if(!p.isStarted){
			tv.setText("正在等待");
		}
		else{
			if(p.progress == 100) {
                tv.setText("下载完成");
                settings.data.remove(p.postion);
                settings.dataMap.remove(p.title);
                settings.postInc--;
            }
			else {
                tv.setText(p.toString());
            }
				
		}
//		Log.i("DownloadListActivity", "upadteRow");
	}

	// don't call notifyDatasetChanged() too frequently, have a look at
	// following url http://stackoverflow.com/a/19090832/1112882
	protected void onProgressUpdate(String fileName, int progress, String speed, boolean status) {
		final ListView listView = mListView;
		int first = listView.getFirstVisiblePosition();
		int last = listView.getLastVisiblePosition();
        settings.mAdapter.getItem(settings.dataMap.get(fileName)).postion = settings.dataMap.get(fileName);
        settings.mAdapter.getItem(settings.dataMap.get(fileName)).progress = progress > 100 ? 100 : progress;
		settings.mAdapter.getItem(settings.dataMap.get(fileName)).speed = speed;
		settings.mAdapter.getItem(settings.dataMap.get(fileName)).isStarted = status;
		if (settings.dataMap.get(fileName) < first || settings.dataMap.get(fileName) > last) {
			return;
		} else {
			View convertView = mListView.getChildAt(settings.dataMap.get(fileName) - first);
			upadteRow((Progress)(settings.mAdapter.getItem(settings.dataMap.get(fileName))), convertView);
		}
	}

	protected void onProgressUpdateOneShot(String fileName, int progress, String speed, boolean status) {
		onProgressUpdate(fileName, progress, speed, status);
	}

	public class MyWebRequestReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
                    "DownloadingProgress")) {//DownloadingService.PROGRESS_UPDATE_ACTION)) {
				final int progress = intent.getIntExtra("progress", 0);
				final String fileName = intent.getStringExtra("fileName");
				final String speed = intent.getStringExtra("speeds");
				final boolean status = intent.getBooleanExtra("status", false);
                if (settings.dataMap.get(fileName) == null) {
                    settings.dataMap.put(fileName, settings.postInc);
                    settings.dataIncMap.put(fileName, 1);
                    settings.postInc++;
                    getData(fileName);
                    mListView.setAdapter(settings.mAdapter);
                } else {
                    if (settings.dataIncMap.get(fileName) == 1) {
                        onProgressUpdateOneShot(fileName, progress, speed, status);
                    }
                }
			}
		}
	}
}