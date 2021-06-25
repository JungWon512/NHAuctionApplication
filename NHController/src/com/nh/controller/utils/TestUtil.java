package com.nh.controller.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
	 * @return test DataList
	 */
	public List<EntryInfo> loadEntryData() {
		
		List<EntryInfo> entryRepository = new ArrayList<EntryInfo>();
		
		BufferedReader tmpBuffer = null;

		try {
			
			URL url = this.getClass().getClassLoader().getResource("com/nh/controller/utils/testData.txt");
			tmpBuffer = Files.newBufferedReader(Paths.get(url.toURI()));
			String line = "";

			while ((line = tmpBuffer.readLine()) != null) {
				String array[] = line.split(",");

				entryRepository.add(new EntryInfo(array[1], array[2], array[3], array[4], array[5], array[6], array[7],
						array[8], array[9], array[10], array[11], array[12], array[13], array[14], array[15], "N"));
			}

			System.out.println("mEntryRepository Size : " + entryRepository.size());

			entryRepository.get(entryRepository.size() - 1).setIsLastEntry("Y");

			for (int i = 0; i < entryRepository.size(); i++) {
				System.out.println((i + 1) + "번 개체번호 : " + entryRepository.get(i).getIndNum() + " / 마지막자료 여부 : "
						+ entryRepository.get(i).getIsLastEntry());
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
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
