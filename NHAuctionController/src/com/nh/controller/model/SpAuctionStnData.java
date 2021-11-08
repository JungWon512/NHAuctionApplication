package com.nh.controller.model;

import java.util.List;

import com.nh.share.controller.models.EntryInfo;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 일괄경매정보
 * 
 * @author jhlee
 *
 */
public class SpAuctionStnData {

	private StringProperty naBzplc;
	private StringProperty aucObjDsc;
	private StringProperty aucDt;
	private StringProperty rgSqno;
	private StringProperty stAucNo;
	private StringProperty edAucNo;
	private StringProperty selStsDsc;
	private StringProperty delYn;
	private StringProperty ddlQcn;
	private StringProperty fsrgDtm;
	private StringProperty fsrgmnEno;
	private StringProperty lschgDtm;
	private StringProperty lsCmeno;
	
	private StringProperty totalCount;
	private StringProperty progressCount;

	private List<EntryInfo> entryInfoDataList = null;
	
	public SpAuctionStnData(AuctionStnData aucStnData) {
		this.naBzplc = new SimpleStringProperty(aucStnData.getNaBzplc());
		this.aucObjDsc = new SimpleStringProperty(aucStnData.getAucObjDsc());
		this.aucDt = new SimpleStringProperty(aucStnData.getAucDt());
		this.rgSqno = new SimpleStringProperty(aucStnData.getRgSqno());
		this.stAucNo = new SimpleStringProperty(aucStnData.getStAucNo());
		this.edAucNo = new SimpleStringProperty(aucStnData.getEdAucNo());
		this.selStsDsc = new SimpleStringProperty(aucStnData.getSelStsDsc());
		this.delYn = new SimpleStringProperty(aucStnData.getDelYn());
		this.ddlQcn = new SimpleStringProperty(aucStnData.getDdlQcn());
		this.fsrgDtm = new SimpleStringProperty(aucStnData.getFsrgDtm());
		this.fsrgmnEno = new SimpleStringProperty(aucStnData.getFsrgmnEno());
		this.lschgDtm = new SimpleStringProperty(aucStnData.getLschgDtm());
		this.lsCmeno = new SimpleStringProperty(aucStnData.getLsCmeno());
		this.entryInfoDataList = aucStnData.getEntryInfoDataList();
		this.totalCount = new SimpleStringProperty(aucStnData.getTotalCount());
		this.progressCount = new SimpleStringProperty(aucStnData.getProgressCount());
	}

	public StringProperty getNaBzplc() {
		return naBzplc;
	}

	public void setNaBzplc(StringProperty naBzplc) {
		this.naBzplc = naBzplc;
	}

	public StringProperty getAucObjDsc() {
		return aucObjDsc;
	}

	public void setAucObjDsc(StringProperty aucObjDsc) {
		this.aucObjDsc = aucObjDsc;
	}

	public StringProperty getAucDt() {
		return aucDt;
	}

	public void setAucDt(StringProperty aucDt) {
		this.aucDt = aucDt;
	}

	public StringProperty getRgSqno() {
		return rgSqno;
	}

	public void setRgSqno(StringProperty rgSqno) {
		this.rgSqno = rgSqno;
	}

	public StringProperty getStAucNo() {
		return stAucNo;
	}

	public void setStAucNo(StringProperty stAucNo) {
		this.stAucNo = stAucNo;
	}

	public StringProperty getEdAucNo() {
		return edAucNo;
	}

	public void setEdAucNo(StringProperty edAucNo) {
		this.edAucNo = edAucNo;
	}

	public StringProperty getSelStsDsc() {
		return selStsDsc;
	}

	public void setSelStsDsc(StringProperty selStsDsc) {
		this.selStsDsc = selStsDsc;
	}

	public StringProperty getDelYn() {
		return delYn;
	}

	public void setDelYn(StringProperty delYn) {
		this.delYn = delYn;
	}

	public StringProperty getDdlQcn() {
		return ddlQcn;
	}

	public void setDdlQcn(StringProperty ddlQcn) {
		this.ddlQcn = ddlQcn;
	}

	public StringProperty getFsrgDtm() {
		return fsrgDtm;
	}

	public void setFsrgDtm(StringProperty fsrgDtm) {
		this.fsrgDtm = fsrgDtm;
	}

	public StringProperty getFsrgmnEno() {
		return fsrgmnEno;
	}

	public void setFsrgmnEno(StringProperty fsrgmnEno) {
		this.fsrgmnEno = fsrgmnEno;
	}

	public StringProperty getLschgDtm() {
		return lschgDtm;
	}

	public void setLschgDtm(StringProperty lschgDtm) {
		this.lschgDtm = lschgDtm;
	}

	public StringProperty getLsCmeno() {
		return lsCmeno;
	}

	public void setLsCmeno(StringProperty lsCmeno) {
		this.lsCmeno = lsCmeno;
	}

	public List<EntryInfo> getEntryInfoDataList() {
		return entryInfoDataList;
	}

	public StringProperty getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(StringProperty totalCount) {
		this.totalCount = totalCount;
	}

	public StringProperty getProgressCount() {
		return progressCount;
	}

	public void setProgressCount(StringProperty progressCount) {
		this.progressCount = progressCount;
	}
	
}
