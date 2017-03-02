package com.boc.cdse.db.model;

public class CarProductModel {
    private String productCode;
    private String name;
// 卡级别标志
    private String grageFlag;
//    银卡
    public static String grageFlag_Silver="01";
//  金卡
    public static String grageFlag_Golden="02";
//  不区分金普
    public static String grageFlag_NoDifferenceGoldSilver="03";   

//  卡种类标志
    private String typeFlag;
//  普卡
    public static String typeFlag_Normal="01";
//  双币卡
    public static String typeFlag_JCB="02";
//  奥运卡
    public static String typeFlag_OLike="03";
//  公务员卡
    public static String typeFlag_Duty="04";
//  白金卡
    public static String typeFlag_Platina="05";
    private  Float exchangeRate;
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGrageFlag() {
		return grageFlag;
	}
	public void setGrageFlag(String grageFlag) {
		this.grageFlag = grageFlag;
	}
	public String getTypeFlag() {
		return typeFlag;
	}
	public void setTypeFlag(String typeFlag) {
		this.typeFlag = typeFlag;
	}
	public Float getExchangeRate() {
		return exchangeRate;
	}
	public void setExchangeRate(Float exchangeRate) {
		this.exchangeRate = exchangeRate;
	}
	
   }
