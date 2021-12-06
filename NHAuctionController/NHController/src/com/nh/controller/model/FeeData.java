package com.nh.controller.model;

import com.nh.share.api.models.FeeBaseData;

/**
 * 수수료 기준 데이터
 * 
 * @author jhlee
 *
 */
public class FeeData {

	private String naBzplc; // 경제통합사업장코드
	private String aplDt; // 적용일자
	private String aucObjDsc; // 경매대상구분코드
	private String feeRgSqno; // -
	private String naFee_c; // 경제통합수수료코드
	private String sraFeenm; // 축산수수료명
	private String jnlzBsnDsc; // 분개업무구분코드
	private String sraNaFee_c; // 축산경제통합수수료코드
	private String feeAplObj_c; // 수수료적용대상코드
	private String amRtoDsc; // 금액비율구분코드
	private String sgnoPrcDsc; // 단수처리구분코드
	private int macoFeeUpr; // 조합원수수료단가
	private int nmacoFeeUpr; // 비조합원수수료단가
	private String ansDsc; // 가감구분코드
	private String sbidYn; // 낙찰여부
	private String ppgcowFeeDsc; // 번식우수수료구분코드
	private String delYn; // 삭제여부
	private String fsrgDtm;
	private String fsrgmnEno;
	private String lschgDtm;
	private String lsCmeno;
	
	public FeeData() {}

	/**
	 * API 받아온 데이터 파싱
	 * @param data
	 */
	public FeeData(FeeBaseData data) {
		naBzplc = data.getNA_BZPLC();
		aplDt = data.getAPL_DT();
		aucObjDsc = data.getAUC_OBJ_DSC();
		feeRgSqno = Integer.toString(data.getFEE_RG_SQNO());
		naFee_c = data.getNA_FEE_C();
		sraFeenm = data.getSRA_FEENM();
		jnlzBsnDsc = data.getJNLZ_BSN_DSC();
		sraNaFee_c = data.getSRA_NA_FEE_C();
		feeAplObj_c = data.getFEE_APL_OBJ_C();
		amRtoDsc = data.getAM_RTO_DSC();
		sgnoPrcDsc = data.getSGNO_PRC_DSC();
		macoFeeUpr = data.getMACO_FEE_UPR();
		nmacoFeeUpr = data.getNMACO_FEE_UPR();
		ansDsc = data.getANS_DSC();
		sbidYn = data.getSBID_YN();
		ppgcowFeeDsc = data.getPPGCOW_FEE_DSC();
		delYn = data.getDEL_YN();
		fsrgDtm = data.getFSRG_DTM();
		fsrgmnEno = data.getFSRGMN_ENO();
		lschgDtm = data.getLSCHG_DTM();
		lsCmeno = data.getLS_CMENO();
	}

	public String getNaBzplc() {
		return naBzplc;
	}

	public void setNaBzplc(String naBzplc) {
		this.naBzplc = naBzplc;
	}

	public String getAplDt() {
		return aplDt;
	}

	public void setAplDt(String aplDt) {
		this.aplDt = aplDt;
	}

	public String getAucObjDsc() {
		return aucObjDsc;
	}

	public void setAucObjDsc(String aucObjDsc) {
		this.aucObjDsc = aucObjDsc;
	}

	public String getFeeRgSqno() {
		return feeRgSqno;
	}

	public void setFeeRgSqno(String feeRgSqno) {
		this.feeRgSqno = feeRgSqno;
	}

	public String getNaFee_c() {
		return naFee_c;
	}

	public void setNaFee_c(String naFee_c) {
		this.naFee_c = naFee_c;
	}

	public String getSraFeenm() {
		return sraFeenm;
	}

	public void setSraFeenm(String sraFeenm) {
		this.sraFeenm = sraFeenm;
	}

	public String getJnlzBsnDsc() {
		return jnlzBsnDsc;
	}

	public void setJnlzBsnDsc(String jnlzBsnDsc) {
		this.jnlzBsnDsc = jnlzBsnDsc;
	}

	public String getSraNaFee_c() {
		return sraNaFee_c;
	}

	public void setSraNaFee_c(String sraNaFee_c) {
		this.sraNaFee_c = sraNaFee_c;
	}

	public String getFeeAplObj_c() {
		return feeAplObj_c;
	}

	public void setFeeAplObj_c(String feeAplObj_c) {
		this.feeAplObj_c = feeAplObj_c;
	}

	public String getAmRtoDsc() {
		return amRtoDsc;
	}

	public void setAmRtoDsc(String amRtoDsc) {
		this.amRtoDsc = amRtoDsc;
	}

	public String getSgnoPrcDsc() {
		return sgnoPrcDsc;
	}

	public void setSgnoPrcDsc(String sgnoPrcDsc) {
		this.sgnoPrcDsc = sgnoPrcDsc;
	}

	public int getMacoFeeUpr() {
		return macoFeeUpr;
	}

	public void setMacoFeeUpr(int macoFeeUpr) {
		this.macoFeeUpr = macoFeeUpr;
	}

	public int getNmacoFeeUpr() {
		return nmacoFeeUpr;
	}

	public void setNmacoFeeUpr(int nmacoFeeUpr) {
		this.nmacoFeeUpr = nmacoFeeUpr;
	}

	public String getAnsDsc() {
		return ansDsc;
	}

	public void setAnsDsc(String ansDsc) {
		this.ansDsc = ansDsc;
	}

	public String getSbidYn() {
		return sbidYn;
	}

	public void setSbidYn(String sbidYn) {
		this.sbidYn = sbidYn;
	}

	public String getPpgcowFeeDsc() {
		return ppgcowFeeDsc;
	}

	public void setPpgcowFeeDsc(String ppgcowFeeDsc) {
		this.ppgcowFeeDsc = ppgcowFeeDsc;
	}

	public String getDelYn() {
		return delYn;
	}

	public void setDelYn(String delYn) {
		this.delYn = delYn;
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

}