/*
 * Created by Lolay, Inc.
 * Copyright 2011 Lolay, Inc. All rights reserved.
 */
package com.lolay.android.progress;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.app.Activity;
import android.view.View;

import com.lolay.android.log.LolayLog;

public class LolayProgressManager implements LolayProgressListener {
	private static final String TAG = LolayProgressManager.class.getSimpleName();
	public static final String ACTIVITY_NOTIFICATION = LolayProgressManager.class.getName() + ".ACTIVITY_NOTIFICATION";
	private static final Boolean VALUE = Boolean.TRUE;
	private int defaultActiveVisibility = View.VISIBLE;
	private int defaultInactiveVisibility = View.GONE;
	private int progressCount = 0;
	private Map<View,LolayProgressManagedView> managedViews = new WeakHashMap<View,LolayProgressManagedView>();
	private Map<LolayProgressListener,Boolean> listeners = new WeakHashMap<LolayProgressListener,Boolean>(); // Used as a Set as Android 2.2 doesn't support newSetFromMap
	private Lock progressLock = new ReentrantLock();
	private Lock managedViewsLock = new ReentrantLock();
	private Lock listenersLock = new ReentrantLock();
	
	public LolayProgressManager() {
		listeners.put(this, VALUE);
	}
	
	public boolean isActive() {
		progressLock.lock();
		try {
			return progressCount > 0;
		} finally {
			progressLock.unlock();
		}
	}
	
	public void setDefaultActiveVisibility(int visibility) {
		defaultActiveVisibility = visibility;
	}
	
	public void setDefaultInactiveVisibility(int visibility) {
		defaultInactiveVisibility = visibility;
	}
	
	public void manageVisibilityOnProgressChanged(Activity activity, int id) {
		View view = activity.findViewById(id);
		if (view != null) {
			manageVisibilityOnProgressChanged(view);
		} else {
			LolayLog.w(TAG, "manageVisibilityOnProgressChanged", "Could not find view to manage by id (id=%s)", id);
		}
	}
	
	public void manageVisibilityOnProgressChanged(View view) {
		manageVisibilityOnProgressChanged(view, defaultActiveVisibility, defaultInactiveVisibility);
	}
	
	public void manageVisibilityOnProgressChanged(Activity activity, int id, int activeVisibility, int inactiveVisibility) {
		View view = activity.findViewById(id);
		if (view != null) {
			manageVisibilityOnProgressChanged(view, activeVisibility, inactiveVisibility);
		} else {
			LolayLog.w(TAG, "manageVisibilityOnProgressChanged", "Could not find view to manage by id (id=%s,activeVisibility=%s,inactiveVisibility%s)", id, activeVisibility, inactiveVisibility);
		}
	}
	
	public void manageVisibilityOnProgressChanged(View view, int activeVisibility, int inactiveVisibility) {
		managedViewsLock.lock();
		try {
			LolayProgressManagedView managedView = managedViews.get(view);
			if (managedView == null) {
				managedView = new LolayProgressManagedView(activeVisibility, inactiveVisibility);
				view.setVisibility(inactiveVisibility);
			}
			managedViews.put(view, managedView);
		} finally {
			managedViewsLock.unlock();
		}
		LolayLog.d(TAG, "manageVisibilityOnProgressChanged", "view managed (view=%s,activeVisibility=%s,inactiveVisibility=%s,managedViews.size=%s)", view, activeVisibility, inactiveVisibility, managedViews.size());
	}
	
	public void unmanageVisibilityOnProgressChanged(Activity activity, int id) {
		View view = activity.findViewById(id);
		if (view != null) {
			unmanageVisibilityOnProgressChanged(view);
		} else {
			LolayLog.w(TAG, "unmanageVisibilityOnProgressChanged", "Could not find view to unmanage by id (id=%s)", id);
		}
	}

	public void unmanageVisibilityOnProgressChanged(View view) {
		managedViewsLock.lock();
		try {
			managedViews.remove(view);
		} finally {
			managedViewsLock.unlock();
		}
		LolayLog.d(TAG, "unmanageVisibilityOnProgressChanged", "view unmanaged (view=%s,managedViews.size=%s)", view, managedViews.size());
	}
	
	@Override
	public void onProgressChanged(boolean isActive) {
		LolayLog.d(TAG, "onProgressChanged", "activity changed (isActive=%s,managedViews.size=%s)", isActive, managedViews.size());
		managedViewsLock.lock();
		try {
			for (Map.Entry<View,LolayProgressManagedView> managedViewEntry : managedViews.entrySet()) {
				View view = managedViewEntry.getKey();
				if (view != null) {
					int visibility;
					if (isActive) {
						visibility = managedViewEntry.getValue().getActiveVisibility();
					} else {
						visibility = managedViewEntry.getValue().getInactiveVisibility();
					}
					view.setVisibility(visibility);
					LolayLog.v(TAG, "onProgressChanged", "Set visibility=%s", visibility);
				} else {
					LolayLog.v(TAG, "onProgressChanged", "View was null, so not setting visibility");
				}
			}
		} finally {
			managedViewsLock.unlock();
		}
	}
	
	public void addProgressListener(LolayProgressListener listener) {
		listenersLock.lock();
		try {
			if (listeners.containsKey(listener)) {
				listeners.remove(listener);
			}
			listeners.put(listener, VALUE);
		} finally {
			listenersLock.unlock();
		}
		LolayLog.d(TAG, "addProgressListener", "listener added (listener=%s)", listener);
	}
	
	public void removeProgressListener(LolayProgressListener listener) {
		listenersLock.lock();
		try {
			listeners.remove(listener);
		} finally {
			listenersLock.unlock();
		}
		LolayLog.d(TAG, "removeProgressListener", "listener removed (listener=%s)", listener);
	}
	
	private void notifyOnProgressChanged(boolean isActive) {
		listenersLock.lock();
		try {
			for (LolayProgressListener listener : listeners.keySet()) {
				listener.onProgressChanged(isActive);
			}
		} finally {
			listenersLock.unlock();
		}
	}
	
	private void adjustCount(int delta) {
		progressLock.lock();
		try {
			boolean oldIsActivity = isActive();
			if (delta == 0) {
				progressCount = 0;
			} else {
				progressCount += delta;
			}
			
			boolean isActive = isActive();
			if (oldIsActivity != isActive) {
				notifyOnProgressChanged(isActive);
			}
		} finally {
			progressLock.unlock();
		}
	}
	
	public void incrementCount() {
		adjustCount(1);
	}
	
	public void decrementCount() {
		adjustCount(-1);
	}
	
	public void clearCount() {
		adjustCount(0);
		LolayLog.v(TAG, "clearCount", "Progress cleared");
	}
}
