package com.boc.cdse;

import java.text.*;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;

/**
 * <p>
 * 作为“数据处理”功能的PersonalCreditCardAttributes类
 * <code>{@link PersonalCreditCardAttributes}</code>类用于 提供对外部变量的数据处理及计算（
 * <code>{@link # caculateAllAttributs}</code>、
 * <code>{@link # createDocument}</code>、<code>{@link # getAge}</code> 、
 * <code>{@link # getCurrentMonth}</code>、 <code>{@link # getCurrentYear}</code>、 <code>{@link # getMonthes}</code>、 <code>{@link # randIntGenerator}</code>
 * 方法） 和外部变量取值（<code>{@link # getAttributs }</code>、
 * <code>{@link # getCardType }</code>、 <code>{@link # getCardGrade }</code>、
 * <code>{@link # getCardLevel }</code>、 <code>{@link # getIsDownSell }</code>、
 * <code>{@link # getRandNum }</code>、 <code>{@link # getDocument }</code>方法）
 * 请求的逻辑处理和控制。
 * </p>
 * 
 * <p>
 * Copyright: 版权 (c) 2002
 * </p>
 * <p>
 * Company: 首航财务管理有限公司
 * </p>
 * 
 * @author: CDSE项目组
 * @version 1.0
 * @see com.entity.VariableBean
 */

public class PersonalCreditCardAttributes extends Attributes {
	private java.util.ArrayList supplData = new java.util.ArrayList();
	private Document document = null;
	private ArrayList attachVariableA = null;
	private boolean isDownSell = false;
	private boolean isQccCardApp = false;
	private boolean isPlatinaCardApp = false;
	private boolean isDutyCardApp = false;
	private boolean isSupplApplOnly = false;
	private boolean isSupplAppl = false;
	private int cardType = 0;
	private int cardGrade = 0;
	private int cardLevel = 0;
	private int randomNum = 0;
	private String cofirmingDate = "";
	private String ApplicationId = "";
	private String applProductCd = "";
	// private String warningInfo=null;

	/** 错误号 */
	private String errorCode = "";

	/** 新增两个参数变量 */
	private double exchangeRate = 0.125f; // 人民币兑美元汇率，需要从适当的途径获取
	private double supplLtdRatio = 1.0f; // 主卡额度向附属卡额度的转换率，需要计算

	/** 最大日期 */
	public final static int maxDate = 29991231;
	/** 最小日期 */
	public final static int minDate = 18991231;
	/** 空日期(使用最小日期)-字符串型 */
	public final static String strNull = "18991231";
	/** SQL空日期(使用最小日期, 已有单引号)-字符串型 */
	public final static String strSqlNull = "'18991231'";

	/** 月份-1月份 */
	public final static int monthJanuary = 1;
	/** 月份-2月份 */
	public final static int monthFebruary = 2;
	/** 月份-3月份 */
	public final static int monthMarch = 3;
	/** 月份-4月份 */
	public final static int monthApril = 4;
	/** 月份-5月份 */
	public final static int monthMay = 5;
	/** 月份-6月份 */
	public final static int monthJune = 6;
	/** 月份-7月份 */
	public final static int monthJuly = 7;
	/** 月份-8月份 */
	public final static int monthAugust = 8;
	/** 月份-9月份 */
	public final static int monthSeptember = 9;
	/** 月份-10月份 */
	public final static int monthOctober = 10;
	/** 月份-11月份 */
	public final static int monthNovember = 11;
	/** 月份-12月份 */
	public final static int monthDecember = 12;
	private int supplNum = 0;

	public Object clone() {
		try {
			PersonalCreditCardAttributes clonedPCCAttributes = new PersonalCreditCardAttributes();
			clonedPCCAttributes.setDocument((Document) this.document
					.cloneNode(true));
			clonedPCCAttributes.setAttachVariableA(this.attachVariableA);
			clonedPCCAttributes.setIsDownSell(this.isDownSell);
			clonedPCCAttributes.setIsSupplAppl(this.isIsSupplAppl());
			clonedPCCAttributes.setSupplApplOnly(this.isSupplApplOnly);
			clonedPCCAttributes.setCardType(this.cardType);
			clonedPCCAttributes.setCardGrade(this.cardGrade);
			clonedPCCAttributes.setCardLevel(this.cardLevel);
			clonedPCCAttributes.setRandomNum(this.randomNum);
			clonedPCCAttributes.setCofirmingDate(this.cofirmingDate);
			clonedPCCAttributes.setErrorCode(this.errorCode);
			clonedPCCAttributes.setExchangeRate(this.exchangeRate);
			clonedPCCAttributes.setSupplLtdRatio(this.supplLtdRatio);
			clonedPCCAttributes.setSupplData(this.supplData);
			clonedPCCAttributes.setSupplNum(this.supplNum);
			clonedPCCAttributes.setIsQccCardApp(this.isQccCardApp);
			clonedPCCAttributes.setIsDutyCardApp(this.isDutyCardApp);
			clonedPCCAttributes.setIsPlatinaCardApp(this.isPlatinaCardApp);

			return clonedPCCAttributes;
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		}
	}

	PersonalCreditCardAttributes() {
		try {
			jbInit();
		} catch (Exception ex) {

			ex.printStackTrace();
		}
	}

	PersonalCreditCardAttributes(Document appForm, Log log)
			throws CDSEException {
		if (appForm == null) {
			errorCode = "9000";
			throw new CDSEException(errorCode);
		}
		// this.logRowVariable(appForm, log);
	}

	/**
	 * 原始变量的数据缺失处理和外部变量的计算。
	 * 
	 * @param appForm
	 *            包含申请原始数据的Document 实例
	 * @param log
	 *            日志实例
	 * @throws CDSEException
	 *             如果异常出现
	 */

	public void caculateAllAttributs(Document appForm, Log log)
			throws CDSEException {
		// ArrayList attachVariable = null;
		// String fldName = "";
		// String fldType = "";
		// String fldSrc = "";
		// String fldDsc = "";
		// String value = "";

		try {
			Document doc = appForm;

			Element basic = doc.getDocumentElement();
			Node data = basic.getFirstChild();
			Element variable = (Element) data.getFirstChild();
			// attachVariable = new ArrayList();
			attachVariableA = new ArrayList();

			VariableBean tempVariable = null;

			// 取得系统日期
			SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyyMMdd");
			Date date = new Date();
			cofirmingDate = bartDateFormat.format(date);

			String appId = variable.getAttribute("AppId").trim();

			// if ("".equals(appId)) {
			// this.errorCode = "RR050:申请号缺失或异常，交人工处理。";
			// throw new CDSEException(errorCode);
			// } else {
			this.ApplicationId = appId;
			// }
			// 是否纯粹附属卡
			String AT0020 = "-9";
			String attrString = variable.getAttribute("SupplApplOnly").trim();
			/**C1507封版后，统一改为判断1和Y*/
			if ("Y".equalsIgnoreCase(attrString) || "1".equalsIgnoreCase(attrString)) {
				AT0020 = "1";
				isSupplApplOnly = true;
			} else {
				AT0020 = "0";
				isSupplApplOnly = false;
			}
			tempVariable = new VariableBean("AT0020", AT0020, "N", "EX",
					"纯粹附属卡");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0020", AT0020);
			// 申请产品编号
			String AT0040 = "-9";
			attrString = variable.getAttribute("ProductCd").trim();
			/*
			 * String[] valueArray = new String[] { "1001", "1002", "1004",
			 * "1005", "1009", "1010", "1012", "1013", "2001", "2002", "2004",
			 * "2005", "2009", "2010", "2012", "2013", "4C02", "4C01", "2O01",
			 * "2O02", "2O03", "2O04", "2O05", "2L01", "VZ01", "4PK1", "4J01",
			 * "4001", "4002", "2JY1", "3002", "3001", "3004", "3003", "1CG1",
			 * "3DZ1", "3DZ2", "2CZ1", "1003", "1011", "1006", "1015", "4005",
			 * "4007", "4006", "4008", "4CA1", "4CA2", "4CA3", "4CA4", "4CA5",
			 * "4CA6", "4CA7", "4CA8", "4CA9", "4CA0", "1CT1", "1CT2", "4U01",
			 * "1R01", "1R02", "1R03", "1R04", "1R05", "1R06", "1WY1", "3R01",
			 * "3R02", "3R03", "3R04", "3R05", "3R06", "4AW1", "4AW2", "4AW3",
			 * "4L01", "4L02", "4L03", "4L04", "4L05", "4L06", "4L07", "4L08",
			 * "4L09", "4L10", "4L11", "4L12", "4L13", "4L14", "4L15", "4L16",
			 * "4L17", "4L18", "4L19", "4L20", "4OV1", "4R01", "4R02", "4R03",
			 * "4R04", "4R05", "4R06", "4U02", "4U03", "4U04", "4U05", "4U06",
			 * "4U07", "4U08", "4U09", "4U10", "4U11", "4U12", "4U13", "4U14",
			 * "4U15", "4U16", "4U17", "4U18", "4U19", "4U20", "2007", "2008",
			 * "1007", "1008", "2P03", "1P03", "1P02", "1P01", "2P02", "2P01",
			 * "4101", "4102", "4103", "4104", "4105", "4106", "4107", "4108",
			 * "4109", "4110", "4111", "4112", "4113", "4114", "4115", "4116",
			 * "4117", "4118", "4119", "4120", "4121", "4122", "4123", "4124",
			 * "4125", "4126", "4127", "4128", "4129", "4130", "4131", "4132",
			 * "4133", "4134", "4135", "4136", "4137", "4138", "4139", "4140",
			 * "4201", "4202", "4203", "4204", "4205", "4206", "4207", "4208",
			 * "4209", "4210", "4211", "4212", "4213", "4214", "4215", "4216",
			 * "4217", "4218", "4219", "4220", "4221", "4222", "4223", "4224",
			 * "4225", "4226", "4227", "4228", "4229", "4230", "4231", "4232",
			 * "4233", "4234", "4235", "4236", "4237", "4238", "4239", "4240",
			 * "4CM1", "4CM2", "2ZF1", "2LY1", "2R01", "2R02", "2R03", "2R04",
			 * "2R05", "2R06", "2R07", "2R08", "2R09", "2R10", "2R11", "2R12",
			 * "2R13", "2R14", "2R15", "2R16", "2R17", "2R18", "2R19", "2R20",
			 * "2R21", "2R22", "2R23", "2R24", "2R25", "2R26", "2R27", "2R28",
			 * "2R29", "2R30", "1CZ1", "1CZ2", "4HN1", "4HN2", "1SZ1", "1SZ2",
			 * "4MN1", "4MN2", "4050", "G001", "G002", "G003", "G004", "G005",
			 * "2C01", "2C02", "2C03", "2C04", "2C05", "2C06", "2C07", "2C08",
			 * "2C09", "2C10", "2C11", "2C12", "2C13", "2C14", "2C15", "2C16",
			 * "2C17", "2C18", "2C19", "2C20", "2C21", "2C22", "2C23", "2C24",
			 * "2C25", "2C26", "2C27", "2C28", "2C29", "2C30", "2C31", "2C32",
			 * "2C33", "2C34", "2C35", "2C36", "2C37", "2C38", "2C39", "2C40",
			 * "2C41", "2C42", "2C43", "2C44", "2C45", "2C46", "2C47", "2C48",
			 * "2C49", "2C50", "2C51", "2C52", "2C53", "2C54", "2C55", "2C56",
			 * "2C57", "2C58", "2C59", "2C60", "2C61", "2C62", "2C63", "2C64",
			 * "2C65", "2C66", "2C67", "2C68", "2C69", "2C70", "2C71", "2C72",
			 * "2C73", "2C74", "2C75", "2C76", "2C77", "2C78", "2C79", "2C80",
			 * "2C81", "2C82", "2C83", "2C84", "2C85", "2C86", "2C87", "2C88",
			 * "2C89", "2C90", "2C91", "2C92", "2C93", "2C94", "2C95", "2C96",
			 * "2C97", "2C98", "2C99", "1R19", "1R20", "4BW1", "4BW2", "4BW3",
			 * "4BW4", "4LC1", "1R11", "1R12", "4SG1", "4SG2", "4SG3", "4SG4",
			 * "4SG5", "3005", "3006", "1PL4", "4R08", "4R09", "4F01", "4F02",
			 * "4S01", "4X01", "4XE1", "4XA1", "4XA2", "1PL1","1WY2","1OV1"
			 * ,"4CR1", "4R07","1R14","1PL3","1PL2","4T01","4T02","4T03" ,
			 * "4T04","4T05","4T06","4SP1","4WK3","4AW4","1R13","CXA1"};
			 * System.out.println("valueArray"+valueArray.length); String[]
			 * qccCardArray = new String[] { "1003", "1011", "1006", "1015",
			 * "4005", "4007", "4006", "4008" }; //
			 * System.out.println("valueArray"+valueArray.length);
			 * 
			 * String[] dutyCardArray = new String[] { "2007", "2008", "1007",
			 * "1008", "4101", "4102", "4103", "4104", "4105", "4106", "4107",
			 * "4108", "4109", "4110", "4111", "4112", "4113", "4114", "4115",
			 * "4116", "4117", "4118", "4119", "4120", "4121", "4122", "4123",
			 * "4124", "4125", "4126", "4127", "4128", "4129", "4130", "4131",
			 * "4132", "4133", "4134", "4135", "4136", "4137", "4138", "4139",
			 * "4140", "4201", "4202 ", "4203", "4204", "4205", "4206", "4207",
			 * "4208", "4209", "4210", "4211", "4212", "4213", "4214", "4215",
			 * "4216", "4217", "4218", "4219", "4220", "4221", "4222", "4223",
			 * "4224", "4225", "4226", "4227", "4228", "4229", "4230", "4231",
			 * "4232", "4233", "4234", "4235", "4236", "4237", "4238", "4239",
			 * "4240", "4050" };
			 * System.out.println("dutyCardArray"+dutyCardArray.length);
			 * 
			 * String[] normalCardArray = new String[] { "1001", "1002", "1004",
			 * "1005", "1009", "1010", "1012", "1013", "2001", "2002", "2004",
			 * "2005", "2009", "2010", "2012", "2013", "4C02", "4C01", "1003",
			 * "1011", "1006", "1015", "4005", "4007", "4006", "4008", "1CT1",
			 * "1CT2", "4AW1", "4AW2", "4AW3", "4L01", "4L02", "4L03", "4L04",
			 * "4L05", "4L06", "4L07", "4L08", "4L09", "4L10", "4L11", "4L12",
			 * "4L13", "4L14", "4L15", "4L16", "4L17", "4L18", "4L19", "4L20",
			 * "G001", "G002", "G003", "G004", "G005", "2C01", "2C02", "2C03",
			 * "2C04", "2C05", "2C06", "2C07", "2C08", "2C09", "2C10", "2C11",
			 * "2C12", "2C13", "2C14", "2C15", "2C16", "2C17", "2C18", "2C19",
			 * "2C20", "2C21", "2C22", "2C23", "2C24", "2C25", "2C26", "2C27",
			 * "2C28", "2C29", "2C30", "2C31", "2C32", "2C33", "2C34", "2C35",
			 * "2C36", "2C37", "2C38", "2C39", "2C40", "2C41", "2C42", "2C43",
			 * "2C44", "2C45", "2C46", "2C47", "2C48", "2C49", "2C50", "2C51",
			 * "2C52", "2C53", "2C54", "2C55", "2C56", "2C57", "2C58", "2C59",
			 * "2C60", "2C61", "2C62", "2C63", "2C64", "2C65", "2C66", "2C67",
			 * "2C68", "2C69", "2C70", "2C71", "2C72", "2C73", "2C74", "2C75",
			 * "2C76", "2C77", "2C78", "2C79", "2C80", "2C81", "2C82", "2C83",
			 * "2C84", "2C85", "2C86", "2C87", "2C88", "2C89", "2C90", "2C91",
			 * "2C92", "2C93", "2C94", "2C95", "2C96", "2C97", "2C98", "2C99",
			 * "2R02", "2R04", "2R05", "2R06", "2R08", "2R09", "2R10", "2R13",
			 * "2R14", "2R15", "2R16", "2R19", "2R22", "2R24", "2R25", "4R01",
			 * "4R02", "4R03", "4R05", "1R05", "1SZ1", "1SZ2", "4SG1", "4SG2",
			 * "4SG3", "4SG4", "4SG5", "1R06", "1R19", "1R20", "4BW1", "4BW2",
			 * "4BW3", "4BW4", "4CM1", "4CM2", "4LC1", "4U02", "1R11", "1R12",
			 * "2O01", "2O02", "2O03", "2O04", "2O05", "1PL4", "4R08", "4R09",
			 * "4F01", "4F02", "4S01", "4X01", "4XE1", "4XA1", "4XA2",
			 * "1PL1","1WY2" ,"4CR1",
			 * "4R07","1R14","1PL3","1PL2","4T01","4T02","4T03" ,
			 * "4T04","4T05","4T06","4SP1","4WK3","1P01" ,"4AW4","1R13","CXA1"
			 * 
			 * }; System.out.println("normalCardArray"+normalCardArray.length);
			 * 
			 * String[] oLikeCardArray = new String[] { "VZ01", "4PK1", "4J01",
			 * "4001", "4002", "2JY1", "1CG1", "2CZ1", "4CA1", "4CA2", "4CA3",
			 * "4CA4", "4CA5", "4CA6", "4CA7", "4CA8", "4CA9", "4CA0", "4U01",
			 * "1R01", "1R02", "1R03", "1R04", "1WY1", "4OV1", "4R04", "4R06",
			 * "4U03", "4U04", "4U05", "4U06", "4U07", "4U08", "4U09", "4U10",
			 * "4U11", "4U12", "4U13", "4U14", "4U15", "4U16", "4U17", "4U18",
			 * "4U19", "4U20", "4HN1", "2ZF1", "2LY1", "2R01", "2R03", "2R07",
			 * "2R11", "2R12", "2R17", "2R18", "2R20", "2R21", "2R23", "2R26",
			 * "2R27", "2R28", "2R29", "2R30", "1CZ1", "1CZ2", "4HN2", "4MN1",
			 * "4MN2" ,"1OV1"};
			 * System.out.println("oLikeCardArray"+oLikeCardArray.length);
			 * 
			 * String[] jcbCardArray = new String[] { "3002", "3001", "3004",
			 * "3003", "3005", "3006", "3DZ1", "3DZ2", "3R01", "3R02", "3R03",
			 * "3R04", "3R05", "3R06", "2L01" };
			 * System.out.println("jcbCardArray"+jcbCardArray.length); String[]
			 * silverCardArray = new String[] { "1001", "1004", "1009", "1012",
			 * "2001", "2004", "2009", "2012", "4C02", "3002", "3004", "3006",
			 * "3DZ2", "1003", "1006", "4005", "4006", "1CT2", "4AW3", "4CM2",
			 * "1CZ2", "4HN2", "1SZ2", "4MN2", "4SG1", "4SG2", "4SG3", "4SG4",
			 * "4SG5", "2R02", "2R04", "2R05", "2R06", "2R09", "2R10", "2R13",
			 * "2R14", "2R15", "2R16", "2R19", "2R22", "2R25", "3R02", "3R03",
			 * "4R01", "4R02", "4R03", "4R05", "4L03", "4L04", "1R06", "1R20",
			 * "4BW2", "4BW4", "4LC1", "4U02", "1R12", "2O01", "2O02", "2O03",
			 * "2O04", "2O05", "1PL4", "4R09", "4F01", "4F02", "4S01", "4X01",
			 * "4XE1", "4XA2" , "2C03", "1PL1","1WY2","1OV1" ,"4CR1",
			 * "4R07","1R14","1PL3","1PL2","4T01","4T02","4T03" ,
			 * "4T04","4T05","4T06","4SP1","4WK3","1P01" ,"4AW4"};
			 * System.out.println("silverCardArray"+silverCardArray.length);
			 * 
			 * String[] goldenCardArray = new String[] { "1002", "1005", "1010",
			 * "1013", "2002", "2005", "2010", "2013", "2L01", "3001", "VZ01",
			 * "4C01", "4J01", "4001", "4002", "2JY1", "3003", "3005", "4PK1",
			 * "3DZ1", "1CG1", "2CZ1", "1011", "1015", "4007", "4008", "1CT1",
			 * "4CA1", "4CA2", "4CA3", "4CA4", "4CA5", "4CA6", "4CA7", "4CA8",
			 * "4CA9", "4CA0", "4U01", "1R01", "1R02", "1R03", "1R04", "1R05",
			 * "1WY1", "3R01", "3R04", "3R05", "3R06", "4AW1", "4AW2", "4CM1",
			 * "4L01", "4L02", "4L05", "4L06", "4L07", "4L08", "4L09", "4L10",
			 * "4L11", "4L12", "4L13", "4L14", "4L15", "4L16", "4L17", "4L18",
			 * "4L19", "4L20", "4OV1", "4R04", "4R06", "4U03", "4U04", "4U05",
			 * "4U06", "4U07", "4U08", "4U09", "4U10", "4U11", "4U12", "4U13",
			 * "4U14", "4U15", "4U16", "4U17", "4U18", "4U19", "4U20", "2007",
			 * "2008", "1007", "1008", "2P03", "1P03", "1P02", "1P01", "2P02",
			 * "2P01", "4HN1", "4101", "4102", "4103", "4104", "4105", "4106",
			 * "4107", "4108", "4109", "4110", "4111", "4112", "4113", "4114",
			 * "4115", "4116", "4117", "4118", "4119", "4120", "4121", "4122",
			 * "4123", "4124", "4125", "4126", "4127", "4128", "4129", "4130",
			 * "4131", "4132", "4133", "4134", "4135", "4136", "4137", "4138",
			 * "4139", "4140", "4201", "4202 ", "4203", "4204", "4205", "4206",
			 * "4207", "4208", "4209", "4210", "4211", "4212", "4213", "4214",
			 * "4215", "4216", "4217", "4218", "4219", "4220", "4221", "4222",
			 * "4223", "4224", "4225", "4226", "4227", "4228", "4229", "4230",
			 * "4231", "4232", "4233", "4234", "4235", "4236", "4237", "4238",
			 * "4239", "4240", "2ZF1", "2LY1", "2R01", "2R03", "2R07", "2R08",
			 * "2R11", "2R12", "2R17", "2R18", "2R20", "2R21", "2R23", "2R24",
			 * "2R26", "2R27", "2R28", "2R29", "2R30", "1CZ1", "1SZ1", "4MN1",
			 * "4050", "G001", "G002", "G003", "G004", "G005", "1R19", "4BW1",
			 * "4BW3", "1R11", "4R08", "4XA1","1R13","CXA1" };
			 * System.out.println("goldenCardArray"+goldenCardArray.length);
			 * 
			 * String[] platinaCardArray = new String[] { "2P03", "1P03",
			 * "1P02", "1P01", "2P02", "2P01" }; // 白金卡
			 * System.out.println("platinaCardArray"+platinaCardArray.length);
			 */

			String AT0041 = "-9";
			String AT0070 = "-9";
			String AT3110 = "-9";
			String AT4200 = "-9"; // 是否QCC申请
			String AT3120 = "0"; // 是否白金卡客户
			System.out.println("AT0070_0" + AT0070);
			/*
			 * if (this.verifyValue(attrString, valueArray)) {
			 *//**
			 * **********************************董春国 2007-1-26
			 * 修改(QCC卡)**************************
			 */
			/*
			 * // 是否QCC卡申请 // if (this.verifyValue(attrString, qccCardArray)) {
			 * // AT4200 = "1"; // this.isQccCardApp = true; // } else { //
			 * AT4200 = "0"; // }
			 *//**
			 * **********************************董春国 2007-1-26
			 * 修改(QCC卡)**************************
			 */
			/*
			 * 
			 * if (this.verifyValue(attrString, normalCardArray)) { AT0040 =
			 * "1"; } else if (this.verifyValue(attrString, oLikeCardArray)) {
			 * AT0040 = "2"; AT0070 = "1"; // 为奥运卡时,强制同意降级 this.isDownSell =
			 * true; } else if (this.verifyValue(attrString, jcbCardArray)) {
			 * AT0040 = "3"; } else if (this.verifyValue(attrString,
			 * dutyCardArray)) { AT0040 = "4"; AT0070 = "1"; // 为公务卡时,强制同意降级
			 * this.isDownSell = true; this.isDutyCardApp = true; } else if
			 * (this.verifyValue(attrString, platinaCardArray)) // 是否为白金卡申请 {
			 * AT3110 = "1";
			 * 
			 * } if (this.verifyValue(attrString, silverCardArray)) { AT0041 =
			 * "1"; } else if (this.verifyValue(attrString, goldenCardArray)) {
			 * AT0041 = "2"; } applProductCd = attrString; }
			 */
			// zhangshengtao 修改卡类别内容来自数据库
			applProductCd = attrString;
			String cards = null;
			cards = attrString;
			System.out.println("数据库读取卡种为：" + applProductCd);
			// if(1002==1002){
			// System.out.println("attrString:"+attrString);
			// System.out.println("数据库读取卡种为："+ProductCardInfoParameter.allProductCardMap);
			if (ProductCardInfoParameter.allProductCardMap
					.containsKey(attrString)) {
				System.out.println("卡种匹配成功");
				// ***** h*******************************董春国 2007-1-26
				// 修改(QCC卡)***************************//*
				// 是否QCC卡申请
				// if (this.verifyValue(attrString, qccCardArray)) {
				// AT4200 = "1";
				// this.isQccCardApp = true;
				// } else {
				// AT4200 = "0";
				// }
				// ************************************董春国 2007-1-26
				// 修改(QCC卡)***************************//*

				if (ProductCardInfoParameter.oLikeProductCardMap
						.containsKey(attrString)) {
					AT0040 = "2";
					AT0070 = "1"; // 为奥运卡时,强制同意降级
					// System.out.println("AT0070_2"+AT0070);
					this.isDownSell = true;
					// System.out.println("这里是奥运卡");
				} else if (ProductCardInfoParameter.jcbProductCardMap
						.containsKey(attrString)) {
					AT0040 = "3";
					// System.out.println("这里是jcb卡");
				} else if (ProductCardInfoParameter.dutyProductCardMap
						.containsKey(attrString)) {
					AT0040 = "4";
					AT0070 = "1"; // 为公务卡时,强制同意降级
					// System.out.println("AT0070_3"+AT0070);
					this.isDownSell = true;
					this.isDutyCardApp = true;
					// System.out.println("这里是公务卡");
				} else if (ProductCardInfoParameter.platinaProductCardMap
						.containsKey(attrString)) // 是否为白金卡申请
				{
					AT3110 = "1";
					System.out.println("AT3110_for_1: " + AT3110);
				} else {
					AT0040 = "1";
					// System.out.println("这里是非白金卡");
				}
				if (ProductCardInfoParameter.goldenProductCardMap
						.containsKey(attrString)) {
					AT0041 = "2";
					// System.out.println("这里是金卡");
				} else if (ProductCardInfoParameter.noDifferenceGoldSilverCardMap
						.containsKey(attrString)) {
					AT0041 = "2";
					AT0070 = "1"; // 为公务卡时,强制同意降级
					// System.out.println("AT0040_4"+AT0040);
					// System.out.println("AT0070_4"+AT0070);
					this.isDownSell = true;
					// System.out.println("这里是金卡强制降级");
				} else {
					AT0041 = "1";
					// System.out.println("这里是未匹配上的卡种");
				}
			} else {

				// StringBuffer strs = new StringBuffer();;
				// String str1 = null;
				// if("".equals(cards)||cards==null){
				// strs.append("读取卡种，未能找到：卡种未设置 传值卡种为：null");
				// // str1+="读取卡种，未能找到：卡种未设置 传值卡种为：null";
				// }
				// else{
				// System.out.println("111111111111"+cards);
				// strs.append("读取卡种，未能找到：卡种未设置 传值卡种为：");
				// strs.append(cards);
				// // str1+="读取卡种，未能找到：卡种未设置 传值卡种为："+cards;
				// }
				// if(ProductCardInfoParameter.allProductCardMap==null){
				// strs.append("数据库读取卡种为：null");
				// // str1+="数据库读取卡种为：null";
				// }
				// else{
				// strs.append("数据库读取卡种为：");
				// strs.append(ProductCardInfoParameter.allProductCardMap);
				// //
				// str1+="数据库读取卡种为："+ProductCardInfoParameter.allProductCardMap;
				// }
				// LogManager.getInstance().toCdsLog(strs.toString());
				//
				System.out.println("卡种匹配失败");
				// 不存在卡种的时候按普卡处理
				// AT0040="1";
				// AT0041 = "1";
				// System.out.println("1234");
				// this.errorCode=8888;
				System.out.println("!!!!!!!!!!!!!" + isSupplApplOnly);
				if (!isSupplApplOnly) {
					throw new CDSEException(8888);
				}

				// this.setWarningInfo("卡种未设置");

			}

			// System.out.println("AT0040:"+AT0040);
			// System.out.println("AT0041:"+AT0041);
			// System.out.println("AT0070:"+AT0070);
			/**
			 * **********************************马宝 2009-11-27
			 * 增加资信核查结果**************************
			 */
			// 资信核查结果
			String AT3270 = "-9";
			attrString = variable.getAttribute("recheckResultFlag").trim();
			String[] reCheckResultArray = new String[] { "1", "2", "3","4" };
			if (this.verifyValue(attrString, reCheckResultArray)) {
				AT3270 = attrString;
			} else if("".equals(attrString) || "null".equalsIgnoreCase(attrString)||"-999".equalsIgnoreCase(attrString)){
				AT3270 = "1";//预处理状态，不返回资信核查结果。
			}else {
				AT3270 = "4";
			}
			tempVariable = new VariableBean("AT3270", AT3270, "N", "EX",
					"资信核查结果");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT2370", attrString);
			// System.out.println("%%%%%%%%%%%%%%资信核查结果"+attrString);
			/**
			 * **********************************马宝 2009-11-27
			 * 增加资信核查结果**************************
			 */

			tempVariable = new VariableBean("AT0040", AT0040, "AN", "EX",
					"申请产品编号");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0040", AT0040);
			tempVariable = new VariableBean("AT3120", AT3120, "AN", "EX",
					"是否白金卡申请");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3120", AT3120);

			this.cardGrade = Integer.parseInt(AT0041);

			/**
			 * **********************************董春国 2007-1-26
			 * 修改(QCC卡)**************************
			 */
			tempVariable = new VariableBean("AT4200", AT4200, "AN", "EX",
					"是否QCC卡申请");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT4200", AT4200);

			// QCC卡申请是否零额度
			attrString = variable.getAttribute("ZeroLimitFlag").trim();
			String AT4210 = "-9";
			if ("N".equals(attrString)) {
				AT4210 = "0";
			} else if ("Y".equals(attrString)) {
				AT4210 = "1";
			}
			tempVariable = new VariableBean("AT4210", AT4210, "AN", "EX",
					"QCC卡申请是否零额度");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT4210", AT4210);
			/**
			 * **********************************董春国 2007-1-26
			 * 修改(QCC卡)**************************
			 */

			// 申请产品线
			tempVariable = new VariableBean("AT0041", AT0041, "AN", "EX",
					"申请产品线");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0041", AT0041);

			// 申请类型
			String AT0010 = "-9";
			attrString = variable.getAttribute("AppType").trim();
			String[] PriAppTypeArray = new String[] { "1", "2", "3" };
			if (this.verifyValue(attrString, PriAppTypeArray)) {
				AT0010 = attrString;
				if ("3".equals(AT0010)) {
					this.isSupplApplOnly = true;
				}
			}
			if ("4".equals(AT0040)) {
				AT0010 = "1";
				this.isSupplAppl = false;
				this.isSupplApplOnly = false;
			}
			tempVariable = new VariableBean("AT0010", AT0010, "N", "EX",
					"申请单类型");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0010", AT0010);

			// 原始申请产品线
			String AT0045 = AT0041;
			// if ("-9".equals(AT0045) && isSupplApplOnly == false) {
			// this.errorCode = "RR052:申请产品线缺失或异常，交人工处理。";
			// throw new CDSEException(errorCode);
			// }
			tempVariable = new VariableBean("AT0045", AT0045, "AN", "EX",
					"原始申请产品线");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0045", AT0045);

			// 公务卡清偿方式
			String AT0050 = "-9";
			String[] dischargeMethodArray = new String[] { "1", "2", "3" };
			attrString = variable.getAttribute("ReservedField3").trim();
			if (this.verifyValue(attrString, dischargeMethodArray)) {
				AT0050 = attrString;
			}
			tempVariable = new VariableBean("AT0050", AT0050, "AN", "EX",
					"公务卡清偿方式");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0050", AT0050);

			// 附属卡人数
			String AT0030 = "-9";
			attrString = variable.getAttribute("SuppNum").trim();
			System.out.println("SuppNum:" + attrString);
			if (isNumeric(attrString) && toDouble(attrString) > 0) {
				AT0030 = attrString;
			}

			// 申请产品币种
			String AT0060 = "-9";
			attrString = variable.getAttribute("PriCardCurr").trim();
			String[] PriCardCurrArray = new String[] { "1", "2" };
			if (this.verifyValue(attrString, PriCardCurrArray)) {
				AT0060 = attrString;
			}

			// 如果为JCB卡，则强制为双币
			if ("3".equalsIgnoreCase(AT0040)) {
				AT0060 = "1";
			}
			tempVariable = new VariableBean("AT0060", AT0060, "AN", "EX",
					"申请产品货币种类");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0060", AT0060);

			// 卡片币种
			String AT0065 = "-9";
			/*
			 * if ( (variable.getAttribute("ProductCd").trim().equals("3001") ||
			 * variable.getAttribute("ProductCd").trim().equals("3002")) &&
			 * variable.getAttribute("PriCardCurr").trim().equals("1")) {
			 */
			attrString = variable.getAttribute("ProductCd").trim();
			if (ProductCardInfoParameter.jcbProductCardMap
					.containsKey(attrString)) {
				AT0065 = "2";
			} else if (variable.getAttribute("PriCardCurr").trim().equals("1")) {
				AT0065 = "1";
			} else {
				AT0065 = "0";
			}
			tempVariable = new VariableBean("AT0065", AT0065, "AN", "EX",
					"卡片币种");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0065", AT0065);

			// String AT0070 = "-9";
			// 是否同意降级
			attrString = variable.getAttribute("CCardTypeDownGrade").trim();
			System.out.println("AT0070_5" + AT0070);
			if (!"1".equals(AT0070)) {
				if ("N".equalsIgnoreCase(attrString)) {
					AT0070 = "0";
					this.isDownSell = false;
					/**C1507,改为1和Y*/
				} else if ("Y".equalsIgnoreCase(attrString) ||"1".equalsIgnoreCase(attrString)) {
					AT0070 = "1";
					this.isDownSell = true;
				} else {
					if (isSupplApplOnly == false) {
						AT0070 = "1";
						this.isDownSell = true;
					} else {
						AT0070 = "-9";
						this.isDownSell = false;
					}
				}
			}
			System.out.println("AT0070_6" + AT0070);
			tempVariable = new VariableBean("AT0070", AT0070, "N", "EX",
					"如被决拒绝是否接受低一款信用卡");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0070", AT0070);

			// 申请人性别
			String AT0130 = "-9";
			attrString = variable.getAttribute("PriGender").trim();
			String[] PriGenderArray = new String[] { "1", "2" };
			if(null == attrString || "".equals(attrString) ||"-999".equals(attrString)|| "null".equalsIgnoreCase(attrString)){
				AT0130 = "-999";
			}else if (this.verifyValue(attrString, PriGenderArray)) {
				if ("2".equals(attrString)) {
					attrString = "0";
				}
				AT0130 = attrString;
			}else{
				AT0130 = "-9";
			}
			tempVariable = new VariableBean("AT0130", AT0130, "N", "EX",
					"主卡申请人性别");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0130", AT0130);

			// 主卡人出生日期
			String AT0140 = "-9";
			attrString = variable.getAttribute("PriCardBirthday").trim();
			if (attrString.length() >= 10) {
				attrString = this.changeDataFormat(attrString);
			}
			if (this.isDate(attrString)) {
				AT0140 = attrString;
			} else {
				AT0140 = "-9";
			}

			tempVariable = new VariableBean("AT0140", AT0140, "N", "EX",
					"主卡申请人出生日期");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0140", AT0140);

			// 主卡申请人年龄
			String AT0141 = "-9";
			int age = 0;
			if(null == attrString || "".equals(attrString) ||"-999".equals(attrString)|| "null".equalsIgnoreCase(attrString)){
				AT0141 = "-999";
			}else if (isDate(AT0140) == false) {
				AT0141 = "-9";
			} else {
				age = this.getAge(AT0140);
				AT0141 = String.valueOf(age);
			}
			tempVariable = new VariableBean("AT0141", AT0141, "N", "EX",
					"主卡申请人年龄");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0141", AT0141);

			// 主卡人证件类型
			String AT0150 = "-9";
			String AT0160 = "-9";
			attrString = variable.getAttribute("PriIDType").trim();
			String[] PriIDTypeArray = new String[] { "1", "2", "3", "4", "5" };
			if (this.verifyValue(attrString, PriIDTypeArray)) {
				if ("2".equals(attrString) || "4".equals(attrString)
						|| "5".equals(attrString)) {
					AT0150 = "3";
				} else if ("3".equals(attrString)) {
					AT0150 = "2";
				} else {
					AT0150 = "1";
				}
				AT0160 = attrString;
			} else {
				AT0150 = "-9";
				AT0160 = "-9";
			}
			tempVariable = new VariableBean("AT0150", AT0150, "N", "EX",
					"主卡申请人证件名称");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0150", AT0150);
			tempVariable = new VariableBean("AT0160", AT0160, "N", "EX",
					"主卡申请人证件名称2");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0160", AT0160);
			/**C1601添加：身份证前4位*/
			String AT0142 = "-9";
			attrString = variable.getAttribute("PriIDNo").trim();
			if("1".equals(AT0150)){//类型选择为身份证
				if(attrString.length() == 18 || attrString.length() == 15){
					attrString = attrString.substring(0, 4);
				}else{
					attrString="-9";
				}
			}else{
				attrString="-9";
			}
			AT0142 = attrString;
			tempVariable = new VariableBean("AT0142", AT0142, "N", "EX",
					"身份证前4位");
			attachVariableA.add(tempVariable);
			// 主卡人身份证件校验是否存在不符
			String AT0165 = "-9";
			attrString = variable.getAttribute("PriIDNo").trim();
			if ("1".equals(AT0150)) {
				AT0165 = getCheckPriIdNo(attrString, AT0140, AT0130);
			} else {
				AT0165 = "0";
			}
			tempVariable = new VariableBean("AT0165", AT0165, "N", "EX",
					"主卡人身份证件校验是否存在不符");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0165", AT0165);

			// 申请人国籍
			String AT0170 = "-9";
			attrString = variable.getAttribute("PriNationality").trim();
			String[] PriNationArray = new String[] { "CHN", "USA", "JPN" };
			if (this.verifyValue(attrString, PriNationArray)) {
				AT0170 = attrString;
			}
			tempVariable = new VariableBean("AT0170", AT0170, "C", "EX",
					"主卡申请人国籍");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0170", AT0170);

			// 是否本国公民
			String AT0171 = "-9";
			if ("CHN".equalsIgnoreCase(AT0170)) {
				AT0171 = "1";
			} else if (!"-9".equals(AT0170)) {
				AT0171 = "0";
			} else {
				AT0171 = "-9";
			}
			tempVariable = new VariableBean("AT0171", AT0171, "N", "EX",
					"主卡申请人是否本国公民");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0171", AT0171);

			// 主卡人身份类型
			String AT0190 = "-9";
			attrString = variable.getAttribute("PriJCBType").trim();
			String[] PriJCBTypeArray = new String[] { "1", "2", "3", "4" };
			if (this.verifyValue(attrString, PriJCBTypeArray)) {
				AT0190 = attrString;
			} else {
				AT0190 = "-9";
			}
			tempVariable = new VariableBean("AT0190", AT0190, "N", "EX",
					"主卡人身份类型");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0190", AT0190);

			// 主卡人婚姻状况
			String AT0180 = "-9";
			attrString = variable.getAttribute("PriMaritalSta").trim();
			String[] pmsArray = new String[] { "1", "2", "3", "4" };
			if (this.verifyValue(attrString, pmsArray)) {
				if ("1".equals(attrString)) { // 未婚
					AT0180 = "1";
				} else if ("2".equals(attrString)) { // 已婚
					AT0180 = "3";
				} else if ("3".equals(attrString)) { // 离异
					AT0180 = "2";
				} else {
					AT0180 = "4";
				}
			} else {
				AT0180 = "-9";
			}
			tempVariable = new VariableBean("AT0180", AT0180, "N", "EX",
					"主卡申请人婚姻状况");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0180", AT0180);

			// 主卡人教育程度
			String AT0200 = "-9";
			attrString = variable.getAttribute("PriEducationLvl").trim();
			String[] pidtArray = new String[] { "1", "2", "3", "4", "5" };
			if(null == attrString || "".equals(attrString) ||"-999".equals(attrString)|| "null".equalsIgnoreCase(attrString)){
				AT0200 = "-999";
			}else if (this.verifyValue(attrString, pidtArray)) {
				if ("1".equals(attrString)) { // 研究生及以上
					AT0200 = "5";
				} else if ("2".equals(attrString)) {
					AT0200 = "4";
				} else if ("3".equals(attrString)) { // 大学专科/高职学院
					AT0200 = "3"; // 大学本科
				} else { // 高中/职高/初中
					AT0200 = "2";
				}
			} else {
				AT0200 = "-9";
			}
			tempVariable = new VariableBean("AT0200", AT0200, "N", "EX",
					"主卡申请人教育程度");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0200", AT0200);

			// 现住地年限、现住地月份
			String AT0280 = "-9";
			attrString = variable.getAttribute("HomeResidingYear").trim();
			if (isNumeric(attrString) && toDouble(attrString) > 0) {
				AT0280 = attrString;
			}

			// String attrStringDurMth =
			// variable.getAttribute("HomeResidingMonth").
			// trim();
			// if ( (isNumeric(attrString) && toDouble(attrString) > 0) ||
			// (isNumeric(attrStringDurMth) && toDouble(attrStringDurMth) > 0))
			// {
			// double desidingDur = 0;
			// if ( (isNumeric(attrString) && toDouble(attrString) > 0)) {
			// desidingDur = toDouble(attrString);
			// }
			// if ( (isNumeric(attrStringDurMth) &&
			// toDouble(attrStringDurMth) > 0)) {
			// desidingDur = desidingDur + toDouble(attrStringDurMth) / 12;
			// }
			// AT0280 = String.valueOf( (float) desidingDur);
			// }
			tempVariable = new VariableBean("AT0280", AT0280, "N", "EX",
					"现居住地居住年数");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0280", AT0280);

			// 住宅性质
			String AT0290 = "-9";
			attrString = variable.getAttribute("HomeOwshType").trim(); // ?????原为HomeOwShType
			String[] hostArray = new String[] { "1", "2", "3", "4", "5", "6" };
			if(null == attrString || "".equals(attrString) ||"-999".equals(attrString)|| "null".equalsIgnoreCase(attrString)){
				AT0290 = "-999";
			}else if (this.verifyValue(attrString, hostArray)) {
				if ("5".equals(attrString)) { // 租用
					AT0290 = "1"; // 租用
				} else if ("4".equals(attrString)) { // 按揭自置
					AT0290 = "5"; // 按揭自置
				} else if ("3".equals(attrString)) { // 无按揭自置
					AT0290 = "6"; // 无按揭自置
				} else if ("2".equals(attrString)) { // 单位宿舍
					AT0290 = "4"; // 单位宿舍
				} else if ("1".equals(attrString)) { // 与亲属同住
					AT0290 = "3"; // 与亲属同住
				} else {
					AT0290 = "2"; // 其他
				}
			} else {
				AT0290 = "-9";
			}
			tempVariable = new VariableBean("AT0290", AT0290, "N", "EX",
					"现居住地性质");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0290", AT0290);

			// 每月租金金额
			String AT0300 = "-9";
			if ("1".equals(AT0290)) {
				attrString = variable.getAttribute("HouseHireAmt").trim();
				if ((isNumeric(attrString)) && (toDouble(attrString) > 0)) {
					AT0300 = attrString;
				} else {
					AT0300 = "-9";
				}
			} else {
				AT0300 = "-9";
			}
			tempVariable = new VariableBean("AT0300", AT0300, "N", "EX",
					"每月租金金额");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0300", AT0300);

			// 每月按揭供款金额
			String AT0310 = "-9";
			if ("5".equals(AT0290)) {
				attrString = variable.getAttribute("HousePayAmt").trim();
				if (isNumeric(attrString) && toDouble(attrString) > 0) {
					AT0310 = attrString;
				} else {
					AT0310 = "-9";
				}
			} else {
				AT0310 = "-9";
			}
			tempVariable = new VariableBean("AT0310", AT0310, "N", "EX",
					"现居住地性质若为按揭，每月供款");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0310", AT0310);

			// 住宅电话区号、住宅电话区号前两位
			String AT0340 = "-9";
			attrString = variable.getAttribute("HomePhonePro").trim();
			if (!("".equals(attrString))) {
				AT0340 = attrString;
			} else {
				AT0340 = "-9";
			}

			String AT0342 = "-9";
			int tempPhACd = 0;
			if (this.isInt(AT0340)) {
				tempPhACd = toInteger(AT0340);
				if ((tempPhACd > 0) && (tempPhACd < 100)) {
					AT0342 = String.valueOf(tempPhACd);
				} else if (tempPhACd >= 100) {
					AT0342 = String.valueOf(tempPhACd).substring(0, 2);
				} else {
					AT0342 = "0";
				}
			} else {
				AT0342 = "-9";
			}
			tempVariable = new VariableBean("AT0340", AT0340, "N", "EX",
					"家庭电话区号");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0340", AT0340);

			tempVariable = new VariableBean("AT0342", AT0342, "N", "EX",
					"家庭电话区号前两位");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0342", AT0342);

			// 住宅电话号码
			String AT0330 = "-9";
			attrString = variable.getAttribute("HomePhoneNo").trim();
			if (isNumeric(attrString)) {
				AT0330 = attrString;
			} else {
				AT0330 = "-9";
			}
			tempVariable = new VariableBean("AT0330", AT0330, "N", "EX",
					"家庭电话号码");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0330", AT0330);

			// 主卡人供养人数
			String AT0440 = "-9";
			attrString = variable.getAttribute("DependentCnt").trim();
			if (isNumeric(attrString) && toDouble(attrString) >= 0) {
				AT0440 = attrString;
			} else {
				AT0440 = "-9";
			}
			tempVariable = new VariableBean("AT0440", AT0440, "N", "EX", "供养人数");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0440", AT0440);

			// 公司地址邮编
			String AT0540 = "-9";
			attrString = variable.getAttribute("ComAddrPtCd").trim();
			AT0540 = attrString;
			tempVariable = new VariableBean("AT0540", AT0540, "N", "EX", "公司邮编");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0540", AT0540);

			// 公司电话区号
			String AT0560 = "-9";
			attrString = variable.getAttribute("CompTelPhonePro").trim();
			/*if (this.isInt(attrString)) {
				AT0560 = attrString;
			} else {
				AT0560 = "-9";
			}*/
			if (null == attrString
					|| "".equals(attrString) ||"-999".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT0560 = "-999";
			} else {
				if (attrString.startsWith("0")) {
					attrString = attrString.substring(1);
				}
				String[] arr = { "755", "532", "531", "571", "574", "411",
						"731", "371", "311", "451", "431", "591", "551", "871",
						"791", "592", "771", "471", "351", "898", "891", "892",
						"893", "894", "895", "896", "897" };
				if (verifyValue(attrString, arr)) {//true表示不需要截断
				} else {
					if (attrString.length() > 2) {
						attrString = attrString.substring(0, 2);
					}
				}
				
				AT0560 = attrString;
				System.out.println("AT0560:"+AT0560);
			}
			
			tempVariable = new VariableBean("AT0560", AT0560, "N", "EX",
					"公司电话区号");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0560", AT0560);

			// 公司电话号码
			String AT0570 = "-9";
			attrString = variable.getAttribute("CompTelPhone").trim();
			if (isNumeric(attrString)) {
				AT0570 = attrString;
			} else {
				AT0570 = "-9";
			}
			tempVariable = new VariableBean("AT0570", AT0570, "N", "EX",
					"公司电话号码");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0570", AT0570);

			// 住宅电话与公司电话区号是否相同
			int tempCompPhACd = 0;
			String AT0550 = "-9";
			if (this.isInt(variable.getAttribute("CompTelPhonePro").trim())) {
				tempCompPhACd = toInteger(variable.getAttribute(
						"CompTelPhonePro").trim());
			}

			if (tempPhACd != tempCompPhACd) {
				AT0550 = "0";
			} else {
				if ((!"-9".equals(AT0330)) && AT0330.equalsIgnoreCase(AT0570)) {
					AT0550 = "1";
				}
			}
			tempVariable = new VariableBean("AT0550", AT0550, "N", "EX",
					"住宅电话与公司电话是否相同");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0550", AT0550);
			//职位信息
			String AT6120 = "-9";
			attrString = variable.getAttribute("AT6120").trim();
			if (null == attrString
					|| "".equals(attrString) ||"-999".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT6120 = attrString;
			}else {
				AT6120 = "-9";
			}
			tempVariable = new VariableBean("AT6120", AT6120, "N", "EX",
					"职业信息");
			attachVariableA.add(tempVariable);
			// 判断是否新旧表单，1新表单2旧表单
			String AT6130 = "-9";
			attrString = variable.getAttribute("AT6130").trim();
			if ("1".equalsIgnoreCase(attrString)
					|| "Y".equalsIgnoreCase(attrString)) {
				AT6130 = "1";
			} else {
				AT6130 = "2";
			}
			tempVariable = new VariableBean("AT6130", AT6130, "N", "EX",
					"是否新表单");
			attachVariableA.add(tempVariable);
			// 行业性质
			String AT0620 = "-9";
			attrString = variable.getAttribute("CompBizField").trim();
			if ("1".equals(AT6130)) {// 新表单
				AT0620 = newForm(attrString);
			} else {// 旧表单
				AT0620 = oldForm(attrString);
			}

			tempVariable = new VariableBean("AT0620", AT0620, "AN", "EX",
					"行业性质");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0620", AT0620);

			// 经济类型
			String AT0630 = "-9";
			attrString = variable.getAttribute("CompBizOwShType").trim();
			String[] bizOwShTypeArray = new String[] { "01", "02", "03", "04",
					"05", "06", "07", "08", "09", "10", "11", "12", "13", "14",
					"15", "16", "17", "18" };
			if(null == attrString || "".equals(attrString) ||"-999".equals(attrString)|| "null".equalsIgnoreCase(attrString)){
				AT0630 = "-999";
			}else if (this.verifyValue(attrString, bizOwShTypeArray)) {
				if ("01".equals(attrString) || "11".equals(attrString)) {
					AT0630 = "01";
				} else if ("02".equals(attrString) || "12".equals(attrString)) {
					AT0630 = "02";
				} else if ("03".equals(attrString)) {
					AT0630 = "03";
				} else if ("04".equals(attrString) || "13".equals(attrString)) {
					AT0630 = "04";
				} else if ("05".equals(attrString) || "14".equals(attrString)) {
					AT0630 = "05";
				} else if ("06".equals(attrString) || "15".equals(attrString)) {
					AT0630 = "06";
				} else if ("07".equals(attrString) || "16".equals(attrString)) {
					AT0630 = "07";
				} else if ("08".equals(attrString)) {
					AT0630 = "08";
				} else if ("09".equals(attrString) || "18".equals(attrString)) {
					AT0630 = "09";
				} else if ("17".equals(attrString)) {
					AT0630 = "10";
				}
			} else {
				AT0630 = "-9";
			}
			tempVariable = new VariableBean("AT0630", AT0630, "AN", "EX",
					"经济类型");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0630", AT0630);

			// 申请人职位
			String AT0660 = "-9";
			attrString = variable.getAttribute("PriPos").trim();
			String[] PriPosArray = new String[] { "101", "102", "103", "104",
					"201", "202", "203", "204", "205", "301", "302", "303",
					"304", "401", "402", "403", "404", "405", "406", "407",
					"501", "502", "503", "504", "601", "602", "603", "604",
					"701", "702", "703" };
			if (attrString.equals("") || attrString == null
					|| attrString.equalsIgnoreCase("null")) {
				AT0660 = "-9";
			} else if (this.verifyValue(attrString, PriPosArray)) {
				if ("101".equals(attrString) || "501".equals(attrString)) {
					AT0660 = "101";
				} else if ("102".equals(attrString) || "502".equals(attrString)) {
					AT0660 = "102";
				} else if ("103".equals(attrString) || "503".equals(attrString)) {
					AT0660 = "103";
				} else if ("104".equals(attrString) || "504".equals(attrString)) {
					AT0660 = "104";
				} else if ("401".equals(attrString) || "601".equals(attrString)) {
					AT0660 = "401";
				} else if ("402".equals(attrString) || "602".equals(attrString)) {
					AT0660 = "402";
				} else if ("403".equals(attrString) || "603".equals(attrString)) {
					AT0660 = "403";
				} else if ("406".equals(attrString) || "604".equals(attrString)) {
					AT0660 = "406";
				} else {
					AT0660 = attrString;
				}
			} else {
				AT0660 = attrString;
			}

			tempVariable = new VariableBean("AT0660", AT0660, "N", "EX",
					"职位或职级");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0660", AT0660);
			// 新申请人职位
			String AT0665 = "-9";
			attrString = variable.getAttribute("PriPos").trim();
			if (attrString != null || attrString.equals("")) {
				if ("101".equals(attrString) || "501".equals(attrString)) {
					AT0665 = "501";
				} else if ("401".equals(attrString) || "601".equals(attrString)) {
					AT0665 = "601";
				} else {
					AT0665 = attrString;
				}
			}
			tempVariable = new VariableBean("AT0665", AT0665, "N", "EX",
					"新职位或职级");
			attachVariableA.add(tempVariable);
			// 现职年限
			String AT0680 = "-9";
			attrString = variable.getAttribute("CompPosSeniority").trim();
			if(null == attrString || "".equals(attrString) ||"-999".equals(attrString)|| "null".equalsIgnoreCase(attrString)){
				AT0680 = "-999";
			}else if (isNumeric(attrString) && toDouble(attrString) >= 0) {
				AT0680 = attrString;
			} else {
				AT0680 = "-9";
			}
			tempVariable = new VariableBean("AT0680", AT0680, "N", "EX", "现职年限");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0680", AT0680);

			// 当前固定月收入
			String AT0700 = "-9";
			String mthlySalaryString = variable.getAttribute("MthlySalary")
					.trim();
			String annSalaryString = variable.getAttribute("AmmSalary").trim(); // ?AnnSalary
			double mthlySalaryDouble = 0.0f;
			double annSalaryDouble = 0.0f;
			if (isNumeric(mthlySalaryString)) {
				mthlySalaryDouble = toDouble(mthlySalaryString);
			}
			if (isNumeric(annSalaryString)) {
				annSalaryDouble = toDouble(annSalaryString);
			}
			if (annSalaryDouble > 0) {
				if (mthlySalaryDouble > 0) {
					AT0700 = String.valueOf(mthlySalaryDouble);
				} else {
					AT0700 = String.valueOf(annSalaryDouble / 12);
				}
			} else {
				if (mthlySalaryDouble > 0) {
					AT0700 = String.valueOf(mthlySalaryDouble);
				}
			}
			tempVariable = new VariableBean("AT0700", AT0700, "N", "EX",
					"当前工作月收入");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0700", AT0700);
			// 申请表年收入
			String AT0720 = "-999";
			attrString = variable.getAttribute("AT0710").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT0720 = "0";
			} else {
				AT0720 = attrString;
			}
			tempVariable = new VariableBean("AT0720", AT0720, "N", "EX",
					"申请表年收入");
			attachVariableA.add(tempVariable);
			// 年收入总额
			String AT0710 = "-9";
			attrString = variable.getAttribute("AmmSalary").trim();
			if ("".equals(attrString) || (annSalaryDouble <= 0)) {
				if (!"-9".equals(AT0700)) {
					AT0710 = String.valueOf(toDouble(AT0700) * 12);
				} else {
					AT0710 = "-9";
				}
			} else {
				AT0710 = String.valueOf(annSalaryDouble);
			}
			tempVariable = new VariableBean("AT0710", AT0710, "N", "EX",
					"当前工作年收入");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0710", AT0710);

			// log.appendParameter("AT0710", AT0710);
			// 是否拥有房产

			String AT1141 = "-9";

			attrString = variable.getAttribute("appiMcHouseProperty").trim();
			if ("1".equalsIgnoreCase(attrString)) {
				AT1141 = "1";
			} else if ("2".equalsIgnoreCase(attrString)) {
				AT1141 = "2";
			}
			tempVariable = new VariableBean("AT1141", AT1141, "N", "EX",
					"是否拥有房产");
			attachVariableA.add(tempVariable);

			// 是否拥有机动车 2009 -6-5

			String AT1142 = "-9";

			attrString = variable.getAttribute("appiMcCarInd").trim();
			if ("1".equalsIgnoreCase(attrString)) {
				AT1142 = "1";
			} else if ("2".equalsIgnoreCase(attrString)) {
				AT1142 = "2";
			}
			tempVariable = new VariableBean("AT1142", AT1142, "N", "EX",
					"是否拥有机动车产");
			attachVariableA.add(tempVariable);

			// 是否持有其它机构信用卡
			String AT0780 = "-9";
			attrString = variable.getAttribute("OtherBankCardSta").trim();
			if ("1".equalsIgnoreCase(attrString)) {
				AT0780 = "2";
			} else if ("2".equalsIgnoreCase(attrString)) {
				AT0780 = "1";
			} else if ("3".equalsIgnoreCase(attrString)) {
				AT0780 = "0";
			} else {
				AT0780 = "-9";
			}
			tempVariable = new VariableBean("AT0780", AT0780, "N", "EX",
					"持有其他发卡机构信用卡");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0780", AT0780);

			// 信用卡数量
			String AT0790 = "-9";
			attrString = variable.getAttribute("OthCardCnt").trim();
			if (isNumeric(attrString) && toDouble(attrString) > 0) {
				AT0790 = attrString;
			} else {
				if (AT0780 == "1" || AT0780 == "2") {
					AT0790 = "1";
				} else {
					AT0790 = "-9";
				}
			}
			tempVariable = new VariableBean("AT0790", AT0790, "N", "EX",
					"现有信用卡数量");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0790", AT0790);

			// 是否有中行储蓄帐户
			String AT1100 = "-9";
			attrString = variable.getAttribute("BOCSaveActFlg").trim();
			/**C1507封版后，统一改为判断1和Y*/
			if ("Y".equalsIgnoreCase(attrString) || "1".equalsIgnoreCase(attrString)) {
				AT1100 = "1";
			} else if ("N".equalsIgnoreCase(attrString) || "2".equalsIgnoreCase(attrString)
					|| "0".equalsIgnoreCase(attrString)) {
				AT1100 = "0";
			} else {
				AT1100 = "-9";
			}
			tempVariable = new VariableBean("AT1100", AT1100, "N", "EX",
					"是否已在中国银行开立储蓄账户");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT1100", AT1100);

			String AT1140 = "-9";
			attrString = variable.getAttribute("AssetValue").trim();
			if (isNumeric(attrString) && toDouble(attrString) > 0) {
				AT1140 = attrString;
			} else {
				AT1140 = "-9";
			}
			tempVariable = new VariableBean("AT1140", AT1140, "N", "EX",
					"现有物业及其它投资价值");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT1140", AT1140);

			// 是否拥有机动车
			String AT1150 = "-9";
			attrString = variable.getAttribute("CarOwShCondition").trim();
			/**C1507封版后，统一改为判断1和Y*/
			if ("Y".equalsIgnoreCase(attrString) || "1".equalsIgnoreCase(attrString)) {
				AT1150 = "1";
			} else if ("N".equalsIgnoreCase(attrString) ||"2".equalsIgnoreCase(attrString)) {
				AT1150 = "0";
			} else {
				AT1150 = "-9";
			}
			tempVariable = new VariableBean("AT1150", AT1150, "N", "EX",
					"现有机动车辆情况");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT1150", AT1150);

			// 2004-3-22 根据最新的申请单更新副卡申请人年龄。
			NodeList suppls = variable.getChildNodes();
			System.out.println("suppl:" + suppls.getLength());
			if (suppls != null && this.isDutyCardApp == false) {
				this.supplNum = suppls.getLength();
			} else {
				this.supplNum = 0;
			}

			// 如果为纯粹附属卡，同时又没有附属卡信息输入，抛出异常
			// if (this.isSupplApplOnly == true && this.supplNum <= 0) {
			// this.errorCode = "RR054:附属卡的信息缺失或异常，交人工处理。";
			// throw new CDSEException(errorCode);
			// }

			AT0030 = String.valueOf(supplNum);
			tempVariable = new VariableBean("AT0030", AT0030, "AN", "EX",
					"附属卡人数");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT0030", AT0030);

			Element item = null;
			for (int i = 0; i < supplNum; i++) {
				item = (Element) suppls.item(i);

				String supplId = item.getAttribute("SupplID").trim();
				// if ("".equals(supplId)) {
				// this.errorCode = "RR051:附属卡的编号缺失或异常，交人工处理。";
				// throw new CDSEException(errorCode);
				// }
				supplData.add(supplId);

				if (isSupplApplOnly == true) {
					String tempString1 = item.getAttribute("PrimaryCardNo")
							.trim();
					// if ("".equals(tempString1)) {
					// this.errorCode = "RR053:附属卡申请中主卡卡号缺失或异常，交人工处理。";
					// throw new CDSEException(errorCode);
					// }
				}
				if (!"".equals(item.getAttribute("PrimaryCardNo").trim())
						&& !"".equals(item.getAttribute("SupplID").trim())
						&& !"".equals(item.getAttribute("SuppTitle").trim())
						&& !"".equals(item.getAttribute("SuppBirthday").trim())
						&& !"".equals(item.getAttribute("SuppIDType").trim())
						&& !"".equals(item.getAttribute("SuppIDNo").trim())
						&& !"".equals(item.getAttribute("SuppRelation").trim())
						&& !"".equals(item.getAttribute("SuppJCBType").trim())
						&& !"".equals(item.getAttribute("SuppLimitFlag").trim())
						&& !"".equals(item.getAttribute("SuppLimitPercent")
								.trim())) {
					this.isSupplAppl = true;
				}
				String AT1235 = "-9";
				attrString = item.getAttribute("SuppBirthday").trim();
				if (attrString.length() >= 10) {
					attrString = this.changeDataFormat(attrString);
				}
				if (this.isDate(attrString)) {
					AT1235 = attrString;
				} else {
					AT1235 = "-9";
				}
				tempVariable = new VariableBean("AT1235", AT1235, "N",
						String.valueOf(i + 1), "附属卡申请人出生日期");
				attachVariableA.add(tempVariable);
				// //log.appendParameter("AT1235", AT1235);

				String AT1236 = "-9";
				age = 0;
				attrString = item.getAttribute("SuppBirthday").trim();
				if (attrString.length() >= 10) {
					attrString = this.changeDataFormat(attrString);
					// System.out.println("长度大于等于10=============="+attrString.length());
				}
				// System.out.println("attrString==================="+attrString);
				if (isDate(attrString) == false) {
					age = -9;
				} else {
					age = this.getAge(attrString);
				}
				AT1236 = String.valueOf(age);
				// System.out.println("AT1236==================="+AT1236);
				tempVariable = new VariableBean("AT1236", AT1236, "N",
						String.valueOf(i + 1), "附属卡申请人年龄");
				attachVariableA.add(tempVariable);
				// //log.appendParameter("AT1236", AT1236);

				String AT1240 = "-9";
				attrString = item.getAttribute("SuppRelation").trim();
				String[] supplRelationArray = new String[] { "1", "2", "3", "4" };
				if (this.verifyValue(attrString, supplRelationArray)) {
					AT1240 = attrString;
				} else if (age > 0) {
					AT1240 = "4";
				} else {
					AT1240 = "-9";
				}
				tempVariable = new VariableBean("AT1240", AT1240, "N",
						String.valueOf(i + 1), "与主卡申请人的关系");
				attachVariableA.add(tempVariable);
				// //log.appendParameter("AT1240", AT1240);

				String AT1420 = "-9";
				double supplLtdRatioDouble = -1;
				attrString = item.getAttribute("SuppLimitFlag").trim();
				String anotherAttrString = variable.getAttribute(
						"SuppLimitPercent").trim();
				if (!"".equalsIgnoreCase(anotherAttrString)) {
					supplLtdRatioDouble = toDouble(anotherAttrString);
				}
				String[] supplLtdRatioTypeArray = new String[] { "1", "2" };
				if (this.verifyValue(attrString, supplLtdRatioTypeArray)) {
					AT1420 = attrString;
				} else {
					if ((age > 0)
							&& (supplLtdRatioDouble == 0 || ""
									.equalsIgnoreCase(anotherAttrString))) {
						AT1420 = "1";
					} else if ((age > 0)
							&& (supplLtdRatioDouble >= 0 && supplLtdRatioDouble <= 100)) {
						AT1420 = "2";
					}
				}
				tempVariable = new VariableBean("AT1420", AT1420, "AN",
						String.valueOf(i + 1), "附属卡信用额度设定");
				attachVariableA.add(tempVariable);
				// //log.appendParameter("AT1420", AT1420);

				String AT1430 = "-9";
				if ("1".equals(AT1420)) {
					AT1430 = "100";
				} else {
					attrString = item.getAttribute("SuppLimitPercent").trim();
					if (isNumeric(attrString)) {
						supplLtdRatioDouble = toDouble(attrString);
						if (supplLtdRatioDouble >= 0
								&& supplLtdRatioDouble <= 100) {
							AT1430 = attrString;
						} else {
							if (age > 0) {
								AT1430 = "100";
							}
						}
					} else {
						if (age > 0) {
							AT1430 = "100";
						}
					}
				}
				tempVariable = new VariableBean("AT1430", AT1430, "N",
						String.valueOf(i + 1), "若要设定附属卡信用额度，百分比");
				attachVariableA.add(tempVariable);
				// //log.appendParameter("AT1430", AT1430);
				supplLtdRatio = toDouble(AT1430) / 100;
			}

			// 帐单邮寄地址选择
			String AT1440 = "-9";
			attrString = variable.getAttribute("PriStatAddrSel").trim();
			String[] priStatAddrSelTypeArray = new String[] { "1", "2" };
			if(null == attrString || "".equals(attrString) ||"-999".equals(attrString)|| "null".equalsIgnoreCase(attrString)){
				AT1440 = "-999";
			}else if (this.verifyValue(attrString, priStatAddrSelTypeArray)) {
				AT1440 = attrString;
			} else {
				AT1440 = "-9";
			}
			tempVariable = new VariableBean("AT1440", AT1440, "N", "EX",
					"主卡对账单邮寄地址选择");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT1440", AT1440);

			// 是否为当地户口 zhaowei 2009-5-31

			String AT4220 = "-9";
			String personId = null;
			String postalCode = null;
			if ("1".equals(AT1440)) {
				postalCode = variable.getAttribute("ComAddrPtCd").trim();
			} else if ("2".equals(AT1440)) {
				postalCode = variable.getAttribute("PriHomePtCd").trim();
			}
			personId = variable.getAttribute("PriIDNo").trim();

			if (personId != null && postalCode != null) {

				if (this.isLocal(personId, postalCode) == true) {
					AT4220 = "0";
				} else {
					AT4220 = "1";
				}
			} else {
				AT4220 = "-9";
			}
			tempVariable = new VariableBean("AT4220", AT4220, "N", "EX",
					"主卡申请人是否本地户口");
			attachVariableA.add(tempVariable);

			// 还款方式选择
			String AT1630 = "-9";
			attrString = variable.getAttribute("AutoPayMethod").trim();
			String[] paytMethodArray = new String[] { "1", "2" };
			if(null == attrString || "".equals(attrString) ||"-999".equals(attrString)|| "null".equalsIgnoreCase(attrString)){
				AT1630 = "-999";
			}else if (this.verifyValue(attrString, paytMethodArray)) {
				if ("1".equals(attrString)) {
					AT1630 = "2";
				} else if ("2".equals(attrString)) {
					AT1630 = "1";
				}
			}else{
				AT1630 = "-9";
			}

			// 还款金额选择
			String AT1680 = "-9";
			attrString = variable.getAttribute("AutoPayAmtSel").trim();
			String[] paytAmtArray = new String[] { "1", "2" };
			if (this.verifyValue(attrString, paytAmtArray)) {
				AT1680 = attrString;
			}
			tempVariable = new VariableBean("AT1630", AT1630, "N", "EX", "还款方式");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT1630", AT1630);

			tempVariable = new VariableBean("AT1680", AT1680, "AN", "EX",
					"还款额设定");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT1680", AT1680);

			// 还款账户选择
			String AT1690 = "-9";
			attrString = variable.getAttribute("AutoPayActSel").trim();
			String[] paytActSelArray = new String[] { "1", "2" };
			if (this.verifyValue(attrString, paytActSelArray)) {
				AT1690 = attrString;
			}
			tempVariable = new VariableBean("AT1690", AT1690, "AN", "EX",
					"还款账户选择");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT1690", AT1690);

			// 营销活动编码
			String AT2310 = "-9";
			attrString = variable.getAttribute("BKCC").trim();
			if (attrString != null && !"".equals(attrString)) {
				AT2310 = attrString;
			}

			tempVariable = new VariableBean("AT2310", AT2310, "N", "EX", "活动编码");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT2310", AT2310);

			// 渠道编号
			String AT2320 = "-9";
			String BH_num = "0";
			attrString = variable.getAttribute("BH").trim();
			if (attrString != null && !"".equals(attrString)
					&& attrString.length() > 3) {
				AT2320 = attrString.substring(1, 3);
				/**4324433001江苏：
				 * 4324445000苏州:
				 * 4335143000宁波
				 * 4335129000浙江*/
				if (AT2320.equals("32") || AT2320.equals("33")) {
					BH_num = attrString.substring(6, 8);
					if (BH_num.equals("30")) {
						AT2320 = "30";
					}
					if (BH_num.equals("50")) {
						AT2320 = "39";
					}
				}
			}
			System.out.println(AT2320);
			tempVariable = new VariableBean("AT2320", AT2320, "N", "EX", "渠道编号");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT2320", AT2320);

			// 是否分行审批
			String AT2340 = "-9";
			attrString = variable.getAttribute("IfBHDA").trim();
			if ("Y".equals(attrString)) {
				AT2340 = "1";
			} else if ("N".equals(attrString)) {
				AT2340 = "0";
			}
			tempVariable = new VariableBean("AT2340", AT2340, "N", "EX",
					"是否分行审批");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT2340", AT2340);

			// 是否提供了身份证明文件
			/*
			 * String AT2400 = "-9"; attrString =
			 * variable.getAttribute("IDProof").trim(); if
			 * ("Y".equalsIgnoreCase(attrString)) { AT2400 = "1"; } else {
			 * AT2400 = "0"; } tempVariable = new VariableBean("AT2400", AT2400,
			 * "N", "EX", "是否提供身份证明文件");
			 * attachVariableA.add(tempVariable.getFldName(),tempVariable);
			 * //log.appendParameter("AT2400", AT2400);
			 */

			// 是否提供了工作证明文件
			/*
			 * String AT2410 = "-9"; attrString =
			 * variable.getAttribute("JobProof").trim(); if
			 * ("Y".equalsIgnoreCase(attrString)) { AT2410 = "1"; } else {
			 * AT2410 = "0"; } tempVariable = new VariableBean("AT2410", AT2410,
			 * "N", "EX", "是否提供工作证明文件");
			 * attachVariableA.add(tempVariable.getFldName(),tempVariable);
			 * //log.appendParameter("AT2410", AT2410);
			 */

			// 是否提供了收入证明文件
			String AT2420 = "-9";
			attrString = variable.getAttribute("IncomeProof").trim();
			/**C1507封版后，统一改为判断1和Y*/
			if ("Y".equalsIgnoreCase(attrString) || "1".equalsIgnoreCase(attrString)) {
				AT2420 = "1";
			} else if ("N".equalsIgnoreCase(attrString)|| "2".equalsIgnoreCase(attrString)
					|| "0".equalsIgnoreCase(attrString)) {
				AT2420 = "0";
			}
			tempVariable = new VariableBean("AT2420", AT2420, "N", "EX",
					"是否提供收入证明文件");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT2420", AT2420);

			// 是否提供了资产证明文件
			/*
			 * String AT2430 = "-9"; attrString =
			 * variable.getAttribute("AssetProof").trim(); if
			 * ("Y".equalsIgnoreCase(attrString)) { AT2430 = "1"; } else {
			 * AT2430 = "0"; } tempVariable = new VariableBean("AT2430", AT2430,
			 * "N", "EX", "是否提供资产证明文件（包括定期存款、有价证券等）");
			 * attachVariableA.add(tempVariable.getFldName(),tempVariable);
			 * //log.appendParameter("AT2430", AT2430);
			 */

			// 住址是否与公司在同一地区
			String AT2500 = "-9";
			if (AT0560.equalsIgnoreCase(AT0340)) {
				AT2500 = "1";
			} else {
				AT2500 = "0";
			}
			tempVariable = new VariableBean("AT2500", AT2500, "N", "EX",
					"住址是否与公司在同一地区");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT2500", AT2500);

			// 是否中行信用卡持卡人
			String AT3000 = "-9";
			attrString = variable.getAttribute("BOCCardHolder").trim();
			/**C1507封版后，统一改为判断1和Y*/
			if ("Y".equalsIgnoreCase(attrString) || "1".equalsIgnoreCase(attrString)) {
				AT3000 = "1";
			} else if ("N".equalsIgnoreCase(attrString) || "2".equalsIgnoreCase(attrString)
					|| "0".equalsIgnoreCase(attrString)) {
				AT3000 = "0";
			}
			tempVariable = new VariableBean("AT3000", AT3000, "N", "EX",
					"是否中行信用卡现有持卡人");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3000", AT3000);

			// 持有中行信用卡的最早开始日期
			String AT3010 = "-9";
			attrString = variable.getAttribute("BOCCardBeginDate").trim();
			if (attrString.length() >= 10) {
				attrString = this.changeDataFormat(attrString);
			}
			if (this.isDate(attrString)) {
				AT3010 = attrString;
			}
			tempVariable = new VariableBean("AT3010", AT3010, "N", "EX",
					"持有中行信用卡的最早开始日期");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3010", AT3010);

			// 最早持有中行信用卡距今月
			String AT3011 = "-9";
			if (("0".equals(AT3000)) || ("".equals(AT3010))) {
				AT3011 = "0";
			} else {
				if (isDate(AT3010)) {
					age = this.getMonthes(AT3010);
					AT3011 = String.valueOf(age);
				} else {
					AT3011 = "-9";
				}
			}
			tempVariable = new VariableBean("AT3011", AT3011, "N", "EX",
					"最早持有中行信用卡距今月");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3011", AT3011);

			// 主卡申请人在中行的总信用额度(人民币)
			String AT3020 = "-9";
			attrString = variable.getAttribute("BOCCardLimit").trim();
			if (isSupplApplOnly == true) {
				attrString = variable.getAttribute("BOCCardAmnt").trim();
				if (this.isNumeric(attrString) && toDouble(attrString) > 0) {
					AT3020 = attrString;
				}
			} else {
				if (this.isNumeric(attrString) && toDouble(attrString) > 0) {
					AT3020 = attrString;
				} else {
					AT3020 = "-9";
				}
			}
			tempVariable = new VariableBean("AT3020", AT3020, "N", "EX",
					"主卡申请人在中行的总信用额度(人民币)");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3020", AT3020);

			// 持有中行信用卡主卡的张数
			String AT3030 = "-9";
			attrString = variable.getAttribute("BOCCardNum").trim();
			if ("".equals(attrString)) {
				AT3030 = "-9";
			} else {
				if (isNumeric(attrString) && toDouble(attrString) > 0) {
					AT3030 = attrString;
				} else {
					AT3030 = "-9";
				}
			}
			tempVariable = new VariableBean("AT3030", AT3030, "N", "EX",
					"持有中行信用卡主卡的张数");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3030", AT3030);

			// 在过去24个月内是否拖欠30天
			String AT3040 = "-9";
			attrString = variable.getAttribute("DPD30P24Mon").trim();
			/**C1507封版后，统一改为判断1和Y*/
			if ("Y".equalsIgnoreCase(attrString) || "1".equalsIgnoreCase(attrString)) {
				AT3040 = "1";
			} else if ("N".equalsIgnoreCase(attrString) || "2".equalsIgnoreCase(attrString)
					|| "0".equalsIgnoreCase(attrString)) {
				AT3040 = "0";
			}
			tempVariable = new VariableBean("AT3040", AT3040, "N", "EX",
					"在过去24个月内是否拖欠30天");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3040", AT3040);

			// 在过去24个月内是否拖欠60天
			String AT3050 = "-9";
			attrString = variable.getAttribute("DPD60P24Mon").trim();
			/**C1507封版后，统一改为判断1和Y*/
			if ("Y".equalsIgnoreCase(attrString) || "1".equalsIgnoreCase(attrString)) {
				AT3050 = "1";
			} else if ("N".equalsIgnoreCase(attrString) || "2".equalsIgnoreCase(attrString)
					|| "0".equalsIgnoreCase(attrString)) {
				AT3050 = "0";
			}
			tempVariable = new VariableBean("AT3050", AT3050, "N", "EX",
					"在过去24个月内是否拖欠60天");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3050", AT3050);

			// 在过去24个月内是否拖欠90天
			String AT3060 = "-9";
			attrString = variable.getAttribute("DPD90P24Mon").trim();
			/**C1507封版后，统一改为判断1和Y*/
			if ("Y".equalsIgnoreCase(attrString) || "1".equalsIgnoreCase(attrString)) {
				AT3060 = "1";
			} else if ("N".equalsIgnoreCase(attrString)|| "2".equalsIgnoreCase(attrString)
					|| "0".equalsIgnoreCase(attrString)) {
				AT3060 = "0";
			}
			tempVariable = new VariableBean("AT3060", AT3060, "N", "EX",
					"在过去24个月内是否拖欠90天");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3060", AT3060);

			// 申请人的客户随机数ID(若是中行信用卡现有持卡人)
			String AT3070 = "-9";
			attrString = variable.getAttribute("AppRanNum").trim();
			if (!("".equals(attrString))) {
				if (isNumeric(attrString)) {
					this.randomNum = Integer.parseInt(attrString);
					AT3070 = attrString;
				}
			}
			tempVariable = new VariableBean("AT3070", AT3070, "N", "EX",
					"申请人的客户随机数ID（若是中行信用卡现有持卡人）");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3070", AT3070);

			// 申请人的随机数（新客户时CDS-E产生的）
			String AT3075 = "-9";
			attrString = variable.getAttribute("AppRanNum").trim();
			if ("".equals(attrString) || (!isNumeric(attrString))) {
				this.randomNum = this.randIntGenerator(1000);
				AT3075 = String.valueOf(this.randomNum);
			}
			tempVariable = new VariableBean("AT3075", AT3075, "N", "EX",
					"申请人的随机数（新客户时CDS-E产生的）");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3075", AT3075);

			// 当时第一张卡被批准时的策略标志号ID
			String AT3090 = "0";
			attrString = variable.getAttribute("FirstApprvdStrategyID").trim();
			if (attrString != null && !"".equals(attrString)) {
				AT3090 = attrString;
			}
			tempVariable = new VariableBean("AT3090", AT3090, "N", "EX",
					"当时第一张卡被批准时的策略标志号ID");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3090", AT3090);

			// 申请人过去申请过的次数
			String AT3100 = "-9";
			attrString = variable.getAttribute("AppCnt").trim();
			if (isNumeric(attrString) && toDouble(attrString) > 0) {
				AT3100 = attrString;
			} else {
				AT3100 = "-9";
			}
			tempVariable = new VariableBean("AT3100", AT3100, "N", "EX",
					"申请人过去申请过的次数");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3100", AT3100);

			// 是否VIP用户

			attrString = variable.getAttribute("BKVIP").trim();
			/**C1507封版后，统一改为判断1和Y*/
			if ("Y".equalsIgnoreCase(attrString) || "1".equalsIgnoreCase(attrString)) {
				AT3110 = "1";
				this.cardLevel = 1;
			} else if ("N".equalsIgnoreCase(attrString) || "2".equalsIgnoreCase(attrString)) {
				if (ProductCardInfoParameter.platinaProductCardMap
						.containsKey(cards)) {
					AT3110 = "1";
					this.cardLevel = 1;
				} else {
					AT3110 = "0";
					this.cardLevel = 0;
				}
			}
			tempVariable = new VariableBean("AT3110", AT3110, "N", "EX",
					"是否VIP客户");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3110", AT3110);

			String AT3160 = "-9";
			attrString = variable.getAttribute("PriPos").trim();
			if ("301".equals(attrString) || "302".equals(attrString)
					|| "303".equals(attrString) || "304".equals(attrString)) {
				AT3160 = "1";
			} else {
				AT3160 = "0";
			}
			tempVariable = new VariableBean("AT3160", AT3160, "N", "EX",
					"是否在读学生");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3160", AT3160);

			// 是否为员工卡
			String AT3170 = "-9";
			attrString = variable.getAttribute("BKSTF").trim();
			/**C1507封版后，统一改为判断1和Y*/
			if ("Y".equalsIgnoreCase(attrString) || "1".equalsIgnoreCase(attrString)) {
				AT3170 = "1";
			} else if ("N".equalsIgnoreCase(attrString) || "2".equalsIgnoreCase(attrString)
					|| "0".equalsIgnoreCase(attrString)) {
				AT3170 = "0";
			}
			tempVariable = new VariableBean("AT3170", AT3170, "N", "EX",
					"是否为员工卡");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3170", AT3170);

			// 银联保留字段处理
			String AT3200 = "-9";
			attrString = variable.getAttribute("CredCcardL6M3Num").trim();
			if (this.isInt(attrString)) {
				AT3200 = attrString;
			}
			tempVariable = new VariableBean("AT3200", AT3200, "N", "EX",
					"贷记卡近6个月M3+的次数");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3200", AT3200);

			String AT3210 = "-9";
			attrString = variable.getAttribute("ReservedField2").trim();
			if (attrString != null && !"".equals(attrString)) {
				AT3210 = attrString;
			}
			tempVariable = new VariableBean("AT3210", AT3210, "N", "EX",
					"预留字段2");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3210", AT3210);

			/*
			 * String AT3220 = "-9"; attrString =
			 * variable.getAttribute("ReservedField3").trim(); if (attrString !=
			 * null && !"".equals(attrString)) { AT3220 = attrString; }
			 * tempVariable = new VariableBean("AT3220", AT3220, "N", "EX",
			 * "预留字段3"); attachVariableA.add(tempVariable);
			 * //log.appendParameter("AT3220", AT3220);
			 * 
			 * String AT3230 = "-9"; attrString =
			 * variable.getAttribute("ReservedField4").trim(); if (attrString !=
			 * null && !"".equals(attrString)) { AT3230 = attrString; }
			 * tempVariable = new VariableBean("AT3230", AT3230, "N", "EX",
			 * "预留字段4"); attachVariableA.add(tempVariable);
			 * //log.appendParameter("AT3230", AT3230);
			 * 
			 * String AT3240 = "-9"; attrString =
			 * variable.getAttribute("ReservedField5").trim(); if (attrString !=
			 * null && !"".equals(attrString)) { AT3240 = attrString; }
			 * tempVariable = new VariableBean("AT3240", AT3240, "N", "EX",
			 * "预留字段5"); attachVariableA.add(tempVariable);
			 * //log.appendParameter("AT3240", AT3240);
			 * 
			 * String AT3250 = "-9"; attrString =
			 * variable.getAttribute("ReservedField6").trim(); if (attrString !=
			 * null && !"".equals(attrString)) { AT3250 = attrString; }
			 * tempVariable = new VariableBean("AT3250", AT3250, "N", "EX",
			 * "预留字段6"); attachVariableA.add(tempVariable);
			 * //log.appendParameter("AT3250", AT3250);
			 * 
			 * String AT3260 = "-9"; attrString =
			 * variable.getAttribute("ReservedField7").trim(); if (attrString !=
			 * null && !"".equals(attrString)) { AT3260 = attrString; }
			 * tempVariable = new VariableBean("AT3260", AT3260, "N", "EX",
			 * "预留字段7"); attachVariableA.add(tempVariable);
			 * //log.appendParameter("AT3260", AT3260);
			 * 
			 * String AT3270 = "-9"; attrString =
			 * variable.getAttribute("ReservedField8").trim(); if (attrString !=
			 * null && !"".equals(attrString)) { AT3270 = attrString; }
			 * tempVariable = new VariableBean("AT3270", AT3270, "N", "EX",
			 * "预留字段8"); attachVariableA.add(tempVariable);
			 * //log.appendParameter("AT3270", AT3270);
			 * 
			 * String AT3280 = "-9"; attrString =
			 * variable.getAttribute("ReservedField9").trim(); if (attrString !=
			 * null && !"".equals(attrString)) { AT3280 = attrString; }
			 * tempVariable = new VariableBean("AT3280", AT3280, "N", "EX",
			 * "预留字段9"); attachVariableA.add(tempVariable);
			 * //log.appendParameter("AT3280", AT3280);
			 * 
			 * String AT3290 = "-9"; attrString =
			 * variable.getAttribute("ReservedField10").trim(); if (attrString
			 * != null && !"".equals(attrString)) { AT3290 = attrString; }
			 * tempVariable = new VariableBean("AT3290", AT3290, "N", "EX",
			 * "预留字段10"); attachVariableA.add(tempVariable);
			 * //log.appendParameter("AT3290", AT3290);
			 * 
			 * String AT3300 = "-9"; attrString =
			 * variable.getAttribute("ReservedField11").trim(); if (attrString
			 * != null && !"".equals(attrString)) { AT3300 = attrString; }
			 * tempVariable = new VariableBean("AT3300", AT3300, "N", "EX",
			 * "预留字段11"); attachVariableA.add(tempVariable);
			 * //log.appendParameter("AT3300", AT3300);
			 * 
			 * String AT3310 = "-9"; attrString =
			 * variable.getAttribute("ReservedField12").trim(); if (attrString
			 * != null && !"".equals(attrString)) { AT3310 = attrString; }
			 * tempVariable = new VariableBean("AT3310", AT3310, "N", "EX",
			 * "预留字段12"); attachVariableA.add(tempVariable);
			 * //log.appendParameter("AT3310", AT3310);
			 * 
			 * String AT3320 = "-9"; attrString =
			 * variable.getAttribute("ReservedField13").trim(); if (attrString
			 * != null && !"".equals(attrString)) { AT3320 = attrString; }
			 * tempVariable = new VariableBean("AT3320", AT3320, "N", "EX",
			 * "预留字段13"); attachVariableA.add(tempVariable);
			 * //log.appendParameter("AT3320", AT3320);
			 * 
			 * String AT3330 = "-9"; attrString =
			 * variable.getAttribute("ReservedField14").trim(); if (attrString
			 * != null && !"".equals(attrString)) { AT3330 = attrString; }
			 * tempVariable = new VariableBean("AT3330", AT3330, "N", "EX",
			 * "预留字段14"); attachVariableA.add(tempVariable);
			 * //log.appendParameter("AT3330", AT3330);
			 * 
			 * String AT3340 = "-9"; attrString =
			 * variable.getAttribute("ReservedField15").trim(); if (attrString
			 * != null && !"".equals(attrString)) { AT3340 = attrString; }
			 * tempVariable = new VariableBean("AT3340", AT3340, "N", "EX",
			 * "预留字段15"); attachVariableA.add(tempVariable);
			 * //log.appendParameter("AT3340", AT3340);
			 */
			String AT3350 = "-9";
			attrString = variable.getAttribute("ReservedField16").trim();
			if (attrString != null && !"".equals(attrString)) {
				AT3350 = attrString;
			}
			tempVariable = new VariableBean("AT3350", AT3350, "N", "EX",
					"预留字段16");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3350", AT3350);

			String AT3360 = "-9";
			attrString = variable.getAttribute("ReservedField17").trim();
			if (attrString != null && !"".equals(attrString)) {
				AT3360 = attrString;
			}
			tempVariable = new VariableBean("AT3360", AT3360, "N", "EX",
					"预留字段17");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3360", AT3360);

			String AT3370 = "-9";
			attrString = variable.getAttribute("ReservedField18").trim();
			if (attrString != null && !"".equals(attrString)) {
				AT3370 = attrString;
			}
			tempVariable = new VariableBean("AT3370", AT3370, "N", "EX",
					"预留字段18");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3370", AT3370);

			String AT3380 = "-9";
			attrString = variable.getAttribute("ReservedField19").trim();
			if (attrString != null && !"".equals(attrString)) {
				AT3380 = attrString;
			}
			tempVariable = new VariableBean("AT3380", AT3380, "N", "EX",
					"预留字段19");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3380", AT3380);

			String AT3390 = "-9";
			attrString = variable.getAttribute("ReservedField20").trim();
			if (attrString != null && !"".equals(attrString)) {
				AT3390 = attrString;
			}
			tempVariable = new VariableBean("AT3390", AT3390, "N", "EX",
					"预留字段20");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3390", AT3390);

			// 其他预留字段
			String AT2440 = "-9";
			attrString = variable.getAttribute("ResiAddrAprv").trim();
			/**C1507封版后，统一改为判断1和Y*/
			if ("Y".equalsIgnoreCase(attrString) || "1".equalsIgnoreCase(attrString)) {
				AT2440 = "1";
			} else if ("N".equalsIgnoreCase(attrString) || "2".equalsIgnoreCase(attrString)
					|| "0".equalsIgnoreCase(attrString)) {
				AT2440 = "0";
			}
			tempVariable = new VariableBean("AT2440", AT2440, "N", "EX",
					"是否提供固定居住地址证明文件");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT2440", AT2440);
			// 修改可证明资产类型和数值----赵伟---2008-10-23
			String AT3400 = "-9";
			// 提供资产证明的数量
			String AT3525 = "-9";
			int assetCount = 0;
			int houseCount = 0;
			int carCount = 0;
			int fundCount = 0;
			int curDepositCount = 0;
			int fixDepositCount = 0;
			int insuranceCount = 0;
			double houseArea = 0.0f;
			double housesum = 0.0f;
			double carValue = 0.0f;
			double fundValue = 0.0f;
			double curDepositValue = 0.0f;
			double fixDepositValue = 0.0f;
			double insuranceValue = 0.0f;
			attrString = variable.getAttribute("CredAssetType1").trim();

			String[] CredAssetType1Array = new String[] { "21", "22", "23",
					"24", "25", "26" };
			if (this.verifyValue(attrString, CredAssetType1Array)) {
				AT3400 = attrString;
				int type = Integer.parseInt(AT3400);
				if (type > 0) {
					if (type == 21) {
						houseCount += 1;
					}
					if (type == 22) {
						carCount += 1;
					}
					if (type == 23) {
						fundCount += 1;
					}
					if (type == 24) {
						curDepositCount += 1;
					}
					if (type == 25) {
						fixDepositCount += 1;
					}
					if (type == 26) {
						insuranceCount += 1;
					}
					assetCount += 1;
				}
			}
			tempVariable = new VariableBean("AT3400", AT3400, "N", "EX",
					"可证明的资产类型1");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3400", AT3400);

			String AT3410 = "-9";
			attrString = variable.getAttribute("CredAssetValue1").trim();
			if (this.isNumeric(attrString)) {
				double typeValue = toDouble(attrString);
				if (AT3400.equalsIgnoreCase("21")) {
					houseArea += typeValue;
					housesum += Double.parseDouble(attrString);
				}
				if (AT3400.equalsIgnoreCase("22")) {
					carValue += typeValue;
				}
				if (AT3400.equalsIgnoreCase("23")) {
					fundValue += typeValue;
				}
				if (AT3400.equalsIgnoreCase("24")) {
					curDepositValue += typeValue;
				}
				if (AT3400.equalsIgnoreCase("25")) {
					fixDepositValue += typeValue;
				}
				if (AT3400.equalsIgnoreCase("26")) {
					insuranceValue += typeValue;
				}

				AT3410 = attrString;
			}
			tempVariable = new VariableBean("AT3410", AT3410, "N", "EX",
					"可证明的资产价值1");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3410", AT3410);

			String AT3420 = "-9";
			attrString = variable.getAttribute("CredAssetType2").trim();
			String[] credAssetType2Array = new String[] { "21", "22", "23",
					"24", "25", "26" };
			if (this.verifyValue(attrString, credAssetType2Array)) {
				AT3420 = attrString;
				int type1 = Integer.parseInt(AT3420);
				if (type1 > 0) {
					if (type1 == 21) {
						houseCount += 1;
					}
					if (type1 == 22) {
						carCount += 1;
					}
					if (type1 == 23) {
						fundCount += 1;
					}
					if (type1 == 24) {
						curDepositCount += 1;
					}
					if (type1 == 25) {
						fixDepositCount += 1;
					}
					if (type1 == 26) {
						insuranceCount += 1;
					}

					assetCount += 1;
				}
			}
			tempVariable = new VariableBean("AT3420", AT3420, "N", "EX",
					"可证明的资产类型2");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3420", AT3420);

			String AT3430 = "-9";
			attrString = variable.getAttribute("CredAssetValue2").trim();
			if (this.isNumeric(attrString)) {
				double typeValue1 = toDouble(attrString);
				if (AT3420.equalsIgnoreCase("21")) {
					houseArea += typeValue1;
					housesum += Double.parseDouble(attrString);
				}
				if (AT3420.equalsIgnoreCase("22")) {
					carValue += typeValue1;
				}
				if (AT3420.equalsIgnoreCase("23")) {
					fundValue += typeValue1;
				}
				if (AT3420.equalsIgnoreCase("24")) {
					curDepositValue += typeValue1;
				}
				if (AT3420.equalsIgnoreCase("25")) {
					fixDepositValue += typeValue1;
				}
				if (AT3420.equalsIgnoreCase("26")) {
					insuranceValue += typeValue1;
				}

				AT3430 = attrString;
			}
			tempVariable = new VariableBean("AT3430", AT3430, "N", "EX",
					"可证明的资产价值2");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3430", AT3430);

			String AT3440 = "-9";
			// 提供资产证明的数量
			attrString = variable.getAttribute("CredAssetType3").trim();
			String[] credAssetType3Array = new String[] { "21", "22", "23",
					"24", "25", "26" };
			if (this.verifyValue(attrString, credAssetType3Array)) {
				AT3440 = attrString;

				int type2 = Integer.parseInt(AT3440);
				if (type2 > 0) {
					if (type2 == 21) {
						houseCount += 1;
					}
					if (type2 == 22) {
						carCount += 1;
					}
					if (type2 == 23) {
						fundCount += 1;
					}
					if (type2 == 24) {
						curDepositCount += 1;
					}
					if (type2 == 25) {
						fixDepositCount += 1;
					}
					if (type2 == 26) {
						insuranceCount += 1;
					}

					assetCount += 1;
				}

			}
			tempVariable = new VariableBean("AT3440", AT3440, "N", "EX",
					"可证明的资产类型3");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3440", AT3440);

			String AT3450 = "-9";
			attrString = variable.getAttribute("CredAssetValue3").trim();
			if (this.isNumeric(attrString)) {
				double typeValue2 = toDouble(attrString);
				if (AT3440.equalsIgnoreCase("21")) {
					houseArea += typeValue2;
					housesum += Double.parseDouble(attrString);
				}
				if (AT3440.equalsIgnoreCase("22")) {
					carValue += typeValue2;
				}
				if (AT3440.equalsIgnoreCase("23")) {
					fundValue += typeValue2;
				}
				if (AT3440.equalsIgnoreCase("24")) {
					curDepositValue += typeValue2;
				}
				if (AT3440.equalsIgnoreCase("25")) {
					fixDepositValue += typeValue2;
				}
				if (AT3440.equalsIgnoreCase("26")) {
					insuranceValue += typeValue2;
				}

				AT3450 = attrString;
			}
			tempVariable = new VariableBean("AT3450", AT3450, "N", "EX",
					"可证明的资产价值3");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3450", AT3450);

			// 新加资产价值类型 4，5，6 zhaow 2008-10-23
			// 可证明资产类型4
			String AT3460 = "-9";
			// 提供资产证明的数量
			attrString = variable.getAttribute("CredAssetType4").trim();
			String[] credAssetType4Array = new String[] { "21", "22", "23",
					"24", "25", "26" };
			if (this.verifyValue(attrString, credAssetType4Array)) {
				AT3460 = attrString;

				int type3 = Integer.parseInt(AT3460);
				if (type3 > 0) {
					if (type3 == 21) {
						houseCount += 1;
					}
					if (type3 == 22) {
						carCount += 1;
					}
					if (type3 == 23) {
						fundCount += 1;
					}
					if (type3 == 24) {
						curDepositCount += 1;
					}
					if (type3 == 25) {
						fixDepositCount += 1;
					}
					if (type3 == 26) {
						insuranceCount += 1;
					}

					assetCount += 1;
				}

			}
			tempVariable = new VariableBean("AT3460", AT3460, "N", "EX",
					"可证明的资产类型4");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3440", AT3440);

			String AT3470 = "-9";
			attrString = variable.getAttribute("CredAssetValue4").trim();
			if (this.isNumeric(attrString)) {
				double typeValue3 = toDouble(attrString);
				if (AT3460.equalsIgnoreCase("21")) {
					houseArea += typeValue3;
					housesum += Double.parseDouble(attrString);
				}
				if (AT3460.equalsIgnoreCase("22")) {
					carValue += typeValue3;
				}
				if (AT3460.equalsIgnoreCase("23")) {
					fundValue += typeValue3;
				}
				if (AT3460.equalsIgnoreCase("24")) {
					curDepositValue += typeValue3;
				}
				if (AT3460.equalsIgnoreCase("25")) {
					fixDepositValue += typeValue3;
				}
				if (AT3460.equalsIgnoreCase("26")) {
					insuranceValue += typeValue3;
				}

				AT3470 = attrString;
			}
			tempVariable = new VariableBean("AT3470", AT3470, "N", "EX",
					"可证明的资产价值4");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3450", AT3450);

			// 可证明资产类型5
			String AT3480 = "-9";
			// 提供资产证明的数量
			attrString = variable.getAttribute("CredAssetType5").trim();
			String[] credAssetType5Array = new String[] { "21", "22", "23",
					"24", "25", "26" };
			if (this.verifyValue(attrString, credAssetType5Array)) {
				AT3480 = attrString;

				int type4 = Integer.parseInt(AT3480);
				if (type4 > 0) {
					if (type4 == 21) {
						houseCount += 1;
					}
					if (type4 == 22) {
						carCount += 1;
					}
					if (type4 == 23) {
						fundCount += 1;
					}
					if (type4 == 24) {
						curDepositCount += 1;
					}
					if (type4 == 25) {
						fixDepositCount += 1;
					}
					if (type4 == 26) {
						insuranceCount += 1;
					}

					assetCount += 1;
				}

			}
			tempVariable = new VariableBean("AT3480", AT3480, "N", "EX",
					"可证明的资产类型5");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3440", AT3440);

			String AT3490 = "-9";
			attrString = variable.getAttribute("CredAssetValue5").trim();
			if (this.isNumeric(attrString)) {
				double typeValue4 = toDouble(attrString);
				if (AT3480.equalsIgnoreCase("21")) {
					houseArea += typeValue4;
					housesum += Double.parseDouble(attrString);
				}
				if (AT3480.equalsIgnoreCase("22")) {
					carValue += typeValue4;
				}
				if (AT3480.equalsIgnoreCase("23")) {
					fundValue += typeValue4;
				}
				if (AT3480.equalsIgnoreCase("24")) {
					curDepositValue += typeValue4;
				}
				if (AT3480.equalsIgnoreCase("25")) {
					fixDepositValue += typeValue4;
				}
				if (AT3480.equalsIgnoreCase("26")) {
					insuranceValue += typeValue4;
				}

				AT3490 = attrString;
			}
			tempVariable = new VariableBean("AT3490", AT3490, "N", "EX",
					"可证明的资产价值5");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3450", AT3450);
			// 可证明资产类型6
			String AT3500 = "-9";
			// 提供资产证明的数量
			attrString = variable.getAttribute("CredAssetType6").trim();
			String[] credAssetType6Array = new String[] { "21", "22", "23",
					"24", "25", "26" };
			if (this.verifyValue(attrString, credAssetType6Array)) {
				AT3500 = attrString;

				int type5 = Integer.parseInt(AT3500);
				if (type5 > 0) {
					if (type5 == 21) {
						houseCount += 1;
					}
					if (type5 == 22) {
						carCount += 1;
					}
					if (type5 == 23) {
						fundCount += 1;
					}
					if (type5 == 24) {
						curDepositCount += 1;
					}
					if (type5 == 25) {
						fixDepositCount += 1;
					}
					if (type5 == 26) {
						insuranceCount += 1;
					}

					assetCount += 1;
				}

			}
			tempVariable = new VariableBean("AT3500", AT3500, "N", "EX",
					"可证明的资产类型6");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3440", AT3440);

			String AT3510 = "-9";
			attrString = variable.getAttribute("CredAssetValue6").trim();
			if (this.isNumeric(attrString)) {
				double typeValue5 = toDouble(attrString);
				if (AT3500.equalsIgnoreCase("21")) {
					houseArea += typeValue5;
					housesum += Double.parseDouble(attrString);
				}
				if (AT3500.equalsIgnoreCase("22")) {
					carValue += typeValue5;
				}
				if (AT3500.equalsIgnoreCase("23")) {
					fundValue += typeValue5;
				}
				if (AT3500.equalsIgnoreCase("24")) {
					curDepositValue += typeValue5;
				}
				if (AT3500.equalsIgnoreCase("25")) {
					fixDepositValue += typeValue5;
				}
				if (AT3500.equalsIgnoreCase("26")) {
					insuranceValue += typeValue5;
				}

				AT3510 = attrString;
			}
			tempVariable = new VariableBean("AT3510", AT3510, "N", "EX",
					"可证明的资产价值6");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3450", AT3450);
			// 修改部分
			String AT3860 = String.valueOf(houseCount);
			String AT3880 = String.valueOf(carCount);
			String AT3900 = String.valueOf(fundCount);
			String AT3920 = String.valueOf(curDepositCount);
			String AT3940 = String.valueOf(fixDepositCount);
			String AT3960 = String.valueOf(insuranceCount);
			String AT3870 = String.valueOf(houseArea);
			String AT3890 = String.valueOf(carValue);
			String AT3910 = String.valueOf(fundValue);
			String AT3930 = String.valueOf(curDepositValue);
			String AT3950 = String.valueOf(fixDepositValue);
			String AT3970 = String.valueOf(insuranceValue);
			tempVariable = new VariableBean("AT3860", AT3860, "N", "EX",
					"自有房产证明数量");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3860", AT3860);

			tempVariable = new VariableBean("AT3870", AT3870, "N", "EX",
					"自有房产总建筑面积");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3870", AT3870);

			tempVariable = new VariableBean("AT3880", AT3880, "N", "EX",
					"自有汽车证明数量");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3880", AT3880);

			tempVariable = new VariableBean("AT3890", AT3890, "N", "EX",
					"自有汽车证明总价值");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3890", AT3890);

			tempVariable = new VariableBean("AT3900", AT3900, "N", "EX",
					"基金债券证明数量");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3900", AT3900);

			tempVariable = new VariableBean("AT3910", AT3910, "N", "EX",
					"基金债券证明总价值");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3910", AT3910);

			tempVariable = new VariableBean("AT3920", AT3920, "N", "EX",
					"活期存款证明数量");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3920", AT3920);

			tempVariable = new VariableBean("AT3930", AT3930, "N", "EX",
					"活期存款证明总价值");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3930", AT3930);

			tempVariable = new VariableBean("AT3940", AT3940, "N", "EX",
					"定期存款证明数量");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3940", AT3940);

			tempVariable = new VariableBean("AT3950", AT3950, "N", "EX",
					"定期存款证明总价值");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3950", AT3950);

			tempVariable = new VariableBean("AT3960", AT3960, "N", "EX",
					"人寿保险证明数量");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3960", AT3960);

			tempVariable = new VariableBean("AT3970", AT3970, "N", "EX",
					"人寿保险证明总价值");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3970", AT3970);
			
			String AT20011 = "";// 审批状态：
			attrString = variable.getAttribute("AT20011").trim();
			if (null == attrString || "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20011 = "-999";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT20011 = "1";
				} else if ("2".equalsIgnoreCase(attrString)
						|| "N".equalsIgnoreCase(attrString)) {
					AT20011 = "2";
				} else{
					AT20011 = "-999";
				}
			}
			tempVariable = new VariableBean("AT20011", AT20011, "N", "EX",
					"审批状态");
			attachVariableA.add(tempVariable);
			// 增加可证明的收入类型和可证明收入类型值
			String AT3220 = "";
			attrString = variable.getAttribute("CredIncome1").trim();
			if (attrString != null && !"".equals(attrString)) {
				AT3220 = attrString;
			}
			//tempVariable = new VariableBean("AT3220", AT3220, "N", "EX","可证明的收入类型1");
			//attachVariableA.add(tempVariable);

			String AT3230 = "0";

			attrString = variable.getAttribute("CredIncome1Value").trim();
			if (attrString != null && !"".equals(attrString)) {
				AT3230 = attrString;
			}
			tempVariable = new VariableBean("AT3230", AT3230, "N", "EX",
					"可证明的收入类型值1");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3290", AT3290);
			String AT3240 = "";
			attrString = variable.getAttribute("CredIncome2").trim();
			if (attrString != null && !"".equals(attrString)) {
				AT3240 = attrString;
			}
			tempVariable = new VariableBean("AT3240", AT3240, "N", "EX","可证明的收入类型2");
			attachVariableA.add(tempVariable);
			if("2".equalsIgnoreCase(AT20011) && "".equals(AT3220) && "".equals(AT3240)){
				AT3220 = "17";
			}
			tempVariable = new VariableBean("AT3220", AT3220, "N", "EX","可证明的收入类型1");
			attachVariableA.add(tempVariable);
			
			String AT3250 = "0";
			attrString = variable.getAttribute("CredIncome2Value").trim();
			if (attrString != null && !"".equals(attrString)) {
				AT3250 = attrString;
			}
			tempVariable = new VariableBean("AT3250", AT3250, "N", "EX",
					"可证明的收入类型值2");
			attachVariableA.add(tempVariable);
			// 主卡人与联系人关系
			String AT3260 = "-9";
			attrString = variable.getAttribute("AppiMcContactRelship").trim();
			String[] appiMcContactArray = new String[] { "11", "12", "13", "14" };
			if(null == attrString || "".equals(attrString) ||"-999".equals(attrString)|| "null".equalsIgnoreCase(attrString)){
				AT3260 = "-999";
			}else if (this.verifyValue(attrString, appiMcContactArray)) {
				AT3260 = attrString;
			}else{
				AT3260 = "-9";
			}
			tempVariable = new VariableBean("AT3260", AT3260, "N", "EX",
					"主卡人与联系人关系");
			attachVariableA.add(tempVariable);

			/*
			 * String AT3460 = "-9"; attrString =
			 * variable.getAttribute("AprAssetType4").trim(); String[]
			 * aprAssetType4Array = new String[] {"0", "1", "2", "3", "4", "5",
			 * "6"}; if (this.verifyValue(attrString, aprAssetType4Array)) {
			 * AT3460 = attrString; if (Integer.parseInt(AT3460) > 0) {
			 * assetCount += ; } } tempVariable = new VariableBean("AT3460",
			 * AT3460, "N", "EX", "可证明的资产类型4");
			 * attachVariableA.add(tempVariable);
			 * //log.appendParameter("AT3460", AT3460);
			 * 
			 * String AT3470 = "-9";
			 * 
			 * attrString = variable.getAttribute("AprAssetValue4").trim(); if
			 * (this.isNumeric(attrString)) { AT3470 = attrString; }
			 * tempVariable = new VariableBean("AT3470", AT3470, "N", "EX",
			 * "可证明的资产价值4"); attachVariableA.add(tempVariable);
			 * //log.appendParameter("AT3470", AT3470);
			 * 
			 * String AT3480 = "-9"; attrString =
			 * variable.getAttribute("AprAssetType5").trim(); String[]
			 * aprAssetType5Array = new String[] {"0", "1", "2", "3", "4", "5",
			 * "6"}; if (this.verifyValue(attrString, aprAssetType5Array)) {
			 * AT3480 = attrString; if (Integer.parseInt(AT3480) > 0) {
			 * assetCount += 1; } } tempVariable = new VariableBean("AT3480",
			 * AT3480, "N", "EX", "可证明的资产类型5");
			 * attachVariableA.add(tempVariable);
			 * //log.appendParameter("AT3480", AT3480);
			 * 
			 * String AT3490 = "-9"; attrString =
			 * variable.getAttribute("AprAssetValue5").trim(); if
			 * (this.isNumeric(attrString)) { AT3490 = attrString; }
			 * tempVariable = new VariableBean("AT3490", AT3490, "N", "EX",
			 * "可证明的资产价值5"); attachVariableA.add(tempVariable);
			 * //log.appendParameter("AT3490", AT3490);
			 * 
			 * String AT3500 = "-9"; attrString =
			 * variable.getAttribute("AprAssetType6").trim(); String[]
			 * aprAssetType6Array = new String[] {"0", "1", "2", "3", "4", "5",
			 * "6"}; if (this.verifyValue(attrString, aprAssetType6Array)) {
			 * AT3500 = attrString; if (Integer.parseInt(AT3500) > 0) {
			 * assetCount += 1; } } tempVariable = new VariableBean("AT3500",
			 * AT3500, "N", "EX", "可证明的资产类型6");
			 * attachVariableA.add(tempVariable);
			 * //log.appendParameter("AT3500", AT3500);
			 * 
			 * //提供资产证明的数量 if (assetCount > 0) { AT3525 =
			 * String.valueOf(assetCount); } tempVariable = new
			 * VariableBean("AT3525", AT3525, "N", "EX", "提供资产证明的数量");
			 * attachVariableA.add(tempVariable);
			 * //log.appendParameter("AT3525", AT3525);
			 * 
			 * String AT3510 = "-9"; attrString =
			 * variable.getAttribute("AprAssetValue6").trim();
			 * 
			 * if (this.isNumeric(attrString)) { AT3510 = attrString; }
			 * tempVariable = new VariableBean("AT3510", AT3510, "N", "EX",
			 * "可证明的资产价值6"); attachVariableA.add(tempVariable);
			 * //log.appendParameter("AT3510", AT3510);
			 */

			String AT3520 = "0";
			attrString = variable.getAttribute("AprAnnIncome").trim();
			if (this.isNumeric(attrString)) {
				AT3520 = attrString;
			}
			tempVariable = new VariableBean("AT3520", AT3520, "N", "EX",
					"可证明的年收入额");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3520", AT3520);

			String AT3530 = "-9";
			attrString = variable.getAttribute("AprSpeGrpProof").trim();
			// String[] aprSpeGrpArray = new String[] { "0", "8100", "8200",
			// "8500","8600","8700","8810","8820","8900","8300", "8410", "8420"
			// };
			// if (this.verifyValue(attrString, aprSpeGrpArray)) {
			// AT3530 = attrString;
			// }else{
			AT3530 = attrString;
			// }
			tempVariable = new VariableBean("AT3530", AT3530, "N", "EX", "客户分类");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3530", AT3530);

			String AT3540 = "-9";
			attrString = variable.getAttribute("AprPosLevl").trim();
			String[] aprPosLevlArray = new String[] { "0", "1", "2", "3", "4" };
			if (this.verifyValue(attrString, aprPosLevlArray)) {
				AT3540 = attrString;
			}
			tempVariable = new VariableBean("AT3540", AT3540, "N", "EX",
					"可证明的职务级别");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3540", AT3540);

			String AT3550 = "-9";
			attrString = variable.getAttribute("AprWrkSeniority").trim();
			if (this.isNumeric(attrString)) {
				AT3550 = attrString;
			}
			tempVariable = new VariableBean("AT3550", AT3550, "N", "EX",
					"可证明的从业年限");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3550", AT3550);

			String AT3560 = "-9";
			attrString = variable.getAttribute("IfHiRiskBiz").trim();
			String[] ifHiRiskBizArray = new String[] { "0", "1", "2" };
			if (this.verifyValue(attrString, ifHiRiskBizArray)) {
				AT3560 = attrString;
			}
			tempVariable = new VariableBean("AT3560", AT3560, "N", "EX",
					"是否从事高风险行业");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3560", AT3560);

			String AT3570 = "-9";
			attrString = variable.getAttribute("IfSSInfo").trim();
			String[] ifSSInfoArray = new String[] { "0", "1", "2" };
			if (this.verifyValue(attrString, ifSSInfoArray)) {
				AT3570 = attrString;
			}
			tempVariable = new VariableBean("AT3570", AT3570, "N", "EX",
					"是否获取社保信息");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3570", AT3570);

			String AT3580 = "-9";
			attrString = variable.getAttribute("IfPBOCInfo").trim();
			String[] ifPBOCInfoArray = new String[] { "0", "1", "2" };
			if (this.verifyValue(attrString, ifPBOCInfoArray)) {
				AT3580 = attrString;
			}
			tempVariable = new VariableBean("AT3580", AT3580, "N", "EX",
					"是否获取征信信息");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3580", AT3580);
			//
			// String AT3590 = "-9";
			// attrString = variable.getAttribute("IfCBRCInfo").trim();
			// String[] ifCBRCInfoArray = new String[] { "0", "1", "2" };
			// if (this.verifyValue(attrString, ifCBRCInfoArray)) {
			// AT3590 = attrString;
			// }
			// tempVariable = new VariableBean("AT3590", AT3590, "N", "EX",
			// "是否获取银联信息");
			// attachVariableA.add(tempVariable);
			// // log.appendParameter("AT3590", AT3590);
			//
			String AT3600 = "-9";
			attrString = variable.getAttribute("IfMPSInfo").trim();
			String[] ifMPSInfoArray = new String[] { "0", "1", "2" };
			if (this.verifyValue(attrString, ifMPSInfoArray)) {
				AT3600 = attrString;
			}
			tempVariable = new VariableBean("AT3600", AT3600, "N", "EX",
					"是否获取公安信息");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3600", AT3600);

			String AT3610 = "-9";
			attrString = variable.getAttribute("CurrSSMonthPay").trim();
			if (this.isNumeric(attrString)) {
				AT3610 = attrString;
			}
			tempVariable = new VariableBean("AT3610", AT3610, "N", "EX",
					"当前社保月缴款额");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3610", AT3610);

			String AT3620 = "-9";
			attrString = variable.getAttribute("LstSSPayDate").trim();
			if (attrString != null && !"".equals(attrString)) {
				AT3620 = attrString;
			}
			tempVariable = new VariableBean("AT3620", AT3620, "N", "EX",
					"最近缴费年月");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3620", AT3620);

			String AT3630 = "-9";
			attrString = variable.getAttribute("PayMonth").trim();
			if (this.isNumeric(attrString)) {
				AT3630 = attrString;
			}
			tempVariable = new VariableBean("AT3630", AT3630, "N", "EX",
					"连续缴费的月数");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3630", AT3630);

			String AT3640 = "-9";
			attrString = variable.getAttribute("MPSBirthDate").trim();
			if (attrString.length() >= 10) {
				attrString = this.changeDataFormat(attrString);
			}
			if (this.isDate(attrString)) {
				AT3640 = attrString;
			}
			tempVariable = new VariableBean("AT3640", AT3640, "N", "EX",
					"公安提供的出生日期");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3640", AT3640);

			// 根据公安提供的出生日期和主卡人出生日期计算A3645
			String AT3645 = "-9";
			attrString = AT3640;
			if (attrString.length() >= 10) {
				attrString = this.changeDataFormat(attrString);
			}
			if (this.isDate(attrString)) {
				if (!attrString.equals(AT0140)) {
					AT3645 = "0";
				}
			}
			tempVariable = new VariableBean("AT3645", AT3645, "N", "EX",
					"出生日期是否相符");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3645", AT3645);

			String AT3650 = "-9";
			attrString = variable.getAttribute("NumCCardAct").trim();
			if (this.isInt(attrString)) {
				AT3650 = attrString;
			}
			tempVariable = new VariableBean("AT3650", AT3650, "N", "EX",
					"贷记卡帐户总数");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3650", AT3650);

			String AT3660 = "-9";
			attrString = variable.getAttribute("CredCcardQcctNum").trim();
			if (this.isInt(attrString)) {
				AT3660 = attrString;
			}
			tempVariable = new VariableBean("AT3660", AT3660, "N", "EX",
					"准贷记卡帐户数");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3660", AT3660);

			String AT3670 = "-9";
			attrString = variable.getAttribute("TotlCCardCLmt").trim();
			if (this.isNumeric(attrString)) {
				AT3670 = attrString;
			}
			tempVariable = new VariableBean("AT3670", AT3670, "N", "EX",
					"贷记卡当月总信用额度");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3670", AT3670);

			String AT3680 = "-9";
			attrString = variable.getAttribute("TotlCCardOLmt").trim();
			if (this.isNumeric(attrString)) {
				AT3680 = attrString;
			}
			tempVariable = new VariableBean("AT3680", AT3680, "N", "EX",
					"贷记卡当月总透支额度");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3680", AT3680);

			String AT3690 = "-9";
			attrString = variable.getAttribute("CCardFrtOpnDate").trim();

			if (attrString.length() >= 10) {
				attrString = this.changeDataFormat(attrString);
			}
			if (this.isDate(attrString)) {
				AT3690 = attrString.substring(0, 4);
			}
			tempVariable = new VariableBean("AT3690", AT3690, "N", "EX",
					"贷记卡最早开户日期");
			attachVariableA.add(tempVariable);

			// log.appendParameter("AT3690", AT3690);

			// 获得贷记卡开户月数
			String AT3695 = "-9";
			attrString = variable.getAttribute("CCardFrtOpnDate").trim();
			if (attrString.length() >= 10) {
				attrString = this.changeDataFormat(attrString);
			}
			int months = 0;
			if (this.isDate(attrString)) {
				months = this.getMonthes(attrString);
				AT3695 = String.valueOf(months);
			}
			tempVariable = new VariableBean("AT3695", AT3695, "N", "EX",
					"贷记卡最早开户开户月数");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3695", AT3695);

			String AT3700 = "-9";
			attrString = variable.getAttribute("CCardLstOpnDate").trim();
			if (attrString.length() >= 10) {
				attrString = this.changeDataFormat(attrString);
			}
			if (this.isDate(attrString)) {
				AT3700 = attrString;
			}
			tempVariable = new VariableBean("AT3700", AT3700, "N", "EX",
					"贷记卡最晚开户日期");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3700", AT3700);

			// 获得贷记卡开户月数
			String AT3705 = "-9";
			attrString = AT3700;
			if (attrString.length() >= 10) {
				attrString = this.changeDataFormat(attrString);
			}
			if (this.isDate(attrString)) {
				months = this.getMonthes(attrString);
				AT3705 = String.valueOf(months);
			}
			tempVariable = new VariableBean("AT3705", AT3705, "N", "EX",
					"贷记卡最晚开户开户月数");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3705", AT3705);

			// 贷记卡单卡最高额度（人民币）
			String AT3710 = "-9";
			attrString = variable.getAttribute("CCardHiLimit").trim();
			if (this.isNumeric(attrString)) {
				AT3710 = attrString;
			}
			tempVariable = new VariableBean("AT3710", AT3710, "N", "EX",
					"贷记卡单卡最高额度（人民币）");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3710", AT3710);

			// 贷记卡最近6个月M1的次数
			String AT3720 = "-9";
			attrString = variable.getAttribute("CCardM1In6M").trim();
			if (this.isInt(attrString)) {
				AT3720 = attrString;
			}
			tempVariable = new VariableBean("AT3720", AT3720, "N", "EX",
					"贷记卡最近6个月M1的次数");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3720", AT3720);

			// 贷记卡近6个月M2次数
			String AT3725 = "-9";
			attrString = variable.getAttribute("CredCcardL6M2Num").trim();
			if (this.isInt(attrString)) {
				AT3725 = attrString;
			}
			tempVariable = new VariableBean("AT3725", AT3725, "N", "EX",
					"贷记卡最近6个月M2的次数");
			attachVariableA.add(tempVariable);

			// 贷记卡最近12个月M2或M2+的次数
			String AT3730 = "-9";
			attrString = variable.getAttribute("CCardM2In12M").trim();
			if (this.isInt(attrString)) {
				AT3730 = attrString;
			}
			tempVariable = new VariableBean("AT3730", AT3730, "N", "EX",
					"贷记卡最近12个月M2或M2+的次数");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3730", AT3730);

			// 贷款当前总余额
			String AT3740 = "-9";
			attrString = variable.getAttribute("CurrLoanBalance").trim();
			if (this.isNumeric(attrString)) {
				AT3740 = attrString;
			}
			tempVariable = new VariableBean("AT3740", AT3740, "N", "EX",
					"贷款当前总余额");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3740", AT3740);

			// 贷款当前月还款总额
			String AT3750 = "-9";
			attrString = variable.getAttribute("CurrLoanMthlyPay").trim();
			if (this.isNumeric(attrString)) {
				AT3750 = attrString;
			}
			tempVariable = new VariableBean("AT3750", AT3750, "N", "EX",
					"贷款当前月还款总额");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3750", AT3750);

			// 个人公积金月缴存额 2009-05-31 zw
			String AT4090 = "-9";
			attrString = variable.getAttribute("ProvFundFirstLimit").trim();
			if (this.isNumeric(attrString)) {
				AT4090 = attrString;
			}
			tempVariable = new VariableBean("AT4090", AT4090, "N", "EX",
					"个人公积金月缴存额");
			attachVariableA.add(tempVariable);

			// 贷款合同金额
			String AT3745 = "-9";
			attrString = variable.getAttribute("CredLoanContractOlmt").trim();
			if (this.isNumeric(attrString)) {
				AT3745 = attrString;
			}
			tempVariable = new VariableBean("AT3745", AT3745, "N", "EX",
					"个人公积金月缴存额");
			attachVariableA.add(tempVariable);

			// 准贷记卡近6个月M3,M3+的次数
			String AT3760 = "-9";
			attrString = variable.getAttribute("CredQcardL6M3Num").trim();
			if (this.isInt(attrString)) {
				AT3760 = attrString;
			}
			tempVariable = new VariableBean("AT3760", AT3760, "N", "EX",
					"准贷记卡近6个月M3,M3+的次数");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3760", AT3760);

			// 准贷记卡近3个月M3,M3+的次数
			String AT3761 = "-9";
			attrString = variable.getAttribute("CredQcardL3M3Num").trim();
			if (this.isInt(attrString)) {
				AT3761 = attrString;
			}
			tempVariable = new VariableBean("AT3761", AT3761, "N", "EX",
					"准贷记卡近3个月M3,M3+的次数");
			attachVariableA.add(tempVariable);

			// log.appendParameter("AT3760", AT3760);

			// 贷款最近6个月M1的次数
			String AT3770 = "-9";
			attrString = variable.getAttribute("LoanM1In6M").trim();
			if (this.isInt(attrString)) {
				AT3770 = attrString;
			}
			tempVariable = new VariableBean("AT3770", AT3770, "N", "EX",
					"贷款最近6个月M1的次数");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3770", AT3770);
			// 贷款最近6个月M2,M2+的次数 2009-5-31 zw
			String AT4040 = "-9";
			attrString = variable.getAttribute("CredLoanL6M2Num").trim();
			if (this.isInt(attrString)) {
				AT4040 = attrString;
			}
			tempVariable = new VariableBean("AT4040", AT4040, "N", "EX",
					"贷款最近6个月M2,M2+的次数");
			attachVariableA.add(tempVariable);

			// 贷款最近12个月M2或M2+的次数
			String AT3780 = "-9";
			attrString = variable.getAttribute("LoanM2In12M").trim();
			if (this.isInt(attrString)) {
				AT3780 = attrString;
			}
			tempVariable = new VariableBean("AT3780", AT3780, "N", "EX",
					"贷款最近12个月M2或M2+的次数");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3780", AT3780);

			// 贷记卡近3个月M0的次数
			String AT3790 = "-9";
			attrString = variable.getAttribute("CCardM0In3M").trim();
			if (this.isInt(attrString)) {
				AT3790 = attrString;
			}
			tempVariable = new VariableBean("AT3790", AT3790, "N", "EX",
					"贷记卡近3个月M0的次数");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3790", AT3790);

			// 贷记卡近3个月M1的次数
			String AT3800 = "-9";
			attrString = variable.getAttribute("CCardM1In3M").trim();
			if (this.isInt(attrString)) {
				AT3800 = attrString;
			}
			tempVariable = new VariableBean("AT3800", AT3800, "N", "EX",
					"贷记卡近3个月M1的次数");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3800", AT3800);

			// 贷记卡近3个月M2和M2+的次数
			String AT3810 = "-9";
			attrString = variable.getAttribute("CCardM2In3M").trim();
			if (this.isInt(attrString)) {
				AT3810 = attrString;
			}
			tempVariable = new VariableBean("AT3810", AT3810, "N", "EX",
					"贷记卡近3个月M2+的次数");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3810", AT3810);

			// 贷款近3个月M0的次数
			String AT3820 = "-9";
			attrString = variable.getAttribute("LoanM0In3M").trim();
			if (this.isInt(attrString)) {
				AT3820 = attrString;
			}
			tempVariable = new VariableBean("AT3820", AT3820, "N", "EX",
					"贷款近3个月M0的次数");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3820", AT3820);

			// 贷款近3个月M1的次数
			String AT3830 = "-9";
			attrString = variable.getAttribute("LoanM1In3M").trim();
			if (this.isInt(attrString)) {
				AT3830 = attrString;
			}
			tempVariable = new VariableBean("AT3830", AT3830, "N", "EX",
					"贷款近3个月M1的次数");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3830", AT3830);

			// 贷款近3个月M2和M2+的次数
			String AT3840 = "-9";
			attrString = variable.getAttribute("LoanM2In3M").trim();
			if (this.isInt(attrString)) {
				AT3840 = attrString;
			}
			tempVariable = new VariableBean("AT3840", AT3840, "N", "EX",
					"贷款近3个月M2+的次数");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3840", AT3840);

			// 近6个月同时发生贷记卡和消费信贷逾期的次数
			String AT3850 = "-9";
			attrString = variable.getAttribute("CLM1In3M").trim();
			if (this.isInt(attrString)) {
				AT3850 = attrString;
			}
			tempVariable = new VariableBean("AT3850", AT3850, "N", "EX",
					"近6个月同时发生贷记卡和消费信贷逾期的次数");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3850", AT3850);

			// 2006-10-09根据中行接口改变而修改ashem
			// 黑名单检测结果
			String AT4130 = "-9";
			attrString = variable.getAttribute("BlkChkResult").trim();
			if (attrString != null && !"".equals(attrString)) {
				AT4130 = attrString;
			}
			tempVariable = new VariableBean("AT4130", AT4130, "N", "EX",
					"黑名单检测结果");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT4130", AT4130);

			// EOM检测结果
			String AT4140 = "-9";
			attrString = variable.getAttribute("EomChkResult").trim();
			if (attrString != null && !"".equals(attrString)) {
				AT4140 = attrString;
			}
			tempVariable = new VariableBean("AT4140", AT4140, "N", "EX",
					"EOM检测结果");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT4140", AT4140);

			// 重复申请件检测结果
			String AT4150 = "-9";
			attrString = variable.getAttribute("DupChkResult").trim();
			if (attrString != null && !"".equals(attrString)) {
				AT4150 = attrString;
			}
			tempVariable = new VariableBean("AT4150", AT4150, "N", "EX",
					"重复申请件检测结果");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT4150", AT4150);

			// 公司灰名单检测结果
			String AT4160 = "-9";
			attrString = variable.getAttribute("CgryChkResult").trim();
			if (attrString != null && !"".equals(attrString)) {
				AT4160 = attrString;
			}
			tempVariable = new VariableBean("AT4160", AT4160, "N", "EX",
					"公司灰名单检测结果");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT4160", AT4160);

			// 个人灰名单检测结果
			String AT4170 = "-9";
			attrString = variable.getAttribute("PgryChkResult").trim();
			if (attrString != null && !"".equals(attrString)) {
				AT4170 = attrString;
			}
			tempVariable = new VariableBean("AT4170", AT4170, "N", "EX",
					"个人灰名单检测结果");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT4170", AT4170);

			// 分行通知批准转拒绝检测结果
			String AT4180 = "-9";
			attrString = variable.getAttribute("A2rChkResult").trim();
			if (attrString != null && !"".equals(attrString)) {
				AT4180 = attrString;
			}
			tempVariable = new VariableBean("AT4180", AT4180, "N", "EX",
					"分行通知批准转拒绝检测结果");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT4180", AT4180);
			// //////////////////////////////////

			String A01001 = "";// 是否获取到征信报告：
			attrString = variable.getAttribute("A01001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A01001 = "2";
			} else {
				/**C1507封版后，统一改为判断1和Y*/
				if ("Y".equalsIgnoreCase(attrString) || "1".equalsIgnoreCase(attrString)) {
					A01001 = "1";
				} else {
					A01001 = "2";
				}
			}
			tempVariable = new VariableBean("A01001", A01001, "N", "EX",
					"是否获取到征信报告");
			attachVariableA.add(tempVariable);

			String A01002 = "";// 征信报告中是否具有征信信息：
			attrString = variable.getAttribute("A01002").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A01002 = "2";
			} else {
				/**C1507封版后，统一改为判断1和Y*/
				if ("Y".equalsIgnoreCase(attrString) || "1".equalsIgnoreCase(attrString)) {
					A01002 = "1";
				} else {
					A01002 = "2";
				}
			}
			tempVariable = new VariableBean("A01002", A01002, "N", "EX",
					"征信报告中是否具有征信信息");
			attachVariableA.add(tempVariable);

			String A02001 = "";// 是否我行信用卡客户：
			attrString = variable.getAttribute("A02001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A02001 = "2";
			} else {
				/**C1507封版后，统一改为判断1和Y*/
				if ("Y".equalsIgnoreCase(attrString) || "1".equalsIgnoreCase(attrString)) {
					A02001 = "1";
				} else {
					A02001 = "2";
				}
			}
			tempVariable = new VariableBean("A02001", A02001, "N", "EX",
					"是否我行信用卡客户");
			attachVariableA.add(tempVariable);

			String A02002 = "";// 我行客户信用额度：
			attrString = variable.getAttribute("A02002").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A02002 = "0";
			} else {
				A02002 = attrString;
			}
			tempVariable = new VariableBean("A02002", A02002, "N", "EX",
					"我行客户信用额度");
			attachVariableA.add(tempVariable);

			String A03001 = "";// 是否他行信用卡客户：
			attrString = variable.getAttribute("A03001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A03001 = "2";
			} else {
				/**C1507封版后，统一改为判断1和Y*/
				if ("Y".equalsIgnoreCase(attrString) || "1".equalsIgnoreCase(attrString)) {
					A03001 = "1";
				} else {
					A03001 = "2";
				}
			}
			tempVariable = new VariableBean("A03001", A03001, "N", "EX",
					"是否他行信用卡客户");
			attachVariableA.add(tempVariable);

			String A03002 = "";// 他行信用卡单卡最高额度(人民币)：
			attrString = variable.getAttribute("A03002").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A03002 = "0";
			} else {
				A03002 = attrString;
			}
			tempVariable = new VariableBean("A03002", A03002, "N", "EX",
					"他行信用卡单卡最高额度");
			attachVariableA.add(tempVariable);

			String A03003 = "";// 他行信用卡卡龄6个月以上的单卡最高额度(人民币)：
			attrString = variable.getAttribute("A03003").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A03003 = "0";
			} else {
				A03003 = attrString;
			}
			tempVariable = new VariableBean("A03003", A03003, "N", "EX",
					"他行信用卡卡龄6个月以上的单卡最高额度");
			attachVariableA.add(tempVariable);
			String A04002 = "";// 行内个人金融资产类客户等级：
			attrString = variable.getAttribute("A04002").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A04002 = "5";
			} else {
				if ("00".equals(attrString)) {
					A04002 = "5";
				} else if ("04".equals(attrString)) {
					A04002 = "1";//
				} else if ("05".equals(attrString)) {
					A04002 = "2";
				} else if ("06".equals(attrString)) {
					A04002 = "3";
				} else if ("08".equals(attrString)) {
					A04002 = "4";
				}
			}
			tempVariable = new VariableBean("A04002", A04002, "N", "EX",
					"行内个人金融资产类客户等级");
			attachVariableA.add(tempVariable);

			String A04003 = "";// 行内个人金融资产类客户近三个月月日均资产余额：
			attrString = variable.getAttribute("A04003").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A04003 = "0";
			} else {
				A04003 = attrString;
			}
			tempVariable = new VariableBean("A04003", A04003, "N", "EX",
					"行内个人金融资产类客户近三个月月日均资产余额");
			attachVariableA.add(tempVariable);
			String A04001 = "";// 是否行内个人金融资产类客户：
			attrString = variable.getAttribute("A04001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A04001 = "2";
			} else if("5".equalsIgnoreCase(A04002)){
				A04001 = "2";
			}else {
				/**C1507封版后，统一改为判断1和Y*/
				if ("Y".equalsIgnoreCase(attrString) || "1".equalsIgnoreCase(attrString)) {
					A04001 = "1";
				} else {
					A04001 = "2";
				}
			}
			tempVariable = new VariableBean("A04001", A04001, "N", "EX",
					"是否行内个人金融资产类客户");
			attachVariableA.add(tempVariable);
			String A05001 = "";// 养老保险金参保地
			attrString = variable.getAttribute("A05001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A05001 = "0";
			} else {
				A05001 = attrString;
			}
			tempVariable = new VariableBean("A05001", A05001, "N", "EX",
					"养老保险金参保地");
			attachVariableA.add(tempVariable);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String[] today, time, number;
			String Year = "";
			String Month = "";
			String Day = "";
			String first_time = "";
			String A05002 = "";// 养老保险金参保日期
			today = format.format(date).split("-");
			attrString = variable.getAttribute("A05002").trim();
			System.out.println("old_A05002-----------" + attrString);
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A05002 = "0";
			} else {
				number = Month_Number(attrString);
				Year = number[0];
				Month = number[1];
				Day = number[2];
				if (Year != null && Month != null
						&& !Year.equalsIgnoreCase("null") && !Year.equals("")
						&& !Month.equalsIgnoreCase("null") && !Month.equals("")) {
					A05002 = String
							.valueOf((Integer.parseInt(today[0]) - Integer
									.parseInt(Year))
									* 12
									+ (Integer.parseInt(today[1]) - Integer
											.parseInt(Month)));
				} else {
					A05002 = "0";
				}
			}
			tempVariable = new VariableBean("A05002", A05002, "N", "EX",
					"养老保险金参保日期");
			attachVariableA.add(tempVariable);

			String A05003 = "";// 养老保险金累计缴费月数
			attrString = variable.getAttribute("A05003").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A05003 = "0";
			} else {
				A05003 = attrString;
			}
			tempVariable = new VariableBean("A05003", A05003, "N", "EX",
					"养老保险金累计缴费月数");
			attachVariableA.add(tempVariable);

			String A05004 = "";// 养老保险金参加工作月份
			attrString = variable.getAttribute("A05004").trim();
			System.out.println("old_A05004-----------" + attrString);
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A05004 = "0";
			} else {
				number = Month_Number(attrString);
				Year = number[0];
				Month = number[1];
				Day = number[2];
				A05004 = Month;
			}
			tempVariable = new VariableBean("A05004", A05004, "N", "EX",
					"养老保险金参加工作月份");
			attachVariableA.add(tempVariable);

			String A05005 = "";// 养老保险金缴费状态
			attrString = variable.getAttribute("A05005").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A05005 = "0";
			} else {
				if (attrString.indexOf("|") > 0) {
					time = attrString.split("|");
					A05005 = time[0];
				} else {
					A05005 = attrString;
				}
			}
			tempVariable = new VariableBean("A05005", A05005, "N", "EX",
					"养老保险金缴费状态");
			attachVariableA.add(tempVariable);

			String A05006 = "";// 养老保险金个人缴费基数
			attrString = variable.getAttribute("A05006").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A05006 = "0";
			} else {
				A05006 = attrString;
			}
			tempVariable = new VariableBean("A05006", A05006, "N", "EX",
					"养老保险金个人缴费基数");
			attachVariableA.add(tempVariable);

			String A05007 = "";// 养老保险金本月缴费金额
			attrString = variable.getAttribute("A05007").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A05007 = "0";
			} else {
				A05007 = attrString;
			}
			tempVariable = new VariableBean("A05007", A05007, "N", "EX",
					"养老保险金本月缴费金额");
			attachVariableA.add(tempVariable);

			String A05008 = "";// 养老保险金信息更新日期
			attrString = variable.getAttribute("A05008").trim();
			System.out.println("old_A05008-----------" + attrString);
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A05008 = "0";
			} else {
				number = Month_Number(attrString);
				Year = number[0];
				Month = number[1];
				Day = number[2];
				if (Year != null && Month != null
						&& !Year.equalsIgnoreCase("null") && !Year.equals("")
						&& !Month.equalsIgnoreCase("null") && !Month.equals("")) {
					A05008 = String
							.valueOf((Integer.parseInt(today[0]) - Integer
									.parseInt(Year))
									* 12
									+ (Integer.parseInt(today[1]) - Integer
											.parseInt(Month)));
				} else {
					A05008 = "0";
				}
			}
			tempVariable = new VariableBean("A05008", A05008, "N", "EX",
					"养老保险金信息更新日期");
			attachVariableA.add(tempVariable);

			String A05009 = "";// 养老保险金缴费单位
			attrString = variable.getAttribute("A05009").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A05009 = "0";
			} else {
				A05009 = attrString;
			}
			tempVariable = new VariableBean("A05009", A05009, "N", "EX",
					"养老保险金缴费单位");
			attachVariableA.add(tempVariable);

			String A05010 = "";// 养老保险金中断或终止缴费原因
			attrString = variable.getAttribute("A05010").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A05010 = "0";
			} else {
				A05010 = attrString;
			}
			tempVariable = new VariableBean("A05010", A05010, "N", "EX",
					"养老保险金中断或终止缴费原因");
			attachVariableA.add(tempVariable);

			String A06001 = "";// 住房公积金参缴地：
			attrString = variable.getAttribute("A06001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A06001 = "0";
			} else {
				A06001 = attrString;
			}
			tempVariable = new VariableBean("A06001", A06001, "N", "EX",
					"住房公积金参缴地");
			attachVariableA.add(tempVariable);

			String A06002 = "";// 住房公积金初缴月份：
			attrString = variable.getAttribute("A06002").trim();
			System.out.println("old_A06002-----------" + attrString);
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A06002 = "0";
			} else {
				number = Month_Number(attrString);
				Year = number[0];
				Month = number[1];
				Day = number[2];
				A06002 = Month;
			}
			tempVariable = new VariableBean("A06002", A06002, "N", "EX",
					"住房公积金初缴月份");
			attachVariableA.add(tempVariable);

			String A06003 = "";// 住房公积金缴至月份：
			attrString = variable.getAttribute("A06003").trim();
			System.out.println("old_A06003-----------" + attrString);
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A06003 = "0";
			} else {
				number = Month_Number(attrString);
				Year = number[0];
				Month = number[1];
				Day = number[2];
				A06003 = Month;
			}
			tempVariable = new VariableBean("A06003", A06003, "N", "EX",
					"住房公积金缴至月份");
			attachVariableA.add(tempVariable);

			String A06004 = "";// 住房公积金缴费状态：
			attrString = variable.getAttribute("A06004").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A06004 = "0";
			} else {
				if (attrString.indexOf("|") > 0) {
					time = attrString.split("|");
					A06004 = time[0];
				} else {
					A06004 = attrString;
				}
			}
			tempVariable = new VariableBean("A06004", A06004, "N", "EX",
					"住房公积金缴费状态");
			attachVariableA.add(tempVariable);

			String A06005 = "";// 住房公积金月缴存额：
			attrString = variable.getAttribute("A06005").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A06005 = "0";
			} else {
				A06005 = attrString;
			}
			tempVariable = new VariableBean("A06005", A06005, "N", "EX",
					"住房公积金月缴存额");
			attachVariableA.add(tempVariable);

			String A06006 = "";// 住房公积金个人缴存比例：
			attrString = variable.getAttribute("A06006").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A06006 = "0";
			} else {
				if (attrString.indexOf("%") > 0) {
					A06006 = String.valueOf(Float.valueOf(attrString.substring(
							0, attrString.indexOf("%"))) / 100);
				} else {
					A06006 = attrString;
				}
			}
			tempVariable = new VariableBean("A06006", A06006, "N", "EX",
					"住房公积金个人缴存比例");
			attachVariableA.add(tempVariable);

			String A06007 = "";// 住房公积金单位缴存比例：
			attrString = variable.getAttribute("A06007").trim();
			if (null == attrString || null == attrString
					|| "-999".equals(attrString) || "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A06007 = "0";
			} else {
				if (attrString.indexOf("%") > 0) {
					A06007 = String.valueOf(Float.valueOf(attrString.substring(
							0, attrString.indexOf("%"))) / 100);
				} else {
					A06007 = attrString;
				}
			}
			tempVariable = new VariableBean("A06007", A06007, "N", "EX",
					"住房公积金单位缴存比例");
			attachVariableA.add(tempVariable);

			String A06008 = "";// 住房公积金缴费单位
			attrString = variable.getAttribute("A06008").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A06008 = "0";
			} else {
				A06008 = attrString;
			}
			tempVariable = new VariableBean("A06008", A06008, "N", "EX",
					"住房公积金缴费单位");
			attachVariableA.add(tempVariable);

			String A06009 = "";// 住房公积金信息更新日期
			attrString = variable.getAttribute("A06009").trim();
			System.out.println("old_A06009-----------" + attrString);
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A06009 = "0";
			} else {
				number = Month_Number(attrString);
				Year = number[0];
				Month = number[1];
				Day = number[2];
				if (!Year.equalsIgnoreCase("null") && Year != null
						&& !Year.equals("") && !Month.equalsIgnoreCase("null")
						&& Month != null && !Month.equals("")) {
					A06009 = String
							.valueOf((Integer.parseInt(today[0]) - Integer
									.parseInt(Year))
									* 12
									+ (Integer.parseInt(today[1]) - Integer
											.parseInt(Month)));
				} else {
					A06009 = "0";
				}
			}
			tempVariable = new VariableBean("A06009", A06009, "N", "EX",
					"住房公积金信息更新日期");
			attachVariableA.add(tempVariable);

			String A07001 = "";// 近3个月同时发生信用卡和贷款逾期的次数：
			attrString = variable.getAttribute("A07001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A07001 = "0";
			} else {
				A07001 = attrString;
			}
			tempVariable = new VariableBean("A07001", A07001, "N", "EX",
					"近3个月同时发生信用卡和贷款逾期的次数");
			attachVariableA.add(tempVariable);

			String A07002 = "";// 近6个月同时发生信用卡和贷款逾期的次数：
			attrString = variable.getAttribute("A07002").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A07002 = "0";
			} else {
				A07002 = attrString;
			}
			tempVariable = new VariableBean("A07002", A07002, "N", "EX",
					"近6个月同时发生信用卡和贷款逾期的次数");
			attachVariableA.add(tempVariable);

			String A07003 = "";// 近12个月同时发生信用卡和贷款逾期的次数：
			attrString = variable.getAttribute("A07003").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A07003 = "0";
			} else {
				A07003 = attrString;
			}
			tempVariable = new VariableBean("A07003", A07003, "N", "EX",
					"近12个月同时发生信用卡和贷款逾期的次数");
			attachVariableA.add(tempVariable);

			String A07004 = "";// 近24个月同时发生信用卡和贷款逾期的次数：
			attrString = variable.getAttribute("A07004").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A07004 = "0";
			} else {
				A07004 = attrString;
			}
			tempVariable = new VariableBean("A07004", A07004, "N", "EX",
					"近24个月同时发生信用卡和贷款逾期的次数");
			attachVariableA.add(tempVariable);

			String A08001 = "";// 贷记卡发卡机构数：
			attrString = variable.getAttribute("A08001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08001 = "0";
			} else {
				A08001 = attrString;
			}
			tempVariable = new VariableBean("A08001", A08001, "N", "EX",
					"贷记卡发卡机构数");
			attachVariableA.add(tempVariable);

			String A08002 = "";// 贷记卡账户数：
			attrString = variable.getAttribute("A08002").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08002 = "0";
			} else {
				A08002 = attrString;
			}
			tempVariable = new VariableBean("A08002", A08002, "N", "EX",
					"贷记卡账户数");
			attachVariableA.add(tempVariable);

			String A08003 = "";// 贷记卡非正常账户数：
			attrString = variable.getAttribute("A08003").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08003 = "0";
			} else {
				A08003 = attrString;
			}
			tempVariable = new VariableBean("A08003", A08003, "N", "EX",
					"贷记卡非正常账户数");
			attachVariableA.add(tempVariable);

			String A08004 = "";// 贷记卡最早开户日期：
			attrString = variable.getAttribute("A08004").trim();
			// System.out.println("old_A08004-----------"+attrString);
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08004 = "0";
			} else {
				number = Month_Number(attrString);
				Year = number[0];
				Month = number[1];
				Day = number[2];
				if (Year != null && Month != null
						&& !Year.equalsIgnoreCase("null") && !Year.equals("")
						&& !Month.equalsIgnoreCase("null") && !Month.equals("")) {
					A08004 = String
							.valueOf((Integer.parseInt(today[0]) - Integer
									.parseInt(Year))
									* 12
									+ (Integer.parseInt(today[1]) - Integer
											.parseInt(Month)));
				} else {
					A08004 = "0";
				}
			}
			tempVariable = new VariableBean("A08004", A08004, "N", "EX",
					"贷记卡最早开户日期");
			attachVariableA.add(tempVariable);

			String A08005 = "";// 贷记卡授信总额：
			attrString = variable.getAttribute("A08005").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08005 = "-999";
			} else {
				A08005 = attrString;
			}
			tempVariable = new VariableBean("A08005", A08005, "N", "EX",
					"贷记卡授信总额");
			attachVariableA.add(tempVariable);

			String A08006 = "";// 贷记卡总已用额度：
			attrString = variable.getAttribute("A08006").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08006 = "-999";
			} else {
				A08006 = attrString;
			}
			tempVariable = new VariableBean("A08006", A08006, "N", "EX",
					"贷记卡总已用额度");
			attachVariableA.add(tempVariable);

			String A08007 = "";// 贷记卡最近6个月平均使用额度：
			attrString = variable.getAttribute("A08007").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08007 = "0";
			} else {
				A08007 = attrString;
			}
			tempVariable = new VariableBean("A08007", A08007, "N", "EX",
					"贷记卡最近6个月平均使用额度");
			attachVariableA.add(tempVariable);

			String A08008 = "";// 贷记卡单家行最高额度(人民币)：
			attrString = variable.getAttribute("A08008").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08008 = "0";
			} else {
				A08008 = attrString;
			}
			tempVariable = new VariableBean("A08008", A08008, "N", "EX",
					"贷记卡单家行最高额度");
			attachVariableA.add(tempVariable);

			String A08009 = "";// 贷记卡单家行最低授信额(人民币)：
			attrString = variable.getAttribute("A08009").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08009 = "0";
			} else {
				A08009 = attrString;
			}
			tempVariable = new VariableBean("A08009", A08009, "N", "EX",
					"贷记卡单家行最低授信额");
			attachVariableA.add(tempVariable);

			String A08010 = "";// 贷记卡卡龄6个月以上最高额度(人民币)：
			attrString = variable.getAttribute("A08010").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08010 = "0";
			} else {
				A08010 = attrString;
			}
			tempVariable = new VariableBean("A08010", A08010, "N", "EX",
					"贷记卡卡龄6个月以上最高额度");
			attachVariableA.add(tempVariable);

			String A08011 = "";// 贷记卡他行最高额度(人民币)：
			attrString = variable.getAttribute("A08011").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08011 = "0";
			} else {
				A08011 = attrString;
			}
			tempVariable = new VariableBean("A08011", A08011, "N", "EX",
					"贷记卡他行最高额度");
			attachVariableA.add(tempVariable);

			String A08012 = "";// 贷记卡他行10万及以下第一高额度(人民币)：
			attrString = variable.getAttribute("A08012").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08012 = "0";
			} else {
				A08012 = attrString;
			}
			tempVariable = new VariableBean("A08012", A08012, "N", "EX",
					"贷记卡他行10万及以下第一高额度");
			attachVariableA.add(tempVariable);

			String A08013 = "";// 贷记卡他行10万及以下第二高额度(人民币)：
			attrString = variable.getAttribute("A08013").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08013 = "0";
			} else {
				A08013 = attrString;
			}
			tempVariable = new VariableBean("A08013", A08013, "N", "EX",
					"贷记卡他行10万及以下第二高额度");
			attachVariableA.add(tempVariable);

			String A08014 = "";// 贷记卡当前逾期总额：
			attrString = variable.getAttribute("A08014").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08014 = "0";
			} else {
				A08014 = attrString;
			}
			tempVariable = new VariableBean("A08014", A08014, "N", "EX",
					"贷记卡当前逾期总额");
			attachVariableA.add(tempVariable);

			String A09001 = "";// 贷记卡近3个月M1的次数：
			attrString = variable.getAttribute("A09001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09001 = "-999";
			} else {
				A09001 = attrString;
			}
			tempVariable = new VariableBean("A09001", A09001, "N", "EX",
					"贷记卡近3个月M1的次数");
			attachVariableA.add(tempVariable);

			String A09002 = "";// 贷记卡近3个月M2的次数：
			attrString = variable.getAttribute("A09002").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09002 = "0";
			} else {
				A09002 = attrString;
			}
			tempVariable = new VariableBean("A09002", A09002, "N", "EX",
					"贷记卡近3个月M2的次数");
			attachVariableA.add(tempVariable);

			String A09003 = "";// 贷记卡近3个月M3的次数：
			attrString = variable.getAttribute("A09003").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09003 = "0";
			} else {
				A09003 = attrString;
			}
			tempVariable = new VariableBean("A09003", A09003, "N", "EX",
					"贷记卡近3个月M3的次数");
			attachVariableA.add(tempVariable);

			String A09004 = "";// 贷记卡近3个月M4的次数：
			attrString = variable.getAttribute("A09004").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09004 = "0";
			} else {
				A09004 = attrString;
			}
			tempVariable = new VariableBean("A09004", A09004, "N", "EX",
					"贷记卡近3个月M4的次数");
			attachVariableA.add(tempVariable);

			String A09005 = "";// 贷记卡近3个月M5的次数：
			attrString = variable.getAttribute("A09005").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09005 = "0";
			} else {
				A09005 = attrString;
			}
			tempVariable = new VariableBean("A09005", A09005, "N", "EX",
					"贷记卡近3个月M5的次数");
			attachVariableA.add(tempVariable);

			String A09006 = "";// 贷记卡近3个月M6的次数：
			attrString = variable.getAttribute("A09006").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09006 = "0";
			} else {
				A09006 = attrString;
			}
			tempVariable = new VariableBean("A09006", A09006, "N", "EX",
					"贷记卡近3个月M6的次数");
			attachVariableA.add(tempVariable);

			String A09007 = "";// 贷记卡近3个月M7的次数：
			attrString = variable.getAttribute("A09007").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09007 = "0";
			} else {
				A09007 = attrString;
			}
			tempVariable = new VariableBean("A09007", A09007, "N", "EX",
					"贷记卡近3个月M7的次数");
			attachVariableA.add(tempVariable);

			String A09008 = "";// 贷记卡近6个月M1的次数：
			attrString = variable.getAttribute("A09008").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09008 = "-999";
			} else {
				A09008 = attrString;
			}
			tempVariable = new VariableBean("A09008", A09008, "N", "EX",
					"贷记卡近6个月M1的次数");
			attachVariableA.add(tempVariable);

			String A09009 = "";// 贷记卡近6个月M2的次数：
			attrString = variable.getAttribute("A09009").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09009 = "0";
			} else {
				A09009 = attrString;
			}
			tempVariable = new VariableBean("A09009", A09009, "N", "EX",
					"贷记卡近6个月M2的次数");
			attachVariableA.add(tempVariable);

			String A09010 = "";// 贷记卡近6个月M3的次数：
			attrString = variable.getAttribute("A09010").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09010 = "0";
			} else {
				A09010 = attrString;
			}
			tempVariable = new VariableBean("A09010", A09010, "N", "EX",
					"贷记卡近6个月M3的次数");
			attachVariableA.add(tempVariable);

			String A09011 = "";// 贷记卡近6个月M4的次数：
			attrString = variable.getAttribute("A09011").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09011 = "0";
			} else {
				A09011 = attrString;
			}
			tempVariable = new VariableBean("A09011", A09011, "N", "EX",
					"贷记卡近6个月M4的次数");
			attachVariableA.add(tempVariable);

			String A09012 = "";// 贷记卡近6个月M5的次数：
			attrString = variable.getAttribute("A09012").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09012 = "0";
			} else {
				A09012 = attrString;
			}
			tempVariable = new VariableBean("A09012", A09012, "N", "EX",
					"贷记卡近6个月M5的次数");
			attachVariableA.add(tempVariable);

			String A09013 = "";// 贷记卡近6个月M6的次数：
			attrString = variable.getAttribute("A09013").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09013 = "0";
			} else {
				A09013 = attrString;
			}
			tempVariable = new VariableBean("A09013", A09013, "N", "EX",
					"贷记卡近6个月M6的次数");
			attachVariableA.add(tempVariable);

			String A09014 = "";// 贷记卡近6个月M7的次数：
			attrString = variable.getAttribute("A09014").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09014 = "0";
			} else {
				A09014 = attrString;
			}
			tempVariable = new VariableBean("A09014", A09014, "N", "EX",
					"贷记卡近6个月M7的次数");
			attachVariableA.add(tempVariable);

			String A09015 = "";// 贷记卡近12个月M1的次数：
			attrString = variable.getAttribute("A09015").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09015 = "0";
			} else {
				A09015 = attrString;
			}
			tempVariable = new VariableBean("A09015", A09015, "N", "EX",
					"贷记卡近12个月M1的次数");
			attachVariableA.add(tempVariable);

			String A09016 = "";// 贷记卡近12个月M2的次数：
			attrString = variable.getAttribute("A09016").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09016 = "0";
			} else {
				A09016 = attrString;
			}
			tempVariable = new VariableBean("A09016", A09016, "N", "EX",
					"贷记卡近12个月M2的次数");
			attachVariableA.add(tempVariable);

			String A09017 = "";// 贷记卡近12个月M3的次数：
			attrString = variable.getAttribute("A09017").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09017 = "0";
			} else {
				A09017 = attrString;
			}
			tempVariable = new VariableBean("A09017", A09017, "N", "EX",
					"贷记卡近12个月M3的次数");
			attachVariableA.add(tempVariable);

			String A09018 = "";// 贷记卡近12个月M4的次数：
			attrString = variable.getAttribute("A09018").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09018 = "0";
			} else {
				A09018 = attrString;
			}
			tempVariable = new VariableBean("A09018", A09018, "N", "EX",
					"贷记卡近12个月M4的次数");
			attachVariableA.add(tempVariable);

			String A09019 = "";// 贷记卡近12个月M5的次数：
			attrString = variable.getAttribute("A09019").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09019 = "0";
			} else {
				A09019 = attrString;
			}
			tempVariable = new VariableBean("A09019", A09019, "N", "EX",
					"贷记卡近12个月M5的次数");
			attachVariableA.add(tempVariable);

			String A09020 = "";// 贷记卡近12个月M6的次数：
			attrString = variable.getAttribute("A09020").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09020 = "0";
			} else {
				A09020 = attrString;
			}
			tempVariable = new VariableBean("A09020", A09020, "N", "EX",
					"贷记卡近12个月M6的次数");
			attachVariableA.add(tempVariable);

			String A09021 = "";// 贷记卡近12个月M7的次数：
			attrString = variable.getAttribute("A09021").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09021 = "0";
			} else {
				A09021 = attrString;
			}
			tempVariable = new VariableBean("A09021", A09021, "N", "EX",
					"贷记卡近12个月M7的次数");
			attachVariableA.add(tempVariable);

			String A09022 = "";// 贷记卡近24个月M1的次数：
			attrString = variable.getAttribute("A09022").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09022 = "0";
			} else {
				A09022 = attrString;
			}
			tempVariable = new VariableBean("A09022", A09022, "N", "EX",
					"贷记卡近24个月M1的次数");
			attachVariableA.add(tempVariable);

			String A09023 = "";// 贷记卡近24个月M2的次数：
			attrString = variable.getAttribute("A09023").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09023 = "0";
			} else {
				A09023 = attrString;
			}
			tempVariable = new VariableBean("A09023", A09023, "N", "EX",
					"贷记卡近24个月M2的次数");
			attachVariableA.add(tempVariable);

			String A09024 = "";// 贷记卡近24个月M3的次数：
			attrString = variable.getAttribute("A09024").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09024 = "0";
			} else {
				A09024 = attrString;
			}
			tempVariable = new VariableBean("A09024", A09024, "N", "EX",
					"贷记卡近24个月M3的次数");
			attachVariableA.add(tempVariable);

			String A09025 = "";// 贷记卡近24个月M4的次数：
			attrString = variable.getAttribute("A09025").trim();

			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09025 = "0";
			} else {
				A09025 = attrString;
			}
			tempVariable = new VariableBean("A09025", A09025, "N", "EX",
					"贷记卡近24个月M4的次数");
			attachVariableA.add(tempVariable);

			String A09026 = "";// 贷记卡近24个月M5的次数：
			attrString = variable.getAttribute("A09026").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09026 = "0";
			} else {
				A09026 = attrString;
			}
			tempVariable = new VariableBean("A09026", A09026, "N", "EX",
					"贷记卡近24个月M5的次数");
			attachVariableA.add(tempVariable);

			String A09027 = "";// 贷记卡近24个月M6的次数：
			attrString = variable.getAttribute("A09027").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09027 = "0";
			} else {
				A09027 = attrString;
			}
			tempVariable = new VariableBean("A09027", A09027, "N", "EX",
					"贷记卡近24个月M6的次数");
			attachVariableA.add(tempVariable);

			String A09028 = "";// 贷记卡近24个月M7的次数：
			attrString = variable.getAttribute("A09028").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09028 = "0";
			} else {
				A09028 = attrString;
			}
			tempVariable = new VariableBean("A09028", A09028, "N", "EX",
					"贷记卡近24个月M7的次数");
			attachVariableA.add(tempVariable);

			String A09029 = "";// 贷记卡近5年前36个月逾期最高持续月数：
			attrString = variable.getAttribute("A09029").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09029 = "0";
			} else {
				A09029 = attrString;
			}
			tempVariable = new VariableBean("A09029", A09029, "N", "EX",
					"贷记卡近5年前36个月逾期最高持续月数");
			attachVariableA.add(tempVariable);

			String A09030 = "";// 贷记卡当月最高逾期级别：
			attrString = variable.getAttribute("A09030").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09030 = "0";
			} else {
				if (attrString.indexOf("|") > 0) {
					time = attrString.split("|");
					A09030 = time[0];
				} else {
					A09030 = attrString;
				}
			}
			tempVariable = new VariableBean("A09030", A09030, "N", "EX",
					"贷记卡当月最高逾期级别");
			attachVariableA.add(tempVariable);

			String A10001 = "";// 准贷记卡发卡机构数：
			attrString = variable.getAttribute("A10001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10001 = "0";
			} else {
				A10001 = attrString;
			}
			tempVariable = new VariableBean("A10001", A10001, "N", "EX",
					"准贷记卡发卡机构数");
			attachVariableA.add(tempVariable);

			String A10002 = "";// 准贷记卡账户数：
			attrString = variable.getAttribute("A10002").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10002 = "0";
			} else {
				A10002 = attrString;
			}
			tempVariable = new VariableBean("A10002", A10002, "N", "EX",
					"准贷记卡账户数");
			attachVariableA.add(tempVariable);

			String A10003 = "";// 准贷记卡非正常账户数：
			attrString = variable.getAttribute("A10003").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10003 = "0";
			} else {
				A10003 = attrString;
			}
			tempVariable = new VariableBean("A10003", A10003, "N", "EX",
					"准贷记卡非正常账户数");
			attachVariableA.add(tempVariable);

			String A10004 = "";// 准贷记卡最早开户日期：
			attrString = variable.getAttribute("A10004").trim();
			// System.out.println("old_A10004-----------"+attrString);
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10004 = "0";
			} else {
				number = Month_Number(attrString);
				Year = number[0];
				Month = number[1];
				Day = number[2];
				if (Year != null && Month != null
						&& !Year.equalsIgnoreCase("null") && !Year.equals("")
						&& !Month.equalsIgnoreCase("null") && !Month.equals("")) {
					A10004 = String
							.valueOf((Integer.parseInt(today[0]) - Integer
									.parseInt(Year))
									* 12
									+ (Integer.parseInt(today[1]) - Integer
											.parseInt(Month)));
				} else {
					A10004 = "0";
				}
			}
			tempVariable = new VariableBean("A10004", A10004, "N", "EX",
					"准贷记卡最早开户日期");
			attachVariableA.add(tempVariable);

			String A10005 = "";// 准贷记卡授信总额：
			attrString = variable.getAttribute("A10005").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10005 = "0";
			} else {
				A10005 = attrString;
			}
			tempVariable = new VariableBean("A10005", A10005, "N", "EX",
					"准贷记卡授信总额");
			attachVariableA.add(tempVariable);

			String A10006 = "";// 准贷记卡总透支余额：
			attrString = variable.getAttribute("A10006").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10006 = "0";
			} else {
				A10006 = attrString;
			}
			tempVariable = new VariableBean("A10006", A10006, "N", "EX",
					"准贷记卡总透支余额");
			attachVariableA.add(tempVariable);

			String A10007 = "";// 准贷记卡最近6个月平均透支余额：
			attrString = variable.getAttribute("A10007").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10007 = "0";
			} else {
				A10007 = attrString;
			}
			tempVariable = new VariableBean("A10007", A10007, "N", "EX",
					"准贷记卡最近6个月平均透支余额");
			attachVariableA.add(tempVariable);

			String A10008 = "";// 准贷记卡单家行最高额度(人民币)：
			attrString = variable.getAttribute("A10008").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10008 = "0";
			} else {
				A10008 = attrString;
			}
			tempVariable = new VariableBean("A10008", A10008, "N", "EX",
					"准贷记卡单家行最高额度");
			attachVariableA.add(tempVariable);

			String A10009 = "";// 准贷记卡单家行最低授信额(人民币)：
			attrString = variable.getAttribute("A10009").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10009 = "0";
			} else {
				A10009 = attrString;
			}
			tempVariable = new VariableBean("A10009", A10009, "N", "EX",
					"准贷记卡单家行最低授信额");
			attachVariableA.add(tempVariable);

			String A10010 = "";// 准贷记卡卡龄6个月以上最高额度(人民币)：
			attrString = variable.getAttribute("A10010").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10010 = "0";
			} else {
				A10010 = attrString;
			}
			tempVariable = new VariableBean("A10010", A10010, "N", "EX",
					"准贷记卡卡龄6个月以上最高额度");
			attachVariableA.add(tempVariable);

			String A10011 = "";// 贷记卡他行最高额度(人民币)：
			attrString = variable.getAttribute("A10011").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10011 = "0";
			} else {
				A10011 = attrString;
			}
			tempVariable = new VariableBean("A10011", A10011, "N", "EX",
					"贷记卡他行最高额度");
			attachVariableA.add(tempVariable);

			String A10012 = "";// 贷记卡他行10万及以下第一高额度(人民币)：
			attrString = variable.getAttribute("A10012").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10012 = "0";
			} else {
				A10012 = attrString;
			}
			tempVariable = new VariableBean("A10012", A10012, "N", "EX",
					"贷记卡他行10万及以下第一高额度");
			attachVariableA.add(tempVariable);

			String A10013 = "";// 贷记卡他行10万及以下第二高额度(人民币)：
			attrString = variable.getAttribute("A10013").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10013 = "0";
			} else {
				A10013 = attrString;
			}
			tempVariable = new VariableBean("A10013", A10013, "N", "EX",
					"贷记卡他行10万及以下第二高额度");
			attachVariableA.add(tempVariable);

			String A10014 = "";// 准贷记卡当前逾期总额：
			attrString = variable.getAttribute("A10014").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10014 = "0";
			} else {
				A10014 = attrString;
			}
			tempVariable = new VariableBean("A10014", A10014, "N", "EX",
					"准贷记卡当前逾期总额");
			attachVariableA.add(tempVariable);

			String A11001 = "";// 准贷记卡近3个月M1的次数：
			attrString = variable.getAttribute("A11001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11001 = "-999";
			} else {
				A11001 = attrString;
			}
			tempVariable = new VariableBean("A11001", A11001, "N", "EX",
					"准贷记卡近3个月M1的次数");
			attachVariableA.add(tempVariable);

			String A11002 = "";// 准贷记卡近3个月M2的次数：
			attrString = variable.getAttribute("A11002").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11002 = "0";
			} else {
				A11002 = attrString;
			}
			tempVariable = new VariableBean("A11002", A11002, "N", "EX",
					"准贷记卡近3个月M2的次数");
			attachVariableA.add(tempVariable);

			String A11003 = "";// 准贷记卡近3个月M3的次数：
			attrString = variable.getAttribute("A11003").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11003 = "0";
			} else {
				A11003 = attrString;
			}
			tempVariable = new VariableBean("A11003", A11003, "N", "EX",
					"准贷记卡近3个月M3的次数");
			attachVariableA.add(tempVariable);

			String A11004 = "";// 准贷记卡近3个月M4的次数：
			attrString = variable.getAttribute("A11004").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11004 = "0";
			} else {
				A11004 = attrString;
			}
			tempVariable = new VariableBean("A11004", A11004, "N", "EX",
					"准贷记卡近3个月M4的次数");
			attachVariableA.add(tempVariable);

			String A11005 = "";// 准贷记卡近3个月M5的次数：
			attrString = variable.getAttribute("A11005").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11005 = "0";
			} else {
				A11005 = attrString;
			}
			tempVariable = new VariableBean("A11005", A11005, "N", "EX",
					"准贷记卡近3个月M5的次数");
			attachVariableA.add(tempVariable);

			String A11006 = "";// 准贷记卡近3个月M6的次数：
			attrString = variable.getAttribute("A11006").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11006 = "0";
			} else {
				A11006 = attrString;
			}
			tempVariable = new VariableBean("A11006", A11006, "N", "EX",
					"准贷记卡近3个月M6的次数");
			attachVariableA.add(tempVariable);

			String A11007 = "";// 准贷记卡近3个月M7的次数：
			attrString = variable.getAttribute("A11007").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11007 = "0";
			} else {
				A11007 = attrString;
			}
			tempVariable = new VariableBean("A11007", A11007, "N", "EX",
					"准贷记卡近3个月M7的次数");
			attachVariableA.add(tempVariable);

			String A11008 = "";// 准贷记卡近6个月M1的次数：
			attrString = variable.getAttribute("A11008").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11008 = "0";
			} else {
				A11008 = attrString;
			}
			tempVariable = new VariableBean("A11008", A11008, "N", "EX",
					"准贷记卡近6个月M1的次数");
			attachVariableA.add(tempVariable);

			String A11009 = "";// 准贷记卡近6个月M2的次数：
			attrString = variable.getAttribute("A11009").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11009 = "0";
			} else {
				A11009 = attrString;
			}
			tempVariable = new VariableBean("A11009", A11009, "N", "EX",
					"准贷记卡近6个月M2的次数");
			attachVariableA.add(tempVariable);

			String A11010 = "";// 准贷记卡近6个月M3的次数：
			attrString = variable.getAttribute("A11010").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11010 = "0";
			} else {
				A11010 = attrString;
			}
			tempVariable = new VariableBean("A11010", A11010, "N", "EX",
					"准贷记卡近6个月M3的次数");
			attachVariableA.add(tempVariable);

			String A11011 = "";// 准贷记卡近6个月M4的次数：
			attrString = variable.getAttribute("A11011").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11011 = "0";
			} else {
				A11011 = attrString;
			}
			tempVariable = new VariableBean("A11011", A11011, "N", "EX",
					"准贷记卡近6个月M4的次数");
			attachVariableA.add(tempVariable);

			String A11012 = "";// 准贷记卡近6个月M5的次数：
			attrString = variable.getAttribute("A11012").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11012 = "0";
			} else {
				A11012 = attrString;
			}
			tempVariable = new VariableBean("A11012", A11012, "N", "EX",
					"准贷记卡近6个月M5的次数");
			attachVariableA.add(tempVariable);

			String A11013 = "";// 准贷记卡近6个月M6的次数：
			attrString = variable.getAttribute("A11013").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11013 = "0";
			} else {
				A11013 = attrString;
			}
			tempVariable = new VariableBean("A11013", A11013, "N", "EX",
					"准贷记卡近6个月M6的次数");
			attachVariableA.add(tempVariable);

			String A11014 = "";// 准贷记卡近6个月M7的次数：
			attrString = variable.getAttribute("A11014").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11014 = "0";
			} else {
				A11014 = attrString;
			}
			tempVariable = new VariableBean("A11014", A11014, "N", "EX",
					"准贷记卡近6个月M7的次数");
			attachVariableA.add(tempVariable);

			String A11015 = "";// 准贷记卡近12个月M1的次数：
			attrString = variable.getAttribute("A11015").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11015 = "0";
			} else {
				A11015 = attrString;
			}
			tempVariable = new VariableBean("A11015", A11015, "N", "EX",
					"准贷记卡近12个月M1的次数");
			attachVariableA.add(tempVariable);

			String A11016 = "";// 准贷记卡近12个月M2的次数：
			attrString = variable.getAttribute("A11016").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11016 = "0";
			} else {
				A11016 = attrString;
			}
			tempVariable = new VariableBean("A11016", A11016, "N", "EX",
					"准贷记卡近12个月M2的次数");
			attachVariableA.add(tempVariable);

			String A11017 = "";// 准贷记卡近12个月M3的次数：
			attrString = variable.getAttribute("A11017").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11017 = "0";
			} else {
				A11017 = attrString;
			}
			tempVariable = new VariableBean("A11017", A11017, "N", "EX",
					"准贷记卡近12个月M3的次数");
			attachVariableA.add(tempVariable);

			String A11018 = "";// 准贷记卡近12个月M4的次数：
			attrString = variable.getAttribute("A11018").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11018 = "0";
			} else {
				A11018 = attrString;
			}
			tempVariable = new VariableBean("A11018", A11018, "N", "EX",
					"准贷记卡近12个月M4的次数");
			attachVariableA.add(tempVariable);

			String A11019 = "";// 准贷记卡近12个月M5的次数：
			attrString = variable.getAttribute("A11019").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11019 = "0";
			} else {
				A11019 = attrString;
			}
			tempVariable = new VariableBean("A11019", A11019, "N", "EX",
					"准贷记卡近12个月M5的次数");
			attachVariableA.add(tempVariable);

			String A11020 = "";// 准贷记卡近12个月M6的次数：
			attrString = variable.getAttribute("A11020").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11020 = "0";
			} else {
				A11020 = attrString;
			}
			tempVariable = new VariableBean("A11020", A11020, "N", "EX",
					"准贷记卡近12个月M6的次数");
			attachVariableA.add(tempVariable);

			String A11021 = "";// 准贷记卡近12个月M7的次数：
			attrString = variable.getAttribute("A11021").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11021 = "0";
			} else {
				A11021 = attrString;
			}
			tempVariable = new VariableBean("A11021", A11021, "N", "EX",
					"准贷记卡近12个月M7的次数");
			attachVariableA.add(tempVariable);

			String A11022 = "";// 准贷记卡近24个月M1的次数：
			attrString = variable.getAttribute("A11022").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11022 = "0";
			} else {
				A11022 = attrString;
			}
			tempVariable = new VariableBean("A11022", A11022, "N", "EX",
					"准贷记卡近24个月M1的次数");
			attachVariableA.add(tempVariable);

			String A11023 = "";// 准贷记卡近24个月M2的次数：
			attrString = variable.getAttribute("A11023").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11023 = "0";
			} else {
				A11023 = attrString;
			}
			tempVariable = new VariableBean("A11023", A11023, "N", "EX",
					"准贷记卡近24个月M2的次数");
			attachVariableA.add(tempVariable);

			String A11024 = "";// 准贷记卡近24个月M3的次数：
			attrString = variable.getAttribute("A11024").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11024 = "0";
			} else {
				A11024 = attrString;
			}
			tempVariable = new VariableBean("A11024", A11024, "N", "EX",
					"准贷记卡近24个月M3的次数");
			attachVariableA.add(tempVariable);

			String A11025 = "";// 准贷记卡近24个月M4的次数：
			attrString = variable.getAttribute("A11025").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11025 = "0";
			} else {
				A11025 = attrString;
			}
			tempVariable = new VariableBean("A11025", A11025, "N", "EX",
					"准贷记卡近24个月M4的次数");
			attachVariableA.add(tempVariable);

			String A11026 = "";// 准贷记卡近24个月M5的次数：
			attrString = variable.getAttribute("A11026").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11026 = "0";
			} else {
				A11026 = attrString;
			}
			tempVariable = new VariableBean("A11026", A11026, "N", "EX",
					"准贷记卡近24个月M5的次数");
			attachVariableA.add(tempVariable);

			String A11027 = "";// 准贷记卡近24个月M6的次数：
			attrString = variable.getAttribute("A11027").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11027 = "0";
			} else {
				A11027 = attrString;
			}
			tempVariable = new VariableBean("A11027", A11027, "N", "EX",
					"准贷记卡近24个月M6的次数");
			attachVariableA.add(tempVariable);

			String A11028 = "";// 准贷记卡近24个月M7的次数：
			attrString = variable.getAttribute("A11028").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11028 = "0";
			} else {
				A11028 = attrString;
			}
			tempVariable = new VariableBean("A11028", A11028, "N", "EX",
					"准贷记卡近24个月M7的次数");
			attachVariableA.add(tempVariable);

			String A11029 = "";// 准贷记卡近5年前36个月逾期最高持续月数：
			attrString = variable.getAttribute("A11029").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11029 = "0";
			} else {
				A11029 = attrString;
			}
			tempVariable = new VariableBean("A11029", A11029, "N", "EX",
					"准贷记卡近5年前36个月逾期最高持续月数");
			attachVariableA.add(tempVariable);

			String A11030 = "";// 准贷记卡当月最高逾期级别：
			attrString = variable.getAttribute("A11030").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11030 = "0";
			} else {
				if (attrString.indexOf("|") > 0) {
					time = attrString.split("|");
					A11030 = time[0];
				} else {
					A11030 = attrString;
				}
			}
			tempVariable = new VariableBean("A11030", A11030, "N", "EX",
					"准贷记卡当月最高逾期级别");
			attachVariableA.add(tempVariable);

			String A12001 = "";// 贷款机构数：
			attrString = variable.getAttribute("A12001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A12001 = "0";
			} else {
				A12001 = attrString;
			}
			tempVariable = new VariableBean("A12001", A12001, "N", "EX",
					"贷款机构数");
			attachVariableA.add(tempVariable);

			String A12002 = "";// 贷款总笔数：
			attrString = variable.getAttribute("A12002").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A12002 = "0";
			} else {
				A12002 = attrString;
			}
			tempVariable = new VariableBean("A12002", A12002, "N", "EX",
					"贷款总笔数");
			attachVariableA.add(tempVariable);

			String A12003 = "";// 贷款非正常账户数：
			attrString = variable.getAttribute("A12003").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A12003 = "0";
			} else {
				A12003 = attrString;
			}
			tempVariable = new VariableBean("A12003", A12003, "N", "EX",
					"贷款非正常账户数");
			attachVariableA.add(tempVariable);

			String A12004 = "";// 贷款合同总额：
			attrString = variable.getAttribute("A12004").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A12004 = "0";
			} else {
				A12004 = attrString;
			}
			tempVariable = new VariableBean("A12004", A12004, "N", "EX",
					"贷款合同总额");
			attachVariableA.add(tempVariable);

			String A12005 = "";// 贷款总余额：
			attrString = variable.getAttribute("A12005").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A12005 = "0";
			} else {
				A12005 = attrString;
			}
			tempVariable = new VariableBean("A12005", A12005, "N", "EX",
					"贷款总余额");
			attachVariableA.add(tempVariable);

			String A12006 = "";// 贷款最近6个月平均应还款：
			attrString = variable.getAttribute("A12006").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A12006 = "0";
			} else {
				A12006 = attrString;
			}
			tempVariable = new VariableBean("A12006", A12006, "N", "EX",
					"贷款最近6个月平均应还款");
			attachVariableA.add(tempVariable);

			String A12007 = "";// 贷款本月应还款总额：
			attrString = variable.getAttribute("A12007").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A12007 = "0";
			} else {
				A12007 = attrString;
			}
			tempVariable = new VariableBean("A12007", A12007, "N", "EX",
					"贷款本月应还款总额");
			attachVariableA.add(tempVariable);

			String A12008 = "";// 贷款最近连续正常还款期数>=3的本月应还款总额：
			attrString = variable.getAttribute("A12008").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A12008 = "0";
			} else {
				A12008 = attrString;
			}
			tempVariable = new VariableBean("A12008", A12008, "N", "EX",
					"贷款最近连续正常还款期数>=3的本月应还款总额");
			attachVariableA.add(tempVariable);

			String A12009 = "";// 贷款当前逾期总额
			attrString = variable.getAttribute("A12009").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A12009 = "0";
			} else {
				A12009 = attrString;
			}
			tempVariable = new VariableBean("A12009", A12009, "N", "EX",
					"贷款当前逾期总额");
			attachVariableA.add(tempVariable);

			String A12010 = "";// 贷款（31-个人助学贷款）的笔数：
			attrString = variable.getAttribute("A12010").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A12010 = "0";
			} else {
				A12010 = attrString;
			}
			tempVariable = new VariableBean("A12010", A12010, "N", "EX",
					"贷款（31-个人助学贷款）的笔数");
			attachVariableA.add(tempVariable);

			String A13001 = "";// 贷款近3个月M1的次数：
			attrString = variable.getAttribute("A13001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13001 = "0";
			} else {
				A13001 = attrString;
			}
			tempVariable = new VariableBean("A13001", A13001, "N", "EX",
					"贷款近3个月M1的次数");
			attachVariableA.add(tempVariable);

			String A13002 = "";// 贷款近3个月M2的次数：
			attrString = variable.getAttribute("A13002").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13002 = "0";
			} else {
				A13002 = attrString;
			}
			tempVariable = new VariableBean("A13002", A13002, "N", "EX",
					"贷款近3个月M2的次数");
			attachVariableA.add(tempVariable);

			String A13003 = "";// 贷款近3个月M3的次数：
			attrString = variable.getAttribute("A13003").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13003 = "0";
			} else {
				A13003 = attrString;
			}
			tempVariable = new VariableBean("A13003", A13003, "N", "EX",
					"贷款近3个月M3的次数");
			attachVariableA.add(tempVariable);

			String A13004 = "";// 贷款近3个月M4的次数：
			attrString = variable.getAttribute("A13004").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13004 = "0";
			} else {
				A13004 = attrString;
			}
			tempVariable = new VariableBean("A13004", A13004, "N", "EX",
					"贷款近3个月M4的次数");
			attachVariableA.add(tempVariable);

			String A13005 = "";// 贷款近3个月M5的次数：
			attrString = variable.getAttribute("A13005").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13005 = "0";
			} else {
				A13005 = attrString;
			}
			tempVariable = new VariableBean("A13005", A13005, "N", "EX",
					"贷款近3个月M5的次数");
			attachVariableA.add(tempVariable);

			String A13006 = "";// 贷款近3个月M6的次数：
			attrString = variable.getAttribute("A13006").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13006 = "0";
			} else {
				A13006 = attrString;
			}
			tempVariable = new VariableBean("A13006", A13006, "N", "EX",
					"贷款近3个月M6的次数");
			attachVariableA.add(tempVariable);

			String A13007 = "";// 贷款近3个月M7的次数：
			attrString = variable.getAttribute("A13007").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13007 = "0";
			} else {
				A13007 = attrString;
			}
			tempVariable = new VariableBean("A13007", A13007, "N", "EX",
					"贷款近3个月M7的次数");
			attachVariableA.add(tempVariable);

			String A13008 = "";// 贷款近6个月M1的次数：
			attrString = variable.getAttribute("A13008").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13008 = "-999";
			} else {
				A13008 = attrString;
			}
			tempVariable = new VariableBean("A13008", A13008, "N", "EX",
					"贷款近6个月M1的次数");
			attachVariableA.add(tempVariable);

			String A13009 = "";// 贷款近6个月M2的次数：
			attrString = variable.getAttribute("A13009").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13009 = "0";
			} else {
				A13009 = attrString;
			}
			tempVariable = new VariableBean("A13009", A13009, "N", "EX",
					"贷款近6个月M2的次数");
			attachVariableA.add(tempVariable);

			String A13010 = "";// 贷款近6个月M3的次数：
			attrString = variable.getAttribute("A13010").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13010 = "0";
			} else {
				A13010 = attrString;
			}
			tempVariable = new VariableBean("A13010", A13010, "N", "EX",
					"贷款近6个月M3的次数");
			attachVariableA.add(tempVariable);

			String A13011 = "";// 贷款近6个月M4的次数：
			attrString = variable.getAttribute("A13011").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13011 = "0";
			} else {
				A13011 = attrString;
			}
			tempVariable = new VariableBean("A13011", A13011, "N", "EX",
					"贷款近6个月M4的次数");
			attachVariableA.add(tempVariable);

			String A13012 = "";// 贷款近6个月M5的次数：
			attrString = variable.getAttribute("A13012").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13012 = "0";
			} else {
				A13012 = attrString;
			}
			tempVariable = new VariableBean("A13012", A13012, "N", "EX",
					"贷款近6个月M5的次数");
			attachVariableA.add(tempVariable);

			String A13013 = "";// 贷款近6个月M6的次数：
			attrString = variable.getAttribute("A13013").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13013 = "0";
			} else {
				A13013 = attrString;
			}
			tempVariable = new VariableBean("A13013", A13013, "N", "EX",
					"贷款近6个月M6的次数");
			attachVariableA.add(tempVariable);

			String A13014 = "";// 贷款近6个月M7的次数：
			attrString = variable.getAttribute("A13014").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13014 = "0";
			} else {
				A13014 = attrString;
			}
			tempVariable = new VariableBean("A13014", A13014, "N", "EX",
					"贷款近6个月M7的次数");
			attachVariableA.add(tempVariable);

			String A13015 = "";// 贷款近12个月M1的次数：
			attrString = variable.getAttribute("A13015").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13015 = "0";
			} else {
				A13015 = attrString;
			}
			tempVariable = new VariableBean("A13015", A13015, "N", "EX",
					"贷款近12个月M1的次数");
			attachVariableA.add(tempVariable);

			String A13016 = "";// 贷款近12个月M2的次数：
			attrString = variable.getAttribute("A13016").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13016 = "0";
			} else {
				A13016 = attrString;
			}
			tempVariable = new VariableBean("A13016", A13016, "N", "EX",
					"贷款近12个月M2的次数");
			attachVariableA.add(tempVariable);

			String A13017 = "";// 贷款近12个月M3的次数：
			attrString = variable.getAttribute("A13017").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13017 = "0";
			} else {
				A13017 = attrString;
			}
			tempVariable = new VariableBean("A13017", A13017, "N", "EX",
					"贷款近12个月M3的次数");
			attachVariableA.add(tempVariable);

			String A13018 = "";// 贷款近12个月M4的次数：
			attrString = variable.getAttribute("A13018").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13018 = "0";
			} else {
				A13018 = attrString;
			}
			tempVariable = new VariableBean("A13018", A13018, "N", "EX",
					"贷款近12个月M4的次数");
			attachVariableA.add(tempVariable);

			String A13019 = "";// 贷款近12个月M5的次数：
			attrString = variable.getAttribute("A13019").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13019 = "0";
			} else {
				A13019 = attrString;
			}
			tempVariable = new VariableBean("A13019", A13019, "N", "EX",
					"贷款近12个月M5的次数");
			attachVariableA.add(tempVariable);

			String A13020 = "";// 贷款近12个月M6的次数：
			attrString = variable.getAttribute("A13020").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13020 = "0";
			} else {
				A13020 = attrString;
			}
			tempVariable = new VariableBean("A13020", A13020, "N", "EX",
					"贷款近12个月M6的次数");
			attachVariableA.add(tempVariable);

			String A13021 = "";// 贷款近12个月M7的次数：
			attrString = variable.getAttribute("A13021").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13021 = "0";
			} else {
				A13021 = attrString;
			}
			tempVariable = new VariableBean("A13021", A13021, "N", "EX",
					"贷款近12个月M7的次数");
			attachVariableA.add(tempVariable);

			String A13022 = "";// 贷款近24个月M1的次数：
			attrString = variable.getAttribute("A13022").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13022 = "0";
			} else {
				A13022 = attrString;
			}
			tempVariable = new VariableBean("A13022", A13022, "N", "EX",
					"贷款近24个月M1的次数");
			attachVariableA.add(tempVariable);

			String A13023 = "";// 贷款近24个月M2的次数：
			attrString = variable.getAttribute("A13023").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13023 = "0";
			} else {
				A13023 = attrString;
			}
			tempVariable = new VariableBean("A13023", A13023, "N", "EX",
					"贷款近24个月M2的次数");
			attachVariableA.add(tempVariable);

			String A13024 = "";// 贷款近24个月M3的次数：
			attrString = variable.getAttribute("A13024").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13024 = "0";
			} else {
				A13024 = attrString;
			}
			tempVariable = new VariableBean("A13024", A13024, "N", "EX",
					"贷款近24个月M3的次数");
			attachVariableA.add(tempVariable);

			String A13025 = "";// 贷款近24个月M4的次数：
			attrString = variable.getAttribute("A13025").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13025 = "0";
			} else {
				A13025 = attrString;
			}
			tempVariable = new VariableBean("A13025", A13025, "N", "EX",
					"贷款近24个月M4的次数");
			attachVariableA.add(tempVariable);

			String A13026 = "";// 贷款近24个月M5的次数：
			attrString = variable.getAttribute("A13026").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13026 = "0";
			} else {
				A13026 = attrString;
			}
			tempVariable = new VariableBean("A13026", A13026, "N", "EX",
					"贷款近24个月M5的次数");
			attachVariableA.add(tempVariable);

			String A13027 = "";// 贷款近24个月M6的次数：
			attrString = variable.getAttribute("A13027").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13027 = "0";
			} else {
				A13027 = attrString;
			}
			tempVariable = new VariableBean("A13027", A13027, "N", "EX",
					"贷款近24个月M6的次数");
			attachVariableA.add(tempVariable);

			String A13028 = "";// 贷款近24个月M7的次数：
			attrString = variable.getAttribute("A13028").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13028 = "0";
			} else {
				A13028 = attrString;
			}
			tempVariable = new VariableBean("A13028", A13028, "N", "EX",
					"贷款近24个月M7的次数");
			attachVariableA.add(tempVariable);

			String A13029 = "";// 贷款近5年前36个月逾期最高持续月数：
			attrString = variable.getAttribute("A13029").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13029 = "0";
			} else {
				A13029 = attrString;
			}
			tempVariable = new VariableBean("A13029", A13029, "N", "EX",
					"贷款近5年前36个月逾期最高持续月数");
			attachVariableA.add(tempVariable);

			String A13030 = "";// 贷款当月最高逾期级别：
			attrString = variable.getAttribute("A13030").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13030 = "0";
			} else {
				if (attrString.indexOf("|") > 0) {
					time = attrString.split("|");
					A13030 = time[0];
				} else {
					A13030 = attrString;
				}
			}
			tempVariable = new VariableBean("A13030", A13030, "N", "EX",
					"贷款当月最高逾期级别");
			attachVariableA.add(tempVariable);

			String A14001 = "";// 担保贷款五级分类为非正常的总数：
			attrString = variable.getAttribute("A14001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A14001 = "0";
			} else {
				A14001 = attrString;
			}
			tempVariable = new VariableBean("A14001", A14001, "N", "EX",
					"担保贷款五级分类为非正常的总数");
			attachVariableA.add(tempVariable);

			String A14002 = "";// 担保贷款五级分类为非正常的累计本金余额：
			attrString = variable.getAttribute("A14002").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A14002 = "0";
			} else {
				A14002 = attrString;
			}
			tempVariable = new VariableBean("A14002", A14002, "N", "EX",
					"担保贷款五级分类为非正常的累计本金余额");
			attachVariableA.add(tempVariable);

			String A15001 = "";// 最近1个月内的查询机构数-贷款审批：
			attrString = variable.getAttribute("A15001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A15001 = "0";
			} else {
				A15001 = attrString;
			}
			tempVariable = new VariableBean("A15001", A15001, "N", "EX",
					"最近1个月内的查询机构数-贷款审批");
			attachVariableA.add(tempVariable);

			String A15002 = "";// 最近1个月内的查询机构数-信用卡审批：
			attrString = variable.getAttribute("A15002").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A15002 = "0";
			} else {
				A15002 = attrString;
			}
			tempVariable = new VariableBean("A15002", A15002, "N", "EX",
					"最近1个月内的查询机构数-信用卡审批");
			attachVariableA.add(tempVariable);

			String A15003 = "";// 最近1个月内的查询次数-贷款审批：
			attrString = variable.getAttribute("A15003").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A15003 = "0";
			} else {
				A15003 = attrString;
			}
			tempVariable = new VariableBean("A15003", A15003, "N", "EX",
					"最近1个月内的查询次数-贷款审批");
			attachVariableA.add(tempVariable);

			String A15004 = "-9";// 最近1个月内的查询次数-信用卡审批：
			attrString = variable.getAttribute("A15004").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A15004 = "-999";
			} else {
				A15004 = attrString;
			}
			tempVariable = new VariableBean("A15004", A15004, "N", "EX",
					"最近1个月内的查询次数-信用卡审批");
			attachVariableA.add(tempVariable);

			String A15005 = "";// 最近2年内的查询次数-贷后管理：
			attrString = variable.getAttribute("A15005").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A15005 = "-999";
			} else {
				A15005 = attrString;
			}
			tempVariable = new VariableBean("A15005", A15005, "N", "EX",
					"最近2年内的查询次数-贷后管理");
			attachVariableA.add(tempVariable);

			String A15006 = "";// 最近2年内的查询次数-担保资格审查：
			attrString = variable.getAttribute("A15006").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A15006 = "-999";
			} else {
				A15006 = attrString;
			}
			tempVariable = new VariableBean("A15006", A15006, "N", "EX",
					"最近2年内的查询次数-担保资格审查");
			attachVariableA.add(tempVariable);

			String A15007 = "";// 最近2年内的查询次数-特约商户实名审查：
			attrString = variable.getAttribute("A15007").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A15007 = "0";
			} else {
				A15007 = attrString;
			}
			tempVariable = new VariableBean("A15007", A15007, "N", "EX",
					"最近2年内的查询次数-特约商户实名审查");
			attachVariableA.add(tempVariable);

			double num1 = 0, num2 = 0;
			String A15012 = "";// 银行代发工资、存折
			if ("11".equals(AT3220)) {
				num1 = Double.parseDouble(AT3230);
			}
			if ("11".equals(AT3240)) {
				num2 = Double.parseDouble(AT3250);
				;
			}
			if (num1 >= num2) {
				A15012 = String.valueOf(num1);
			} else {
				A15012 = String.valueOf(num2);
			}
			tempVariable = new VariableBean("A15012", A15012, "N", "EX",
					"银行代发工资、存折");
			attachVariableA.add(tempVariable);

			String A15013 = "";// 账单月收入
			attrString = variable.getAttribute("A15013").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A15013 = "0";
			} else {
				A15013 = attrString;
			}
			tempVariable = new VariableBean("A15013", A15013, "N", "EX",
					"账单月收入");
			attachVariableA.add(tempVariable);

			num1 = 0;
			num2 = 0;
			String A15015 = "";// 可证明收入数值其他年收入
			if ("18".equals(AT3220)) {
				num1 = Double.parseDouble(AT3230);
			}
			if ("18".equals(AT3240)) {
				num2 = Double.parseDouble(AT3250);
			}
			if (num1 >= num2) {
				A15015 = String.valueOf(num1);
			} else {
				A15015 = String.valueOf(num2);
			}
			tempVariable = new VariableBean("A15015", A15015, "N", "EX",
					"可证明收入数值其他年收入");
			attachVariableA.add(tempVariable);

			String A15016 = "";// 税前月收入
			A15016 = AT0700;
			;
			tempVariable = new VariableBean("A15016", A15016, "N", "EX",
					"税前月收入");
			attachVariableA.add(tempVariable);

			num1 = 0;
			num2 = 0;
			String A15017 = "";// 知名企业月收入
			if ("15".equals(AT3220)) {
				num1 = Double.parseDouble(AT3230);
			}
			if ("15".equals(AT3240)) {
				num2 = Double.parseDouble(AT3250);
			}
			if (num1 >= num2) {
				A15017 = String.valueOf(num1);
			} else {
				A15017 = String.valueOf(num2);
			}
			tempVariable = new VariableBean("A15017", A15017, "N", "EX",
					"知名企业月收入");
			attachVariableA.add(tempVariable);

			num1 = 0;
			num2 = 0;
			String A15018 = "";// 政府或事业单位月收入
			if ("16".equals(AT3220)) {
				num1 = Double.parseDouble(AT3230);
			}
			if ("16".equals(AT3240)) {
				num2 = Double.parseDouble(AT3250);
			}
			if (num1 >= num2) {
				A15018 = String.valueOf(num1);
			} else {
				A15018 = String.valueOf(num2);
			}
			tempVariable = new VariableBean("A15018", A15018, "N", "EX",
					"政府或事业单位月收入");
			attachVariableA.add(tempVariable);

			String A12011 = "";// 房产价值
			attrString = variable.getAttribute("A12011").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A12011 = "0";
			} else {
				A12011 = attrString;
			}
			tempVariable = new VariableBean("A12011", A12011, "N", "EX", "房产价值");
			attachVariableA.add(tempVariable);

			String A15033 = "";// 发卡账户数
			attrString = variable.getAttribute("A15033").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A15033 = "0";
			} else {
				A15033 = attrString;
			}
			tempVariable = new VariableBean("A15033", A15033, "N", "EX",
					"发卡账户数");
			attachVariableA.add(tempVariable);

			String AT4300 = "";// VIP等级
			attrString = variable.getAttribute("AT4300").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4300 = "6";
			} else {
				if ("1".equals(attrString)) {
					AT4300 = "1";
				} else if ("2".equals(attrString)) {
					AT4300 = "2";
				} else if ("5".equals(attrString)) {
					AT4300 = "3";
				} else if ("6".equals(attrString)) {
					AT4300 = "4";
				} else if ("C".equalsIgnoreCase(attrString)) {
					AT4300 = "5";
				} else {
					AT4300 = "6";
				}
			}
			tempVariable = new VariableBean("AT4300", AT4300, "N", "EX",
					"VIP等级");
			attachVariableA.add(tempVariable);

			String AT4310 = "";// 是否为公安检测出4类人员
			attrString = variable.getAttribute("AT4310").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4310 = "2";
			} else {
				/**C1507封版后，统一改为判断1和Y*/
				if ("Y".equalsIgnoreCase(attrString) || "1".equalsIgnoreCase(attrString)) {
					AT4310 = "1";
				} else {
					AT4310 = "2";
				}
			}
			tempVariable = new VariableBean("AT4310", AT4310, "N", "EX",
					"是否为公安检测出4类人员");
			attachVariableA.add(tempVariable);

			String AT4320 = "";// 所属机构号(一级分行)
			attrString = variable.getAttribute("AT4320").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4320 = "-999";
			} else {
				AT4320 = attrString;
			}
			tempVariable = new VariableBean("AT4320", AT4320, "N", "EX",
					"所属机构号");
			attachVariableA.add(tempVariable);

			String A08015 = "";// 符合要求贷记卡中信用额度最大值
			attrString = variable.getAttribute("A08015").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08015 = "0";
			} else {
				A08015 = attrString;
			}
			tempVariable = new VariableBean("A08015", A08015, "N", "EX",
					"符合要求贷记卡中信用额度最大值");
			attachVariableA.add(tempVariable);

			String A10015 = "";// 符合要求准贷记卡中信用额度最大值
			attrString = variable.getAttribute("A10015").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10015 = "0";
			} else {
				A10015 = attrString;
			}
			tempVariable = new VariableBean("A10015", A10015, "N", "EX",
					"符合要求准贷记卡中信用额度最大值");
			attachVariableA.add(tempVariable);

			String A08016 = "";// 贷记卡逾期-最长逾期月数
			attrString = variable.getAttribute("A08016").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08016 = "0";
			} else {
				A08016 = attrString;
			}
			tempVariable = new VariableBean("A08016", A08016, "N", "EX",
					"贷记卡逾期-最长逾期月数");
			attachVariableA.add(tempVariable);

			String A10016 = "";// 准贷记卡60天以上透支-最长透支月数
			attrString = variable.getAttribute("A10016").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10016 = "0";
			} else {
				A10016 = attrString;
			}
			tempVariable = new VariableBean("A10016", A10016, "N", "EX",
					"准贷记卡60天以上透支-最长透支月数");
			attachVariableA.add(tempVariable);

			String A12012 = "";// 贷款逾期-最长逾期月数
			attrString = variable.getAttribute("A12012").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A12012 = "0";
			} else {
				A12012 = attrString;
			}
			tempVariable = new VariableBean("A12012", A12012, "N", "EX",
					"贷款逾期-最长逾期月数");
			attachVariableA.add(tempVariable);

			String AT4330 = "";// 要客分类
			attrString = variable.getAttribute("AT4330").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4330 = "5";
			} else {
				if ("A".equalsIgnoreCase(attrString)) {
					AT4330 = "1";
				} else if ("B".equalsIgnoreCase(attrString)) {
					AT4330 = "2";
				} else if ("C".equalsIgnoreCase(attrString)) {
					AT4330 = "3";
				} else if ("D".equalsIgnoreCase(attrString)) {
					AT4330 = "4";
				} else {
					AT4330 = "5";
				}
			}
			tempVariable = new VariableBean("AT4330", AT4330, "N", "EX", "要客分类");
			attachVariableA.add(tempVariable);
			/**
			 * 需求：临增、长增变量
			 * */
			String AT4410 = "";// 工单产品是否专用卡：
			attrString = variable.getAttribute("AT4410").trim(); // 获取XML中A04001变量的值
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4410 = "2";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT4410 = "1";
				} else {
					AT4410 = "2";
				}
			}
			tempVariable = new VariableBean("AT4410", AT4410, "N", "EX",
					"工单产品是否专用卡");
			attachVariableA.add(tempVariable);

			String AT4420 = "";// 清偿方式：
			attrString = variable.getAttribute("AT4420").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4420 = "2";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT4420 = "1";
				} else {
					AT4420 = "2";
				}
			}
			tempVariable = new VariableBean("AT4420", AT4420, "N", "EX", "清偿方式");
			attachVariableA.add(tempVariable);

			String AT4421 = "";// 公司组织机构代码号是否存在于支持临增公司名单中：
			attrString = variable.getAttribute("AT4421").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4421 = "2";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT4421 = "1";
				} else {
					AT4421 = "2";
				}
			}
			tempVariable = new VariableBean("AT4421", AT4421, "N", "EX",
					"公司组织机构代码号是否存在于支持临增公司名单中");
			attachVariableA.add(tempVariable);

			String AT4422 = "";// 公司组织机构代码号是否存在于支持长增公司名单中：
			attrString = variable.getAttribute("AT4422").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4422 = "2";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT4422 = "1";
				} else {
					AT4422 = "2";
				}
			}
			tempVariable = new VariableBean("AT4422", AT4422, "N", "EX",
					"公司组织机构代码号是否存在于支持长增公司名单中");
			attachVariableA.add(tempVariable);

			String AT4430 = "";// 是否为睡眠帐户：
			attrString = variable.getAttribute("AT4430").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4430 = "2";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT4430 = "1";
				} else {
					AT4430 = "2";
				}
			}
			tempVariable = new VariableBean("AT4430", AT4430, "N", "EX",
					"是否为睡眠帐户");
			attachVariableA.add(tempVariable);

			String AT4450 = "";// 客户证件类型：
			attrString = variable.getAttribute("AT4450").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4450 = "1";
			} else {
				if ("I".equalsIgnoreCase(attrString)) {
					AT4450 = "6";
				} else if ("J".equalsIgnoreCase(attrString)) {
					AT4450 = "7";
				} else {
					AT4450 = attrString;
				}
			}
			tempVariable = new VariableBean("AT4450", AT4450, "N", "EX",
					"客户证件类型");
			attachVariableA.add(tempVariable);

			String AT4750 = "";// 申请卡币种：
			attrString = variable.getAttribute("AT4750").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4750 = "1";
			} else {
				if ("156".equalsIgnoreCase(attrString)) {
					AT4750 = "1";// 人民币
				} else {
					AT4750 = "2";// 非人民币为2
				}
			}
			tempVariable = new VariableBean("AT4750", AT4750, "N", "EX",
					"申请卡币种");
			attachVariableA.add(tempVariable);
			// Double A4760 = Double.parseDouble(AT4760);
			// Double A4750 = 0.0;//工单产品的当前永久额度（产品层）(工单产品为主卡的当前额度)
			// Double A6020 = 0.0;//客户所有名下有效卡的最高永久额度
			// Double A6030 = 0.0;//工单卡片子级CC01当前永久额度
			// if(!"0".equalsIgnoreCase(AT4440)){
			// A4750 = Double.parseDouble(AT4440)*A4760;
			// }
			// AT4440 = A4750.toString();
			// if(!"0".equalsIgnoreCase(AT6020)){
			// A6020 = Double.parseDouble(AT6020)*A4760;
			// }
			// AT6020 = A6020.toString();
			// if(!"0".equalsIgnoreCase(AT6030)){
			// A6030 = Double.parseDouble(AT6030)*A4760;
			// }
			// AT6030 = A6030.toString();

			String AT4760 = "";// 申请卡汇率：
			Double B4760 = 0.0;
			attrString = variable.getAttribute("AT4760").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4760 = "0";
			} else {
				if (attrString.indexOf("/") != -1) {
					String[] A4760 = attrString.split("/");
					B4760 = Double.parseDouble(A4760[0])
							/ Double.parseDouble(A4760[1]);
					AT4760 = B4760.toString();
				} else {
					AT4760 = attrString.toString();
				}
			}
			tempVariable = new VariableBean("AT4760", AT4760, "N", "EX",
					"申请卡汇率");
			attachVariableA.add(tempVariable);
			// attachVariableA.add(tempVariable);
			// tempVariable = new VariableBean("AT4440", AT4440, "N", "EX",
			// "工单产品的当前永久额度（产品层）(工单产品为主卡的当前额度)");
			// attachVariableA.add(tempVariable);
			// tempVariable = new VariableBean("AT6020", AT6020, "N", "EX",
			// "客户所有名下有效卡的最高永久额度");
			// attachVariableA.add(tempVariable);
			// tempVariable = new VariableBean("AT6030", AT6030, "N", "EX",
			// "工单卡片子级CC01当前永久额度");
			// attachVariableA.add(tempVariable);

			String AT4440 = "";// 工单产品的当前永久额度（产品层）(工单产品为主卡的当前额度)：
			attrString = variable.getAttribute("AT4440").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4440 = "0";
			} else {
				AT4440 = attrString;
				// if(!"1".equalsIgnoreCase(AT4750)){
				// Double A4760 = Double.parseDouble(AT4760);
				// Double A4440 = Double.parseDouble(AT4440)*A4760;
				// AT4440 = A4440.toString();
				// }

			}
			tempVariable = new VariableBean("AT4440", AT4440, "N", "EX",
					"工单产品的当前永久额度（产品层）(工单产品为主卡的当前额度)");
			attachVariableA.add(tempVariable);

			String AT4470 = "";// 近半年内客户顶层永久额度提升次数：
			attrString = variable.getAttribute("AT4470").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4470 = "0";
			} else {
				AT4470 = attrString;
			}
			tempVariable = new VariableBean("AT4470", AT4470, "N", "EX",
					"近半年内客户顶层永久额度提升次数");
			attachVariableA.add(tempVariable);

			String AT4451 = "";// 客户身份证号码：
			attrString = variable.getAttribute("AT4451").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4451 = "0";
			} else {
				AT4451 = attrString;
			}
			// tempVariable = new VariableBean("AT4451", AT4451, "N", "EX",
			// "客户身份证号码");
			// attachVariableA.add(tempVariable);

			String AT4460 = "";// 客户出生日期：
			attrString = variable.getAttribute("AT4460").trim();
			if (attrString.length() >= 10) {
				attrString = this.changeDataFormat(attrString);
			}
			if (this.isDate(attrString)) {
				AT4460 = attrString;
			} else {
				AT4460 = "-9";
			}
			// tempVariable = new VariableBean("AT4460", AT4460, "N", "EX",
			// "客户出生日期");
			// attachVariableA.add(tempVariable);

			// String AT4480 = "";//工单产品是否存在于NUW“不取用额度产品名单”：
			// attrString = variable.getAttribute("AT4480").trim();
			// if (null==attrString || "-999".equals(attrString) ||
			// "".equals(attrString) || "null".equalsIgnoreCase(attrString)) {
			// AT4480 = "2";
			// }else{
			// if("1".equalsIgnoreCase(attrString)){
			// AT4480 = "1";
			// }else{
			// AT4480 = "2";
			// }
			// }
			// tempVariable = new VariableBean("AT4480", AT4480, "N", "EX",
			// "工单产品是否存在于NUW“不取用额度产品名单”");
			// attachVariableA.add(tempVariable);

			String AT4530 = "";// 近半年内客户顶层临时额度提升次数：
			attrString = variable.getAttribute("AT4530").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4530 = "0";
			} else {
				AT4530 = attrString;
			}
			tempVariable = new VariableBean("AT4530", AT4530, "N", "EX",
					"近半年内客户顶层临时额度提升次数");
			attachVariableA.add(tempVariable);

			String AT4550 = "";// 全部产品层账户争议交易金额>0则为争议账户：
			attrString = variable.getAttribute("AT4550").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4550 = "2";
			} else {
				AT4550 = attrString;
				Integer num = 0;
				if (AT4550.contains("，")) {
					AT4550 = AT4550.replace("，", ",");
				}
				if (AT4550.contains(",")) {
					String A4[] = AT4550.split(",");
					for (Integer i = 0; i < A4.length; i++) {
						if (Double.parseDouble(A4[i]) > 0) {
							num = num + 1;
						}
					}
				} else {
					if (Double.parseDouble(AT4550) > 0) {
						AT4550 = "1";
					} else {
						AT4550 = "2";
					}
				}
				if (num >= 1) {
					AT4550 = "1";
				}

			}
			tempVariable = new VariableBean("AT4550", AT4550, "N", "EX",
					"是否争议账户");
			attachVariableA.add(tempVariable);

			String AT4590 = "";// 全部产品层账户过去12个月内逾期M1及以上次数：
			attrString = variable.getAttribute("AT4590").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4590 = "0";
			} else {
				AT4590 = attrString;
				Integer max = 0;
				if (AT4590.contains("，")) {
					AT4590 = AT4590.replace("，", ",");
				}
				if (AT4590.contains(",")) {
					String A4[] = AT4590.split(",");
					for (Integer i = 0; i < A4.length; i++) {
						if (i == 0) {
							max = Integer.parseInt(A4[i]);
						}
						for (Integer j = i + 1; j < A4.length; j++) {
							if (max <= Integer.parseInt(A4[j])) {
								max = Integer.parseInt(A4[j]);
							}
						}
					}
					AT4590 = max.toString();
				}

			}
			tempVariable = new VariableBean("AT4590", AT4590, "N", "EX",
					"全部产品层账户过去12个月内逾期M1及以上次数");
			attachVariableA.add(tempVariable);

			String AT4600 = "";// 客户是否存在于分行报送增额黑名单：
			attrString = variable.getAttribute("AT4600").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4600 = "2";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT4600 = "1";
				} else {
					AT4600 = "2";
				}
			}
			tempVariable = new VariableBean("AT4600", AT4600, "N", "EX",
					"客户是否存在于分行报送增额黑名单");
			attachVariableA.add(tempVariable);

			String AT4610 = "";// 客户是否存在于分行报送增额灰名单：
			attrString = variable.getAttribute("AT4610").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4610 = "2";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT4610 = "1";
				} else {
					AT4610 = "2";
				}
			}
			tempVariable = new VariableBean("AT4610", AT4610, "N", "EX",
					"客户是否存在于分行报送增额灰名单");
			attachVariableA.add(tempVariable);

			// String AT4620 = "";//客户是否被风险降额(客户名下全部卡片包含主附卡)：
			// attrString = variable.getAttribute("AT4620").trim();
			// if (null==attrString || "-999".equals(attrString) ||
			// "".equals(attrString) || "null".equalsIgnoreCase(attrString)) {
			// AT4620 = "2";
			// }else{
			// if("1".equalsIgnoreCase(attrString)||"Y".equalsIgnoreCase(attrString)){
			// AT4620 = "1";
			// }else{
			// AT4620 = "2";
			// }
			// }
			// tempVariable = new VariableBean("AT4620", AT4620, "N", "EX",
			// "客户是否被风险降额(客户名下全部卡片包含主附卡)");
			// attachVariableA.add(tempVariable);

			String AT4630 = "";// 近6个月内，连续未全额还款次数：
			attrString = variable.getAttribute("AT4630").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4630 = "0";
			} else {
				AT4630 = attrString;
			}
			tempVariable = new VariableBean("AT4630", AT4630, "N", "EX",
					"近6个月内，连续未全额还款次数");
			attachVariableA.add(tempVariable);

			String AT4640 = "";// 近6个月内，累计未全额还款次数：
			attrString = variable.getAttribute("AT4640").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4640 = "0";
			} else {
				AT4640 = attrString;
			}
			tempVariable = new VariableBean("AT4640", AT4640, "N", "EX",
					"近6个月内，累计未全额还款次数");
			attachVariableA.add(tempVariable);

			String AT4650 = "";// 近6个月内，最近一期账单是否全额还款：
			attrString = variable.getAttribute("AT4650").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4650 = "2";
			} else {
				if ("1".equalsIgnoreCase(attrString)) {
					AT4650 = "1";
				} else {
					AT4650 = "2";
				}
			}
			tempVariable = new VariableBean("AT4650", AT4650, "N", "EX",
					"近6个月内，最近一期账单是否全额还款");
			attachVariableA.add(tempVariable);

			String AT4660 = "";// 近6个月内，M1次数：
			attrString = variable.getAttribute("AT4660").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4660 = "0";
			} else {
				AT4660 = attrString;
			}
			tempVariable = new VariableBean("AT4660", AT4660, "N", "EX",
					"近6个月内，M1次数");
			attachVariableA.add(tempVariable);

			String AT4670 = "";// 卡龄：
			attrString = variable.getAttribute("AT4670").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4670 = "0";
			} else {
				AT4670 = attrString;
			}
			tempVariable = new VariableBean("AT4670", AT4670, "N", "EX", "卡龄");
			attachVariableA.add(tempVariable);

			String AT4680 = "";// 近12个月内，连续未全额还款次数：
			attrString = variable.getAttribute("AT4680").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4680 = "0";
			} else {
				AT4680 = attrString;
			}
			tempVariable = new VariableBean("AT4680", AT4680, "N", "EX",
					"近12个月内，连续未全额还款次数");
			attachVariableA.add(tempVariable);

			String AT4690 = "";// 近12个月内，累计未全额还款次数：
			attrString = variable.getAttribute("AT4690").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4690 = "0";
			} else {
				AT4690 = attrString;
			}
			tempVariable = new VariableBean("AT4690", AT4690, "N", "EX",
					"近12个月内，累计未全额还款次数");
			attachVariableA.add(tempVariable);

			String AT4700 = "";// 近12个月内，M1次数：
			attrString = variable.getAttribute("AT4700").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4700 = "0";
			} else {
				AT4700 = attrString;
			}
			tempVariable = new VariableBean("AT4700", AT4700, "N", "EX",
					"近12个月内，M1次数");
			attachVariableA.add(tempVariable);

			String AT4710 = "";// 近12个月内，M2次数：
			attrString = variable.getAttribute("AT4710").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4710 = "0";
			} else {
				AT4710 = attrString;
			}
			tempVariable = new VariableBean("AT4710", AT4710, "N", "EX",
					"近12个月内，M2次数");
			attachVariableA.add(tempVariable);

			String AT4720 = "";// 近24个月内，M3及M3+次数：
			attrString = variable.getAttribute("AT4720").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4720 = "0";
			} else {
				AT4720 = attrString;
			}
			tempVariable = new VariableBean("AT4720", AT4720, "N", "EX",
					"近24个月内，M3及M3+次数");
			attachVariableA.add(tempVariable);

			String AT4724 = "";// 是否年费逾期：
			attrString = variable.getAttribute("AT4724").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4724 = "2";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT4724 = "1";
				} else {
					AT4724 = "2";
				}
			}
			tempVariable = new VariableBean("AT4724", AT4724, "N", "EX",
					"是否年费逾期");
			attachVariableA.add(tempVariable);

			String AT4730 = "";// 套现规则触发结果：
			attrString = variable.getAttribute("AT4730").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4730 = "2";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT4730 = "1";
				} else {
					AT4730 = "2";
				}
			}
			tempVariable = new VariableBean("AT4730", AT4730, "N", "EX",
					"套现规则触发结果");
			attachVariableA.add(tempVariable);

			String AT4740 = "";// 全部产品账户近6个月平均额度使用率：
			attrString = variable.getAttribute("AT4740").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4740 = "0";
			} else {
				AT4740 = attrString;
				if (AT4740.contains("%") || AT4740.contains("%")) {// 存在百分号
					String[] a = AT4740.split("%");
					Double b = Double.parseDouble(a[0]) / 100;
					AT4740 = b.toString();
				}
			}
			tempVariable = new VariableBean("AT4740", AT4740, "N", "EX",
					"全部产品账户近6个月平均额度使用率");
			attachVariableA.add(tempVariable);

			String AT5600 = "";// 评分等级：
			attrString = variable.getAttribute("AT5600").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT5600 = "10";
			} else {
				if ("XA".equalsIgnoreCase(attrString)) {
					AT5600 = "1";
				} else if ("XB".equalsIgnoreCase(attrString)) {
					AT5600 = "2";
				} else if ("ZA".equalsIgnoreCase(attrString)
						|| "ZB".equalsIgnoreCase(attrString)
						|| "ZC".equalsIgnoreCase(attrString)) {
					AT5600 = "3";
				} else if ("A".equalsIgnoreCase(attrString)) {
					AT5600 = "4";
				} else if ("B".equalsIgnoreCase(attrString)) {
					AT5600 = "5";
				} else if ("C".equalsIgnoreCase(attrString)) {
					AT5600 = "6";
				} else if ("D".equalsIgnoreCase(attrString)) {
					AT5600 = "7";
				} else if ("E".equalsIgnoreCase(attrString)) {
					AT5600 = "8";
				} else if ("XC".equalsIgnoreCase(attrString)) {
					AT5600 = "9";
				} else if ("Y".equalsIgnoreCase(attrString)) {
					AT5600 = "10";
				} else {
					AT5600 = attrString;
				}

			}
			tempVariable = new VariableBean("AT5600", AT5600, "N", "EX", "评分等级");
			attachVariableA.add(tempVariable);

			String AT5601 = "";// 预设额度：
			attrString = variable.getAttribute("AT5601").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT5601 = "0";
			} else {
				AT5601 = attrString;
			}
			tempVariable = new VariableBean("AT5601", AT5601, "N", "EX", "预设额度");
			attachVariableA.add(tempVariable);

			String AT5602 = "";// 渠道系统标识：
			attrString = variable.getAttribute("AT5602").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT5602 = "1"; // 总行NUW
			} else {
				if ("01".equalsIgnoreCase(attrString)) {
					AT5602 = "1"; // 总行NUW
				} else if ("02".equalsIgnoreCase(attrString)) {
					AT5602 = "2"; // 总行CSR
				} else if ("03".equalsIgnoreCase(attrString)) {
					AT5602 = "3"; // 总行CRM
				} else if ("04".equalsIgnoreCase(attrString)) {
					AT5602 = "4"; // 分行落地终端
				} else if ("05".equalsIgnoreCase(attrString)) {
					AT5602 = "5"; // 网银
				} else if ("06".equalsIgnoreCase(attrString)) {
					AT5602 = "6"; // 微信
				} else if ("07".equalsIgnoreCase(attrString)) {
					AT5602 = "7"; // 财富OCRM
				} else {
					AT5602 = attrString;
				}

			}
			tempVariable = new VariableBean("AT5602", AT5602, "N", "EX",
					"渠道系统标识");
			attachVariableA.add(tempVariable);

			// String AT5604 = "";//客户类型标识：
			// attrString = variable.getAttribute("AT5604").trim();
			// if (null==attrString || "-999".equals(attrString) ||
			// "".equals(attrString) || "null".equalsIgnoreCase(attrString)) {
			// AT5604 = "3"; //新客户
			// }else{
			// if("01".equalsIgnoreCase(attrString)){
			// AT5604 = "1"; //已持卡老客户
			// }else if("02".equalsIgnoreCase(attrString)){
			// AT5604 = "2"; //未持卡老客户
			// }else if("03".equalsIgnoreCase(attrString)){
			// AT5604 = "3"; //新客户
			// }
			//
			// }
			// tempVariable = new VariableBean("AT5604", AT5604, "N", "EX",
			// "客户类型标识");
			// attachVariableA.add(tempVariable);

			String AT5605 = "";// 任务类型标识：
			attrString = variable.getAttribute("AT5605").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT5605 = "1"; // 总行NUW
			} else {
				if ("01".equalsIgnoreCase(attrString)) {
					AT5605 = "1"; // 01申请评分
				} else if ("02".equalsIgnoreCase(attrString)) {
					AT5605 = "2"; // 02临增
				} else if ("03".equalsIgnoreCase(attrString)) {
					AT5605 = "3"; // 03长增
				} else if ("04".equalsIgnoreCase(attrString)) {
					AT5605 = "4"; // 04事件式增额、临增
				} else if ("05".equalsIgnoreCase(attrString)) {
					AT5605 = "5"; // 05批量临增
				} else if ("06".equalsIgnoreCase(attrString)) {
					AT5605 = "6"; // 06批量长增
				} else {
					AT5605 = attrString;
				}

			}
			tempVariable = new VariableBean("AT5605", AT5605, "N", "EX",
					"任务类型标识");
			attachVariableA.add(tempVariable);

			// String AT5606 = "";//客户预设额度类型：
			// attrString = variable.getAttribute("AT5606").trim();
			// if (null==attrString || "-999".equals(attrString) ||
			// "".equals(attrString) || "null".equalsIgnoreCase(attrString)) {
			// AT5606 = "1";
			// }else{
			// if("1".equalsIgnoreCase(attrString)||"Y".equalsIgnoreCase(attrString)){
			// AT5606 = "1";
			// }else{
			// AT5606 = "2";
			// }
			// }
			// tempVariable = new VariableBean("AT5606", AT5606, "N", "EX",
			// "客户预设额度类型");
			// attachVariableA.add(tempVariable);

			String AT5607 = "";// 所属分行临增上限：
			attrString = variable.getAttribute("AT5607").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT5607 = "0";
			} else {
				AT5607 = attrString;
			}
			tempVariable = new VariableBean("AT5607", AT5607, "N", "EX",
					"所属分行临增上限");
			attachVariableA.add(tempVariable);

			String AT5608 = "";// 所属分行长增上限：
			attrString = variable.getAttribute("AT5608").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT5608 = "0";
			} else {
				AT5608 = attrString;
			}
			tempVariable = new VariableBean("AT5608", AT5608, "N", "EX",
					"所属分行长增上限");
			attachVariableA.add(tempVariable);

			String AT5609 = "";// 公司组织机构代码号：
			attrString = variable.getAttribute("AT5609").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT5609 = "0";
			} else {
				AT5609 = attrString;
			}
			tempVariable = new VariableBean("AT5609", AT5609, "N", "EX",
					"公司组织机构代码号");
			attachVariableA.add(tempVariable);

			String AT5610 = "";// 公务卡临增上限：
			attrString = variable.getAttribute("AT5610").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT5610 = "0";
			} else {
				AT5610 = attrString;
			}
			tempVariable = new VariableBean("AT5610", AT5610, "N", "EX",
					"公务卡临增上限");
			attachVariableA.add(tempVariable);

			String AT5611 = "";// 公务卡长增上限：
			attrString = variable.getAttribute("AT5611").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT5611 = "0";
			} else {
				AT5611 = attrString;
			}
			tempVariable = new VariableBean("AT5611", AT5611, "N", "EX",
					"公务卡长增上限");
			attachVariableA.add(tempVariable);

			String AT5620 = "";// 产品大类：
			attrString = variable.getAttribute("AT5620").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT5620 = "1"; // 个人
			} else {
				if ("1".equalsIgnoreCase(attrString)) {
					AT5620 = "1"; // 个人
				} else if ("2".equalsIgnoreCase(attrString)) {
					AT5620 = "2"; // 高端
				} else if ("3".equalsIgnoreCase(attrString)) {
					AT5620 = "3"; // 公务卡
				}

			}
			tempVariable = new VariableBean("AT5620", AT5620, "N", "EX", "产品大类");
			attachVariableA.add(tempVariable);

			String AT5630 = "";// 期望额度：
			attrString = variable.getAttribute("AT5630").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT5630 = "0";
			} else {
				AT5630 = attrString;
				if (!"1".equalsIgnoreCase(AT4750)) {
					Double A4760 = Double.parseDouble(AT4760);
					Double A5630 = Double.parseDouble(AT5630) * A4760;
					AT5630 = A5630.toString();
				}
			}
			tempVariable = new VariableBean("AT5630", AT5630, "N", "EX", "期望额度");
			attachVariableA.add(tempVariable);

			String AT5640 = "";// 是否接受低于期望额度：
			attrString = variable.getAttribute("AT5640").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT5640 = "-999";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT5640 = "1";
				} else {
					AT5640 = "2";
				}
			}
			tempVariable = new VariableBean("AT5640", AT5640, "N", "EX",
					"是否接受低于期望额度");
			attachVariableA.add(tempVariable);

			// String AT6000 = "";//是否白金卡：
			// attrString = variable.getAttribute("AT6000").trim();
			// if (null==attrString || "-999".equals(attrString) ||
			// "".equals(attrString) || "null".equalsIgnoreCase(attrString)) {
			// AT6000 = "1";
			// }else{
			// if("1".equalsIgnoreCase(attrString)){
			// AT6000 = "1";
			// }else{
			// AT6000 = "2";
			// }
			// }
			// tempVariable = new VariableBean("AT6000", AT6000, "N", "EX",
			// "是否白金卡");
			// attachVariableA.add(tempVariable);
			//
			// String AT6010 = "";//是否为美运卡：
			// attrString = variable.getAttribute("AT6010").trim();
			// if (null==attrString || "-999".equals(attrString) ||
			// "".equals(attrString) || "null".equalsIgnoreCase(attrString)) {
			// AT6010 = "1";
			// }else{
			// if("1".equalsIgnoreCase(attrString)){
			// AT6010 = "1";
			// }else{
			// AT6010 = "2";
			// }
			// }
			// tempVariable = new VariableBean("AT6010", AT6010, "N", "EX",
			// "是否为美运卡");
			// attachVariableA.add(tempVariable);

			String AT6020 = "";// 客户所有名下有效卡的最高永久额度：
			attrString = variable.getAttribute("AT6020").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT6020 = "0";
			} else {
				AT6020 = attrString;
				// if(!"1".equalsIgnoreCase(AT4750)){
				// Double A4760 = Double.parseDouble(AT4760);
				// Double A6020 = Double.parseDouble(AT6020)*A4760;
				// AT6020 = A6020.toString();
				// }
			}
			tempVariable = new VariableBean("AT6020", AT6020, "N", "EX",
					"客户所有名下有效卡的最高永久额度");
			attachVariableA.add(tempVariable);

			String AT6030 = "";// 工单卡片子级CC01当前永久额度：
			attrString = variable.getAttribute("AT6030").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT6030 = "0";
			} else {
				AT6030 = attrString;
				// if(!"1".equalsIgnoreCase(AT4750)){
				// Double A4760 = Double.parseDouble(AT4760);
				// Double A6030 = Double.parseDouble(AT6030)*A4760;
				// AT6030 = A6030.toString();
				// }
			}
			tempVariable = new VariableBean("AT6030", AT6030, "N", "EX",
					"工单卡片子级CC01当前永久额度");
			attachVariableA.add(tempVariable);

			String AT6040 = "";// 是否跨境交易客户：
			attrString = variable.getAttribute("AT6040").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT6040 = "1";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT6040 = "1";
				} else {
					AT6040 = "2";
				}
			}
			tempVariable = new VariableBean("AT6040", AT6040, "N", "EX",
					"是否跨境交易客户");
			attachVariableA.add(tempVariable);

			String AT6050 = "";// 预设额度更新渠道：
			attrString = variable.getAttribute("AT6050").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT6050 = "0";
			} else {
				AT6050 = attrString;
			}
			tempVariable = new VariableBean("AT6050", AT6050, "N", "EX",
					"预设额度更新渠道");
			attachVariableA.add(tempVariable);

			String AT5650 = "";// 年龄 长增、临增 ：
			int Age5650 = 0;
			if (null == AT4451 || "-999".equals(AT4451) || "".equals(AT4451)
					|| "null".equalsIgnoreCase(AT4451) || "0".equals(AT4451)) {
				Age5650 = this.getAge(AT4460);
				AT5650 = Integer.toString(Age5650);
			} else {
				Age5650 = this.getAge(AT4451.substring(6, 14));
				AT5650 = Integer.toString(Age5650);
			}
			tempVariable = new VariableBean("AT5650", AT5650, "N", "EX", "年龄");
			attachVariableA.add(tempVariable);

			String AT4721 = "";// 根据状态判断账户状态:1正常2欺诈3坏账4核销5逾期6未激活
			String AT6060 = "";// 账户状态是否为欺诈
			String AT6070 = "";// 账户状态是否为坏账
			String AT6080 = "";// 账户状态是否为核销
			String AT6090 = "";// 账户状态是否为逾期
			String AT9003 = "";// 账户状态是否为未激活
			attrString = variable.getAttribute("AT4721").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				// AT4721 = "1"; //需确认
				AT6060 = "2";
				AT6070 = "2";
				AT6080 = "2";
				AT6090 = "2";
				AT9003 = "2";
			} else {
				AT4721 = attrString;
				if (AT4721.contains("，")) {
					AT4721 = AT4721.replace("，", ",");
				}
				if (AT4721.contains(",")) {
					String A4[] = AT4721.split(",");
					for (Integer a = 0; a < A4.length; a++) {
						if ("ATPD".equalsIgnoreCase(A4[a])) {
							AT9003 = "1";// 账户状态是否为未激活
						} else if ("FRCC".equalsIgnoreCase(A4[a])) {
							AT6060 = "1";// 账户状态是否为欺诈
						} else if ("FRAU".equalsIgnoreCase(A4[a])) {
							AT6060 = "1";// 账户状态是否为欺诈
						} else if ("INBL".equalsIgnoreCase(A4[a])) {
							AT6060 = "1";// 账户状态是否为欺诈
						} else if ("COCR".equalsIgnoreCase(A4[a])) {
							AT6070 = "1";// 账户状态是否为坏账
						} else if ("DQCO".equalsIgnoreCase(A4[a])) {
							AT6070 = "1";// 账户状态是否为坏账
						} else if ("DQWO".equalsIgnoreCase(A4[a])) {
							// AT6070 = "1";//账户状态是否为坏账
							AT6080 = "1";// 坏账转核销
						} else if ("SBWO".equalsIgnoreCase(A4[a])) {
							// AT6070 = "1";//账户状态是否为坏账
							AT6080 = "1";// 坏账转核销
						} else if ("CAWO".equalsIgnoreCase(A4[a])) {
							AT6080 = "1";// 账户状态是否为核销
						} else if ("CRAC".equalsIgnoreCase(A4[a])) {
							AT6080 = "1";// 账户状态是否为核销
						} else if ("DQPD".equalsIgnoreCase(A4[a])) {
							AT6090 = "1";// 账户状态是否为逾期
						} else if (A4[a].contains("DQC")
								|| A4[a].contains("dqc")
								&& !A4[a].equalsIgnoreCase("DQCO")) {
							AT6090 = "1";
						} else if ("DQ10".equalsIgnoreCase(A4[a])) {
							AT6090 = "1";
						} else if ("DQ11".equalsIgnoreCase(A4[a])) {
							AT6090 = "1";
						} else if ("DQ12".equalsIgnoreCase(A4[a])) {
							AT6090 = "1";
						} else if (A4[a].contains("OLC")
								|| A4[a].contains("olc")) {
							AT6090 = "1";
						} else if ("OC10".equalsIgnoreCase(A4[a])) {
							AT6090 = "1";
						} else if ("OC11".equalsIgnoreCase(A4[a])) {
							AT6090 = "1";
						} else if ("OC12".equalsIgnoreCase(A4[a])) {
							AT6090 = "1";
						} else if ("OLPD".equalsIgnoreCase(A4[a])) {
							AT6090 = "1";
						} else if ("D540".equalsIgnoreCase(A4[a])) {
							AT6090 = "1";
						} else if ("D720".equalsIgnoreCase(A4[a])) {
							AT6090 = "1";
						} else if ("O540".equalsIgnoreCase(A4[a])) {
							AT6090 = "1";
						} else if ("O720".equalsIgnoreCase(A4[a])) {
							AT6090 = "1";
						}

					}
				} else {
					if ("ATPD".equalsIgnoreCase(AT4721)) {
						AT9003 = "1";
					} else if ("FRCC".equalsIgnoreCase(AT4721)) {
						AT6060 = "1";
					} else if ("FRAU".equalsIgnoreCase(AT4721)) {
						AT6060 = "1";
					} else if ("INBL".equalsIgnoreCase(AT4721)) {
						AT6060 = "1";
					} else if ("COCR".equalsIgnoreCase(AT4721)) {
						AT6070 = "1";
					} else if ("DQCO".equalsIgnoreCase(AT4721)) {
						AT6070 = "1";
					} else if ("DQWO".equalsIgnoreCase(AT4721)) {
						// AT6070 = "1";//账户状态是否为坏账
						AT6080 = "1";// 坏账转核销
					} else if ("SBWO".equalsIgnoreCase(AT4721)) {
						// AT6070 = "1";//账户状态是否为坏账
						AT6080 = "1";// 坏账转核销
					} else if ("CAWO".equalsIgnoreCase(AT4721)) {
						AT6080 = "1";
					} else if ("CRAC".equalsIgnoreCase(AT4721)) {
						AT6080 = "1";
					} else if ("DQPD".equalsIgnoreCase(AT4721)) {
						AT6090 = "1";
					} else if (AT4721.contains("DQC") || AT4721.contains("dqc")
							&& !AT4721.equalsIgnoreCase("DQCO")) {
						AT6090 = "1";
					} else if ("DQ10".equalsIgnoreCase(AT4721)) {
						AT6090 = "1";
					} else if ("DQ11".equalsIgnoreCase(AT4721)) {
						AT6090 = "1";
					} else if ("DQ12".equalsIgnoreCase(AT4721)) {
						AT6090 = "1";
					} else if (AT4721.contains("OLC") || AT4721.contains("olc")) {
						AT6090 = "1";
					} else if ("OC10".equalsIgnoreCase(AT4721)) {
						AT6090 = "1";
					} else if ("OC11".equalsIgnoreCase(AT4721)) {
						AT6090 = "1";
					} else if ("OC12".equalsIgnoreCase(AT4721)) {
						AT6090 = "1";
					} else if ("OLPD".equalsIgnoreCase(AT4721)) {
						AT6090 = "1";
					} else if ("D540".equalsIgnoreCase(AT4721)) {
						AT6090 = "1";
					} else if ("D720".equalsIgnoreCase(AT4721)) {
						AT6090 = "1";
					} else if ("O540".equalsIgnoreCase(AT4721)) {
						AT6090 = "1";
					} else if ("O720".equalsIgnoreCase(AT4721)) {
						AT6090 = "1";
					}
				}
			}
			tempVariable = new VariableBean("AT6060", AT6060, "N", "EX",
					"账户状态是否为欺诈");
			attachVariableA.add(tempVariable);
			tempVariable = new VariableBean("AT6070", AT6070, "N", "EX",
					"账户状态是否为坏账");
			attachVariableA.add(tempVariable);
			tempVariable = new VariableBean("AT6080", AT6080, "N", "EX",
					"账户状态是否为核销");
			attachVariableA.add(tempVariable);
			tempVariable = new VariableBean("AT6090", AT6090, "N", "EX",
					"账户状态是否为逾期");
			attachVariableA.add(tempVariable);
			tempVariable = new VariableBean("AT9003", AT9003, "N", "EX",
					"账户状态是否为未激活");
			attachVariableA.add(tempVariable);

			String AT4490 = "";// 全部产品层账户开立日期：
			attrString = variable.getAttribute("AT4490").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4490 = "0";
			} else {
				AT4490 = attrString;
				SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
				Date nowDate = new Date();
				String nowD = sf.format(nowDate);
				Long to = null;
				Long from = null;
				Long riqi = null;
				// System.out.println("转换前"+AT4490);
				if (AT4490.contains("，")) {
					AT4490 = AT4490.replace("，", ",");
				}
				// System.out.println("转换后"+AT4490);
				if (AT4490.contains(",")) {
					String A4[] = AT4490.split(",");
					Integer min = 0;
					Integer max = 0;
					for (Integer i = 0; i < A4.length; i++) {
						if (i + 1 == A4.length) {
							break;
						}
						if (i == 0) {
							min = Integer.parseInt(A4[0].toString());
						}
						max = Integer.parseInt(A4[i + 1].toString());
						if (min >= max) {
							min = max;
						}
					}
					to = sf.parse(nowD).getTime();
					from = sf.parse(min.toString()).getTime();

					riqi = (to - from) / (1000 * 60 * 60 * 24);
					AT4490 = riqi.toString();
				} else {
					to = sf.parse(nowD).getTime();
					from = sf.parse(AT4490).getTime();

					riqi = (to - from) / (1000 * 60 * 60 * 24);
					AT4490 = riqi.toString();
				}

			}
			tempVariable = new VariableBean("AT4490", AT4490, "N", "EX",
					"全部产品层账户开立日期");
			attachVariableA.add(tempVariable);
			String AT4723 = "";// 客户是否要最大额度：
			attrString = variable.getAttribute("AT4723").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4723 = "2";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT4723 = "1";
				} else {
					AT4723 = "2";
				}
			}
			tempVariable = new VariableBean("AT4723", AT4723, "N", "EX",
					"客户是否要最大额度");
			attachVariableA.add(tempVariable);
			/**
			 * 需求：网络渠道申请策略 变量
			 * */
			/**
			 * 0 分行录入 1 网申 2 客户经理移动终端 3 柜台老客户 4 手机（微信） 5 网银 6 缤纷生活 7 行内平台（OCRM）
			 * 8 客服 9 自助终端 10 上海分行集中平台录入 F 全流程审批 N 老预制卡 O 老aps P 批量非预制卡 Q 批量预制卡
			 * X 区域中心 Y 华道 Z 综合开户
			 * **/
			String AT20001 = "";// 渠道标识：
			attrString = variable.getAttribute("AT20001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20001 = "-999";
			} else {
				AT20001 = attrString;
				if ("Y".equalsIgnoreCase(attrString)) {
					AT20001 = "11";//Y 商务公司录入
				} else if ("N".equalsIgnoreCase(attrString)) {
					AT20001 = "12";//F 全流程审批进件
				}else if ("Z".equalsIgnoreCase(attrString)) {
					AT20001 = "13";//Z 综合开户
				}else if ("Q".equalsIgnoreCase(attrString)) {
					AT20001 = "14";//Q 批量预制卡（新）
				}else if ("O".equalsIgnoreCase(attrString)) {
					AT20001 = "15";//O 老aps进件
				}else if ("F".equalsIgnoreCase(attrString)) {
					AT20001 = "16";//F 全流程审批进件
				}else if ("A".equalsIgnoreCase(attrString)) {
					AT20001 = "17";//A 移动终端(APP端)
				}else if ("0".equalsIgnoreCase(attrString)) {
					AT20001 = "0";//0 分行录入
				}else if ("1".equalsIgnoreCase(attrString)) {
					AT20001 = "1";//1 网上申请
				}else if ("2".equalsIgnoreCase(attrString)) {
					AT20001 = "2";//2 移动终端网页版
				}else if ("3".equalsIgnoreCase(attrString)) {
					AT20001 = "3";//3 柜台老客户
				}else if ("4".equalsIgnoreCase(attrString)) {
					AT20001 = "4";//4 手机
				}else if ("5".equalsIgnoreCase(attrString)) {
					AT20001 = "5";//5 网银理财版
				}else if ("6".equalsIgnoreCase(attrString)) {
					AT20001 = "6";//6 缤纷生活
				}else if ("7".equalsIgnoreCase(attrString)) {
					AT20001 = "7";//7 行内平台（OCRM）
				}else if ("8".equalsIgnoreCase(attrString)) {
					AT20001 = "8";//8 客服
				}else if ("9".equalsIgnoreCase(attrString)) {
					AT20001 = "9";//9 网银非理财版
				}else{
					AT20001 = "-777";//不符合接口规范
				}

			}
			tempVariable = new VariableBean("AT20001", AT20001, "N", "EX",
					"渠道标识");
			attachVariableA.add(tempVariable);

			String AT20002 = "";// 学历：
			attrString = variable.getAttribute("AT20002").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20002 = "-999";
			} else {
				if("-888".equals(attrString)){
					AT20002 = "-888";
				}else if(attrString.contains("博士")){
					AT20002 = "1";//博士研究生
				}else if(attrString.contains("硕士")){
					AT20002 = "2";//硕士研究生
				}else if(attrString.contains("研究生班")){
					AT20002 = "3";//研究生班
				}else if(attrString.contains("第二学士")){
					AT20002 = "4";//第二学士学位
				}else if(attrString.contains("第二本科")){
					AT20002 = "7";//第二本科
				}else if(attrString.contains("本科")){
					AT20002 = "5";//本科
				}else if(attrString.contains("高升本")){
					AT20002 = "6";//高升本
				}else if(attrString.contains("高职")){
					AT20002 = "9";//专科(高职)
				}else if(attrString.contains("第二专科")){
					AT20002 = "10";//第二专科
				}else if(attrString.contains("专科")){
					AT20002 = "8";//专科
				}else if(attrString.contains("夜大")){
					AT20002 = "11";//夜大
				}else if(attrString.contains("大学")){
					AT20002 = "12";//大学
				}else{
					AT20002 = "-888";
				}
				//AT20002 = attrString;
			}
			tempVariable = new VariableBean("AT20002", AT20002, "N", "EX", "学历");
			System.out.println("AT20002:"+AT20002);
			attachVariableA.add(tempVariable);
			
			String AT20018 = "";// 学历：
			attrString = variable.getAttribute("AT20018").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20018 = "-999";
			} else {
				if("-888".equals(attrString)){
					AT20018 = "-888";
				}else if(attrString.contains("高级技师") || attrString.contains("1级")){
					AT20018 = "5";//高级技师（国家职业资格一级）
				}else if(attrString.contains("初级") || attrString.contains("5级")){
					AT20018 = "1";//初级（国家职业资格五级）
				}else if(attrString.contains("中级") || attrString.contains("4级")){
					AT20018 = "2";//中级（国家职业资格四级）
				}else if(attrString.contains("高级") || attrString.contains("3级")){
					AT20018 = "3";//高级（国家职业资格三级）
				}else if(attrString.contains("技师") || attrString.contains("2级")){
					AT20018 = "4";//技师（国家职业资格二级）
				}else{
					AT20018 = "-888";
				}
				//AT20018 = attrString;
			}
			System.out.println("AT20018:"+AT20018);
			tempVariable = new VariableBean("AT20018", AT20018, "N", "EX", "学历");
			attachVariableA.add(tempVariable);
			
			String AT20004 = "";// 户籍与工作地域是否一致：
			attrString = variable.getAttribute("AT20004").trim();
			if (null == attrString || "".equals(attrString) ||"-999".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20004 = "2";
			}else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT20004 = "1";
				} else {
					AT20004 = "2";
				}
			}
			tempVariable = new VariableBean("AT20004", AT20004, "N", "EX",
					"户籍与工作地域是否一致");
			attachVariableA.add(tempVariable);

			String AT20005 = "";// 是否为信用卡老客户：
			attrString = variable.getAttribute("AT20005").trim();
			if (null == attrString || "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20005 = "2";
			} else if("-999".equals(attrString)){
				AT20005 = "-999";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT20005 = "1";
				} else {
					AT20005 = "2";
				}
			}
			tempVariable = new VariableBean("AT20005", AT20005, "N", "EX",
					"是否为信用卡老客户");
			attachVariableA.add(tempVariable);

			String AT20006 = "";// 信用卡老客户额度：
			attrString = variable.getAttribute("AT20006").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20006 = "0";
			} else {
				AT20006 = attrString;
			}
			tempVariable = new VariableBean("AT20006", AT20006, "N", "EX",
					"信用卡老客户额度");
			attachVariableA.add(tempVariable);

			String AT20007 = "";// 是否白名单：
			//针对C1507封版前，2015-11-17 16:24邮件确认修改为A04009
			attrString = variable.getAttribute("A04009").trim();
			if (null == attrString || "".equals(attrString) ||"-999".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20007 = "2";
			}else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT20007 = "1";
				} else {
					AT20007 = "2";
				}
			}
			tempVariable = new VariableBean("AT20007", AT20007, "N", "EX",
					"是否白名单");
			attachVariableA.add(tempVariable);

			String AT20008 = "";// 白名单额度：
			//针对C1507封版前，2015-11-17 16:24邮件确认修改为A04010
			attrString = variable.getAttribute("A04010").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20008 = "0";
			} else {
				AT20008 = attrString;
			}
			tempVariable = new VariableBean("AT20008", AT20008, "N", "EX",
					"白名单额度");
			attachVariableA.add(tempVariable);

			String AT20009 = "";// 反申请欺诈评级：
			attrString = variable.getAttribute("AT20009").trim();
			if (null == attrString || "".equals(attrString) ||"-999".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20009 = "-999";
			}else {
				AT20009 = attrString;
				if ("A".equalsIgnoreCase(attrString)) {
					AT20009 = "1";
				} else if ("A+".equalsIgnoreCase(attrString)) {
					AT20009 = "2";
				} else if ("D+".equalsIgnoreCase(attrString)) {
					AT20009 = "3";
				} else if ("D".equalsIgnoreCase(attrString)) {
					AT20009 = "4";
				} else if ("D-".equalsIgnoreCase(attrString)) {
					AT20009 = "5";
				} else if ("E".equalsIgnoreCase(attrString)) {
					AT20009 = "6";
				} else if ("F".equalsIgnoreCase(attrString)) {
					AT20009 = "7";
				} else if ("G".equalsIgnoreCase(attrString)) {
					AT20009 = "8";
				} else if ("0".equalsIgnoreCase(attrString)) {//0当作好人处理
					AT20009 = "0";
				}else {
					AT20009 = "-999";
				}

			}
			tempVariable = new VariableBean("AT20009", AT20009, "N", "EX",
					"反申请欺诈评级");
			attachVariableA.add(tempVariable);

			String AT20010 = "";// 公安是否返回：
			attrString = variable.getAttribute("AT20010").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20010 = "2";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT20010 = "1";
				} else if ("-888".equalsIgnoreCase(attrString)) {
					AT20010 = "1";
				}else {
					AT20010 = "2";
				}
			}
			tempVariable = new VariableBean("AT20010", AT20010, "N", "EX",
					"公安是否返回");
			attachVariableA.add(tempVariable);
			
			String AT20012 = "";// 白名单所属机构：
			//针对C1507封版前，2015-11-17 16:24邮件确认修改为A04011
			attrString = variable.getAttribute("A04011").trim();
			if (null == attrString || "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20012 = "0";
			} else {
				AT20012 = attrString;
			}
			tempVariable = new VariableBean("AT20012", AT20012, "N", "EX",
					"白名单所属机构");
			attachVariableA.add(tempVariable);
			
			String AT20014 = "";// 是否行内房贷客户：
			//针对C1507封版前，2015-11-17 16:24邮件确认修改为A04006
			attrString = variable.getAttribute("A04006").trim();
			if (null == attrString || "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20014 = "2";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT20014 = "1";
				} else if ("2".equalsIgnoreCase(attrString)
						|| "N".equalsIgnoreCase(attrString)) {
					AT20014 = "2";
				}else{
					AT20014 = "2";
				}
			}
			tempVariable = new VariableBean("AT20014", AT20014, "N", "EX",
					"是否行内房贷客户");
			attachVariableA.add(tempVariable);
			
			String AT20015 = "";// 最长正常还款周期长度（天）：
			//针对C1507封版前，2015-11-17 16:24邮件确认修改为A04007
			attrString = variable.getAttribute("A04007").trim();
			if (null == attrString || "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20015 = "0";
			} else {
				AT20015 = attrString;
			}
			tempVariable = new VariableBean("AT20015", AT20015, "N", "EX",
					"最长正常还款周期长度（天）");
			attachVariableA.add(tempVariable);
			String AT20016 = "";// 本月累计应还款额（元）：
			//针对C1507封版前，2015-11-17 16:24邮件确认修改为A04008
			attrString = variable.getAttribute("A04008").trim();
			if (null == attrString || "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20016 = "0";
			} else {
				AT20016 = attrString;
			}
			tempVariable = new VariableBean("AT20016", AT20016, "N", "EX",
					"本月累计应还款额（元）");
			attachVariableA.add(tempVariable);
			
			String AT20017 = "";// 任务类型标识：
			attrString = variable.getAttribute("AT20017").trim();
			if (null == attrString || "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20017 = "-999";
			} else {
				if ("01".equalsIgnoreCase(attrString)) {
					AT20017 = "1";//申请
				} else if ("02".equalsIgnoreCase(attrString)) {
					AT20017 = "2";//增额
				}else if ("03".equalsIgnoreCase(attrString)) {
					AT20017 = "3";//风险预警
				}else if ("04".equalsIgnoreCase(attrString)) {
					AT20017 = "4";//催收
				}else if ("05".equalsIgnoreCase(attrString)) {
					AT20017 = "5";//预设额度调整
				}else{
					AT20017 = "-999";
				}
			}
			tempVariable = new VariableBean("AT20017", AT20017, "N", "EX",
					"任务类型标识");
			attachVariableA.add(tempVariable);
			
			
			String AT20019 = "";// 决策请求原因类型：
			attrString = variable.getAttribute("AT20019").trim();
			if (null == attrString || "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20019 = "-999";
			} else {
				if ("0101".equalsIgnoreCase(attrString)) {
					AT20019 = "1";//一次决策
				} else if ("0102".equalsIgnoreCase(attrString)) {
					AT20019 = "2";//二次决策
				}else if ("0201".equalsIgnoreCase(attrString)) {
					AT20019 = "3";//0201增额，临增
				}else if ("0202".equalsIgnoreCase(attrString)) {
					AT20019 = "4";//0202增额，事件式临增:客户余额不足
				}else if ("0203".equalsIgnoreCase(attrString)) {
					AT20019 = "5";//0203增额，事件式临增:客户线下跨境首次交易
				}else if ("0204".equalsIgnoreCase(attrString)) {
					AT20019 = "6";//0204增额，事件式临增:客户当前额度使用率大于80%
				}else if ("0205".equalsIgnoreCase(attrString)) {
					AT20019 = "7";//0205增额，长增
				}else if ("0206".equalsIgnoreCase(attrString)) {
					AT20019 = "8";//0206增额，批量长增
				}else if ("0301".equalsIgnoreCase(attrString)) {
					AT20019 = "9";//0301风险预警
				}else{
					AT20019 = "-999";
				}
			}
			tempVariable = new VariableBean("AT20019", AT20019, "N", "EX",
					"决策请求原因类型");
			attachVariableA.add(tempVariable);
			
			String AT20020 = "";// 是否有学籍：
			attrString = variable.getAttribute("AT20020").trim();
			if (null == attrString || "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20020 = "-999";
			} else if ("-888".equalsIgnoreCase(attrString)) {
				AT20020 = "-888";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT20020 = "1";
				} else if ("2".equalsIgnoreCase(attrString)
						|| "N".equalsIgnoreCase(attrString)) {
					AT20020 = "2";
				} else {
					AT20020 = "-999";
				}
			}
			tempVariable = new VariableBean("AT20020", AT20020, "N", "EX",
					"是否有学籍");
			attachVariableA.add(tempVariable);
			
			String AT20021 = "";// 公安失信被执行人：
			attrString = variable.getAttribute("AT20021").trim();
			if (null == attrString || "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20021 = "-999";
			} else if ("-888".equalsIgnoreCase(attrString)) {
				AT20021 = "-888";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT20021 = "1";
				} else if ("2".equalsIgnoreCase(attrString)
						|| "N".equalsIgnoreCase(attrString)) {
					AT20021 = "2";
				} else {
					AT20021 = "-999";
				}
			}
			tempVariable = new VariableBean("AT20021", AT20021, "N", "EX",
					"公安失信被执行人");
			attachVariableA.add(tempVariable);
			
			String AT20022 = "";// 入学年份(enrolDate desc)
			attrString = variable.getAttribute("AT20022").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20022 = "0";
			} else {
				AT20022 = attrString;
			}
			tempVariable = new VariableBean("AT20022", AT20022, "N", "EX",
					"入学年份(enrolDate desc)");
			attachVariableA.add(tempVariable);
			
			String AT20023 = "";// 毕业时间(graduateTime desc)
			attrString = variable.getAttribute("AT20023").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20023 = "0";
			} else {
				AT20023 = attrString;
			}
			tempVariable = new VariableBean("AT20023", AT20023, "N", "EX",
					"毕业时间(graduateTime desc)");
			attachVariableA.add(tempVariable);
			
			String AT20024 = "";// 毕业年限(bynx desc)
			attrString = variable.getAttribute("AT20024").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20024 = "0";
			} else {
				AT20024 = attrString;
			}
			tempVariable = new VariableBean("AT20024", AT20024, "N", "EX",
					"毕业年限(bynx desc)");
			attachVariableA.add(tempVariable);
			
			String AT20025 = "";// 毕业结论(studyResult desc)
			attrString = variable.getAttribute("AT20025").trim();
			if (null == attrString || "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20025 = "-999";
			} else if ("-888".equalsIgnoreCase(attrString)) {
				AT20025 = "-888";
			} else {
				if (attrString.contains("毕业")) {
					AT20025 = "1";
				} else if (attrString.contains("结业")) {
					AT20025 = "2";
				} else if (attrString.contains("肄业")) {
					AT20025 = "3";
				}else {
					AT20025 = "-999";
				}
			}
			tempVariable = new VariableBean("AT20025", AT20025, "N", "EX",
					"毕业结论(studyResult desc)");
			attachVariableA.add(tempVariable);
			
			String AT20026 = "";// 学历类型D（dxllx desc）
			attrString = variable.getAttribute("AT20026").trim();
			if (null == attrString || "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20026 = "-999";
			} else if ("-888".equalsIgnoreCase(attrString)) {
				AT20026 = "-888";
			} else {
				if (attrString.contains("普通")) {
					AT20026 = "1";
				} else if (attrString.contains("研究生")) {
					AT20026 = "2";
				} else if (attrString.contains("成人")) {
					AT20026 = "3";
				} else if (attrString.contains("自考")) {
					AT20026 = "4";
				} else if (attrString.contains("网络")) {
					AT20026 = "5";//网络教育
				} else if (attrString.contains("开放")) {
					AT20026 = "6";//开放教育
				}else {
					AT20026 = "-999";
				}
			}
			tempVariable = new VariableBean("AT20026", AT20026, "N", "EX",
					"学历类型D(dxllx desc)");
			attachVariableA.add(tempVariable);
			
			String AT20028 = "";// 学校性质(公办、民办等)
			attrString = variable.getAttribute("AT20028").trim();
			if (null == attrString || "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20028 = "-999";
			} else if ("-888".equalsIgnoreCase(attrString)) {
				AT20028 = "-888";
			} else {
				if (attrString.contains("公办")) {
					AT20028 = "1";
				} else if (attrString.contains("民办")) {
					AT20028 = "2";
				}else {
					AT20028 = "-999";
				}
			}
			tempVariable = new VariableBean("AT20028", AT20028, "N", "EX",
					"学校性质(公办、民办等)");
			attachVariableA.add(tempVariable);
			String AT20027 = "";// 毕业院校类型(schoolType desc)
			attrString = variable.getAttribute("AT20027").trim();
			if (null == attrString || "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20027 = "-999";
			} else if ("-888".equalsIgnoreCase(attrString)) {
				AT20027 = "-888";
			} else {
				if ("1".equals(attrString)) {
					AT20027 = "1";
				} else if ("2".equals(attrString)) {
					AT20027 = "2";
				}else if ("3".equals(attrString)) {
					AT20027 = "3";
				}else {
					AT20027 = "-999";
				}
			}
			tempVariable = new VariableBean("AT20027", AT20027, "N", "EX",
					"毕业院校类型(schoolType desc)");
			attachVariableA.add(tempVariable);
			
			String AT20029 = "";// 学习形式D（dstudyStyle desc）
			attrString = variable.getAttribute("AT20029").trim();
			if (null == attrString || "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20029 = "-999";
			} else if ("-888".equalsIgnoreCase(attrString)) {
				AT20029 = "-888";
			} else {
				if (attrString.contains("普通")) {
					AT20029 = "1";//普通全日制
				} else if (attrString.contains("非全日")) {
					AT20029 = "3";//非全日制
				}else if (attrString.contains("全日")) {
					AT20029 = "2";//全日制
				}else if (attrString.contains("脱产")) {
					AT20029 = "4";
				}else if (attrString.contains("业余")) {
					AT20029 = "5";
				}else if (attrString.contains("在职")) {
					AT20029 = "6";
				}else if (attrString.contains("夜大学")) {
					AT20029 = "7";
				}else if (attrString.contains("网络")) {
					AT20029 = "8";//网络教育
				}else if (attrString.contains("函授")) {
					AT20029 = "9";
				}else if (attrString.contains("开放")) {
					AT20029 = "10";//开放教育
				}else if (attrString.contains("远程")) {
					AT20029 = "11";
				}else if (attrString.contains("电视")) {
					AT20029 = "12";
				}else if (attrString.contains("不详")) {
					AT20029 = "13";
				}else {
					AT20029 = "13";
				}
			}
			tempVariable = new VariableBean("AT20029", AT20029, "N", "EX",
					"学习形式D（dstudyStyle desc）");
			attachVariableA.add(tempVariable);
			
			String AT20030 = "";// 证书-领证日期
			attrString = variable.getAttribute("AT20030").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20030 = "0";
			} else {
				AT20030 = attrString;
			}
			tempVariable = new VariableBean("AT20030", AT20030, "N", "EX",
					"证书-领证日期");
			attachVariableA.add(tempVariable);
			// Document tempdocument = this.createDocument(attachVariable);
			// this.setDocument(tempdocument);
			this.setSupplData(supplData);

		} catch (CDSEException cdseex) {
			throw cdseex;

		} catch (Exception ex) {
			ex.printStackTrace();

			throw new CDSEException(errorCode);
		}
	}

	// ArrayList attachVariable = null;
	// String fldName = "";
	// String fldType = "";
	// String fldSrc = "";
	// String fldDsc = "";
	// String value = "";
	public void caculateAllAttributs_amount(Document appForm, Log log,
			String status) throws CDSEException {
		try {
			Document doc = appForm;

			Element basic = doc.getDocumentElement();
			Node data = basic.getFirstChild();
			Element variable = (Element) data.getFirstChild();
			// attachVariable = new ArrayList();
			attachVariableA = new ArrayList();

			VariableBean tempVariable = null;

			// 取得系统日期
			SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyyMMdd");
			Date date = new Date();
			cofirmingDate = bartDateFormat.format(date);
			String appId = variable.getAttribute("AppId").trim();

			// if ("".equals(appId)) {
			// this.errorCode = "RR050:申请号缺失或异常，交人工处理。";
			// throw new CDSEException(errorCode);
			// } else {
			this.ApplicationId = appId;

			// 申请产品编号
			String AT0040 = "-9";
			String attrString = variable.getAttribute("ProductCd").trim();
			String AT0041 = "-9";
			String AT0070 = "-9";
			String AT3110 = "-9";
			String AT4200 = "-9"; // 是否QCC申请
			String AT3120 = "0"; // 是否白金卡客户
			System.out.println("AT0070_0" + AT0070);
			/*
			 * if (this.verifyValue(attrString, valueArray)) {
			 *//**
			 * **********************************董春国 2007-1-26
			 * 修改(QCC卡)**************************
			 */
			/*
			 * // 是否QCC卡申请 // if (this.verifyValue(attrString, qccCardArray)) {
			 * // AT4200 = "1"; // this.isQccCardApp = true; // } else { //
			 * AT4200 = "0"; // }
			 *//**
			 * **********************************董春国 2007-1-26
			 * 修改(QCC卡)**************************
			 */
			/*
			 * 
			 * if (this.verifyValue(attrString, normalCardArray)) { AT0040 =
			 * "1"; } else if (this.verifyValue(attrString, oLikeCardArray)) {
			 * AT0040 = "2"; AT0070 = "1"; // 为奥运卡时,强制同意降级 this.isDownSell =
			 * true; } else if (this.verifyValue(attrString, jcbCardArray)) {
			 * AT0040 = "3"; } else if (this.verifyValue(attrString,
			 * dutyCardArray)) { AT0040 = "4"; AT0070 = "1"; // 为公务卡时,强制同意降级
			 * this.isDownSell = true; this.isDutyCardApp = true; } else if
			 * (this.verifyValue(attrString, platinaCardArray)) // 是否为白金卡申请 {
			 * AT3110 = "1";
			 * 
			 * } if (this.verifyValue(attrString, silverCardArray)) { AT0041 =
			 * "1"; } else if (this.verifyValue(attrString, goldenCardArray)) {
			 * AT0041 = "2"; } applProductCd = attrString; }
			 */
			// zhangshengtao 修改卡类别内容来自数据库
			applProductCd = attrString;
			String cards = null;
			cards = attrString;
			System.out.println("数据库读取卡种为：" + applProductCd);
			// if(1002==1002){
			// System.out.println("attrString:"+attrString);
			// System.out.println("数据库读取卡种为："+ProductCardInfoParameter.allProductCardMap);
			if (ProductCardInfoParameter.allProductCardMap
					.containsKey(attrString)) {
				System.out.println("卡种匹配成功");
				// ***** h*******************************董春国 2007-1-26
				// 修改(QCC卡)***************************//*
				// 是否QCC卡申请
				// if (this.verifyValue(attrString, qccCardArray)) {
				// AT4200 = "1";
				// this.isQccCardApp = true;
				// } else {
				// AT4200 = "0";
				// }
				// ************************************董春国 2007-1-26
				// 修改(QCC卡)***************************//*

				if (ProductCardInfoParameter.oLikeProductCardMap
						.containsKey(attrString)) {
					AT0040 = "2";
					AT0070 = "1"; // 为奥运卡时,强制同意降级
					// System.out.println("AT0070_2"+AT0070);
					this.isDownSell = true;
					// System.out.println("这里是奥运卡");
				} else if (ProductCardInfoParameter.jcbProductCardMap
						.containsKey(attrString)) {
					AT0040 = "3";
					// System.out.println("这里是jcb卡");
				} else if (ProductCardInfoParameter.dutyProductCardMap
						.containsKey(attrString)) {
					AT0040 = "4";
					AT0070 = "1"; // 为公务卡时,强制同意降级
					// System.out.println("AT0070_3"+AT0070);
					this.isDownSell = true;
					this.isDutyCardApp = true;
					// System.out.println("这里是公务卡");
				} else if (ProductCardInfoParameter.platinaProductCardMap
						.containsKey(attrString)) // 是否为白金卡申请
				{
					AT3110 = "1";
					System.out.println("AT3110_for_1: " + AT3110);
				} else {
					AT0040 = "1";
					// System.out.println("这里是非白金卡");
				}
				if (ProductCardInfoParameter.goldenProductCardMap
						.containsKey(attrString)) {
					AT0041 = "2";
					// System.out.println("这里是金卡");
				} else if (ProductCardInfoParameter.noDifferenceGoldSilverCardMap
						.containsKey(attrString)) {
					AT0041 = "2";
					AT0070 = "1"; // 为公务卡时,强制同意降级
					// System.out.println("AT0040_4"+AT0040);
					// System.out.println("AT0070_4"+AT0070);
					this.isDownSell = true;
					// System.out.println("这里是金卡强制降级");
				} else {
					AT0041 = "1";
					// System.out.println("这里是未匹配上的卡种");
				}
			} else {

				// StringBuffer strs = new StringBuffer();;
				// String str1 = null;
				// if("".equals(cards)||cards==null){
				// strs.append("读取卡种，未能找到：卡种未设置 传值卡种为：null");
				// // str1+="读取卡种，未能找到：卡种未设置 传值卡种为：null";
				// }
				// else{
				// System.out.println("111111111111"+cards);
				// strs.append("读取卡种，未能找到：卡种未设置 传值卡种为：");
				// strs.append(cards);
				// // str1+="读取卡种，未能找到：卡种未设置 传值卡种为："+cards;
				// }
				// if(ProductCardInfoParameter.allProductCardMap==null){
				// strs.append("数据库读取卡种为：null");
				// // str1+="数据库读取卡种为：null";
				// }
				// else{
				// strs.append("数据库读取卡种为：");
				// strs.append(ProductCardInfoParameter.allProductCardMap);
				// //
				// str1+="数据库读取卡种为："+ProductCardInfoParameter.allProductCardMap;
				// }
				// LogManager.getInstance().toCdsLog(strs.toString());
				//
				System.out.println("卡种匹配失败");
				// 不存在卡种的时候按普卡处理
				// AT0040="1";
				// AT0041 = "1";
				// System.out.println("1234");
				// this.errorCode=8888;
				System.out.println("!!!!!!!!!!!!!" + isSupplApplOnly);
				if (!isSupplApplOnly) {
					throw new CDSEException(8888);
				}

				// this.setWarningInfo("卡种未设置");

			}
			// 不写此变量，会一直报"政策项不符合入组"
			// 是否纯粹附属卡
			String AT0020 = "-9";
			attrString = variable.getAttribute("SupplApplOnly").trim();
			/**C1507封版后，统一改为判断1和Y*/
			if ("Y".equalsIgnoreCase(attrString) || "1".equalsIgnoreCase(attrString)) {
				AT0020 = "1";
				isSupplApplOnly = true;
			} else {
				AT0020 = "0";
				isSupplApplOnly = false;
			}
			tempVariable = new VariableBean("AT0020", AT0020, "N", "EX",
					"纯粹附属卡");
			attachVariableA.add(tempVariable);
			// 当前固定月收入
			String AT0700 = "-9";
			String mthlySalaryString = variable.getAttribute("MthlySalary")
					.trim();
			String annSalaryString = variable.getAttribute("AmmSalary").trim(); // ?AnnSalary
			double mthlySalaryDouble = 0.0f;
			double annSalaryDouble = 0.0f;
			if (isNumeric(mthlySalaryString)) {
				mthlySalaryDouble = toDouble(mthlySalaryString);
			}
			if (isNumeric(annSalaryString)) {
				annSalaryDouble = toDouble(annSalaryString);
			}
			if (annSalaryDouble > 0) {
				if (mthlySalaryDouble > 0) {
					AT0700 = String.valueOf(mthlySalaryDouble);
				} else {
					AT0700 = String.valueOf(annSalaryDouble / 12);
				}
			} else {
				if (mthlySalaryDouble > 0) {
					AT0700 = String.valueOf(mthlySalaryDouble);
				}
			}
			tempVariable = new VariableBean("AT0700", AT0700, "N", "EX",
					"当前工作月收入");
			attachVariableA.add(tempVariable);

			// 申请表年收入
			String AT0720 = "-999";
			attrString = variable.getAttribute("AT0710").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT0720 = "-999";
			} else {
				AT0720 = attrString;
			}
			tempVariable = new VariableBean("AT0720", AT0720, "N", "EX",
					"申请表年收入");
			attachVariableA.add(tempVariable);
			String A01001 = "";// 是否获取到征信报告：
			attrString = variable.getAttribute("A01001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A01001 = "-999";
			} else {
				/**C1507封版后，统一改为判断1和Y*/
				if ("Y".equalsIgnoreCase(attrString) || "1".equalsIgnoreCase(attrString)) {
					A01001 = "1";
				} else if ("N".equalsIgnoreCase(attrString) || "2".equalsIgnoreCase(attrString)) {
					A01001 = "2";
				} else if (attrString.indexOf("3") != -1) {
					A01001 = "3";
				} else {
					A01001 = "-999";
				}
			}
			tempVariable = new VariableBean("A01001", A01001, "N", "EX",
					"是否获取到征信报告");
			attachVariableA.add(tempVariable);

			// 变量缺失
			String AT6100 = "-999";
			if ("".equals(variable.getAttribute("AT5620").trim())
					|| null == variable.getAttribute("AT5620").trim()
					|| "null".equalsIgnoreCase(variable.getAttribute("AT5620")
							.trim())
					|| "-999".equals(variable.getAttribute("AT5620").trim())// 产品大类
					|| "".equals(variable.getAttribute("AT4420").trim())
					|| null == variable.getAttribute("AT4420").trim()
					|| "null".equalsIgnoreCase(variable.getAttribute("AT4420")
							.trim())
					|| "-999".equals(variable.getAttribute("AT4420").trim())) {// 清偿方式
				AT6100 = "1";
			}
			String liuyanghe = "2";
			String liuyang = "2";
			if ("2".equals(status) || "3".equals(status)) {
				if (("".equals(variable.getAttribute("AT4723").trim())
						|| null == variable.getAttribute("AT4723").trim()
						|| "null".equalsIgnoreCase(variable.getAttribute(
								"AT4723").trim()) || "-999".equals(variable
						.getAttribute("AT4723").trim()))
						&& ("".equals(variable.getAttribute("AT5630").trim())
								|| null == variable.getAttribute("AT5630")
										.trim()
								|| "null".equalsIgnoreCase(variable
										.getAttribute("AT5630").trim()) || "-999"
									.equals(variable.getAttribute("AT5630")
											.trim()))) {
					liuyanghe = "1";// AT4723客户是否要最大额度 与期望额度同时为空
				} else if ((("2".equals(variable.getAttribute("AT4723")) || "N"
						.equalsIgnoreCase(variable.getAttribute("AT4723")))
						&& (("".equals(variable.getAttribute("AT5630").trim())
								|| null == variable.getAttribute("AT5630")
										.trim()
								|| "null".equalsIgnoreCase(variable
										.getAttribute("AT5630").trim()) || "-999"
									.equals(variable.getAttribute("AT5630")
											.trim()))
								|| "".equals(variable.getAttribute("AT5640")
										.trim())
								|| null == variable.getAttribute("AT5640")
										.trim()
								|| "null".equalsIgnoreCase(variable
										.getAttribute("AT5640").trim()) || "-999"
									.equals(variable.getAttribute("AT5640")
											.trim())) && !"03".equals(variable
						.getAttribute("AT5602").trim()))

				// ||("02".equals(variable.getAttribute("AT5602").trim())//任务类型为CSR
				// && (("".equals(variable.getAttribute("AT5630").trim())
				// ||null==variable.getAttribute("AT5630").trim()
				// ||
				// "null".equalsIgnoreCase(variable.getAttribute("AT5630").trim())
				// || "-999".equals(variable.getAttribute("AT5630").trim()))))
				) {
					liuyanghe = "1";// 变量缺失：客户不要最大额度，期望额度不可以为空
				} else {
					liuyanghe = "2";
				}
			}
			if ("2".equals(status)) {
				if ("3".equals(variable.getAttribute("AT5620").trim())
						&& "2".equals(variable.getAttribute("AT4420").trim())) {
					System.out.println("产品大类"
							+ variable.getAttribute("AT5620").trim() + ",清偿方式"
							+ variable.getAttribute("AT5620").trim());
					if ("-999".equals(A01001)
							|| "".equals(variable.getAttribute("AT4410").trim())
							|| null == variable.getAttribute("AT4410").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4410").trim())
							|| "-999".equals(variable.getAttribute("AT4410")
									.trim())
							// ||
							// "".equals(variable.getAttribute("AT4421").trim())
							// ||null==variable.getAttribute("AT4421").trim()
							// ||"null".equalsIgnoreCase(variable.getAttribute("AT4421").trim())
							// ||
							// "-999".equals(variable.getAttribute("AT4421").trim())
							|| "".equals(variable.getAttribute("AT4430").trim())
							|| null == variable.getAttribute("AT4430").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4430").trim())
							|| "-999".equals(variable.getAttribute("AT4430")
									.trim())
							|| "".equals(variable.getAttribute("AT4440").trim())
							|| null == variable.getAttribute("AT4440").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4440").trim())
							|| "-999".equals(variable.getAttribute("AT4440")
									.trim())
							|| "".equals(variable.getAttribute("AT4460").trim())
							|| null == variable.getAttribute("AT4460").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4460").trim())
							|| "-999".equals(variable.getAttribute("AT4460")
									.trim())
							|| "".equals(variable.getAttribute("AT4530").trim())
							|| null == variable.getAttribute("AT4530").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4530").trim())
							|| "-999".equals(variable.getAttribute("AT4530")
									.trim())
							|| "".equals(variable.getAttribute("AT4550").trim())
							|| null == variable.getAttribute("AT4550").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4550").trim())
							|| "-999".equals(variable.getAttribute("AT4550")
									.trim())
							|| "".equals(variable.getAttribute("AT4590").trim())
							|| null == variable.getAttribute("AT4590").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4590").trim())
							|| "-999".equals(variable.getAttribute("AT4590")
									.trim())
							|| "".equals(variable.getAttribute("AT4600").trim())
							|| null == variable.getAttribute("AT4600").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4600").trim())
							|| "-999".equals(variable.getAttribute("AT4600")
									.trim())
							|| "".equals(variable.getAttribute("AT4610").trim())
							|| null == variable.getAttribute("AT4610").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4610").trim())
							|| "-999".equals(variable.getAttribute("AT4610")
									.trim())
							|| "".equals(variable.getAttribute("AT4630").trim())
							|| null == variable.getAttribute("AT4630").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4630").trim())
							|| "-999".equals(variable.getAttribute("AT4630")
									.trim())
							|| "".equals(variable.getAttribute("AT4640").trim())
							|| null == variable.getAttribute("AT4640").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4640").trim())
							|| "-999".equals(variable.getAttribute("AT4640")
									.trim())
							|| "".equals(variable.getAttribute("AT4650").trim())
							|| null == variable.getAttribute("AT4650").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4650").trim())
							|| "-999".equals(variable.getAttribute("AT4650")
									.trim())
							|| "".equals(variable.getAttribute("AT4660").trim())
							|| null == variable.getAttribute("AT4660").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4660").trim())
							|| "-999".equals(variable.getAttribute("AT4660")
									.trim())
							|| "".equals(variable.getAttribute("AT4670").trim())
							|| null == variable.getAttribute("AT4670").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4670").trim())
							|| "-999".equals(variable.getAttribute("AT4670")
									.trim())
							|| "".equals(variable.getAttribute("AT4680").trim())
							|| null == variable.getAttribute("AT4680").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4680").trim())
							|| "-999".equals(variable.getAttribute("AT4680")
									.trim())
							|| "".equals(variable.getAttribute("AT4690").trim())
							|| null == variable.getAttribute("AT4690").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4690").trim())
							|| "-999".equals(variable.getAttribute("AT4690")
									.trim())
							|| "".equals(variable.getAttribute("AT4700").trim())
							|| null == variable.getAttribute("AT4700").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4700").trim())
							|| "-999".equals(variable.getAttribute("AT4700")
									.trim())
							|| "".equals(variable.getAttribute("AT4710").trim())
							|| null == variable.getAttribute("AT4710").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4710").trim())
							|| "-999".equals(variable.getAttribute("AT4710")
									.trim())
							|| "".equals(variable.getAttribute("AT4720").trim())
							|| null == variable.getAttribute("AT4720").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4720").trim())
							|| "-999".equals(variable.getAttribute("AT4720")
									.trim())
							|| "".equals(variable.getAttribute("AT4721").trim())
							|| null == variable.getAttribute("AT4721").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4721").trim())
							|| "-999".equals(variable.getAttribute("AT4721")
									.trim())
							|| "".equals(variable.getAttribute("AT4724").trim())
							|| null == variable.getAttribute("AT4724").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4724").trim())
							|| "-999".equals(variable.getAttribute("AT4724")
									.trim())
							// ||
							// "".equals(variable.getAttribute("AT4730").trim())
							// ||null==variable.getAttribute("AT4730").trim()
							// ||"null".equalsIgnoreCase(variable.getAttribute("AT4730").trim())
							// ||
							// "-999".equals(variable.getAttribute("AT4730").trim())
							|| "".equals(variable.getAttribute("AT4740").trim())
							|| null == variable.getAttribute("AT4740").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4740").trim())
							|| "-999".equals(variable.getAttribute("AT4740")
									.trim())
							|| "".equals(variable.getAttribute("AT5601").trim())
							|| null == variable.getAttribute("AT5601").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT5601").trim())
							|| "-999".equals(variable.getAttribute("AT5601")
									.trim())
							|| "".equals(variable.getAttribute("AT5605").trim())
							|| null == variable.getAttribute("AT5605").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT5605").trim())
							|| "-999".equals(variable.getAttribute("AT5605")
									.trim())
							|| "".equals(variable.getAttribute("AT5607").trim())
							|| null == variable.getAttribute("AT5607").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT5607").trim())
							|| "-999".equals(variable.getAttribute("AT5607")
									.trim())
							// ||
							// "".equals(variable.getAttribute("AT5630").trim())
							// ||null==variable.getAttribute("AT5630").trim()
							// ||"null".equalsIgnoreCase(variable.getAttribute("AT5630").trim())
							// ||
							// "-999".equals(variable.getAttribute("AT5630").trim())//测试用
							// ||
							// "".equals(variable.getAttribute("AT5640").trim())
							// ||null==variable.getAttribute("AT5640").trim()
							// ||"null".equalsIgnoreCase(variable.getAttribute("AT5640").trim())
							// ||
							// "-999".equals(variable.getAttribute("AT5640").trim())
							|| "".equals(variable.getAttribute("AT6020").trim())
							|| null == variable.getAttribute("AT6020").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT6020").trim())
							|| "-999".equals(variable.getAttribute("AT6020")
									.trim())
							|| "".equals(variable.getAttribute("AT6030").trim())
							|| null == variable.getAttribute("AT6030").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT6030").trim())
							|| "-999".equals(variable.getAttribute("AT6030")
									.trim())
							|| "".equals(variable.getAttribute("AT6050").trim())
							|| null == variable.getAttribute("AT6050").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT6050").trim())
							|| "-999".equals(variable.getAttribute("AT6050")
									.trim())
							|| "".equals(variable.getAttribute("AT4450").trim())
							|| null == variable.getAttribute("AT4450").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4450").trim())
							|| "-999".equals(variable.getAttribute("AT4450")
									.trim())
							|| "".equals(variable.getAttribute("AT4750").trim())
							|| null == variable.getAttribute("AT4750").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4750").trim())
							|| "-999".equals(variable.getAttribute("AT4750")
									.trim())
							|| "".equals(variable.getAttribute("AT4760").trim())
							|| null == variable.getAttribute("AT4760").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4760").trim())
							|| "-999".equals(variable.getAttribute("AT4760")
									.trim())) {
						liuyang = "1";
					} else {
						liuyang = "2";
					}
				} else {
					System.out.println("产品大类"
							+ variable.getAttribute("AT5620").trim() + ",清偿方式"
							+ variable.getAttribute("AT5620").trim());
					if ("-999".equals(A01001)
							|| "".equals(variable.getAttribute("AT4410").trim())
							|| null == variable.getAttribute("AT4410").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4410").trim())
							|| "-999".equals(variable.getAttribute("AT4410")
									.trim())
							|| "".equals(variable.getAttribute("AT4430").trim())
							|| null == variable.getAttribute("AT4430").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4430").trim())
							|| "-999".equals(variable.getAttribute("AT4430")
									.trim())
							|| "".equals(variable.getAttribute("AT4440").trim())
							|| null == variable.getAttribute("AT4440").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4440").trim())
							|| "-999".equals(variable.getAttribute("AT4440")
									.trim())
							|| "".equals(variable.getAttribute("AT4460").trim())
							|| null == variable.getAttribute("AT4460").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4460").trim())
							|| "-999".equals(variable.getAttribute("AT4460")
									.trim())
							|| "".equals(variable.getAttribute("AT4530").trim())
							|| null == variable.getAttribute("AT4530").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4530").trim())
							|| "-999".equals(variable.getAttribute("AT4530")
									.trim())
							|| "".equals(variable.getAttribute("AT4550").trim())
							|| null == variable.getAttribute("AT4550").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4550").trim())
							|| "-999".equals(variable.getAttribute("AT4550")
									.trim())
							|| "".equals(variable.getAttribute("AT4590").trim())
							|| null == variable.getAttribute("AT4590").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4590").trim())
							|| "-999".equals(variable.getAttribute("AT4590")
									.trim())
							|| "".equals(variable.getAttribute("AT4600").trim())
							|| null == variable.getAttribute("AT4600").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4600").trim())
							|| "-999".equals(variable.getAttribute("AT4600")
									.trim())
							|| "".equals(variable.getAttribute("AT4610").trim())
							|| null == variable.getAttribute("AT4610").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4610").trim())
							|| "-999".equals(variable.getAttribute("AT4610")
									.trim())
							|| "".equals(variable.getAttribute("AT4630").trim())
							|| null == variable.getAttribute("AT4630").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4630").trim())
							|| "-999".equals(variable.getAttribute("AT4630")
									.trim())
							|| "".equals(variable.getAttribute("AT4640").trim())
							|| null == variable.getAttribute("AT4640").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4640").trim())
							|| "-999".equals(variable.getAttribute("AT4640")
									.trim())
							|| "".equals(variable.getAttribute("AT4650").trim())
							|| null == variable.getAttribute("AT4650").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4650").trim())
							|| "-999".equals(variable.getAttribute("AT4650")
									.trim())
							|| "".equals(variable.getAttribute("AT4660").trim())
							|| null == variable.getAttribute("AT4660").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4660").trim())
							|| "-999".equals(variable.getAttribute("AT4660")
									.trim())
							|| "".equals(variable.getAttribute("AT4670").trim())
							|| null == variable.getAttribute("AT4670").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4670").trim())
							|| "-999".equals(variable.getAttribute("AT4670")
									.trim())
							|| "".equals(variable.getAttribute("AT4680").trim())
							|| null == variable.getAttribute("AT4680").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4680").trim())
							|| "-999".equals(variable.getAttribute("AT4680")
									.trim())
							|| "".equals(variable.getAttribute("AT4690").trim())
							|| null == variable.getAttribute("AT4690").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4690").trim())
							|| "-999".equals(variable.getAttribute("AT4690")
									.trim())
							|| "".equals(variable.getAttribute("AT4700").trim())
							|| null == variable.getAttribute("AT4700").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4700").trim())
							|| "-999".equals(variable.getAttribute("AT4700")
									.trim())
							|| "".equals(variable.getAttribute("AT4710").trim())
							|| null == variable.getAttribute("AT4710").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4710").trim())
							|| "-999".equals(variable.getAttribute("AT4710")
									.trim())
							|| "".equals(variable.getAttribute("AT4720").trim())
							|| null == variable.getAttribute("AT4720").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4720").trim())
							|| "-999".equals(variable.getAttribute("AT4720")
									.trim())
							|| "".equals(variable.getAttribute("AT4721").trim())
							|| null == variable.getAttribute("AT4721").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4721").trim())
							|| "-999".equals(variable.getAttribute("AT4721")
									.trim())
							|| "".equals(variable.getAttribute("AT4724").trim())
							|| null == variable.getAttribute("AT4724").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4724").trim())
							|| "-999".equals(variable.getAttribute("AT4724")
									.trim())
							// ||
							// "".equals(variable.getAttribute("AT4730").trim())
							// ||null==variable.getAttribute("AT4730").trim()
							// ||"null".equalsIgnoreCase(variable.getAttribute("AT4730").trim())
							// ||
							// "-999".equals(variable.getAttribute("AT4730").trim())
							|| "".equals(variable.getAttribute("AT4740").trim())
							|| null == variable.getAttribute("AT4740").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4740").trim())
							|| "-999".equals(variable.getAttribute("AT4740")
									.trim())
							|| "".equals(variable.getAttribute("AT5601").trim())
							|| null == variable.getAttribute("AT5601").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT5601").trim())
							|| "-999".equals(variable.getAttribute("AT5601")
									.trim())
							|| "".equals(variable.getAttribute("AT5605").trim())
							|| null == variable.getAttribute("AT5605").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT5605").trim())
							|| "-999".equals(variable.getAttribute("AT5605")
									.trim())
							|| "".equals(variable.getAttribute("AT5607").trim())
							|| null == variable.getAttribute("AT5607").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT5607").trim())
							|| "-999".equals(variable.getAttribute("AT5607")
									.trim())
							// ||
							// "".equals(variable.getAttribute("AT5630").trim())
							// ||null==variable.getAttribute("AT5630").trim()
							// ||"null".equalsIgnoreCase(variable.getAttribute("AT5630").trim())
							// ||
							// "-999".equals(variable.getAttribute("AT5630").trim())
							// ||
							// "".equals(variable.getAttribute("AT5640").trim())
							// ||null==variable.getAttribute("AT5640").trim()
							// ||"null".equalsIgnoreCase(variable.getAttribute("AT5640").trim())
							// ||
							// "-999".equals(variable.getAttribute("AT5640").trim())
							|| "".equals(variable.getAttribute("AT6020").trim())
							|| null == variable.getAttribute("AT6020").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT6020").trim())
							|| "-999".equals(variable.getAttribute("AT6020")
									.trim())
							|| "".equals(variable.getAttribute("AT6030").trim())
							|| null == variable.getAttribute("AT6030").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT6030").trim())
							|| "-999".equals(variable.getAttribute("AT6030")
									.trim())
							|| "".equals(variable.getAttribute("AT6050").trim())
							|| null == variable.getAttribute("AT6050").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT6050").trim())
							|| "-999".equals(variable.getAttribute("AT6050")
									.trim())
							|| "".equals(variable.getAttribute("AT4450").trim())
							|| null == variable.getAttribute("AT4450").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4450").trim())
							|| "-999".equals(variable.getAttribute("AT4450")
									.trim())
							|| "".equals(variable.getAttribute("AT4750").trim())
							|| null == variable.getAttribute("AT4750").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4750").trim())
							|| "-999".equals(variable.getAttribute("AT4750")
									.trim())
							|| "".equals(variable.getAttribute("AT4760").trim())
							|| null == variable.getAttribute("AT4760").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4760").trim())
							|| "-999".equals(variable.getAttribute("AT4760")
									.trim())) {
						liuyang = "1";
					} else {
						liuyang = "2";
					}
				}
			} else if ("3".equals(status)) {
				if ("3".equals(variable.getAttribute("AT5620").trim())
						&& "2".equals(variable.getAttribute("AT4420").trim())) {
					System.out.println("产品大类"
							+ variable.getAttribute("AT5620").trim() + ",清偿方式"
							+ variable.getAttribute("AT5620").trim());
					if ("-999".equals(A01001)
							|| "".equals(variable.getAttribute("AT4410").trim())
							|| null == variable.getAttribute("AT4410").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4410").trim())
							|| "-999".equals(variable.getAttribute("AT4410")
									.trim())
							// ||
							// "".equals(variable.getAttribute("AT4422").trim())
							// ||null==variable.getAttribute("AT4422").trim()
							// ||"null".equalsIgnoreCase(variable.getAttribute("AT4422").trim())
							// ||
							// "-999".equals(variable.getAttribute("AT4422").trim())
							|| "".equals(variable.getAttribute("AT4430").trim())
							|| null == variable.getAttribute("AT4430").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4430").trim())
							|| "-999".equals(variable.getAttribute("AT4430")
									.trim())
							|| "".equals(variable.getAttribute("AT4440").trim())
							|| null == variable.getAttribute("AT4440").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4440").trim())
							|| "-999".equals(variable.getAttribute("AT4440")
									.trim())
							|| "".equals(variable.getAttribute("AT4460").trim())
							|| null == variable.getAttribute("AT4460").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4460").trim())
							|| "-999".equals(variable.getAttribute("AT4460")
									.trim())
							|| "".equals(variable.getAttribute("AT4470").trim())
							|| null == variable.getAttribute("AT4470").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4470").trim())
							|| "-999".equals(variable.getAttribute("AT4470")
									.trim())
							|| "".equals(variable.getAttribute("AT4490").trim())
							|| null == variable.getAttribute("AT4490").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4490").trim())
							|| "-999".equals(variable.getAttribute("AT4490")
									.trim())
							|| "".equals(variable.getAttribute("AT4550").trim())
							|| null == variable.getAttribute("AT4550").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4550").trim())
							|| "-999".equals(variable.getAttribute("AT4550")
									.trim())
							|| "".equals(variable.getAttribute("AT4590").trim())
							|| null == variable.getAttribute("AT4590").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4590").trim())
							|| "-999".equals(variable.getAttribute("AT4590")
									.trim())
							|| "".equals(variable.getAttribute("AT4600").trim())
							|| null == variable.getAttribute("AT4600").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4600").trim())
							|| "-999".equals(variable.getAttribute("AT4600")
									.trim())
							|| "".equals(variable.getAttribute("AT4610").trim())
							|| null == variable.getAttribute("AT4610").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4610").trim())
							|| "-999".equals(variable.getAttribute("AT4610")
									.trim())
							|| "".equals(variable.getAttribute("AT4630").trim())
							|| null == variable.getAttribute("AT4630").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4630").trim())
							|| "-999".equals(variable.getAttribute("AT4630")
									.trim())
							|| "".equals(variable.getAttribute("AT4640").trim())
							|| null == variable.getAttribute("AT4640").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4640").trim())
							|| "-999".equals(variable.getAttribute("AT4640")
									.trim())
							|| "".equals(variable.getAttribute("AT4650").trim())
							|| null == variable.getAttribute("AT4650").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4650").trim())
							|| "-999".equals(variable.getAttribute("AT4650")
									.trim())
							|| "".equals(variable.getAttribute("AT4660").trim())
							|| null == variable.getAttribute("AT4660").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4660").trim())
							|| "-999".equals(variable.getAttribute("AT4660")
									.trim())
							|| "".equals(variable.getAttribute("AT4670").trim())
							|| null == variable.getAttribute("AT4670").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4670").trim())
							|| "-999".equals(variable.getAttribute("AT4670")
									.trim())
							|| "".equals(variable.getAttribute("AT4680").trim())
							|| null == variable.getAttribute("AT4680").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4680").trim())
							|| "-999".equals(variable.getAttribute("AT4680")
									.trim())
							|| "".equals(variable.getAttribute("AT4690").trim())
							|| null == variable.getAttribute("AT4690").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4690").trim())
							|| "-999".equals(variable.getAttribute("AT4690")
									.trim())
							|| "".equals(variable.getAttribute("AT4700").trim())
							|| null == variable.getAttribute("AT4700").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4700").trim())
							|| "-999".equals(variable.getAttribute("AT4700")
									.trim())
							|| "".equals(variable.getAttribute("AT4710").trim())
							|| null == variable.getAttribute("AT4710").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4710").trim())
							|| "-999".equals(variable.getAttribute("AT4710")
									.trim())
							|| "".equals(variable.getAttribute("AT4720").trim())
							|| null == variable.getAttribute("AT4720").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4720").trim())
							|| "-999".equals(variable.getAttribute("AT4720")
									.trim())
							|| "".equals(variable.getAttribute("AT4721").trim())
							|| null == variable.getAttribute("AT4721").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4721").trim())
							|| "-999".equals(variable.getAttribute("AT4721")
									.trim())
							|| "".equals(variable.getAttribute("AT4724").trim())
							|| null == variable.getAttribute("AT4724").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4724").trim())
							|| "-999".equals(variable.getAttribute("AT4724")
									.trim())
							// ||
							// "".equals(variable.getAttribute("AT4730").trim())
							// ||null==variable.getAttribute("AT4730").trim()
							// ||"null".equalsIgnoreCase(variable.getAttribute("AT4730").trim())
							// ||
							// "-999".equals(variable.getAttribute("AT4730").trim())
							|| "".equals(variable.getAttribute("AT4740").trim())
							|| null == variable.getAttribute("AT4740").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4740").trim())
							|| "-999".equals(variable.getAttribute("AT4740")
									.trim())
							|| "".equals(variable.getAttribute("AT5601").trim())
							|| null == variable.getAttribute("AT5601").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT5601").trim())
							|| "-999".equals(variable.getAttribute("AT5601")
									.trim())
							|| "".equals(variable.getAttribute("AT5605").trim())
							|| null == variable.getAttribute("AT5605").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT5605").trim())
							|| "-999".equals(variable.getAttribute("AT5605")
									.trim())
							|| "".equals(variable.getAttribute("AT5608").trim())
							|| null == variable.getAttribute("AT5608").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT5608").trim())
							|| "-999".equals(variable.getAttribute("AT5608")
									.trim())
							// ||
							// "".equals(variable.getAttribute("AT5630").trim())
							// ||null==variable.getAttribute("AT5630").trim()
							// ||"null".equalsIgnoreCase(variable.getAttribute("AT5630").trim())
							// ||
							// "-999".equals(variable.getAttribute("AT5630").trim())
							// ||
							// "".equals(variable.getAttribute("AT5640").trim())
							// ||null==variable.getAttribute("AT5640").trim()
							// ||"null".equalsIgnoreCase(variable.getAttribute("AT5640").trim())
							// ||
							// "-999".equals(variable.getAttribute("AT5640").trim())
							|| "".equals(variable.getAttribute("AT6020").trim())
							|| null == variable.getAttribute("AT6020").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT6020").trim())
							|| "-999".equals(variable.getAttribute("AT6020")
									.trim())
							|| "".equals(variable.getAttribute("AT6030").trim())
							|| null == variable.getAttribute("AT6030").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT6030").trim())
							|| "-999".equals(variable.getAttribute("AT6030")
									.trim())
							|| "".equals(variable.getAttribute("AT6050").trim())
							|| null == variable.getAttribute("AT6050").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT6050").trim())
							|| "-999".equals(variable.getAttribute("AT6050")
									.trim())
							|| "".equals(variable.getAttribute("AT4450").trim())
							|| null == variable.getAttribute("AT4450").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4450").trim())
							|| "-999".equals(variable.getAttribute("AT4450")
									.trim())
							|| "".equals(variable.getAttribute("AT4750").trim())
							|| null == variable.getAttribute("AT4750").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4750").trim())
							|| "-999".equals(variable.getAttribute("AT4750")
									.trim())
							|| "".equals(variable.getAttribute("AT4760").trim())
							|| null == variable.getAttribute("AT4760").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4760").trim())
							|| "-999".equals(variable.getAttribute("AT4760")
									.trim())) {
						liuyang = "1";
					} else {
						liuyang = "2";
					}
				} else {
					System.out.println("产品大类"
							+ variable.getAttribute("AT5620").trim() + ",清偿方式"
							+ variable.getAttribute("AT5620").trim());
					if ("-999".equals(A01001)
							|| "".equals(variable.getAttribute("AT4410").trim())
							|| null == variable.getAttribute("AT4410").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4410").trim())
							|| "-999".equals(variable.getAttribute("AT4410")
									.trim())
							|| "".equals(variable.getAttribute("AT4430").trim())
							|| null == variable.getAttribute("AT4430").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4430").trim())
							|| "-999".equals(variable.getAttribute("AT4430")
									.trim())
							|| "".equals(variable.getAttribute("AT4440").trim())
							|| null == variable.getAttribute("AT4440").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4440").trim())
							|| "-999".equals(variable.getAttribute("AT4440")
									.trim())
							|| "".equals(variable.getAttribute("AT4460").trim())
							|| null == variable.getAttribute("AT4460").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4460").trim())
							|| "-999".equals(variable.getAttribute("AT4460")
									.trim())
							|| "".equals(variable.getAttribute("AT4470").trim())
							|| null == variable.getAttribute("AT4470").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4470").trim())
							|| "-999".equals(variable.getAttribute("AT4470")
									.trim())
							|| "".equals(variable.getAttribute("AT4490").trim())
							|| null == variable.getAttribute("AT4490").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4490").trim())
							|| "-999".equals(variable.getAttribute("AT4490")
									.trim())
							|| "".equals(variable.getAttribute("AT4550").trim())
							|| null == variable.getAttribute("AT4550").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4550").trim())
							|| "-999".equals(variable.getAttribute("AT4550")
									.trim())
							|| "".equals(variable.getAttribute("AT4590").trim())
							|| null == variable.getAttribute("AT4590").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4590").trim())
							|| "-999".equals(variable.getAttribute("AT4590")
									.trim())
							|| "".equals(variable.getAttribute("AT4600").trim())
							|| null == variable.getAttribute("AT4600").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4600").trim())
							|| "-999".equals(variable.getAttribute("AT4600")
									.trim())
							|| "".equals(variable.getAttribute("AT4610").trim())
							|| null == variable.getAttribute("AT4610").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4610").trim())
							|| "-999".equals(variable.getAttribute("AT4610")
									.trim())
							|| "".equals(variable.getAttribute("AT4630").trim())
							|| null == variable.getAttribute("AT4630").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4630").trim())
							|| "-999".equals(variable.getAttribute("AT4630")
									.trim())
							|| "".equals(variable.getAttribute("AT4640").trim())
							|| null == variable.getAttribute("AT4640").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4640").trim())
							|| "-999".equals(variable.getAttribute("AT4640")
									.trim())
							|| "".equals(variable.getAttribute("AT4650").trim())
							|| null == variable.getAttribute("AT4650").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4650").trim())
							|| "-999".equals(variable.getAttribute("AT4650")
									.trim())
							|| "".equals(variable.getAttribute("AT4660").trim())
							|| null == variable.getAttribute("AT4660").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4660").trim())
							|| "-999".equals(variable.getAttribute("AT4660")
									.trim())
							|| "".equals(variable.getAttribute("AT4670").trim())
							|| null == variable.getAttribute("AT4670").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4670").trim())
							|| "-999".equals(variable.getAttribute("AT4670")
									.trim())
							|| "".equals(variable.getAttribute("AT4680").trim())
							|| null == variable.getAttribute("AT4680").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4680").trim())
							|| "-999".equals(variable.getAttribute("AT4680")
									.trim())
							|| "".equals(variable.getAttribute("AT4690").trim())
							|| null == variable.getAttribute("AT4690").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4690").trim())
							|| "-999".equals(variable.getAttribute("AT4690")
									.trim())
							|| "".equals(variable.getAttribute("AT4700").trim())
							|| null == variable.getAttribute("AT4700").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4700").trim())
							|| "-999".equals(variable.getAttribute("AT4700")
									.trim())
							|| "".equals(variable.getAttribute("AT4710").trim())
							|| null == variable.getAttribute("AT4710").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4710").trim())
							|| "-999".equals(variable.getAttribute("AT4710")
									.trim())
							|| "".equals(variable.getAttribute("AT4720").trim())
							|| null == variable.getAttribute("AT4720").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4720").trim())
							|| "-999".equals(variable.getAttribute("AT4720")
									.trim())
							|| "".equals(variable.getAttribute("AT4721").trim())
							|| null == variable.getAttribute("AT4721").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4721").trim())
							|| "-999".equals(variable.getAttribute("AT4721")
									.trim())
							|| "".equals(variable.getAttribute("AT4724").trim())
							|| null == variable.getAttribute("AT4724").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4724").trim())
							|| "-999".equals(variable.getAttribute("AT4724")
									.trim())
							// ||
							// "".equals(variable.getAttribute("AT4730").trim())
							// ||null==variable.getAttribute("AT4730").trim()
							// ||"null".equalsIgnoreCase(variable.getAttribute("AT4730").trim())
							// ||
							// "-999".equals(variable.getAttribute("AT4730").trim())
							|| "".equals(variable.getAttribute("AT4740").trim())
							|| null == variable.getAttribute("AT4740").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4740").trim())
							|| "-999".equals(variable.getAttribute("AT4740")
									.trim())
							|| "".equals(variable.getAttribute("AT5601").trim())
							|| null == variable.getAttribute("AT5601").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT5601").trim())
							|| "-999".equals(variable.getAttribute("AT5601")
									.trim())
							|| "".equals(variable.getAttribute("AT5605").trim())
							|| null == variable.getAttribute("AT5605").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT5605").trim())
							|| "-999".equals(variable.getAttribute("AT5605")
									.trim())
							|| "".equals(variable.getAttribute("AT5608").trim())
							|| null == variable.getAttribute("AT5608").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT5608").trim())
							|| "-999".equals(variable.getAttribute("AT5608")
									.trim())
							// ||
							// "".equals(variable.getAttribute("AT5630").trim())
							// ||null==variable.getAttribute("AT5630").trim()
							// ||"null".equalsIgnoreCase(variable.getAttribute("AT5630").trim())
							// ||
							// "-999".equals(variable.getAttribute("AT5630").trim())
							// ||
							// "".equals(variable.getAttribute("AT5640").trim())
							// ||null==variable.getAttribute("AT5640").trim()
							// ||"null".equalsIgnoreCase(variable.getAttribute("AT5640").trim())
							// ||
							// "-999".equals(variable.getAttribute("AT5640").trim())
							|| "".equals(variable.getAttribute("AT6020").trim())
							|| null == variable.getAttribute("AT6020").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT6020").trim())
							|| "-999".equals(variable.getAttribute("AT6020")
									.trim())
							|| "".equals(variable.getAttribute("AT6030").trim())
							|| null == variable.getAttribute("AT6030").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT6030").trim())
							|| "-999".equals(variable.getAttribute("AT6030")
									.trim())
							|| "".equals(variable.getAttribute("AT6050").trim())
							|| null == variable.getAttribute("AT6050").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT6050").trim())
							|| "-999".equals(variable.getAttribute("AT6050")
									.trim())
							|| "".equals(variable.getAttribute("AT4450").trim())
							|| null == variable.getAttribute("AT4450").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4450").trim())
							|| "-999".equals(variable.getAttribute("AT4450")
									.trim())
							|| "".equals(variable.getAttribute("AT4750").trim())
							|| null == variable.getAttribute("AT4750").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4750").trim())
							|| "-999".equals(variable.getAttribute("AT4750")
									.trim())
							|| "".equals(variable.getAttribute("AT4760").trim())
							|| null == variable.getAttribute("AT4760").trim()
							|| "null".equalsIgnoreCase(variable.getAttribute(
									"AT4760").trim())
							|| "-999".equals(variable.getAttribute("AT4760")
									.trim())) {
						liuyang = "1";
					} else {
						liuyang = "2";
					}
				}
			}
			System.out.println("liuyang" + liuyang);
			System.out.println("liuyanghe" + liuyanghe);
			if ("2".equals(liuyanghe) && "2".equals(liuyang)) {
				AT6100 = "2";
			} else {
				AT6100 = "1";
			}
			tempVariable = new VariableBean("AT6100", AT6100, "N", "EX", "变量缺失");
			attachVariableA.add(tempVariable);

			String A01002 = "";// 征信报告中是否具有征信信息：
			attrString = variable.getAttribute("A01002").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A01002 = "2";
			} else {
				/**C1507封版后，统一改为判断1和Y*/
				if ("Y".equalsIgnoreCase(attrString) || "1".equalsIgnoreCase(attrString)) {
					A01002 = "1";
				} else {
					A01002 = "2";
				}
			}
			if ("3".equals(A01001)) {
				A01002 = "1";
			}
			tempVariable = new VariableBean("A01002", A01002, "N", "EX",
					"征信报告中是否具有征信信息");
			attachVariableA.add(tempVariable);

			String A02001 = "";// 是否我行信用卡客户：
			attrString = variable.getAttribute("A02001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A02001 = "2";
			} else {
				/**C1507封版后，统一改为判断1和Y*/
				if ("Y".equalsIgnoreCase(attrString) || "1".equalsIgnoreCase(attrString)) {
					A02001 = "1";
				} else {
					A02001 = "2";
				}
			}
			tempVariable = new VariableBean("A02001", A02001, "N", "EX",
					"是否我行信用卡客户");
			attachVariableA.add(tempVariable);

			String A02002 = "";// 我行客户信用额度：
			attrString = variable.getAttribute("A02002").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A02002 = "0";
			} else {
				A02002 = attrString;
			}
			tempVariable = new VariableBean("A02002", A02002, "N", "EX",
					"我行客户信用额度");
			attachVariableA.add(tempVariable);

			String A03001 = "";// 是否他行信用卡客户：
			attrString = variable.getAttribute("A03001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A03001 = "2";
			} else {
				/**C1507封版后，统一改为判断1和Y*/
				if ("Y".equalsIgnoreCase(attrString) || "1".equalsIgnoreCase(attrString)) {
					A03001 = "1";
				} else {
					A03001 = "2";
				}
			}
			tempVariable = new VariableBean("A03001", A03001, "N", "EX",
					"是否他行信用卡客户");
			attachVariableA.add(tempVariable);

			String A03002 = "";// 他行信用卡单卡最高额度(人民币)：
			attrString = variable.getAttribute("A03002").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A03002 = "0";
			} else {
				A03002 = attrString;
			}
			tempVariable = new VariableBean("A03002", A03002, "N", "EX",
					"他行信用卡单卡最高额度");
			attachVariableA.add(tempVariable);

			String A03003 = "";// 他行信用卡卡龄6个月以上的单卡最高额度(人民币)：
			attrString = variable.getAttribute("A03003").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A03003 = "0";
			} else {
				A03003 = attrString;
			}
			tempVariable = new VariableBean("A03003", A03003, "N", "EX",
					"他行信用卡卡龄6个月以上的单卡最高额度");
			attachVariableA.add(tempVariable);
			
			String A04002 = "";// 行内个人金融资产类客户等级：
			attrString = variable.getAttribute("A04002").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A04002 = "5";
			} else {
				if ("00".equals(attrString)) {
					A04002 = "5";
				} else if ("04".equals(attrString)) {
					A04002 = "1";//
				} else if ("05".equals(attrString)) {
					A04002 = "2";
				} else if ("06".equals(attrString)) {
					A04002 = "3";
				} else if ("08".equals(attrString)) {
					A04002 = "4";
				}
			}
			tempVariable = new VariableBean("A04002", A04002, "N", "EX",
					"行内个人金融资产类客户等级");
			attachVariableA.add(tempVariable);
			
			String A04001 = "";// 是否行内个人金融资产类客户：
			attrString = variable.getAttribute("A04001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A04001 = "2";
			} else if("5".equalsIgnoreCase(A04002)){
				A04001 = "2";
			}else {
				/**C1507封版后，统一改为判断1和Y*/
				if ("Y".equalsIgnoreCase(attrString) || "1".equalsIgnoreCase(attrString)) {
					A04001 = "1";
				} else {
					A04001 = "2";
				}
			}
			tempVariable = new VariableBean("A04001", A04001, "N", "EX",
					"是否行内个人金融资产类客户");
			attachVariableA.add(tempVariable);
			String A04003 = "";// 行内个人金融资产类客户近三个月月日均资产余额：
			attrString = variable.getAttribute("A04003").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A04003 = "0";
			} else {
				A04003 = attrString;
			}
			tempVariable = new VariableBean("A04003", A04003, "N", "EX",
					"行内个人金融资产类客户近三个月月日均资产余额");
			attachVariableA.add(tempVariable);

			String A05001 = "";// 养老保险金参保地
			attrString = variable.getAttribute("A05001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A05001 = "0";
			} else {
				A05001 = attrString;
			}
			tempVariable = new VariableBean("A05001", A05001, "N", "EX",
					"养老保险金参保地");
			attachVariableA.add(tempVariable);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String[] today, time, number;
			String Year = "";
			String Month = "";
			String Day = "";
			String first_time = "";
			String A05002 = "";// 养老保险金参保日期
			today = format.format(date).split("-");
			attrString = variable.getAttribute("A05002").trim();
			System.out.println("old_A05002-----------" + attrString);
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A05002 = "0";
			} else {
				number = Month_Number(attrString);
				Year = number[0];
				Month = number[1];
				Day = number[2];
				if (Year != null && Month != null
						&& !Year.equalsIgnoreCase("null") && !Year.equals("")
						&& !Month.equalsIgnoreCase("null") && !Month.equals("")) {
					A05002 = String
							.valueOf((Integer.parseInt(today[0]) - Integer
									.parseInt(Year))
									* 12
									+ (Integer.parseInt(today[1]) - Integer
											.parseInt(Month)));
				} else {
					A05002 = "0";
				}
			}
			tempVariable = new VariableBean("A05002", A05002, "N", "EX",
					"养老保险金参保日期");
			attachVariableA.add(tempVariable);

			String A05003 = "";// 养老保险金累计缴费月数
			attrString = variable.getAttribute("A05003").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A05003 = "0";
			} else {
				A05003 = attrString;
			}
			tempVariable = new VariableBean("A05003", A05003, "N", "EX",
					"养老保险金累计缴费月数");
			attachVariableA.add(tempVariable);

			String A05004 = "";// 养老保险金参加工作月份
			attrString = variable.getAttribute("A05004").trim();
			System.out.println("old_A05004-----------" + attrString);
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A05004 = "0";
			} else {
				number = Month_Number(attrString);
				Year = number[0];
				Month = number[1];
				Day = number[2];
				A05004 = Month;
			}
			tempVariable = new VariableBean("A05004", A05004, "N", "EX",
					"养老保险金参加工作月份");
			attachVariableA.add(tempVariable);

			String A05005 = "";// 养老保险金缴费状态
			attrString = variable.getAttribute("A05005").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A05005 = "0";
			} else {
				if (attrString.indexOf("|") > 0) {
					time = attrString.split("|");
					A05005 = time[0];
				} else {
					A05005 = attrString;
				}
			}
			tempVariable = new VariableBean("A05005", A05005, "N", "EX",
					"养老保险金缴费状态");
			attachVariableA.add(tempVariable);

			String A05006 = "";// 养老保险金个人缴费基数
			attrString = variable.getAttribute("A05006").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A05006 = "0";
			} else {
				A05006 = attrString;
			}
			tempVariable = new VariableBean("A05006", A05006, "N", "EX",
					"养老保险金个人缴费基数");
			attachVariableA.add(tempVariable);

			String A05007 = "";// 养老保险金本月缴费金额
			attrString = variable.getAttribute("A05007").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A05007 = "0";
			} else {
				A05007 = attrString;
			}
			tempVariable = new VariableBean("A05007", A05007, "N", "EX",
					"养老保险金本月缴费金额");
			attachVariableA.add(tempVariable);

			String A05008 = "";// 养老保险金信息更新日期
			attrString = variable.getAttribute("A05008").trim();
			System.out.println("old_A05008-----------" + attrString);
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A05008 = "0";
			} else {
				number = Month_Number(attrString);
				Year = number[0];
				Month = number[1];
				Day = number[2];
				if (Year != null && Month != null
						&& !Year.equalsIgnoreCase("null") && !Year.equals("")
						&& !Month.equalsIgnoreCase("null") && !Month.equals("")) {
					A05008 = String
							.valueOf((Integer.parseInt(today[0]) - Integer
									.parseInt(Year))
									* 12
									+ (Integer.parseInt(today[1]) - Integer
											.parseInt(Month)));
				} else {
					A05008 = "0";
				}
			}
			tempVariable = new VariableBean("A05008", A05008, "N", "EX",
					"养老保险金信息更新日期");
			attachVariableA.add(tempVariable);

			String A05009 = "";// 养老保险金缴费单位
			attrString = variable.getAttribute("A05009").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A05009 = "0";
			} else {
				A05009 = attrString;
			}
			tempVariable = new VariableBean("A05009", A05009, "N", "EX",
					"养老保险金缴费单位");
			attachVariableA.add(tempVariable);

			String A05010 = "";// 养老保险金中断或终止缴费原因
			attrString = variable.getAttribute("A05010").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A05010 = "0";
			} else {
				A05010 = attrString;
			}
			tempVariable = new VariableBean("A05010", A05010, "N", "EX",
					"养老保险金中断或终止缴费原因");
			attachVariableA.add(tempVariable);

			String A06001 = "";// 住房公积金参缴地：
			attrString = variable.getAttribute("A06001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A06001 = "0";
			} else {
				A06001 = attrString;
			}
			tempVariable = new VariableBean("A06001", A06001, "N", "EX",
					"住房公积金参缴地");
			attachVariableA.add(tempVariable);

			String A06002 = "";// 住房公积金初缴月份：
			attrString = variable.getAttribute("A06002").trim();
			System.out.println("old_A06002-----------" + attrString);
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A06002 = "0";
			} else {
				number = Month_Number(attrString);
				Year = number[0];
				Month = number[1];
				Day = number[2];
				A06002 = Month;
			}
			tempVariable = new VariableBean("A06002", A06002, "N", "EX",
					"住房公积金初缴月份");
			attachVariableA.add(tempVariable);

			String A06003 = "";// 住房公积金缴至月份：
			attrString = variable.getAttribute("A06003").trim();
			System.out.println("old_A06003-----------" + attrString);
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A06003 = "0";
			} else {
				number = Month_Number(attrString);
				Year = number[0];
				Month = number[1];
				Day = number[2];
				A06003 = Month;
			}
			tempVariable = new VariableBean("A06003", A06003, "N", "EX",
					"住房公积金缴至月份");
			attachVariableA.add(tempVariable);

			String A06004 = "";// 住房公积金缴费状态：
			attrString = variable.getAttribute("A06004").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A06004 = "0";
			} else {
				if (attrString.indexOf("|") > 0) {
					time = attrString.split("|");
					A06004 = time[0];
				} else {
					A06004 = attrString;
				}
			}
			tempVariable = new VariableBean("A06004", A06004, "N", "EX",
					"住房公积金缴费状态");
			attachVariableA.add(tempVariable);

			String A06005 = "";// 住房公积金月缴存额：
			attrString = variable.getAttribute("A06005").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A06005 = "0";
			} else {
				A06005 = attrString;
			}
			tempVariable = new VariableBean("A06005", A06005, "N", "EX",
					"住房公积金月缴存额");
			attachVariableA.add(tempVariable);

			String A06006 = "";// 住房公积金个人缴存比例：
			attrString = variable.getAttribute("A06006").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A06006 = "0";
			} else {
				if (attrString.indexOf("%") > 0) {
					A06006 = String.valueOf(Float.valueOf(attrString.substring(
							0, attrString.indexOf("%"))) / 100);
				} else {
					A06006 = attrString;
				}
			}
			tempVariable = new VariableBean("A06006", A06006, "N", "EX",
					"住房公积金个人缴存比例");
			attachVariableA.add(tempVariable);

			String A06007 = "";// 住房公积金单位缴存比例：
			attrString = variable.getAttribute("A06007").trim();
			if (null == attrString || null == attrString
					|| "-999".equals(attrString) || "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A06007 = "0";
			} else {
				if (attrString.indexOf("%") > 0) {
					A06007 = String.valueOf(Float.valueOf(attrString.substring(
							0, attrString.indexOf("%"))) / 100);
				} else {
					A06007 = attrString;
				}
			}
			tempVariable = new VariableBean("A06007", A06007, "N", "EX",
					"住房公积金单位缴存比例");
			attachVariableA.add(tempVariable);

			String A06008 = "";// 住房公积金缴费单位
			attrString = variable.getAttribute("A06008").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A06008 = "0";
			} else {
				A06008 = attrString;
			}
			tempVariable = new VariableBean("A06008", A06008, "N", "EX",
					"住房公积金缴费单位");
			attachVariableA.add(tempVariable);

			String A06009 = "";// 住房公积金信息更新日期
			attrString = variable.getAttribute("A06009").trim();
			System.out.println("old_A06009-----------" + attrString);
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A06009 = "0";
			} else {
				number = Month_Number(attrString);
				Year = number[0];
				Month = number[1];
				Day = number[2];
				if (!Year.equalsIgnoreCase("null") && Year != null
						&& !Year.equals("") && !Month.equalsIgnoreCase("null")
						&& Month != null && !Month.equals("")) {
					A06009 = String
							.valueOf((Integer.parseInt(today[0]) - Integer
									.parseInt(Year))
									* 12
									+ (Integer.parseInt(today[1]) - Integer
											.parseInt(Month)));
				} else {
					A06009 = "0";
				}
			}
			tempVariable = new VariableBean("A06009", A06009, "N", "EX",
					"住房公积金信息更新日期");
			attachVariableA.add(tempVariable);

			String A07001 = "";// 近3个月同时发生信用卡和贷款逾期的次数：
			attrString = variable.getAttribute("A07001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A07001 = "0";
			} else {
				A07001 = attrString;
			}
			tempVariable = new VariableBean("A07001", A07001, "N", "EX",
					"近3个月同时发生信用卡和贷款逾期的次数");
			attachVariableA.add(tempVariable);

			String A07002 = "";// 近6个月同时发生信用卡和贷款逾期的次数：
			attrString = variable.getAttribute("A07002").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A07002 = "0";
			} else {
				A07002 = attrString;
			}
			if ("3".equals(A01001)) {
				A07002 = "0";
			} else {
				A07002 = "-999";
			}
			tempVariable = new VariableBean("A07002", A07002, "N", "EX",
					"近6个月同时发生信用卡和贷款逾期的次数");
			attachVariableA.add(tempVariable);

			String A07003 = "";// 近12个月同时发生信用卡和贷款逾期的次数：
			attrString = variable.getAttribute("A07003").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A07003 = "0";
			} else {
				A07003 = attrString;
			}
			tempVariable = new VariableBean("A07003", A07003, "N", "EX",
					"近12个月同时发生信用卡和贷款逾期的次数");
			attachVariableA.add(tempVariable);

			String A07004 = "";// 近24个月同时发生信用卡和贷款逾期的次数：
			attrString = variable.getAttribute("A07004").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A07004 = "0";
			} else {
				A07004 = attrString;
			}
			tempVariable = new VariableBean("A07004", A07004, "N", "EX",
					"近24个月同时发生信用卡和贷款逾期的次数");
			attachVariableA.add(tempVariable);

			String A08001 = "";// 贷记卡发卡机构数：
			attrString = variable.getAttribute("A08001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08001 = "0";
			} else {
				A08001 = attrString;
			}
			if ("3".equals(A01001)) {
				A08001 = "0";
			}
			tempVariable = new VariableBean("A08001", A08001, "N", "EX",
					"贷记卡发卡机构数");
			attachVariableA.add(tempVariable);

			String A08002 = "";// 贷记卡账户数：
			attrString = variable.getAttribute("A08002").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08002 = "0";
			} else {
				A08002 = attrString;
			}
			if ("3".equals(A01001)) {
				A08002 = "0";
			}
			tempVariable = new VariableBean("A08002", A08002, "N", "EX",
					"贷记卡账户数");
			attachVariableA.add(tempVariable);

			String A08003 = "";// 贷记卡非正常账户数：
			attrString = variable.getAttribute("A08003").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08003 = "0";
			} else {
				A08003 = attrString;
			}
			if ("3".equals(A01001)) {
				A08003 = "0";
			}
			tempVariable = new VariableBean("A08003", A08003, "N", "EX",
					"贷记卡非正常账户数");
			attachVariableA.add(tempVariable);

			String A08004 = "";// 贷记卡最早开户日期：
			attrString = variable.getAttribute("A08004").trim();
			// System.out.println("old_A08004-----------"+attrString);
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08004 = "0";
			} else {
				number = Month_Number(attrString);
				Year = number[0];
				Month = number[1];
				Day = number[2];
				if (Year != null && Month != null
						&& !Year.equalsIgnoreCase("null") && !Year.equals("")
						&& !Month.equalsIgnoreCase("null") && !Month.equals("")) {
					A08004 = String
							.valueOf((Integer.parseInt(today[0]) - Integer
									.parseInt(Year))
									* 12
									+ (Integer.parseInt(today[1]) - Integer
											.parseInt(Month)));
				} else {
					A08004 = "0";
				}
			}
			tempVariable = new VariableBean("A08004", A08004, "N", "EX",
					"贷记卡最早开户日期");
			attachVariableA.add(tempVariable);

			String A08005 = "";// 贷记卡授信总额：
			attrString = variable.getAttribute("A08005").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08005 = "0";
			} else {
				A08005 = attrString;
			}
			if ("3".equals(A01001)) {
				A08005 = "0";
			}
			tempVariable = new VariableBean("A08005", A08005, "N", "EX",
					"贷记卡授信总额");
			attachVariableA.add(tempVariable);

			String A08006 = "";// 贷记卡总已用额度：
			attrString = variable.getAttribute("A08006").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08006 = "0";
			} else {
				A08006 = attrString;
			}
			tempVariable = new VariableBean("A08006", A08006, "N", "EX",
					"贷记卡总已用额度");
			attachVariableA.add(tempVariable);

			String A08007 = "";// 贷记卡最近6个月平均使用额度：
			attrString = variable.getAttribute("A08007").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08007 = "0";
			} else {
				A08007 = attrString;
			}
			if ("3".equals(A01001)) {
				A08007 = "0";
			}
			tempVariable = new VariableBean("A08007", A08007, "N", "EX",
					"贷记卡最近6个月平均使用额度");
			attachVariableA.add(tempVariable);

			String A08008 = "";// 贷记卡单家行最高额度(人民币)：
			attrString = variable.getAttribute("A08008").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08008 = "0";
			} else {
				A08008 = attrString;
			}
			tempVariable = new VariableBean("A08008", A08008, "N", "EX",
					"贷记卡单家行最高额度");
			attachVariableA.add(tempVariable);

			String A08009 = "";// 贷记卡单家行最低授信额(人民币)：
			attrString = variable.getAttribute("A08009").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08009 = "0";
			} else {
				A08009 = attrString;
			}
			tempVariable = new VariableBean("A08009", A08009, "N", "EX",
					"贷记卡单家行最低授信额");
			attachVariableA.add(tempVariable);

			String A08010 = "";// 贷记卡卡龄6个月以上最高额度(人民币)：
			attrString = variable.getAttribute("A08010").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08010 = "0";
			} else {
				A08010 = attrString;
			}
			tempVariable = new VariableBean("A08010", A08010, "N", "EX",
					"贷记卡卡龄6个月以上最高额度");
			attachVariableA.add(tempVariable);

			String A08011 = "";// 贷记卡他行最高额度(人民币)：
			attrString = variable.getAttribute("A08011").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08011 = "0";
			} else {
				A08011 = attrString;
			}
			tempVariable = new VariableBean("A08011", A08011, "N", "EX",
					"贷记卡他行最高额度");
			attachVariableA.add(tempVariable);

			String A08012 = "";// 贷记卡他行10万及以下第一高额度(人民币)：
			attrString = variable.getAttribute("A08012").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08012 = "0";
			} else {
				A08012 = attrString;
			}
			tempVariable = new VariableBean("A08012", A08012, "N", "EX",
					"贷记卡他行10万及以下第一高额度");
			attachVariableA.add(tempVariable);

			String A08013 = "";// 贷记卡他行10万及以下第二高额度(人民币)：
			attrString = variable.getAttribute("A08013").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08013 = "0";
			} else {
				A08013 = attrString;
			}
			tempVariable = new VariableBean("A08013", A08013, "N", "EX",
					"贷记卡他行10万及以下第二高额度");
			attachVariableA.add(tempVariable);

			String A08014 = "";// 贷记卡当前逾期总额：
			attrString = variable.getAttribute("A08014").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08014 = "0";
			} else {
				A08014 = attrString;
			}
			tempVariable = new VariableBean("A08014", A08014, "N", "EX",
					"贷记卡当前逾期总额");
			attachVariableA.add(tempVariable);

			String A09001 = "";// 贷记卡近3个月M1的次数：
			attrString = variable.getAttribute("A09001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09001 = "0";
			} else {
				A09001 = attrString;
			}
			if ("3".equals(A01001)) {
				A09001 = "0";
			}
			tempVariable = new VariableBean("A09001", A09001, "N", "EX",
					"贷记卡近3个月M1的次数");
			attachVariableA.add(tempVariable);

			String A09002 = "";// 贷记卡近3个月M2的次数：
			attrString = variable.getAttribute("A09002").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09002 = "0";
			} else {
				A09002 = attrString;
			}
			if ("3".equals(A01001)) {
				A09002 = "0";
			}
			tempVariable = new VariableBean("A09002", A09002, "N", "EX",
					"贷记卡近3个月M2的次数");
			attachVariableA.add(tempVariable);

			String A09003 = "";// 贷记卡近3个月M3的次数：
			attrString = variable.getAttribute("A09003").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09003 = "0";
			} else {
				A09003 = attrString;
			}
			if ("3".equals(A01001)) {
				A09003 = "0";
			}
			tempVariable = new VariableBean("A09003", A09003, "N", "EX",
					"贷记卡近3个月M3的次数");
			attachVariableA.add(tempVariable);

			String A09004 = "";// 贷记卡近3个月M4的次数：
			attrString = variable.getAttribute("A09004").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09004 = "0";
			} else {
				A09004 = attrString;
			}
			if ("3".equals(A01001)) {
				A09004 = "0";
			}
			tempVariable = new VariableBean("A09004", A09004, "N", "EX",
					"贷记卡近3个月M4的次数");
			attachVariableA.add(tempVariable);

			String A09005 = "";// 贷记卡近3个月M5的次数：
			attrString = variable.getAttribute("A09005").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09005 = "0";
			} else {
				A09005 = attrString;
			}
			if ("3".equals(A01001)) {
				A09005 = "0";
			}
			tempVariable = new VariableBean("A09005", A09005, "N", "EX",
					"贷记卡近3个月M5的次数");
			attachVariableA.add(tempVariable);

			String A09006 = "";// 贷记卡近3个月M6的次数：
			attrString = variable.getAttribute("A09006").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09006 = "0";
			} else {
				A09006 = attrString;
			}
			if ("3".equals(A01001)) {
				A09006 = "0";
			}
			tempVariable = new VariableBean("A09006", A09006, "N", "EX",
					"贷记卡近3个月M6的次数");
			attachVariableA.add(tempVariable);

			String A09007 = "";// 贷记卡近3个月M7的次数：
			attrString = variable.getAttribute("A09007").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09007 = "0";
			} else {
				A09007 = attrString;
			}
			if ("3".equals(A01001)) {
				A09007 = "0";
			}
			tempVariable = new VariableBean("A09007", A09007, "N", "EX",
					"贷记卡近3个月M7的次数");
			attachVariableA.add(tempVariable);

			String A09008 = "";// 贷记卡近6个月M1的次数：
			attrString = variable.getAttribute("A09008").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09008 = "0";
			} else {
				A09008 = attrString;
			}
			if ("3".equals(A01001)) {
				A09008 = "0";
			}
			tempVariable = new VariableBean("A09008", A09008, "N", "EX",
					"贷记卡近6个月M1的次数");
			attachVariableA.add(tempVariable);

			String A09009 = "";// 贷记卡近6个月M2的次数：
			attrString = variable.getAttribute("A09009").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09009 = "0";
			} else {
				A09009 = attrString;
			}
			if ("3".equals(A01001)) {
				A09009 = "0";
			}
			tempVariable = new VariableBean("A09009", A09009, "N", "EX",
					"贷记卡近6个月M2的次数");
			attachVariableA.add(tempVariable);

			String A09010 = "";// 贷记卡近6个月M3的次数：
			attrString = variable.getAttribute("A09010").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09010 = "0";
			} else {
				A09010 = attrString;
			}
			if ("3".equals(A01001)) {
				A09010 = "0";
			}
			tempVariable = new VariableBean("A09010", A09010, "N", "EX",
					"贷记卡近6个月M3的次数");
			attachVariableA.add(tempVariable);

			String A09011 = "";// 贷记卡近6个月M4的次数：
			attrString = variable.getAttribute("A09011").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09011 = "0";
			} else {
				A09011 = attrString;
			}
			if ("3".equals(A01001)) {
				A09011 = "0";
			}
			tempVariable = new VariableBean("A09011", A09011, "N", "EX",
					"贷记卡近6个月M4的次数");
			attachVariableA.add(tempVariable);

			String A09012 = "";// 贷记卡近6个月M5的次数：
			attrString = variable.getAttribute("A09012").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09012 = "0";
			} else {
				A09012 = attrString;
			}
			if ("3".equals(A01001)) {
				A09012 = "0";
			}
			tempVariable = new VariableBean("A09012", A09012, "N", "EX",
					"贷记卡近6个月M5的次数");
			attachVariableA.add(tempVariable);

			String A09013 = "";// 贷记卡近6个月M6的次数：
			attrString = variable.getAttribute("A09013").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09013 = "0";
			} else {
				A09013 = attrString;
			}
			if ("3".equals(A01001)) {
				A09013 = "0";
			}
			tempVariable = new VariableBean("A09013", A09013, "N", "EX",
					"贷记卡近6个月M6的次数");
			attachVariableA.add(tempVariable);

			String A09014 = "";// 贷记卡近6个月M7的次数：
			attrString = variable.getAttribute("A09014").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09014 = "0";
			} else {
				A09014 = attrString;
			}
			if ("3".equals(A01001)) {
				A09014 = "0";
			}
			tempVariable = new VariableBean("A09014", A09014, "N", "EX",
					"贷记卡近6个月M7的次数");
			attachVariableA.add(tempVariable);

			String A09015 = "";// 贷记卡近12个月M1的次数：
			attrString = variable.getAttribute("A09015").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09015 = "0";
			} else {
				A09015 = attrString;
			}
			if ("3".equals(A01001)) {
				A09015 = "0";
			}
			tempVariable = new VariableBean("A09015", A09015, "N", "EX",
					"贷记卡近12个月M1的次数");
			attachVariableA.add(tempVariable);

			String A09016 = "";// 贷记卡近12个月M2的次数：
			attrString = variable.getAttribute("A09016").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09016 = "0";
			} else {
				A09016 = attrString;
			}
			if ("3".equals(A01001)) {
				A09016 = "0";
			}
			tempVariable = new VariableBean("A09016", A09016, "N", "EX",
					"贷记卡近12个月M2的次数");
			attachVariableA.add(tempVariable);

			String A09017 = "";// 贷记卡近12个月M3的次数：
			attrString = variable.getAttribute("A09017").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09017 = "0";
			} else {
				A09017 = attrString;
			}
			if ("3".equals(A01001)) {
				A09017 = "0";
			}
			tempVariable = new VariableBean("A09017", A09017, "N", "EX",
					"贷记卡近12个月M3的次数");
			attachVariableA.add(tempVariable);

			String A09018 = "";// 贷记卡近12个月M4的次数：
			attrString = variable.getAttribute("A09018").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09018 = "0";
			} else {
				A09018 = attrString;
			}
			if ("3".equals(A01001)) {
				A09018 = "0";
			}
			tempVariable = new VariableBean("A09018", A09018, "N", "EX",
					"贷记卡近12个月M4的次数");
			attachVariableA.add(tempVariable);

			String A09019 = "";// 贷记卡近12个月M5的次数：
			attrString = variable.getAttribute("A09019").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09019 = "0";
			} else {
				A09019 = attrString;
			}
			if ("3".equals(A01001)) {
				A09019 = "0";
			}
			tempVariable = new VariableBean("A09019", A09019, "N", "EX",
					"贷记卡近12个月M5的次数");
			attachVariableA.add(tempVariable);

			String A09020 = "";// 贷记卡近12个月M6的次数：
			attrString = variable.getAttribute("A09020").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09020 = "0";
			} else {
				A09020 = attrString;
			}
			if ("3".equals(A01001)) {
				A09020 = "0";
			}
			tempVariable = new VariableBean("A09020", A09020, "N", "EX",
					"贷记卡近12个月M6的次数");
			attachVariableA.add(tempVariable);

			String A09021 = "";// 贷记卡近12个月M7的次数：
			attrString = variable.getAttribute("A09021").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09021 = "0";
			} else {
				A09021 = attrString;
			}
			if ("3".equals(A01001)) {
				A09021 = "0";
			}
			tempVariable = new VariableBean("A09021", A09021, "N", "EX",
					"贷记卡近12个月M7的次数");
			attachVariableA.add(tempVariable);

			String A09022 = "";// 贷记卡近24个月M1的次数：
			attrString = variable.getAttribute("A09022").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09022 = "0";
			} else {
				A09022 = attrString;
			}
			if ("3".equals(A01001)) {
				A09022 = "0";
			}
			tempVariable = new VariableBean("A09022", A09022, "N", "EX",
					"贷记卡近24个月M1的次数");
			attachVariableA.add(tempVariable);

			String A09023 = "";// 贷记卡近24个月M2的次数：
			attrString = variable.getAttribute("A09023").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09023 = "0";
			} else {
				A09023 = attrString;
			}
			if ("3".equals(A01001)) {
				A09023 = "0";
			}
			tempVariable = new VariableBean("A09023", A09023, "N", "EX",
					"贷记卡近24个月M2的次数");
			attachVariableA.add(tempVariable);

			String A09024 = "";// 贷记卡近24个月M3的次数：
			attrString = variable.getAttribute("A09024").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09024 = "0";
			} else {
				A09024 = attrString;
			}
			if ("3".equals(A01001)) {
				A09024 = "0";
			}
			tempVariable = new VariableBean("A09024", A09024, "N", "EX",
					"贷记卡近24个月M3的次数");
			attachVariableA.add(tempVariable);

			String A09025 = "";// 贷记卡近24个月M4的次数：
			attrString = variable.getAttribute("A09025").trim();

			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09025 = "0";
			} else {
				A09025 = attrString;
			}
			if ("3".equals(A01001)) {
				A09025 = "0";
			}
			tempVariable = new VariableBean("A09025", A09025, "N", "EX",
					"贷记卡近24个月M4的次数");
			attachVariableA.add(tempVariable);

			String A09026 = "";// 贷记卡近24个月M5的次数：
			attrString = variable.getAttribute("A09026").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09026 = "0";
			} else {
				A09026 = attrString;
			}
			if ("3".equals(A01001)) {
				A09026 = "0";
			}
			tempVariable = new VariableBean("A09026", A09026, "N", "EX",
					"贷记卡近24个月M5的次数");
			attachVariableA.add(tempVariable);

			String A09027 = "";// 贷记卡近24个月M6的次数：
			attrString = variable.getAttribute("A09027").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09027 = "0";
			} else {
				A09027 = attrString;
			}
			if ("3".equals(A01001)) {
				A09027 = "0";
			}
			tempVariable = new VariableBean("A09027", A09027, "N", "EX",
					"贷记卡近24个月M6的次数");
			attachVariableA.add(tempVariable);

			String A09028 = "";// 贷记卡近24个月M7的次数：
			attrString = variable.getAttribute("A09028").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09028 = "0";
			} else {
				A09028 = attrString;
			}
			if ("3".equals(A01001)) {
				A09028 = "0";
			}
			tempVariable = new VariableBean("A09028", A09028, "N", "EX",
					"贷记卡近24个月M7的次数");
			attachVariableA.add(tempVariable);

			String A09029 = "";// 贷记卡近5年前36个月逾期最高持续月数：
			attrString = variable.getAttribute("A09029").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09029 = "0";
			} else {
				A09029 = attrString;
			}
			if ("3".equals(A01001)) {
				A09029 = "0";
			}
			tempVariable = new VariableBean("A09029", A09029, "N", "EX",
					"贷记卡近5年前36个月逾期最高持续月数");
			attachVariableA.add(tempVariable);

			String A09030 = "";// 贷记卡当月最高逾期级别：
			attrString = variable.getAttribute("A09030").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A09030 = "0";
			} else {
				if (attrString.indexOf("|") > 0) {
					time = attrString.split("|");
					A09030 = time[0];
				} else {
					A09030 = attrString;
				}
			}
			if ("3".equals(A01001)) {
				A09030 = "0";
			}
			tempVariable = new VariableBean("A09030", A09030, "N", "EX",
					"贷记卡当月最高逾期级别");
			attachVariableA.add(tempVariable);

			String A10001 = "";// 准贷记卡发卡机构数：
			attrString = variable.getAttribute("A10001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10001 = "0";
			} else {
				A10001 = attrString;
			}
			if ("3".equals(A01001)) {
				A10001 = "0";
			}
			tempVariable = new VariableBean("A10001", A10001, "N", "EX",
					"准贷记卡发卡机构数");
			attachVariableA.add(tempVariable);

			String A10002 = "";// 准贷记卡账户数：
			attrString = variable.getAttribute("A10002").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10002 = "0";
			} else {
				A10002 = attrString;
			}
			if ("3".equals(A01001)) {
				A10002 = "0";
			}
			tempVariable = new VariableBean("A10002", A10002, "N", "EX",
					"准贷记卡账户数");
			attachVariableA.add(tempVariable);

			String A10003 = "";// 准贷记卡非正常账户数：
			attrString = variable.getAttribute("A10003").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10003 = "0";
			} else {
				A10003 = attrString;
			}
			if ("3".equals(A01001)) {
				A10003 = "0";
			}
			tempVariable = new VariableBean("A10003", A10003, "N", "EX",
					"准贷记卡非正常账户数");
			attachVariableA.add(tempVariable);

			String A10004 = "";// 准贷记卡最早开户日期：
			attrString = variable.getAttribute("A10004").trim();
			// System.out.println("old_A10004-----------"+attrString);
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10004 = "0";
			} else {
				number = Month_Number(attrString);
				Year = number[0];
				Month = number[1];
				Day = number[2];
				if (Year != null && Month != null
						&& !Year.equalsIgnoreCase("null") && !Year.equals("")
						&& !Month.equalsIgnoreCase("null") && !Month.equals("")) {
					A10004 = String
							.valueOf((Integer.parseInt(today[0]) - Integer
									.parseInt(Year))
									* 12
									+ (Integer.parseInt(today[1]) - Integer
											.parseInt(Month)));
				} else {
					A10004 = "0";
				}
			}
			tempVariable = new VariableBean("A10004", A10004, "N", "EX",
					"准贷记卡最早开户日期");
			attachVariableA.add(tempVariable);

			String A10005 = "";// 准贷记卡授信总额：
			attrString = variable.getAttribute("A10005").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10005 = "0";
			} else {
				A10005 = attrString;
			}
			if ("3".equals(A01001)) {
				A10005 = "0";
			}
			tempVariable = new VariableBean("A10005", A10005, "N", "EX",
					"准贷记卡授信总额");
			attachVariableA.add(tempVariable);

			String A10006 = "";// 准贷记卡总透支余额：
			attrString = variable.getAttribute("A10006").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10006 = "0";
			} else {
				A10006 = attrString;
			}
			tempVariable = new VariableBean("A10006", A10006, "N", "EX",
					"准贷记卡总透支余额");
			attachVariableA.add(tempVariable);

			String A10007 = "";// 准贷记卡最近6个月平均透支余额：
			attrString = variable.getAttribute("A10007").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10007 = "0";
			} else {
				A10007 = attrString;
			}
			if ("3".equals(A01001)) {
				A10007 = "0";
			}
			tempVariable = new VariableBean("A10007", A10007, "N", "EX",
					"准贷记卡最近6个月平均透支余额");
			attachVariableA.add(tempVariable);

			String A10008 = "";// 准贷记卡单家行最高额度(人民币)：
			attrString = variable.getAttribute("A10008").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10008 = "0";
			} else {
				A10008 = attrString;
			}
			tempVariable = new VariableBean("A10008", A10008, "N", "EX",
					"准贷记卡单家行最高额度");
			attachVariableA.add(tempVariable);

			String A10009 = "";// 准贷记卡单家行最低授信额(人民币)：
			attrString = variable.getAttribute("A10009").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10009 = "0";
			} else {
				A10009 = attrString;
			}
			tempVariable = new VariableBean("A10009", A10009, "N", "EX",
					"准贷记卡单家行最低授信额");
			attachVariableA.add(tempVariable);

			String A10010 = "";// 准贷记卡卡龄6个月以上最高额度(人民币)：
			attrString = variable.getAttribute("A10010").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10010 = "0";
			} else {
				A10010 = attrString;
			}
			tempVariable = new VariableBean("A10010", A10010, "N", "EX",
					"准贷记卡卡龄6个月以上最高额度");
			attachVariableA.add(tempVariable);

			String A10011 = "";// 贷记卡他行最高额度(人民币)：
			attrString = variable.getAttribute("A10011").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10011 = "0";
			} else {
				A10011 = attrString;
			}
			tempVariable = new VariableBean("A10011", A10011, "N", "EX",
					"贷记卡他行最高额度");
			attachVariableA.add(tempVariable);

			String A10012 = "";// 贷记卡他行10万及以下第一高额度(人民币)：
			attrString = variable.getAttribute("A10012").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10012 = "0";
			} else {
				A10012 = attrString;
			}
			tempVariable = new VariableBean("A10012", A10012, "N", "EX",
					"贷记卡他行10万及以下第一高额度");
			attachVariableA.add(tempVariable);

			String A10013 = "";// 贷记卡他行10万及以下第二高额度(人民币)：
			attrString = variable.getAttribute("A10013").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10013 = "0";
			} else {
				A10013 = attrString;
			}
			tempVariable = new VariableBean("A10013", A10013, "N", "EX",
					"贷记卡他行10万及以下第二高额度");
			attachVariableA.add(tempVariable);

			String A10014 = "";// 准贷记卡当前逾期总额：
			attrString = variable.getAttribute("A10014").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10014 = "0";
			} else {
				A10014 = attrString;
			}
			tempVariable = new VariableBean("A10014", A10014, "N", "EX",
					"准贷记卡当前逾期总额");
			attachVariableA.add(tempVariable);

			String A11001 = "";// 准贷记卡近3个月M1的次数：
			attrString = variable.getAttribute("A11001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11001 = "0";
			} else {
				A11001 = attrString;
			}
			if ("3".equals(A01001)) {
				A08001 = "0";
			}
			tempVariable = new VariableBean("A11001", A11001, "N", "EX",
					"准贷记卡近3个月M1的次数");
			attachVariableA.add(tempVariable);

			String A11002 = "";// 准贷记卡近3个月M2的次数：
			attrString = variable.getAttribute("A11002").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11002 = "0";
			} else {
				A11002 = attrString;
			}
			if ("3".equals(A01001)) {
				A11002 = "0";
			}
			tempVariable = new VariableBean("A11002", A11002, "N", "EX",
					"准贷记卡近3个月M2的次数");
			attachVariableA.add(tempVariable);

			String A11003 = "";// 准贷记卡近3个月M3的次数：
			attrString = variable.getAttribute("A11003").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11003 = "0";
			} else {
				A11003 = attrString;
			}
			if ("3".equals(A01001)) {
				A11003 = "0";
			}
			tempVariable = new VariableBean("A11003", A11003, "N", "EX",
					"准贷记卡近3个月M3的次数");
			attachVariableA.add(tempVariable);

			String A11004 = "";// 准贷记卡近3个月M4的次数：
			attrString = variable.getAttribute("A11004").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11004 = "0";
			} else {
				A11004 = attrString;
			}
			if ("3".equals(A01001)) {
				A11004 = "0";
			}
			tempVariable = new VariableBean("A11004", A11004, "N", "EX",
					"准贷记卡近3个月M4的次数");
			attachVariableA.add(tempVariable);

			String A11005 = "";// 准贷记卡近3个月M5的次数：
			attrString = variable.getAttribute("A11005").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11005 = "0";
			} else {
				A11005 = attrString;
			}
			if ("3".equals(A01001)) {
				A11005 = "0";
			}
			tempVariable = new VariableBean("A11005", A11005, "N", "EX",
					"准贷记卡近3个月M5的次数");
			attachVariableA.add(tempVariable);

			String A11006 = "";// 准贷记卡近3个月M6的次数：
			attrString = variable.getAttribute("A11006").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11006 = "0";
			} else {
				A11006 = attrString;
			}
			if ("3".equals(A01001)) {
				A11006 = "0";
			}
			tempVariable = new VariableBean("A11006", A11006, "N", "EX",
					"准贷记卡近3个月M6的次数");
			attachVariableA.add(tempVariable);

			String A11007 = "";// 准贷记卡近3个月M7的次数：
			attrString = variable.getAttribute("A11007").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11007 = "0";
			} else {
				A11007 = attrString;
			}
			if ("3".equals(A01001)) {
				A11007 = "0";
			}
			tempVariable = new VariableBean("A11007", A11007, "N", "EX",
					"准贷记卡近3个月M7的次数");
			attachVariableA.add(tempVariable);

			String A11008 = "";// 准贷记卡近6个月M1的次数：
			attrString = variable.getAttribute("A11008").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11008 = "0";
			} else {
				A11008 = attrString;
			}
			if ("3".equals(A01001)) {
				A11008 = "0";
			}
			tempVariable = new VariableBean("A11008", A11008, "N", "EX",
					"准贷记卡近6个月M1的次数");
			attachVariableA.add(tempVariable);

			String A11009 = "";// 准贷记卡近6个月M2的次数：
			attrString = variable.getAttribute("A11009").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11009 = "0";
			} else {
				A11009 = attrString;
			}
			if ("3".equals(A01001)) {
				A11009 = "0";
			}
			tempVariable = new VariableBean("A11009", A11009, "N", "EX",
					"准贷记卡近6个月M2的次数");
			attachVariableA.add(tempVariable);

			String A11010 = "";// 准贷记卡近6个月M3的次数：
			attrString = variable.getAttribute("A11010").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11010 = "0";
			} else {
				A11010 = attrString;
			}
			if ("3".equals(A01001)) {
				A11010 = "0";
			}
			tempVariable = new VariableBean("A11010", A11010, "N", "EX",
					"准贷记卡近6个月M3的次数");
			attachVariableA.add(tempVariable);

			String A11011 = "";// 准贷记卡近6个月M4的次数：
			attrString = variable.getAttribute("A11011").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11011 = "0";
			} else {
				A11011 = attrString;
			}
			if ("3".equals(A01001)) {
				A11011 = "0";
			}
			tempVariable = new VariableBean("A11011", A11011, "N", "EX",
					"准贷记卡近6个月M4的次数");
			attachVariableA.add(tempVariable);

			String A11012 = "";// 准贷记卡近6个月M5的次数：
			attrString = variable.getAttribute("A11012").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11012 = "0";
			} else {
				A11012 = attrString;
			}
			if ("3".equals(A01001)) {
				A11012 = "0";
			}
			tempVariable = new VariableBean("A11012", A11012, "N", "EX",
					"准贷记卡近6个月M5的次数");
			attachVariableA.add(tempVariable);

			String A11013 = "";// 准贷记卡近6个月M6的次数：
			attrString = variable.getAttribute("A11013").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11013 = "0";
			} else {
				A11013 = attrString;
			}
			if ("3".equals(A01001)) {
				A11013 = "0";
			}
			tempVariable = new VariableBean("A11013", A11013, "N", "EX",
					"准贷记卡近6个月M6的次数");
			attachVariableA.add(tempVariable);

			String A11014 = "";// 准贷记卡近6个月M7的次数：
			attrString = variable.getAttribute("A11014").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11014 = "0";
			} else {
				A11014 = attrString;
			}
			if ("3".equals(A01001)) {
				A11014 = "0";
			}
			tempVariable = new VariableBean("A11014", A11014, "N", "EX",
					"准贷记卡近6个月M7的次数");
			attachVariableA.add(tempVariable);

			String A11015 = "";// 准贷记卡近12个月M1的次数：
			attrString = variable.getAttribute("A11015").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11015 = "0";
			} else {
				A11015 = attrString;
			}
			if ("3".equals(A01001)) {
				A11015 = "0";
			}
			tempVariable = new VariableBean("A11015", A11015, "N", "EX",
					"准贷记卡近12个月M1的次数");
			attachVariableA.add(tempVariable);

			String A11016 = "";// 准贷记卡近12个月M2的次数：
			attrString = variable.getAttribute("A11016").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11016 = "0";
			} else {
				A11016 = attrString;
			}
			if ("3".equals(A01001)) {
				A11016 = "0";
			}
			tempVariable = new VariableBean("A11016", A11016, "N", "EX",
					"准贷记卡近12个月M2的次数");
			attachVariableA.add(tempVariable);

			String A11017 = "";// 准贷记卡近12个月M3的次数：
			attrString = variable.getAttribute("A11017").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11017 = "0";
			} else {
				A11017 = attrString;
			}
			if ("3".equals(A01001)) {
				A11017 = "0";
			}
			tempVariable = new VariableBean("A11017", A11017, "N", "EX",
					"准贷记卡近12个月M3的次数");
			attachVariableA.add(tempVariable);

			String A11018 = "";// 准贷记卡近12个月M4的次数：
			attrString = variable.getAttribute("A11018").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11018 = "0";
			} else {
				A11018 = attrString;
			}
			if ("3".equals(A01001)) {
				A11018 = "0";
			}
			tempVariable = new VariableBean("A11018", A11018, "N", "EX",
					"准贷记卡近12个月M4的次数");
			attachVariableA.add(tempVariable);

			String A11019 = "";// 准贷记卡近12个月M5的次数：
			attrString = variable.getAttribute("A11019").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11019 = "0";
			} else {
				A11019 = attrString;
			}
			if ("3".equals(A01001)) {
				A11019 = "0";
			}
			tempVariable = new VariableBean("A11019", A11019, "N", "EX",
					"准贷记卡近12个月M5的次数");
			attachVariableA.add(tempVariable);

			String A11020 = "";// 准贷记卡近12个月M6的次数：
			attrString = variable.getAttribute("A11020").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11020 = "0";
			} else {
				A11020 = attrString;
			}
			if ("3".equals(A01001)) {
				A11020 = "0";
			}
			tempVariable = new VariableBean("A11020", A11020, "N", "EX",
					"准贷记卡近12个月M6的次数");
			attachVariableA.add(tempVariable);

			String A11021 = "";// 准贷记卡近12个月M7的次数：
			attrString = variable.getAttribute("A11021").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11021 = "0";
			} else {
				A11021 = attrString;
			}
			if ("3".equals(A01001)) {
				A11021 = "0";
			}
			tempVariable = new VariableBean("A11021", A11021, "N", "EX",
					"准贷记卡近12个月M7的次数");
			attachVariableA.add(tempVariable);

			String A11022 = "";// 准贷记卡近24个月M1的次数：
			attrString = variable.getAttribute("A11022").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11022 = "0";
			} else {
				A11022 = attrString;
			}
			if ("3".equals(A01001)) {
				A11022 = "0";
			}
			tempVariable = new VariableBean("A11022", A11022, "N", "EX",
					"准贷记卡近24个月M1的次数");
			attachVariableA.add(tempVariable);

			String A11023 = "";// 准贷记卡近24个月M2的次数：
			attrString = variable.getAttribute("A11023").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11023 = "0";
			} else {
				A11023 = attrString;
			}
			if ("3".equals(A01001)) {
				A11023 = "0";
			}
			tempVariable = new VariableBean("A11023", A11023, "N", "EX",
					"准贷记卡近24个月M2的次数");
			attachVariableA.add(tempVariable);

			String A11024 = "";// 准贷记卡近24个月M3的次数：
			attrString = variable.getAttribute("A11024").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11024 = "0";
			} else {
				A11024 = attrString;
			}
			if ("3".equals(A01001)) {
				A11024 = "0";
			}
			tempVariable = new VariableBean("A11024", A11024, "N", "EX",
					"准贷记卡近24个月M3的次数");
			attachVariableA.add(tempVariable);

			String A11025 = "";// 准贷记卡近24个月M4的次数：
			attrString = variable.getAttribute("A11025").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11025 = "0";
			} else {
				A11025 = attrString;
			}
			if ("3".equals(A01001)) {
				A11025 = "0";
			}
			tempVariable = new VariableBean("A11025", A11025, "N", "EX",
					"准贷记卡近24个月M4的次数");
			attachVariableA.add(tempVariable);

			String A11026 = "";// 准贷记卡近24个月M5的次数：
			attrString = variable.getAttribute("A11026").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11026 = "0";
			} else {
				A11026 = attrString;
			}
			if ("3".equals(A01001)) {
				A11026 = "0";
			}
			tempVariable = new VariableBean("A11026", A11026, "N", "EX",
					"准贷记卡近24个月M5的次数");
			attachVariableA.add(tempVariable);

			String A11027 = "";// 准贷记卡近24个月M6的次数：
			attrString = variable.getAttribute("A11027").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11027 = "0";
			} else {
				A11027 = attrString;
			}
			if ("3".equals(A01001)) {
				A11027 = "0";
			}
			tempVariable = new VariableBean("A11027", A11027, "N", "EX",
					"准贷记卡近24个月M6的次数");
			attachVariableA.add(tempVariable);

			String A11028 = "";// 准贷记卡近24个月M7的次数：
			attrString = variable.getAttribute("A11028").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11028 = "0";
			} else {
				A11028 = attrString;
			}
			if ("3".equals(A01001)) {
				A11028 = "0";
			}
			tempVariable = new VariableBean("A11028", A11028, "N", "EX",
					"准贷记卡近24个月M7的次数");
			attachVariableA.add(tempVariable);

			String A11029 = "";// 准贷记卡近5年前36个月逾期最高持续月数：
			attrString = variable.getAttribute("A11029").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11029 = "0";
			} else {
				A11029 = attrString;
			}
			if ("3".equals(A01001)) {
				A11029 = "0";
			}
			tempVariable = new VariableBean("A11029", A11029, "N", "EX",
					"准贷记卡近5年前36个月逾期最高持续月数");
			attachVariableA.add(tempVariable);

			String A11030 = "";// 准贷记卡当月最高逾期级别：
			attrString = variable.getAttribute("A11030").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A11030 = "0";
			} else {
				if (attrString.indexOf("|") > 0) {
					time = attrString.split("|");
					A11030 = time[0];
				} else {
					A11030 = attrString;
				}
			}
			if ("3".equals(A01001)) {
				A11030 = "0";
			}
			tempVariable = new VariableBean("A11030", A11030, "N", "EX",
					"准贷记卡当月最高逾期级别");
			attachVariableA.add(tempVariable);

			String A12001 = "";// 贷款机构数：
			attrString = variable.getAttribute("A12001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A12001 = "0";
			} else {
				A12001 = attrString;
			}
			tempVariable = new VariableBean("A12001", A12001, "N", "EX",
					"贷款机构数");
			attachVariableA.add(tempVariable);

			String A12002 = "";// 贷款总笔数：
			attrString = variable.getAttribute("A12002").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A12002 = "0";
			} else {
				A12002 = attrString;
			}
			tempVariable = new VariableBean("A12002", A12002, "N", "EX",
					"贷款总笔数");
			attachVariableA.add(tempVariable);

			String A12003 = "";// 贷款非正常账户数：
			attrString = variable.getAttribute("A12003").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A12003 = "0";
			} else {
				A12003 = attrString;
			}
			if ("3".equals(A01001)) {
				A12003 = "0";
			}
			tempVariable = new VariableBean("A12003", A12003, "N", "EX",
					"贷款非正常账户数");
			attachVariableA.add(tempVariable);

			String A12004 = "";// 贷款合同总额：
			attrString = variable.getAttribute("A12004").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A12004 = "0";
			} else {
				A12004 = attrString;
			}
			tempVariable = new VariableBean("A12004", A12004, "N", "EX",
					"贷款合同总额");
			attachVariableA.add(tempVariable);

			String A12005 = "";// 贷款总余额：
			attrString = variable.getAttribute("A12005").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A12005 = "0";
			} else {
				A12005 = attrString;
			}
			tempVariable = new VariableBean("A12005", A12005, "N", "EX",
					"贷款总余额");
			attachVariableA.add(tempVariable);

			String A12006 = "";// 贷款最近6个月平均应还款：
			attrString = variable.getAttribute("A12006").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A12006 = "0";
			} else {
				A12006 = attrString;
			}
			if ("3".equals(A01001)) {
				A12006 = "0";
			}
			tempVariable = new VariableBean("A12006", A12006, "N", "EX",
					"贷款最近6个月平均应还款");
			attachVariableA.add(tempVariable);

			String A12007 = "";// 贷款本月应还款总额：
			attrString = variable.getAttribute("A12007").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A12007 = "0";
			} else {
				A12007 = attrString;
			}
			tempVariable = new VariableBean("A12007", A12007, "N", "EX",
					"贷款本月应还款总额");
			attachVariableA.add(tempVariable);

			String A12008 = "";// 贷款最近连续正常还款期数>=3的本月应还款总额：
			attrString = variable.getAttribute("A12008").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A12008 = "0";
			} else {
				A12008 = attrString;
			}
			tempVariable = new VariableBean("A12008", A12008, "N", "EX",
					"贷款最近连续正常还款期数>=3的本月应还款总额");
			attachVariableA.add(tempVariable);

			String A12009 = "";// 贷款当前逾期总额
			attrString = variable.getAttribute("A12009").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A12009 = "0";
			} else {
				A12009 = attrString;
			}
			tempVariable = new VariableBean("A12009", A12009, "N", "EX",
					"贷款当前逾期总额");
			attachVariableA.add(tempVariable);

			String A12010 = "";// 贷款（31-个人助学贷款）的笔数：
			attrString = variable.getAttribute("A12010").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A12010 = "0";
			} else {
				A12010 = attrString;
			}
			tempVariable = new VariableBean("A12010", A12010, "N", "EX",
					"贷款（31-个人助学贷款）的笔数");
			attachVariableA.add(tempVariable);

			String A13001 = "";// 贷款近3个月M1的次数：
			attrString = variable.getAttribute("A13001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13001 = "0";
			} else {
				A13001 = attrString;
			}
			if ("3".equals(A01001)) {
				A13001 = "0";
			}
			tempVariable = new VariableBean("A13001", A13001, "N", "EX",
					"贷款近3个月M1的次数");
			attachVariableA.add(tempVariable);

			String A13002 = "";// 贷款近3个月M2的次数：
			attrString = variable.getAttribute("A13002").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13002 = "0";
			} else {
				A13002 = attrString;
			}
			if ("3".equals(A01001)) {
				A13002 = "0";
			}
			tempVariable = new VariableBean("A13002", A13002, "N", "EX",
					"贷款近3个月M2的次数");
			attachVariableA.add(tempVariable);

			String A13003 = "";// 贷款近3个月M3的次数：
			attrString = variable.getAttribute("A13003").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13003 = "0";
			} else {
				A13003 = attrString;
			}
			if ("3".equals(A01001)) {
				A13003 = "0";
			}
			tempVariable = new VariableBean("A13003", A13003, "N", "EX",
					"贷款近3个月M3的次数");
			attachVariableA.add(tempVariable);

			String A13004 = "";// 贷款近3个月M4的次数：
			attrString = variable.getAttribute("A13004").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13004 = "0";
			} else {
				A13004 = attrString;
			}
			if ("3".equals(A01001)) {
				A13004 = "0";
			}
			tempVariable = new VariableBean("A13004", A13004, "N", "EX",
					"贷款近3个月M4的次数");
			attachVariableA.add(tempVariable);

			String A13005 = "";// 贷款近3个月M5的次数：
			attrString = variable.getAttribute("A13005").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13005 = "0";
			} else {
				A13005 = attrString;
			}
			if ("3".equals(A01001)) {
				A13005 = "0";
			}
			tempVariable = new VariableBean("A13005", A13005, "N", "EX",
					"贷款近3个月M5的次数");
			attachVariableA.add(tempVariable);

			String A13006 = "";// 贷款近3个月M6的次数：
			attrString = variable.getAttribute("A13006").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13006 = "0";
			} else {
				A13006 = attrString;
			}
			if ("3".equals(A01001)) {
				A13006 = "0";
			}
			tempVariable = new VariableBean("A13006", A13006, "N", "EX",
					"贷款近3个月M6的次数");
			attachVariableA.add(tempVariable);

			String A13007 = "";// 贷款近3个月M7的次数：
			attrString = variable.getAttribute("A13007").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13007 = "0";
			} else {
				A13007 = attrString;
			}
			if ("3".equals(A01001)) {
				A13007 = "0";
			}
			tempVariable = new VariableBean("A13007", A13007, "N", "EX",
					"贷款近3个月M7的次数");
			attachVariableA.add(tempVariable);

			String A13008 = "";// 贷款近6个月M1的次数：
			attrString = variable.getAttribute("A13008").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13008 = "0";
			} else {
				A13008 = attrString;
			}
			if ("3".equals(A01001)) {
				A13008 = "0";
			}
			tempVariable = new VariableBean("A13008", A13008, "N", "EX",
					"贷款近6个月M1的次数");
			attachVariableA.add(tempVariable);

			String A13009 = "";// 贷款近6个月M2的次数：
			attrString = variable.getAttribute("A13009").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13009 = "0";
			} else {
				A13009 = attrString;
			}
			if ("3".equals(A01001)) {
				A13009 = "0";
			}
			tempVariable = new VariableBean("A13009", A13009, "N", "EX",
					"贷款近6个月M2的次数");
			attachVariableA.add(tempVariable);

			String A13010 = "";// 贷款近6个月M3的次数：
			attrString = variable.getAttribute("A13010").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13010 = "0";
			} else {
				A13010 = attrString;
			}
			if ("3".equals(A01001)) {
				A13010 = "0";
			}
			tempVariable = new VariableBean("A13010", A13010, "N", "EX",
					"贷款近6个月M3的次数");
			attachVariableA.add(tempVariable);

			String A13011 = "";// 贷款近6个月M4的次数：
			attrString = variable.getAttribute("A13011").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13011 = "0";
			} else {
				A13011 = attrString;
			}
			if ("3".equals(A01001)) {
				A13011 = "0";
			}
			tempVariable = new VariableBean("A13011", A13011, "N", "EX",
					"贷款近6个月M4的次数");
			attachVariableA.add(tempVariable);

			String A13012 = "";// 贷款近6个月M5的次数：
			attrString = variable.getAttribute("A13012").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13012 = "0";
			} else {
				A13012 = attrString;
			}
			if ("3".equals(A01001)) {
				A13012 = "0";
			}
			tempVariable = new VariableBean("A13012", A13012, "N", "EX",
					"贷款近6个月M5的次数");
			attachVariableA.add(tempVariable);

			String A13013 = "";// 贷款近6个月M6的次数：
			attrString = variable.getAttribute("A13013").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13013 = "0";
			} else {
				A13013 = attrString;
			}
			if ("3".equals(A01001)) {
				A13013 = "0";
			}
			tempVariable = new VariableBean("A13013", A13013, "N", "EX",
					"贷款近6个月M6的次数");
			attachVariableA.add(tempVariable);

			String A13014 = "";// 贷款近6个月M7的次数：
			attrString = variable.getAttribute("A13014").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13014 = "0";
			} else {
				A13014 = attrString;
			}
			if ("3".equals(A01001)) {
				A13014 = "0";
			}
			tempVariable = new VariableBean("A13014", A13014, "N", "EX",
					"贷款近6个月M7的次数");
			attachVariableA.add(tempVariable);

			String A13015 = "";// 贷款近12个月M1的次数：
			attrString = variable.getAttribute("A13015").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13015 = "0";
			} else {
				A13015 = attrString;
			}
			if ("3".equals(A01001)) {
				A13015 = "0";
			}
			tempVariable = new VariableBean("A13015", A13015, "N", "EX",
					"贷款近12个月M1的次数");
			attachVariableA.add(tempVariable);

			String A13016 = "";// 贷款近12个月M2的次数：
			attrString = variable.getAttribute("A13016").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13016 = "0";
			} else {
				A13016 = attrString;
			}
			if ("3".equals(A01001)) {
				A13016 = "0";
			}
			tempVariable = new VariableBean("A13016", A13016, "N", "EX",
					"贷款近12个月M2的次数");
			attachVariableA.add(tempVariable);

			String A13017 = "";// 贷款近12个月M3的次数：
			attrString = variable.getAttribute("A13017").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13017 = "0";
			} else {
				A13017 = attrString;
			}
			if ("3".equals(A01001)) {
				A13017 = "0";
			}
			tempVariable = new VariableBean("A13017", A13017, "N", "EX",
					"贷款近12个月M3的次数");
			attachVariableA.add(tempVariable);

			String A13018 = "";// 贷款近12个月M4的次数：
			attrString = variable.getAttribute("A13018").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13018 = "0";
			} else {
				A13018 = attrString;
			}
			if ("3".equals(A01001)) {
				A13018 = "0";
			}
			tempVariable = new VariableBean("A13018", A13018, "N", "EX",
					"贷款近12个月M4的次数");
			attachVariableA.add(tempVariable);

			String A13019 = "";// 贷款近12个月M5的次数：
			attrString = variable.getAttribute("A13019").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13019 = "0";
			} else {
				A13019 = attrString;
			}
			if ("3".equals(A01001)) {
				A13019 = "0";
			}
			tempVariable = new VariableBean("A13019", A13019, "N", "EX",
					"贷款近12个月M5的次数");
			attachVariableA.add(tempVariable);

			String A13020 = "";// 贷款近12个月M6的次数：
			attrString = variable.getAttribute("A13020").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13020 = "0";
			} else {
				A13020 = attrString;
			}
			if ("3".equals(A01001)) {
				A13020 = "0";
			}
			tempVariable = new VariableBean("A13020", A13020, "N", "EX",
					"贷款近12个月M6的次数");
			attachVariableA.add(tempVariable);

			String A13021 = "";// 贷款近12个月M7的次数：
			attrString = variable.getAttribute("A13021").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13021 = "0";
			} else {
				A13021 = attrString;
			}
			if ("3".equals(A01001)) {
				A13021 = "0";
			}
			tempVariable = new VariableBean("A13021", A13021, "N", "EX",
					"贷款近12个月M7的次数");
			attachVariableA.add(tempVariable);

			String A13022 = "";// 贷款近24个月M1的次数：
			attrString = variable.getAttribute("A13022").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13022 = "0";
			} else {
				A13022 = attrString;
			}
			if ("3".equals(A01001)) {
				A13022 = "0";
			}
			tempVariable = new VariableBean("A13022", A13022, "N", "EX",
					"贷款近24个月M1的次数");
			attachVariableA.add(tempVariable);

			String A13023 = "";// 贷款近24个月M2的次数：
			attrString = variable.getAttribute("A13023").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13023 = "0";
			} else {
				A13023 = attrString;
			}
			if ("3".equals(A01001)) {
				A13023 = "0";
			}
			tempVariable = new VariableBean("A13023", A13023, "N", "EX",
					"贷款近24个月M2的次数");
			attachVariableA.add(tempVariable);

			String A13024 = "";// 贷款近24个月M3的次数：
			attrString = variable.getAttribute("A13024").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13024 = "0";
			} else {
				A13024 = attrString;
			}
			if ("3".equals(A01001)) {
				A13024 = "0";
			}
			tempVariable = new VariableBean("A13024", A13024, "N", "EX",
					"贷款近24个月M3的次数");
			attachVariableA.add(tempVariable);

			String A13025 = "";// 贷款近24个月M4的次数：
			attrString = variable.getAttribute("A13025").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13025 = "0";
			} else {
				A13025 = attrString;
			}
			if ("3".equals(A01001)) {
				A13025 = "0";
			}
			tempVariable = new VariableBean("A13025", A13025, "N", "EX",
					"贷款近24个月M4的次数");
			attachVariableA.add(tempVariable);

			String A13026 = "";// 贷款近24个月M5的次数：
			attrString = variable.getAttribute("A13026").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13026 = "0";
			} else {
				A13026 = attrString;
			}
			if ("3".equals(A01001)) {
				A13026 = "0";
			}
			tempVariable = new VariableBean("A13026", A13026, "N", "EX",
					"贷款近24个月M5的次数");
			attachVariableA.add(tempVariable);

			String A13027 = "";// 贷款近24个月M6的次数：
			attrString = variable.getAttribute("A13027").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13027 = "0";
			} else {
				A13027 = attrString;
			}
			if ("3".equals(A01001)) {
				A13027 = "0";
			}
			tempVariable = new VariableBean("A13027", A13027, "N", "EX",
					"贷款近24个月M6的次数");
			attachVariableA.add(tempVariable);

			String A13028 = "";// 贷款近24个月M7的次数：
			attrString = variable.getAttribute("A13028").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13028 = "0";
			} else {
				A13028 = attrString;
			}
			if ("3".equals(A01001)) {
				A13028 = "0";
			}
			tempVariable = new VariableBean("A13028", A13028, "N", "EX",
					"贷款近24个月M7的次数");
			attachVariableA.add(tempVariable);

			String A13029 = "";// 贷款近5年前36个月逾期最高持续月数：
			attrString = variable.getAttribute("A13029").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13029 = "0";
			} else {
				A13029 = attrString;
			}
			if ("3".equals(A01001)) {
				A13029 = "0";
			}
			tempVariable = new VariableBean("A13029", A13029, "N", "EX",
					"贷款近5年前36个月逾期最高持续月数");
			attachVariableA.add(tempVariable);

			String A13030 = "";// 贷款当月最高逾期级别：
			attrString = variable.getAttribute("A13030").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A13030 = "0";
			} else {
				if (attrString.indexOf("|") > 0) {
					time = attrString.split("|");
					A13030 = time[0];
				} else {
					A13030 = attrString;
				}
			}
			if ("3".equals(A01001)) {
				A13030 = "0";
			}
			tempVariable = new VariableBean("A13030", A13030, "N", "EX",
					"贷款当月最高逾期级别");
			attachVariableA.add(tempVariable);

			String A14001 = "";// 担保贷款五级分类为非正常的总数：
			attrString = variable.getAttribute("A14001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A14001 = "0";
			} else {
				A14001 = attrString;
			}
			if ("3".equals(A01001)) {
				A14001 = "0";
			}
			tempVariable = new VariableBean("A14001", A14001, "N", "EX",
					"担保贷款五级分类为非正常的总数");
			attachVariableA.add(tempVariable);

			String A14002 = "";// 担保贷款五级分类为非正常的累计本金余额：
			attrString = variable.getAttribute("A14002").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A14002 = "0";
			} else {
				A14002 = attrString;
			}
			tempVariable = new VariableBean("A14002", A14002, "N", "EX",
					"担保贷款五级分类为非正常的累计本金余额");
			attachVariableA.add(tempVariable);

			String A15001 = "";// 最近1个月内的查询机构数-贷款审批：
			attrString = variable.getAttribute("A15001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A15001 = "0";
			} else {
				A15001 = attrString;
			}
			if ("3".equals(A01001)) {
				A15001 = "0";
			}
			tempVariable = new VariableBean("A15001", A15001, "N", "EX",
					"最近1个月内的查询机构数-贷款审批");
			attachVariableA.add(tempVariable);

			String A15002 = "";// 最近1个月内的查询机构数-信用卡审批：
			attrString = variable.getAttribute("A15002").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A15002 = "0";
			} else {
				A15002 = attrString;
			}
			if ("3".equals(A01001)) {
				A15002 = "0";
			}
			tempVariable = new VariableBean("A15002", A15002, "N", "EX",
					"最近1个月内的查询机构数-信用卡审批");
			attachVariableA.add(tempVariable);

			String A15003 = "";// 最近1个月内的查询次数-贷款审批：
			attrString = variable.getAttribute("A15003").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A15003 = "0";
			} else {
				A15003 = attrString;
			}
			if ("3".equals(A01001)) {
				A15003 = "0";
			}
			tempVariable = new VariableBean("A15003", A15003, "N", "EX",
					"最近1个月内的查询次数-贷款审批");
			attachVariableA.add(tempVariable);

			String A15004 = "";// 最近1个月内的查询次数-信用卡审批：
			attrString = variable.getAttribute("A15004").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A15004 = "0";
			} else {
				A15004 = attrString;
			}
			if ("3".equals(A01001)) {
				A15004 = "0";
			}
			tempVariable = new VariableBean("A15004", A15004, "N", "EX",
					"最近1个月内的查询次数-信用卡审批");
			attachVariableA.add(tempVariable);

			String A15005 = "";// 最近2年内的查询次数-贷后管理：
			attrString = variable.getAttribute("A15005").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A15005 = "0";
			} else {
				A15005 = attrString;
			}
			tempVariable = new VariableBean("A15005", A15005, "N", "EX",
					"最近2年内的查询次数-贷后管理");
			attachVariableA.add(tempVariable);

			String A15006 = "";// 最近2年内的查询次数-担保资格审查：
			attrString = variable.getAttribute("A15006").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A15006 = "0";
			} else {
				A15006 = attrString;
			}
			tempVariable = new VariableBean("A15006", A15006, "N", "EX",
					"最近2年内的查询次数-担保资格审查");
			attachVariableA.add(tempVariable);

			String A15007 = "";// 最近2年内的查询次数-特约商户实名审查：
			attrString = variable.getAttribute("A15007").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A15007 = "0";
			} else {
				A15007 = attrString;
			}
			tempVariable = new VariableBean("A15007", A15007, "N", "EX",
					"最近2年内的查询次数-特约商户实名审查");
			attachVariableA.add(tempVariable);
			String AT3220 = "11";
			// attrString = variable.getAttribute("CredIncome1").trim();
			attrString = variable.getAttribute("AT3220").trim();
			if (attrString != null && !"".equals(attrString)) {
				AT3220 = attrString;
			}
			tempVariable = new VariableBean("AT3220", AT3220, "N", "EX",
					"可证明的收入类型1");
			attachVariableA.add(tempVariable);

			String AT3230 = "0";
			attrString = variable.getAttribute("AT3230").trim();
			// attrString = variable.getAttribute("CredIncome1Value").trim();
			if (attrString != null && !"".equals(attrString)) {
				AT3230 = attrString;
			}
			tempVariable = new VariableBean("AT3230", AT3230, "N", "EX",
					"可证明的收入类型值1");
			attachVariableA.add(tempVariable);
			// log.appendParameter("AT3290", AT3290);
			String AT3240 = "11";
			attrString = variable.getAttribute("AT3240").trim();
			// attrString = variable.getAttribute("CredIncome2").trim();
			if (attrString != null && !"".equals(attrString)) {
				AT3240 = attrString;
			}
			tempVariable = new VariableBean("AT3240", AT3240, "N", "EX",
					"可证明的收入类型2");
			attachVariableA.add(tempVariable);

			String AT3250 = "0";
			attrString = variable.getAttribute("AT3250").trim();
			// attrString = variable.getAttribute("CredIncome2Value").trim();
			if (attrString != null && !"".equals(attrString)) {
				AT3250 = attrString;
			}
			tempVariable = new VariableBean("AT3250", AT3250, "N", "EX",
					"可证明的收入类型值2");
			attachVariableA.add(tempVariable);
			double num1 = 0, num2 = 0;
			String A15012 = "";// 银行代发工资、存折
			if ("11".equals(AT3220)) {
				num1 = Double.parseDouble(AT3230);
			}
			if ("11".equals(AT3240)) {
				num2 = Double.parseDouble(AT3250);
				;
			}
			if (num1 >= num2) {
				A15012 = String.valueOf(num1);
			} else {
				A15012 = String.valueOf(num2);
			}
			tempVariable = new VariableBean("A15012", A15012, "N", "EX",
					"银行代发工资、存折");
			attachVariableA.add(tempVariable);

			String A15013 = "";// 账单月收入
			attrString = variable.getAttribute("A15013").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A15013 = "0";
			} else {
				A15013 = attrString;
			}
			tempVariable = new VariableBean("A15013", A15013, "N", "EX",
					"账单月收入");
			attachVariableA.add(tempVariable);

			num1 = 0;
			num2 = 0;
			String A15015 = "";// 可证明收入数值其他年收入
			if ("18".equals(AT3220)) {
				num1 = Double.parseDouble(AT3230);
			}
			if ("18".equals(AT3240)) {
				num2 = Double.parseDouble(AT3250);
			}
			if (num1 >= num2) {
				A15015 = String.valueOf(num1);
			} else {
				A15015 = String.valueOf(num2);
			}
			tempVariable = new VariableBean("A15015", A15015, "N", "EX",
					"可证明收入数值其他年收入");
			attachVariableA.add(tempVariable);

			// log.appendParameter("AT0700", AT0700);
			String A15016 = "";// 税前月收入
			A15016 = AT0700;
			tempVariable = new VariableBean("A15016", A15016, "N", "EX",
					"税前月收入");
			attachVariableA.add(tempVariable);

			num1 = 0;
			num2 = 0;
			String A15017 = "";// 知名企业月收入
			if ("15".equals(AT3220)) {
				num1 = Double.parseDouble(AT3230);
			}
			if ("15".equals(AT3240)) {
				num2 = Double.parseDouble(AT3250);
			}
			if (num1 >= num2) {
				A15017 = String.valueOf(num1);
			} else {
				A15017 = String.valueOf(num2);
			}
			tempVariable = new VariableBean("A15017", A15017, "N", "EX",
					"知名企业月收入");
			attachVariableA.add(tempVariable);

			num1 = 0;
			num2 = 0;
			String A15018 = "";// 政府或事业单位月收入
			if ("16".equals(AT3220)) {
				num1 = Double.parseDouble(AT3230);
			}
			if ("16".equals(AT3240)) {
				num2 = Double.parseDouble(AT3250);
			}
			if (num1 >= num2) {
				A15018 = String.valueOf(num1);
			} else {
				A15018 = String.valueOf(num2);
			}
			tempVariable = new VariableBean("A15018", A15018, "N", "EX",
					"政府或事业单位月收入");
			attachVariableA.add(tempVariable);

			String A12011 = "";// 房产价值
			attrString = variable.getAttribute("A12011").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A12011 = "0";
			} else {
				A12011 = attrString;
			}
			tempVariable = new VariableBean("A12011", A12011, "N", "EX", "房产价值");
			attachVariableA.add(tempVariable);

			String A15033 = "";// 发卡账户数
			attrString = variable.getAttribute("A15033").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A15033 = "0";
			} else {
				A15033 = attrString;
			}
			tempVariable = new VariableBean("A15033", A15033, "N", "EX",
					"发卡账户数");
			attachVariableA.add(tempVariable);

			String AT4300 = "";// VIP等级
			attrString = variable.getAttribute("AT4300").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4300 = "6";
			} else {
				if ("1".equals(attrString)) {
					AT4300 = "1";
				} else if ("2".equals(attrString)) {
					AT4300 = "2";
				} else if ("5".equals(attrString)) {
					AT4300 = "3";
				} else if ("6".equals(attrString)) {
					AT4300 = "4";
				} else if ("C".equalsIgnoreCase(attrString)) {
					AT4300 = "5";
				} else {
					AT4300 = "6";
				}
			}
			tempVariable = new VariableBean("AT4300", AT4300, "N", "EX",
					"VIP等级");
			attachVariableA.add(tempVariable);

			String AT4310 = "";// 是否为公安检测出4类人员
			attrString = variable.getAttribute("AT4310").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4310 = "2";
			} else {
				/**C1507封版后，统一改为判断1和Y*/
				if ("Y".equalsIgnoreCase(attrString) || "1".equalsIgnoreCase(attrString)) {
					AT4310 = "1";
				} else {
					AT4310 = "2";
				}
			}
			if ("3".equals(A01001)) {
				AT4310 = "2";
			}
			tempVariable = new VariableBean("AT4310", AT4310, "N", "EX",
					"是否为公安检测出4类人员");
			attachVariableA.add(tempVariable);

			String AT4320 = "";// 所属机构号(一级分行)
			attrString = variable.getAttribute("AT4320").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4320 = "-999";
			} else {
				AT4320 = attrString;
			}
			tempVariable = new VariableBean("AT4320", AT4320, "N", "EX",
					"所属机构号");
			attachVariableA.add(tempVariable);

			String A08015 = "";// 符合要求贷记卡中信用额度最大值
			attrString = variable.getAttribute("A08015").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08015 = "0";
			} else {
				A08015 = attrString;
			}
			tempVariable = new VariableBean("A08015", A08015, "N", "EX",
					"符合要求贷记卡中信用额度最大值");
			attachVariableA.add(tempVariable);

			String A10015 = "";// 符合要求准贷记卡中信用额度最大值
			attrString = variable.getAttribute("A10015").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10015 = "0";
			} else {
				A10015 = attrString;
			}
			tempVariable = new VariableBean("A10015", A10015, "N", "EX",
					"符合要求准贷记卡中信用额度最大值");
			attachVariableA.add(tempVariable);

			String A08016 = "";// 贷记卡逾期-最长逾期月数
			attrString = variable.getAttribute("A08016").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A08016 = "0";
			} else {
				A08016 = attrString;
			}
			tempVariable = new VariableBean("A08016", A08016, "N", "EX",
					"贷记卡逾期-最长逾期月数");
			attachVariableA.add(tempVariable);

			String A10016 = "";// 准贷记卡60天以上透支-最长透支月数
			attrString = variable.getAttribute("A10016").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A10016 = "0";
			} else {
				A10016 = attrString;
			}
			tempVariable = new VariableBean("A10016", A10016, "N", "EX",
					"准贷记卡60天以上透支-最长透支月数");
			attachVariableA.add(tempVariable);

			String A12012 = "";// 贷款逾期-最长逾期月数
			attrString = variable.getAttribute("A12012").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				A12012 = "0";
			} else {
				A12012 = attrString;
			}
			tempVariable = new VariableBean("A12012", A12012, "N", "EX",
					"贷款逾期-最长逾期月数");
			attachVariableA.add(tempVariable);

			String AT4330 = "";// 要客分类
			attrString = variable.getAttribute("AT4330").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4330 = "5";
			} else {
				if ("A".equalsIgnoreCase(attrString)) {
					AT4330 = "1";
				} else if ("B".equalsIgnoreCase(attrString)) {
					AT4330 = "2";
				} else if ("C".equalsIgnoreCase(attrString)) {
					AT4330 = "3";
				} else if ("D".equalsIgnoreCase(attrString)) {
					AT4330 = "4";
				} else {
					AT4330 = "5";
				}
			}
			tempVariable = new VariableBean("AT4330", AT4330, "N", "EX", "要客分类");
			attachVariableA.add(tempVariable);
			/**
			 * 需求：临增、长增变量
			 * */
			String AT4410 = "";// 工单产品是否专用卡：
			attrString = variable.getAttribute("AT4410").trim(); // 获取XML中A04001变量的值
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4410 = "-999";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT4410 = "1";
				} else {
					AT4410 = "2";
				}
			}
			tempVariable = new VariableBean("AT4410", AT4410, "N", "EX",
					"工单产品是否专用卡");
			attachVariableA.add(tempVariable);

			String AT4420 = "";// 清偿方式：
			attrString = variable.getAttribute("AT4420").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4420 = "-999";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT4420 = "1";
				} else {
					AT4420 = "2";
				}
			}
			tempVariable = new VariableBean("AT4420", AT4420, "N", "EX", "清偿方式");
			attachVariableA.add(tempVariable);

			String AT4421 = "";// 公司组织机构代码号是否存在于支持临增公司名单中：
			attrString = variable.getAttribute("AT4421").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				if ("3".equals(variable.getAttribute("AT5620").trim())
						&& "2".equals(status)
						&& ("1".equalsIgnoreCase(variable
								.getAttribute("AT4420").trim()) || "Y"
								.equalsIgnoreCase(variable.getAttribute(
										"AT4420").trim()))) {
					AT4421 = "1";
				} else {
					AT4421 = "1";
				}
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT4421 = "1";
				} else {
					AT4421 = "2";
				}
			}
			tempVariable = new VariableBean("AT4421", AT4421, "N", "EX",
					"公司组织机构代码号是否存在于支持临增公司名单中");
			attachVariableA.add(tempVariable);

			String AT4422 = "";// 公司组织机构代码号是否存在于支持长增公司名单中：
			attrString = variable.getAttribute("AT4422").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				if ("3".equals(variable.getAttribute("AT5620").trim())
						&& "3".equals(status)
						&& ("1".equalsIgnoreCase(variable
								.getAttribute("AT4420").trim()) || "Y"
								.equalsIgnoreCase(variable.getAttribute(
										"AT4420").trim()))) {
					AT4422 = "1";
				} else {
					AT4422 = "1";
				}
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT4422 = "1";
				} else {
					AT4422 = "2";
				}
			}
			tempVariable = new VariableBean("AT4422", AT4422, "N", "EX",
					"公司组织机构代码号是否存在于支持长增公司名单中");
			attachVariableA.add(tempVariable);

			String AT4430 = "";// 是否为睡眠帐户：
			attrString = variable.getAttribute("AT4430").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4430 = "-999";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT4430 = "1";
				} else {
					AT4430 = "2";
				}
			}
			tempVariable = new VariableBean("AT4430", AT4430, "N", "EX",
					"是否为睡眠帐户");
			attachVariableA.add(tempVariable);

			String AT4450 = "";// 客户证件类型：
			attrString = variable.getAttribute("AT4450").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4450 = "-999";
			} else {
				if ("I".equalsIgnoreCase(attrString)) {
					AT4450 = "6";
				} else if ("J".equalsIgnoreCase(attrString)) {
					AT4450 = "7";
				} else {
					AT4450 = attrString;
				}
			}
			tempVariable = new VariableBean("AT4450", AT4450, "N", "EX",
					"客户证件类型");
			attachVariableA.add(tempVariable);

			String AT4750 = "";// 申请卡币种：
			attrString = variable.getAttribute("AT4750").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4750 = "-999";
			} else {
				if ("156".equalsIgnoreCase(attrString)) {
					AT4750 = "1";// 人民币
				} else {
					AT4750 = "2";// 非人民币为2
				}
			}
			tempVariable = new VariableBean("AT4750", AT4750, "N", "EX",
					"申请卡币种");
			attachVariableA.add(tempVariable);
			// Double A4760 = Double.parseDouble(AT4760);
			// Double A4750 = 0.0;//工单产品的当前永久额度（产品层）(工单产品为主卡的当前额度)
			// Double A6020 = 0.0;//客户所有名下有效卡的最高永久额度
			// Double A6030 = 0.0;//工单卡片子级CC01当前永久额度
			// if(!"0".equalsIgnoreCase(AT4440)){
			// A4750 = Double.parseDouble(AT4440)*A4760;
			// }
			// AT4440 = A4750.toString();
			// if(!"0".equalsIgnoreCase(AT6020)){
			// A6020 = Double.parseDouble(AT6020)*A4760;
			// }
			// AT6020 = A6020.toString();
			// if(!"0".equalsIgnoreCase(AT6030)){
			// A6030 = Double.parseDouble(AT6030)*A4760;
			// }
			// AT6030 = A6030.toString();

			String AT4760 = "";// 申请卡汇率：
			Double B4760 = 0.0;
			attrString = variable.getAttribute("AT4760").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4760 = "-999";
			} else {
				if (attrString.indexOf("/") != -1) {
					String[] A4760 = attrString.split("/");
					B4760 = Double.parseDouble(A4760[0])
							/ Double.parseDouble(A4760[1]);
					AT4760 = B4760.toString();
				} else {
					AT4760 = attrString.toString();
				}
			}
			tempVariable = new VariableBean("AT4760", AT4760, "N", "EX",
					"申请卡汇率");
			attachVariableA.add(tempVariable);
			// attachVariableA.add(tempVariable);
			// tempVariable = new VariableBean("AT4440", AT4440, "N", "EX",
			// "工单产品的当前永久额度（产品层）(工单产品为主卡的当前额度)");
			// attachVariableA.add(tempVariable);
			// tempVariable = new VariableBean("AT6020", AT6020, "N", "EX",
			// "客户所有名下有效卡的最高永久额度");
			// attachVariableA.add(tempVariable);
			// tempVariable = new VariableBean("AT6030", AT6030, "N", "EX",
			// "工单卡片子级CC01当前永久额度");
			// attachVariableA.add(tempVariable);

			String AT4440 = "";// 工单产品的当前永久额度（产品层）(工单产品为主卡的当前额度)：
			attrString = variable.getAttribute("AT4440").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4440 = "-999";
			} else {
				AT4440 = attrString;
				// if(!"1".equalsIgnoreCase(AT4750)){
				// Double A4760 = Double.parseDouble(AT4760);
				// Double A4440 = Double.parseDouble(AT4440)*A4760;
				// AT4440 = A4440.toString();
				// }

			}
			tempVariable = new VariableBean("AT4440", AT4440, "N", "EX",
					"工单产品的当前永久额度（产品层）(工单产品为主卡的当前额度)");
			attachVariableA.add(tempVariable);

			String AT4470 = "";// 近半年内客户顶层永久额度提升次数：
			attrString = variable.getAttribute("AT4470").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				if ("3".equals(status)) {
					AT4470 = "-999";
				} else {
					AT4470 = "0";
				}
			} else {
				AT4470 = attrString;
			}
			tempVariable = new VariableBean("AT4470", AT4470, "N", "EX",
					"近半年内客户顶层永久额度提升次数");
			attachVariableA.add(tempVariable);

			String AT4451 = "";// 客户身份证号码：
			attrString = variable.getAttribute("AT4451").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4451 = "0";
			} else {
				AT4451 = attrString;
			}
			// tempVariable = new VariableBean("AT4451", AT4451, "N", "EX",
			// "客户身份证号码");
			// attachVariableA.add(tempVariable);

			String AT4460 = "";// 客户出生日期：
			attrString = variable.getAttribute("AT4460").trim();
			if (attrString.length() >= 10) {
				attrString = this.changeDataFormat(attrString);
			}
			if (this.isDate(attrString)) {
				AT4460 = attrString;
			} else {
				AT4460 = "-999";
			}
			tempVariable = new VariableBean("AT4460", AT4460, "N", "EX",
					"客户出生日期");
			attachVariableA.add(tempVariable);

			// String AT4480 = "";//工单产品是否存在于NUW“不取用额度产品名单”：
			// attrString = variable.getAttribute("AT4480").trim();
			// if (null==attrString || "-999".equals(attrString) ||
			// "".equals(attrString) || "null".equalsIgnoreCase(attrString)) {
			// AT4480 = "2";
			// }else{
			// if("1".equalsIgnoreCase(attrString)){
			// AT4480 = "1";
			// }else{
			// AT4480 = "2";
			// }
			// }
			// tempVariable = new VariableBean("AT4480", AT4480, "N", "EX",
			// "工单产品是否存在于NUW“不取用额度产品名单”");
			// attachVariableA.add(tempVariable);

			String AT4530 = "";// 近半年内客户顶层临时额度提升次数：
			attrString = variable.getAttribute("AT4530").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				if ("2".equals(status)) {
					AT4530 = "-999";
				} else {
					AT4530 = "0";
				}
			} else {
				AT4530 = attrString;
			}
			tempVariable = new VariableBean("AT4530", AT4530, "N", "EX",
					"近半年内客户顶层临时额度提升次数");
			attachVariableA.add(tempVariable);

			String AT4550 = "";// 全部产品层账户争议交易金额>0则为争议账户：
			attrString = variable.getAttribute("AT4550").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4550 = "-999";
			} else {
				AT4550 = attrString;
				Integer num = 0;
				if (AT4550.contains("，")) {
					AT4550 = AT4550.replace("，", ",");
				}
				if (AT4550.contains(",")) {
					String A4[] = AT4550.split(",");
					for (Integer i = 0; i < A4.length; i++) {
						if (Double.parseDouble(A4[i]) > 0) {
							num = num + 1;
						}
					}
				} else {
					if (Double.parseDouble(AT4550) > 0) {
						AT4550 = "1";
					} else {
						AT4550 = "2";
					}
				}
				if (num >= 1) {
					AT4550 = "1";
				}

			}
			tempVariable = new VariableBean("AT4550", AT4550, "N", "EX",
					"是否争议账户");
			attachVariableA.add(tempVariable);

			String AT4590 = "";// 全部产品层账户过去12个月内逾期M1及以上次数：
			attrString = variable.getAttribute("AT4590").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4590 = "-999";
			} else {
				AT4590 = attrString;
				Integer max = 0;
				if (AT4590.contains("，")) {
					AT4590 = AT4590.replace("，", ",");
				}
				if (AT4590.contains(",")) {
					String A4[] = AT4590.split(",");
					for (Integer i = 0; i < A4.length; i++) {
						if (i == 0) {
							max = Integer.parseInt(A4[i]);
						}
						for (Integer j = i + 1; j < A4.length; j++) {
							if (max <= Integer.parseInt(A4[j])) {
								max = Integer.parseInt(A4[j]);
							}
						}
					}
					AT4590 = max.toString();
				}

			}
			tempVariable = new VariableBean("AT4590", AT4590, "N", "EX",
					"全部产品层账户过去12个月内逾期M1及以上次数");
			attachVariableA.add(tempVariable);

			String AT4600 = "";// 客户是否存在于分行报送增额黑名单：
			attrString = variable.getAttribute("AT4600").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4600 = "-999";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT4600 = "1";
				} else {
					AT4600 = "2";
				}
			}
			tempVariable = new VariableBean("AT4600", AT4600, "N", "EX",
					"客户是否存在于分行报送增额黑名单");
			attachVariableA.add(tempVariable);

			String AT4610 = "";// 客户是否存在于分行报送增额灰名单：
			attrString = variable.getAttribute("AT4610").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4610 = "-999";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT4610 = "1";
				} else {
					AT4610 = "2";
				}
			}
			tempVariable = new VariableBean("AT4610", AT4610, "N", "EX",
					"客户是否存在于分行报送增额灰名单");
			attachVariableA.add(tempVariable);

			// String AT4620 = "";//客户是否被风险降额(客户名下全部卡片包含主附卡)：
			// attrString = variable.getAttribute("AT4620").trim();
			// if (null==attrString || "-999".equals(attrString) ||
			// "".equals(attrString) || "null".equalsIgnoreCase(attrString)) {
			// AT4620 = "2";
			// }else{
			// if("1".equalsIgnoreCase(attrString)||"Y".equalsIgnoreCase(attrString)){
			// AT4620 = "1";
			// }else{
			// AT4620 = "2";
			// }
			// }
			// tempVariable = new VariableBean("AT4620", AT4620, "N", "EX",
			// "客户是否被风险降额(客户名下全部卡片包含主附卡)");
			// attachVariableA.add(tempVariable);

			String AT4630 = "";// 近6个月内，连续未全额还款次数：
			attrString = variable.getAttribute("AT4630").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4630 = "-999";
			} else {
				AT4630 = attrString;
			}
			tempVariable = new VariableBean("AT4630", AT4630, "N", "EX",
					"近6个月内，连续未全额还款次数");
			attachVariableA.add(tempVariable);

			String AT4640 = "";// 近6个月内，累计未全额还款次数：
			attrString = variable.getAttribute("AT4640").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4640 = "-999";
			} else {
				AT4640 = attrString;
			}
			tempVariable = new VariableBean("AT4640", AT4640, "N", "EX",
					"近6个月内，累计未全额还款次数");
			attachVariableA.add(tempVariable);

			String AT4650 = "";// 近6个月内，最近一期账单是否全额还款：
			attrString = variable.getAttribute("AT4650").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4650 = "-999";
			} else {
				if ("1".equalsIgnoreCase(attrString)) {
					AT4650 = "1";
				} else {
					AT4650 = "2";
				}
			}
			tempVariable = new VariableBean("AT4650", AT4650, "N", "EX",
					"近6个月内，最近一期账单是否全额还款");
			attachVariableA.add(tempVariable);

			String AT4660 = "";// 近6个月内，M1次数：
			attrString = variable.getAttribute("AT4660").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4660 = "-999";
			} else {
				AT4660 = attrString;
			}
			tempVariable = new VariableBean("AT4660", AT4660, "N", "EX",
					"近6个月内，M1次数");
			attachVariableA.add(tempVariable);

			String AT4670 = "";// 卡龄：
			attrString = variable.getAttribute("AT4670").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4670 = "-999";
			} else {
				AT4670 = attrString;
			}
			tempVariable = new VariableBean("AT4670", AT4670, "N", "EX", "卡龄");
			attachVariableA.add(tempVariable);

			String AT4680 = "";// 近12个月内，连续未全额还款次数：
			attrString = variable.getAttribute("AT4680").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4680 = "-999";
			} else {
				AT4680 = attrString;
			}
			tempVariable = new VariableBean("AT4680", AT4680, "N", "EX",
					"近12个月内，连续未全额还款次数");
			attachVariableA.add(tempVariable);

			String AT4690 = "";// 近12个月内，累计未全额还款次数：
			attrString = variable.getAttribute("AT4690").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4690 = "-999";
			} else {
				AT4690 = attrString;
			}
			tempVariable = new VariableBean("AT4690", AT4690, "N", "EX",
					"近12个月内，累计未全额还款次数");
			attachVariableA.add(tempVariable);

			String AT4700 = "";// 近12个月内，M1次数：
			attrString = variable.getAttribute("AT4700").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4700 = "-999";
			} else {
				AT4700 = attrString;
			}
			tempVariable = new VariableBean("AT4700", AT4700, "N", "EX",
					"近12个月内，M1次数");
			attachVariableA.add(tempVariable);

			String AT4710 = "";// 近12个月内，M2次数：
			attrString = variable.getAttribute("AT4710").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4710 = "-999";
			} else {
				AT4710 = attrString;
			}
			tempVariable = new VariableBean("AT4710", AT4710, "N", "EX",
					"近12个月内，M2次数");
			attachVariableA.add(tempVariable);

			String AT4720 = "";// 近24个月内，M3及M3+次数：
			attrString = variable.getAttribute("AT4720").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4720 = "-999";
			} else {
				AT4720 = attrString;
			}
			tempVariable = new VariableBean("AT4720", AT4720, "N", "EX",
					"近24个月内，M3及M3+次数");
			attachVariableA.add(tempVariable);

			String AT4724 = "";// 是否年费逾期：
			attrString = variable.getAttribute("AT4724").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4724 = "-999";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT4724 = "1";
				} else {
					AT4724 = "2";
				}
			}
			tempVariable = new VariableBean("AT4724", AT4724, "N", "EX",
					"是否年费逾期");
			attachVariableA.add(tempVariable);

			String AT4730 = "";// 套现规则触发结果：
			attrString = variable.getAttribute("AT4730").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4730 = "2";
			} else {
				AT4730 = attrString;
				if (AT4730.contains("，")) {
					AT4730 = AT4730.replace("，", ",");
				}
				if (AT4730.startsWith(",")) {
					AT4730 = AT4730.substring(1);
				}
				if (AT4730.endsWith(",")) {
					AT4730 = AT4730.substring(0, AT4730.length() - 1);
				}
				if(AT4730.contains(",")){
					String arr[] = AT4730.split(",");
					for(String a:arr){
						System.out.println(a);
						if ("1".equalsIgnoreCase(a)
								|| "Y".equalsIgnoreCase(a)) {
							AT4730= "1";
							break;
						} else {
							AT4730 = "2";
						}
					}
				}else{
					if ("1".equalsIgnoreCase(AT4730)
							|| "Y".equalsIgnoreCase(AT4730)) {
						AT4730= "1";
					} else {
						AT4730 = "2";
					}
				}
			}
			tempVariable = new VariableBean("AT4730", AT4730, "N", "EX",
					"套现规则触发结果");
			attachVariableA.add(tempVariable);

			String AT4740 = "";// 全部产品账户近6个月平均额度使用率：
			attrString = variable.getAttribute("AT4740").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4740 = "-999";
			} else {
				AT4740 = attrString;
				if (AT4740.contains("，")) {
					AT4740 = AT4740.replace("，", ",");
				}
				if (AT4740.startsWith(",")) {
					AT4740 = AT4740.substring(1);
				}
				if (AT4740.endsWith(",")) {
					AT4740 = AT4740.substring(0, AT4740.length() - 1);
				}
				if (AT4740.contains(",")) {
					String arr[] = AT4740.split(",");
					String max = "0";
					if (arr[0].contains("%") || arr[0].contains("%")) {// 存在百分号
						String[] a = arr[0].split("%");
						Double b = Double.parseDouble(a[0]) / 100;
						arr[0] = b.toString();
						if (isNumeric(arr[0])) {
							max = arr[0];
						}
					}
					for (int i = 1; i < arr.length; i++) {
						if (arr[i].contains("%") || arr[i].contains("%")) {// 存在百分号
							String[] a = arr[i].split("%");
							Double b = Double.parseDouble(a[0]) / 100;
							arr[i] = b.toString();
						}
						if (isNumeric(arr[i])) {
							if (Double.parseDouble(arr[i]) > Double.parseDouble(max)) {
								max = arr[i];
							}
						}

					}
					AT4740 = max;
					//System.out.println("最大：" + max);
				} else {
					if (AT4740.contains("%") || AT4740.contains("%")) {// 存在百分号
						String[] a = AT4740.split("%");
						Double b = Double.parseDouble(a[0]) / 100;
						AT4740 = b.toString();
					}
					//System.out.println(AT4740);
				}
			}
			tempVariable = new VariableBean("AT4740", AT4740, "N", "EX",
					"全部产品账户近6个月平均额度使用率");
			attachVariableA.add(tempVariable);

			String AT5600 = "";// 评分等级：
			attrString = variable.getAttribute("AT5600").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT5600 = "-999";
			} else {
				if ("XA".equalsIgnoreCase(attrString)) {
					AT5600 = "1";
				} else if ("XB".equalsIgnoreCase(attrString)) {
					AT5600 = "2";
				} else if ("ZA".equalsIgnoreCase(attrString)
						|| "ZB".equalsIgnoreCase(attrString)
						|| "ZC".equalsIgnoreCase(attrString)) {
					AT5600 = "3";
				} else if ("A".equalsIgnoreCase(attrString)) {
					AT5600 = "4";
				} else if ("B".equalsIgnoreCase(attrString)) {
					AT5600 = "5";
				} else if ("C".equalsIgnoreCase(attrString)) {
					AT5600 = "6";
				} else if ("D".equalsIgnoreCase(attrString)) {
					AT5600 = "7";
				} else if ("E".equalsIgnoreCase(attrString)) {
					AT5600 = "8";
				} else if ("XC".equalsIgnoreCase(attrString)) {
					AT5600 = "9";
				} else if ("Y".equalsIgnoreCase(attrString)) {
					AT5600 = "10";
				} else {
					AT5600 = attrString;
				}

			}
			tempVariable = new VariableBean("AT5600", AT5600, "N", "EX", "评分等级");
			attachVariableA.add(tempVariable);

			String AT5601 = "";// 预设额度：
			attrString = variable.getAttribute("AT5601").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT5601 = "-999";
			} else {
				AT5601 = attrString;
			}
			tempVariable = new VariableBean("AT5601", AT5601, "N", "EX", "预设额度");
			attachVariableA.add(tempVariable);

			String AT5602 = "";// 渠道系统标识：
			attrString = variable.getAttribute("AT5602").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT5602 = "-999"; // 总行NUW
			} else {
				if ("01".equalsIgnoreCase(attrString)) {
					AT5602 = "1"; // 总行NUW
				} else if ("02".equalsIgnoreCase(attrString)) {
					AT5602 = "2"; // 总行CSR
				} else if ("03".equalsIgnoreCase(attrString)) {
					AT5602 = "3"; // 总行CRM
				} else if ("04".equalsIgnoreCase(attrString)) {
					AT5602 = "4"; // 分行落地终端
				} else if ("05".equalsIgnoreCase(attrString)) {
					AT5602 = "5"; // 网银
				} else if ("06".equalsIgnoreCase(attrString)) {
					AT5602 = "6"; // 微信
				} else if ("07".equalsIgnoreCase(attrString)) {
					AT5602 = "7"; // 财富OCRM
				} else {
					AT5602 = attrString;
				}

			}
			tempVariable = new VariableBean("AT5602", AT5602, "N", "EX",
					"渠道系统标识");
			attachVariableA.add(tempVariable);

			// String AT5604 = "";//客户类型标识：
			// attrString = variable.getAttribute("AT5604").trim();
			// if (null==attrString || "-999".equals(attrString) ||
			// "".equals(attrString) || "null".equalsIgnoreCase(attrString)) {
			// AT5604 = "3"; //新客户
			// }else{
			// if("01".equalsIgnoreCase(attrString)){
			// AT5604 = "1"; //已持卡老客户
			// }else if("02".equalsIgnoreCase(attrString)){
			// AT5604 = "2"; //未持卡老客户
			// }else if("03".equalsIgnoreCase(attrString)){
			// AT5604 = "3"; //新客户
			// }
			//
			// }
			// tempVariable = new VariableBean("AT5604", AT5604, "N", "EX",
			// "客户类型标识");
			// attachVariableA.add(tempVariable);

			String AT5605 = "";// 任务类型标识：
			attrString = variable.getAttribute("AT5605").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT5605 = "-999"; // 总行NUW
			} else {
				if ("01".equalsIgnoreCase(attrString)) {
					AT5605 = "1"; // 01申请评分
				} else if ("02".equalsIgnoreCase(attrString)) {
					AT5605 = "2"; // 02临增
				} else if ("03".equalsIgnoreCase(attrString)) {
					AT5605 = "3"; // 03长增
				} else if ("04".equalsIgnoreCase(attrString)) {
					AT5605 = "4"; // 04事件式增额
				} else if ("05".equalsIgnoreCase(attrString)) {
					AT5605 = "5"; // 05批量增额
				} else if ("06".equalsIgnoreCase(attrString)) {
					AT5605 = "6"; // 06风险预警
				} else {
					AT5605 = attrString;
				}

			}
			tempVariable = new VariableBean("AT5605", AT5605, "N", "EX",
					"任务类型标识");
			attachVariableA.add(tempVariable);

			// String AT5606 = "";//客户预设额度类型：
			// attrString = variable.getAttribute("AT5606").trim();
			// if (null==attrString || "-999".equals(attrString) ||
			// "".equals(attrString) || "null".equalsIgnoreCase(attrString)) {
			// AT5606 = "1";
			// }else{
			// if("1".equalsIgnoreCase(attrString)||"Y".equalsIgnoreCase(attrString)){
			// AT5606 = "1";
			// }else{
			// AT5606 = "2";
			// }
			// }
			// tempVariable = new VariableBean("AT5606", AT5606, "N", "EX",
			// "客户预设额度类型");
			// attachVariableA.add(tempVariable);

			String AT5607 = "";// 所属分行临增上限：
			attrString = variable.getAttribute("AT5607").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				if ("2".equals(status)) {
					AT5607 = "-999";
				} else {
					AT5607 = "0";
				}
			} else {
				AT5607 = attrString;
			}
			tempVariable = new VariableBean("AT5607", AT5607, "N", "EX",
					"所属分行临增上限");
			attachVariableA.add(tempVariable);

			String AT5608 = "";// 所属分行长增上限：
			attrString = variable.getAttribute("AT5608").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				if ("3".equals(status)) {
					AT5608 = "-999";
				} else {
					AT5608 = "0";
				}
			} else {
				AT5608 = attrString;
			}
			tempVariable = new VariableBean("AT5608", AT5608, "N", "EX",
					"所属分行长增上限");
			attachVariableA.add(tempVariable);

			String AT5609 = "";// 公司组织机构代码号：
			attrString = variable.getAttribute("AT5609").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT5609 = "0";
			} else {
				AT5609 = attrString;
			}
			tempVariable = new VariableBean("AT5609", AT5609, "N", "EX",
					"公司组织机构代码号");
			attachVariableA.add(tempVariable);

			String AT5610 = "";// 公务卡临增上限：
			attrString = variable.getAttribute("AT5610").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT5610 = "0";
			} else {
				AT5610 = attrString;
			}
			tempVariable = new VariableBean("AT5610", AT5610, "N", "EX",
					"公务卡临增上限");
			attachVariableA.add(tempVariable);

			String AT5611 = "";// 公务卡长增上限：
			attrString = variable.getAttribute("AT5611").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT5611 = "0";
			} else {
				AT5611 = attrString;
			}
			tempVariable = new VariableBean("AT5611", AT5611, "N", "EX",
					"公务卡长增上限");
			attachVariableA.add(tempVariable);

			String AT5612 = "";// 客户近三个月行内资产余额：
			attrString = variable.getAttribute("AT5612").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT5612 = "0";
			} else {
				AT5612 = attrString;
			}
			tempVariable = new VariableBean("AT5612", AT5612, "N", "EX",
					"客户近三个月行内资产余额");
			attachVariableA.add(tempVariable);

			String AT5620 = "";// 产品大类：
			attrString = variable.getAttribute("AT5620").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT5620 = "-999"; // 个人
			} else {
				if ("1".equalsIgnoreCase(attrString)) {
					AT5620 = "1"; // 个人
				} else if ("2".equalsIgnoreCase(attrString)) {
					AT5620 = "2"; // 高端
				} else if ("3".equalsIgnoreCase(attrString)) {
					AT5620 = "3"; // 公务卡
				}

			}
			tempVariable = new VariableBean("AT5620", AT5620, "N", "EX", "产品大类");
			attachVariableA.add(tempVariable);

			String AT5630 = "";// 期望额度：
			attrString = variable.getAttribute("AT5630").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT5630 = "-999";
			} else {
				AT5630 = attrString;
				if (!"1".equalsIgnoreCase(AT4750)) {
					Double A4760 = Double.parseDouble(AT4760);
					Double A5630 = Double.parseDouble(AT5630) * A4760;
					AT5630 = A5630.toString();
				}
			}
			tempVariable = new VariableBean("AT5630", AT5630, "N", "EX", "期望额度");
			attachVariableA.add(tempVariable);

			String AT5640 = "";// 是否接受低于期望额度：
			attrString = variable.getAttribute("AT5640").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT5640 = "-999";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT5640 = "1";
				} else {
					AT5640 = "2";
				}
			}
			tempVariable = new VariableBean("AT5640", AT5640, "N", "EX",
					"是否接受低于期望额度");
			attachVariableA.add(tempVariable);

			// String AT6000 = "";//是否白金卡：
			// attrString = variable.getAttribute("AT6000").trim();
			// if (null==attrString || "-999".equals(attrString) ||
			// "".equals(attrString) || "null".equalsIgnoreCase(attrString)) {
			// AT6000 = "1";
			// }else{
			// if("1".equalsIgnoreCase(attrString)){
			// AT6000 = "1";
			// }else{
			// AT6000 = "2";
			// }
			// }
			// tempVariable = new VariableBean("AT6000", AT6000, "N", "EX",
			// "是否白金卡");
			// attachVariableA.add(tempVariable);
			//
			// String AT6010 = "";//是否为美运卡：
			// attrString = variable.getAttribute("AT6010").trim();
			// if (null==attrString || "-999".equals(attrString) ||
			// "".equals(attrString) || "null".equalsIgnoreCase(attrString)) {
			// AT6010 = "1";
			// }else{
			// if("1".equalsIgnoreCase(attrString)){
			// AT6010 = "1";
			// }else{
			// AT6010 = "2";
			// }
			// }
			// tempVariable = new VariableBean("AT6010", AT6010, "N", "EX",
			// "是否为美运卡");
			// attachVariableA.add(tempVariable);

			String AT6020 = "";// 客户所有名下有效卡的最高永久额度：
			attrString = variable.getAttribute("AT6020").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT6020 = "-999";
			} else {
				AT6020 = attrString;
				// if(!"1".equalsIgnoreCase(AT4750)){
				// Double A4760 = Double.parseDouble(AT4760);
				// Double A6020 = Double.parseDouble(AT6020)*A4760;
				// AT6020 = A6020.toString();
				// }
			}
			tempVariable = new VariableBean("AT6020", AT6020, "N", "EX",
					"客户所有名下有效卡的最高永久额度");
			attachVariableA.add(tempVariable);

			String AT6030 = "";// 工单卡片子级CC01当前永久额度：
			attrString = variable.getAttribute("AT6030").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT6030 = "-999";
			} else {
				AT6030 = attrString;
				// if(!"1".equalsIgnoreCase(AT4750)){
				// Double A4760 = Double.parseDouble(AT4760);
				// Double A6030 = Double.parseDouble(AT6030)*A4760;
				// AT6030 = A6030.toString();
				// }
			}
			tempVariable = new VariableBean("AT6030", AT6030, "N", "EX",
					"工单卡片子级CC01当前永久额度");
			attachVariableA.add(tempVariable);

			String AT6040 = "";// 是否跨境交易客户：
			attrString = variable.getAttribute("AT6040").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT6040 = "-999";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT6040 = "1";
				} else {
					AT6040 = "2";
				}
			}
			tempVariable = new VariableBean("AT6040", AT6040, "N", "EX",
					"是否跨境交易客户");
			attachVariableA.add(tempVariable);

			String AT6050 = "";// 预设额度更新渠道：
			attrString = variable.getAttribute("AT6050").trim();
			String[] A6050 = new String[] { "1", "2", "3", "4", "5","6","NA","X" };
			if("NA".equalsIgnoreCase(attrString) || "X".equalsIgnoreCase(attrString)){
				AT6050 = "7";
			}else if (this.verifyValue(attrString, A6050)) {
				AT6050 = attrString;
			}else {
				AT6050 = "7";
			}
			tempVariable = new VariableBean("AT6050", AT6050, "N", "EX",
					"预设额度更新渠道");
			attachVariableA.add(tempVariable);

			String AT5650 = "";// 年龄 长增、临增 ：
			int Age5650 = 0;
			if (null == AT4460 || "-999".equals(AT4460) || "".equals(AT4460)
					|| "null".equalsIgnoreCase(AT4460)) {
				AT5650 = "-999";
			} else {
				Age5650 = this.getAge(AT4460);
				AT5650 = Integer.toString(Age5650);
			}
			// if(null==AT4451 || "-999".equals(AT4451) || "".equals(AT4451) ||
			// "null".equalsIgnoreCase(AT4451) || "0".equals(AT4451)){
			// Age5650 = this.getAge(AT4460);
			// AT5650 = Integer.toString(Age5650);
			// }else{
			// Age5650 = this.getAge(AT4451.substring(6, 14));
			// AT5650 = Integer.toString(Age5650);
			// }
			tempVariable = new VariableBean("AT5650", AT5650, "N", "EX", "年龄");
			attachVariableA.add(tempVariable);

			String AT4721 = "";// 根据状态判断账户状态:1正常2欺诈3坏账4核销5逾期6未激活
			String AT6060 = "";// 账户状态是否为欺诈
			String AT6070 = "";// 账户状态是否为坏账
			String AT6080 = "";// 账户状态是否为核销
			String AT6090 = "";// 账户状态是否为逾期
			String AT9003 = "";// 账户状态是否为未激活
			attrString = variable.getAttribute("AT4721").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4721 = "-999"; // 需确认
				AT6060 = "-999";
				AT6070 = "-999";
				AT6080 = "-999";
				AT6090 = "-999";
				AT9003 = "-999";
			} else {
				AT4721 = attrString;
				if (AT4721.contains("，")) {
					AT4721 = AT4721.replace("，", ",");
				}
				if (AT4721.contains(",")) {
					String A4[] = AT4721.split(",");
					for (Integer a = 0; a < A4.length; a++) {
						if ("ATPD".equalsIgnoreCase(A4[a])) {
							AT9003 = "1";
						} else if ("FRCC".equalsIgnoreCase(A4[a])) {
							AT6060 = "1";
						} else if ("FRAU".equalsIgnoreCase(A4[a])) {
							AT6060 = "1";
						} else if ("INBL".equalsIgnoreCase(A4[a])) {
							AT6060 = "1";
						} else if ("COCR".equalsIgnoreCase(A4[a])) {
							AT6070 = "1";
						} else if ("DQCO".equalsIgnoreCase(A4[a])) {
							AT6070 = "1";
						} else if ("DQWO".equalsIgnoreCase(A4[a])) {
							// AT6070 = "1";//账户状态是否为坏账
							AT6080 = "1";// 坏账转核销
						} else if ("SBWO".equalsIgnoreCase(A4[a])) {
							// AT6070 = "1";//账户状态是否为坏账
							AT6080 = "1";// 坏账转核销
						} else if ("CAWO".equalsIgnoreCase(A4[a])) {
							AT6080 = "1";//核销
						} else if ("CRAC".equalsIgnoreCase(A4[a])) {
							AT6080 = "1";//核销
						} else if ("PFRA".equalsIgnoreCase(A4[a])) {
							AT6080 = "1";//核销，C1601新增
						} else if ("RFRA".equalsIgnoreCase(A4[a])) {
							AT6080 = "1";//核销，C1601新增
						} else if ("WFRA".equalsIgnoreCase(A4[a])) {
							AT6080 = "1";//核销，C1601新增
						} else if ("DQPD".equalsIgnoreCase(A4[a])) {
							AT6090 = "1";
						} else if ("DQC1".equalsIgnoreCase(A4[a])
								|| "DQC2".equalsIgnoreCase(A4[a])
								|| "DQC3".equalsIgnoreCase(A4[a])
								|| "DQC4".equalsIgnoreCase(A4[a])
								|| "DQC5".equalsIgnoreCase(A4[a])
								|| "DQC6".equalsIgnoreCase(A4[a])
								|| "DQC7".equalsIgnoreCase(A4[a])
								|| "DQC8".equalsIgnoreCase(A4[a])
								|| "DQC9".equalsIgnoreCase(A4[a])) {
							AT6090 = "1";
						}
						// else if(A4[a].contains("DQC") ||A4[a].contains("dqc")
						// && !A4[a].equalsIgnoreCase("DQCO")){
						// AT6090 = "1";
						// }
						else if ("DQ10".equalsIgnoreCase(A4[a])) {
							AT6090 = "1";
						} else if ("DQ11".equalsIgnoreCase(A4[a])) {
							AT6090 = "1";
						} else if ("DQ12".equalsIgnoreCase(A4[a])) {
							AT6090 = "1";
						} else if ("OLC1".equalsIgnoreCase(A4[a])
								|| "OLC2".equalsIgnoreCase(A4[a])
								|| "OLC3".equalsIgnoreCase(A4[a])
								|| "OLC4".equalsIgnoreCase(A4[a])
								|| "OLC5".equalsIgnoreCase(A4[a])
								|| "OLC6".equalsIgnoreCase(A4[a])
								|| "OLC7".equalsIgnoreCase(A4[a])
								|| "OLC8".equalsIgnoreCase(A4[a])
								|| "OLC9".equalsIgnoreCase(A4[a])) {
							AT6090 = "1";
						}
						// else
						// if(A4[a].contains("OLC")||A4[a].contains("olc")){
						// AT6090 = "1";
						// }
						else if ("OC10".equalsIgnoreCase(A4[a])) {
							AT6090 = "1";
						} else if ("OC11".equalsIgnoreCase(A4[a])) {
							AT6090 = "1";
						} else if ("OC12".equalsIgnoreCase(A4[a])) {
							AT6090 = "1";
						} else if ("OLPD".equalsIgnoreCase(A4[a])) {
							AT6090 = "1";
						} else if ("D540".equalsIgnoreCase(A4[a])) {
							AT6090 = "1";
						} else if ("D720".equalsIgnoreCase(A4[a])) {
							AT6090 = "1";
						} else if ("O540".equalsIgnoreCase(A4[a])) {
							AT6090 = "1";
						} else if ("O720".equalsIgnoreCase(A4[a])) {
							AT6090 = "1";
						} else {
							AT6060 = "2";
							AT6070 = "2";
							AT6080 = "2";
							AT6090 = "2";
							AT9003 = "2";
						}

					}
				} else {
					if ("ATPD".equalsIgnoreCase(AT4721)) {
						AT9003 = "1";
					} else if ("FRCC".equalsIgnoreCase(AT4721)) {
						AT6060 = "1";
					} else if ("FRAU".equalsIgnoreCase(AT4721)) {
						AT6060 = "1";
					} else if ("INBL".equalsIgnoreCase(AT4721)) {
						AT6060 = "1";
					} else if ("COCR".equalsIgnoreCase(AT4721)) {
						AT6070 = "1";
					} else if ("DQCO".equalsIgnoreCase(AT4721)) {
						AT6070 = "1";
					} else if ("DQWO".equalsIgnoreCase(AT4721)) {
						// AT6070 = "1";//账户状态是否为坏账
						AT6080 = "1";// 坏账转核销
					} else if ("SBWO".equalsIgnoreCase(AT4721)) {
						// AT6070 = "1";//账户状态是否为坏账
						AT6080 = "1";// 坏账转核销
					} else if ("CAWO".equalsIgnoreCase(AT4721)) {
						AT6080 = "1";
					} else if ("CRAC".equalsIgnoreCase(AT4721)) {
						AT6080 = "1";
					} else if ("PFRA".equalsIgnoreCase(AT4721)) {
						AT6080 = "1";//核销，C1601新增
					} else if ("RFRA".equalsIgnoreCase(AT4721)) {
						AT6080 = "1";//核销，C1601新增
					} else if ("WFRA".equalsIgnoreCase(AT4721)) {
						AT6080 = "1";//核销，C1601新增
					}else if ("DQPD".equalsIgnoreCase(AT4721)) {
						AT6090 = "1";
					} else if ("DQC1".equalsIgnoreCase(AT4721)
							|| "DQC2".equalsIgnoreCase(AT4721)
							|| "DQC3".equalsIgnoreCase(AT4721)
							|| "DQC4".equalsIgnoreCase(AT4721)
							|| "DQC5".equalsIgnoreCase(AT4721)
							|| "DQC6".equalsIgnoreCase(AT4721)
							|| "DQC7".equalsIgnoreCase(AT4721)
							|| "DQC8".equalsIgnoreCase(AT4721)
							|| "DQC9".equalsIgnoreCase(AT4721)) {
						AT6090 = "1";
					}
					// else if(AT4721.contains("DQC")||AT4721.contains("dqc") &&
					// !AT4721.equalsIgnoreCase("DQCO")){
					// AT6090 = "1";
					// }
					else if ("DQ10".equalsIgnoreCase(AT4721)) {
						AT6090 = "1";
					} else if ("DQ11".equalsIgnoreCase(AT4721)) {
						AT6090 = "1";
					} else if ("DQ12".equalsIgnoreCase(AT4721)) {
						AT6090 = "1";
					} else if ("OLC1".equalsIgnoreCase(AT4721)
							|| "OLC2".equalsIgnoreCase(AT4721)
							|| "OLC3".equalsIgnoreCase(AT4721)
							|| "OLC4".equalsIgnoreCase(AT4721)
							|| "OLC5".equalsIgnoreCase(AT4721)
							|| "OLC6".equalsIgnoreCase(AT4721)
							|| "OLC7".equalsIgnoreCase(AT4721)
							|| "OLC8".equalsIgnoreCase(AT4721)
							|| "OLC9".equalsIgnoreCase(AT4721)) {
						AT6090 = "1";
					}
					// else if(AT4721.contains("OLC")||AT4721.contains("olc")){
					// AT6090 = "1";
					// }
					else if ("OC10".equalsIgnoreCase(AT4721)) {
						AT6090 = "1";
					} else if ("OC11".equalsIgnoreCase(AT4721)) {
						AT6090 = "1";
					} else if ("OC12".equalsIgnoreCase(AT4721)) {
						AT6090 = "1";
					} else if ("OLPD".equalsIgnoreCase(AT4721)) {
						AT6090 = "1";
					} else if ("D540".equalsIgnoreCase(AT4721)) {
						AT6090 = "1";
					} else if ("D720".equalsIgnoreCase(AT4721)) {
						AT6090 = "1";
					} else if ("O540".equalsIgnoreCase(AT4721)) {
						AT6090 = "1";
					} else if ("O720".equalsIgnoreCase(AT4721)) {
						AT6090 = "1";
					} else {
						AT6060 = "2";
						AT6070 = "2";
						AT6080 = "2";
						AT6090 = "2";
						AT9003 = "2";
					}
				}
			}
			tempVariable = new VariableBean("AT6060", AT6060, "N", "EX",
					"账户状态是否为欺诈");
			attachVariableA.add(tempVariable);
			tempVariable = new VariableBean("AT6070", AT6070, "N", "EX",
					"账户状态是否为坏账");
			attachVariableA.add(tempVariable);
			tempVariable = new VariableBean("AT6080", AT6080, "N", "EX",
					"账户状态是否为核销");
			attachVariableA.add(tempVariable);
			tempVariable = new VariableBean("AT6090", AT6090, "N", "EX",
					"账户状态是否为逾期");
			attachVariableA.add(tempVariable);
			tempVariable = new VariableBean("AT9003", AT9003, "N", "EX",
					"账户状态是否为未激活");
			attachVariableA.add(tempVariable);
			tempVariable = new VariableBean("AT4721", AT4721, "N", "EX",
					"全部产品层账户状态");
			attachVariableA.add(tempVariable);

			String AT4490 = "";// 全部产品层账户开立日期：
			attrString = variable.getAttribute("AT4490").trim();
			if ("0".equals(attrString) || null == attrString
					|| "-999".equals(attrString) || "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				if ("3".equals(status)) {
					AT4490 = "-999";
				} else {
					AT4490 = "0";
				}
			} else {
				AT4490 = attrString;
				SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
				Date nowDate = new Date();
				String nowD = sf.format(nowDate);
				Long to = null;
				Long from = null;
				Long riqi = null;
				// System.out.println("转换前"+AT4490);
				if (AT4490.contains("，")) {
					AT4490 = AT4490.replace("，", ",");
				}
				// System.out.println("转换后"+AT4490);
				if (AT4490.contains(",")) {
					String A4[] = AT4490.split(",");
					Integer min = 0;
					Integer max = 0;
					for (Integer i = 0; i < A4.length; i++) {
						// A4[i] = sf.format(sf.parse(A4[i]));
						if (A4[i].contains("-")) {
							A4[i] = A4[i].replace("-", "");
						}
						if (A4[i].contains("/")) {
							A4[i] = A4[i].replace("/", "");
						}

						// System.out.println(A4[i]);
					}
					for (Integer i = 0; i < A4.length; i++) {
						if (i + 1 == A4.length) {
							break;
						}
						if (i == 0) {
							min = Integer.parseInt(A4[0].toString());
						}
						max = Integer.parseInt(A4[i + 1].toString());
						if (min >= max) {
							min = max;
						}
					}
					to = sf.parse(nowD).getTime();
					from = sf.parse(min.toString()).getTime();

					riqi = (to - from) / (1000 * 60 * 60 * 24);
					AT4490 = riqi.toString();
				} else {
					to = sf.parse(nowD).getTime();
					from = sf.parse(AT4490).getTime();

					riqi = (to - from) / (1000 * 60 * 60 * 24);
					AT4490 = riqi.toString();
				}

			}
			tempVariable = new VariableBean("AT4490", AT4490, "N", "EX",
					"全部产品层账户开立日期");
			attachVariableA.add(tempVariable);
			String AT4723 = "";// 客户是否要最大额度：
			attrString = variable.getAttribute("AT4723").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT4723 = "-999";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT4723 = "1";
				} else {
					AT4723 = "2";
				}
			}
			tempVariable = new VariableBean("AT4723", AT4723, "N", "EX",
					"客户是否要最大额度");
			attachVariableA.add(tempVariable);
			/**
			 * 需求：网络渠道申请策略 变量
			 * */
			/**
			 * 0 分行录入 1 网申 2 客户经理移动终端 3 柜台老客户 4 手机（微信） 5 网银 6 缤纷生活 7 行内平台（OCRM）
			 * 8 客服 9 自助终端 10 上海分行集中平台录入 F 全流程审批 N 老预制卡 O 老aps P 批量非预制卡 Q 批量预制卡
			 * X 区域中心 Y 华道 Z 综合开户
			 * **/
			String AT20001 = "";// 渠道标识：
			attrString = variable.getAttribute("AT20001").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20001 = "1";
			} else {
				AT20001 = attrString;
				if ("F".equalsIgnoreCase(attrString)) {
					AT20001 = "11";
				} else if ("N".equalsIgnoreCase(attrString)) {
					AT20001 = "12";
				} else if ("O".equalsIgnoreCase(attrString)) {
					AT20001 = "13";
				} else if ("P".equalsIgnoreCase(attrString)) {
					AT20001 = "14";
				} else if ("Q".equalsIgnoreCase(attrString)) {
					AT20001 = "15";
				} else if ("X".equalsIgnoreCase(attrString)) {
					AT20001 = "16";
				} else if ("Y".equalsIgnoreCase(attrString)) {
					AT20001 = "17";
				} else if ("Z".equalsIgnoreCase(attrString)) {
					AT20001 = "18";
				}

			}
			tempVariable = new VariableBean("AT20001", AT20001, "N", "EX",
					"渠道标识");
			attachVariableA.add(tempVariable);

			String AT20002 = "";// 学历：
			attrString = variable.getAttribute("AT20002").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20002 = "0";
			} else {
				AT20002 = "0";
			}
			tempVariable = new VariableBean("AT20002", AT20002, "N", "EX", "学历");
			attachVariableA.add(tempVariable);

			String AT20004 = "";// 户籍与工作地域是否一致：
			attrString = variable.getAttribute("AT20004").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20004 = "2";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT20004 = "1";
				} else {
					AT20004 = "2";
				}
			}
			tempVariable = new VariableBean("AT20004", AT20004, "N", "EX",
					"户籍与工作地域是否一致");
			attachVariableA.add(tempVariable);

			String AT20005 = "";// 是否为信用卡老客户：
			attrString = variable.getAttribute("AT20005").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20005 = "2";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT20005 = "1";
				} else {
					AT20005 = "2";
				}
			}
			tempVariable = new VariableBean("AT20005", AT20005, "N", "EX",
					"是否为信用卡老客户");
			attachVariableA.add(tempVariable);

			String AT20006 = "";// 信用卡老客户额度：
			attrString = variable.getAttribute("AT20006").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20006 = "0";
			} else {
				AT20006 = attrString;
			}
			tempVariable = new VariableBean("AT20006", AT20006, "N", "EX",
					"信用卡老客户额度");
			attachVariableA.add(tempVariable);

			String AT20007 = "";// 是否白名单：
			attrString = variable.getAttribute("AT20007").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20007 = "2";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT20007 = "1";
				} else {
					AT20007 = "2";
				}
			}
			tempVariable = new VariableBean("AT20007", AT20007, "N", "EX",
					"是否白名单");
			attachVariableA.add(tempVariable);

			String AT20008 = "";// 白名单额度：
			attrString = variable.getAttribute("AT20008").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20008 = "0";
			} else {
				AT20008 = attrString;
			}
			tempVariable = new VariableBean("AT20008", AT20008, "N", "EX",
					"白名单额度");
			attachVariableA.add(tempVariable);

			String AT20009 = "";// 反申请欺诈评级：
			attrString = variable.getAttribute("AT20009").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20009 = "1";
			} else {
				AT20009 = attrString;
				if ("A".equalsIgnoreCase(attrString)) {
					AT20009 = "1";
				} else if ("A+".equalsIgnoreCase(attrString)) {
					AT20009 = "2";
				} else if ("D+".equalsIgnoreCase(attrString)) {
					AT20009 = "3";
				} else if ("D".equalsIgnoreCase(attrString)) {
					AT20009 = "4";
				} else if ("D-".equalsIgnoreCase(attrString)) {
					AT20009 = "5";
				} else if ("E".equalsIgnoreCase(attrString)) {
					AT20009 = "6";
				} else if ("F".equalsIgnoreCase(attrString)) {
					AT20009 = "7";
				} else if ("G".equalsIgnoreCase(attrString)) {
					AT20009 = "8";
				} else {
					AT20009 = "1";
				}

			}
			tempVariable = new VariableBean("AT20009", AT20009, "N", "EX",
					"反申请欺诈评级");
			attachVariableA.add(tempVariable);

			String AT20010 = "";// 公安是否返回：
			attrString = variable.getAttribute("AT20010").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20010 = "2";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT20010 = "1";
				} else {
					AT20010 = "2";
				}
			}
			tempVariable = new VariableBean("AT20010", AT20010, "N", "EX",
					"公安是否返回");
			attachVariableA.add(tempVariable);

			String AT20011 = "";// 审批状态：
			attrString = variable.getAttribute("AT20011").trim();
			if (null == attrString || "-999".equals(attrString)
					|| "".equals(attrString)
					|| "null".equalsIgnoreCase(attrString)) {
				AT20011 = "1";
			} else {
				if ("1".equalsIgnoreCase(attrString)
						|| "Y".equalsIgnoreCase(attrString)) {
					AT20011 = "1";
				} else {
					AT20011 = "2";
				}
			}
			tempVariable = new VariableBean("AT20011", AT20011, "N", "EX",
					"审批状态");
			attachVariableA.add(tempVariable);
			// Document tempdocument = this.createDocument(attachVariable);
			// this.setDocument(tempdocument);
			this.setSupplData(supplData);

		} catch (CDSEException cdseex) {
			throw cdseex;

		} catch (Exception ex) {
			ex.printStackTrace();

			throw new CDSEException(errorCode);
		}
	}

	/**
	 * 根据外部变量名称获得外部变量对象。
	 * 
	 * @param variableName
	 *            外部变量名称
	 * @return VariableBean对象
	 * @throws Exception
	 *             如果异常出现
	 */

	public VariableBean getAttributs(String variableName) throws Exception {
		if (document == null) {
			throw new Exception("document 对象为空");

		}
		Element basic = document.getDocumentElement();
		NodeList variables = basic.getChildNodes();
		Element tempVariable = null;
		VariableBean tempVariableBean = null;
		String fldName = "";
		String fldType = "";
		String fldSrc = "";
		String fldDsc = "";
		String value = "";
		for (int i = 0; i < variables.getLength(); i++) {
			tempVariable = (Element) variables.item(i);
			if (variableName
					.equals(tempVariable.getAttribute("FldName").trim())) {
				fldName = tempVariable.getAttribute("FldName").trim();
				;
				value = tempVariable.getAttribute("Value").trim();
				;
				fldType = tempVariable.getAttribute("FldType").trim();
				;
				fldSrc = tempVariable.getAttribute("FldSrc").trim();
				;
				fldDsc = tempVariable.getAttribute("FldDsc").trim();
				;
				tempVariableBean = new VariableBean(fldName, value, fldType,
						fldSrc, fldDsc);
				break;
			}
		}
		return tempVariableBean;
	}

	/**
	 * 将外部变量集转换成Document对象。
	 * 
	 * @param attachVariable
	 *            ArrayList类型的外部变量集
	 * @return Document实例
	 * @throws Exception
	 *             如果异常出现
	 */

	private Document createDocument(ArrayList attachVariable) throws Exception {
		if (attachVariable == null) {
			attachVariable = new ArrayList();
		}
		ArrayList tempAttachVariables = attachVariable;
		String fldName = "";
		String fldType = "";
		String fldSrc = "";
		String fldDsc = "";
		String value = "";
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			// System.err.println(pce); // 出异常时输出异常信息，然后退出，下同
			throw new Exception(pce.getMessage());
		}
		Document tempdocument = db.newDocument();
		// 下面是建立XML文档内容的过程，先建立根元素"basic"
		Element root = tempdocument.createElement("Basic");
		// 根元素添加上文档
		tempdocument.appendChild(root);

		for (java.util.Iterator it = tempAttachVariables.iterator(); it
				.hasNext();) {

			VariableBean variableBean = (VariableBean) it.next();
			// 建立"Variable"元素，添加到根元素
			Element variable = tempdocument.createElement("Variable");
			variable.setAttribute("FldName", variableBean.getFldName());
			variable.setAttribute("Value", variableBean.getValue());
			variable.setAttribute("FldType", variableBean.getFldType());
			variable.setAttribute("FldSrc", variableBean.getFldSrc());
			variable.setAttribute("FldDsc", variableBean.getFldDsc());
			root.appendChild(variable);

		}

		return tempdocument;
	}

	public static void main(String[] args) throws Exception {
		/*
		 * DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		 * DocumentBuilder db = null; try { db = dbf.newDocumentBuilder(); }
		 * catch (ParserConfigurationException pce) { System.err.println(pce);
		 * //出异常时输出异常信息，然后退出，下同 throw new Exception(pce.getMessage()); } File
		 * fileName = new File("c://test//input.xml"); Document doc = null; try
		 * { doc = db.parse(fileName); } catch (org.xml.sax.SAXException ex1) {
		 * System.err.println(ex1.getMessage()); throw new
		 * Exception(ex1.getMessage()); } catch (DOMException dom) {
		 * System.err.println(dom.getMessage()); throw new
		 * Exception(dom.getMessage()); } catch (IOException ioe) {
		 * System.err.println(ioe); throw new Exception(ioe.getMessage()); }
		 * PersonalCreditCardAttributes attributes = null; Log log = new Log();
		 * try { attributes = new PersonalCreditCardAttributes(doc, log); }
		 * catch (Exception ex) { System.err.println("Error :" +
		 * ex.getMessage()); throw new Exception(ex.getMessage()); }
		 * attributes.caculateAllAttributs(doc,log); Document document =
		 * attributes.getDocument(); VariableBean variable =
		 * attributes.getAttributs("AT3075"); System.out.println("变量名称是：" +
		 * variable.getFldName()); System.out.println("变量值是：" +
		 * variable.getValue()); System.out.println("变量类型是：" +
		 * variable.getFldType()); System.out.println("变量来源是：" +
		 * variable.getFldSrc()); System.out.println("变量说明是是：" +
		 * variable.getFldDsc()); System.out.println("卡种类是：" +
		 * attributes.getCardType()); System.out.println("卡等级是：" +
		 * attributes.getCardGrade()); System.out.println("卡档次是：" +
		 * attributes.getCardLevel()); System.out.println("是否DOWNSELL是：" +
		 * attributes.getIsDownSell()); System.out.println("随机数是：" +
		 * attributes.getRandNum()); TransformerFactory tfactory =
		 * TransformerFactory.newInstance(); Transformer transformer =
		 * tfactory.newTransformer(); //
		 * 将DOM对象转化为DOMSource类对象，该对象表现为转化成别的表达形式的信息容器。 DOMSource source = new
		 * DOMSource(document); //
		 * 获得一个StreamResult类对象，该对象是DOM文档转化成的其他形式的文档的容器，可以是XML文件
		 * ，文本文件，HTML文件。这里为一个XML文件。 StreamResult result = new StreamResult(new
		 * File("c:\\test\\text.xml")); // 调用API，将DOM文档转化成XML文件。
		 * transformer.transform(source, result);
		 */
		// System.out.println("日期判断结果是：" +
		// PersonalCreditCardAttributes.isDate("20000431"));
	}

	/**
	 * 获得生成的Document对象。
	 * 
	 * @return Document实例
	 */

	public Document getDocument() {
		return this.document;
	}

	/**
	 * 设置Document对象。
	 * 
	 * @param doc
	 *            Document实例
	 */

	private void setDocument(Document doc) {
		this.document = doc;
	}

	/**
	 * 整型随机数产生器。
	 * 
	 * @param modlo
	 *            int数
	 * @return 整型随机数
	 */

	private int randIntGenerator(int modlo) {
		int mod = modlo;
		Random random = new Random();
		return Math.abs(random.nextInt()) % mod;

	}

	/***
	 * 旧表单----行业性质
	 * 
	 * @param attrString
	 *            XML当前值
	 * @return
	 */
	private String oldForm(String attrString) {
		String AT0620 = "-9";
		String[] bizFieldArray = new String[] { "01", "02", "03", "04", "05",
				"06", "07", "08", "09", "10", "11", "12", "13", "14", "15",
				"16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
				"26", "27", "28", "29", "30", "31", "32", "33", "34", "35",
				"36" };
		if (this.verifyValue(attrString, bizFieldArray)) {
			if ("01".equals(attrString) || "21".equals(attrString)) {
				AT0620 = "01";
			} else if ("02".equals(attrString)) {
				AT0620 = "02";
			} else if ("03".equals(attrString) || "22".equals(attrString)) {
				AT0620 = "03";
			} else if ("04".equals(attrString) || "23".equals(attrString)) {
				AT0620 = "04";
			} else if ("05".equals(attrString) || "24".equals(attrString)) {
				AT0620 = "05";
			} else if ("06".equals(attrString) || "25".equals(attrString)) {
				AT0620 = "06";
			} else if ("07".equals(attrString)) {
				AT0620 = "07";
			} else if ("08".equals(attrString) || "26".equals(attrString)) {
				AT0620 = "08";
			} else if ("09".equals(attrString) || "27".equals(attrString)) {
				AT0620 = "09";
			} else if ("10".equals(attrString) || "28".equals(attrString)) {
				AT0620 = "10";
			} else if ("11".equals(attrString)) {
				AT0620 = "11";
			} else if ("12".equals(attrString) || "29".equals(attrString)) {
				AT0620 = "12";
			} else if ("13".equals(attrString)) {
				AT0620 = "13";
			} else if ("14".equals(attrString) || "30".equals(attrString)) {
				AT0620 = "14";
			} else if ("15".equals(attrString) || "31".equals(attrString)) {
				AT0620 = "15";
			} else if ("16".equals(attrString) || "32".equals(attrString)) {
				AT0620 = "16";
			} else if ("17".equals(attrString) || "33".equals(attrString)) {
				AT0620 = "17";
			} else if ("18".equals(attrString) || "34".equals(attrString)) {
				AT0620 = "18";
			} else if ("19".equals(attrString) || "35".equals(attrString)) {
				AT0620 = "19";
			} else if ("20".equals(attrString) || "36".equals(attrString)) {
				AT0620 = "20";
			}
		} else {
			AT0620 = "-9";
		}
		return AT0620;
	}

	/***
	 * 新表单----行业性质
	 * 
	 * @param attrString
	 *            XML当前值
	 * @return
	 */
	private String newForm(String attrString) {
		String AT0620 = "-9";
		String[] bizFieldArray = new String[] { "01", "02", "03", "04", "05",
				"06", "07", "08", "09", "10", "11", "12", "13", "14", "15",
				"16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
				"26", "27", "28", "29", "30" ,"31"};
		if (this.verifyValue(attrString, bizFieldArray)) {
			if ("11".equals(attrString)) {// 11制造业
				AT0620 = "01";
			} else if ("14".equals(attrString)) {// 14批发与零售业
				AT0620 = "03";
			} else if ("28".equals(attrString)) {// 28进出口贸易业
				AT0620 = "02";
			} else if ("03".equals(attrString)) {// 03－金融/保险
				AT0620 = "04";
			} else if ("10".equals(attrString)) {// 10－电力，燃气及水的生产及供应业
				AT0620 = "05";
			} else if ("06".equals(attrString)) {// 06－信息传输、计算机服务、软件和技术服务业
				AT0620 = "06";
			} else if ("19".equals(attrString)) {// 19－住宿和餐饮业
				AT0620 = "08";
			} else if ("04".equals(attrString)) {// 04－公共管理与社会组织
				AT0620 = "09";
			} else if ("12".equals(attrString)) {// 12－建筑业
				AT0620 = "10";
			} else if ("02".equals(attrString)) {// 02－教育
				AT0620 = "12";
			} else if ("13".equals(attrString)) {// 13－交通运输，仓储和邮政业
				AT0620 = "14";
			} else if ("08".equals(attrString)) {// 08－工商服务业
				AT0620 = "15";// 法律/会计
			} else if ("04".equals(attrString)) {// 04－公共管理与社会组织
				AT0620 = "17";
			} else if ("17".equals(attrString)) {// 17－文化，体育和娱乐业
				AT0620 = "18";
			} else if ("27".equals(attrString)) {// 27－其他
				AT0620 = "20";
			} else if ("31".equals(attrString)) {// 31律所、会计师事务所等咨询
				AT0620 = "15";
			}else {
				AT0620 = "20";
			}
		} else {
			AT0620 = "-9";
		}
		return AT0620;
	}

	// public String getWarningInfo() {
	// return warningInfo;
	// }
	//
	// public void setWarningInfo(String warningInfo) {
	// this.warningInfo = warningInfo;
	// }

	/**
	 * 获得年龄。
	 * 
	 * @param priBirthDate
	 *            8位出生日期（YYYYMMDD）
	 * @return 年龄数
	 * @throws Exception
	 *             如果异常出现
	 */

	private int getAge(String birthDate) throws Exception {
		int age = 0;
		int currentYear = this.getCurrentYear();
		int currentMonth = this.getCurrentMonth();
		int currentDay = this.getCurrentDay();
		int birthYear = 0;
		int birthMonth = 0;
		int birthDay = 0;

		try {
			birthYear = Integer.parseInt(birthDate.substring(0, 4));
			birthMonth = Integer.parseInt(birthDate.substring(4, 6));
			birthDay = Integer.parseInt(birthDate.substring(6, 8));
		} catch (Exception ex) {
			// ex.printStackTrace();
			return 0;
		}
		age = currentYear - birthYear;
		// 2011-11-18 根据张晓晶要求 更改年龄算法 不判断月份与日期
		// if (birthMonth > currentMonth) {
		// age = age - 1;
		// } else if (birthMonth == currentMonth) {
		// if (birthDay > currentDay) {
		// age = age - 1;
		// }
		// }

		return age;
	}

	/**
	 * 获得当前Year。
	 * 
	 * @return 整型四位Year
	 */

	private int getCurrentYear() {
		int currentYear = 0;
		String tempDate = this.cofirmingDate.substring(0, 4);
		currentYear = Integer.parseInt(tempDate);
		return currentYear;
	}

	/**
	 * 获得当前Month。
	 * 
	 * @return 整型Month
	 */

	private int getCurrentMonth() {
		int currentMonth = 0;
		String tempDate = this.cofirmingDate.substring(4, 6);
		currentMonth = Integer.parseInt(tempDate);
		return currentMonth;
	}

	/**
	 * 获得当前Day。
	 * 
	 * @return 整型Day
	 */

	private int getCurrentDay() {
		int currentDay = 0;
		String tempDate = this.cofirmingDate.substring(6, 8);
		currentDay = Integer.parseInt(tempDate);
		return currentDay;
	}

	/**
	 * 获得月差。
	 * 
	 * @param StartDate
	 *            起始日期（YYYYMMDD）
	 * @return 相差月数
	 * @throws Exception
	 *             如果异常出现
	 */

	private int getMonthes(String StartDate) throws Exception {
		int i = 0;
		int beginYear = 0;
		int beginMonth = 0;
		int endYear = this.getCurrentYear();
		int endMonth = this.getCurrentMonth();
		try {
			beginYear = Integer.parseInt(StartDate.substring(0, 4));
			beginMonth = Integer.parseInt(StartDate.substring(4, 6));
		} catch (Exception ex) {
			// ex.printStackTrace();
			// throw new Exception(ex.getMessage());
			return 0;
		}
		i = (endYear - beginYear) * 12 + endMonth - beginMonth + 1;
		return i;
	}

	/**
	 * 获得是否是DOWNSELL。
	 * 
	 * @return Boolean型值
	 */

	public boolean getIsDownSell() {
		return this.isDownSell;
	}

	/**
	 * 获得是否是申请附属卡。
	 * 
	 * @return Boolean型值
	 */

	public boolean isSupplAppl() {
		return this.isSupplAppl;
	}

	/**
	 * 获得是否只申请附属卡。
	 * 
	 * @return Boolean型值
	 */

	public boolean isSupplApplOnly() {
		return this.isSupplApplOnly;
	}

	/**
	 * 获得是否Qcc申请。
	 * 
	 * @return boolean
	 */
	public boolean isQccCardApp() {
		return this.isQccCardApp;
	}

	/**
	 * 获得信用卡类型。
	 * 
	 * @return 整型值
	 */

	public int getCardType() {
		this.cardType = 1;
		return this.cardType;
	}

	/**
	 * 获得信用卡等级。
	 * 
	 * @return 整型值
	 */

	public int getCardGrade() {
		return this.cardGrade;
	}

	/**
	 * 获得信用卡档次。
	 * 
	 * @return 整型值
	 */

	public int getCardLevel() {
		return this.cardLevel;
	}

	/**
	 * 获得产生的随机数。
	 * 
	 * @return 整型值
	 */

	public int getRandNum() {
		return this.randomNum;
	}

	/**
	 * 获得申请单编号。
	 * 
	 * @return 字符串
	 */

	public String getApplNum() {
		return this.ApplicationId;
	}

	/**
	 * 日志所有传入的原始变量。
	 * 
	 * @param appForm
	 *            包含申请原始数据的Document 实例
	 * @param log
	 *            日志实例
	 * @throws CDSEException
	 *             如果异常出现
	 */

	private void logRowVariable(Document appForm, Log log) throws CDSEException {
		try {
			Document doc = appForm;
			Element basic = doc.getDocumentElement();
			Node data = basic.getFirstChild();
			Element variable = (Element) data.getFirstChild();
			NodeList suppls = variable.getChildNodes();
			if (suppls != null) {
				this.supplNum = suppls.getLength();
			} else {
				this.supplNum = 0;
			}

			this.ApplicationId = variable.getAttribute("ApplNum").trim();
			log.appendParameter("AppId", variable.getAttribute("AppId").trim());
			log.appendParameter("AppType", variable.getAttribute("AppType")
					.trim());
			log.appendParameter("SupplApplOnly",
					variable.getAttribute("SupplApplOnly").trim());
			log.appendParameter("SuppNum", variable.getAttribute("SuppNum")
					.trim());
			log.appendParameter("ProductCd", variable.getAttribute("ProductCd")
					.trim());
			log.appendParameter("PriCardCurr",
					variable.getAttribute("PriCardCurr").trim());
			log.appendParameter("CCardTypeDownGrade",
					variable.getAttribute("CCardTypeDownGrade").trim());
			log.appendParameter("PriGender", variable.getAttribute("PriGender")
					.trim());

			log.appendParameter("PriCardBirthday",
					variable.getAttribute("PriCardBirthday").trim());
			log.appendParameter("PriIDType", variable.getAttribute("PriIDType")
					.trim());
			log.appendParameter("PriIDNo", variable.getAttribute("PriIDNo")
					.trim());
			log.appendParameter("PriNationality",
					variable.getAttribute("PriNationality").trim());

			log.appendParameter("PriJCBType",
					variable.getAttribute("PriJCBType").trim());

			log.appendParameter("PriMaritalSta",
					variable.getAttribute("PriMaritalSta").trim());
			log.appendParameter("PriEducationLvl",
					variable.getAttribute("PriEducationLvl").trim());
			log.appendParameter("PriHomePtCd",
					variable.getAttribute("PriHomePtCd").trim());
			log.appendParameter("HomeResidingYear",
					variable.getAttribute("HomeResidingYear").trim());
			/*
			 * log.appendParameter("HomeResidingMonth",
			 * variable.getAttribute("HomeResidingMonth").trim());
			 */
			log.appendParameter("HomeOwshType",
					variable.getAttribute("HomeOwshType").trim());
			log.appendParameter("HouseHireAmt",
					variable.getAttribute("HouseHireAmt").trim());
			log.appendParameter("HousePayAmt",
					variable.getAttribute("HousePayAmt").trim());
			log.appendParameter("HomePhonePro",
					variable.getAttribute("HomePhonePro").trim());
			log.appendParameter("HomePhoneNo",
					variable.getAttribute("HomePhoneNo").trim());
			log.appendParameter("HomePhoneExt",
					variable.getAttribute("HomePhoneExt").trim());
			log.appendParameter("DependentCnt",
					variable.getAttribute("DependentCnt").trim());
			log.appendParameter("ComAddrPtCd",
					variable.getAttribute("ComAddrPtCd").trim());
			log.appendParameter("CompTelPhonePro",
					variable.getAttribute("CompTelPhonePro").trim());
			log.appendParameter("CompTelPhone",
					variable.getAttribute("CompTelPhone").trim());
			log.appendParameter("CompTelPhoneExt",
					variable.getAttribute("CompTelPhoneExt").trim());
			log.appendParameter("CompBizField",
					variable.getAttribute("CompBizField").trim());
			log.appendParameter("CompBizOwShType",
					variable.getAttribute("CompBizOwShType").trim());
			log.appendParameter("PriPos", variable.getAttribute("PriPos")
					.trim());
			log.appendParameter("CompPosSeniority",
					variable.getAttribute("CompPosSeniority").trim());
			log.appendParameter("AmmSalary", variable.getAttribute("AmmSalary")
					.trim());
			/*
			 * log.appendParameter("FieldSeniority",
			 * variable.getAttribute("FieldSeniority").trim());
			 * log.appendParameter("MthlySalary",
			 * variable.getAttribute("MthlySalary").trim());
			 */
			log.appendParameter("OtherBankCardSta",
					variable.getAttribute("OtherBankCardSta").trim());
			log.appendParameter("OthCardCnt",
					variable.getAttribute("OthCardCnt").trim());
			log.appendParameter("OtherLon", variable.getAttribute("OtherLon")
					.trim());
			log.appendParameter("BOCSaveActFlg",
					variable.getAttribute("BOCSaveActFlg").trim());
			log.appendParameter("OthBankSaveActNum",
					variable.getAttribute("OthBankSaveActNum").trim());
			log.appendParameter("CarOwShCondition",
					variable.getAttribute("CarOwShCondition").trim());

			log.appendParameter("ReceiveDate",
					variable.getAttribute("ReceiveDate").trim());
			log.appendParameter("AppliedLtd",
					variable.getAttribute("AppliedLtd").trim());
			/**
			 * log.appendParameter("AssetValue",
			 * variable.getAttribute("AssetValue").trim());
			 * log.appendParameter("SuppBirthday",
			 * variable.getAttribute("SuppBirthday").trim());
			 * log.appendParameter("SuppRelation",
			 * variable.getAttribute("SuppRelation").trim());
			 * log.appendParameter("SupplCardCurr",
			 * variable.getAttribute("SupplCardCurr").trim());
			 * log.appendParameter("SuppLimitFlag",
			 * variable.getAttribute("SuppLimitFlag").trim());
			 * log.appendParameter("SuppLimitPercent",
			 * variable.getAttribute("SuppLimitPercent").trim());
			 * 
			 */
			log.appendParameter("PriStatAddrSel",
					variable.getAttribute("PriStatAddrSel").trim());
			/*
			 * log.appendParameter("SuppStatAddrPtCd",
			 * variable.getAttribute("SuppStatAddrPtCd").trim());
			 */
			log.appendParameter("AutoPayMethod",
					variable.getAttribute("AutoPayMethod").trim());
			log.appendParameter("AutoPayAmtSel",
					variable.getAttribute("AutoPayAmtSel").trim());
			log.appendParameter("AutoPayActSel",
					variable.getAttribute("AutoPayActSel").trim());
			log.appendParameter("AutopayUSDPaytPotn",
					variable.getAttribute("AutopayUSDPaytPotn").trim());
			log.appendParameter("BKCC", variable.getAttribute("BKCC").trim());
			log.appendParameter("BKUA", variable.getAttribute("BKUA").trim());
			log.appendParameter("BKVIP", variable.getAttribute("BKVIP").trim());
			log.appendParameter("BKSTF", variable.getAttribute("BKSTF").trim());
			log.appendParameter("BKFW", variable.getAttribute("BKFW").trim());
			log.appendParameter("BH", variable.getAttribute("BH").trim());
			log.appendParameter("IfBHDA", variable.getAttribute("IfBHDA")
					.trim());
			log.appendParameter("AprSpeGrpProof",
					variable.getAttribute("AprSpeGrpProof").trim());
			log.appendParameter("AprWrkSeniority",
					variable.getAttribute("AprWrkSeniority").trim());
			log.appendParameter("AprPosLevl",
					variable.getAttribute("AprPosLevl").trim());
			log.appendParameter("IncomeProof",
					variable.getAttribute("IncomeProof").trim());
			log.appendParameter("AprAnnIncome",
					variable.getAttribute("AprAnnIncome").trim());
			log.appendParameter("AprAssetType1",
					variable.getAttribute("AprAssetType1").trim());
			log.appendParameter("AprAssetValue1",
					variable.getAttribute("AprAssetValue1").trim());
			log.appendParameter("AprAssetType2",
					variable.getAttribute("AprAssetType2").trim());
			log.appendParameter("AprAssetValue2",
					variable.getAttribute("AprAssetValue2").trim());
			log.appendParameter("AprAssetType3",
					variable.getAttribute("AprAssetType3").trim());
			log.appendParameter("AprAssetValue3",
					variable.getAttribute("AprAssetValue3").trim());
			log.appendParameter("IfHiRiskBiz",
					variable.getAttribute("IfHiRiskBiz").trim());
			log.appendParameter("SavsActProof",
					variable.getAttribute("SavsActProof").trim());
			log.appendParameter("BOCCardHolder",
					variable.getAttribute("BOCCardHolder").trim());
			log.appendParameter("BOCCardLimit",
					variable.getAttribute("BOCCardLimit").trim());
			log.appendParameter("BOCCardAmnt",
					variable.getAttribute("BOCCardAmnt").trim());
			/*
			 * log.appendParameter("BOCCardBeginDate",
			 * variable.getAttribute("BOCCardBeginDate").trim());
			 * log.appendParameter("BOCCardNum",
			 * variable.getAttribute("BOCCardNum").trim());
			 * log.appendParameter("DPD30P24Mon",
			 * variable.getAttribute("DPD30P24Mon").trim());
			 * log.appendParameter("DPD60P24Mon",
			 * variable.getAttribute("DPD60P24Mon").trim());
			 * log.appendParameter("DPD90P24Mon",
			 * variable.getAttribute("DPD90P24Mon").trim());
			 * log.appendParameter("AppRanNum",
			 * variable.getAttribute("AppRanNum").trim());
			 * log.appendParameter("FirstApprvdStrategyID",
			 * variable.getAttribute("FirstApprvdStrategyID"). trim());
			 * log.appendParameter("AppCnt",
			 * variable.getAttribute("AppCnt").trim());
			 */
			log.appendParameter("IfSSInfo", variable.getAttribute("IfSSInfo")
					.trim());
			log.appendParameter("IfPBOCInfo",
					variable.getAttribute("IfPBOCInfo").trim());
			log.appendParameter("IfCBRCInfo",
					variable.getAttribute("IfCBRCInfo").trim());
			log.appendParameter("IfMPSInfo", variable.getAttribute("IfMPSInfo")
					.trim());
			log.appendParameter("CurrSSMonthPay",
					variable.getAttribute("CurrSSMonthPay").trim());
			log.appendParameter("LstSSPayDate",
					variable.getAttribute("LstSSPayDate").trim());
			log.appendParameter("PayMonth", variable.getAttribute("PayMonth")
					.trim());
			log.appendParameter("MPSBirthDate",
					variable.getAttribute("MPSBirthDate").trim());
			log.appendParameter("NumCCardAct",
					variable.getAttribute("NumCCardAct").trim());
			log.appendParameter("CredCcardQcctNum",
					variable.getAttribute("CredCcardQcctNum").trim());
			log.appendParameter("TotlCCardCLmt",
					variable.getAttribute("TotlCCardCLmt").trim());
			log.appendParameter("TotlCCardOLmt",
					variable.getAttribute("TotlCCardOLmt").trim());
			log.appendParameter("CCardFrtOpnDate",
					variable.getAttribute("CCardFrtOpnDate").trim());
			log.appendParameter("CCardLstOpnDate",
					variable.getAttribute("CCardLstOpnDate").trim());
			log.appendParameter("CCardHiLimit",
					variable.getAttribute("CCardHiLimit").trim());
			log.appendParameter("CredCcardL6M3Num",
					variable.getAttribute("CredCcardL6M3Num").trim());
			log.appendParameter("CCardM1In3M",
					variable.getAttribute("CCardM1In3M").trim());
			log.appendParameter("CCardM2In3M",
					variable.getAttribute("CCardM2In3M").trim());
			log.appendParameter("CCardM1In6M",
					variable.getAttribute("CCardM1In6M").trim());
			log.appendParameter("CCardM2In12M",
					variable.getAttribute("CCardM2In12M").trim());
			log.appendParameter("CurrLoanBalance",
					variable.getAttribute("CurrLoanBalance").trim());
			log.appendParameter("CurrLoanMthlyPay",
					variable.getAttribute("CurrLoanMthlyPay").trim());
			log.appendParameter("LoanM1In3M",
					variable.getAttribute("LoanM1In3M").trim());
			log.appendParameter("LoanM2In3M",
					variable.getAttribute("LoanM2In3M").trim());
			log.appendParameter("CredQcardL6M3Num",
					variable.getAttribute("CredQcardL6M3Num").trim());
			log.appendParameter("CredQcardL3M3Num",
					variable.getAttribute("CredQcardL3M3Num").trim());
			log.appendParameter("LoanM1In6M",
					variable.getAttribute("LoanM1In6M").trim());
			log.appendParameter("LoanM2In12M",
					variable.getAttribute("LoanM2In12M").trim());
			log.appendParameter("CLM1In3M", variable.getAttribute("CLM1In3M")
					.trim());
			log.appendParameter("CredCcardL6M2Num",
					variable.getAttribute("CredCcardL6M2Num").trim());
			log.appendParameter("CCardM3In24M",
					variable.getAttribute("CCardM3In24M").trim());
			log.appendParameter("TotlCCardOTme",
					variable.getAttribute("TotlCCardOTme").trim());
			log.appendParameter("CurrCCardOLmt",
					variable.getAttribute("CurrCCardOLmt").trim());
			log.appendParameter("NumNonnormCCardCloAct",
					variable.getAttribute("NumNonnormCCardCloAct").trim());
			log.appendParameter("CCardActiActIn6M",
					variable.getAttribute("CCardActiActIn6M").trim());
			log.appendParameter("CCardActiActIn12M",
					variable.getAttribute("CCardActiActIn12M").trim());
			log.appendParameter("NumLoan", variable.getAttribute("NumLoan")
					.trim());
			log.appendParameter("CredLoanL6M2Num",
					variable.getAttribute("CredLoanL6M2Num").trim());
			log.appendParameter("CurrLoanOLmt",
					variable.getAttribute("CurrLoanOLmt").trim());
			log.appendParameter("TotlLoanOTme",
					variable.getAttribute("TotlLoanOTme").trim());
			log.appendParameter("MaxOMBetCCardNLoan",
					variable.getAttribute("MaxOMBetCCardNLoan").trim());
			log.appendParameter("CredYearIncome",
					variable.getAttribute("CredYearIncome").trim());
			log.appendParameter("ProvFundFirstLimit",
					variable.getAttribute("ProvFundFirstLimit").trim());
			log.appendParameter("CredLoanContractOlmt",
					variable.getAttribute("CredLoanContractOlmt").trim());
			log.appendParameter("NumNonnormLoanCloAct",
					variable.getAttribute("NumNonnormLoanCloAct").trim());
			log.appendParameter("NumReqCheTimIn3M",
					variable.getAttribute("NumReqCheTimIn3M").trim());
			log.appendParameter("ReservedField2",
					variable.getAttribute("ReservedField2").trim());
			log.appendParameter("ReservedField3",
					variable.getAttribute("ReservedField3").trim());
			log.appendParameter("ReservedField4",
					variable.getAttribute("ReservedField4").trim());
			log.appendParameter("ReservedField5",
					variable.getAttribute("ReservedField5").trim());
			log.appendParameter("ReservedField6",
					variable.getAttribute("ReservedField6").trim());
			log.appendParameter("ReservedField7",
					variable.getAttribute("ReservedField7").trim());
			log.appendParameter("ReservedField8",
					variable.getAttribute("ReservedField8").trim());
			log.appendParameter("ReservedField9",
					variable.getAttribute("ReservedField9").trim());
			log.appendParameter("ReservedField10",
					variable.getAttribute("ReservedField10").trim());
			log.appendParameter("ReservedField11",
					variable.getAttribute("ReservedField11").trim());
			log.appendParameter("ReservedField12",
					variable.getAttribute("ReservedField12").trim());
			log.appendParameter("ReservedField13",
					variable.getAttribute("ReservedField13").trim());
			log.appendParameter("ReservedField14",
					variable.getAttribute("ReservedField14").trim());
			log.appendParameter("ReservedField15",
					variable.getAttribute("ReservedField15").trim());
			log.appendParameter("ReservedField16",
					variable.getAttribute("ReservedField16").trim());
			log.appendParameter("ReservedField17",
					variable.getAttribute("ReservedField17").trim());
			log.appendParameter("ReservedField18",
					variable.getAttribute("ReservedField18").trim());
			log.appendParameter("ReservedField19",
					variable.getAttribute("ReservedField19").trim());
			log.appendParameter("ReservedField20",
					variable.getAttribute("ReservedField20").trim());
			// 2006-10-09根据中行接口更改而修改
			log.appendParameter("BlkChkResult",
					variable.getAttribute("BlkChkResult").trim());
			log.appendParameter("EomChkResult",
					variable.getAttribute("EomChkResult").trim());
			log.appendParameter("DupChkResult",
					variable.getAttribute("DupChkResult").trim());

			log.appendParameter("appiMcHouseProperty",
					variable.getAttribute("appiMcHouseProperty").trim());
			log.appendParameter("appiMcCarInd",
					variable.getAttribute("appiMcCarInd").trim());

			log.appendParameter("CgryChkResult",
					variable.getAttribute("CgryChkResult").trim());
			log.appendParameter("PgryChkResult",
					variable.getAttribute("PgryChkResult").trim());
			log.appendParameter("A2rChkResult",
					variable.getAttribute("A2rChkResult").trim());
			// 2008-10-27 根据中行需求添加字段
			log.appendParameter("CredIncome1",
					variable.getAttribute("CredIncome1").trim());
			log.appendParameter("CredIncome1Value",
					variable.getAttribute("CredIncome1Value").trim());
			log.appendParameter("CredIncome2",
					variable.getAttribute("CredIncome2").trim());
			log.appendParameter("CredIncome2Value",
					variable.getAttribute("CredIncome2Value").trim());
			log.appendParameter("CredAssetType1",
					variable.getAttribute("CredAssetType1").trim());
			log.appendParameter("CredAssetValue1",
					variable.getAttribute("CredAssetValue1").trim());
			log.appendParameter("CredAssetType2",
					variable.getAttribute("CredAssetType2").trim());
			log.appendParameter("CredAssetValue2",
					variable.getAttribute("CredAssetValue2").trim());
			log.appendParameter("CredAssetType3",
					variable.getAttribute("CredAssetType3").trim());
			log.appendParameter("CredAssetValue3",
					variable.getAttribute("CredAssetValue3").trim());
			log.appendParameter("CredAssetType4",
					variable.getAttribute("CredAssetType4").trim());
			log.appendParameter("CredAssetValue4",
					variable.getAttribute("CredAssetValue4").trim());
			log.appendParameter("CredAssetType5",
					variable.getAttribute("CredAssetType5").trim());
			log.appendParameter("CredAssetValue5",
					variable.getAttribute("CredAssetValue5").trim());
			log.appendParameter("CredAssetType6",
					variable.getAttribute("CredAssetType6").trim());
			log.appendParameter("CredAssetValue6",
					variable.getAttribute("CredAssetValue6").trim());
			log.appendParameter("AppiMcContactRelship",
					variable.getAttribute("AppiMcContactRelship").trim());

			// 附属卡信息
			Element item = null;
			for (int i = 0; i < this.supplNum; ++i) {
				item = (Element) suppls.item(i);
				log.appendParameter("SupplID" + (i + 1),
						item.getAttribute("SupplID").trim());
				log.appendParameter("PrimaryCardNo" + (i + 1), item
						.getAttribute("PrimaryCardNo").trim());
				log.appendParameter("SuppTitle" + (i + 1),
						item.getAttribute("SuppTitle").trim());
				log.appendParameter("SuppBirthday" + (i + 1), item
						.getAttribute("SuppBirthday").trim());
				log.appendParameter("SuppIDType" + (i + 1),
						item.getAttribute("SuppIDType").trim());
				log.appendParameter("SuppIDNo" + (i + 1),
						item.getAttribute("SuppIDNo").trim());
				log.appendParameter("SuppRelation" + (i + 1), item
						.getAttribute("SuppRelation").trim());
				log.appendParameter("SuppJCBType" + (i + 1),
						item.getAttribute("SuppJCBType").trim());
				log.appendParameter("SuppLimitFlag" + (i + 1), item
						.getAttribute("SuppLimitFlag").trim());
				log.appendParameter("SuppLimitPercent" + (i + 1), item
						.getAttribute("SuppLimitPercent").trim());
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
			// errorCode = "9006";
			throw new CDSEException(errorCode);
		}

	}

	public double getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public double getSupplLtdRatio() {
		return supplLtdRatio;
	}

	public void setSupplLtdRatio(double supplLtdRatio) {
		this.supplLtdRatio = supplLtdRatio;
	}

	// change the format yyyy-mm-dd to YYYYMMDD
	private String changeDataFormat(String day) {
		String anotherFormatDate = new String();
		if (day != null && day.length() >= 10) {
			anotherFormatDate = day.substring(0, 4) + day.substring(5, 7)
					+ day.substring(8, 10);
		} else {
			anotherFormatDate = "";
		}
		return anotherFormatDate;
	}

	public void setCardGrade(int cardGrade) {
		this.cardGrade = cardGrade;
	}

	public void setCardLevel(int cardLevel) {
		this.cardLevel = cardLevel;
	}

	public void setCardType(int cardType) {
		this.cardType = cardType;
	}

	public void setIsDownSell(boolean isDownSell) {
		this.isDownSell = isDownSell;
	}

	public void setRandomNum(int randomNum) {
		this.randomNum = randomNum;
	}

	public void setSupplAppl(boolean isSupplAppl) {
		this.isSupplAppl = isSupplAppl;
	}

	public void setSupplApplOnly(boolean isSupplApplOnly) {
		this.isSupplApplOnly = isSupplApplOnly;
	}

	public int getRandomNum() {
		return randomNum;
	}

	public boolean isIsSupplApplOnly() {
		return isSupplApplOnly;
	}

	public boolean isIsSupplAppl() {
		return isSupplAppl;
	}

	public void setIsSupplAppl(boolean isSupplAppl) {
		this.isSupplAppl = isSupplAppl;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public void setCofirmingDate(String cofirmingDate) {
		this.cofirmingDate = cofirmingDate;
	}

	public String getCofirmingDate() {
		return cofirmingDate;
	}

	// 2004-3-21 根据金卡降级处理额度决策时AT0040 为2的问题修改 Huang Shaicheng
	public void setValue(String valName, String valValue) {
		Element inVariables = null;
		Node tmpNode = null;
		Element basic = document.getDocumentElement();
		basic.getChildNodes();
		tmpNode = basic.getFirstChild();
		if (tmpNode == null) {
			return;
		}
		while (!(tmpNode == null)) {
			if (((Element) tmpNode).getAttribute("FldName").equalsIgnoreCase(
					valName)) {
				((Element) tmpNode).setAttribute("Value", valValue);
				break;
			}
			tmpNode = tmpNode.getNextSibling();
		}
	}

	private boolean verifyValue(String attrString, String[] valueArray) {
		boolean result = false;
		if (("".equals(attrString)) || (valueArray == null)) {
			result = false;
			return result;
		}
		for (int i = 0; i < valueArray.length; i++) {
			if (attrString.equalsIgnoreCase(valueArray[i])) {
				result = true;
				break;
			}
		}
		return result;
	}

	private boolean isNumeric(String numStr) {
		if ("".equals(numStr)) {
			return false;
		}
		try {
			Double.parseDouble(numStr);
		} catch (Exception e) {
			// e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean isInt(String numStr) {

		if ("".equals(numStr)) {
			return false;
		}
		try {
			Integer.parseInt(numStr);
		} catch (Exception ex) {
			// ex.printStackTrace();
			return false;
		}
		return true;
	}

	/*
	 * private boolean isDate(String dateStr) { int birthYear = 0; int
	 * birthMonth = 0; int birthDay = 0; if (dateStr == null ||
	 * "".equalsIgnoreCase(dateStr)) { return false; } if (! (dateStr.length()
	 * == 8 || dateStr.length() >= 10)) { return false; } try { SimpleDateFormat
	 * bartDateFormat = new SimpleDateFormat("yyyy-MM-dd"); String
	 * dateStringToParse = ""; if (dateStr.length() == 8) { dateStringToParse =
	 * dateStr.substring(0, 4) + "-" + dateStr.substring(4, 6) + "-" +
	 * dateStr.substring(6, 8); } else if (dateStr.length() >= 10) {
	 * dateStringToParse = dateStr.substring(0, 4) + "-" + dateStr.substring(5,
	 * 7) + "-" + dateStr.substring(8, 10); } Date date =
	 * bartDateFormat.parse(dateStringToParse); } catch (Exception ex) {
	 * ex.printStackTrace(); return false; } return true; }
	 */
	public boolean isLocal(String personId, String postalCode) {
		boolean result = false;
		String str1 = null;
		String str2 = null;
		if (!personId.equals("") && !postalCode.equals("")) {
			str1 = personId.substring(0, 2);
			str2 = postalCode.substring(0, 2);
		} else {
			return result;
		}
		System.out.println("str1:" + str1 + "; str2:" + str2);
		if (("11".equals(str1) && "10".equals(str2))
				|| ("12".equals(str1) && "30".equals(str2))
				|| ("13".equals(str1) && "05".equals(str2))
				|| ("14".equals(str1) && "03".equals(str2))
				|| ("15".equals(str1) && "16".equals(str2))
				|| ("15".equals(str1) && "02".equals(str2))
				|| ("15".equals(str1) && "01".equals(str2))
				|| ("21".equals(str1) && "11".equals(str2))
				|| ("22".equals(str1) && "13".equals(str2))
				|| ("23".equals(str1) && "15".equals(str2))
				|| ("23".equals(str1) && "16".equals(str2))
				|| ("31".equals(str1) && "20".equals(str2))
				|| ("32".equals(str1) && "21".equals(str2))
				|| ("32".equals(str1) && "22".equals(str2))
				|| ("33".equals(str1) && "31".equals(str2))
				|| ("33".equals(str1) && "32".equals(str2))
				|| ("34".equals(str1) && "23".equals(str2))
				|| ("34".equals(str1) && "24".equals(str2))
				|| ("35".equals(str1) && "35".equals(str2))
				|| ("35".equals(str1) && "36".equals(str2))
				|| ("36".equals(str1) && "33".equals(str2))
				|| ("37".equals(str1) && "24".equals(str2))
				|| ("37".equals(str1) && "25".equals(str2))
				|| ("37".equals(str1) && "26".equals(str2))
				|| ("37".equals(str1) && "27".equals(str2))
				|| ("41".equals(str1) && "45".equals(str2))
				|| ("41".equals(str1) && "47".equals(str2))
				|| ("42".equals(str1) && "43".equals(str2))
				|| ("42".equals(str1) && "44".equals(str2))
				|| ("43".equals(str1) && "41".equals(str2))
				|| ("44".equals(str1) && "51".equals(str2))
				|| ("44".equals(str1) && "52".equals(str2))
				|| ("45".equals(str1) && "53".equals(str2))
				|| ("45".equals(str1) && "54".equals(str2))
				|| ("46".equals(str1) && "57".equals(str2))
				|| ("50".equals(str1) && "40".equals(str2))
				|| ("51".equals(str1) && "60".equals(str2))
				|| ("51".equals(str1) && "61".equals(str2))
				|| ("52".equals(str1) && "55".equals(str2))
				|| ("52".equals(str1) && "56".equals(str2))
				|| ("53".equals(str1) && "65".equals(str2))
				|| ("53".equals(str1) && "66".equals(str2))
				|| ("54".equals(str1) && "85".equals(str2))
				|| ("61".equals(str1) && "71".equals(str2))
				|| ("62".equals(str1) && "73".equals(str2))
				|| ("62".equals(str1) && "74".equals(str2))
				|| ("63".equals(str1) && "81".equals(str2))
				|| ("64".equals(str1) && "75".equals(str2))
				|| ("65".equals(str1) && "83".equals(str2))
				|| ("65".equals(str1) && "84".equals(str2))) {
			result = true;
		}

		return result;
	}

	public boolean isDate(String dateStr) {
		if (dateStr == null || "".equalsIgnoreCase(dateStr)) {
			return false;
		}
		if (!(dateStr.length() == 8 || dateStr.length() >= 10)) {
			return false;
		}
		try {

			String dateStringToParse = "";
			if (dateStr.length() == 8) {
				dateStringToParse = dateStr.substring(0, 4)
						+ dateStr.substring(4, 6) + dateStr.substring(6);
			} else if (dateStr.length() >= 10) {
				dateStringToParse = dateStr.substring(0, 4)
						+ dateStr.substring(5, 7) + dateStr.substring(8, 10);
			}
			int dateInt = Integer.parseInt(dateStringToParse);

			/** 以下检查非法年份 */
			if (dateInt > maxDate) {
				return false;
			}
			if (dateInt < minDate) {
				return false;
			}

			/** 以下检查非法月份 */
			int year = dateInt / 10000;
			int month = (dateInt - year * 10000) / 100;
			if (month > monthDecember) {
				return false;
			}

			/** 以下检查日 */
			int day = dateInt - year * 10000 - month * 100;
			int nEndDay;
			switch (month) {
			case monthJanuary: // 月份-1月份
			case monthMarch: // 月份-3月份
			case monthMay: // 月份-5月份
			case monthJuly: // 月份-7月份
			case monthAugust: // 月份-8月份
			case monthOctober: // 月份-10月份
			case monthDecember: // 月份-12月份
				nEndDay = 31;
				break;
			case monthFebruary: // 月份-2月份
				if (PersonalCreditCardAttributes.isLeapYear(year)) {
					nEndDay = 29; // 闰年29天
				} else {
					nEndDay = 28; // 平年28天
				}
				break;
			case monthApril: // 月份-4月份
			case monthJune: // 月份-6月份
			case monthSeptember: // 月份-9月份
			case monthNovember: // 月份-11月份
			default:
				nEndDay = 30;
				break;
			}
			if (day > nEndDay) {
				return false;
			}

		} catch (Exception ex) {
			// ex.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 检查所给的年份是否是闰年
	 * 
	 * @param year
	 *            年
	 * @return 检查结果: true - 是闰年; false - 是平年
	 */
	public static boolean isLeapYear(int year) {
		if (year / 4 * 4 != year) {
			return false; // 不能被4整除
		} else if (year / 100 * 100 != year) {
			return true; // 能被4整除，不能被100整除
		} else if (year / 400 * 400 != year) {
			return false; // 能被100整除，不能被400整除
		} else {
			return true; // 能被400整除
		}
	}

	private int toInteger(String str) {
		int result = 0;
		try {
			result = Integer.parseInt(str);
		} catch (Exception ex) {
			// ex.printStackTrace();
		}
		return result;

	}

	private double toDouble(String str) {
		double result = 0;
		try {
			result = Double.parseDouble(str);
		} catch (Exception ex) {
			// ex.printStackTrace();
		}
		return result;

	}

	/**
	 * 日期格式加工
	 * 
	 * @param attrString
	 *            String
	 * @return
	 */
	private String[] Month_Number(String attrString) {
		String first_time = "";
		String Year = "";
		String Month = "";
		String Day = "";
		String number[] = { "", "", "" };
		if (attrString.length() == 10) {
			first_time = attrString;
			if (first_time.indexOf("-") != -1) {
				first_time = first_time.replace("-", "");
			} else if (first_time.indexOf(".") != -1) {
				first_time = first_time.replace(".", "");
			} else if (first_time.indexOf("/") != -1) {
				first_time = first_time.replace("/", "");
			}
			try {
				Double.parseDouble(first_time);
				Year = first_time.substring(0, 4);
				Month = first_time.substring(4, 6);
				Day = first_time.substring(6, 8);
			} catch (Exception e) {
				Year = "";
				Month = "";
				Day = "";
			}
		} else if (attrString.length() == 8) {
			try {
				Double.parseDouble(attrString);
				Year = attrString.substring(0, 4);
				Month = attrString.substring(4, 6);
				Day = attrString.substring(6, 8);
			} catch (Exception e) {
				Year = "";
				Month = "";
				Day = "";
			}
		} else if (attrString.length() == 7) {
			first_time = attrString;
			if (first_time.indexOf("-") != -1) {
				first_time = first_time.replace("-", "");
			} else if (first_time.indexOf(".") != -1) {
				first_time = first_time.replace(".", "");
			} else if (first_time.indexOf("/") != -1) {
				first_time = first_time.replace("/", "");
			}
			try {
				Double.parseDouble(first_time);
				Year = first_time.substring(0, 4);
				Month = first_time.substring(4, 6);
			} catch (Exception e) {
				Year = "";
				Month = "";
				Day = "";
			}
		} else if (attrString.length() == 6) {
			try {
				Double.parseDouble(attrString);
				Year = attrString.substring(0, 4);
				Month = attrString.substring(4, 6);
			} catch (Exception e) {
				Year = "";
				Month = "";
				Day = "";
			}
		} else if (attrString.length() == 4) {
			try {
				Double.parseDouble(attrString);
				Year = attrString.substring(0, 4);
			} catch (Exception e) {
				Year = "";
				Month = "";
				Day = "";
			}
		} else if (attrString.length() == 2) {
			try {
				Double.parseDouble(attrString);
				Month = attrString;
			} catch (Exception e) {
				Year = "";
				Month = "";
				Day = "";
			}
		} else {
			Year = "";
			Month = "";
			Day = "";
		}
		number[0] = Year;
		number[1] = Month;
		number[2] = Day;
		return number;
	}

	/**
	 * 判断身份证与出生日期性别是否一致
	 * 
	 * @param idNo
	 *            String
	 * @param priBirthday
	 *            String
	 * @return String
	 */
	private String getCheckPriIdNo(String idNo, String priBirthday,
			String priGender) {
		String result = "0";
		int idNoLen = idNo.length();
		try {
			switch (idNoLen) {
			case 15:
				String cardbirth15 = idNo.substring(6, 8)
						+ idNo.substring(8, 10) + idNo.substring(10, 12);
				priBirthday = priBirthday.substring(2, 8);

				if (!cardbirth15.equals(priBirthday)) {
					result = "1";
				}
				if ("1".equals(priGender)) { // 男
					if ((Integer.parseInt(idNo.substring(idNo.length() - 1)) % 2) == 0) {
						result = "1";
					}
				} else if ("0".equals(priGender)) { // 女
					if ((Integer.parseInt(idNo.substring(idNo.length() - 1)) % 2) != 0) {
						result = "1";
					}
				} else {
					result = "1";
				}

				break;
			case 18:
				String cardbirth18 = idNo.substring(6, 10)
						+ idNo.substring(10, 12) + idNo.substring(12, 14);
				if (!cardbirth18.equals(priBirthday)) {
					result = "1";
				}
				if ("1".equals(priGender)) { // 男
					if ((Integer
							.parseInt(idNo.substring(16, idNo.length() - 1)) % 2) == 0) {
						result = "1";
					}
				} else if ("0".equals(priGender)) { // 女
					if ((Integer
							.parseInt(idNo.substring(16, idNo.length() - 1)) % 2) != 0) {
						result = "1";
					}
				} else {
					result = "1";
				}

				break;
			default:
				result = "1";
				break;
			}
		} catch (Exception e) {
			result = "1";
		}

		return result;
	}

	public String getApplProductCd() {
		return applProductCd;
	}

	public void setApplProductCd(String applProductCd) {
		this.applProductCd = applProductCd;
	}

	public int getSupplNum() {
		return supplNum;
	}

	public ArrayList getSupplData() {
		return supplData;
	}

	public ArrayList getAttachVariableA() {
		return attachVariableA;
	}

	public boolean isIsDutyCardApp() {
		return isDutyCardApp;
	}

	public boolean isIsPlatinaCardApp() {
		return isPlatinaCardApp;
	}

	public void setSupplNum(int supplNum) {
		this.supplNum = supplNum;
	}

	public void setSupplData(ArrayList supplData) {
		this.supplData = supplData;
	}

	public void setAttachVariableA(ArrayList attachVariableA) {
		this.attachVariableA = attachVariableA;
	}

	public void setIsQccCardApp(boolean isQccCardApp) {
		this.isQccCardApp = isQccCardApp;
	}

	public void setIsDutyCardApp(boolean isDutyCardApp) {
		this.isDutyCardApp = isDutyCardApp;
	}

	public void setIsPlatinaCardApp(boolean isPlatinaCardApp) {
		this.isPlatinaCardApp = isPlatinaCardApp;
	}

	private void jbInit() throws Exception {
	}
}

/*AT20001 = attrString;
if ("0600".equalsIgnoreCase(attrString)) {
	AT20001 = "1";//0600网站
} else if ("0100".equalsIgnoreCase(attrString)) {
	AT20001 = "2";//0100总行NUW
} else if ("0101".equalsIgnoreCase(attrString)) {
	AT20001 = "3";//0101总行NUW（分行录入）
} else if ("0102".equalsIgnoreCase(attrString)) {
	AT20001 = "4";//0102总行NUW（商务公司录入）
} else if ("0103".equalsIgnoreCase(attrString)) {
	AT20001 = "5";//0103总行NUW（区域中心录入）
} else if ("0101".equalsIgnoreCase(attrString)) {
	AT20001 = "6";//0101总行NUW-老客户简化申请（分行录入）
} else if ("0102".equalsIgnoreCase(attrString)) {
	AT20001 = "7";//0102总行NUW-老客户简化申请（商务公司录入）
} else if ("0103".equalsIgnoreCase(attrString)) {
	AT20001 = "8";//0103总行NUW-老客户简化申请（区域中心录入）
}else if ("0104".equalsIgnoreCase(attrString)) {
	AT20001 = "9";//0104批量预制卡（旧）
}else if ("0105".equalsIgnoreCase(attrString)) {
	AT20001 = "10";//0105批量预制卡（新）
}else if ("0106".equalsIgnoreCase(attrString)) {
	AT20001 = "11";//0106批量非预制卡
}else if ("0107".equalsIgnoreCase(attrString)) {
	AT20001 = "12";//0107BANCS(综合开户)
}else if ("0108".equalsIgnoreCase(attrString)) {
	AT20001 = "13";//0108旧审批系统（APS）
}else if ("0200".equalsIgnoreCase(attrString)) {
	AT20001 = "14";//0200总行CSR，原：客服
}else if ("0300".equalsIgnoreCase(attrString)) {
	AT20001 = "15";//0300总行CRM
}else if ("0400".equalsIgnoreCase(attrString)) {
	AT20001 = "16";//0400分行落地终端
}else if ("0500".equalsIgnoreCase(attrString)) {
	AT20001 = "17";//0500网银
}else if ("0700".equalsIgnoreCase(attrString)) {
	AT20001 = "18";//0700财富OCRM，原：行内平台（OCRM）
}else if ("0700".equalsIgnoreCase(attrString)) {
	AT20001 = "19";//0800微信，原：手机
}else if ("0700".equalsIgnoreCase(attrString)) {
	AT20001 = "20";//0900移动终端
}else if ("0700".equalsIgnoreCase(attrString)) {
	AT20001 = "21";//1000缤纷生活
}else{
	AT20001 = "-999";
}*/
