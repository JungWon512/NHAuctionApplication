package com.nh.controller.utils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.stream.IntStream;

public class AuctionUtil {

	private static AuctionUtil instance = null;

	public AuctionUtil() {
	}

	public static AuctionUtil getInstance() {
		if (instance == null) {
			instance = new AuctionUtil();
		}
		return instance;
	}

	/**
	 * 성별 값 가져옴.
	 * @param aucObjDsc
	 * @param genderCode
	 * @return
	 */
	public String getGenderName(int aucObjDsc,String genderCode) {
		
		if(!CommonUtils.getInstance().isValidString(genderCode)) {
			return "";
		}
		
		//송아지
		if(aucObjDsc == GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_1) {
			return CalfGender.which(genderCode);
		}else {
			//번식/비육우
			return FattenOrBreedingCattleGender.which(genderCode);
		}
	}
	
	//성별 - 송아지
	public enum CalfGender {	
		NONE("0", "없음"),
		FEMALE("1", "암송아지"),
		MALE("2", "숫송아지"),
		CAST("3", "거세우"),
		VIR("4", "미경산"),
		NONCAST("5", "비거세우"),
		FREE("6", "프리마틴"),
		COMMON("9", "공통");

		private final String description;
		private final String code;
			
		CalfGender(String code, String description) {
			this.code = code;
			this.description = description;
		}

		public static String which(String code) {
			return Arrays.stream(CalfGender.values())
				.filter(gender -> (gender.code.equals(code)))
				.map(gender -> gender.description)
				.findAny()
				.orElse(NONE.description);
		}
	}
	
	//성별 - 번식/비육우
	public enum FattenOrBreedingCattleGender {	
		NONE("0", "없음"),
		FEMALE("1", "암송아지"),
		MALE("2", "숫송아지"),
		CAST("3", "거세우"),
		VIR("4", "미경산"),
		NONCAST("5", "비거세우"),
		FREE("6", "프리마틴"),
		COMMON("9", "공통");

		private final String description;
		private final String code;
			
		FattenOrBreedingCattleGender(String code, String description) {
			this.code = code;
			this.description = description;
		}
			
		public static String which(String code) {
			return Arrays.stream(FattenOrBreedingCattleGender.values())
				.filter(gender -> (gender.code.equals(code)))
				.map(gender -> gender.description)
				.findAny()
				.orElse(NONE.description);
		}
	}
	
	 // 혈통
    public enum Lineage {
        BASE("01", "기초"),
        LINEAGE("02", "혈통"),
        HIGH("03", "고등"),
        UNREGISTER("09", "미등록우");

        private final String code;
        private final String description;

        Lineage(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public static String which(String code) {
            return Arrays.stream(Lineage.values())
                    .filter(lineage -> (lineage.code.equals(code)))
                    .map(lineage -> lineage.description)
                    .findAny()
                    .orElse(BASE.description);
        }
    }
    
    public String getEntryInfoMessage(LinkedHashMap<Boolean, String> entryInfoMap) {
    	return "";
    }
}
