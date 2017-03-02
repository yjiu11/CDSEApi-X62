package com.boc.cdse.db.operator;


import java.io.Reader;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class DBOperator {
	protected SqlMapClient sqlMap = null;

	/**
	 * ≥ı ºªØsqlMap
	 */
	protected void initDB() {
		try {
			if (sqlMap == null) {
				String userDir = System.getProperty("user.dir");
				String resource ;
//				System.out.println(resource);
				Reader reader;
//				File sqlMapCfgFile = new File(resource);
				resource = "com/boc/cdse/db/SqlMapConfig.xml";
				
				reader = Resources.getResourceAsReader(resource);
				sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
				if(sqlMap==null){
					System.out.println("sqlMap is null");
				}
				reader.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
