package com.nh.share.api.request.body;

import java.util.HashMap;

/**
 *
 */
public class RequestBzloc extends HashMap<String, Object> {

	public RequestBzloc(String naBzplc) {
		this.put("naBzplc", naBzplc);	//아이디
	}
}
