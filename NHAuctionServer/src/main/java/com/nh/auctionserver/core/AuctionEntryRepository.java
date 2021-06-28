package com.nh.auctionserver.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.nh.share.controller.models.EntryInfo;

public class AuctionEntryRepository {
	private Map<String, Integer> mTotalCountMap = new HashMap<String, Integer>();
	private Map<String, LinkedList<EntryInfo>> mEntryListMap = new HashMap<String, LinkedList<EntryInfo>>();

	public synchronized void setInitialEntryList(String auctionHouseCode, List<EntryInfo> entryList) {
		if (mTotalCountMap != null) {
			mEntryListMap.get(auctionHouseCode).addAll(entryList);
			mTotalCountMap.put(auctionHouseCode, mEntryListMap.get(auctionHouseCode).size());
		}
	}

	public synchronized int getTotalCount(String auctionHouseCode) {
		int result = 0;

		if (mTotalCountMap != null) {
			if (mTotalCountMap.containsKey(auctionHouseCode)) {
				result = mTotalCountMap.get(auctionHouseCode);
			}
		}

		return result;
	}

	public synchronized List<EntryInfo> getEntryList(String auctionHouseCode) {
		List<EntryInfo> result = new LinkedList<EntryInfo>();

		if (mEntryListMap != null) {
			if (mEntryListMap.containsKey(auctionHouseCode)) {
				result.addAll(mEntryListMap.get(auctionHouseCode));
			}
		}

		return result;
	}

	public synchronized EntryInfo getEntryInfo(String auctionHouseCode, String entryNum) {
		List<EntryInfo> entryList = new LinkedList<EntryInfo>();

		if (mEntryListMap != null) {
			if (mEntryListMap.containsKey(auctionHouseCode)) {
				entryList.addAll(mEntryListMap.get(auctionHouseCode));

				if (entryList != null && entryList.size() > 0) {
					for (int i = 0; i < entryList.size(); i++) {
						if (entryList.get(i).getEntryNum().equals(entryNum)) {
							return entryList.get(i);
						}
					}
				}
			}
		}

		return null;
	}

	public synchronized EntryInfo popEntry(String auctionHouseCode, String entryNum) {
		EntryInfo entryInfo = null;

		if (mEntryListMap != null) {
			if (mEntryListMap.containsKey(auctionHouseCode)) {
				if (mEntryListMap.get(auctionHouseCode) != null && mEntryListMap.get(auctionHouseCode).size() > 0) {
					for (int i = 0; i < mEntryListMap.get(auctionHouseCode).size(); i++) {
						if (mEntryListMap.get(auctionHouseCode).get(i).getEntryNum().equals(entryNum)) {
							entryInfo = mEntryListMap.get(auctionHouseCode).get(i);
							mEntryListMap.get(auctionHouseCode).remove(i);
							mTotalCountMap.put(auctionHouseCode, mTotalCountMap.get(auctionHouseCode) - 1);
						}
					}
				}
			}
		}

		return entryInfo;
	}

	public synchronized EntryInfo popEntry(String auctionHouseCode) {
		EntryInfo entryInfo = null;

		if (mEntryListMap != null) {
			for(String key : mEntryListMap.keySet()) {
				System.out.println("mEntryListMap key : " + key);
				System.out.println("mEntryListMap value size : " + mEntryListMap.get(key).size());
			}
			
			if (mEntryListMap.containsKey(auctionHouseCode)) {
				entryInfo = mEntryListMap.get(auctionHouseCode).get(0);
				mEntryListMap.get(auctionHouseCode).remove(0);
				mTotalCountMap.put(auctionHouseCode, mTotalCountMap.get(auctionHouseCode) - 1);
			}
		}

		return entryInfo;
	}

	public synchronized void pushEntry(String auctionHouseCode, EntryInfo entry) {
		if (mEntryListMap != null) {
			if (mEntryListMap.containsKey(auctionHouseCode)) {
				mEntryListMap.get(auctionHouseCode).add(entry);
			} else {
				mEntryListMap.put(auctionHouseCode, new LinkedList<EntryInfo>());
				mEntryListMap.get(auctionHouseCode).add(entry);
			}
			
			if(mTotalCountMap.containsKey(auctionHouseCode)) {
				mTotalCountMap.put(auctionHouseCode, mTotalCountMap.get(auctionHouseCode) + 1);
			} else {
				mTotalCountMap.put(auctionHouseCode, 1);
			}

			System.out.println("출품 자료가 추가되었습니다.(자료수 : " + mTotalCountMap.get(auctionHouseCode) + ")");
		}
	}
}
