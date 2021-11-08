package com.nh.controller.model;

import java.sql.Timestamp;

/**
 * 회원 정보 insert Dto
 * ConnectionInfo -> UserInfo -> insert into DB
 *
 * @author dhKim
 */
public class UserInfo {
    private String naBzplc; // 조합구분코드
    private String aucDt; // 경매일자
    private String aucObjDsc; // 경매대상구분코드
    private String lvstAucPtcMnNo; // 가축경매 참여자번호
    private int trmnAmnno; // 거래인관리번호
    private Timestamp fsrgDtm; // 최초등록일시
    private String fsrgmnEno; // 최초등록자 개인번호

    public UserInfo() {
    }

    public UserInfo(String naBzplc, String aucDt, String aucObjDsc, String lvstAucPtcMnNo, int trmnAmnno, Timestamp fsrgDtm, String fsrgmnEno) {
        this.naBzplc = naBzplc;
        this.aucDt = aucDt;
        this.aucObjDsc = aucObjDsc;
        this.lvstAucPtcMnNo = lvstAucPtcMnNo;
        this.trmnAmnno = trmnAmnno;
        this.fsrgDtm = fsrgDtm;
        this.fsrgmnEno = fsrgmnEno;
    }

    public String getNaBzplc() {
        return naBzplc;
    }

    public void setNaBzplc(String naBzplc) {
        this.naBzplc = naBzplc;
    }

    public String getAucDt() {
        return aucDt;
    }

    public void setAucDt(String aucDt) {
        this.aucDt = aucDt;
    }

    public String getAucObjDsc() {
        return aucObjDsc;
    }

    public void setAucObjDsc(String aucObjDsc) {
        this.aucObjDsc = aucObjDsc;
    }

    public String getLvstAucPtcMnNo() {
        return lvstAucPtcMnNo;
    }

    public void setLvstAucPtcMnNo(String lvstAucPtcMnNo) {
        this.lvstAucPtcMnNo = lvstAucPtcMnNo;
    }

    public int getTrmnAmnno() {
        return trmnAmnno;
    }

    public void setTrmnAmnno(int trmnAmnno) {
        this.trmnAmnno = trmnAmnno;
    }

    public Timestamp getFsrgDtm() {
        return fsrgDtm;
    }

    public void setFsrgDtm(Timestamp fsrgDtm) {
        this.fsrgDtm = fsrgDtm;
    }

    public String getFsrgmnEno() {
        return fsrgmnEno;
    }

    public void setFsrgmnEno(String fsrgmnEno) {
        this.fsrgmnEno = fsrgmnEno;
    }
}