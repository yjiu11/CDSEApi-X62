package com.boc.cdse.db.model;

public class CarProductModel {
    private String productCode;
    private String name;
// �������־
    private String grageFlag;
//    ����
    public static String grageFlag_Silver="01";
//  ��
    public static String grageFlag_Golden="02";
//  �����ֽ���
    public static String grageFlag_NoDifferenceGoldSilver="03";   

//  �������־
    private String typeFlag;
//  �տ�
    public static String typeFlag_Normal="01";
//  ˫�ҿ�
    public static String typeFlag_JCB="02";
//  ���˿�
    public static String typeFlag_OLike="03";
//  ����Ա��
    public static String typeFlag_Duty="04";
//  �׽�
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
