package com.nh.controller.interfaces;

import com.nh.controller.model.SpEntryInfo;
import com.nh.controller.utils.MoveStageUtil.EntryDialogType;

import javafx.collections.ObservableList;

/**
 * 전체,보류 목록 선택 Callback
 *
 */
public interface SelectEntryListener {

	public void callBack(EntryDialogType type,int index ,ObservableList<SpEntryInfo> entryDataList);

}
