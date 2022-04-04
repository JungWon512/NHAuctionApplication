package com.nh.controller.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 경매 회차 정보
 * @author jhlee
 *
 */
public class SpAuctionRoundData {

	private StringProperty naBzplc; // 경제통합사업장코드
	private StringProperty aucObjDsc; // 경매대상구분코드
	private StringProperty aucDt; // 경매일자
	private StringProperty qcn; // 차수
	//private StringProperty baseLmtAm; // 기초한도금액
	private StringProperty cutAm; // 절사금액
	private StringProperty sgnoPrcDsc; // 단수처리구분코드
	private StringProperty ddlYn; // 마감여부
	private StringProperty tmsYn; // 전송여부
	private StringProperty delYn; // 삭제여부
	private StringProperty maleKg; // 수컷kg
	private StringProperty femaleKg; // 암컷kg
	private StringProperty ttScr; //
//	private StringProperty fsrgDtm; // 최초등록일시
//	private StringProperty fsgmnEno; // 최초등록자개인번호
//	private StringProperty lschgDtm; // 최종변경일시
//	private StringProperty lsCmeno; // 최초변경자개인번호

	public SpAuctionRoundData(AuctionRound auctionRound) {
		this.naBzplc = new SimpleStringProperty(auctionRound.getNaBzplc());
		this.aucObjDsc = new SimpleStringProperty(Integer.toString(auctionRound.getAucObjDsc()));
		this.aucDt = new SimpleStringProperty(auctionRound.getAucDt());
		this.qcn = new SimpleStringProperty(Integer.toString(auctionRound.getQcn()));
		this.cutAm = new SimpleStringProperty(Integer.toString(auctionRound.getCutAm()));
		this.sgnoPrcDsc = new SimpleStringProperty(auctionRound.getSgnoPrcDsc());
		this.ddlYn = new SimpleStringProperty(auctionRound.getDdlYn());
		this.tmsYn = new SimpleStringProperty(auctionRound.getTmsYn());
		this.delYn = new SimpleStringProperty(auctionRound.getDelYn());
		this.maleKg = new SimpleStringProperty(Integer.toString(auctionRound.getMaleKg()));
		this.femaleKg = new SimpleStringProperty(Integer.toString(auctionRound.getFemaleKg()));
		this.ttScr = new SimpleStringProperty(Float.toString(auctionRound.getTtScr()));
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

	public StringProperty getQcn() {
		return qcn;
	}

	public void setQcn(StringProperty qcn) {
		this.qcn = qcn;
	}

	public StringProperty getCutAm() {
		return cutAm;
	}

	public void setCutAm(StringProperty cutAm) {
		this.cutAm = cutAm;
	}

	public StringProperty getSgnoPrcDsc() {
		return sgnoPrcDsc;
	}

	public void setSgnoPrcDsc(StringProperty sgnoPrcDsc) {
		this.sgnoPrcDsc = sgnoPrcDsc;
	}

	public StringProperty getDdlYn() {
		return ddlYn;
	}

	public void setDdlYn(StringProperty ddlYn) {
		this.ddlYn = ddlYn;
	}

	public StringProperty getTmsYn() {
		return tmsYn;
	}

	public void setTmsYn(StringProperty tmsYn) {
		this.tmsYn = tmsYn;
	}

	public StringProperty getDelYn() {
		return delYn;
	}

	public void setDelYn(StringProperty delYn) {
		this.delYn = delYn;
	}

	public StringProperty getMaleKg() {
		return maleKg;
	}

	public void setMaleKg(StringProperty maleKg) {
		this.maleKg = maleKg;
	}

	public StringProperty getFemaleKg() {
		return femaleKg;
	}

	public void setFemaleKg(StringProperty femaleKg) {
		this.femaleKg = femaleKg;
	}

	public StringProperty getTtScr() {
		return ttScr;
	}

	public void setTtScr(StringProperty ttScr) {
		this.ttScr = ttScr;
	}
	
}
