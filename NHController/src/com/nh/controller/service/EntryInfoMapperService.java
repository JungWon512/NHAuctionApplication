package com.nh.controller.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nh.controller.dao.EntryInfoDao;
import com.nh.controller.database.DBSessionFactory;
import com.nh.controller.mapper.EntryInfoMapper;
import com.nh.controller.model.AucEntrData;
import com.nh.controller.model.AuctionRound;
import com.nh.controller.model.AuctionStnData;
import com.nh.controller.model.SelStsCountData;
import com.nh.controller.utils.GlobalDefine;
import com.nh.share.controller.models.EntryInfo;
import com.nh.share.controller.models.SendAuctionResult;

/**
 * 출품 데이터 MapperService
 *
 * @author dhKim
 */
public class EntryInfoMapperService extends BaseMapperService<EntryInfoDao> implements EntryInfoMapper {

	private static EntryInfoMapperService entryInfoMapperService = null;

	public static EntryInfoMapperService getInstance() {

		if (entryInfoMapperService == null) {
			entryInfoMapperService = new EntryInfoMapperService();
		}
		return entryInfoMapperService;
	}

	public EntryInfoMapperService() {
		this.setDao(new EntryInfoDao());
	}

	@Override
	public int getAllEntryDataCount(AuctionRound auctionRound) {

		int recordCount = 0;

		try (SqlSession session = DBSessionFactory.getSession()) {
			recordCount = getDao().selectAllEntryInfoCount(auctionRound, session);
		}

		return recordCount;
	}

	@Override
	public List<EntryInfo> getAllEntryData(AuctionRound auctionRound) {

		List<EntryInfo> list;
		try (SqlSession session = DBSessionFactory.getSession()) {
			list = getDao().selectAllEntryInfo(auctionRound, session);
		}

		if (!list.isEmpty()) {
			// 마지막 출품정보 표기 (Y/N)
			for (int i = 0; i < list.size(); i++) {
				String flag = (i == list.size() - 1) ? "Y" : "N";
				list.get(i).setIsLastEntry(flag);
			}
		}
		return list;
	}

	@Override
	public List<EntryInfo> getFinishedEntryData(AuctionRound auctionRound) {

		List<EntryInfo> list = null;

		try (SqlSession session = DBSessionFactory.getSession()) {

			auctionRound.setAuctionResultParam(GlobalDefine.ETC_INFO.AUCTION_SEARCH_PARAM_SP);

			list = getDao().selectAllEntryInfo(auctionRound, session);

			auctionRound.setAuctionResultParam(null);

		} catch (Exception e) {
			return new ArrayList<EntryInfo>();
		}

		return list;
	}

	@Override
	public int updateEntryPrice(EntryInfo entryInfo) {

		int resultValue = 0;

		try (SqlSession session = DBSessionFactory.getSession()) {

			resultValue = getDao().updateEntryPrice(entryInfo, session);

			if (resultValue > 0) {
				session.commit();
			} else {
				session.rollback();
			}

		} catch (Exception e) {
			// exception에 대한 처리 시 사용
			resultValue = -1;
		}

		return resultValue;
	}

	@Override
	public int updateEntryPriceList(List<EntryInfo> entryInfoList) {

		int resultValue = 0;

		try (SqlSession session = DBSessionFactory.getSession()) {

			resultValue = getDao().updateEntryPriceList(entryInfoList, session);

			if (resultValue > 0) {
				session.commit();
			} else {
				session.rollback();
			}

		} catch (Exception e) {
			// exception에 대한 처리 시 사용
			resultValue = -1;
		}

		return resultValue;
	}

	@Override
	public int updateEntryState(EntryInfo entryInfo) {

		int resultValue = 0;

		try (SqlSession session = DBSessionFactory.getSession()) {

			resultValue = getDao().updateEntryState(entryInfo, session);

			if (resultValue > 0) {
				session.commit();
			} else {
				session.rollback();
			}

		} catch (Exception e) {
			// exception에 대한 처리 시 사용
			resultValue = -1;
		}

		return resultValue;
	}

	@Override
	public int updateAuctionResult(SendAuctionResult auctionResult) {

		int resultValue = 0;

		try (SqlSession session = DBSessionFactory.getSession()) {

			resultValue = getDao().updateAuctionResult(auctionResult, session);

			if (resultValue > 0) {
				session.commit();
			} else {
				session.rollback();
			}

		} catch (Exception e) {
			// exception에 대한 처리 시 사용
			resultValue = -1;
		}

		return resultValue;
	}

	@Override
	public int insertBiddingHistory(AucEntrData aucEntrData) {
		int resultValue = 0;

		try (SqlSession session = DBSessionFactory.getSession()) {

			resultValue = getDao().insertBiddingHistory(aucEntrData, session);

			if (resultValue > 0) {
				session.commit();
			} else {
				session.rollback();
			}

		} catch (Exception e) {
			// exception에 대한 처리 시 사용
			resultValue = -1;
		}

		return resultValue;
	}

	@Override
	public List<EntryInfo> getStnEntryData(AuctionStnData auctionStnData) {

		List<EntryInfo> list = null;

		try (SqlSession session = DBSessionFactory.getSession()) {
			list = getDao().selectStnEntryInfo(auctionStnData, session);

		} catch (Exception e) {
			// exception에 대한 처리 시 사용
			return new ArrayList<EntryInfo>();
		}

		return list;
	}

	@Override
	public SelStsCountData getSelStsCount(AuctionStnData auctionStnData) {
		
		SelStsCountData countData;

		try (SqlSession session = DBSessionFactory.getSession()) {
			countData =  getDao().selectSelStsCount(auctionStnData, session);
		}catch (Exception e) {
			return new SelStsCountData();
		}

		return countData;
	}

	@Override
	public int getBiddingHistoryCount(AucEntrData aucEntrData) {
		
		int resultValue = 0;

		try (SqlSession session = DBSessionFactory.getSession()) {

			resultValue = getDao().selectBiddingHistoryCount(aucEntrData, session);

		} catch (Exception e) {
			// exception에 대한 처리 시 사용
			return resultValue = 0;
		}

		return resultValue;
	}

	@Override
	public int getNextBiddingHistoryCount(AucEntrData aucEntrData) {
		
		int resultValue = -1;

		try (SqlSession session = DBSessionFactory.getSession()) {

			resultValue = getDao().selectNextBiddingHistoryCount(aucEntrData, session);

		} catch (Exception e) {
			// exception에 대한 처리 시 사용
			return resultValue = -1;
		}

		return resultValue;
	}
}
