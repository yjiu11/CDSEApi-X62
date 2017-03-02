package com.boc.cdse;

import java.io.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class CreateData implements Serializable{

    private String AppId = "";

    private String AppType = "";

    private String ProductCd = "";

    private String PriCardCurr = "";

    private String SupplApplOnly = "";

    private String AprAssetType1 = "";
    private String AprAssetType2 = "";
    private String AprAssetType3 = "";

    private String AprAssetValue1 = "";
    private String AprAssetValue2 = "";
    private String AprAssetValue3 = "";


    private String CCardTypeDownGrade = "";

    private String PriJCBType = "";

    private String PriGender = "";

    private String PriCardBirthday = "";

    private String PriIDType = "";

    private String PriIDNo = "";

    private String PriNationality = "";

    private String PriMaritalSta = "";

    private String PriEducationLvl = "";

    private String Supp1Check = "";

    private String Supp2Check = "";

    private String Supp3Check = "";

    private String PriHomePtCd = "";

    private String HomeResidingYear = "";

    private String HomeResidingMonth = "";

    private String HomeOwshType = "";



    private String HouseHireAmt = "";

    private String HousePayAmt = "";

    private String HomePhonePro = "";

    private String HomePhoneNo = "";

    private String DependentCnt = "";

    private String ComAddrPtCd = "";

    private String CompTelPhonePro = "";

    private String CompTelPhone = "";

    private String CompBizField = "";

    private String CompBizOwShType = "";

    private String PriPos = "";

    private String CompPosSeniority = "";

    private String MthlySalary = "";

    private String AmmSalary = "";

    private String OtherBankCardSta = "";

    private String OthCardCnt = "";

    private String BOCSaveActFlg = "";

    private String OthBankSaveActNum = "";

    private String AssetValue = "";

    private String CarOwShCondition = "";

    private String SuppRelation1 = "";

    private String PrimaryCardNo1 = "";

    private String SupplRelationExpl = "";

    private String SuppSex1 = "";

    private String SuppBirthday1 = "";

    private String SuppIDType1 = "";

    private String SuppIDNo1 = "";

    private String SuppLimitFlag1 = "";

    private String SuppLimitPercent1 = "";

    private String SuppRelation2 = "";

    private String PrimaryCardNo2 = "";



    private String SuppSex2 = "";

    private String SuppBirthday2 = "";

    private String SuppIDType2 = "";

    private String SuppIDNo2 = "";

    private String SuppLimitFlag2 = "";

    private String SuppLimitPercent2 = "";

    private String SuppRelation3 = "";

    private String PrimaryCardNo3 = "";

    private String SuppSex3 = "";

    private String SuppBirthday3 = "";

    private String SuppIDType3 = "";

    private String SuppIDNo3 = "";

    private String SuppLimitFlag3 = "";

    private String SuppLimitPercent3 = "";

    private String PriStatAddrSel = "";

    private String SuppStatAddrPtCd = "";

    private String AutoPayMethod = "";

    private String AutoPayAmtSel = "";

    private String AutoPayActSel = "";

    private String BKCC = "";

    private String BKBH = "";

    private String BKPCC = "";

    private String BKVIP = "";

    private String BKSTF = "";

    private String IncomeProof = "";

    private String BOCCardHolder = "";

    private String BOCCardBeginDate = "";

    private String BOCCardLimit = "";

    private String BOCCardNum = "";

    private String DPD30P24Mon = "";

    private String DPD60P24Mon = "";

    private String DPD90P24Mon = "";

    private String AppRanNum = "";

    private String FirstApprvdStrategyID = "";

    private String AppCnt = "";



    private String AprAnnIncome = "";

    private String AprSpeGrpType = "";

    private String AprPosLevl = "";

    private String AprWrkSeniority = "";

    private String IfHiRiskBiz = "";

    private String IfSSInfo = "";

    private String IfPBOCInfo = "";

    private String IfCBRCInfo = "";

    private String IfMPSInfo = "";

    private String CurrSSMonthPay = "";

    private String LstSSPayDate = "";

    private String MPSBirthDate = "";

    private String NumCCardAct = "";

    private String NumCCardOrg = "";

    private String TotlCCardCLmt = "";

    private String TotlCCardOLmt = "";

    private String CCardFrtOpnDate = "";

    private String CCardLstOpnDate = "";

    private String CCardHiLimit = "";

    private String ReservedField1 = "";

    private String CCardM0In3M = "";

    private String CCardM1In3M = "";

    private String CCardM2In3M = "";

    private String CCardM1In6M = "";

    private String CCardM2In12M = "";

    private String CurrLoanBalance = "";

    private String CurrLoanMthlyPay = "";

    private String NumOpnActin6M = "";

    private String LoanM0In3M = "";

    private String LoanM1In3M = "";

    private String LoanM2In3M = "";

    private String LoanM1In6M = "";

    private String LoanM2In12M = "";

    private String CLM1In3M = "";
    private String autoPayMethod;
    private String homePhonePro;
    private String housePayAmt;
    private String incomeProof;
    private String productCd;
    
    
    private String A01001 = "";//是否获取到征信报告：
    private String A01002 = "";//征信报告中是否具有征信信息：
    private String A02001 = "";//是否我行信用卡客户：
    private String A02002 = "";//我行客户信用额度：
    private String A03001 = "";//是否他行信用卡客户：
    private String A03002 = "";//他行信用卡单卡最高额度(人民币)：
    private String A03003 = "";//他行信用卡卡龄6个月以上的单卡最高额度(人民币)：
    private String A04001 = "";//是否行内个人金融资产类客户：
    private String A04002 = "";//行内个人金融资产类客户等级：
    private String A04003 = "";//行内个人金融资产类客户近三个月月日均资产余额：
    private String A05001 = "";//养老保险金参保地
    private String A05002 = "";//养老保险金参保日期
    private String A05003 = "";//养老保险金累计缴费月数
    private String A05004 = "";//养老保险金参加工作月份
    private String A05005 = "";//养老保险金缴费状态
    private String A05006 = "";//养老保险金个人缴费基数
    private String A05007 = "";//养老保险金本月缴费金额
    private String A05008 = "";//养老保险金信息更新日期
    private String A05009 = "";//养老保险金缴费单位
    private String A05010 = "";//养老保险金中断或终止缴费原因
    private String A06001 = "";//住房公积金参缴地：
    private String A06002 = "";//住房公积金初缴月份：
    private String A06003 = "";//住房公积金缴至月份：
    private String A06004 = "";//住房公积金缴费状态：
    private String A06005 = "";//住房公积金月缴存额：
    private String A06006 = "";//住房公积金个人缴存比例：
    private String A06007 = "";//住房公积金单位缴存比例：
    private String A06008 = "";//住房公积金缴费单位
    private String A06009 = "";//住房公积金信息更新日期
    private String A07001 = "";//近3个月同时发生信用卡和贷款逾期的次数：
    private String A07002 = "";//近6个月同时发生信用卡和贷款逾期的次数：
    private String A07003 = "";//近12个月同时发生信用卡和贷款逾期的次数：
    private String A07004 = "";//近24个月同时发生信用卡和贷款逾期的次数：
    private String A08001 = "";//贷记卡发卡机构数：
    private String A08002 = "";//贷记卡账户数：
    private String A08003 = "";//贷记卡非正常账户数：
    private String A08004 = "";//贷记卡最早开户日期：
    private String A08005 = "";//贷记卡授信总额：
    private String A08006 = "";//贷记卡总已用额度：
    private String A08007 = "";//贷记卡最近6个月平均使用额度：
    private String A08008 = "";//贷记卡单家行最高额度(人民币)：
    private String A08009 = "";//贷记卡单家行最低授信额(人民币)：
    private String A08010 = "";//贷记卡卡龄6个月以上最高额度(人民币)：
    private String A08011 = "";//贷记卡他行最高额度(人民币)：
    private String A08012 = "";//贷记卡他行10万及以下第一高额度(人民币)：
    private String A08013 = "";//贷记卡他行10万及以下第二高额度(人民币)：
    private String A08014 = "";//贷记卡当前逾期总额：
    private String A09001 = "";//贷记卡近3个月M1的次数：
    private String A09002 = "";//贷记卡近3个月M2的次数：
    private String A09003 = "";//贷记卡近3个月M3的次数：
    private String A09004 = "";//贷记卡近3个月M4的次数：
    private String A09005 = "";//贷记卡近3个月M5的次数：
    private String A09006 = "";//贷记卡近3个月M6的次数：
    private String A09007 = "";//贷记卡近3个月M7的次数：
    private String A09008 = "";//贷记卡近6个月M1的次数：
    private String A09009 = "";//贷记卡近6个月M2的次数：
    private String A09010 = "";//贷记卡近6个月M3的次数：
    private String A09011 = "";//贷记卡近6个月M4的次数：
    private String A09012 = "";//贷记卡近6个月M5的次数：
    private String A09013 = "";//贷记卡近6个月M6的次数：
    private String A09014 = "";//贷记卡近6个月M7的次数：
    private String A09015 = "";//贷记卡近12个月M1的次数：
    private String A09016 = "";//贷记卡近12个月M2的次数：
    private String A09017 = "";//贷记卡近12个月M3的次数：
    private String A09018 = "";//贷记卡近12个月M4的次数：
    private String A09019 = "";//贷记卡近12个月M5的次数：
    private String A09020 = "";//贷记卡近12个月M6的次数：
    private String A09021 = "";//贷记卡近12个月M7的次数：
    private String A09022 = "";//贷记卡近24个月M1的次数：
    private String A09023 = "";//贷记卡近24个月M2的次数：
    private String A09024 = "";//贷记卡近24个月M3的次数：
    private String A09025 = "";//贷记卡近24个月M4的次数：
    private String A09026 = "";//贷记卡近24个月M5的次数：
    private String A09027 = "";//贷记卡近24个月M6的次数：
    private String A09028 = "";//贷记卡近24个月M7的次数：
    private String A09029 = "";//贷记卡近5年前36个月逾期最高持续月数：
    private String A09030 = "";//贷记卡当月最高逾期级别：
    private String A10001 = "";//准贷记卡发卡机构数：
    private String A10002 = "";//准贷记卡账户数：
    private String A10003 = "";//准贷记卡非正常账户数：
    private String A10004 = "";//准贷记卡最早开户日期：
    private String A10005 = "";//准贷记卡授信总额：
    private String A10006 = "";//准贷记卡总透支余额：
    private String A10007 = "";//准贷记卡最近6个月平均透支余额：
    private String A10008 = "";//准贷记卡单家行最高额度(人民币)：
    private String A10009 = "";//准贷记卡单家行最低授信额(人民币)：
    private String A10010 = "";//准贷记卡卡龄6个月以上最高额度(人民币)：
    private String A10011 = "";//贷记卡他行最高额度(人民币)：
    private String A10012 = "";//贷记卡他行10万及以下第一高额度(人民币)：
    private String A10013 = "";//贷记卡他行10万及以下第二高额度(人民币)：
    private String A10014 = "";//准贷记卡当前逾期总额：
    private String A11001 = "";//准贷记卡近3个月M1的次数：
    private String A11002 = "";//准贷记卡近3个月M2的次数：
    private String A11003 = "";//准贷记卡近3个月M3的次数：
    private String A11004 = "";//准贷记卡近3个月M4的次数：
    private String A11005 = "";//准贷记卡近3个月M5的次数：
    private String A11006 = "";//准贷记卡近3个月M6的次数：
    private String A11007 = "";//准贷记卡近3个月M7的次数：
    private String A11008 = "";//准贷记卡近6个月M1的次数：
    private String A11009 = "";//准贷记卡近6个月M2的次数：
    private String A11010 = "";//准贷记卡近6个月M3的次数：
    private String A11011 = "";//准贷记卡近6个月M4的次数：
    private String A11012 = "";//准贷记卡近6个月M5的次数：
    private String A11013 = "";//准贷记卡近6个月M6的次数：
    private String A11014 = "";//准贷记卡近6个月M7的次数：
    private String A11015 = "";//准贷记卡近12个月M1的次数：
    private String A11016 = "";//准贷记卡近12个月M2的次数：
    private String A11017 = "";//准贷记卡近12个月M3的次数：
    private String A11018 = "";//准贷记卡近12个月M4的次数：
    private String A11019 = "";//准贷记卡近12个月M5的次数：
    private String A11020 = "";//准贷记卡近12个月M6的次数：
    private String A11021 = "";//准贷记卡近12个月M7的次数：
    private String A11022 = "";//准贷记卡近24个月M1的次数：
    private String A11023 = "";//准贷记卡近24个月M2的次数：
    private String A11024 = "";//准贷记卡近24个月M3的次数：
    private String A11025 = "";//准贷记卡近24个月M4的次数：
    private String A11026 = "";//准贷记卡近24个月M5的次数：
    private String A11027 = "";//准贷记卡近24个月M6的次数：
    private String A11028 = "";//准贷记卡近24个月M7的次数：
    private String A11029 = "";//准贷记卡近5年前36个月逾期最高持续月数：
    private String A11030 = "";//准贷记卡当月最高逾期级别：
    private String A12001 = "";//贷款机构数：
    private String A12002 = "";//贷款总笔数：
    private String A12003 = "";//贷款非正常账户数：
    private String A12004 = "";//贷款合同总额：
    private String A12005 = "";//贷款总余额：
    private String A12006 = "";//贷款最近6个月平均应还款：
    private String A12007 = "";//贷款本月应还款总额：
    private String A12008 = "";//贷款最近连续正常还款期数>=3的本月应还款总额：
    private String A12009 = "";//贷款当前逾期总额
    private String A12010 = "";//贷款（31-个人助学贷款）的笔数：
    private String A13001 = "";//贷款近3个月M1的次数：
    private String A13002 = "";//贷款近3个月M2的次数：
    private String A13003 = "";//贷款近3个月M3的次数：
    private String A13004 = "";//贷款近3个月M4的次数：
    private String A13005 = "";//贷款近3个月M5的次数：
    private String A13006 = "";//贷款近3个月M6的次数：
    private String A13007 = "";//贷款近3个月M7的次数：
    private String A13008 = "";//贷款近6个月M1的次数：
    private String A13009 = "";//贷款近6个月M2的次数：
    private String A13010 = "";//贷款近6个月M3的次数：
    private String A13011 = "";//贷款近6个月M4的次数：
    private String A13012 = "";//贷款近6个月M5的次数：
    private String A13013 = "";//贷款近6个月M6的次数：
    private String A13014 = "";//贷款近6个月M7的次数：
    private String A13015 = "";//贷款近12个月M1的次数：
    private String A13016 = "";//贷款近12个月M2的次数：
    private String A13017 = "";//贷款近12个月M3的次数：
    private String A13018 = "";//贷款近12个月M4的次数：
    private String A13019 = "";//贷款近12个月M5的次数：
    private String A13020 = "";//贷款近12个月M6的次数：
    private String A13021 = "";//贷款近12个月M7的次数：
    private String A13022 = "";//贷款近24个月M1的次数：
    private String A13023 = "";//贷款近24个月M2的次数：
    private String A13024 = "";//贷款近24个月M3的次数：
    private String A13025 = "";//贷款近24个月M4的次数：
    private String A13026 = "";//贷款近24个月M5的次数：
    private String A13027 = "";//贷款近24个月M6的次数：
    private String A13028 = "";//贷款近24个月M7的次数：
    private String A13029 = "";//贷款近5年前36个月逾期最高持续月数：
    private String A13030 = "";//贷款当月最高逾期级别：
    private String A14001 = "";//担保贷款五级分类为非正常的总数：
    private String A14002 = "";//担保贷款五级分类为非正常的累计本金余额：
    private String A15001 = "";//最近1个月内的查询机构数-贷款审批：
    private String A15002 = "";//最近1个月内的查询机构数-信用卡审批：
    private String A15003 = "";//最近1个月内的查询次数-贷款审批：
    private String A15004 = "";//最近1个月内的查询次数-信用卡审批：
    private String A15005 = "";//最近2年内的查询次数-贷后管理：
    private String A15006 = "";//最近2年内的查询次数-担保资格审查：
    private String A15007 = "";//最近2年内的查询次数-特约商户实名审查：
    private String A15012 = "";//银行代发工资、存折
    private String A15013 = "";//账单月收入
    private String A15015 = "";//可证明收入数值其他年收入
    private String A15016 = "";//税前月收入
    private String A15017 = "";//知名企业月收入
    private String A15018 = "";//政府或事业单位月收入
    private String A12011 = "";//房产价值
    private String A15033 = "";//发卡账户数
    private String AT4300 = "";//VIP等级
    private String AT4310 = "";//是否为公安检测出4类人员
    private String AT4320 = "";//所属机构号(一级分行)
    private String A08015 = "";//符合要求贷记卡中信用额度最大值
    private String A10015 = "";//符合要求准贷记卡中信用额度最大值
    private String A08016 = "";//贷记卡逾期-最长逾期月数
    private String A10016 = "";//准贷记卡60天以上透支-最长透支月数
    private String A12012 = "";//贷款逾期-最长逾期月数
    private String AT4330 = "";//要客分类
    public String getAT4310() {
		return AT4310;
	}
	public void setAT4310(String at4310) {
		AT4310 = at4310;
	}
		public String getIncomeProof() {
                          return IncomeProof;
                      }
                      public void setIncomeProof(String IncomeProof) {
                          this.IncomeProof = IncomeProof;
      }



        public String getProductCd() {
                        return ProductCd;
                    }
                    public void setProductCd(String ProductCd) {
                        this.ProductCd = ProductCd;
    }
    public String getPriGender() {
                      return PriGender;
                  }
                  public void setPriGender(String PriGender) {
                      this.PriGender = PriGender;
  }


    public String getAutoPayMethod() {
                 return AutoPayMethod;
             }
             public void setAutoPayMethod(String AutoPayMethod) {
                 this.AutoPayMethod = AutoPayMethod;
    }



    public String getHomePhonePro() {
              return homePhonePro;
          }
          public void setHomePhonePro(String homePhonePro) {
              this.homePhonePro = homePhonePro;
    }

    public String getHousePayAmt() {
           return housePayAmt;
       }
       public void setHousePayAmt(String housePayAmt) {
           this.housePayAmt = housePayAmt;
    }


    public String getAmmSalary() {
        return AmmSalary;
    }
    public void setAmmSalary(String AmmSalary) {
        this.AmmSalary = AmmSalary;
    }



    public void setAppCnt(String AppCnt) {
        this.AppCnt = AppCnt;
    }
    public String getAppCnt() {
        return AppCnt;
    }
    public String getAppId() {
        return AppId;
    }
    public void setAppId(String AppId) {
        this.AppId = AppId;
    }
    public void setAppRanNum(String AppRanNum) {
        this.AppRanNum = AppRanNum;
    }
    public String getAppRanNum() {
        return AppRanNum;
    }
    public String getAppType() {
        return AppType;
    }
    public void setAppType(String AppType) {
        this.AppType = AppType;
    }
    public void setAprAnnIncome(String AprAnnIncome) {
        this.AprAnnIncome = AprAnnIncome;
    }
    public String getAprAnnIncome() {
        return AprAnnIncome;
    }
    public String getAprAssetType1() {
        return AprAssetType1;
    }
    public void setAprAssetType1(String AprAssetType1) {
        this.AprAssetType1 = AprAssetType1;
    }
    public void setAprAssetType2(String AprAssetType2) {
        this.AprAssetType2 = AprAssetType2;
    }
    public String getAprAssetType2() {
        return AprAssetType2;
    }
    public String getAprAssetType3() {
        return AprAssetType3;
    }
    public void setAprAssetType3(String AprAssetType3) {
        this.AprAssetType3 = AprAssetType3;
    }
    public void setAprPosLevl(String AprPosLevl) {
        this.AprPosLevl = AprPosLevl;
    }
    public String getAprPosLevl() {
        return AprPosLevl;
    }
    public String getAprSpeGrpType() {
        return AprSpeGrpType;
    }
    public String getAprWrkSeniority() {
        return AprWrkSeniority;
    }
    public void setAprSpeGrpType(String AprSpeGrpType) {
        this.AprSpeGrpType = AprSpeGrpType;
    }
    public void setAprWrkSeniority(String AprWrkSeniority) {
        this.AprWrkSeniority = AprWrkSeniority;
    }
    public void setAssetValue(String AssetValue) {
        this.AssetValue = AssetValue;
    }
    public String getAssetValue() {
        return AssetValue;
    }
    public String getAutoPayActSel() {
        return AutoPayActSel;
    }
    public String getAutoPayAmtSel() {
        return AutoPayAmtSel;
    }
    public String getBKBH() {
        return BKBH;
    }
    public String getBKCC() {
        return BKCC;
    }
    public String getBKPCC() {
        return BKPCC;
    }
    public String getBKSTF() {
        return BKSTF;
    }
    public String getBKVIP() {
        return BKVIP;
    }
    public String getBOCCardBeginDate() {
        return BOCCardBeginDate;
    }
    public String getBOCCardHolder() {
        return BOCCardHolder;
    }
    public String getBOCCardLimit() {
        return BOCCardLimit;
    }
    public void setAutoPayActSel(String AutoPayActSel) {
        this.AutoPayActSel = AutoPayActSel;
    }
    public void setAutoPayAmtSel(String AutoPayAmtSel) {
        this.AutoPayAmtSel = AutoPayAmtSel;
    }

    public void setBKBH(String BKBH) {
        this.BKBH = BKBH;
    }
    public void setBKCC(String BKCC) {
        this.BKCC = BKCC;
    }
    public void setBKPCC(String BKPCC) {
        this.BKPCC = BKPCC;
    }
    public void setBKSTF(String BKSTF) {
        this.BKSTF = BKSTF;
    }
    public void setBKVIP(String BKVIP) {
        this.BKVIP = BKVIP;
    }
    public void setBOCCardBeginDate(String BOCCardBeginDate) {
        this.BOCCardBeginDate = BOCCardBeginDate;
    }
    public void setBOCCardHolder(String BOCCardHolder) {
        this.BOCCardHolder = BOCCardHolder;
    }
    public void setBOCCardLimit(String BOCCardLimit) {
        this.BOCCardLimit = BOCCardLimit;
    }
    public String getBOCCardNum() {
        return BOCCardNum;
    }
    public String getBOCSaveActFlg() {
        return BOCSaveActFlg;
    }
    public String getCarOwShCondition() {
        return CarOwShCondition;
    }
    public String getCCardFrtOpnDate() {
        return CCardFrtOpnDate;
    }
    public String getCCardHiLimit() {
        return CCardHiLimit;
    }
    public String getCCardLstOpnDate() {
        return CCardLstOpnDate;
    }
    public String getCCardM0In3M() {
        return CCardM0In3M;
    }
    public String getCCardM1In3M() {
        return CCardM1In3M;
    }
    public String getCCardM1In6M() {
        return CCardM1In6M;
    }
    public String getCCardM2In12M() {
        return CCardM2In12M;
    }
    public void setCCardM2In12M(String CCardM2In12M) {
        this.CCardM2In12M = CCardM2In12M;
    }
    public void setCCardM1In6M(String CCardM1In6M) {
        this.CCardM1In6M = CCardM1In6M;
    }
    public void setCCardM1In3M(String CCardM1In3M) {
        this.CCardM1In3M = CCardM1In3M;
    }
    public void setCCardM0In3M(String CCardM0In3M) {
        this.CCardM0In3M = CCardM0In3M;
    }
    public void setCCardLstOpnDate(String CCardLstOpnDate) {
        this.CCardLstOpnDate = CCardLstOpnDate;
    }
    public void setCCardHiLimit(String CCardHiLimit) {
        this.CCardHiLimit = CCardHiLimit;
    }
    public void setCCardFrtOpnDate(String CCardFrtOpnDate) {
        this.CCardFrtOpnDate = CCardFrtOpnDate;
    }
    public void setCarOwShCondition(String CarOwShCondition) {
        this.CarOwShCondition = CarOwShCondition;
    }
    public void setBOCSaveActFlg(String BOCSaveActFlg) {
        this.BOCSaveActFlg = BOCSaveActFlg;
    }
    public void setBOCCardNum(String BOCCardNum) {
        this.BOCCardNum = BOCCardNum;
    }
    public String getCCardM2In3M() {
        return CCardM2In3M;
    }
    public String getCCardTypeDownGrade() {
        return CCardTypeDownGrade;
    }
    public String getCLM1In3M() {
        return CLM1In3M;
    }
    public String getComAddrPtCd() {
        return ComAddrPtCd;
    }
    public String getCompBizField() {
        return CompBizField;
    }
    public String getCompBizOwShType() {
        return CompBizOwShType;
    }
    public String getCompPosSeniority() {
        return CompPosSeniority;
    }
    public String getCompTelPhone() {
        return CompTelPhone;
    }
    public String getCompTelPhonePro() {
        return CompTelPhonePro;
    }
    public String getCurrLoanBalance() {
        return CurrLoanBalance;
    }
    public String getCurrLoanMthlyPay() {
        return CurrLoanMthlyPay;
    }
    public void setCCardM2In3M(String CCardM2In3M) {
        this.CCardM2In3M = CCardM2In3M;
    }
    public void setCCardTypeDownGrade(String CCardTypeDownGrade) {
        this.CCardTypeDownGrade = CCardTypeDownGrade;
    }
    public void setCLM1In3M(String CLM1In3M) {
        this.CLM1In3M = CLM1In3M;
    }
    public void setComAddrPtCd(String ComAddrPtCd) {
        this.ComAddrPtCd = ComAddrPtCd;
    }
    public void setCompBizField(String CompBizField) {
        this.CompBizField = CompBizField;
    }
    public void setCompBizOwShType(String CompBizOwShType) {
        this.CompBizOwShType = CompBizOwShType;
    }
    public void setCompPosSeniority(String CompPosSeniority) {
        this.CompPosSeniority = CompPosSeniority;
    }
    public void setCompTelPhone(String CompTelPhone) {
        this.CompTelPhone = CompTelPhone;
    }
    public void setCompTelPhonePro(String CompTelPhonePro) {
        this.CompTelPhonePro = CompTelPhonePro;
    }
    public void setCurrLoanBalance(String CurrLoanBalance) {
        this.CurrLoanBalance = CurrLoanBalance;
    }
    public void setCurrLoanMthlyPay(String CurrLoanMthlyPay) {
        this.CurrLoanMthlyPay = CurrLoanMthlyPay;
    }
    public String getCurrSSMonthPay() {
        return CurrSSMonthPay;
    }
    public void setCurrSSMonthPay(String CurrSSMonthPay) {
        this.CurrSSMonthPay = CurrSSMonthPay;
    }
    public void setDependentCnt(String DependentCnt) {
        this.DependentCnt = DependentCnt;
    }
    public String getDependentCnt() {
        return DependentCnt;
    }
    public String getDPD30P24Mon() {
        return DPD30P24Mon;
    }
    public void setDPD30P24Mon(String DPD30P24Mon) {
        this.DPD30P24Mon = DPD30P24Mon;
    }
    public void setDPD60P24Mon(String DPD60P24Mon) {
        this.DPD60P24Mon = DPD60P24Mon;
    }
    public String getDPD60P24Mon() {
        return DPD60P24Mon;
    }
    public String getDPD90P24Mon() {
        return DPD90P24Mon;
    }
    public void setDPD90P24Mon(String DPD90P24Mon) {
        this.DPD90P24Mon = DPD90P24Mon;
    }
    public void setFirstApprvdStrategyID(String FirstApprvdStrategyID) {
        this.FirstApprvdStrategyID = FirstApprvdStrategyID;
    }
    public String getFirstApprvdStrategyID() {
        return FirstApprvdStrategyID;
    }
    public String getHomeOwshType() {
        return HomeOwshType;
    }
    public void setHomeOwshType(String HomeOwshType) {
        this.HomeOwshType = HomeOwshType;
    }
    public void setHomePhoneNo(String HomePhoneNo) {
        this.HomePhoneNo = HomePhoneNo;
    }
    public String getHomePhoneNo() {
        return HomePhoneNo;
    }




    public void setHomeResidingMonth(String HomeResidingMonth) {
        this.HomeResidingMonth = HomeResidingMonth;
    }
    public String getHomeResidingMonth() {
        return HomeResidingMonth;
    }
    public String getHomeResidingYear() {
        return HomeResidingYear;
    }
    public void setHomeResidingYear(String HomeResidingYear) {
        this.HomeResidingYear = HomeResidingYear;
    }
    public void setHouseHireAmt(String HouseHireAmt) {
        this.HouseHireAmt = HouseHireAmt;
    }
    public String getHouseHireAmt() {
        return HouseHireAmt;
    }

    public String getIfCBRCInfo() {
        return IfCBRCInfo;
    }
    public String getIfHiRiskBiz() {
        return IfHiRiskBiz;
    }
    public String getIfMPSInfo() {
        return IfMPSInfo;
    }
    public String getIfPBOCInfo() {
        return IfPBOCInfo;
    }
    public String getIfSSInfo() {
        return IfSSInfo;
    }

    public String getLoanM0In3M() {
        return LoanM0In3M;
    }
    public String getLoanM1In3M() {
        return LoanM1In3M;
    }
    public String getLoanM1In6M() {
        return LoanM1In6M;
    }
    public String getLoanM2In12M() {
        return LoanM2In12M;
    }
    public void setIfCBRCInfo(String IfCBRCInfo) {
        this.IfCBRCInfo = IfCBRCInfo;
    }
    public void setIfHiRiskBiz(String IfHiRiskBiz) {
        this.IfHiRiskBiz = IfHiRiskBiz;
    }
    public void setIfPBOCInfo(String IfPBOCInfo) {
        this.IfPBOCInfo = IfPBOCInfo;
    }
    public void setIfMPSInfo(String IfMPSInfo) {
        this.IfMPSInfo = IfMPSInfo;
    }
    public void setIfSSInfo(String IfSSInfo) {
        this.IfSSInfo = IfSSInfo;
    }
    public void setLoanM0In3M(String LoanM0In3M) {
        this.LoanM0In3M = LoanM0In3M;
    }
    public void setLoanM1In3M(String LoanM1In3M) {
        this.LoanM1In3M = LoanM1In3M;
    }
    public void setLoanM1In6M(String LoanM1In6M) {
        this.LoanM1In6M = LoanM1In6M;
    }
    public void setLoanM2In12M(String LoanM2In12M) {
        this.LoanM2In12M = LoanM2In12M;
    }
    public String getLoanM2In3M() {
        return LoanM2In3M;
    }
    public String getLstSSPayDate() {
        return LstSSPayDate;
    }
    public String getMPSBirthDate() {
        return MPSBirthDate;
    }
    public String getMthlySalary() {
        return MthlySalary;
    }
    public String getNumCCardAct() {
        return NumCCardAct;
    }
    public String getNumCCardOrg() {
        return NumCCardOrg;
    }
    public String getNumOpnActin6M() {
        return NumOpnActin6M;
    }
    public String getOthBankSaveActNum() {
        return OthBankSaveActNum;
    }
    public String getOthCardCnt() {
        return OthCardCnt;
    }
    public String getOtherBankCardSta() {
        return OtherBankCardSta;
    }
    public String getPriCardBirthday() {
        return PriCardBirthday;
    }
    public String getPriCardCurr() {
        return PriCardCurr;
    }
    public void setLoanM2In3M(String LoanM2In3M) {
        this.LoanM2In3M = LoanM2In3M;
    }
    public void setLstSSPayDate(String LstSSPayDate) {
        this.LstSSPayDate = LstSSPayDate;
    }
    public void setMPSBirthDate(String MPSBirthDate) {
        this.MPSBirthDate = MPSBirthDate;
    }
    public void setMthlySalary(String MthlySalary) {
        this.MthlySalary = MthlySalary;
    }
    public void setNumCCardAct(String NumCCardAct) {
        this.NumCCardAct = NumCCardAct;
    }
    public void setNumCCardOrg(String NumCCardOrg) {
        this.NumCCardOrg = NumCCardOrg;
    }
    public void setNumOpnActin6M(String NumOpnActin6M) {
        this.NumOpnActin6M = NumOpnActin6M;
    }
    public void setOthBankSaveActNum(String OthBankSaveActNum) {
        this.OthBankSaveActNum = OthBankSaveActNum;
    }
    public void setOthCardCnt(String OthCardCnt) {
        this.OthCardCnt = OthCardCnt;
    }
    public void setOtherBankCardSta(String OtherBankCardSta) {
        this.OtherBankCardSta = OtherBankCardSta;
    }
    public void setPriCardBirthday(String PriCardBirthday) {
        this.PriCardBirthday = PriCardBirthday;
    }
    public void setPriCardCurr(String PriCardCurr) {
        this.PriCardCurr = PriCardCurr;
    }
    public String getPriEducationLvl() {
        return PriEducationLvl;
    }

    public String getPriHomePtCd() {
        return PriHomePtCd;
    }
    public String getPriIDNo() {
        return PriIDNo;
    }
    public String getPriIDType() {
        return PriIDType;
    }
    public String getPriJCBType() {
        return PriJCBType;
    }
    public String getPriMaritalSta() {
        return PriMaritalSta;
    }
    public String getPrimaryCardNo1() {
        return PrimaryCardNo1;
    }
    public String getPrimaryCardNo2() {
        return PrimaryCardNo2;
    }
    public String getPrimaryCardNo3() {
        return PrimaryCardNo3;
    }
    public String getPriNationality() {
        return PriNationality;
    }
    public String getPriPos() {
        return PriPos;
    }
    public String getPriStatAddrSel() {
        return PriStatAddrSel;
    }
    public void setPriEducationLvl(String PriEducationLvl) {
        this.PriEducationLvl = PriEducationLvl;
    }
    public void setPriHomePtCd(String PriHomePtCd) {
        this.PriHomePtCd = PriHomePtCd;
    }
    public void setPriIDNo(String PriIDNo) {
        this.PriIDNo = PriIDNo;
    }
    public void setPriIDType(String PriIDType) {
        this.PriIDType = PriIDType;
    }
    public void setPriJCBType(String PriJCBType) {
        this.PriJCBType = PriJCBType;
    }
    public void setPriMaritalSta(String PriMaritalSta) {
        this.PriMaritalSta = PriMaritalSta;
    }
    public void setPrimaryCardNo1(String PrimaryCardNo1) {
        this.PrimaryCardNo1 = PrimaryCardNo1;
    }
    public void setPrimaryCardNo2(String PrimaryCardNo2) {
        this.PrimaryCardNo2 = PrimaryCardNo2;
    }
    public void setPrimaryCardNo3(String PrimaryCardNo3) {
        this.PrimaryCardNo3 = PrimaryCardNo3;
    }
    public void setPriNationality(String PriNationality) {
        this.PriNationality = PriNationality;
    }
    public void setPriPos(String PriPos) {
        this.PriPos = PriPos;
    }
    public void setPriStatAddrSel(String PriStatAddrSel) {
        this.PriStatAddrSel = PriStatAddrSel;
    }
    public String getReservedField1() {
        return ReservedField1;
    }
    public String getSuppBirthday1() {
        return SuppBirthday1;
    }
    public String getSuppBirthday2() {
        return SuppBirthday2;
    }
    public String getSuppBirthday3() {
        return SuppBirthday3;
    }
    public String getSuppIDNo1() {
        return SuppIDNo1;
    }
    public String getSuppIDNo2() {
        return SuppIDNo2;
    }
    public String getSuppIDNo3() {
        return SuppIDNo3;
    }
    public String getSuppIDType1() {
        return SuppIDType1;
    }
    public String getSuppIDType2() {
        return SuppIDType2;
    }
    public String getSuppIDType3() {
        return SuppIDType3;
    }
    public String getSupplApplOnly() {
        return SupplApplOnly;
    }


    public void setSupp3Check(String Supp3Check) {
        this.Supp3Check = Supp3Check;
    }

    public void setSupp2Check(String Supp2Check) {
        this.Supp2Check = Supp2Check;
    }

    public void setSupp1Check(String Supp1Check) {
        this.Supp1Check = Supp1Check;
    }

    public void setAprAssetValue3(String AprAssetValue3) {
        this.AprAssetValue3 = AprAssetValue3;
    }

    public void setAprAssetValue2(String AprAssetValue2) {
        this.AprAssetValue2 = AprAssetValue2;
    }

    public void setAprAssetValue1(String AprAssetValue1) {
        this.AprAssetValue1 = AprAssetValue1;
    }

    public void setReservedField1(String ReservedField1) {
        this.ReservedField1 = ReservedField1;
    }
    public void setSuppBirthday1(String SuppBirthday1) {
        this.SuppBirthday1 = SuppBirthday1;
    }
    public void setSuppBirthday2(String SuppBirthday2) {
        this.SuppBirthday2 = SuppBirthday2;
    }
    public void setSuppBirthday3(String SuppBirthday3) {
        this.SuppBirthday3 = SuppBirthday3;
    }
    public void setSuppIDNo1(String SuppIDNo1) {
        this.SuppIDNo1 = SuppIDNo1;
    }
    public void setSuppIDNo2(String SuppIDNo2) {
        this.SuppIDNo2 = SuppIDNo2;
    }
    public void setSuppIDNo3(String SuppIDNo3) {
        this.SuppIDNo3 = SuppIDNo3;
    }
    public void setSuppIDType1(String SuppIDType1) {
        this.SuppIDType1 = SuppIDType1;
    }
    public void setSuppIDType2(String SuppIDType2) {
        this.SuppIDType2 = SuppIDType2;
    }
    public void setSuppIDType3(String SuppIDType3) {
        this.SuppIDType3 = SuppIDType3;
    }
    public void setSupplApplOnly(String SupplApplOnly) {
        this.SupplApplOnly = SupplApplOnly;
    }
    public String getSuppLimitFlag1() {
        return SuppLimitFlag1;
    }
    public String getSuppLimitFlag2() {
        return SuppLimitFlag2;
    }
    public String getSuppLimitFlag3() {
        return SuppLimitFlag3;
    }
    public String getSuppLimitPercent1() {
        return SuppLimitPercent1;
    }
    public String getSuppLimitPercent2() {
        return SuppLimitPercent2;
    }
    public String getSuppLimitPercent3() {
        return SuppLimitPercent3;
    }
    public String getSupplRelationExpl() {
        return SupplRelationExpl;
    }
    public String getSuppRelation1() {
        return SuppRelation1;
    }
    public String getSuppRelation2() {
        return SuppRelation2;
    }
    public String getSuppRelation3() {
        return SuppRelation3;
    }
    public String getSuppSex1() {
        return SuppSex1;
    }
    public String getSuppSex2() {
        return SuppSex2;
    }
    public void setSuppLimitFlag1(String SuppLimitFlag1) {
        this.SuppLimitFlag1 = SuppLimitFlag1;
    }
    public void setSuppLimitFlag2(String SuppLimitFlag2) {
        this.SuppLimitFlag2 = SuppLimitFlag2;
    }
    public void setSuppLimitFlag3(String SuppLimitFlag3) {
        this.SuppLimitFlag3 = SuppLimitFlag3;
    }
    public void setSuppLimitPercent1(String SuppLimitPercent1) {
        this.SuppLimitPercent1 = SuppLimitPercent1;
    }
    public void setSuppLimitPercent2(String SuppLimitPercent2) {
        this.SuppLimitPercent2 = SuppLimitPercent2;
    }
    public void setSuppLimitPercent3(String SuppLimitPercent3) {
        this.SuppLimitPercent3 = SuppLimitPercent3;
    }
    public void setSupplRelationExpl(String SupplRelationExpl) {
        this.SupplRelationExpl = SupplRelationExpl;
    }
    public void setSuppRelation1(String SuppRelation1) {
        this.SuppRelation1 = SuppRelation1;
    }
    public void setSuppRelation2(String SuppRelation2) {
        this.SuppRelation2 = SuppRelation2;
    }
    public void setSuppRelation3(String SuppRelation3) {
        this.SuppRelation3 = SuppRelation3;
    }
    public void setSuppSex1(String SuppSex1) {
        this.SuppSex1 = SuppSex1;
    }
    public void setSuppSex2(String SuppSex2) {
        this.SuppSex2 = SuppSex2;
    }
    public String getSuppSex3() {
        return SuppSex3;
    }
    public String getSuppStatAddrPtCd() {
        return SuppStatAddrPtCd;
    }
    public String getTotlCCardCLmt() {
        return TotlCCardCLmt;
    }
    public String getTotlCCardOLmt() {
        return TotlCCardOLmt;
    }
    public void setTotlCCardOLmt(String TotlCCardOLmt) {
        this.TotlCCardOLmt = TotlCCardOLmt;
    }
    public void setTotlCCardCLmt(String TotlCCardCLmt) {
        this.TotlCCardCLmt = TotlCCardCLmt;
    }
    public void setSuppStatAddrPtCd(String SuppStatAddrPtCd) {
        this.SuppStatAddrPtCd = SuppStatAddrPtCd;
    }
    public void setSuppSex3(String SuppSex3) {
        this.SuppSex3 = SuppSex3;
    }

    /**
     * setPriGender
     *
     * @param string String
     */




    public String getSupp3Check() {
        return Supp3Check;
    }

    public String getSupp2Check() {
        return Supp2Check;
    }

    public String getSupp1Check() {
        return Supp1Check;
    }

    public String getAprAssetValue3() {
        return AprAssetValue3;
    }

    public String getAprAssetValue2() {
        return AprAssetValue2;
    }

    public String getAprAssetValue1() {
        return AprAssetValue1;
    }
	public String getA01001() {
		return A01001;
	}
	public void setA01001(String a01001) {
		A01001 = a01001;
	}
	public String getA01002() {
		return A01002;
	}
	public void setA01002(String a01002) {
		A01002 = a01002;
	}
	public String getA02001() {
		return A02001;
	}
	public void setA02001(String a02001) {
		A02001 = a02001;
	}
	public String getA02002() {
		return A02002;
	}
	public void setA02002(String a02002) {
		A02002 = a02002;
	}
	public String getA03001() {
		return A03001;
	}
	public void setA03001(String a03001) {
		A03001 = a03001;
	}
	public String getA03002() {
		return A03002;
	}
	public void setA03002(String a03002) {
		A03002 = a03002;
	}
	public String getA03003() {
		return A03003;
	}
	public void setA03003(String a03003) {
		A03003 = a03003;
	}
	public String getA04001() {
		return A04001;
	}
	public void setA04001(String a04001) {
		A04001 = a04001;
	}
	public String getA04002() {
		return A04002;
	}
	public void setA04002(String a04002) {
		A04002 = a04002;
	}
	public String getA04003() {
		return A04003;
	}
	public void setA04003(String a04003) {
		A04003 = a04003;
	}
	public String getA05001() {
		return A05001;
	}
	public void setA05001(String a05001) {
		A05001 = a05001;
	}
	public String getA05002() {
		return A05002;
	}
	public void setA05002(String a05002) {
		A05002 = a05002;
	}
	public String getA05003() {
		return A05003;
	}
	public void setA05003(String a05003) {
		A05003 = a05003;
	}
	public String getA05004() {
		return A05004;
	}
	public void setA05004(String a05004) {
		A05004 = a05004;
	}
	public String getA05005() {
		return A05005;
	}
	public void setA05005(String a05005) {
		A05005 = a05005;
	}
	public String getA05006() {
		return A05006;
	}
	public void setA05006(String a05006) {
		A05006 = a05006;
	}
	public String getA05007() {
		return A05007;
	}
	public void setA05007(String a05007) {
		A05007 = a05007;
	}
	public String getA05008() {
		return A05008;
	}
	public void setA05008(String a05008) {
		A05008 = a05008;
	}
	public String getA05009() {
		return A05009;
	}
	public void setA05009(String a05009) {
		A05009 = a05009;
	}
	public String getA05010() {
		return A05010;
	}
	public void setA05010(String a05010) {
		A05010 = a05010;
	}
	public String getA06001() {
		return A06001;
	}
	public void setA06001(String a06001) {
		A06001 = a06001;
	}
	public String getA06002() {
		return A06002;
	}
	public void setA06002(String a06002) {
		A06002 = a06002;
	}
	public String getA06003() {
		return A06003;
	}
	public void setA06003(String a06003) {
		A06003 = a06003;
	}
	public String getA06004() {
		return A06004;
	}
	public void setA06004(String a06004) {
		A06004 = a06004;
	}
	public String getA06005() {
		return A06005;
	}
	public void setA06005(String a06005) {
		A06005 = a06005;
	}
	public String getA06006() {
		return A06006;
	}
	public void setA06006(String a06006) {
		A06006 = a06006;
	}
	public String getA06007() {
		return A06007;
	}
	public void setA06007(String a06007) {
		A06007 = a06007;
	}
	public String getA06008() {
		return A06008;
	}
	public void setA06008(String a06008) {
		A06008 = a06008;
	}
	public String getA06009() {
		return A06009;
	}
	public void setA06009(String a06009) {
		A06009 = a06009;
	}
	public String getA07001() {
		return A07001;
	}
	public void setA07001(String a07001) {
		A07001 = a07001;
	}
	public String getA07002() {
		return A07002;
	}
	public void setA07002(String a07002) {
		A07002 = a07002;
	}
	public String getA07003() {
		return A07003;
	}
	public void setA07003(String a07003) {
		A07003 = a07003;
	}
	public String getA07004() {
		return A07004;
	}
	public void setA07004(String a07004) {
		A07004 = a07004;
	}
	public String getA08001() {
		return A08001;
	}
	public void setA08001(String a08001) {
		A08001 = a08001;
	}
	public String getA08002() {
		return A08002;
	}
	public void setA08002(String a08002) {
		A08002 = a08002;
	}
	public String getA08003() {
		return A08003;
	}
	public void setA08003(String a08003) {
		A08003 = a08003;
	}
	public String getA08004() {
		return A08004;
	}
	public void setA08004(String a08004) {
		A08004 = a08004;
	}
	public String getA08005() {
		return A08005;
	}
	public void setA08005(String a08005) {
		A08005 = a08005;
	}
	public String getA08006() {
		return A08006;
	}
	public void setA08006(String a08006) {
		A08006 = a08006;
	}
	public String getA08007() {
		return A08007;
	}
	public void setA08007(String a08007) {
		A08007 = a08007;
	}
	public String getA08008() {
		return A08008;
	}
	public void setA08008(String a08008) {
		A08008 = a08008;
	}
	public String getA08009() {
		return A08009;
	}
	public void setA08009(String a08009) {
		A08009 = a08009;
	}
	public String getA08010() {
		return A08010;
	}
	public void setA08010(String a08010) {
		A08010 = a08010;
	}
	public String getA08011() {
		return A08011;
	}
	public void setA08011(String a08011) {
		A08011 = a08011;
	}
	public String getA08012() {
		return A08012;
	}
	public void setA08012(String a08012) {
		A08012 = a08012;
	}
	public String getA08013() {
		return A08013;
	}
	public void setA08013(String a08013) {
		A08013 = a08013;
	}
	public String getA08014() {
		return A08014;
	}
	public void setA08014(String a08014) {
		A08014 = a08014;
	}
	public String getA09001() {
		return A09001;
	}
	public void setA09001(String a09001) {
		A09001 = a09001;
	}
	public String getA09002() {
		return A09002;
	}
	public void setA09002(String a09002) {
		A09002 = a09002;
	}
	public String getA09003() {
		return A09003;
	}
	public void setA09003(String a09003) {
		A09003 = a09003;
	}
	public String getA09004() {
		return A09004;
	}
	public void setA09004(String a09004) {
		A09004 = a09004;
	}
	public String getA09005() {
		return A09005;
	}
	public void setA09005(String a09005) {
		A09005 = a09005;
	}
	public String getA09006() {
		return A09006;
	}
	public void setA09006(String a09006) {
		A09006 = a09006;
	}
	public String getA09007() {
		return A09007;
	}
	public void setA09007(String a09007) {
		A09007 = a09007;
	}
	public String getA09008() {
		return A09008;
	}
	public void setA09008(String a09008) {
		A09008 = a09008;
	}
	public String getA09009() {
		return A09009;
	}
	public void setA09009(String a09009) {
		A09009 = a09009;
	}
	public String getA09010() {
		return A09010;
	}
	public void setA09010(String a09010) {
		A09010 = a09010;
	}
	public String getA09011() {
		return A09011;
	}
	public void setA09011(String a09011) {
		A09011 = a09011;
	}
	public String getA09012() {
		return A09012;
	}
	public void setA09012(String a09012) {
		A09012 = a09012;
	}
	public String getA09013() {
		return A09013;
	}
	public void setA09013(String a09013) {
		A09013 = a09013;
	}
	public String getA09014() {
		return A09014;
	}
	public void setA09014(String a09014) {
		A09014 = a09014;
	}
	public String getA09015() {
		return A09015;
	}
	public void setA09015(String a09015) {
		A09015 = a09015;
	}
	public String getA09016() {
		return A09016;
	}
	public void setA09016(String a09016) {
		A09016 = a09016;
	}
	public String getA09017() {
		return A09017;
	}
	public void setA09017(String a09017) {
		A09017 = a09017;
	}
	public String getA09018() {
		return A09018;
	}
	public void setA09018(String a09018) {
		A09018 = a09018;
	}
	public String getA09019() {
		return A09019;
	}
	public void setA09019(String a09019) {
		A09019 = a09019;
	}
	public String getA09020() {
		return A09020;
	}
	public void setA09020(String a09020) {
		A09020 = a09020;
	}
	public String getA09021() {
		return A09021;
	}
	public void setA09021(String a09021) {
		A09021 = a09021;
	}
	public String getA09022() {
		return A09022;
	}
	public void setA09022(String a09022) {
		A09022 = a09022;
	}
	public String getA09023() {
		return A09023;
	}
	public void setA09023(String a09023) {
		A09023 = a09023;
	}
	public String getA09024() {
		return A09024;
	}
	public void setA09024(String a09024) {
		A09024 = a09024;
	}
	public String getA09025() {
		return A09025;
	}
	public void setA09025(String a09025) {
		A09025 = a09025;
	}
	public String getA09026() {
		return A09026;
	}
	public void setA09026(String a09026) {
		A09026 = a09026;
	}
	public String getA09027() {
		return A09027;
	}
	public void setA09027(String a09027) {
		A09027 = a09027;
	}
	public String getA09028() {
		return A09028;
	}
	public void setA09028(String a09028) {
		A09028 = a09028;
	}
	public String getA09029() {
		return A09029;
	}
	public void setA09029(String a09029) {
		A09029 = a09029;
	}
	public String getA09030() {
		return A09030;
	}
	public void setA09030(String a09030) {
		A09030 = a09030;
	}
	public String getA10001() {
		return A10001;
	}
	public void setA10001(String a10001) {
		A10001 = a10001;
	}
	public String getA10002() {
		return A10002;
	}
	public void setA10002(String a10002) {
		A10002 = a10002;
	}
	public String getA10003() {
		return A10003;
	}
	public void setA10003(String a10003) {
		A10003 = a10003;
	}
	public String getA10004() {
		return A10004;
	}
	public void setA10004(String a10004) {
		A10004 = a10004;
	}
	public String getA10005() {
		return A10005;
	}
	public void setA10005(String a10005) {
		A10005 = a10005;
	}
	public String getA10006() {
		return A10006;
	}
	public void setA10006(String a10006) {
		A10006 = a10006;
	}
	public String getA10007() {
		return A10007;
	}
	public void setA10007(String a10007) {
		A10007 = a10007;
	}
	public String getA10008() {
		return A10008;
	}
	public void setA10008(String a10008) {
		A10008 = a10008;
	}
	public String getA10009() {
		return A10009;
	}
	public void setA10009(String a10009) {
		A10009 = a10009;
	}
	public String getA10010() {
		return A10010;
	}
	public void setA10010(String a10010) {
		A10010 = a10010;
	}
	public String getA10011() {
		return A10011;
	}
	public void setA10011(String a10011) {
		A10011 = a10011;
	}
	public String getA10012() {
		return A10012;
	}
	public void setA10012(String a10012) {
		A10012 = a10012;
	}
	public String getA10013() {
		return A10013;
	}
	public void setA10013(String a10013) {
		A10013 = a10013;
	}
	public String getA10014() {
		return A10014;
	}
	public void setA10014(String a10014) {
		A10014 = a10014;
	}
	public String getA11001() {
		return A11001;
	}
	public void setA11001(String a11001) {
		A11001 = a11001;
	}
	public String getA11002() {
		return A11002;
	}
	public void setA11002(String a11002) {
		A11002 = a11002;
	}
	public String getA11003() {
		return A11003;
	}
	public void setA11003(String a11003) {
		A11003 = a11003;
	}
	public String getA11004() {
		return A11004;
	}
	public void setA11004(String a11004) {
		A11004 = a11004;
	}
	public String getA11005() {
		return A11005;
	}
	public void setA11005(String a11005) {
		A11005 = a11005;
	}
	public String getA11006() {
		return A11006;
	}
	public void setA11006(String a11006) {
		A11006 = a11006;
	}
	public String getA11007() {
		return A11007;
	}
	public void setA11007(String a11007) {
		A11007 = a11007;
	}
	public String getA11008() {
		return A11008;
	}
	public void setA11008(String a11008) {
		A11008 = a11008;
	}
	public String getA11009() {
		return A11009;
	}
	public void setA11009(String a11009) {
		A11009 = a11009;
	}
	public String getA11010() {
		return A11010;
	}
	public void setA11010(String a11010) {
		A11010 = a11010;
	}
	public String getA11011() {
		return A11011;
	}
	public void setA11011(String a11011) {
		A11011 = a11011;
	}
	public String getA11012() {
		return A11012;
	}
	public void setA11012(String a11012) {
		A11012 = a11012;
	}
	public String getA11013() {
		return A11013;
	}
	public void setA11013(String a11013) {
		A11013 = a11013;
	}
	public String getA11014() {
		return A11014;
	}
	public void setA11014(String a11014) {
		A11014 = a11014;
	}
	public String getA11015() {
		return A11015;
	}
	public void setA11015(String a11015) {
		A11015 = a11015;
	}
	public String getA11016() {
		return A11016;
	}
	public void setA11016(String a11016) {
		A11016 = a11016;
	}
	public String getA11017() {
		return A11017;
	}
	public void setA11017(String a11017) {
		A11017 = a11017;
	}
	public String getA11018() {
		return A11018;
	}
	public void setA11018(String a11018) {
		A11018 = a11018;
	}
	public String getA11019() {
		return A11019;
	}
	public void setA11019(String a11019) {
		A11019 = a11019;
	}
	public String getA11020() {
		return A11020;
	}
	public void setA11020(String a11020) {
		A11020 = a11020;
	}
	public String getA11021() {
		return A11021;
	}
	public void setA11021(String a11021) {
		A11021 = a11021;
	}
	public String getA11022() {
		return A11022;
	}
	public void setA11022(String a11022) {
		A11022 = a11022;
	}
	public String getA11023() {
		return A11023;
	}
	public void setA11023(String a11023) {
		A11023 = a11023;
	}
	public String getA11024() {
		return A11024;
	}
	public void setA11024(String a11024) {
		A11024 = a11024;
	}
	public String getA11025() {
		return A11025;
	}
	public void setA11025(String a11025) {
		A11025 = a11025;
	}
	public String getA11026() {
		return A11026;
	}
	public void setA11026(String a11026) {
		A11026 = a11026;
	}
	public String getA11027() {
		return A11027;
	}
	public void setA11027(String a11027) {
		A11027 = a11027;
	}
	public String getA11028() {
		return A11028;
	}
	public void setA11028(String a11028) {
		A11028 = a11028;
	}
	public String getA11029() {
		return A11029;
	}
	public void setA11029(String a11029) {
		A11029 = a11029;
	}
	public String getA11030() {
		return A11030;
	}
	public void setA11030(String a11030) {
		A11030 = a11030;
	}
	public String getA12001() {
		return A12001;
	}
	public void setA12001(String a12001) {
		A12001 = a12001;
	}
	public String getA12002() {
		return A12002;
	}
	public void setA12002(String a12002) {
		A12002 = a12002;
	}
	public String getA12003() {
		return A12003;
	}
	public void setA12003(String a12003) {
		A12003 = a12003;
	}
	public String getA12004() {
		return A12004;
	}
	public void setA12004(String a12004) {
		A12004 = a12004;
	}
	public String getA12005() {
		return A12005;
	}
	public void setA12005(String a12005) {
		A12005 = a12005;
	}
	public String getA12006() {
		return A12006;
	}
	public void setA12006(String a12006) {
		A12006 = a12006;
	}
	public String getA12007() {
		return A12007;
	}
	public void setA12007(String a12007) {
		A12007 = a12007;
	}
	public String getA12008() {
		return A12008;
	}
	public void setA12008(String a12008) {
		A12008 = a12008;
	}
	public String getA12009() {
		return A12009;
	}
	public void setA12009(String a12009) {
		A12009 = a12009;
	}
	public String getA12010() {
		return A12010;
	}
	public void setA12010(String a12010) {
		A12010 = a12010;
	}
	public String getA13001() {
		return A13001;
	}
	public void setA13001(String a13001) {
		A13001 = a13001;
	}
	public String getA13002() {
		return A13002;
	}
	public void setA13002(String a13002) {
		A13002 = a13002;
	}
	public String getA13003() {
		return A13003;
	}
	public void setA13003(String a13003) {
		A13003 = a13003;
	}
	public String getA13004() {
		return A13004;
	}
	public void setA13004(String a13004) {
		A13004 = a13004;
	}
	public String getA13005() {
		return A13005;
	}
	public void setA13005(String a13005) {
		A13005 = a13005;
	}
	public String getA13006() {
		return A13006;
	}
	public void setA13006(String a13006) {
		A13006 = a13006;
	}
	public String getA13007() {
		return A13007;
	}
	public void setA13007(String a13007) {
		A13007 = a13007;
	}
	public String getA13008() {
		return A13008;
	}
	public void setA13008(String a13008) {
		A13008 = a13008;
	}
	public String getA13009() {
		return A13009;
	}
	public void setA13009(String a13009) {
		A13009 = a13009;
	}
	public String getA13010() {
		return A13010;
	}
	public void setA13010(String a13010) {
		A13010 = a13010;
	}
	public String getA13011() {
		return A13011;
	}
	public void setA13011(String a13011) {
		A13011 = a13011;
	}
	public String getA13012() {
		return A13012;
	}
	public void setA13012(String a13012) {
		A13012 = a13012;
	}
	public String getA13013() {
		return A13013;
	}
	public void setA13013(String a13013) {
		A13013 = a13013;
	}
	public String getA13014() {
		return A13014;
	}
	public void setA13014(String a13014) {
		A13014 = a13014;
	}
	public String getA13015() {
		return A13015;
	}
	public void setA13015(String a13015) {
		A13015 = a13015;
	}
	public String getA13016() {
		return A13016;
	}
	public void setA13016(String a13016) {
		A13016 = a13016;
	}
	public String getA13017() {
		return A13017;
	}
	public void setA13017(String a13017) {
		A13017 = a13017;
	}
	public String getA13018() {
		return A13018;
	}
	public void setA13018(String a13018) {
		A13018 = a13018;
	}
	public String getA13019() {
		return A13019;
	}
	public void setA13019(String a13019) {
		A13019 = a13019;
	}
	public String getA13020() {
		return A13020;
	}
	public void setA13020(String a13020) {
		A13020 = a13020;
	}
	public String getA13021() {
		return A13021;
	}
	public void setA13021(String a13021) {
		A13021 = a13021;
	}
	public String getA13022() {
		return A13022;
	}
	public void setA13022(String a13022) {
		A13022 = a13022;
	}
	public String getA13023() {
		return A13023;
	}
	public void setA13023(String a13023) {
		A13023 = a13023;
	}
	public String getA13024() {
		return A13024;
	}
	public void setA13024(String a13024) {
		A13024 = a13024;
	}
	public String getA13025() {
		return A13025;
	}
	public void setA13025(String a13025) {
		A13025 = a13025;
	}
	public String getA13026() {
		return A13026;
	}
	public void setA13026(String a13026) {
		A13026 = a13026;
	}
	public String getA13027() {
		return A13027;
	}
	public void setA13027(String a13027) {
		A13027 = a13027;
	}
	public String getA13028() {
		return A13028;
	}
	public void setA13028(String a13028) {
		A13028 = a13028;
	}
	public String getA13029() {
		return A13029;
	}
	public void setA13029(String a13029) {
		A13029 = a13029;
	}
	public String getA13030() {
		return A13030;
	}
	public void setA13030(String a13030) {
		A13030 = a13030;
	}
	public String getA14001() {
		return A14001;
	}
	public void setA14001(String a14001) {
		A14001 = a14001;
	}
	public String getA14002() {
		return A14002;
	}
	public void setA14002(String a14002) {
		A14002 = a14002;
	}
	public String getA15001() {
		return A15001;
	}
	public void setA15001(String a15001) {
		A15001 = a15001;
	}
	public String getA15002() {
		return A15002;
	}
	public void setA15002(String a15002) {
		A15002 = a15002;
	}
	public String getA15003() {
		return A15003;
	}
	public void setA15003(String a15003) {
		A15003 = a15003;
	}
	public String getA15004() {
		return A15004;
	}
	public void setA15004(String a15004) {
		A15004 = a15004;
	}
	public String getA15005() {
		return A15005;
	}
	public void setA15005(String a15005) {
		A15005 = a15005;
	}
	public String getA15006() {
		return A15006;
	}
	public void setA15006(String a15006) {
		A15006 = a15006;
	}
	public String getA15007() {
		return A15007;
	}
	public void setA15007(String a15007) {
		A15007 = a15007;
	}
	public String getA15012() {
		return A15012;
	}
	public void setA15012(String a15012) {
		A15012 = a15012;
	}
	public String getA15013() {
		return A15013;
	}
	public void setA15013(String a15013) {
		A15013 = a15013;
	}
	public String getA15015() {
		return A15015;
	}
	public void setA15015(String a15015) {
		A15015 = a15015;
	}
	public String getA15016() {
		return A15016;
	}
	public void setA15016(String a15016) {
		A15016 = a15016;
	}
	public String getA15017() {
		return A15017;
	}
	public void setA15017(String a15017) {
		A15017 = a15017;
	}
	public String getA15018() {
		return A15018;
	}
	public void setA15018(String a15018) {
		A15018 = a15018;
	}
	public String getA15033() {
		return A15033;
	}
	public void setA15033(String a15033) {
		A15033 = a15033;
	}
	public String getAT4300() {
		return AT4300;
	}
	public void setAT4300(String at4300) {
		AT4300 = at4300;
	}
	public String getA12011() {
		return A12011;
	}
	public void setA12011(String a12011) {
		A12011 = a12011;
	}
	public String getAT4320() {
		return AT4320;
	}
	public void setAT4320(String at4320) {
		AT4320 = at4320;
	}
	public String getA08015() {
		return A08015;
	}
	public void setA08015(String a08015) {
		A08015 = a08015;
	}
	public String getA10015() {
		return A10015;
	}
	public void setA10015(String a10015) {
		A10015 = a10015;
	}
	public String getA08016() {
		return A08016;
	}
	public void setA08016(String a08016) {
		A08016 = a08016;
	}
	public String getA10016() {
		return A10016;
	}
	public void setA10016(String a10016) {
		A10016 = a10016;
	}
	public String getA12012() {
		return A12012;
	}
	public void setA12012(String a12012) {
		A12012 = a12012;
	}
	public String getAT4330() {
		return AT4330;
	}
	public void setAT4330(String at4330) {
		AT4330 = at4330;
	}

}
