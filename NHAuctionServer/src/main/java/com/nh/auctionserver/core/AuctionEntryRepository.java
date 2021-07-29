package com.nh.auctionserver.core;

import java.util.LinkedList;
import java.util.List;

import com.nh.share.controller.models.EntryInfo;

public class AuctionEntryRepository {
	private int mTotalCount = 0;
	private LinkedList<EntryInfo> mEntryList = new LinkedList<EntryInfo>();

	public synchronized void setInitialEntryList(List<EntryInfo> entryList) {
		if (mTotalCount != 0) {
			mEntryList.addAll(entryList);
			mTotalCount = mEntryList.size();
		}
	}

	public synchronized int getTotalCount() {
		return mTotalCount;
	}

	public synchronized List<EntryInfo> getEntryList() {
		return mEntryList;
	}

	public synchronized EntryInfo getEntryInfo(String entryNum) {
		if (mEntryList != null) {
			if (mEntryList != null && mEntryList.size() > 0) {
				for (int i = 0; i < mEntryList.size(); i++) {
					if (mEntryList.get(i).getEntryNum().equals(entryNum)) {
						return mEntryList.get(i);
					}
				}
			}
		}

		return null;
	}

	public synchronized EntryInfo popEntry(String entryNum) {
		EntryInfo entryInfo = null;

		if (mEntryList != null) {
			if (mEntryList.size() > 0) {
				for (int i = 0; i < mEntryList.size(); i++) {
					if (mEntryList.get(i).getEntryNum().equals(entryNum)) {
						entryInfo = mEntryList.get(i);
						//mEntryList.remove(i);
						mTotalCount--;
					}
				}
			}
		}

		if (entryInfo == null) {
			mTotalCount = -1;
		}

		return entryInfo;
	}

	public synchronized EntryInfo popEntry() {
		EntryInfo entryInfo = null;

		if (mEntryList != null && mEntryList.size() > 0) {
			entryInfo = mEntryList.get(0);
			//mEntryList.remove(0);
			//mTotalCount--;
		}

		if (entryInfo == null) {
			mTotalCount = -1;
		}

		return entryInfo;
	}

	public synchronized void pushEntry(EntryInfo entry) {
		if (mEntryList != null) {

			if (mEntryList.contains(entry)) {
				mEntryList.set((Integer.valueOf(entry.getEntryNum()) - 1), entry);
			} else {
				mEntryList.add(entry);
				mTotalCount++;
			}

			System.out.println("출품 자료가 추가되었습니다.(자료수 : " + mTotalCount + ")");
		}
	}
}
