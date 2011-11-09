/*
 * Created by Lolay, Inc.
 * Copyright 2011 Lolay, Inc. All rights reserved.
 */
package com.lolay.android.task;

import java.lang.ref.WeakReference;

import com.lolay.android.log.LolayLog;
import com.lolay.android.progress.LolayProgressManager;

import android.os.AsyncTask;

public abstract class LolayAsyncTask<Params,Progress,Result> extends AsyncTask<Params,Progress,Result> {
	private static final String TAG = LolayLog.buildTag(LolayAsyncTask.class);
	private WeakReference<LolayProgressManager> progressManagerReference = null;
	
	public LolayAsyncTask() { }
	
	/**
	 * @param progressManager Can be null
	 */
	public LolayAsyncTask(LolayProgressManager progressManager) {
		if (progressManager != null) {
			progressManagerReference = new WeakReference<LolayProgressManager>(progressManager);
		}
	}
	
	protected void onPreExecute() {
		LolayLog.d(TAG, "onPreExecute", "enter");
		if (progressManagerReference != null) {
			LolayProgressManager progressManager = progressManagerReference.get();
			if (progressManager != null) {
				progressManager.incrementCount();
			}
		}
	}
	
	private void decrementCount() {
		if (progressManagerReference != null) {
			LolayProgressManager progressManager = progressManagerReference.get();
			if (progressManager != null) {
				progressManager.decrementCount();
			}
		}
	}
	
	protected void onPostExecute(Result result) {
		LolayLog.d(TAG, "onPostExecute", "enter");
		decrementCount();
	}
	
	protected void onCancelled() {
		LolayLog.d(TAG, "onCancelled", "enter");
		decrementCount();
	}
	
	public void setProgressManager(LolayProgressManager progressManager) {
		progressManagerReference = new WeakReference<LolayProgressManager>(progressManager);
	}
	
	public LolayProgressManager getProgressManager() {
		return progressManagerReference.get();
	}
}
