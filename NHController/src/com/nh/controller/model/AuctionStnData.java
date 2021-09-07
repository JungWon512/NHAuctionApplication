package com.nh.controller.model;

import java.util.ArrayList;
import java.util.List;

import com.nh.controller.utils.CommonUtils;
import com.nh.share.controller.models.EntryInfo;

import javafx.beans.property.StringProperty;

/**
 * 일괄경매 VO
 * @author jhlee
 *
 */
public class AuctionStnData implements Cloneable{

	private String naBzplc;
	private String aucObjDsc;
	private String aucDt;
	private String rgSqno;
	private String stAucNo;
	private String edAucNo;
	private String selStsDsc;
	private String delYn;
	private String ddlQcn;
	private String fsrgDtm;
	private String fsrgmnEno;
	private String lschgDtm;
	private String lsCmeno;
	
	private String totalCount;
	private String progressCount;
	
	private List<EntryInfo> EntryInfoDataList = null;

	public String getNaBzplc() {
		return naBzplc;
	}
	public void setNaBzplc(String naBzplc) {
		this.naBzplc = naBzplc;
	}
	public String getAucObjDsc() {
		return aucObjDsc;
	}
	public void setAucObjDsc(String aucObjDsc) {
		this.aucObjDsc = aucObjDsc;
	}
	public String getAucDt() {
		return aucDt;
	}
	public void setAucDt(String aucDt) {
		this.aucDt = aucDt;
	}
	public String getRgSqno() {
		return rgSqno;
	}
	public void setRgSqno(String rgSqno) {
		this.rgSqno = rgSqno;
	}
	public String getStAucNo() {
		return stAucNo;
	}
	public void setStAucNo(String stAucNo) {
		this.stAucNo = stAucNo;
	}
	public String getEdAucNo() {
		return edAucNo;
	}
	public void setEdAucNo(String edAucNo) {
		this.edAucNo = edAucNo;
	}
	public String getSelStsDsc() {
		return selStsDsc;
	}
	public void setSelStsDsc(String selStsDsc) {
		this.selStsDsc = selStsDsc;
	}
	public String getDelYn() {
		return delYn;
	}
	public void setDelYn(String delYn) {
		this.delYn = delYn;
	}
	public String getDdlQcn() {
		return ddlQcn;
	}
	public void setDdlQcn(String ddlQcn) {
		this.ddlQcn = ddlQcn;
	}
	public String getFsrgDtm() {
		return fsrgDtm;
	}
	public void setFsrgDtm(String fsrgDtm) {
		this.fsrgDtm = fsrgDtm;
	}
	public String getFsrgmnEno() {
		return fsrgmnEno;
	}
	public void setFsrgmnEno(String fsrgmnEno) {
		this.fsrgmnEno = fsrgmnEno;
	}
	public String getLschgDtm() {
		return lschgDtm;
	}
	public void setLschgDtm(String lschgDtm) {
		this.lschgDtm = lschgDtm;
	}
	public String getLsCmeno() {
		return lsCmeno;
	}
	public void setLsCmeno(String lsCmeno) {
		this.lsCmeno = lsCmeno;
	}
	public List<EntryInfo> getEntryInfoDataList() {
		return EntryInfoDataList;
	}
	
	public String getTotalCount() {
		
		if(!CommonUtils.getInstance().isValidString(totalCount)) {
			return "0";
		}
		
		return totalCount;
	}
	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}
	public String getProgressCount() {
		
		if(!CommonUtils.getInstance().isValidString(progressCount)) {
			return "0";
		}
		
		return progressCount;
	}
	
	public void setProgressCount(String progressCount) {
		this.progressCount = progressCount;
	}
	public void setEntryInfoDataList(List<EntryInfo> entryInfoDataList) {
		if(EntryInfoDataList != null) {
			EntryInfoDataList.clear();
		}
		EntryInfoDataList = entryInfoDataList;
	}
	
	public AuctionStnData clone() {
		AuctionStnData vo = null;
		try {
			vo = (AuctionStnData) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return vo;		
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}
