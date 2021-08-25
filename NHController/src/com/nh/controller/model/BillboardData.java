package com.nh.controller.model;

import com.nh.controller.utils.GlobalDefine;
import com.nh.controller.utils.SharedPreference;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.interfaces.NettySendable;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;

public class BillboardData implements NettySendable {

    // 혈통
    private enum BBLineage {
        BASE("01", "기초"),
        LINEAGE("02", "혈통"),
        HIGH("03", "고등"),
        UNREGISTER("09", "미등록우");

        private final String code;
        private final String description;

        BBLineage(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public static String which(String code) {
            return Arrays.stream(BBLineage.values())
                    .filter(lineage -> (lineage.code.equals(code)))
                    .map(lineage -> lineage.description)
                    .findAny()
                    .orElse(BASE.description);
        }
    }

    //성별
    private enum BBGender {
        NONE("0", "없음"),
        FEMALE("1", "암"),
        MALE("2", "수"),
        CAST("3", "거세"),
        VIR("4", "미경산"),
        NONCAST("5", "비거세"),
        FREE("6", "프리마틴"),
        COMMON("9", "공통");

        private final String description;
        private final String code;

        BBGender(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public static String which(String code) {
            return Arrays.stream(BBGender.values())
                    .filter(gender -> (gender.code.equals(code)))
                    .map(gender -> gender.description)
                    .findAny()
                    .orElse(NONE.description);
        }
    }

    private String bEntryNum; // 출품번호(경매번호)
    private String bExhibitor; // 출하주
    private String bWeight; // 우출하중량
    private String bGender; // 성별
    private String bMotherTypeCode; // 혈통
    private String bPasgQcn; // 계대
    private String bMatime; // 산차
    private String bKpn; // KPN
    private String bRegion; // 농가명(지역명)
    private String bNote; // 비고내용
    private String bLowPrice; // 최저가
    private String bAuctionBidPrice; // 낙찰가
    private String bAuctionSucBidder; // 낙찰자
    private String bAuctionSucBidderName; // 낙찰자이름
    private String bDnaYn; // 친자검사결과여부

    public BillboardData() {
    }

    public BillboardData(
            String bEntryNum, String bExhibitor, String bWeight, String bGender, String bMotherTypeCode,
            String bPasgQcn, String bMatime, String bKpn, String bRegion, String bNote, String bLowPrice,
            String bAuctionBidPrice, String bAuctionSucBidder, String bAuctionSucBidderName, String bDnaYn
    ) {
        this.bEntryNum = bEntryNum;
        this.bExhibitor = bExhibitor;
        this.bWeight = bWeight;
        this.bGender = bGender;
        this.bMotherTypeCode = bMotherTypeCode;
        this.bPasgQcn = bPasgQcn;
        this.bMatime = bMatime;
        this.bKpn = bKpn;
        this.bRegion = bRegion;
        this.bNote = bNote;
        this.bLowPrice = bLowPrice;
        this.bAuctionBidPrice = bAuctionBidPrice;
        this.bAuctionSucBidder = bAuctionSucBidder;
        this.bAuctionSucBidderName = bAuctionSucBidderName;
        this.bDnaYn = bDnaYn;
    }

    public String getbEntryNum() {
        return bEntryNum;
    }

    public void setbEntryNum(String bEntryNum) {
        this.bEntryNum = bEntryNum;
    }

    public String getbExhibitor() {
        return bExhibitor;
    }

    public void setbExhibitor(String bExhibitor) {
        this.bExhibitor = bExhibitor;
    }

    public String getbWeight() {
        return bWeight;
    }

    public void setbWeight(String bWeight) {
        this.bWeight = bWeight;
    }

    public String getbGender() {
        return bGender;
    }

    public void setbGender(String bGender) {
        this.bGender = bGender;
    }

    public String getbMotherTypeCode() {
        return bMotherTypeCode;
    }

    public void setbMotherTypeCode(String bMotherTypeCode) {
        this.bMotherTypeCode = bMotherTypeCode;
    }

    public String getbPasgQcn() {
        return bPasgQcn;
    }

    public void setbPasgQcn(String bPasgQcn) {
        this.bPasgQcn = bPasgQcn;
    }

    public String getbMatime() {
        return bMatime;
    }

    public void setbMatime(String bMatime) {
        this.bMatime = bMatime;
    }

    public String getbKpn() {
        return bKpn;
    }

    public void setbKpn(String bKpn) {
        this.bKpn = bKpn;
    }

    public String getbRegion() {
        return bRegion;
    }

    public void setbRegion(String bRegion) {
        this.bRegion = bRegion;
    }

    public String getbNote() {
        return bNote;
    }

    public void setbNote(String bNote) {
        this.bNote = bNote;
    }

    public String getbLowPrice() {
        return bLowPrice;
    }

    public void setbLowPrice(String bLowPrice) {
        this.bLowPrice = bLowPrice;
    }

    public String getbAuctionBidPrice() {
        return Objects.requireNonNullElse(bAuctionBidPrice, "");
    }

    public void setbAuctionBidPrice(String bAuctionBidPrice) {
        this.bAuctionBidPrice = bAuctionBidPrice;
    }

    public String getbAuctionSucBidder() {
        return Objects.requireNonNullElse(bAuctionSucBidder, "");
    }

    public void setbAuctionSucBidder(String bAuctionSucBidder) {
        this.bAuctionSucBidder = bAuctionSucBidder;
    }

    public String getbAuctionSucBidderName() {
        return bAuctionSucBidderName;
    }

    public void setbAuctionSucBidderName(String bAuctionSucBidderName) {
        this.bAuctionSucBidderName = bAuctionSucBidderName;
    }

