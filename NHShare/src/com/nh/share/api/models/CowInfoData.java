package com.nh.share.api.models;

/**
 * 
 * 출장우 정보
 * 
 * @author jhlee
 *
 */
public class CowInfoData {

	private String SRA_SRS_DSC;            	               	
	private String LVST_AUC_PTC_MN_NO;     
	private String INDV_SEX_C;             
	private String FTSNM;                 	
	private String FHS_ID_NO;              
	private String PPGCOW_FEE_DSC;         
	private String RG_DSC;                 
	private String LS_CMENO;               
	private String ATDR_DTM;             
	private String MCOW_DSC;               
	private String SRA_INDV_BRDSRA_RG_NO;  
	private String IS_EXCESS_COW;          
	private String SRA_INDV_AMNNO;         
	private String NA_BZPLC;               
	private String BIRTH;
	private String LSCHG_DTM;                   
	private String MCOW_SRA_INDV_AMNNO;         
	private String SRA_PD_RGNNM;           
	private String SRA_PD_RGNNM_FMT;
	private String RMK_CNTN;                          
	private String INDV_SEX_C_NAME;        
	private String BRANDNM;                
	private String INDV_ID_NO;            
	private String AUC_OBJ_DSC;               
	private String SEL_STS_DSC;            
	private String DNA_YN;                            
	private String KPN_NO;                 
	private String MACO_YN;                          
	private String ANW_YN;                 
	private String SIMP_C;                 
	private String AUC_DT;                 
	private String TRMN_AMNNO;             
	private String TRPCS_PY_YN;            
	private int MATIME;  
	private int FIR_LOWS_SBID_LMT_AM;  
	private int STAND_POSITION;       
	private int SRA_SBID_UPR;      
	private int LED_SQNO;                 	
	private int SRA_SBID_AM;     
	private int PRNY_MTCN;    
	private int SRA_INDV_PASG_QCN;  
	private int AUC_PRG_SQ;         
	private int COW_SOG_WT;                
	private int LWPR_CHG_NT;
	private int FARM_AMNNO; 
	private int LOWS_SBID_LMT_AM;
	private int OSLP_NO;      
	private int QCN;
	
	private String BIRTH_FMT;
	private int EXP_ATDR_AM;
	private int EXP_LVST_AUC_PTC_MN_NO;
	private String AUC_YN;	
	
	private String GAP_MONTH;	// 월령 추가(2022.09.07 by pjs
	private String RG_DSC_NM;	// 송아지혈통명(2023.03.15 by kih)
	private String SRA_MWMNNM;	// 낙찰자명(2023.03.15 by kih)
	private String DIVISION_PRICE_UNIT;	// 경매응찰단위 (2024.04.04)
	
	public CowInfoData() {
		/* 2023.03.17 주석처리  -> EntryInfo에서 처리 
		RG_DSC_NM = "";		// 초기화 - 'null' 방지
		SRA_MWMNNM = "";	// 초기화 - 'null' 방지
		*/
	}
	
