package com.nh.controller.database;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class DBSeesionFactory {

	private static SqlSessionFactory sqlSessionFactory;

	static {
		try {
			String resource = "com/nh/controller/database/config/db_config.xml";
			Reader reader = Resources.getResourceAsReader(resource);

			if (sqlSessionFactory == null) {
				sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
			}
		} catch (FileNotFoundException fileNotFoundException) {
			fileNotFoundException.printStackTrace();
		} catch (IOException iOException) {
			iOException.printStackTrace();
		}
	}

	public static SqlSession getSession() {
		SqlSession session = sqlSessionFactory.openSession();
		return session;
	}

//	public static SqlSessionFactory getSqlSessionFactory() {
//		return sqlSessionFactory;
//	}
}