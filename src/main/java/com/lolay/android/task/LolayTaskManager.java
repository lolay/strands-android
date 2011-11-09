/*
 * Created by Lolay, Inc.
 * Copyright 2011 Lolay, Inc. All rights reserved.
 */
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
