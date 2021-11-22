package com.nh.share.utils;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtCertTokenUtils {
	private static JwtCertTokenUtils instance;
	final private String NH_AUCTION_CERT_CREATE_KEY = "jwtsecretkey";
	final private String JWT_HEADER_TYPE = "typ";
	final private String JWT_HEADER_ALGORITHM = "alg";
	final private String JWT_CLAIM_USER_MEM_NUM = "userMemNum";
	final private String JWT_CLAIM_AUCTION_HOUSE_CODE = "auctionHouseCode";
	final private String JWT_CLAIM_DEVICE_UUID = "deviceUUID";

	public static JwtCertTokenUtils getInstance() {
		if (instance == null) {
			instance = new JwtCertTokenUtils();
		}

		return instance;
	}

	/**
	 * 인증 토큰 생성 처리
	 * 
	 * @param deviceUid
	 * @param auctionHouseCode
	 * @param userMemNum
	 * @param certExpireDate
	 * @return
	 * @throws Exception
	 */
	public String createCertToken(String deviceUUID, String auctionHouseCode, String userMemNum, String certExpireDate)
			throws Exception {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;
		Date certExpireTime = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date toDate = dateFormat.parse(certExpireDate);

		certExpireTime.setTime(certExpireTime.getTime() + (toDate.getTime() - certExpireTime.getTime()));

		byte[] secretByte = DatatypeConverter.parseBase64Binary(NH_AUCTION_CERT_CREATE_KEY);
		Key signingKey = new SecretKeySpec(secretByte, signatureAlgorithm.getJcaName());

		Map<String, Object> headerMap = new HashMap<String, Object>();

		headerMap.put(JWT_HEADER_TYPE, "JWT");
		headerMap.put(JWT_HEADER_ALGORITHM, "HS512");

		Map<String, Object> claimMap = new HashMap<String, Object>();

		claimMap.put(JWT_CLAIM_DEVICE_UUID, deviceUUID);
		claimMap.put(JWT_CLAIM_AUCTION_HOUSE_CODE, auctionHouseCode);
		claimMap.put(JWT_CLAIM_USER_MEM_NUM, userMemNum);

		JwtBuilder tokenBuilder = Jwts.builder().setHeader(headerMap).setClaims(claimMap).setExpiration(certExpireTime)
				.signWith(signatureAlgorithm, signingKey);

		return tokenBuilder.compact();
	}

	/**
	 * 전달 된 토큰 검증 (만료/위변조 시 관련 Exception 발생)
	 * 
	 * @param certToken
	 * @return
	 * @throws Exception
	 */
	public boolean validateCertToken(String deviceUUID, String certToken) throws Exception {
		try {
			Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(NH_AUCTION_CERT_CREATE_KEY))
					.parseClaimsJws(certToken).getBody();

			System.out.println("Expire Date Time :" + claims.getExpiration());
			System.out.println("deviceUUID :" + claims.get(JWT_CLAIM_DEVICE_UUID));
			System.out.println("auctionHouseCode :" + claims.get(JWT_CLAIM_AUCTION_HOUSE_CODE));
			System.out.println("userMemNum :" + claims.get(JWT_CLAIM_USER_MEM_NUM));

			if (!deviceUUID.equals(claims.get(JWT_CLAIM_DEVICE_UUID))) {
				return false;
			}

			return true;
		} catch (ExpiredJwtException exception) {
			System.out.println("Cert Token Expired!");
			return false;
		} catch (JwtException exception) {
			System.out.println("Cert Token Forged!");
			return false;
		}
	}

	/**
	 * 전달 된 토큰 검증 후 deviceUUID 반환 (만료/위변조 시 관련 Exception 발생)
	 * 
	 * @param authToken
	 * @return
	 * @throws Exception
	 */
	public String getDeviceUUID(String authToken) throws Exception {
		try {
			Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(NH_AUCTION_CERT_CREATE_KEY))
					.parseClaimsJws(authToken).getBody();

			System.out.println("deviceUUID :" + claims.get(JWT_CLAIM_DEVICE_UUID));

			return claims.get(JWT_CLAIM_DEVICE_UUID).toString();
		} catch (ExpiredJwtException exception) {
			System.out.println("Cert Token Expired!");
			return null;
		} catch (JwtException exception) {
			System.out.println("Cert Token Forged!");
			return null;
		}
	}

	/**
	 * 전달 된 토큰 검증 후 회원번호 반환 (만료/위변조 시 관련 Exception 발생)
	 * 
	 * @param certToken
	 * @return
	 * @throws Exception
	 */
	public String getUserMemNum(String certToken) throws Exception {
		try {
			Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(NH_AUCTION_CERT_CREATE_KEY))
					.parseClaimsJws(certToken).getBody();

			return claims.get(JWT_CLAIM_USER_MEM_NUM).toString();
		} catch (ExpiredJwtException exception) {
			System.out.println("Cert Token Expired!");
			return null;
		} catch (JwtException exception) {
			System.out.println("Cert Token Forged!");
			return null;
		}
	}
	
	/**
	 * 전달 된 토큰 검증 후 조합코드 반환 (만료/위변조 시 관련 Exception 발생)
	 * 
	 * @param certToken
	 * @return
	 * @throws Exception
	 */
	public String getAuctionHouseCode(String certToken) throws Exception {
		try {
			Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(NH_AUCTION_CERT_CREATE_KEY))
					.parseClaimsJws(certToken).getBody();

			System.out.println("auctionHouseCode :" + claims.get(JWT_CLAIM_AUCTION_HOUSE_CODE));

			return claims.get(JWT_CLAIM_AUCTION_HOUSE_CODE).toString();
		} catch (ExpiredJwtException exception) {
			System.out.println("Cert Token Expired!");
			return null;
		} catch (JwtException exception) {
			System.out.println("Cert Token Forged!");
			return null;
		}
	}
}