	public String getSRA_SRS_DSC() {
		return SRA_SRS_DSC;
	}
	public void setSRA_SRS_DSC(String sRA_SRS_DSC) {
		SRA_SRS_DSC = sRA_SRS_DSC;
	}
	public int getMATIME() {
		return MATIME;
	}
	public void setMATIME(int mATIME) {
		MATIME = mATIME;
	}
	public String getLVST_AUC_PTC_MN_NO() {
		return LVST_AUC_PTC_MN_NO;
	}
	public void setLVST_AUC_PTC_MN_NO(String lVST_AUC_PTC_MN_NO) {
		LVST_AUC_PTC_MN_NO = lVST_AUC_PTC_MN_NO;
	}
	public String getINDV_SEX_C() {
		return INDV_SEX_C;
	}
	public void setINDV_SEX_C(String iNDV_SEX_C) {
		INDV_SEX_C = iNDV_SEX_C;
	}
	public String getFTSNM() {
		return FTSNM;
	}
	public void setFTSNM(String fTSNM) {
		FTSNM = fTSNM;
	}
	public String getFHS_ID_NO() {
		return FHS_ID_NO;
	}
	public void setFHS_ID_NO(String fHS_ID_NO) {
		FHS_ID_NO = fHS_ID_NO;
	}
	public String getPPGCOW_FEE_DSC() {
		return PPGCOW_FEE_DSC;
	}
	public void setPPGCOW_FEE_DSC(String pPGCOW_FEE_DSC) {
		PPGCOW_FEE_DSC = pPGCOW_FEE_DSC;
	}
	public String getRG_DSC() {
		return RG_DSC;
	}
	public void setRG_DSC(String rG_DSC) {
		RG_DSC = rG_DSC;
	}
	public String getLS_CMENO() {
		return LS_CMENO;
	}
	public void setLS_CMENO(String lS_CMENO) {
		LS_CMENO = lS_CMENO;
	}
	public String getATDR_DTM() {
		return ATDR_DTM;
	}
	public void setATDR_DTM(String aTDR_DTM) {
		ATDR_DTM = aTDR_DTM;
	}
	public int getFIR_LOWS_SBID_LMT_AM() {
		return FIR_LOWS_SBID_LMT_AM;
	}
	public void setFIR_LOWS_SBID_LMT_AM(int fIR_LOWS_SBID_LMT_AM) {
		FIR_LOWS_SBID_LMT_AM = fIR_LOWS_SBID_LMT_AM;
	}
	public String getMCOW_DSC() {
		return MCOW_DSC;
	}
	public void setMCOW_DSC(String mCOW_DSC) {
		MCOW_DSC = mCOW_DSC;
	}
	public String getSRA_INDV_BRDSRA_RG_NO() {
		return SRA_INDV_BRDSRA_RG_NO;
	}
	public void setSRA_INDV_BRDSRA_RG_NO(String sRA_INDV_BRDSRA_RG_NO) {
		SRA_INDV_BRDSRA_RG_NO = sRA_INDV_BRDSRA_RG_NO;
	}
	public String getIS_EXCESS_COW() {
		return IS_EXCESS_COW;
	}
	public void setIS_EXCESS_COW(String iS_EXCESS_COW) {
		IS_EXCESS_COW = iS_EXCESS_COW;
	}
	public String getSRA_INDV_AMNNO() {
		return SRA_INDV_AMNNO;
	}
	public void setSRA_INDV_AMNNO(String sRA_INDV_AMNNO) {
		SRA_INDV_AMNNO = sRA_INDV_AMNNO;
	}
	public String getNA_BZPLC() {
		return NA_BZPLC;
	}
	public void setNA_BZPLC(String nA_BZPLC) {
		NA_BZPLC = nA_BZPLC;
	}
	public String getBIRTH() {
		return BIRTH;
	}
	public void setBIRTH(String bIRTH) {
		BIRTH = bIRTH;
	}
	public String getLSCHG_DTM() {
		return LSCHG_DTM;
	}
	public void setLSCHG_DTM(String lSCHG_DTM) {
		LSCHG_DTM = lSCHG_DTM;
	}
	public int getSTAND_POSITION() {
		return STAND_POSITION;
	}
	public void setSTAND_POSITION(int sTAND_POSITION) {
		STAND_POSITION = sTAND_POSITION;
	}
	public String getMCOW_SRA_INDV_AMNNO() {
		return MCOW_SRA_INDV_AMNNO;
	}
	public void setMCOW_SRA_INDV_AMNNO(String mCOW_SRA_INDV_AMNNO) {
		MCOW_SRA_INDV_AMNNO = mCOW_SRA_INDV_AMNNO;
	}
	public int getSRA_SBID_UPR() {
		return SRA_SBID_UPR;
	}
	public void setSRA_SBID_UPR(int sRA_SBID_UPR) {
		SRA_SBID_UPR = sRA_SBID_UPR;
	}
	public String getSRA_PD_RGNNM() {
		return SRA_PD_RGNNM;
	}
	public void setSRA_PD_RGNNM(String sRA_PD_RGNNM) {
		SRA_PD_RGNNM = sRA_PD_RGNNM;
	}
	public String getSRA_PD_RGNNM_FMT() {
		return SRA_PD_RGNNM_FMT;
	}
	public void setSRA_PD_RGNNM_FMT(String sRA_PD_RGNNM_FMT) {
		SRA_PD_RGNNM_FMT = sRA_PD_RGNNM_FMT;
	}
	public int getLED_SQNO() {
		return LED_SQNO;
	}
	public void setLED_SQNO(int lED_SQNO) {
		LED_SQNO = lED_SQNO;
	}
	public int getSRA_SBID_AM() {
		return SRA_SBID_AM;
	}
	public void setSRA_SBID_AM(int sRA_SBID_AM) {
		SRA_SBID_AM = sRA_SBID_AM;
	}
	public String getRMK_CNTN() {
		return RMK_CNTN;
	}
	public void setRMK_CNTN(String rMK_CNTN) {
		RMK_CNTN = rMK_CNTN;
	}
	public int getPRNY_MTCN() {
		return PRNY_MTCN;
	}
	public void setPRNY_MTCN(int pRNY_MTCN) {
		PRNY_MTCN = pRNY_MTCN;
	}
	public String getINDV_SEX_C_NAME() {
		return INDV_SEX_C_NAME;
	}
	public void setINDV_SEX_C_NAME(String iNDV_SEX_C_NAME) {
		INDV_SEX_C_NAME = iNDV_SEX_C_NAME;
	}
	public String getBRANDNM() {
		return BRANDNM;
	}
	public void setBRANDNM(String bRANDNM) {
		BRANDNM = bRANDNM;
	}
	public String getINDV_ID_NO() {
		return INDV_ID_NO;
	}
	public void setINDV_ID_NO(String iNDV_ID_NO) {
		INDV_ID_NO = iNDV_ID_NO;
	}
	public int getSRA_INDV_PASG_QCN() {
		return SRA_INDV_PASG_QCN;
	}
	public void setSRA_INDV_PASG_QCN(int sRA_INDV_PASG_QCN) {
		SRA_INDV_PASG_QCN = sRA_INDV_PASG_QCN;
	}
	public String getAUC_OBJ_DSC() {
		return AUC_OBJ_DSC;
	}
	public void setAUC_OBJ_DSC(String aUC_OBJ_DSC) {
		AUC_OBJ_DSC = aUC_OBJ_DSC;
	}
	public int getAUC_PRG_SQ() {
		return AUC_PRG_SQ;
	}
	public void setAUC_PRG_SQ(int aUC_PRG_SQ) {
		AUC_PRG_SQ = aUC_PRG_SQ;
	}
	public String getSEL_STS_DSC() {
		return SEL_STS_DSC;
	}
	public void setSEL_STS_DSC(String sEL_STS_DSC) {
		SEL_STS_DSC = sEL_STS_DSC;
	}
	public String getDNA_YN() {
		return DNA_YN;
	}
	public void setDNA_YN(String dNA_YN) {
		DNA_YN = dNA_YN;
	}
	public int getCOW_SOG_WT() {
		return COW_SOG_WT;
	}
	public void setCOW_SOG_WT(int cOW_SOG_WT) {
		COW_SOG_WT = cOW_SOG_WT;
	}
	public int getLWPR_CHG_NT() {
		return LWPR_CHG_NT;
	}
	public void setLWPR_CHG_NT(int lWPR_CHG_NT) {
		LWPR_CHG_NT = lWPR_CHG_NT;
	}
	public String getKPN_NO() {
		return KPN_NO;
	}
	public void setKPN_NO(String kPN_NO) {
		KPN_NO = kPN_NO;
	}
	public int getFARM_AMNNO() {
		return FARM_AMNNO;
	}
	public void setFARM_AMNNO(int fARM_AMNNO) {
		FARM_AMNNO = fARM_AMNNO;
	}
	public String getMACO_YN() {
		return MACO_YN;
	}
	public void setMACO_YN(String mACO_YN) {
		MACO_YN = mACO_YN;
	}
	public int getLOWS_SBID_LMT_AM() {
		return LOWS_SBID_LMT_AM;
	}
	public void setLOWS_SBID_LMT_AM(int lOWS_SBID_LMT_AM) {
		LOWS_SBID_LMT_AM = lOWS_SBID_LMT_AM;
	}
	public String getANW_YN() {
		return ANW_YN;
	}
	public void setANW_YN(String aNW_YN) {
		ANW_YN = aNW_YN;
	}
	public String getSIMP_C() {
		return SIMP_C;
	}
	public void setSIMP_C(String sIMP_C) {
		SIMP_C = sIMP_C;
	}
	public String getAUC_DT() {
		return AUC_DT;
	}
	public void setAUC_DT(String aUC_DT) {
		AUC_DT = aUC_DT;
	}
	public String getTRMN_AMNNO() {
		return TRMN_AMNNO;
	}
	public void setTRMN_AMNNO(String tRMN_AMNNO) {
		TRMN_AMNNO = tRMN_AMNNO;
	}
	public int getOSLP_NO() {
		return OSLP_NO;
	}
	public void setOSLP_NO(int oSLP_NO) {
		OSLP_NO = oSLP_NO;
	}
	public String getTRPCS_PY_YN() {
		return TRPCS_PY_YN;
	}
	public void setTRPCS_PY_YN(String tRPCS_PY_YN) {
		TRPCS_PY_YN = tRPCS_PY_YN;
	}
	public int getQCN() {
		return QCN;
	}
	public void setQCN(int qCN) {
		QCN = qCN;
	}
	
