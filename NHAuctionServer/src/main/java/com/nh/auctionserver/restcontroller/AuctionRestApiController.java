package com.nh.auctionserver.restcontroller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nh.auctionserver.socketio.SocketIOServer;
import com.nh.auctionserver.util.ApiResponseMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class AuctionRestApiController {
	@Autowired
	private SocketIOServer mSocketIOServer;
	
	/**
	 * 계류대번호 변경 요청
	 * 
	 * @param request
	 * @param param
	 * @return
	 */
	@PostMapping(value = "/changeStandPosion")
	public ResponseEntity<Map<String, Object>> changeStandPosion(HttpServletRequest request,
			@RequestBody Map<String, String> param) {
		boolean result = false;
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String auctionHouseCode = null;
		String entryNum = null;
		String standPosionNum = null;
		
		if ((param.get("auctionHouseCode") != null && !param.get("auctionHouseCode").equals("") && !param.get("auctionHouseCode").isEmpty())
				&& (param.get("entryNum") != null && !param.get("entryNum").equals("") && !param.get("entryNum").isEmpty())
				&& (param.get("standPosionNum") != null && !param.get("standPosionNum").equals("")
						&& !param.get("standPosionNum").isEmpty())) {
			auctionHouseCode = param.get("auctionHouseCode");
			entryNum = param.get("entryNum");
			standPosionNum = param.get("standPosionNum");
			
			result = mSocketIOServer.getSocketIOHandler().getAuctioneer().changeStandPosion(auctionHouseCode, entryNum, standPosionNum);
			
			if(result) {
				return new ApiResponseMap(ApiResponseMap.REST_API_STATUS_SUCCESS, "정상적으로 요청 출품 건에 대하여 계류대번호를 변경처리하였습니다.", resultDataMap)
						.getResponseEntity();
			} else {
				return new ApiResponseMap(ApiResponseMap.REST_API_STATUS_SUCCESS, "출품 정보가 존재하지 않아 계류대번호를 변경할 수 없습니다.", resultDataMap)
						.getResponseEntity();
			}
		} else {
			return new ApiResponseMap(ApiResponseMap.REST_API_STATUS_FAIL, ApiResponseMap.RESET_API_MESSAGE_RQUIRE_FAIL,
					resultDataMap).getResponseEntity();
		}
	}
}
