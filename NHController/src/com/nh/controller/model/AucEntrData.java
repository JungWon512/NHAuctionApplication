package com.nh.controller.model;

/**
 * 가축시장_응찰로그내역
 * @author jhlee
 */
public class AucEntrData {

	private String naBzplc;                 //경제통합사업장코드
	private int aucObjDsc;              	//경매대상구분코드	
	private String aucDt;                   //경매일자			
	private String oslpNo;                  //원표번호			
	private String rgSqno;                  //등록일련번호		
	private String trmnAmnno;               //거래인관리번호	
	private String lvstAucPtcMnNo;          //가축경매참여자번호
	private String atdrAm;                  //응찰금액			
	private String rmkCntn;                 //비고내용			
	private String atdrDtm;                 //응찰일시			
	private String mmoInpYn;                //수기입력여부		
	private String aucPrgSq;                //경매진행순번		
	private String tmsYnC;                  //전송여부
	
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
	public String getOslpNo() {
		return oslpNo;
	}
	public void setOslpNo(String oslpNo) {
		this.oslpNo = oslpNo;
	}
	public String getRgSqno() {
		return rgSqno;
	}
	public void setRgSqno(String rgSqno) {
		this.rgSqno = rgSqno;
	}
	public String getTrmnAmnno() {
		return trmnAmnno;
	}
	public void setTrmnAmnno(String trmnAmnno) {
		this.trmnAmnno = trmnAmnno;
	}
	public String getLvstAucPtcMnNo() {
		return lvstAucPtcMnNo;
	}
	public void setLvstAucPtcMnNo(String lvstAucPtcMnNo) {
		this.lvstAucPtcMnNo = lvstAucPtcMnNo;
	}
	public String getAtdrAm() {
		return atdrAm;
	}
	public void setAtdrAm(String atdrAm) {
		this.atdrAm = atdrAm;
	}
	public String getRmkCntn() {
		return rmkCntn;
	}
	public void setRmkCntn(String rmkCntn) {
		this.rmkCntn = rmkCntn;
	}
	public String getAtdrDtm() {
		return atdrDtm;
	}
	public void setAtdrDtm(String atdrDtm) {
		this.atdrDtm = atdrDtm;
	}
	public String getMmoInpYn() {
		return mmoInpYn;
	}
	public void setMmoInpYn(String mmoInpYn) {
		this.mmoInpYn = mmoInpYn;
	}
	public String getAucPrgSq() {
		return aucPrgSq;
	}
	public void setAucPrgSq(String aucPrgSq) {
		this.aucPrgSq = aucPrgSq;
	}
	public String getTmsYnC() {
		return tmsYnC;
	}
	public void setTmsYnC(String tmsYnC) {
		this.tmsYnC = tmsYnC;
	}
	
	

}
