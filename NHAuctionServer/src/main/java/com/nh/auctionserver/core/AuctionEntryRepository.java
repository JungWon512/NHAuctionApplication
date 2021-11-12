package com.nh.auctionserver.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.nh.share.controller.models.EntryInfo;

public class AuctionEntryRepository {
	private int mTotalCount = 0;
	private LinkedList<EntryInfo> mEntryList = new LinkedList<EntryInfo>();
	private Map<String, EntryInfo> mEntryMap = new HashMap<String, EntryInfo>();

	public synchronized void removeAllEntryList() {
		mEntryList = new LinkedList<EntryInfo>();
		mEntryMap = new HashMap<String, EntryInfo>();
		mTotalCount = 0;
	}

	public synchronized void setInitialEntryList(List<EntryInfo> entryList) {
		if (mTotalCount != 0) {
			mEntryList.addAll(entryList);
			
			for (int i = 0; i < entryList.size(); i++) {
				mEntryMap.put(entryList.get(i).getEntryNum(), entryList.get(i));
			}
			
			mTotalCount = mEntryList.size();
		}
	}

	public synchronized int getTotalCount() {
		mTotalCount = mEntryList.size();
		
		return mTotalCount;
	}

	public synchronized List<EntryInfo> getEntryList() {
		return mEntryList;
	}

	public synchronized EntryInfo getEntryInfo(String entryNum) {
		if (mEntryMap != null) {
			if (mEntryMap.containsKey(entryNum)) {
				return mEntryMap.get(entryNum);
			}
		}
		
		return null;
	}

	public synchronized EntryInfo popEntry(String entryNum) {
		EntryInfo entryInfo = null;

		if (mEntryMap != null) {
			if (mEntryMap.containsKey(entryNum)) {
				entryInfo = mEntryMap.get(entryNum);
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
			// mEntryList.remove(0);
			// mTotalCount--;
		}

		if (entryInfo == null) {
			mTotalCount = -1;
		}

		return entryInfo;
	}

	public synchronized void pushEntry(EntryInfo entry) {
		if (mEntryList != null) {

			if (mEntryList.contains(entry) && mEntryList.indexOf(entry) != -1) {
				mEntryList.set(mEntryList.indexOf(entry), entry);
			} else {
				mEntryList.add(entry);
			}
			
			if (mEntryMap != null) {
				mEntryMap.put(entry.getEntryNum(), entry);
			}

			System.out.println("출품 자료가 추가되었습니다.(자료수 : " + getTotalCount() + ")");
		}
	}

	public synchronized boolean changeStandPosionInfo(String entryNum, String standPosionNum) {
		boolean result = false;

		if (mEntryList != null) {
			if (mEntryList != null && mEntryList.size() > 0) {
				for (int i = 0; i < mEntryList.size(); i++) {
					if (mEntryList.get(i).getEntryNum().equals(entryNum)) {

						mEntryList.get(i).setStandPosition(standPosionNum);
						result = true;

						return result;
					}
				}
			}
			
			if (mEntryMap != null) {
				if (mEntryMap.containsKey(entryNum)) {
					mEntryMap.get(entryNum).setStandPosition(standPosionNum);
				}
			}
		}

		return result;
	}
}
