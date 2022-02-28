package com.nh.share.api;

import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.share.api.request.Action;
import com.nh.share.utils.CommonUtils;

public class ActionRuler {

	private final ConcurrentLinkedQueue<Runnable> mRunnableList = new ConcurrentLinkedQueue<Runnable>();
	//private final LinkedList<Runnable> mRunnableList = new LinkedList<Runnable>();
	
	private static final ActionRuler ruler = new ActionRuler();
	
	private int requestTotalCount = 0;

	private Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private ActionRuler() {
	}

	public static ActionRuler getInstance() {
		return ruler;
	}

	public synchronized void runNext() {

		if (!mRunnableList.isEmpty()) {

			if (!mRunnableList.isEmpty()) {
				Runnable runnable = mRunnableList.poll();

				if (runnable != null) {
					Thread mHandler = new Thread(runnable);
					mHandler.start();
				}
			} else {
				mRunnableList.remove(0);
			}
		} else {
			runFinishAction();
		}
	}

	public void finish() {
		clear();
		runFinishAction();
	}

	public void skip() {
		if (!mRunnableList.isEmpty())
			mRunnableList.remove(0);
	}

	public boolean hasAction(Runnable runnable) {
		if (mRunnableList.contains(runnable)) {
			return true;
		}
		return false;
	}

	public void remove(Runnable runnable) {
		if (mRunnableList.contains(runnable))
			mRunnableList.remove(runnable);
	}

	public void addRunnable(Runnable runnable) {
		mRunnableList.add(runnable);
	}

	public void addAction(Action action) {
		mRunnableList.add(action);
	}

	public void addActionCount(Action action) {
		requestTotalCount++;
		mRunnableList.add(action);
	}

	public void addActionAtFirst(Action action) {
		mRunnableList.add(action);
		//mRunnableList.add(0, action);
	}

	public void addRunnableAtFirst(Runnable runnable) {
		mRunnableList.add(runnable);
		//mRunnableList.add(0, runnable);
	}

	public int getCount() {
		if (mRunnableList.isEmpty()) {
			return 0;
		}
		return mRunnableList.size();
	}

	private void clear() {
		mRunnableList.clear();
	}

	private Action mFinishAction = null;

	public void setOnFinishAction(Action action) {
		mFinishAction = action;
	}

	private void runFinishAction() {
		if (mFinishAction != null) {
			Action temp = mFinishAction;
			mFinishAction = null;
			temp.run();
			requestTotalCount = 0;
		}
	}

	public void dismissLoadingDialog() {

		requestTotalCount--;

		if (requestTotalCount <= 0) {
			requestTotalCount = 0;
			CommonUtils.getInstance().dismissLoadingDialog();
		}
	}

	private boolean isListEmpty(List list) {
		if (list != null && list.size() > 0) {
			return false;
		} else {
			return true;
		}
	}
}
