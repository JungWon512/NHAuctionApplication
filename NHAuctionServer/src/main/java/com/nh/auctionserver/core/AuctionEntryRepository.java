package com.nh.auctionserver.core;

import java.util.LinkedList;
import java.util.List;

import com.nh.share.controller.models.EntryInfo;

public class AuctionEntryRepository {
	private int mTotalCount = 0;
	private List<EntryInfo> mEntryList = new LinkedList<>();

	public synchronized void setInitialEntryList(List<EntryInfo> entryList) {
		this.mEntryList.addAll(entryList);
		mTotalCount = this.mEntryList.size();
	}

	public synchronized int getTotalCount() {
		return mTotalCount;
	}

	public synchronized List<EntryInfo> getEntryList() {
		return mEntryList;
	}

	public synchronized EntryInfo getEntryInfo(String entryNum) {
		if (mEntryList != null && mEntryList.size() > 0) {
			for (int i = 0; i < mEntryList.size(); i++) {
				if (mEntryList.get(i).getEntryNum().equals(entryNum)) {
					return mEntryList.get(i);
				}
			}
		}

		return null;
	}

	public synchronized EntryInfo popEntry() {
		EntryInfo entry = mEntryList.get(0);
		mEntryList.remove(0);
		mTotalCount -= 1;
		return entry;
	}

	public synchronized void pushEntry(EntryInfo entry) {
		mEntryList.add(entry);
		mTotalCount += 1;
	}
}
