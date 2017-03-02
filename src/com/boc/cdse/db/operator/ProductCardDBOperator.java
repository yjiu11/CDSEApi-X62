package com.boc.cdse.db.operator;


import java.util.List;


public class ProductCardDBOperator extends DBOperator {
	// 项目管理数据库操作类实例
	private static ProductCardDBOperator _instance = null;

	private ProductCardDBOperator() {
		this.initDB();
	}

	/**
	 * 通过此静态方法获取数据库操作类实例
	 *
	 * @return
	 */
	public static ProductCardDBOperator getInstance() {
		if (null == _instance) {
			_instance = new ProductCardDBOperator();
		}

		return _instance;
	}

	/**
	 * 查询产品列表
	 *
	 * @param productCardList
	 * @return
	 */
	public List getProductCardList() {
		List productCardList = null;
    	try{
    		this.sqlMap.startTransaction();
    		productCardList=this.sqlMap.queryForList("CardProduct.getCardProductList",null);
    		if(productCardList==null){
    			System.out.println("productCardList is null");
    		}
    		this.sqlMap.commitTransaction();
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		try {
				this.sqlMap.endTransaction();
			} catch (Exception e) {
			}
    	}

		return productCardList;
	}
	
}
