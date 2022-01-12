package com.nh.scheduler.job;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.nh.scheduler.model.AuctionSchedulerPortModel;
import com.nh.scheduler.setting.AuctionSchedulerSetting;
import com.nh.share.api.ActionResultListener;
import com.nh.share.api.ActionRuler;
import com.nh.share.api.models.QcnData;
import com.nh.share.api.request.ActionRequestSelectQcn;
import com.nh.share.api.request.body.RequestQcnBody;
import com.nh.share.api.response.ResponseQcn;

/**
 * 
 * @ClassName SchedulerJob.java
 * @Description 메인 서버에서 동작하는 스케줄러
 * @anthor ishift
 * @since 2021.11.10
 */
public class SchedulerJob implements Job {

	private static final Logger logger = LogManager.getLogger(SchedulerJob.class);

	private static List<QcnData> mAuctionQcnResult; // 생성해야 하는 경매 목록.
	private static int mAuctionResultIndex; // 경매 목록에서 가져올 index.

	private static int mAuctionPortCheck; // 사용할 포트
	private static AuctionSchedulerPortModel mAuctionPort;

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("SchedulerJob Start [" + new Date(System.currentTimeMillis()) + "]");

		mAuctionQcnResult = new ArrayList<QcnData>();

		// 경매 생성 정보 조회 요청 API
		Date from = new Date();
		SimpleDateFormat transFormat = new SimpleDateFormat("yyyyMMdd");
		String auctionDateTime = transFormat.format(from);
		RequestQcnBody qcnBody = new RequestQcnBody("", "", "", "", "");
		