    public String getbDnaYn() {
        return bDnaYn;
    }

    public void setbDnaYn(String bDnaYn) {
        this.bDnaYn = bDnaYn;
    }

    private String getKPN() {
        if (!getbKpn().isEmpty() || !getbKpn().isBlank()) {
            return getbKpn().substring(3);
        }
        return "";
    }

    private String makeBoardMessages() {
        SharedPreference shared = SharedPreference.getInstance();
        StringBuffer sb = new StringBuffer();
//        sb.append("123456789123456789123456789123456789123456789123456    1");
        sb.append(makeBoardNumberMessage(getbEntryNum(), shared.getString(SharedPreference.PREFERENCE_SETTING_BOARD_ENTRYNUM, "")));
        sb.append(makeBoardKoreanMessage(getbExhibitor(), shared.getString(SharedPreference.PREFERENCE_SETTING_BOARD_EXHIBITOR, "")));
        sb.append(makeBoardNumberMessage(getbWeight(), shared.getString(SharedPreference.PREFERENCE_SETTING_BOARD_WEIGHT, "")));
        sb.append(makeBoardKoreanMessage(BBGender.which(getbGender()), shared.getString(SharedPreference.PREFERENCE_SETTING_BOARD_GENDER, "")));
        sb.append(makeBoardKoreanMessage(BBLineage.which(getbMotherTypeCode()), shared.getString(SharedPreference.PREFERENCE_SETTING_BOARD_MOTHER, "")));
        sb.append(makeBoardNumberMessage(getbPasgQcn(), shared.getString(SharedPreference.PREFERENCE_SETTING_BOARD_PASSAGE, "")));
        sb.append(makeBoardNumberMessage(getbMatime(), shared.getString(SharedPreference.PREFERENCE_SETTING_BOARD_MATIME, "")));
        sb.append(makeBoardNumberMessage(getKPN(), shared.getString(SharedPreference.PREFERENCE_SETTING_BOARD_KPN, "")));
        sb.append(makeBoardKoreanMessage(getbRegion(), shared.getString(SharedPreference.PREFERENCE_SETTING_BOARD_REGION, "")));
        sb.append(makeNoteMessage(getbNote(), shared.getString(SharedPreference.PREFERENCE_SETTING_BOARD_NOTE, "")));
        sb.append(makeBoardNumberMessage(getBaseUnitDivision(getbLowPrice()), shared.getString(SharedPreference.PREFERENCE_SETTING_BOARD_LOWPRICE, "0")));
        sb.append(makeBoardNumberMessage(getBaseUnitDivision(getbAuctionBidPrice()), shared.getString(SharedPreference.PREFERENCE_SETTING_BOARD_SUCPRICE, "")));
        sb.append(makeBoardKoreanMessage(getbAuctionSucBidder(), shared.getString(SharedPreference.PREFERENCE_SETTING_BOARD_SUCBIDDER, "")));
        sb.append("    ");
        sb.append(makeBoardNumberMessage(getbDnaYn(), shared.getString(SharedPreference.PREFERENCE_SETTING_BOARD_DNA, "")));
        return sb.toString();
    }

    /**
     * @Description 비고 메세지
     */
    private String makeNoteMessage(String s, String sharedPreference) {
        int count = Integer.parseInt(sharedPreference);
        int stringToByteSize = s.getBytes(Charset.forName(GlobalDefineCode.BILLBOARD_CHARSET)).length;
        int doubleCount = count / 2; // byte[]와 비교하기위한 length

        StringBuilder temp = new StringBuilder();

        if (s.length() < doubleCount) {
            return " ".repeat(count - stringToByteSize) + s;
        } else {
            if ((stringToByteSize < doubleCount)) {
                temp = new StringBuilder(" ".repeat(count - stringToByteSize) + s);
            } else {
                temp = new StringBuilder(s.substring(0, doubleCount));
            }
            for (int ch :
                    temp.toString().toCharArray()) {
                if ((33 <= ch) && (ch <= 126)) { // 특수문자, 알파벳, 숫자 등 => 1 byte 이기 때문에 빈 문자열 추가
                    temp.insert(0, " ");
                }
            }
            return temp.toString();
        }
    }

    /**
     * @Description 한글이 들어가는 전광판
     */
    private String makeBoardKoreanMessage(String s, String sharedPreference) {
        int count = Integer.parseInt(sharedPreference);
        int stringToByteSize = s.getBytes(Charset.forName(GlobalDefineCode.BILLBOARD_CHARSET)).length;
        int doubleCount = count / 2; // byte[]와 비교하기위한 length

        if (stringToByteSize < count) {
            return " ".repeat(count - stringToByteSize) + s;
        } else {
            return s.replaceAll(",", " ").substring(0, doubleCount);
        }
    }

    /**
     * @Description 숫자만 들어가는 전광판
     */
    private String makeBoardNumberMessage(String s, String sharedPreference) {
        int count = Integer.parseInt(sharedPreference);

        if (s.length() < count) {
            return " ".repeat(count - s.length()) + s;
        } else {
            return s.substring(0, count);
        }
    }

    private String getBaseUnitDivision(String str) {
        if (str.equals("")) {
            return "";
        }
        int price = Integer.parseInt(str);
        int resultPrice = price / GlobalDefine.AUCTION_INFO.MULTIPLICATION_BIDDER_PRICE_10000;
        return String.valueOf(resultPrice);
    }

    @Override
    public String getEncodedMessage() {
        return String.format("%c%c" + "%s" + "%c",
                GlobalDefine.BILLBOARD.STX, GlobalDefine.BILLBOARD.DATA_CODE,
                makeBoardMessages(), GlobalDefine.BILLBOARD.ETX);
    }
}
