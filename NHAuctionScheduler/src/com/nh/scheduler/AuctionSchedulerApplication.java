package com.nh.scheduler;

import static org.quartz.JobBuilder.newJob;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.nh.scheduler.job.SchedulerJob;
import com.nh.scheduler.setting.AuctionSchedulerSetting;

/**
 * 
 * @ClassName AuctionSchedulerApplication.java
 * @Description 스케줄러 메인.
 * @anthor ishift
 * @since 2021.11.10
 */
public class AuctionSchedulerApplication {

	private static final Logger logger = LogManager.getLogger(AuctionSchedulerApplication.class);

	private static String mRunMin;

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			runningError();
		} else if (args.length == 1) {
			if (args[0].toString().contains("version")) {
				versionInfo();
			} else {
				// java -jar Scheduler.jar [RunMin]
				runScheduler(args);
			}
		} else {
			runningError();
		}

	}

	private static void runScheduler(String[] args) {
		logger.info("********************* Scheduler Start *********************");

		mRunMin = AuctionSchedulerSetting.DEFAULT_RUN_TIME;

		if (args.length > 0) {
			try {
				mRunMin = args[0];
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 메인로직 실행 (스케줄러 구동)
		AuctionSchedulerApplication application = new AuctionSchedulerApplication();
		application.runMainScheduler(mRunMin);
	}

	private static void versionInfo() {
		System.out.print("\r\n" + "[NH Auction Scheduler Informations]" + "\r\n");
		System.out.print("Release Version : " + AuctionSchedulerSetting.RELEASE_VERSION_NAME + "\r\n");
		System.out.print("Release Date : " + AuctionSchedulerSetting.RELEASE_VERSION_DATE + "\r\n");
		System.out.print("Copyright (c) 2021 NH Co. Ltd." + "\r\n");
		System.out.print("All rights reserved." + "\r\n");
	}

	private static void runningError() {
		System.out.print("\r\n" + "Invalid Arguments! Please Check Arguments" + "\r\n");
	}

	/**
	 * 메인 서버 로직 실행.
	 * 
	 * 일정 시간마다 스케줄러(SchedulerJob.class)를 실행한다.
	 * 
	 * @param
	 */
	private void runMainScheduler(String schedulerRunMin) {
		logger.info("Running runMainScheduler - !");

		SchedulerFactory schedulerfactory = new StdSchedulerFactory();
		try {
			Scheduler scheduler = schedulerfactory.getScheduler();
			JobDetail job = newJob(SchedulerJob.class)
					.withIdentity(AuctionSchedulerSetting.JOBDETAIL_IDENTITY, Scheduler.DEFAULT_GROUP).build();

			String cronSchedule = "0 */" + mRunMin + " * * * ?";

			Trigger trigger = TriggerBuilder.newTrigger()
					.withIdentity(AuctionSchedulerSetting.JOBTRIGGER_IDENTITY, Scheduler.DEFAULT_GROUP)
					.withSchedule(CronScheduleBuilder.cronSchedule(cronSchedule)) // 매분 5초마다
					.build();
			scheduler.scheduleJob(job, trigger);
			scheduler.start(); // Scheduler 실행.
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