		ActionRuler.getInstance()
				.addAction(new ActionRequestSelectQcn(qcnBody, mActionRequestSelectQcnListener));
		ActionRuler.getInstance().runNext();
	}

	/**
	 * 
	 * @MethodName startPortNumbering
	 * @Description Port 할당 로직 시작. 사용 포트 영역의 첫번째 포트부터 확인한다.
	 *
	 */
	public static void startPortNumbering() {
		logger.info("== Port Numbering Start ==");

		mAuctionResultIndex = 0; // 첫번째 경매 정보부터 포트 할당 시작.
		mAuctionPortCheck = AuctionSchedulerSetting.SERVER_PORTNUM_START; // 사용할 포트 대역의 첫번째 포트

		if (mAuctionQcnResult != null && mAuctionQcnResult.size() > 0) {
			logger.debug("MainServer >> [Number of auctions to create : " + mAuctionQcnResult.size() + "]");

			QcnData auctionQcnInfo = mAuctionQcnResult.get(mAuctionResultIndex);

			AuctionSchedulerPortModel auctionPort = new AuctionSchedulerPortModel();
			auctionPort.setAuctionBizPlaceCode(auctionQcnInfo.getNA_BZPLC());
			auctionPort.setAuctionQcn(auctionQcnInfo.getQCN());
			auctionPort.setAuctionDate(auctionQcnInfo.getAUC_DT());
			auctionPort.setAuctionPort(Integer.toString(mAuctionPortCheck));
			auctionPort.setServerPortAvailable(false);
			auctionPort.setAPIUpdateResult("");

			// 메인 서버에서 사용 가능한 포트인지 확인.
			boolean isAvailable = AuctionSchedulerSetting.serverFunction.isAvailablePort(mAuctionPortCheck);
			logger.debug("MainServer >> CHECKING PORT START [" + mAuctionPortCheck + "]");

			// 사용 가능
			if (isAvailable) {
				logger.debug("MainServer >> PORT [" + mAuctionPortCheck + "] Available");

				// 서버 구동
				requestUpdatePortInformationAPI(auctionPort);
			} else {
				// 사용 불가능
				logger.debug("MainServer >> PORT [" + mAuctionPortCheck + "] Not Available");
				nextPortChecking(auctionPort); // 다음 포트 확인.
			}
		} else {
			// 생성할 경매 데이터 없음.
			logger.debug("MainServer >> ![No Auction data to create]");
		}
	}

	/**
	 * 
	 * @MethodName nextPortChecking
	 * @Description 다음 포트번호 사용 가능 여부 확인.
	 * 
	 * @param auctionCode
	 * @param auctionType
	 */
	public static void nextPortChecking(AuctionSchedulerPortModel auctionPort) {
		logger.debug(">> ~ nextPortChecking ~ <<");

		mAuctionPortCheck++; // 포트 번호에 1을 더한다.

		if (mAuctionPortCheck > AuctionSchedulerSetting.SERVER_PORTNUM_END) {
			// 현재 사용 가능한 포트 없음
			logger.debug("MainServer >> ![SERVER][No ports are currently available]");
		} else {
			logger.debug("MainServer >> NEXT CHECKING PORT [" + mAuctionPortCheck + "]");
			boolean isAvailable = AuctionSchedulerSetting.serverFunction.isAvailablePort(mAuctionPortCheck);

			// 사용 가능
			if (isAvailable) {
				logger.debug("MainServer >> PORT [" + mAuctionPortCheck + "] Available");
				// 서버 구동
				requestUpdatePortInformationAPI(auctionPort);
			} else {
				// 사용 불가능
				logger.debug("MainServer >> PORT [" + mAuctionPortCheck + "] Not Available");
				nextPortChecking(auctionPort);
			}
		}
	}

	/**
	 * 
	 * @MethodName runServer
	 * @Description 사용 가능한 포트 정보로 경매 서버를 실행.
	 * 
	 * @param auctionPort
	 */
	public static void runServer(AuctionSchedulerPortModel auctionPort) {
		AuctionSchedulerSetting.serverFunction.runAuctionServer(auctionPort);
		nextAuction();
	}

	/**
	 * 
	 * @MethodName nextAuction
	 * @Description 경매 서버 실행 후 다음 경매 정보에 포트 할당.
	 * 
	 * @param nextPort
	 */
	public static void nextAuction() {
		logger.info("== Next Auction ==");
		mAuctionResultIndex++;
		if (mAuctionResultIndex < mAuctionQcnResult.size()) {

			mAuctionPortCheck = mAuctionPortCheck + 1;
			if (mAuctionPortCheck > AuctionSchedulerSetting.SERVER_PORTNUM_END) {
				mAuctionPortCheck = AuctionSchedulerSetting.SERVER_PORTNUM_START;
			}

			QcnData auctionQcnInfo = mAuctionQcnResult.get(mAuctionResultIndex);

			AuctionSchedulerPortModel auctionPort = new AuctionSchedulerPortModel();
			auctionPort.setAuctionBizPlaceCode(auctionQcnInfo.getNA_BZPLC());
			auctionPort.setAuctionQcn(auctionQcnInfo.getQCN());
			auctionPort.setAuctionDate(auctionQcnInfo.getAUC_DT());
			auctionPort.setAuctionPort(Integer.toString(mAuctionPortCheck));
			auctionPort.setServerPortAvailable(false);
			auctionPort.setAPIUpdateResult("");

			logger.info("MainServer >> CHECKING PORT [" + mAuctionPortCheck + "]");
			boolean isAvailable = AuctionSchedulerSetting.serverFunction.isAvailablePort(mAuctionPortCheck);

			// 사용 가능
			if (isAvailable) {
				logger.info("MainServer >> PORT [" + mAuctionPortCheck + "] Available");

				// 서버 구동
				requestUpdatePortInformationAPI(auctionPort);
			} else {
				// 사용 불가능
				logger.info("MainServer >> PORT [" + mAuctionPortCheck + "] Not Available");
				nextPortChecking(auctionPort);
			}
		} else {
			logger.info("MainServer >> !Finish AuctionServer Run");
		}
	}

	/**
	 * 
	 * @MethodName requestUpdatePortInformationAPI
	 * @Description requestUpdatePortInformation API 호출
	 * 
	 * @param auctionPort
	 */
	public static void requestUpdatePortInformationAPI(AuctionSchedulerPortModel auctionPort) {
		logger.info("== requestUpdatePortInformationAPI ==");
		mAuctionPort = auctionPort;
		// request API
		logger.info("AuctionCode : " + auctionPort.getAuctionBizPlaceCode());
		logger.info("AuctionRound : " + auctionPort.getAuctionQcn());
		logger.info("AuctionLaneCode : " + auctionPort.getAuctionDate());
		logger.info("AuctionLanePort : " + auctionPort.getAuctionPort());
//        ActionRuler.getInstance().addAction(new ActionRequestUpdateAuctionPortInformation(
//                auctionPort.getAuctionCode(),auctionPort.getAuctionRound(),auctionPort.getAuctionLaneCode(), auctionPort.getAuctionLanePort(), mUpdateAuctionPortInformation));
//        ActionRuler.getInstance().runNext();
		logger.info("=====================================");
	}

	/**
	 * API : getAuctionGenerateInformations
	 */
	private static ActionResultListener mActionRequestSelectQcnListener = new ActionResultListener<ResponseQcn>() {

		@Override
		public void onResponseResult(ResponseQcn result) {
			logger.info("[API : ResponseQcn] onResponseResult :: " + result.getSuccess());
			if (result != null && result.getData() != null) {

				if (result.getSuccess()) {

					QcnData qcnData = result.getData();

					if (qcnData != null) {
						logger.info("[경매 정보 조회 결과]=> " + qcnData.getAUC_DT() + " / " + qcnData.getNA_BZPLC() + " / "
								+ qcnData.getAUC_OBJ_DSC());

						startPortNumbering();
					} else {
						// 경매 회차 존재 하지 않음.
						logger.info("[경매 정보 조회 결과]=> 경매 정보 없음");
					}

				} else {
					// 경매 회차 존재 하지 않음.
					logger.info("[경매 정보 조회 실패]=> Response Fail");
				}

			} else {
				// 경매 회차 존재 하지 않음.
				logger.info("[경매 정보 조회 실패]=> Response result is null");
			}
		}

		@Override
		public void onResponseError(String message) {
			logger.info("[API : ResponseAuctionGenerateInformation] onResponseError :: " + message);
		}

	};

	/**
	 * API : getAuctionGenerateInformations
	 */