	public String getBIRTH_FMT() {
		return BIRTH_FMT;
	}
	public void setBIRTH_FMT(String bIRTH_FMT) {
		BIRTH_FMT = bIRTH_FMT;
	}
	public int getEXP_ATDR_AM() {
		return EXP_ATDR_AM;
	}
	public void setEXP_ATDR_AM(int eXP_ATDR_AM) {
		EXP_ATDR_AM = eXP_ATDR_AM;
	}
	public int getEXP_LVST_AUC_PTC_MN_NO() {
		return EXP_LVST_AUC_PTC_MN_NO;
	}
	public void setEXP_LVST_AUC_PTC_MN_NO(int eXP_LVST_AUC_PTC_MN_NO) {
		EXP_LVST_AUC_PTC_MN_NO = eXP_LVST_AUC_PTC_MN_NO;
	}
	public String getAUC_YN() {
		return AUC_YN;
	}
	public void setAUC_YN(String aUC_YN) {
		AUC_YN = aUC_YN;
	}
	public String getGAP_MONTH() {
		return GAP_MONTH;
	}
	public void setGAP_MONTH(String gAP_MONTH) {
		GAP_MONTH = gAP_MONTH;
	}    
	public String getRG_DSC_NM() {
		return RG_DSC_NM;
	}
	public void setRG_DSC_NM(String rG_DSC_NM) {
		RG_DSC_NM = rG_DSC_NM;
	}
	public String getSRA_MWMNNM() {
		return SRA_MWMNNM;
	}
	public void setSRA_MWMNNM(String sRA_MWMNNM) {
		SRA_MWMNNM = sRA_MWMNNM;
	}

	public String getDIVISION_PRICE_UNIT() {
		return DIVISION_PRICE_UNIT;
	}

	public void setDIVISION_PRICE_UNIT(String dIVISION_PRICE_UNIT) {
		DIVISION_PRICE_UNIT = dIVISION_PRICE_UNIT;
	}
	
}