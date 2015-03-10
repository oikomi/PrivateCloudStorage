package com.yuchuan.privatecloudstorage.activity;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadingService extends IntentService {
	public DownloadingService(String name) {
		super(name);
	}

	public DownloadingService() {
		super("");
	}

	public static final String PROGRESS_UPDATE_ACTION = DownloadingService.class
			.getName() + ".progress_update";
	private boolean mIsAlreadyRunning;

	private ExecutorService mExec;
	private CompletionService<NoResultType> mEcs;
	private LocalBroadcastManager mBroadcastManager;
	private static final long INTERVAL_BROADCAST = 1000;
	private long mLastUpdate = 0;
	private GlobalSettings settings = GlobalSettings.getInstance();

	@Override
	public void onCreate() {
		Log.i("DownloadingService", "onCreate");
		mExec = Executors.newFixedThreadPool( /* only 5 at a time */1);
		mEcs = new ExecutorCompletionService<NoResultType>(mExec);
		mBroadcastManager = LocalBroadcastManager.getInstance(this);

		settings.mTasks = new ArrayList<DownloadTask>();
		for(int itemIndex = 0; itemIndex < settings.mAdapter.getCount(); ++itemIndex){
			if(settings.mAdapter.getItem(itemIndex).iconImg == null){
				new GetIconImg(itemIndex).execute();
			}
			settings.mTasks.add(
					new DownloadTask(
							itemIndex,
							settings.mAdapter.getItem(itemIndex).mUrl)
					);
		}
		
		super.onCreate();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i("DownloadingService", "onHandleIntent");
		if (mIsAlreadyRunning) {
			return;
		}
		mIsAlreadyRunning = true;

		for (DownloadTask t : settings.mTasks) {
			if(!t.isStarted){
				Log.i("DownloadingService", "onHandleIntent - submit task"
						+ t.mPosition);
				mEcs.submit(t);
			}
		}
		// wait for finish
		int n = settings.mTasks.size();
		for (int i = 0; i < n; ++i) {
			try {
				NoResultType r = mEcs.take().get();
				 if (r != null) {
				
				 }
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		// send a last broadcast
		publishCurrentProgressOneShot(true);
		mExec.shutdown();
	}

	public class GetIconImg extends AsyncTask<Void, Void, Void> {
		private int itemIndex;
		GetIconImg(int itemIndex){
			this.itemIndex = itemIndex;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			settings.mAdapter.getItem(itemIndex).iconImg = getURLimage(settings.mAdapter.getItem(itemIndex).mIconUrl);
			return null;
		}

		private Bitmap getURLimage(String url) {  
	        Bitmap bmp = null;  
	        try {  
	            URL myurl = new URL(url);  
	            // 获得连接  
	            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();  
	            conn.setConnectTimeout(6000);//设置超时  
	            conn.setDoInput(true);  
	            conn.setUseCaches(false);//不缓存  
	            conn.connect();  
	            InputStream is = conn.getInputStream();//获得图片的数据流  
	            bmp = BitmapFactory.decodeStream(is);
	            is.close();  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	        return bmp;  
	    } 
	}
	
	
	private void publishCurrentProgressOneShot(boolean forced) {
//		Log.i("DownloadingService", "publishCurrentProgressOneShot");
		if (forced
				|| System.currentTimeMillis() - mLastUpdate > INTERVAL_BROADCAST) {
			mLastUpdate = System.currentTimeMillis();
			final List<DownloadTask> tasks = settings.mTasks;
			int[] positions = new int[tasks.size()];
			int[] progresses = new int[tasks.size()];
			int[] speeds = new int[tasks.size()];
			boolean[] status = new boolean[tasks.size()];
			
			for (int i = 0; i < tasks.size(); i++) {
				DownloadTask t = tasks.get(i);
				positions[i] = t.mPosition;
				progresses[i] = t.mProgress;
				speeds[i] = t.mSpeed;
				status[i] = t.isStarted;
			}
			publishProgress(positions, progresses, speeds, status);
		}
	}

	private void publishCurrentProgressOneShot() {
		publishCurrentProgressOneShot(false);
	}

	private synchronized void publishProgress(int[] positions, int[] progresses, int[] speeds, boolean[] status) {
		Log.i("DownloadingService", "publishProgress");
		Intent i = new Intent();
		i.setAction(PROGRESS_UPDATE_ACTION);
		i.putExtra("position", positions);
		i.putExtra("progress", progresses);
		i.putExtra("speeds", speeds);
		i.putExtra("status", status);
		mBroadcastManager.sendBroadcast(i);
	}

	class DownloadTask implements Callable<NoResultType> {
		private int mPosition;
		private int mProgress;
		private int mSpeed;
		private String mUrl;
		
		public boolean isCancelled;
		public boolean isStarted;

		public DownloadTask(int position, String url) {
			mPosition = position;
			mProgress = 0;
			mSpeed = 0;
			mUrl = url;
			isCancelled = false;
			isStarted = false;
		}

		@Override
		public NoResultType call() throws Exception {
			isStarted = true;
			String fileName = mUrl.substring(mUrl.lastIndexOf("/") + 1);
			try {
				fileName = URLDecoder.decode(fileName, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				return null;
			}
			save2LocalInternal(mUrl, fileName);
			return new NoResultType();
		}
		
		private boolean save2LocalInternal(String url, String fileName) {
			boolean result = false;
			File directory = Environment.getExternalStorageDirectory();
			Log.i("save2LocalInternal", "getExternalStorageDirectory=" + directory);
			File file = new File(directory, fileName);
			if (file.exists()) {
				Log.i("tag", "The file has already exists.");
				file.delete();
				//
			}
			try {

				URL dlURL = new URL(url);
				HttpURLConnection urlConnection = (HttpURLConnection) dlURL
						.openConnection();
				urlConnection.connect();
				int file_size = urlConnection.getContentLength();

				if (HttpURLConnection.HTTP_OK == urlConnection.getResponseCode()) {
					InputStream input = urlConnection.getInputStream();
					if (Environment.getExternalStorageState().equals(
							Environment.MEDIA_MOUNTED)) {
						try {
							FileOutputStream fos = new FileOutputStream(file);
							byte[] b = new byte[8192];
							int j;
							long total = 0;
							long tempTotal = 0;
							long startTime = System.currentTimeMillis();
							boolean isFinished = false;
							while ((j = input.read(b)) != -1) {
								if (isCancelled)
									break;
								fos.write(b, 0, j);
								total += j;
								tempTotal += j;
								mProgress = (int) (total * 100 / file_size);
								long interval = System.currentTimeMillis()
										- startTime;
								if (interval >= 1000) {
									Log.i("now = ", String.valueOf(System
											.currentTimeMillis()));
									Log.i("last = ", String.valueOf(startTime));
									Log.i("currentDump = ",
											String.valueOf(tempTotal));
									mSpeed = (int) (tempTotal * 1000 / interval / 1024);
									startTime = System.currentTimeMillis();
									tempTotal = 0;
								}
								// publish progress
								publishCurrentProgressOneShot();
								isFinished = true;
							}
							fos.flush();
							fos.close();
							if (isFinished) {
							}

						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						Log.i("tag", "NO SDCard.");
					}

					input.close();
					result = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return result;
		}
		
		public int getProgress() {
			return mProgress;
		}

		public int getPosition() {
			return mPosition;
		}
	}

	class NoResultType {
	}
}
