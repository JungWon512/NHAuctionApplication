package com.nh.controller.model;

import java.time.LocalDateTime;

import com.nh.share.api.models.QcnData;
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
	private String sgnoPrcDsc; // 단수처리구분코드
	private String ddlYn; // 마감여부
	private String tmsYn; // 전송여부
	private String delYn; // 삭제여부
	private int maleKg; // 수컷kg
	private int femaleKg; // 암컷kg
	private float ttScr; //
	private LocalDateTime fsrgDtm; // 최초등록일시
	private String fsgmnEno; // 최초등록자개인번호
	private LocalDateTime lschgDtm; // 최종변경일시
	private String lsCmeno; // 최초변경자개인번호
	private int divisionPrice1; // 송아지 단위
	private int divisionPrice2; // 비육우 단위
	private int divisionPrice3; // 번식우 단위
	private int rgSqNo; // 일괄경매 차수 정보
	private String selStsDsc; // 일괄경매 상태 정보 (시작,정지,종료)
	
	public AuctionRound() {
	}

	public AuctionRound(String naBzplc, int aucObjDsc, String aucDt, int qcn, int baseLmtAm, int cutAm,
			String sgnoPrcDsc, String ddlYn, String tmsYn, String delYn, int maleKg, int femaleKg, float ttScr,
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
	
	public AuctionRound(QcnData qcnData) {
		
		this.naBzplc = qcnData.getNA_BZPLC();
		this.aucObjDsc = Integer.parseInt(qcnData.getAUC_OBJ_DSC());
		this.aucDt = qcnData.getAUC_DT();
		this.qcn = qcnData.getQCN();
		this.baseLmtAm = qcnData.getBASE_LMT_AM();
		this.cutAm = qcnData.getCUT_AM();
		this.sgnoPrcDsc = qcnData.getSGNO_PRC_DSC();
		this.ddlYn = qcnData.getDDL_YN();
		this.tmsYn = qcnData.getTMS_YN();
		this.delYn = qcnData.getDEL_YN();
		this.maleKg = qcnData.getMALE_KG();
		this.femaleKg = qcnData.getFEMALE_KG();
		this.divisionPrice1 = qcnData.getDIVISION_PRICE1();
		this.divisionPrice2 = qcnData.getDIVISION_PRICE2();
		this.divisionPrice3 = qcnData.getDIVISION_PRICE3();
		this.rgSqNo = qcnData.getRG_SQNO();
		this.selStsDsc = qcnData.getSEL_STS_DSC();

//		this.ttScr = qcnData.getTT_SCR();
//		this.fsrgDtm = qcnDatagetFSRG_DTM;
//		this.fsgmnEno = qcnData..getFSRGMN_ENO();
//		this.lschgDtm = qcnData.getLSCHG_DTM();
//		this.lsCmeno = qcnData.getLS_CMENO();
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

	public String getSgnoPrcDsc() {
		return sgnoPrcDsc;
	}

	public void setSgnoPrcDsc(String sgnoPrcDsc) {
		this.sgnoPrcDsc = sgnoPrcDsc;
	}

	public String getDdlYn() {
		return ddlYn;
	}

	public void setDdlYn(String ddlYn) {
		this.ddlYn = ddlYn;
	}

	public String getTmsYn() {
		return tmsYn;
	}

	public void setTmsYn(String tmsYn) {
		this.tmsYn = tmsYn;
	}

	public String getDelYn() {
		return delYn;
	}

	public void setDelYn(String delYn) {
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

	public int getRgSqNo() {
		return rgSqNo;
	}

	public void setRgSqNo(int rgSqNo) {
		this.rgSqNo = rgSqNo;
	}
	public String getSelStsDsc() {
		return selStsDsc;
	}

	public void setSelStsDsc(String selStsDsc) {
		this.selStsDsc = selStsDsc;
	}
	public int getDivisionPrice1() {
		return divisionPrice1;
	}

	public void setDivisionPrice1(int divisionPrice1) {
		this.divisionPrice1 = divisionPrice1;
	}

	public int getDivisionPrice2() {
		return divisionPrice2;
	}

	public void setDivisionPrice2(int divisionPrice2) {
		this.divisionPrice2 = divisionPrice2;
	}

	public int getDivisionPrice3() {
		return divisionPrice3;
	}

	public void setDivisionPrice3(int divisionPrice3) {
		this.divisionPrice3 = divisionPrice3;
	}

	@Override
	public String toString() {
		return "AuctionRound [naBzplc=" + naBzplc + ", aucObjDsc=" + aucObjDsc + ", aucDt=" + aucDt + ", qcn=" + qcn
				+ ", baseLmtAm=" + baseLmtAm + ", cutAm=" + cutAm + ", sgnoPrcDsc=" + sgnoPrcDsc + ", ddlYn=" + ddlYn
				+ ", tmsYn=" + tmsYn + ", delYn=" + delYn + ", maleKg=" + maleKg + ", femaleKg=" + femaleKg + ", ttScr="
				+ ttScr + ", fsrgDtm=" + fsrgDtm + ", fsgmnEno=" + fsgmnEno + ", lschgDtm=" + lschgDtm + ", lsCmeno="
				+ lsCmeno + ", rgSqNo=" + rgSqNo +"]";
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
