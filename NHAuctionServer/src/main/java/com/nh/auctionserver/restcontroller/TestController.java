package com.nh.auctionserver.restcontroller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class TestController {
	@GetMapping("/getTest")
	public ResponseEntity<Map<String, Object>> getEventList(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<>();
		boolean validTokenResult = false;

		resultMap.put("Test", "200");
		
		return ResponseEntity.status(HttpStatus.OK).body(resultMap);
	}
}
