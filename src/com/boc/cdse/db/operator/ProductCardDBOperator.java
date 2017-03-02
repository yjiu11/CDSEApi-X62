package com.boc.cdse.db.operator;


import java.util.List;


public class ProductCardDBOperator extends DBOperator {
	// ��Ŀ�������ݿ������ʵ��
	private static ProductCardDBOperator _instance = null;

	private ProductCardDBOperator() {
		this.initDB();
	}

	/**
	 * ͨ���˾�̬������ȡ���ݿ������ʵ��
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
	 * ��ѯ��Ʒ�б�
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
