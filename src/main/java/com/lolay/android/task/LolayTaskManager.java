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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lolay.android.progress.LolayProgressManager;

import android.os.AsyncTask;

public class LolayTaskManager {
	private WeakReference<LolayProgressManager> progressManagerReference = null;
	private Map<Object,Set<AsyncTask<?,?,?>>> taskMap = null;
	private Lock tasksLock = new ReentrantLock();
	
	public LolayTaskManager(LolayProgressManager progressManager) {
		if (progressManager != null) {
			progressManagerReference = new WeakReference<LolayProgressManager>(progressManager);
		}
	}
	
	public <Params,Progress,Result> void addTaskAndExecute(Object object, AsyncTask<Params,Progress,Result> task, Params... params) {
		tasksLock.lock();
		try {
			if (taskMap == null) {
				taskMap = new HashMap<Object,Set<AsyncTask<?,?,?>>>();
			}
			
			Set<AsyncTask<?,?,?>> tasks = taskMap.get(object);
			if (tasks == null) {
				tasks = new HashSet<AsyncTask<?,?,?>>();
				taskMap.put(object, tasks);
			}
			tasks.add(task);
			if (task instanceof LolayAsyncTask && progressManagerReference != null) {
				LolayProgressManager progressManager = progressManagerReference.get();
				if (progressManager != null) {
					((LolayAsyncTask<Params,Progress,Result>) task).setProgressManager(progressManager);
				}
			}
			task.execute(params);
		} finally {
			tasksLock.unlock();
		}
	}

	public void cancelTasks(Object object) {
		tasksLock.lock();
		try {
			if (taskMap != null) {
				Set<AsyncTask<?,?,?>> tasks = taskMap.get(object);
				if (tasks != null) {
					for (AsyncTask<?,?,?> task : tasks) {
						task.cancel(true);
					}
				}
				taskMap.remove(object);
			}
		} finally {
			tasksLock.unlock();
		}
	}

	public void cancelAllTasks() {
		tasksLock.lock();
		try {
			if (taskMap != null) {
				// We clone the keys as the underlying set will change
				for (Object object : new HashSet<Object>(taskMap.keySet())) {
					cancelTasks(object);
				}
			}
		} finally {
			tasksLock.unlock();
		}
	}
}
