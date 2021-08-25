package com.nh.controller.model;

import java.time.LocalDateTime;

import com.nh.share.controller.models.EntryInfo;
/**
 * 경매 회차정보 DTO
 * 
 * @author dhKim
 *
 */
public class AuctionRound extends SearchParamData implements Cloneable{

	private String naBzplc; // 경제통합사업장코드
	private int aucObjDsc; // 경매대상구분코드
	private String aucDt; // 경매일자
	private int qcn; // 차수
	private int baseLmtAm; // 기초한도금액
	private int cutAm; // 절사금액
	private Boolean sgnoPrcDsc; // 단수처리구분코드
	private Boolean ddlYn; // 마감여부
	private Boolean tmsYn; // 전송여부
	private Boolean delYn; // 삭제여부
	private int maleKg; // 수컷kg
	private int femaleKg; // 암컷kg
	private float ttScr; //
	private LocalDateTime fsrgDtm; // 최초등록일시
	private String fsgmnEno; // 최초등록자개인번호
	private LocalDateTime lschgDtm; // 최종변경일시
	private String lsCmeno; // 최초변경자개인번호
	
	

	public AuctionRound() {
	}

	public AuctionRound(String naBzplc, int aucObjDsc, String aucDt, int qcn, int baseLmtAm, int cutAm,
			Boolean sgnoPrcDsc, Boolean ddlYn, Boolean tmsYn, Boolean delYn, int maleKg, int femaleKg, float ttScr,
			LocalDateTime fsrgDtm, String fsgmnEno, LocalDateTime lschgDtm, String lsCmeno) {
		super();
		this.naBzplc = naBzplc;
		this.aucObjDsc = aucObjDsc;
		this.aucDt = aucDt;
		this.qcn = qcn;
		this.baseLmtAm = baseLmtAm;
		this.cutAm = cutAm;
		this.sgnoPrcDsc = sgnoPrcDsc;
		this.ddlYn = ddlYn;
		this.tmsYn = tmsYn;
		this.delYn = delYn;
		this.maleKg = maleKg;
		this.femaleKg = femaleKg;
		this.ttScr = ttScr;
		this.fsrgDtm = fsrgDtm;
		this.fsgmnEno = fsgmnEno;
		this.lschgDtm = lschgDtm;
		this.lsCmeno = lsCmeno;
	}

	public String getNaBzplc() {
		return naBzplc;
	}

	public void setNaBzplc(String naBzplc) {
		this.naBzplc = naBzplc;
	}

	public int getAucObjDsc() {
		return aucObjDsc;
	}

	public void setAucObjDsc(int aucObjDsc) {
		this.aucObjDsc = aucObjDsc;
	}

	public String getAucDt() {
		return aucDt;
	}

	public void setAucDt(String aucDt) {
		this.aucDt = aucDt;
	}

	public int getQcn() {
		return qcn;
	}

	public void setQcn(int qcn) {
		this.qcn = qcn;
	}

	public int getBaseLmtAm() {
		return baseLmtAm;
	}

	public void setBaseLmtAm(int baseLmtAm) {
		this.baseLmtAm = baseLmtAm;
	}

	public int getCutAm() {
		return cutAm;
	}

	public void setCutAm(int cutAm) {
		this.cutAm = cutAm;
	}

	public Boolean getSgnoPrcDsc() {
		return sgnoPrcDsc;
	}

	public void setSgnoPrcDsc(Boolean sgnoPrcDsc) {
		this.sgnoPrcDsc = sgnoPrcDsc;
	}

	public Boolean getDdlYn() {
		return ddlYn;
	}

	public void setDdlYn(Boolean ddlYn) {
		this.ddlYn = ddlYn;
	}

	public Boolean getTmsYn() {
		return tmsYn;
	}

	public void setTmsYn(Boolean tmsYn) {
		this.tmsYn = tmsYn;
	}

	public Boolean getDelYn() {
		return delYn;
	}

	public void setDelYn(Boolean delYn) {
		this.delYn = delYn;
	}

	public int getMaleKg() {
		return maleKg;
	}

	public void setMaleKg(int maleKg) {
		this.maleKg = maleKg;
	}

	public int getFemaleKg() {
		return femaleKg;
	}

	public void setFemaleKg(int femaleKg) {
		this.femaleKg = femaleKg;
	}

	public float getTtScr() {
		return ttScr;
	}

	public void setTtScr(float ttScr) {
		this.ttScr = ttScr;
	}

	public LocalDateTime getFsrgDtm() {
		return fsrgDtm;
	}

	public void setFsrgDtm(LocalDateTime fsrgDtm) {
		this.fsrgDtm = fsrgDtm;
	}

	public String getFsgmnEno() {
		return fsgmnEno;
	}

	public void setFsgmnEno(String fsgmnEno) {
		this.fsgmnEno = fsgmnEno;
	}

	public LocalDateTime getLschgDtm() {
		return lschgDtm;
	}

	public void setLschgDtm(LocalDateTime lschgDtm) {
		this.lschgDtm = lschgDtm;
	}

	public String getLsCmeno() {
		return lsCmeno;
	}

	public void setLsCmeno(String lsCmeno) {
		this.lsCmeno = lsCmeno;
	}

	@Override
	public String toString() {
		return "AuctionRound [naBzplc=" + naBzplc + ", aucObjDsc=" + aucObjDsc + ", aucDt=" + aucDt + ", qcn=" + qcn
				+ ", baseLmtAm=" + baseLmtAm + ", cutAm=" + cutAm + ", sgnoPrcDsc=" + sgnoPrcDsc + ", ddlYn=" + ddlYn
				+ ", tmsYn=" + tmsYn + ", delYn=" + delYn + ", maleKg=" + maleKg + ", femaleKg=" + femaleKg + ", ttScr="
				+ ttScr + ", fsrgDtm=" + fsrgDtm + ", fsgmnEno=" + fsgmnEno + ", lschgDtm=" + lschgDtm + ", lsCmeno="
				+ lsCmeno + "]";
	}
	
	public AuctionRound clone() {
		AuctionRound vo = null;
		try {
			vo = (AuctionRound) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return vo;		
	}
}
