package com.nh.controller.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.nh.controller.model.SpEntryInfo;
import com.nh.share.controller.models.EntryInfo;

public class TestUtil {

	private static TestUtil testData = null;

	public TestUtil() {
	}

	public static TestUtil getInstance() {
		if (testData == null) {
			testData = new TestUtil();
		}
		return testData;
	}

	/**
	 * @return test Map
	 */
	public List<EntryInfo> loadEntryDataList() {

		List<EntryInfo> dataList = new ArrayList<EntryInfo>();

		BufferedReader tmpBuffer = null;

		try {

			tmpBuffer = new BufferedReader(
					new InputStreamReader(this.getClass().getResourceAsStream("testData.txt"), "UTF-8"));

			String next = tmpBuffer.readLine();
			String line = next;

			for (boolean first = true, last = (line == null); !last; first = false, line = next) {

				last = ((next = tmpBuffer.readLine()) == null);

				String array[] = line.split(",");

				String yn = "";

				if (!last) {
					yn = "N";
				} else {
					yn = "Y";
				}

				EntryInfo entryInfo = new EntryInfo(array[1], array[2], array[3], array[4], array[5], array[6],
						array[7], array[8], array[9], array[10], array[11], array[12], array[13], array[14], array[15],
						array[16], array[17], array[18], array[19], array[20], array[21], array[22], array[23],
						array[24], yn);

				dataList.add(entryInfo);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (tmpBuffer != null) {
					tmpBuffer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return dataList;
	}

	/**
	 * @return test Map
	 */
	public LinkedHashMap<String, SpEntryInfo> loadEntryDataMap() {

		LinkedHashMap<String, SpEntryInfo> entryRepository = new LinkedHashMap<String, SpEntryInfo>();

		BufferedReader tmpBuffer = null;

		try {

			tmpBuffer = new BufferedReader(
					new InputStreamReader(this.getClass().getResourceAsStream("testData.txt"), "UTF-8"));

			String next = tmpBuffer.readLine();
			String line = next;

			for (boolean first = true, last = (line == null); !last; first = false, line = next) {

				last = ((next = tmpBuffer.readLine()) == null);

				String array[] = line.split(",");

				String yn = "";

				if (!last) {
					yn = "N";
				} else {
					yn = "Y";
				}

				SpEntryInfo entryInfo = new SpEntryInfo(array[1], array[2], array[3], array[4], array[5], array[6],
						array[7], array[8], array[9], array[10], array[11], array[12], array[13], array[14], array[15],
						yn);

				entryRepository.put(entryInfo.getEntryNum().getValue(), entryInfo);

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (tmpBuffer != null) {
					tmpBuffer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return entryRepository;
	}

	/**
	 * @return test DataList
	 */
	public List<EntryInfo> loadEntryData() {

		List<EntryInfo> entryRepository = new ArrayList<EntryInfo>();

		BufferedReader tmpBuffer = null;

		try {

			tmpBuffer = new BufferedReader(
					new InputStreamReader(this.getClass().getResourceAsStream("testData.txt"), "UTF-8"));

			String line = "";

			while ((line = tmpBuffer.readLine()) != null) {
				String array[] = line.split(",");

				entryRepository.add(new EntryInfo(array[1], array[2], array[3], array[4], array[5], array[6], array[7],
						array[8], array[9], array[10], array[11], array[12], array[13], array[14], array[15], array[16],
						array[17], array[18], array[19], array[20], array[21], array[22], array[23], array[24], "N"));
			}

			System.out.println("mEntryRepository Size : " + entryRepository.size());

			entryRepository.get(entryRepository.size() - 1).setIsLastEntry("Y");

			for (int i = 0; i < entryRepository.size(); i++) {
				System.out.println((i + 1) + "踰� 媛쒖껜踰덊샇 : " + entryRepository.get(i).getIndNum() + " / 留덉�留됱옄猷� �뿬遺� : "
						+ entryRepository.get(i).getIsLastEntry());
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (tmpBuffer != null) {
					tmpBuffer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return entryRepository;
	}
}
