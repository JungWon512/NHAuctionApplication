package com.nh.auctionserver.core;

import java.util.LinkedList;
import java.util.List;

import com.nh.share.api.model.AuctionEntryInformationResult;

public class AuctionEntryRepository {
    private int mTotalCount = 0;

    // Test [Start]
    private List<AuctionEntryInformationResult> mEntryList = new LinkedList<>();

    public synchronized void setTestData() {
        List<AuctionEntryInformationResult> testData = new LinkedList<>();
        setInitialEntryList(testData);
    }
    // Test [End]

    public synchronized void setInitialEntryList(List<AuctionEntryInformationResult> entryList) {
        this.mEntryList.addAll(entryList);
        mTotalCount = this.mEntryList.size();
    }

    public synchronized int getTotalCount() {
        return mTotalCount;
    }

    public synchronized List<AuctionEntryInformationResult> getEntryList() {
        return mEntryList;
    }

    public synchronized AuctionEntryInformationResult getEntryInfo(String entrySeqNum) {
        if (mEntryList != null && mEntryList.size() > 0) {
            for (int i = 0; i < mEntryList.size(); i++) {
                if (mEntryList.get(i).getAuctionEntrySeq().equals(entrySeqNum)) {
                    return mEntryList.get(i);
                }
            }
        }

        return null;
    }

    public synchronized AuctionEntryInformationResult popEntry() {
        AuctionEntryInformationResult entry = mEntryList.get(0);
        mEntryList.remove(0);
        mTotalCount -= 1;
        return entry;
    }

    public synchronized void pushEntry(AuctionEntryInformationResult entry) {
        mEntryList.add(entry);
        mTotalCount += 1;
    }
}
