package com.nh.share.api.request.body;

import java.util.HashMap;

/**
 * 로그인 Body
 * @author jhlee
 *
 */
public class RequestLoginBody extends HashMap<String, String> {

	public RequestLoginBody(String userId , String pw) {
		this.put("usrid", userId);	//아이디
		this.put("pw", pw);			//비밀번호
	}
}
