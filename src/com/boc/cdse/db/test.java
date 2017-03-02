package com.boc.cdse.db;

import java.util.List;

import com.boc.cdse.db.operator.ProductCardDBOperator;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List list1=ProductCardDBOperator.getInstance().getProductCardList();
		System.out.println(list1.size());
		int i=0;
	}

}
