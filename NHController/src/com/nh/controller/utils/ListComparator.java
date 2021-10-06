package com.nh.controller.utils;

import java.util.Comparator;

import com.nh.share.server.models.BidderConnectInfo;

/**
 * List 정렬
 * 
 * @author jhlee
 *
 */
public class ListComparator implements Comparator {

	@Override
	public int compare(Object o1, Object o2) {

		if (o1 instanceof BidderConnectInfo && o2 instanceof BidderConnectInfo) {

			int userNo1 = Integer.parseInt(((BidderConnectInfo) o1).getUserJoinNum());
			int userNo2 = Integer.parseInt(((BidderConnectInfo) o2).getUserJoinNum());

			if (userNo1 > userNo2) {
				return 1;
			} else if (userNo1 < userNo2) {
				return -1;
			} else {
				return 0;
			}
		}

		return 0;
	}

}
