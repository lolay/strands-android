//
//  Copyright 2011, 2012, 2013 Lolay, Inc.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//
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