//    private static ActionResultListener mUpdateAuctionPortInformation = new ActionResultListener<ResponseUpdateAuctionPortInformation>() {
//
//        @Override
//        public void onResponseResult(ResponseUpdateAuctionPortInformation result) {
//            if (result.getResult() != null && result.getResult().size() > 0) {
//                String updateResult = result.getResult().get(0).getUpdateResult();
//                logger.debug("[API : ResponseUpdateAuctionPortInformation] onResponseResult :: " + updateResult);
//                if (updateResult != null && updateResult.equals(AuctionSchedulerSetting.API_SUCCESS)) {
//                    AuctionSchedulerPortModel putAuctionPort = mAuctionPort;
//                    putAuctionPort.setAPIUpdateResult(AuctionSchedulerSetting.API_SUCCESS);
//                    logger.debug("mHazelcastMainPortMap Put! mMapKey > "+mMapKey);
//                    mHazelcastMainPortMap.put(mMapKey, putAuctionPort);
//                    runServer(putAuctionPort);
//                } else {
//                    nextAuction();
//                }
//            } else {
//                logger.info("[API : ResponseUpdateAuctionPortInformation] onResponseResult :: No Data");
//                nextAuction();
//            }
//        }
//
//        @Override
//        public void onResponseError(String message) {
//            logger.info("[API : ResponseUpdateAuctionPortInformation] onResponseError :: " + message);
//            nextAuction();
//        }
//
//    };
}
