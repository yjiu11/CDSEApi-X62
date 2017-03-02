package com.boc.cdse;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.boc.cdse.db.model.CarProductModel;
import com.boc.cdse.db.operator.ProductCardDBOperator;

public class ProductCardInfoParameter {
	public static Map<String, String> allProductCardMap;
	public static Map<String, String> normalProductCardMap;
	public static Map<String, String> dutyProductCardMap;
	public static Map<String, String> jcbProductCardMap;
	public static Map<String, String> oLikeProductCardMap;
	public static Map<String, String> platinaProductCardMap;
	public static Map<String, String> silverProductCardMap;
	public static Map<String, String> goldenProductCardMap;
	public static Map<String, String> noDifferenceGoldSilverCardMap;
	public static Map<String, Float> exchangeRateCardMap;
	public static Map<String, String> allProductCardMap_temp;
	static {
		try {
			
			List list1 = ProductCardDBOperator.getInstance()
					.getProductCardList();
			allProductCardMap_temp = new HashMap<String, String>();
			Map<String, String> normalProductCardMap = new HashMap<String, String>();
			Map<String, String> dutyProductCardMap = new HashMap<String, String>();
			Map<String, String> jcbProductCardMap = new HashMap<String, String>();
			Map<String, String> oLikeProductCardMap = new HashMap<String, String>();
			Map<String, String> silverProductCardMap = new HashMap<String, String>();
			Map<String, String> goldenProductCardMap = new HashMap<String, String>();
			Map<String, String> platinaProductCardMap = new HashMap<String, String>();
			Map<String, String> noDifferenceGoldSilverCardMap = new HashMap<String, String>();
			Map<String, Float> exchangeRateCardMap = new HashMap<String, Float>();
			int count = 0;

			if (list1 != null && list1.size() > 0) {
				for (Iterator it = list1.iterator(); it.hasNext();) {
					CarProductModel cpm = (CarProductModel) it.next();
					allProductCardMap_temp.put(cpm.getProductCode(),
							cpm.getName());
					exchangeRateCardMap.put(cpm.getProductCode(),
							cpm.getExchangeRate());
					// 金卡
					if (CarProductModel.grageFlag_Golden.equals(cpm
							.getGrageFlag())) {
						goldenProductCardMap.put(cpm.getProductCode(),
								cpm.getProductCode());
					} else if (CarProductModel.grageFlag_NoDifferenceGoldSilver
							.equals(cpm.getGrageFlag())) {
						noDifferenceGoldSilverCardMap.put(cpm.getProductCode(),
								cpm.getProductCode());
					} else {
						// 银卡
						silverProductCardMap.put(cpm.getProductCode(),
								cpm.getProductCode());
					}
					// jcb卡
					if (CarProductModel.typeFlag_JCB.equals(cpm.getTypeFlag())) {
						jcbProductCardMap.put(cpm.getProductCode(),
								cpm.getProductCode());
					} else if (CarProductModel.typeFlag_OLike.equals(cpm
							.getTypeFlag())) {
						// 奥运卡
						oLikeProductCardMap.put(cpm.getProductCode(),
								cpm.getProductCode());
					} else if (CarProductModel.typeFlag_Duty.equals(cpm
							.getTypeFlag())) {
						// 公务员卡
						dutyProductCardMap.put(cpm.getProductCode(),
								cpm.getProductCode());
					} else if (CarProductModel.typeFlag_Platina.equals(cpm
							.getTypeFlag())) {
						// 白金卡
						platinaProductCardMap.put(cpm.getProductCode(),
								cpm.getProductCode());
					} else {
						// 普通卡
						normalProductCardMap.put(cpm.getProductCode(),
								cpm.getProductCode());
					}

					count++;
				}
			}
			ProductCardInfoParameter.allProductCardMap = allProductCardMap_temp;
			ProductCardInfoParameter.normalProductCardMap = normalProductCardMap;
			ProductCardInfoParameter.dutyProductCardMap = dutyProductCardMap;
			ProductCardInfoParameter.platinaProductCardMap = platinaProductCardMap;
			ProductCardInfoParameter.oLikeProductCardMap = oLikeProductCardMap;
			ProductCardInfoParameter.jcbProductCardMap = jcbProductCardMap;
			ProductCardInfoParameter.goldenProductCardMap = goldenProductCardMap;
			ProductCardInfoParameter.silverProductCardMap = silverProductCardMap;
			ProductCardInfoParameter.noDifferenceGoldSilverCardMap = noDifferenceGoldSilverCardMap;
			ProductCardInfoParameter.exchangeRateCardMap = exchangeRateCardMap;

			System.out.println("count=" + count);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void load_allProductCardMap() {
		ProductCardInfoParameter.allProductCardMap.clear();
		List list1 = ProductCardDBOperator.getInstance().getProductCardList();
		if (list1 != null && list1.size() > 0) {
			for (Iterator it = list1.iterator(); it.hasNext();) {
				CarProductModel cpm = (CarProductModel) it.next();
				allProductCardMap_temp.put(cpm.getProductCode(), cpm.getName());
				ProductCardInfoParameter.allProductCardMap = allProductCardMap_temp;
			}
		}
	}
}
